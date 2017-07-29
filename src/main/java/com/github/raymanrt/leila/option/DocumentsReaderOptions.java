package com.github.raymanrt.leila.option;

import static java.lang.String.format;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;

import com.github.raymanrt.leila.formatter.DocumentFormatter;
import com.github.raymanrt.leila.formatter.SimpleDocumentFormatter;

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
	
	public static Option tokenStreamFieldsOption() {
		return Option.builder("s")
				.desc("comma separated list of fields to show in token stream")
				.longOpt("stream")
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
		return Option.builder("o")
				.desc("field for sorting (default '')")
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
		final String sortBy = cli.getOptionValue('o', "");
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
		if(cli.hasOption('s')) {
			fields = cli.getOptionValues('s');
			System.out.println(format(":: %d token streams to select: %s", fields.length, StringUtils.join(fields, ", ")));
		}
		return fields;
	}

}
