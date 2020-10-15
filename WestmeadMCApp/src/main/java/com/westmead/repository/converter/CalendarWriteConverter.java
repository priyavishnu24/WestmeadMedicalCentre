package com.westmead.repository.converter;

import java.util.Calendar;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class CalendarWriteConverter implements Converter<Date, Calendar>{

	@Override
	public Calendar convert(Date source) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(source);
		return calendar;
	}

}
