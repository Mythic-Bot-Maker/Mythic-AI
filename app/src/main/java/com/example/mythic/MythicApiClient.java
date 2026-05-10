package com.example.mythic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MythicApiClient {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/responses";

    public String ask(String userPrompt, String fallbackAnswer) {
        if (MythicConfig.hasServerUrl()) {
            return askMythicServer(userPrompt, fallbackAnswer);
        }

        if (!MythicConfig.hasApiKey()) {
            return fallbackAnswer + "\n\nAdd your Mythic server URL in MythicConfig.java to turn on the real AI brain.";
        }

        return askOpenAiDirectly(userPrompt, fallbackAnswer);
    }

    private String askMythicServer(String userPrompt, String fallbackAnswer) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(MythicConfig.MYTHIC_SERVER_URL + "/ask");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            JSONObject body = new JSONObject();
            body.put("message", userPrompt);

            byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream output = connection.getOutputStream();
            output.write(payload);
            output.flush();
            output.close();

            int statusCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    statusCode >= 200 && statusCode < 300
                            ? connection.getInputStream()
                            : connection.getErrorStream(),
                    StandardCharsets.UTF_8
            ));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (statusCode < 200 || statusCode >= 300) {
                return "Mythic's server answered with an error. Check your server URL and API key.\n\n" + fallbackAnswer;
            }

            String reply = new JSONObject(response.toString()).optString("reply").trim();
            return reply.isEmpty() ? fallbackAnswer : reply;
        } catch (Exception error) {
            return "I could not reach Mythic's server right now, so I used my local brain instead.\n\n" + fallbackAnswer;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String askOpenAiDirectly(String userPrompt, String fallbackAnswer) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(OPENAI_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + MythicConfig.OPENAI_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            JSONObject body = new JSONObject();
            body.put("model", MythicConfig.MODEL);
            body.put("instructions", MythicConfig.SYSTEM_INSTRUCTIONS);
            body.put("input", userPrompt);
            body.put("max_output_tokens", 450);

            byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream output = connection.getOutputStream();
            output.write(payload);
            output.flush();
            output.close();

            int statusCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    statusCode >= 200 && statusCode < 300
                            ? connection.getInputStream()
                            : connection.getErrorStream(),
                    StandardCharsets.UTF_8
            ));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (statusCode < 200 || statusCode >= 300) {
                return "The AI API answered with an error. Check your API key and internet connection.\n\n" + fallbackAnswer;
            }

            return extractText(response.toString(), fallbackAnswer);
        } catch (Exception error) {
            return "I could not reach the AI API right now, so I used my local brain instead.\n\n" + fallbackAnswer;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String extractText(String json, String fallbackAnswer) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray output = root.getJSONArray("output");

            for (int i = 0; i < output.length(); i++) {
                JSONObject item = output.getJSONObject(i);
                if (!"message".equals(item.optString("type"))) {
                    continue;
                }

                JSONArray content = item.getJSONArray("content");
                for (int j = 0; j < content.length(); j++) {
                    JSONObject contentItem = content.getJSONObject(j);
                    if ("output_text".equals(contentItem.optString("type"))) {
                        String text = contentItem.optString("text").trim();
                        if (!text.isEmpty()) {
                            return text;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            return fallbackAnswer;
        }

        return fallbackAnswer;
    }
}
