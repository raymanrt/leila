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
import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class CliParser {
	final static CommandLineParser CLI_PARSER = new DefaultParser();
	final static Options options = new Options()
			// TODO: verbose
			
			// query
			.addOption(overviewOption())
			.addOption(DocumentsReaderOptions.fieldsOption())
			.addOption(DocumentsReaderOptions.ignoreOption())
			.addOption(DocumentsReaderOptions.termVectorFieldsOption())
			.addOption(DocumentsReaderOptions.queryOption())
			.addOption(DocumentsReaderOptions.sortByOption())
			.addOption(DocumentsReaderOptions.limitOption())
			.addOption(DocumentsReaderOptions.pluginOption())
			
			// TODO: list fields -f (with freq?)
			
			// TODO: list (top) terms -t (by field, how many)
			.addOption(TopTermsOptions.topTermOption())

			.addOption(datatypesOption())
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

	public static Option overviewOption() {
		return Option.builder("o")
				.desc("enables overview mode")
				.longOpt("overview")
				.hasArg(false)
				.build();
	}

	public boolean hasOverviewOption() {
		return cli.hasOption('o');
	}



	public static Option datatypesOption() {
		return Option.builder("d")
				.desc("comma separated list of fields with their explicit datatype (e.g. id:int)")
				.longOpt("datatypes")
				.hasArgs()
				.valueSeparator(',')
				.build();
	}

    public Map<String, String> getFieldToDatatype() {
		final Map<String, String> fieldsToDatatypes = new HashMap<>();
		if(cli.hasOption('d')) {
			String[] fieldAndDatatypes = cli.getOptionValues('d');

			for(String fieldAndDatatype : fieldAndDatatypes) {
				String[] tokens = fieldAndDatatype.trim().split(":");
				String field = tokens[0].trim();
				String datatype = tokens[1].trim();
				fieldsToDatatypes.put(field, datatype);
			}

			System.out.println(format(":: datatype infos: %s", fieldsToDatatypes));
		}
		return fieldsToDatatypes;
    }
}
