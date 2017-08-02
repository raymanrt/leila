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

package com.github.raymanrt.leila;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.highlight.TokenSources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.apache.lucene.search.SortField.Type.STRING;

public class LuceneDocIterator implements Iterator<Object> {


	private final String[] fieldsToSelect;
	private final String[] fieldsToIgnore;

	private final Set<String> fieldsToLoad;
	
	private final IndexSearcher searcher;
	
	private final TopFieldDocs search;
	private int current = 0;
	private final int limit;
	private int currentDoc;

	public LuceneDocIterator(final IndexSearcher searcher,
			final String query, final String sortByField, final int limit, 
			final String[] fieldsToSelect, final String[] fieldsToIgnore) throws IOException, ParseException {
		
		// SELECT
		this.fieldsToSelect = fieldsToSelect;
		this.fieldsToIgnore = fieldsToIgnore;
		
		// FROM
		this.searcher = searcher;
		fieldsToLoad = fieldsToLoad();
		
		// WHERE
		final QueryParser queryParser = new QueryParser("", new WhitespaceAnalyzer()); // TODO: analyzer custom
		final Query parsedQuery = queryParser.parse(query);
		
		// ORDER BY
		final Sort sort = sortByField.isEmpty() ?
				new Sort(new SortField(sortByField, STRING)) :
				Sort.INDEXORDER; // TODO: sort per field di tipo non string, sort per multifield, ...
		
		// GROUP BY / HAVING ?
		
		// LIMIT
		final int maxDoc = searcher.getIndexReader().maxDoc();
		this.limit = limit < 0 ? maxDoc : Integer.min(limit, maxDoc);

		// TODO: print query time in ms
		search = searcher.search(parsedQuery, this.limit, sort);
		
	}

	private Set<String> fieldsToLoad() throws IOException {
		if(fieldsToSelect.length > 0) {
			return Arrays.stream(fieldsToSelect).collect(toSet());
		}
		
		final Set<String> fields = Util.getFieldsFromIndex(searcher, fieldsToIgnore);
		return fields;
	}
	
	public int getTotalHits() {
		return search.totalHits;
	}

	@Override
	public boolean hasNext() {
		return current < search.scoreDocs.length;
	}

	public void close() throws IOException {
		searcher.getIndexReader().close();
	}

	@Override
	public Document next() {
		Document doc;
		try {
			currentDoc = search.scoreDocs[current].doc;
			doc = searcher.getIndexReader().document(currentDoc, fieldsToLoad);
			current++;
			return doc;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Object> getTokenStream(final String field) {
		try {
			// TODO: custom analyzer
			final TokenStream ts = TokenSources.getAnyTokenStream(searcher.getIndexReader(), currentDoc, field, new WhitespaceAnalyzer());
			return getTokens(ts);
		} catch (final IOException|IllegalArgumentException e) {
//			e.printStackTrace();
		}
		return emptyList();
	}

	private List<Object> getTokens(final TokenStream tokenStream) throws IOException {
		final List<Object> tokens = new ArrayList<>();
		
		final OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
		final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
		    final int startOffset = offsetAttribute.startOffset();
		    final int endOffset = offsetAttribute.endOffset();
		    final String term = charTermAttribute.toString();
		    
		    tokens.add(format("%s <%d-%d>", term, startOffset, endOffset));
		}
		
		return tokens;
	}

}
