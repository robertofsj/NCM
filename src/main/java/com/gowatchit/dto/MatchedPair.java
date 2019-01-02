package com.gowatchit.dto;


public class MatchedPair {
  private int gowatchitMovieId = -1;
  private String externalMovieId = null;

  public MatchedPair() {
  }

  public MatchedPair(int gowatchitMovieId, String externalMovieId) {
    this.gowatchitMovieId = gowatchitMovieId;
    this.externalMovieId = externalMovieId;
  }

  public int getGowatchitMovieId() {
    return gowatchitMovieId;
  }

  public void setGowatchitMovieId(int gowatchitMovieId) {
    this.gowatchitMovieId = gowatchitMovieId;
  }

  public String getExternalMovieId() {
    return externalMovieId;
  }

  public void setExternalMovieId(String externalMovieId) {
    this.externalMovieId = externalMovieId;
  }
}
