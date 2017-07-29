package com.github.raymanrt.leila;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.raymanrt.leila.option.DocumentsReaderOptions;
import com.github.raymanrt.leila.option.TopTermsOptions;

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
