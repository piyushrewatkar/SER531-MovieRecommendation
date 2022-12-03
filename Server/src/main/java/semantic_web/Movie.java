package semantic_web;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {

    private Integer movie_id;
    private List<Cast> cast;
    private String overview;
    private String original_title;
    private Integer release_date;
    private List<Genre> genre;
    private String original_language;
    private Double vote_average;
    private Date release;

    
    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    public Integer getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(Integer movie_id) {
        this.movie_id = movie_id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public Integer getRelease_date() {
        return release_date;
    }

    public void setRelease_date(Integer release_date) {
        this.release_date = release_date;
    }

    public List<Genre> getGenre() {
        return genre;
    }

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", cast=" + cast +
                ", overview='" + overview + '\'' +
                ", original_title='" + original_title + '\'' +
                ", release_date=" + release +
                ", genre=" + genre +
                ", original_language='" + original_language + '\'' +
                ", vote_average=" + vote_average +
                '}';
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = Double.parseDouble(String.format("%.1f", vote_average));
    }
}
