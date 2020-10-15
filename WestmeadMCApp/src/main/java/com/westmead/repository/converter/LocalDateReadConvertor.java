package com.westmead.repository.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public class LocalDateReadConvertor implements Converter<String, LocalDate>{

	private static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public LocalDate convert(String source) {
		return LocalDate.parse(source, DATEFORMATTER);
	}

}
