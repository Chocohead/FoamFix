package pl.asie.foamfix;

import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import org.ini4j.Config;
import org.ini4j.spi.IniFormatter;

public class BetterFormatter extends IniFormatter {
	private static final Field HEADER = FieldUtils.getDeclaredField(IniFormatter.class.getSuperclass(), "_header", true);

	public BetterFormatter() {
	}

	protected boolean isHeader() {
		try {
			return HEADER.getBoolean(this);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Error getting header field", e);
		}
	}

	@Override
	public void handleComment(String comment) {
		Config settings = getConfig();

		if (settings.isComment() && (!isHeader() || settings.isHeaderComment()) && StringUtils.isNotBlank(comment)) {
			PrintWriter writer = getOutput();

			for (String line : comment.split(settings.getLineSeparator())) {
				writer.print(';'); //A much more sensible choice than #
				writer.print(line);
				writer.print(settings.getLineSeparator());
			}
		}

		try {
			HEADER.setBoolean(this, false);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Error setting header field", e);
		}
	}
}