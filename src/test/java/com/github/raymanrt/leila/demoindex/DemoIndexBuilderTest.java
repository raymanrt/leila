/*
 * Copyright 2017 Riccardo Tasso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.LuceneDocIterator;
import com.github.raymanrt.leila.Util;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

public class DemoIndexBuilderTest {

    private static final int MAX_DOCS = 100;

    private static final String MVN_TARGET = "target";
    private static final String DEMO_INDEX = "demo-index";

    private static final double BASE_DOUBLE = 4.0;

    private static final float BASE_FLOAT = 0.0f;

    @BeforeClass
    public static void cleanUp() throws IOException {

        Path path = Paths.get(MVN_TARGET)
                .resolve(DEMO_INDEX);

        if(!path.toFile().exists()) {
            return;
        }

        Files.walk(path)
                .map(Path::toFile)
                .forEach(File::delete)
        ;

        buildDemoIndex();
    }

    @Test
    public void getSearcherTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            Assert.assertEquals(MAX_DOCS, searcher.getIndexReader().maxDoc());
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void alldocumentsTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertEquals(MAX_DOCS, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void oneDocument() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "id_str:10",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertEquals(1, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void zeroDocument() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "id:10",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertEquals(0, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void intDocument() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "id:10",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.singletonMap("id", "int")
            );

            Assert.assertEquals(1, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void longDocument() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "long:[110 TO 111]",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.singletonMap("long", "long")
            );

            Assert.assertEquals(2, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void doubleDocument() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "double:[* TO 4.0900]",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.singletonMap("double", "double")
            );

            Assert.assertEquals(10, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void floatDocument() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "float:000.1",
                    Sort.INDEXORDER,
                    200,
                    new String[]{},
                    new String[]{},
                    Collections.singletonMap("float", "float")
            );

            Assert.assertEquals(1, iteratorCount(it));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    private int iteratorCount(Iterator<Object> it) {
        int i = 0;
        while(it.hasNext()) {
            it.next();
            i ++;
        }
        return i;
    }

    private static void buildDemoIndex() throws IOException {
        Directory dir = FSDirectory.open(new File(MVN_TARGET, DEMO_INDEX));
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, new WhitespaceAnalyzer());

        try(IndexWriter iw = new IndexWriter(dir, conf)) {
            for(int i = 0; i < MAX_DOCS; i++) {
                iw.addDocument(mockDocument(i));
            }
            iw.commit();
            Assert.assertEquals(MAX_DOCS, iw.numDocs());
        }


    }

    private static Document mockDocument(int id) {
        Document doc = new Document();

        doc.add(new IntField("id", id, Field.Store.YES));
        doc.add(new StringField("id_str", Integer.toString(id), Field.Store.YES));

        String contentValue = id % 2 == 0 ?
                "random " + UUID.randomUUID().toString() :
                UUID.randomUUID().toString();
        doc.add(new TextField("content", contentValue, Field.Store.YES));

        doc.add(new NumericDocValuesField("longid", id * 10));

        doc.add(new DoubleField("double", toDouble(id), Field.Store.YES));

        doc.add(new FloatField("float", toFloat(id), Field.Store.YES));

        doc.add(new LongField("long", toLong(id), Field.Store.YES));

        return doc;
    }

    private static long toLong(final int id) {
        return 100L + id;
    }

    private static double toDouble(final int id) {
        return BASE_DOUBLE + ((double) id / 100);
    }

    private static float toFloat(final int id) {
        return BASE_FLOAT + ((float) id / 100);
    }

}