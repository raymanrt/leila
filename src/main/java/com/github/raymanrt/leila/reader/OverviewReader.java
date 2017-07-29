package com.github.raymanrt.leila.reader;

import static com.github.raymanrt.leila.Util.getFieldsFromIndex;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.search.IndexSearcher;

public class OverviewReader {

	private final IndexSearcher searcher;

	public OverviewReader(final IndexSearcher searcher) {
		this.searcher = searcher;
	}

	public void read() throws IOException {
		fields();
		numberOfDocuments();
		numberOfTerms();
		hasDeletions();
		isOptimized();
		indexVersion();
		indexFormat();
		indexFunctionalities();
		termInfosIndexDivisor();
		directoryImplementation();
		commit();
	}
	

	private void numberOfDocuments() {
		System.out.println(":: number of documents: " + searcher.getIndexReader().numDocs());
		System.out.println(":: number of deleted documents: " + searcher.getIndexReader().numDeletedDocs());
	}

	private void numberOfTerms() {
		/**
		 * unusefull and expensive
		 */
	}

	private void hasDeletions() {
		/**
		 * You could simply count the number of deleted documents
		 */
	}

	private void isOptimized() {
		// TODO Auto-generated method stub
	}

	private void indexVersion() {
		final long indexVersion = ((DirectoryReader)searcher.getIndexReader()).getVersion();
		System.out.println(":: index version: " + indexVersion);
		
	}

	private void indexFormat() {
		/**
		 * @see: IndexGate (luke)
		 */
	}

	private void indexFunctionalities() {
		// TODO: not so easy/important
	}

	private void termInfosIndexDivisor() {
		// TODO: not so easy/important
	}
	
	private void directoryImplementation() {
		final Class<? extends DirectoryReader> directoryImplementation = ((DirectoryReader)searcher.getIndexReader()).getClass();
		System.out.println(":: directory implementation: " + directoryImplementation.getCanonicalName());
		
	}

	private void commit() {
		try {
			final IndexCommit commit = ((DirectoryReader) searcher.getIndexReader()).getIndexCommit();
			System.out.print(":: commit: " + commit.getSegmentsFileName());
			System.out.print(" ( generation: " + commit.getGeneration());
			System.out.print(" segments: " + commit.getSegmentCount() + " )");
			System.out.println();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void fields() throws IOException {
		final boolean verbose = true; // TODO: verbose parameter
		final Collection<String> fields = getFieldsFromIndex(searcher, verbose); // TODO: sort by field or by count?
		if(verbose) {
			System.out.println(format(":: %s fields:", fields.size()));
			System.out.print(StringUtils.join(fields, "\n"));
		} else {
			System.out.println(format(":: %s fields: %s", fields.size(), fields));
		}
	}


}
