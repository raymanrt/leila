package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.Util;
import org.apache.lucene.search.IndexSearcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * This test is also important for the correct work of overview mode
 */
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

            List<String> expectedFields = Arrays.asList("id", "id_str", "content", "double", "float", "long", "longid",
                    "tag", "txt", "allstored");
            Assert.assertEquals(expectedFields.size(), fields.size());

            Assert.assertTrue(fields.containsAll(expectedFields));
        } catch (IOException e) {
            Assert.fail();
        }
    }


    @Test
    public void getFieldsWithCountTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());

            Set<String> fieldsWithCount = Util.getFieldsFromIndex(searcher, true);

            System.out.print(fieldsWithCount.stream().filter(s -> s.contains("(0")).collect(Collectors.toList()));


            Map<String, String> expectedFieldCountMap = new HashMap<>();
            expectedFieldCountMap.put("id", "\\d{3}");
            expectedFieldCountMap.put("id_str", "100");
            expectedFieldCountMap.put("content", "\\d{3}");
            expectedFieldCountMap.put("double", "\\d{3}");
            expectedFieldCountMap.put("float", "\\d{3}");
            expectedFieldCountMap.put("long", "\\d{3}");
            expectedFieldCountMap.put("longid", "\\d{3}");
            expectedFieldCountMap.put("tag", "3");
            expectedFieldCountMap.put("txt", "\\d{3}");
            expectedFieldCountMap.put("allstored", "\\d{3}");

            for(Map.Entry<String, String> expectedFieldCount : expectedFieldCountMap.entrySet()) {
                Assert.assertTrue(existsElementMatchingPattern(fieldsWithCount, format("%s [(]%s terms[)]", expectedFieldCount.getKey(), expectedFieldCount.getValue())));
            }
        } catch (IOException e) {
            Assert.fail();
        }
    }

    private boolean existsElementMatchingPattern(Set<String> fieldsWithCount, String pattern) {
        for(String fieldWithCount : fieldsWithCount) {
            if(Pattern.matches(pattern, fieldWithCount)) {
                return true;
            }
        }
        return false;
    }

}
