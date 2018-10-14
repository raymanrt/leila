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

import com.github.raymanrt.leila.reader.DocumentsReader;
import com.github.raymanrt.leila.reader.OverviewReader;
import com.github.raymanrt.leila.reader.TopTermsReader;
import com.github.raymanrt.leila.writer.IndexMerger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

public class Main {
	
	public static void main(final String[] args) {
		final CliParser cliParser = new CliParser(args);
		if (cliParser.isInvalid()) {
//			cliParser.printHelp();
			return;
		}

		if(cliParser.hasWriteMode()) {
			try(IndexWriter writer = Util.getWriter(cliParser.getIndex())) {

				if(cliParser.hasMerge()) {

					IndexMerger merger = new IndexMerger(writer, cliParser.getIndex());
					merger.start();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			return;
		}

		IndexSearcher searcher = null;

		try {

			searcher = Util.getSearcher(cliParser.getIndex());

			if (cliParser.hasOverviewOption()) {
				final OverviewReader overview = new OverviewReader(searcher);
				overview.read();
			}

			final TopTermsReader topTerms = new TopTermsReader(searcher, cliParser.getTopTerrmsOptions());
			topTerms.read();

			final DocumentsReader documents = new DocumentsReader(searcher, cliParser.getDocumentsReaderOptions(),
					cliParser.getFieldToDatatype());
			documents.read();


		} catch (final IOException ex) {
			System.out.println(String.format("problems reading index: %s [%s]", cliParser.getIndex(), ex.getMessage()));
		} catch (final org.apache.lucene.queryparser.classic.ParseException ex) {
			System.out.println("problems parsing lucene query: " + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
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
