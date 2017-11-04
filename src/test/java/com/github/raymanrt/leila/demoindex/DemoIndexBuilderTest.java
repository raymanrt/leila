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

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class DemoIndexBuilderTest {

    private static final int MAX_DOCS = 100;

    private static final String MVN_TARGET = "target";
    private static final String DEMO_INDEX = "demo-index";

    private static final double BASE_DOUBLE = 4.0;

    private static final float BASE_FLOAT = 0.0f;

    @Before
    public void cleanUp() throws IOException {

        Path path = Paths.get(MVN_TARGET)
                .resolve(DEMO_INDEX);

        if(!path.toFile().exists()) {
            return;
        }

        Files.walk(path)
                .map(Path::toFile)
                .forEach(File::delete)
        ;
    }

    @Test
    public void buildDemoIndex() throws IOException {
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

    private Document mockDocument(int id) {
        Document doc = new Document();

        doc.add(new IntField("id", id, Field.Store.YES));

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

    private long toLong(final int id) {
        return 100L + id;
    }

    private double toDouble(final int id) {
        return BASE_DOUBLE + ((double) id / 100);
    }

    private float toFloat(final int id) {
        return BASE_FLOAT + ((float) id / 100);
    }

}