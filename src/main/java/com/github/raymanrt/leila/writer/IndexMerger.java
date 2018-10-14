package com.github.raymanrt.leila.writer;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexWriter;

import java.io.File;
import java.io.IOException;

public class IndexMerger {
    private IndexWriter writer;
    private String index;

    public IndexMerger(IndexWriter writer, String index) {
        this.writer = writer;

        this.index = index;
    }

    public void start() throws IOException {
        File dir = new File(index);
        System.out.println("starting force merge to 1 segment: " + FileUtils.sizeOfDirectory(dir) + " bytes");
        writer.forceMerge(1, true);
        writer.commit();
        System.out.println("merged to 1 segment: " + FileUtils.sizeOfDirectory(dir) + " bytes");
    }
}
