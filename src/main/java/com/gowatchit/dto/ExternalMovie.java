package com.gowatchit.dto;

public class ExternalMovie {

    private String mediaId;
    private String title;
    private String originalReleaseDate;
    private String[] actors;
    private String director;
    
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOriginalReleaseDate() {
		return originalReleaseDate;
	}
	public void setOriginalReleaseDate(String originalReleaseDate) {
		this.originalReleaseDate = originalReleaseDate;
	}
	public String[] getActors() {
		return actors;
	}
	public void setActors(String[] actors) {
		this.actors = actors;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mediaId == null) ? 0 : mediaId.hashCode());
		return result;
	}
	@Override

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalMovie other = (ExternalMovie) obj;
		if (mediaId == null) {
			if (other.mediaId != null)
				return false;
		} else if (!mediaId.equals(other.mediaId))
			return false;
		return true;
	}

}
