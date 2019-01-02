package com.gowatchit.mapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.gowatchit.dto.ExternalMovie;

public class ExternalMovieMapper {
	static Logger log = Logger.getLogger(ExternalMovieMapper.class.getName());
	
	public static ExternalMovie convert(String source) {
		InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
		try (CSVParser csvParser = new CSVParser(isr, CSVFormat.DEFAULT);) {
				CSVRecord csvRecord = csvParser.getRecords().get(0);
				csvRecord.get(0);
				ExternalMovie extMovie = new ExternalMovie();
				  
				extMovie.setMediaId(csvRecord.get(2)); 
				extMovie.setTitle(csvRecord.get(3)); 
				extMovie.setOriginalReleaseDate(csvRecord.get(4));
				extMovie.setActors(csvRecord.get(15).split(","));
				extMovie.setDirector(csvRecord.get(16));
				return extMovie;
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}
}
