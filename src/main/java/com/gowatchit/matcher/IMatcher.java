package com.gowatchit.matcher;

import java.util.List;
import com.gowatchit.dto.MatchedPair;

public interface IMatcher {

  List<MatchedPair> process(String matchFromFile, String moviesDb, String contributorsDb);

}
