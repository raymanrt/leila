package com.github.raymanrt.leila;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.RamUsageEstimator;

import com.github.raymanrt.leila.reader.DocumentsReader;
import com.github.raymanrt.leila.reader.OverviewReader;
import com.github.raymanrt.leila.reader.TopTermsReader;

public class Main {
	
	public static void main(final String[] args) {
		final CliParser cliParser = new CliParser(args);
		if(cliParser.isInvalid()) {
			cliParser.printHelp();
			return;
		}
		
		IndexSearcher searcher = null;
		
		try { 
			
			searcher = Util.getSearcher(cliParser.getIndex());

			final OverviewReader overview = new OverviewReader(searcher);
			overview.read();
			
			final TopTermsReader topTerms = new TopTermsReader(searcher, cliParser.getTopTerrmsOptions());
			topTerms.read();
			
			final DocumentsReader documents = new DocumentsReader(searcher, cliParser.getDocumentsReaderOptions());
			documents.read();
			
			
		} catch(final IOException ex) {
			System.out.println(String.format("problems reading index: %s [%s]", cliParser.getIndex(), ex.getMessage()));
		} catch(final org.apache.lucene.queryparser.classic.ParseException ex) {
			System.out.println("problems parsing lucene query: " + ex.getMessage());
		} catch (final Exception e) {
			// TODO: topTerms launches this exception
			e.printStackTrace();
		} finally {
			closeQuietly(searcher);
		}
		
	}

	private static void closeQuietly(final IndexSearcher searcher) {
		if(searcher != null) {
			try {
				searcher.getIndexReader().close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
