package com.mywork.finance.utils.plasticine;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.UnknownFormatConversionException;

public class PlasticineLayout extends PatternLayout {

	public String doLayout(ILoggingEvent event) {
		try {
			return String.format(super.doLayout(event), event.getArgumentArray());
		} catch (UnknownFormatConversionException e) {
			return super.doLayout(event);
		}
	}

}
