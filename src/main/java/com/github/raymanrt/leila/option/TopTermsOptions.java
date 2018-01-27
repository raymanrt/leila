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
