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

import com.github.raymanrt.leila.option.DocumentsReaderOptions;
import com.github.raymanrt.leila.option.TopTermsOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliParser {
	final static CommandLineParser CLI_PARSER = new DefaultParser();
	final static Options options = new Options()
			// TODO: verbose
			
			// query
			.addOption(DocumentsReaderOptions.fieldsOption()) // TODO: -s
			.addOption(DocumentsReaderOptions.ignoreOption())
			.addOption(DocumentsReaderOptions.tokenStreamFieldsOption())
			.addOption(DocumentsReaderOptions.queryOption())
			.addOption(DocumentsReaderOptions.sortByOption())
			.addOption(DocumentsReaderOptions.limitOption())
			
			// TODO: index metadata -m?
			
			// TODO: list fields -f (with freq?)
			
			// TODO: list (top) terms -t (by field, how many)
			.addOption(TopTermsOptions.topTermOption())
	;
	final static HelpFormatter formatter = new HelpFormatter();
	
	private String index = null;
	private CommandLine cli = null;
	private TopTermsOptions topTermsOptions;
	private DocumentsReaderOptions documentsReaderOptions;
	

	public CliParser(final String[] args) {
		try {
			this.index = args[0];
			this.cli = CLI_PARSER.parse(options, args);
			
			this.topTermsOptions = new TopTermsOptions(cli);
			this.documentsReaderOptions = new DocumentsReaderOptions(cli);
		} catch(final ArrayIndexOutOfBoundsException|ParseException ex) {
			formatter.printHelp("leila [lucene index] [options]", options);
		}
	}

	public boolean isInvalid() {
		return index == null || cli == null;
	}

	public void printHelp() {
		formatter.printHelp("leila [lucene index] [options]", options);
	}

	public String getIndex() {
		return this.index;
	}

	public TopTermsOptions getTopTerrmsOptions() {
		return this.topTermsOptions;
	}

	public DocumentsReaderOptions getDocumentsReaderOptions() {
		return this.documentsReaderOptions;
	}
}
