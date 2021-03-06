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

package com.github.raymanrt.leila.formatter;

import org.apache.lucene.document.Document;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

public class SimpleDocumentFormatter implements Closeable {

	public SimpleDocumentFormatter(String ... args) {
		System.out.println(":: SimpleDocumentFormatter invoked with params: " + Arrays.asList(args));
	}

	public static String format(final Document document) {
		return document.toString();
	}

	@Override
	public void close() throws IOException {
		System.out.println("closing SimpleDocumentFormatter");
	}
}
