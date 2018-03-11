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

import com.github.raymanrt.leila.formatter.DocumentFormatterWrapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.search.SortedSetSortField;

import static java.lang.String.format;

public class DocumentsReaderOptions {

	private final CommandLine cli;

	public DocumentsReaderOptions(final CommandLine cli) {
		this.cli = cli;
	}
	
	public DocumentFormatterWrapper getFormatter() {
		String[] pluginAndParams = cli.getOptionValues('p');
		return new DocumentFormatterWrapper(pluginAndParams);
	}

	public static Option fieldsOption() {
		return Option.builder("f")
				.desc("comma separated list of fields to show in output")
				.longOpt("fields")
				.hasArgs()
				.valueSeparator(',')
				.build();
	}

	public static Option pluginOption() {
		return Option.builder("p")
				.desc("class parameters")
				.longOpt("plugin")
				.hasArgs()
				.valueSeparator(' ')
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

	// TODO: refactor to make independent from cli
	// (in this case this parser will become testable)
	public Sort getSortByField() {
		final String sortBy = cli.getOptionValue('s', "");

		if(sortBy.isEmpty()) {
			return Sort.INDEXORDER;
		}

		final String[] tokens = sortBy.split(":");

		String sortFieldType = "sortfield";
		String fieldname = tokens[0];
		SortField.Type type = SortField.Type.STRING;
		boolean reverse = false;

		if(tokens.length > 1) {
			sortFieldType = maybeSortFieldType(tokens[1], sortFieldType);
			type = maybeType(tokens[1], type);
			reverse = maybeReverse(tokens[1]);
		}
		if(tokens.length > 2) {
			type = maybeType(tokens[2], type);
			reverse = maybeReverse(tokens[2]);
		}
		if(tokens.length > 3) {
			reverse = maybeReverse(tokens[3]);
		}

		SortField sortField;
		if(sortFieldType.equals("sortednumeric")) {
			sortField = new SortedNumericSortField(fieldname, type, reverse);
			// TODO: add selector
		} else if(sortFieldType.equals("sortedset")) {
			sortField = new SortedSetSortField(fieldname, reverse);
			// TODO: add selector
		} else {
			sortField = new SortField(fieldname, type, reverse);
		}
		Sort sort = new Sort(sortField);
		System.out.println(format(":: sortBy: %s", sort));

		return sort;

	}

	private String maybeSortFieldType(final String token, final String sortFieldType) {
		if(token.toLowerCase().equals("sortednumeric")) {
			return "sortednumeric";
		}
		if(token.toLowerCase().equals("sortedset")) {
			return "sortedset";
		}
		return sortFieldType;
	}

	private SortField.Type maybeType(final String token, final SortField.Type type) {
		if(token.toLowerCase().equals("integer")) {
			return SortField.Type.INT;
		}
		try {
			return SortField.Type.valueOf(token.toUpperCase());
		} catch(IllegalArgumentException e) {
			return type;
		}
	}

	private boolean maybeReverse(final String token) {
		if(token.toLowerCase().equals("reverse")) {
			return true;
		}
		if(token.toLowerCase().equals("desc")) {
			return true;
		}
		if(token.toLowerCase().equals("descending")) {
			return true;
		}
		return false;
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
