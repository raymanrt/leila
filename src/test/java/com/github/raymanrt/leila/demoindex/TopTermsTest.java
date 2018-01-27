package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.CliParser;
import com.github.raymanrt.leila.Util;
import com.github.raymanrt.leila.option.TopTermsOptions;
import com.github.raymanrt.leila.reader.TopTermsReader;
import org.apache.lucene.search.IndexSearcher;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class TopTermsTest extends DemoIndexBuilderAbstractTest {

    @Test
    public void optionsTest() {
        try {
            CliParser cli = new CliParser(new String[] {"-t", "tag", "2"});

            TopTermsOptions options = cli.getTopTerrmsOptions();
            Assert.assertTrue(options.isValid());

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void findtagsTest() {
        try {
            IndexSearcher searcher = Util.getSearcher(Paths.get(MVN_TARGET, DEMO_INDEX).toString());

            TopTermsReader reader = new TopTermsReader(searcher, null);

            String[] topTerms = reader.topTermsForField("tag", 2);

            Assert.assertEquals(2, topTerms.length);

            Assert.assertTrue(asList(topTerms).containsAll(asList("odd (45)", "even (45)")));

        } catch (Exception e) {
            Assert.fail();
        }
    }

}
