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

package com.github.raymanrt.leila.option;

import com.github.raymanrt.leila.formatter.DocumentFormatter;
import com.github.raymanrt.leila.formatter.SimpleDocumentFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;

public class DocumentsReaderOptions {

	private final CommandLine cli;

	public DocumentsReaderOptions(final CommandLine cli) {
		this.cli = cli;
	}
	
	public DocumentFormatter getFormatter() {
		return new SimpleDocumentFormatter();
	}
	
	public static Option fieldsOption() {
		return Option.builder("f")
				.desc("comma separated list of fields to show in output")
				.longOpt("fields")
				.hasArgs()
				.valueSeparator(',')
		.build();
	}

	public static Option ignoreOption() {
		return Option.builder("i")
				.desc("comma separated list of fields to ignore for the diff")
				.longOpt("ignore")
				.hasArgs()
				.valueSeparator(',')
		.build();
	}
	
	public static Option termVectorFieldsOption() {
		return Option.builder("v")
				.desc("comma separated list of fields to show as term vector")
				.longOpt("vector")
				.hasArgs()
				.valueSeparator(',')
		.build();
	}

	public static Option queryOption() {
		return Option.builder("q")
				.desc("query to select documents (default *:*)")
				.longOpt("query")
				.hasArg()
		.build();
	}

	public static Option sortByOption() {
		return Option.builder("s")
				.desc("field for sorting (default INDEX ORDER)")
				.longOpt("sortBy")
				.hasArg()
		.build();
	}

	public static Option limitOption() {
		return Option.builder("l")
				.desc("limit results size (default 20, negative for unlimited)")
				.longOpt("limit")
				.hasArg()
		.build();
	}


	public String getQuery() {
		final String query = cli.getOptionValue('q', "*:*");
		System.out.println(format(":: query: %s", query));
		return query;
	}

	public String getSortByField() {
		final String sortBy = cli.getOptionValue('s', "");
		System.out.println(format(":: sortBy: %s", sortBy));
		return sortBy;
	}

	public int getLimit() {
		try {
			final int limit = Integer.parseInt(cli.getOptionValue('l', "20"));
			System.out.println(format(":: limit: %d", limit));
			return limit;
		} catch(final NumberFormatException ex) {
			return 20;
		}
	}

	public String[] getFields() {
		String[] fields = new String[] {};
		if(cli.hasOption('f')) {
			fields = cli.getOptionValues('f');
			System.out.println(format(":: %d fields to select: %s", fields.length, StringUtils.join(fields, ", ")));
		}
		return fields;
	}

	public String[] getIgnoreFields() {
		String[] fields = new String[] {};
		if(cli.hasOption('i')) {
			fields = cli.getOptionValues('i');
			System.out.println(format(":: %d fields to ignore in diff: %s", fields.length, StringUtils.join(fields, ", ")));
		}
		return fields;
	}

	public String[] getTokenStreamFields() {
		String[] fields = new String[] {};
		if(cli.hasOption('v')) {
			fields = cli.getOptionValues('v');
			System.out.println(format(":: %d term vector(s) to select: %s", fields.length, StringUtils.join(fields, ", ")));
		}
		return fields;
	}

}
