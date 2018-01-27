package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.LuceneDocIterator;
import com.github.raymanrt.leila.Util;
import com.github.raymanrt.leila.reader.OverviewReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class LuceneUtilsTest extends DemoIndexBuilderAbstractTest {

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
    public void getFieldsTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());

            Set<String> fields = Util.getFieldsFromIndex(searcher);

            Assert.assertEquals(6, fields.size());

            Assert.assertTrue(fields.containsAll(Arrays.asList("id", "id_str", "content", "double", "float", "long")));
        } catch (IOException e) {
            Assert.fail();
        }
    }


    @Test
    public void getFieldsWithCountTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());

            Set<String> fields = Util.getFieldsFromIndex(searcher, true);

            Assert.assertEquals(6, fields.size());

            Assert.assertTrue(fields.containsAll(Arrays.asList("id (100)", "id_str (100)", "content (100)", "double (100)", "float (100)", "long (100)")));
        } catch (IOException e) {
            Assert.fail();
        }
    }

}
