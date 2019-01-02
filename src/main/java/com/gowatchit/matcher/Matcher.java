package com.gowatchit.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.gowatchit.dto.ExternalMovie;
import com.gowatchit.dto.MatchedPair;
import com.gowatchit.mapper.ExternalMovieMapper;

@Component
public class Matcher implements IMatcher{

	static Logger log = Logger.getLogger(Matcher.class.getName());
	private static final String REGEX_LETTERS = "[^A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]";
	private List<MatchedPair> matchedPairList;
	private List<String> contributors;
	private Set<ExternalMovie> externalMovies;
	private List<String> internalMovies;
	
	public List<MatchedPair> process(String matchFromFile, String moviesDb, String contributorsDb) {
		matchedPairList = Collections.synchronizedList(new ArrayList<MatchedPair>());

		//RETRIEVE ONLY MOVIES FROM EXTERNAL SOURCE
		File file = new File(getClass().getClassLoader().getResource(matchFromFile).getFile());
	    try (Stream<String> externalMovieStream = Files.lines(file.toPath())) {
	    	externalMovies = externalMovieStream.skip(1)
	    										.filter(line -> line.contains(",Movie,"))
								    			.map(s -> ExternalMovieMapper.convert(s))
									 			.distinct()
	    										.collect(Collectors.toSet());
		} catch (IOException e) {
			log.error(e);
		}

	    //RETRIEVE ONLY CAST AND DIRECTOR (NO WRITER)
		File fileContributor = new File(getClass().getClassLoader().getResource(contributorsDb).getFile());
	    try (Stream<String> contributorStream = Files.lines(fileContributor.toPath())) {
	    	contributors = contributorStream.skip(1)
	    									.filter(line -> line.endsWith(",cast") || line.endsWith(",director"))
	    									.collect(Collectors.toList());
		} catch (IOException e) {
			log.error(e);
		}

	    //RETRIEVE INTERNAL MOVIES
		File fileMovie = new File(getClass().getClassLoader().getResource(moviesDb).getFile());
    	try (Stream<String> internalMovieStream = Files.lines(fileMovie.toPath())){
			internalMovies = internalMovieStream.parallel()
												.map(s -> s.replaceAll("\"", ""))
												.collect(Collectors.toList());
		} catch (IOException e) {
			log.error(e);
		}


    	//INTERSECTION BETWEEN INTERNAL AND EXTERNAL MOVIES
    	Set<String> externalTitles = externalMovies.parallelStream().map(s -> s.getTitle())
    										   			   			 .distinct()
    										   			   			 .collect(Collectors.toSet());
	    	
    	Set<String> titles = internalMovies.parallelStream().map(s -> s.split(",")[1])
	    													 .distinct()
	    													 .filter(externalTitles::contains)
	    													 .collect(Collectors.toSet());

    	//MATCH INTERNAL MOVIE WITH EXTERNAL SOURCE
    	titles.parallelStream().forEach(line -> doMatch(line));
		return matchedPairList;
	}

	private void doMatch(String title) {
		Set<String> movies = internalMovies.parallelStream().filter(line -> line.split(",")[1].equalsIgnoreCase(title))
														    .collect(Collectors.toSet());
    	
		for (String movieLine : movies) {
			String[] movieArr = movieLine.split(",");
			String id 	 = movieArr[0];
			String year  = movieArr[2];
			String director = "";
			String actors = "";

			//RETRIEVE ACTORS AND DIRECTOR
			Set<String> list = contributors.parallelStream().filter(line -> line.startsWith(id+",")).collect(Collectors.toSet());
			for (String contributorLine : list) {
				if(contributorLine.endsWith("cast"))
					actors = String.join(",", contributorLine.split(",")[1].replaceAll(REGEX_LETTERS, ""), actors);
				else
					director = contributorLine.split(",")[1].replaceAll(REGEX_LETTERS, "");
			}
			
			//FILTER ON EXTERNAL MOVIES AND MATCH CRITERIAS
			Set<ExternalMovie> extarnalMovies = externalMovies.parallelStream().filter(line -> line.getTitle().equalsIgnoreCase(title))
																	 		   .collect(Collectors.toSet());
			int criteriaMatched = 0;
			for (ExternalMovie extMovie : extarnalMovies) {
			
				//DIRECTOR CRITERIA
				if(director.equalsIgnoreCase(extMovie.getDirector()))	
					criteriaMatched++;
				
				//YEAR CRITERIA
				if(!StringUtils.isEmpty(year) && !year.equalsIgnoreCase("NULL")) {
					if(year.replace(";", "").equals(extMovie.getOriginalReleaseDate().split(" ")[0].split("/")[2]))
						criteriaMatched++;
				}
				
				//ACTOR CRITERIA
				String lowerCaseActors = actors.toLowerCase();
				for (String actor : extMovie.getActors()) {
					if(lowerCaseActors.contains(actor.toLowerCase().replaceAll(REGEX_LETTERS, "").trim()))
						criteriaMatched++;
				}
			
				if(criteriaMatched >= 2) {
					synchronized (matchedPairList) {
						matchedPairList.add(new MatchedPair(Integer.valueOf(id), extMovie.getMediaId()));
					}
					break;
				}
				criteriaMatched = 0;
			}
		}
	}
}
