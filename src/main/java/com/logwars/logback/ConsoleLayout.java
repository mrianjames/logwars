package com.logwars.logback;

import com.logwars.Text;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class ConsoleLayout extends LayoutBase<LoggingEvent> {

  private long today;

public ConsoleLayout() {
	  super();
	  this.today = Text.getToday();
  }
	
  public String doLayout(LoggingEvent record) {

		StringBuilder buffer = new StringBuilder(240);
		/*
		 * Format the time using the reusable date object.
		 */
		long millis = record.getTimeStamp() - this.today;
		buffer.append(Text.toTime(millis));
		buffer.append(Text.SPACE);
		buffer.append("LOGB ");
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