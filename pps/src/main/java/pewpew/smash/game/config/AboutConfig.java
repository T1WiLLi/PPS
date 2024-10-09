package pewpew.smash.game.config;

import java.util.List;

import lombok.Getter;

@Getter
public class AboutConfig {

    private List<CreditSection> credits;

    @Getter
    public static class CreditSection {
        private String section;
        private List<CreditContent> content;
    }

    @Getter
    public static class CreditContent {
        private String title;
        private List<Artist> artists;
    }

    @Getter
    public static class Artist {
        private String author;
        private String website;
        private String license;
        private List<String> assets;
    }
}
