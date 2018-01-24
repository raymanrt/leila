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

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.apache.lucene.index.MultiFields.getFields;

public class Util {
	
	public static IndexSearcher getSearcher(final String index) throws IOException {
		final Directory directory = FSDirectory.open(new File(index));
	    final DirectoryReader reader = DirectoryReader.open(directory);
		return new IndexSearcher(reader);
	}
	
	public static Set<String> getFieldsFromIndex(final IndexSearcher searcher, final String[] fieldsToIgnore, final boolean withCount) throws IOException {
		final Set<String> fieldsToIgnoreSet = stream(fieldsToIgnore).collect(toSet());
		
		final Fields indexFields = getFields(searcher.getIndexReader());
		final TermsEnum termsEnum = null;
		final LongAdder adder = new LongAdder();
		
		final Set<String> fields = new TreeSet<>();
		for(final String field : indexFields) {
			if(fieldsToIgnoreSet.contains(field)) {
				continue;
			}
			
			fields.add(formatField(indexFields, field, withCount, termsEnum, adder));
		}
		
		return fields;
	}

	private static String formatField(final Fields indexFields, final String field, final boolean withCount, final TermsEnum termsEnum, final LongAdder adder) throws IOException {
		if(withCount) {
			final long count = count(indexFields, field, termsEnum, adder);
			return String.format("%s (%d terms)", field, count);
		}
		return field;
	}

	private static long count(final Fields indexFields, final String field, final TermsEnum termsEnum, final LongAdder adder) throws IOException {
		adder.reset();
		final Terms terms = indexFields.terms(field);
		if(terms == null) {
			return 0;
		}
		final TermsEnum it = terms.iterator(termsEnum);
		while(it.next() != null) {
			adder.increment();
		}
		return adder.longValue();
	}

	public static Set<String> getFieldsFromIndex(final IndexSearcher searcher, final boolean withCount) throws IOException {
		return getFieldsFromIndex(searcher, new String[]{}, withCount);
	}

	public static Set<String> getFieldsFromIndex(final IndexSearcher searcher) throws IOException {
		return getFieldsFromIndex(searcher, new String[]{}, false);
	}
	
	public static Set<String> getFieldsFromIndex(final IndexSearcher searcher, final String[] fieldsToIgnore) throws IOException {
		return getFieldsFromIndex(searcher, fieldsToIgnore, false);
	}
}
