package com.gowatchit;

import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gowatchit.dto.MatchedPair;
import com.gowatchit.matcher.IMatcher;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class AppTest extends TestCase
{
    @Autowired
    private IMatcher matcher;

    @org.junit.Test
    public void matchTest()
    {
        List<MatchedPair> matchedMovies = matcher.process("xbox_feed.csv","movies.csv","actors_and_directors.csv");
        for(MatchedPair movie : matchedMovies){
            assertNotSame(-1,movie.getGowatchitMovieId());
            assertNotSame(null,movie.getExternalMovieId());
        }
        assertTrue("Nothing matched!", matchedMovies.size() > 0);
        System.out.println("Total items matched :"+ matchedMovies.size());
    }
}
