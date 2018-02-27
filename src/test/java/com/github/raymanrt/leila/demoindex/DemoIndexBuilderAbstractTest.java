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
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

public abstract class DemoIndexBuilderAbstractTest {

    public static final int MAX_DOCS = 100;

    public static final String MVN_TARGET = "target";
    public static final String DEMO_INDEX = "demo-index";

    private static final double BASE_DOUBLE = 4.0;

    private static final float BASE_FLOAT = 0.0f;

    private static final String ROOT = "root";
    private static final String ODD = "odd";
    private static final String EVEN = "even";

    @BeforeClass
    public static void cleanUp() throws IOException {

        Path path = Paths.get(MVN_TARGET)
                .resolve(DEMO_INDEX);

        if(path.toFile().exists()) {
            Files.walk(path)
                    .map(Path::toFile)
                    .forEach(File::delete)
            ;
        }

        buildDemoIndex();
    }

    public int iteratorCount(Iterator<Object> it) {
        int i = 0;
        while(it.hasNext()) {
            it.next();
            i ++;
        }
        return i;
    }

    private static void buildDemoIndex() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(MVN_TARGET, DEMO_INDEX));
        IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());

        try(IndexWriter iw = new IndexWriter(dir, conf)) {
            for(int i = 0; i < MAX_DOCS; i++) {
                iw.addDocument(mockDocument(i));
            }
            iw.commit();
            Assert.assertEquals(MAX_DOCS, iw.numDocs());
        }


    }

//    private static Document mockDocument(int id) {
//        Document doc = new Document();
//
//        doc.add(new IntField("id", id, Field.Store.YES));
//        doc.add(new StringField("id_str", Integer.toString(id), Field.Store.YES));
//
//        String contentValue = id % 2 == 0 ?
//                "random " + UUID.randomUUID().toString() :
//                UUID.randomUUID().toString();
//        doc.add(new TextField("content", contentValue, Field.Store.YES));
//
//        doc.add(new NumericDocValuesField("longid", id * 10));
//
//        doc.add(new DoubleField("double", toDouble(id), Field.Store.YES));
//
//        doc.add(new FloatField("float", toFloat(id), Field.Store.YES));
//
//        doc.add(new LongField("long", toLong(id), Field.Store.YES));
//
//        doc.add(new StringField("tag", getTag(id), Field.Store.YES));
//
//        doc.add(new StringField("txt", String.format("some text for %s", id), Field.Store.YES));
//
//        FieldType type = new FieldType();
//        type.setStored(true);
//        type.setIndexed(true);
//        type.setTokenized(true);
//        type.setStoreTermVectors(true);
//        type.setStoreTermVectorPositions(true);
//        type.setStoreTermVectorOffsets(true);
//        IndexableField f = new Field("allstored", String.format("some stored text for %s", id), type);
//        doc.add(f);
//
//        return doc;
//    }



    private static Document mockDocument(int id) {
        FieldType allFieldType = getAllFieldType();

        Document doc = new Document();

        doc.add(new IntPoint("id", id));
        doc.add(new StoredField("id", id));

        doc.add(new SortedDocValuesField ("id_str", new BytesRef(Integer.toString(id)) ));
        doc.add(new Field("id_str", Integer.toString(id), allFieldType));

        String contentValue = id % 2 == 0 ?
                "random " + UUID.randomUUID().toString() :
                UUID.randomUUID().toString();
        doc.add(new TextField("content", contentValue, Field.Store.YES));

        doc.add(new NumericDocValuesField("longid", id * 10));
        //doc.add(new StoredField("longid", id * 10));

        doc.add(new DoublePoint("double", toDouble(id)));
        doc.add(new StoredField("double", toDouble(id)));

        doc.add(new FloatPoint("float", toFloat(id)));
        doc.add(new StoredField("float", toFloat(id)));

        doc.add(new LongPoint("long", toLong(id)));
        doc.add(new StoredField("long", toLong(id)));

        doc.add(new Field("tag", getTag(id), allFieldType));

        IndexableField f = new Field("txt", String.format("some text for %s", id), allFieldType);
        doc.add(f);


        f = new Field("allstored", String.format("some stored text for %s", id), allFieldType);
        doc.add(f);

        return doc;
    }

    private static FieldType getAllFieldType() {
        FieldType type = new FieldType();
        type.setStored(true);
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        type.setTokenized(true);
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.setStoreTermVectorOffsets(true);
        return type;
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

    private static String getTag(int id) {
        double sqrt = Math.sqrt((double) id);
        if(sqrt % 1 == 0) return ROOT;
        
        if(id % 2 == 0) return EVEN;
        return ODD;
    }

}