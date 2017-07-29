package com.github.raymanrt.leila.formatter;

import org.apache.lucene.document.Document;

public interface DocumentFormatter {
	
	public String format(Document document);

}
