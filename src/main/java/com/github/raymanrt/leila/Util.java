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
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
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

			fields.add(formatField(searcher.getIndexReader(), fieldInfo.name, withCount));
		});

		return fields;
	}

	private static String formatField(IndexReader reader, final String field, final boolean withCount) {
		if(withCount) {
			try {
				long count = count(reader, field);
				return String.format("%s (%d terms)", field, count);
			} catch (IOException e) {
				return String.format("%s (???)", field);
			}
		}
		return field;
	}

	private static long count(final IndexReader reader, String field) throws IOException {
		return reader.getSumTotalTermFreq(field);
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
