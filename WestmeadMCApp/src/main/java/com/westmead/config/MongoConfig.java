package com.westmead.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.westmead.repository.converter.CalendarReadConverter;
import com.westmead.repository.converter.CalendarWriteConverter;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

	@Autowired
	private MappingMongoConverter mongoConverter;

	@Override
	protected String getDatabaseName() {
		return "Westmead-DB";
	}

	@Override
	public MongoClient mongoClient() {
		final ConnectionString connectionString = new ConnectionString(
				"mongodb+srv://Priya:zaq12WSX@westmead-sb.amfml.mongodb.net/Westmead-DB?retryWrites=true&w=majority");
		final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString).build();
		return MongoClients.create(mongoClientSettings);
	}

	@Override
	public MongoCustomConversions customConversions() {
		converters.add(new CalendarReadConverter());
		converters.add(new CalendarWriteConverter());
		return new MongoCustomConversions(converters);
	}

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mongoConverter);
	}

}
