package com.github.raymanrt.leila.option;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class TopTermsOptions {

	private final CommandLine cli;

	public TopTermsOptions(final CommandLine cli) {
		this.cli = cli;
	}

	public boolean isValid() {
		return cli.hasOption('t');
	}
	


	public String[] getDesiredFields() {
		final String[] options = cli.getOptionValues('t');
		return options[0].split(",");
	}
	
	public int getMaxTopTerms() {
		try {
			String[] options = new String[] {};
			if(cli.hasOption('t')) {
				options = cli.getOptionValues('t');
//				System.out.println("::: DEBUG " + options[1]);
			}
			return Integer.parseInt(options[1]);
		} catch(ArrayIndexOutOfBoundsException|NumberFormatException ex) {
			
		}
		return 50;
	}
	


	public static Option topTermOption() {
		return Option.builder("t")
				.desc("top terms (first parameter: mandatory comma separated list of fields; "
						+ "second paramer: optional maximum number of desired terms, default 50 )")
				.longOpt("top")
				.hasArg()
				.numberOfArgs(2)
				.valueSeparator(' ')
		.build();
	}

}
