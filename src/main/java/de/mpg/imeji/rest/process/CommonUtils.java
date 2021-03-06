package de.mpg.imeji.rest.process;

import java.net.URI;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CommonUtils {
	
	public static final String FILENAME_RENAME_EMPTY = "type a new filename if you want to rename it";
	public static final String FILENAME_RENAME_INVALID_SUFFIX = "invalid suffix";
	public static final String JSON_Invalid ="invalid json input";
	public static final String JAVAX_SERVLET_CONTEXT_TEMPDIR = "javax.servlet.context.tempdir";


	public static String formatDate(Date d) {
		String output = "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		output = f.format(d);
		f = new SimpleDateFormat("HH:mm:ss Z");
		output += "T" + f.format(d);
		return output;

	}

	public static String extractIDFromURI(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
	}

}
