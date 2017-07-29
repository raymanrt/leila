/*
 * Copyright 2017 Riccardo Tasso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.raymanrt.leila.reader;

import com.github.raymanrt.leila.option.TopTermsOptions;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;

import java.util.Arrays;

import static java.lang.String.format;
import static org.apache.lucene.misc.HighFreqTerms.getHighFreqTerms;

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
