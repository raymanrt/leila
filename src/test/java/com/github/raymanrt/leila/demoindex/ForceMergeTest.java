package com.github.raymanrt.leila.demoindex;

import com.github.raymanrt.leila.CliParser;
import com.github.raymanrt.leila.Util;
import com.github.raymanrt.leila.option.TopTermsOptions;
import com.github.raymanrt.leila.writer.IndexMerger;
import org.apache.lucene.index.IndexWriter;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class ForceMergeTest extends DemoIndexBuilderAbstractTest {

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
    public void merge() {
        String index = Paths.get(MVN_TARGET, DEMO_INDEX).toString();
        try(IndexWriter writer = Util.getWriter(index)) {
            IndexMerger merger = new IndexMerger(writer, index);
            merger.start();
        } catch (Exception e) {
            Assert.fail();
        }
    }

}