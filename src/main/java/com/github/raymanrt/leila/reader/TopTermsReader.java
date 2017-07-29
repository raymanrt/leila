package com.github.raymanrt.leila.reader;

import static java.lang.String.format;
import static org.apache.lucene.misc.HighFreqTerms.getHighFreqTerms;

import java.util.Arrays;

import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;

import com.github.raymanrt.leila.option.TopTermsOptions;

public class TopTermsReader {

	private final IndexSearcher searcher;
	private final TopTermsOptions options;

	public TopTermsReader(final IndexSearcher searcher, final TopTermsOptions options) {
		this.searcher = searcher;
		this.options = options;
	}

	public void read() {
		if(!options.isValid()) {
			return;
		}
		
		final String[] desiredFields = options.getDesiredFields();
		final int maxTopTerms = options.getMaxTopTerms();
		
		for(final String field : desiredFields) {
			System.out.println(format("top %d term: %s", maxTopTerms, field));
			try {
				for(final String topTerm : topTermsForField(field, maxTopTerms)) {
					System.out.println(topTerm);
				}
			} catch(final Exception ex) {
				System.out.println("not existing field");
			}
		}
	}
	
	private String[] topTermsForField(final String field, final int maxTopTerms) throws Exception {
		
		final TermStats[] commonTerms = getHighFreqTerms(searcher.getIndexReader(), maxTopTerms, field, new HighFreqTerms.DocFreqComparator());
		
		return Arrays.stream(commonTerms)
			.map(term -> term.termtext.utf8ToString() + " (" + term.docFreq + ")") // TODO: still a bug here, on accented or utf-8 chars
			.toArray(size -> new String[size]);		
	}

}
