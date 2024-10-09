package pewpew.smash.game.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigReader {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> T readConfig(InputStream inputStream, Class<T> configClass) throws IOException {
        return objectMapper.readValue(inputStream, configClass);
    }

    public static <T> void writeConfig(File jsonFile, T configObject) throws IOException {
        objectMapper.writeValue(jsonFile, configObject);
    }
}
