package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.LuceneDocIterator;
import com.github.raymanrt.leila.Util;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class LuceneDocIteratorTokenStreamTest extends DemoIndexBuilderAbstractTest {

    @Test
    public void firstDocumentTokenStreamTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    Sort.INDEXORDER,
                    1,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertTrue(it.hasNext());

            Assert.assertNotNull(it.next());

            List<Object> tokens = it.getTokenStream("txt");
            Assert.assertEquals(4, tokens.size());

            Assert.assertEquals("some <0-4>", tokens.get(0));
            Assert.assertEquals("text <5-9>", tokens.get(1));
            Assert.assertEquals("for <10-13>", tokens.get(2));
            Assert.assertEquals("0 <14-15>", tokens.get(3));


        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void firstDocumentRebuildTokenStreamTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    Sort.INDEXORDER,
                    1,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertTrue(it.hasNext());

            Assert.assertNotNull(it.next());

            List<Object> tokens = it.getTokenStream("allstored");
            Assert.assertEquals(5, tokens.size());

            Assert.assertEquals("some <0-4>", tokens.get(0));
            Assert.assertEquals("stored <5-11>", tokens.get(1));
            Assert.assertEquals("text <12-16>", tokens.get(2));
            Assert.assertEquals("for <17-20>", tokens.get(3));
            Assert.assertEquals("0 <21-22>", tokens.get(4));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

    @Test
    public void intPointTokenStreamTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());
            LuceneDocIterator it = new LuceneDocIterator(
                    searcher,
                    "*:*",
                    Sort.INDEXORDER,
                    1,
                    new String[]{},
                    new String[]{},
                    Collections.emptyMap()
            );

            Assert.assertTrue(it.hasNext());

            Assert.assertNotNull(it.next());

            List<Object> tokens = it.getTokenStream("id");
            Assert.assertEquals(1, tokens.size());

            Assert.assertEquals("0 <0-1>", tokens.get(0));

        } catch (IOException|ParseException e) {
            Assert.fail();
        }
    }

}
