package com.logwars.log4j2;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import com.logwars.Text;

@Plugin(name="LogwarsConsoleLayout", category="Core", elementType="layout", printObject=true)
public class ConsoleLayout extends AbstractStringLayout {

	private long today;

	public ConsoleLayout() {
		super(Charset.defaultCharset());
		this.today = Text.getToday();

	}
	
	@Override
	public String toSerializable(LogEvent record) {
		StringBuilder buffer = new StringBuilder(240);
		/*
		 * Format the time using the reusable date object.
		 */
		long millis = record.getMillis() - this.today;
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

	@Override
	public Map<String, String> getContentFormat() {
		// TODO Auto-generated method stub
		return null;
	}
}
