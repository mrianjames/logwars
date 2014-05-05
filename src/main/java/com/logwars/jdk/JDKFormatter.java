package com.logwars.jdk;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.logwars.Text;

public class JDKFormatter extends Formatter {

	private long today;

	/**
	 * Create a new formatter.
	 */
	public JDKFormatter() {
		super();
		this.today = Text.getToday();
	}


	
	protected void processNewLine(StringBuilder buffer) {
		buffer.append('\n');
	}

	@Override
	public String format(LogRecord record) {
		
		StringBuilder buffer = new StringBuilder(240);
		/*
		 * Format the time using the reusable date object.
		 */
		long millis = record.getMillis() - this.today;
		buffer.append(Text.toTime(millis));
		buffer.append(Text.SPACE);
		buffer.append("JULL ");
		/*
		 * The thread.
		 */
		buffer.append(record.getThreadID());
		buffer.append(Text.SPACE);
		buffer.append("Thread"+record.getThreadID());
		buffer.append(Text.SPACE);
		/*
		 * The level. non localized to save on resource bundle looking.
		 */
		Level level = record.getLevel();
		buffer.append(level.getName());
		buffer.append(Text.SPACE);

		buffer.append(record.getMessage());

		this.processNewLine(buffer);
		return buffer.toString();
	}

}
