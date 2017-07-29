package com.github.raymanrt.leila.formatter;

import org.apache.lucene.document.Document;

public class SimpleDocumentFormatter implements DocumentFormatter {

	@Override
	public String format(final Document document) {
		return document.toString();
	}

}
