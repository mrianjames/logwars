package com.logwars.log4j;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.logwars.Text;

public class ConsoleLayout extends SimpleLayout {

	private long today;
	
	public ConsoleLayout() {
		super();
		this.today = Text.getToday();

	}
	
	public String format(LoggingEvent record) {

		StringBuilder buffer = new StringBuilder(240);
		/*
		 * Format the time using the reusable date object.
		 */
		long millis = record.timeStamp - this.today;
		buffer.append(Text.toTime(millis));
		buffer.append(Text.SPACE);
		buffer.append("LO4J ");
		/*
		 * The thread.
		 */
		buffer.append(Text.ZERO);
		buffer.append(Text.SPACE);
		buffer.append(record.getThreadName());
		buffer.append(Text.SPACE);
		/*
		 * The level. non localized to save on resource bundle looking.
		 */
		buffer.append(record.getLevel().toString());
		buffer.append(Text.SPACE);

		buffer.append(record.getMessage());
		buffer.append(Text.NEW_LINE);
		return buffer.toString();
	}

	
}
