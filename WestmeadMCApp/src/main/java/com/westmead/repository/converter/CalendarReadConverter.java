package com.westmead.repository.converter;

import java.util.Calendar;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class CalendarReadConverter implements Converter<Calendar, Date>{

	@Override
	public Date convert(Calendar source) {
		return source.getTime();
	}

}
