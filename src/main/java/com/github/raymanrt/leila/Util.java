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

import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public class Util {
	
	public static IndexSearcher getSearcher(final String index) throws IOException {
		final Directory directory = FSDirectory.open(Paths.get(index));
	    final DirectoryReader reader = DirectoryReader.open(directory);
		return new IndexSearcher(reader);
	}

	public static Set<String> getFieldsFromIndex(final IndexSearcher searcher, final String[] fieldsToIgnore, final boolean withCount) {
		final Set<String> fieldsToIgnoreSet = stream(fieldsToIgnore).collect(toSet());

		FieldInfos indexFields = MultiFields.getMergedFieldInfos(searcher.getIndexReader());

		final Set<String> fields = new TreeSet<>();
		indexFields.forEach(fieldInfo -> {
			if(fieldsToIgnoreSet.contains(fieldInfo.name)) {
				return;
			}

			fields.add(formatField(searcher.getIndexReader(), fieldInfo, withCount));
		});

		return fields;
	}

	private static String formatField(IndexReader reader, final FieldInfo field, final boolean withCount) {
		if(withCount) {
			try {
				long count = count(reader, field);
				return String.format("%s (%d terms)", field.name, count);
			} catch (IOException e) {
				return String.format("%s (???)", field.name);
			}
		}
		return field.name;
	}

	private static long count(final IndexReader reader, FieldInfo field) throws IOException {
		if(isPoint(field)) {
			return PointValues.size(reader, field.name);
		}

		if(isDocValue(field)) {
			return -1; // lucene doesn't store the inverted index for docvalues
			// then it's very expensive to count the distinct values
		}

		Terms terms = MultiFields.getTerms(reader, field.name);
		if(terms != null) {

			long size = 0;
			TermsEnum termsEnum = terms.iterator();
			while (termsEnum.next() != null) {
				size ++;
			}
			return size;

		}

		return -1;

	}

	private static boolean isDocValue(FieldInfo field) {
		return !field.getDocValuesType().equals(DocValuesType.NONE);
	}

	private static boolean isPoint(FieldInfo field) {
		return field.getPointDimensionCount() > 0 && field.getPointNumBytes() > 0;
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

    public static IndexWriter getWriter(String index) throws IOException {
		final Directory directory = FSDirectory.open(Paths.get(index));
		return new IndexWriter(directory, new IndexWriterConfig());
    }
}
