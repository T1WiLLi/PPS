package pewpew.smash.database;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import pewpew.smash.game.utils.ResourcesLoader;

public final class DatabaseConfig {

    private final static DatabaseConfig INSTANCE = new DatabaseConfig();

    @Getter
    private String HOST;
    @Getter
    private String USER;
    @Getter
    private String PASSWORD;

    protected static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    public void consume() {
        this.HOST = "N/A";
        this.USER = "N/A";
        this.PASSWORD = "N/A";
    }

    private DatabaseConfig() {
        try {
            loadConfig();
        } catch (IOException e) {
            consume();
            e.printStackTrace();
        }
    }

    private void loadConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper
                .readTree(ResourcesLoader.getMiscFile(ResourcesLoader.CONFIG_PATH, "databaseConfig.json"));
        JsonNode databaseNode = root.path("database");

        this.HOST = databaseNode.path("host").asText();
        this.USER = databaseNode.path("user").asText();
        this.PASSWORD = databaseNode.path("password").asText();
    }
}
