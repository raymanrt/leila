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

import com.github.raymanrt.leila.LuceneDocIterator;
import com.github.raymanrt.leila.formatter.DocumentFormatterWrapper;
import com.github.raymanrt.leila.option.DocumentsReaderOptions;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class DocumentsReader {

	private final IndexSearcher searcher;
	private final DocumentsReaderOptions options;
	private final Map<String, String> fieldToDatatype;

	public DocumentsReader(final IndexSearcher searcher, final DocumentsReaderOptions documentsReaderOptions, final Map<String, String> fieldToDatatype) {
		this.searcher = searcher;
		this.options = documentsReaderOptions;
		this.fieldToDatatype = fieldToDatatype;
	}

	public void read() throws IOException, ParseException {
		final String[] fields = options.getFields();
		final String[] ignores = options.getIgnoreFields();
		
		final String[] tokenStreamFields = options.getTokenStreamFields();

		final String query = options.getQuery();
		final Sort sortByField = options.getSortByField();
		final int limit = options.getLimit();
		
		try(final DocumentFormatterWrapper formatter = options.getFormatter()) {
			if(limit < 1) {
				return;
			}

			System.out.println(format(":: listing documents"));
			final LuceneDocIterator docs = new LuceneDocIterator(searcher, query, sortByField, limit, fields, ignores, fieldToDatatype);

			System.out.println(":: total documents found: " + docs.getTotalHits());

			System.out.println(format(":: query time: %dms", docs.getQueryTimeInMs()));

			while(docs.hasNext()) {
				String nextFormattedDocument = formatter.format(docs.next());
				if(!nextFormattedDocument.isEmpty()) {
					System.out.println(nextFormattedDocument);
				}

				for(final String tokenStreamField : tokenStreamFields) {
					final List<Object> tokenStream = docs.getTokenStream(tokenStreamField);
					if(!tokenStream.isEmpty()) {
						System.out.println(format("token stream [%s]: %s", tokenStreamField, tokenStream));
					}
				}
			}
		}

	}

}