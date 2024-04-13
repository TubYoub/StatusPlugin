package de.tubyoub.statusplugin.commands;

import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Class responsible for checking if a new version of the plugin is available.
 */
public class VersionChecker {
    private static final String project = "km0yAITg";

    /**
     * Checks if a new version of the plugin is available.
     * @param version The current version of the plugin.
     * @return A boolean indicating whether a new version is available.
     */
    public static boolean isNewVersionAvailable(String version) {
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/" + project + "/version");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "TubYoub/StatusPlugin/"+ version +" (github@tubyoub.de)");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String jsonResponse = reader.lines().collect(Collectors.joining("\n"));

                    List<Map<String, Object>> versions = parseJsonArray(jsonResponse);
                    if (!versions.isEmpty()) {
                        String latestVersion = (String) versions.get(0).get("version_number");

                        String currentVersion = version;
                        return !latestVersion.equals(currentVersion);
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parses a JSON array into a list of maps.
     * @param jsonArray The JSON array to be parsed.
     * @return A list of maps representing the JSON array.
     */
    private static List<Map<String, Object>> parseJsonArray(String jsonArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonArray, new TypeReference<List<Map<String, Object>>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return Arrays.asList(); // Handle parsing exception
        }
    }
}