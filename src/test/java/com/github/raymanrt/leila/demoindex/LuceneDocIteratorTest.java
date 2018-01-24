package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.LuceneDocIterator;
import com.github.raymanrt.leila.Util;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class LuceneDocIteratorTest extends DemoIndexBuilderTest {

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
    public void limitAlldocumentsTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    Sort.INDEXORDER,
                    10,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertEquals(10, iteratorCount(it));

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

    @Test
    public void sortedTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    new Sort(new SortField("id_str", SortField.Type.STRING, true)),
                    1,
                    new String[]{"float"},
                    new String[]{},
                    Collections.emptyMap()
            );

            Document doc = it.next();
            Assert.assertEquals(1, doc.getFields().size());
            Assert.assertEquals("0.99", doc.get("float"));

            Assert.assertFalse(it.hasNext());

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoreTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    Sort.INDEXORDER,
                    100,
                    new String[]{},
                    new String[]{"float"},
                    Collections.emptyMap()
            );

            while(it.hasNext()) {
                Assert.assertNull(it.next().getField("float"));
            }

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }
}
