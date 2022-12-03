package semantic_web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cast {
    private String cast_name;
    private String cast_character;

    public Cast(String cast_name, String cast_character) {
        this.cast_name = cast_name;
        this.cast_character = cast_character;
    }

    public String getCast_name() {
        return cast_name;
    }

    public void setCast_name(String cast_name) {
        this.cast_name = cast_name;
    }

    public String getCast_character() {
        return cast_character;
    }

    public void setCast_character(String cast_character) {
        this.cast_character = cast_character;
    }

    @Override
    public String toString() {
        return "Cast{" +
                "cast_name='" + cast_name + '\'' +
                ", cast_character='" + cast_character + '\'' +
                '}';
    }
}
