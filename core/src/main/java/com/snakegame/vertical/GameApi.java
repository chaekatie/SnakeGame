package com.snakegame.vertical;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.Preferences;

import java.net.URLEncoder;
import java.util.function.Consumer;

public class GameApi {

    private static final String BASE_URL = "http://localhost:8080/api/game";
    private static final String BASE_URL_2 = "http://localhost:8080/api/auth";
    //private static final String BASE_URL_3 = "http://localhost:8080/api/scores";
    private static final String AUTH_TOKEN_KEY = "auth_token";

    public interface GameStateCallback {
        void onSuccess(GameStateDTO gameState);
        void onError(Throwable t);
    }

    public interface LoginCallback {
        void onSuccess(String token);
        void onError(String error);
    }

    public interface SendEmailCallback {
        void onSuccess(String message);
        void onError(String error);
    }

//    public interface LeaderboardCallback {
//        void onSuccess(Array<ScoreDTO> scores);
//        void onError(String error);
//    }

    private static String getAuthToken() {
        Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
        return prefs.getString(AUTH_TOKEN_KEY, "");
    }

    private static void setAuthToken(String token) {
        Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
        prefs.putString(AUTH_TOKEN_KEY, token);
        prefs.flush();
    }

    public static void fetchGameState(GameStateCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError(new Exception("Not authenticated. Please login first."));
            return;
        }

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        HttpRequest request = requestBuilder.newRequest()
            .method("GET")
            .url(BASE_URL + "/state")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.app.log("GameApi", "Sending request to: " + BASE_URL + "/state");

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                try {
                    int statusCode = httpResponse.getStatus().getStatusCode();
                    Gdx.app.log("GameApi", "Response status code: " + statusCode);

                    if (statusCode == 403) {
                        callback.onError(new Exception("Authentication failed. Please login again."));
                        return;
                    }

                    if (statusCode != 200) {
                        String error = "Server returned status code: " + statusCode;
                        Gdx.app.error("GameApi", error);
                        callback.onError(new Exception(error));
                        return;
                    }

                    String json = httpResponse.getResultAsString();
                    Gdx.app.log("GameApi", "Raw JSON response length: " + (json != null ? json.length() : 0));
                    Gdx.app.log("GameApi", "Raw JSON response: " + json);

                    if (json == null || json.trim().isEmpty()) {
                        String error = "Empty response from server";
                        Gdx.app.error("GameApi", error);
                        callback.onError(new Exception(error));
                        return;
                    }

                    // Parse JSON using JsonReader
                    JsonReader jsonReader = new JsonReader();
                    JsonValue root = jsonReader.parse(json);

                    GameStateDTO gameState = new GameStateDTO();

                    // Parse snake body
                    JsonValue snakeBodyJson = root.get("snakeBody");
                    if (snakeBodyJson != null) {
                        gameState.snakeBody = new java.util.ArrayList<>();
                        for (JsonValue pos : snakeBodyJson) {
                            GameStateDTO.PositionDTO position = new GameStateDTO.PositionDTO();
                            position.x = pos.getInt("x");
                            position.y = pos.getInt("y");
                            gameState.snakeBody.add(position);
                        }
                    }

                    // Parse foods
                    JsonValue foodsJson = root.get("foods");
                    if (foodsJson != null) {
                        gameState.foods = new java.util.ArrayList<>();
                        for (JsonValue food : foodsJson) {
                            GameStateDTO.FoodDTO foodDTO = new GameStateDTO.FoodDTO();
                            JsonValue pos = food.get("position");
                            foodDTO.position = new GameStateDTO.PositionDTO();
                            foodDTO.position.x = pos.getInt("x");
                            foodDTO.position.y = pos.getInt("y");
                            foodDTO.type = GameStateDTO.FoodType.valueOf(food.getString("type"));
                            foodDTO.points = food.getInt("points");
                            gameState.foods.add(foodDTO);
                        }
                    }

                    // Parse other fields
                    gameState.score = root.getInt("score", 0);
                    gameState.gameOver = root.getBoolean("gameOver", false);

                    Gdx.app.log("GameApi", "Successfully parsed game state:");
                    Gdx.app.log("GameApi", "- Snake size: " + gameState.snakeBody.size());
                    Gdx.app.log("GameApi", "- Foods size: " + gameState.foods.size());
                    Gdx.app.log("GameApi", "- Score: " + gameState.score);
                    Gdx.app.log("GameApi", "- Game Over: " + gameState.gameOver);

                    callback.onSuccess(gameState);
                } catch (Exception e) {
                    Gdx.app.error("GameApi", "Error parsing game state: " + e.getMessage());
                    e.printStackTrace();
                    callback.onError(e);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameApi", "HTTP request failed: " + t.getMessage());
                t.printStackTrace();
                callback.onError(t);
            }

            @Override
            public void cancelled() {
                Gdx.app.error("GameApi", "Request cancelled");
                callback.onError(new Exception("Request cancelled"));
            }
        });
    }

    public static void sendDirection(Direction direction) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            Gdx.app.error("GameApi", "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        String directionJson = "\"" + direction.name() + "\"";

        Gdx.app.log("GameApi", "Sending direction: " + directionJson);
        Gdx.app.log("GameApi", "Direction enum value: " + direction.name());

        HttpRequest request = requestBuilder.newRequest()
            .method("POST")
            .url(BASE_URL + "/direction")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .content(directionJson) // Send as JSON
            .build();

        Gdx.app.log("GameApi", "Request URL: " + request.getUrl());
        Gdx.app.log("GameApi", "Request method: " + request.getMethod());
        Gdx.app.log("GameApi", "Request headers: " + request.getHeaders());
        Gdx.app.log("GameApi", "Request content: " + request.getContent());

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                Gdx.app.log("GameApi", "Direction response status: " + httpResponse.getStatus().getStatusCode());
                Gdx.app.log("GameApi", "Direction response: " + httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameApi", "Failed to send direction: " + t.getMessage());
                t.printStackTrace();
            }

            @Override
            public void cancelled() {
                Gdx.app.log("GameApi", "Direction request cancelled");
            }
        });
    }

    // Add update and reset methods
    public static void updateGame(GameStateCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError(new Exception("Not authenticated. Please login first."));
            return;
        }

        HttpRequest request = new HttpRequestBuilder().newRequest()
            .method("POST")
            .url(BASE_URL + "/update")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.app.log("GameApi", "Sending update request to: " + BASE_URL + "/update");

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                Gdx.app.log("GameApi", "Update response status code: " + statusCode);

                if (statusCode == 403) {
                    callback.onError(new Exception("Authentication failed. Please login again."));
                    return;
                }

                if (statusCode != 200) {
                    String error = "Update failed with status code: " + statusCode;
                    Gdx.app.error("GameApi", error);
                    callback.onError(new Exception(error));
                    return;
                }

                fetchGameState(callback);
            }
            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameApi", "Update request failed: " + t.getMessage());
                t.printStackTrace();
                callback.onError(t);
            }
            @Override
            public void cancelled() {
                Gdx.app.error("GameApi", "Update request cancelled");
                callback.onError(new Exception("Update cancelled"));
            }
        });
    }

    public static void resetGame(GameStateCallback callback) {
        HttpRequest request = new HttpRequestBuilder().newRequest()
            .method("POST")
            .url(BASE_URL + "/reset")
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                fetchGameState(callback);
            }

            @Override
            public void failed(Throwable t) {
                callback.onError(t);
            }

            @Override
            public void cancelled() {
                callback.onError(new Exception("Reset cancelled"));
            }
        });
    }

    public static void login(String username, String password, LoginCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("POST")
            .url(BASE_URL_2 + "/login")
            .header("Content-Type", "application/json")
            .build();
        // JSON body
        String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        request.setContent(body);

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString().trim();
                System.out.println("Raw backend response: " + response);

                int status = httpResponse.getStatus().getStatusCode();
                System.out.println("Status code: " + status);
                System.out.println("Raw response: " + response);

                if (status == 200) {
                    // JWT tokens start with 'ey...'
                    callback.onSuccess(response);
                } else {
                    callback.onError("Login failed: " + response);
                }
            }

            @Override
            public void failed(Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
            @Override
            public void cancelled() {
                callback.onError("Request was cancelled");
            }
        });
    }

    public static void register(String username, String password, String email, LoginCallback callback){
        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("POST")
            .url(BASE_URL_2 + "/register")
            .header("Content-Type", "application/json")
            .build();

        String body = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\"}", username, password, email);
        request.setContent(body);

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                int status = httpResponse.getStatus().getStatusCode();
                System.out.println("Status: " + status);
                System.out.println("Response: '" + response + "'");

                if (status == 200) {
                    callback.onSuccess("Registered successfully!");
                } else {
                    callback.onError(response);
                }
            }
            @Override
            public void failed(Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
            @Override
            public void cancelled() {
                callback.onError("Request cancelled");
            }
        });
    }

    public static void sendEmail(String email, SendEmailCallback callback){
        String url = "http://localhost:8080/forgot-password?email=" + email;
        System.out.println("Sending request to: " + url);

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("POST")
            .url(url)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();
                System.out.println("STATUS CODE: " + status);
                System.out.println("STATUS MESSAGE: " + httpResponse.getStatus().toString());
                System.out.println("RESPONSE: " + response);
                System.out.println("HEADERS: " + httpResponse.getHeaders());

                if(status == -1){
                    callback.onSuccess(response);
                } else {
                    callback.onError(response);
                }
            }

            @Override
            public void failed(Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                callback.onError("Request is cancelled.");
            }
        });
    }

    public static void resetPassword(String token, String newPass, SendEmailCallback callback){
        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("POST")
            .url("http://localhost:8080/reset-password")
            .header("Content-Type", "application/json")
            .build();

        String body = String.format("{\"token\":\"%s\", \"newPassword\":\"%s\"}", token, newPass);
        request.setContent(body);

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();
                System.out.println("STATUS CODE: " + status);
                System.out.println("RESPONSE: " + response);

                if (status == 200){
                    callback.onSuccess(response);
                } else {
                    callback.onError(response);
                }
            }

            @Override
            public void failed(Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                callback.onError("Request is cancelled.");
            }
        });
    }
//    public static void leaderboard(LeaderboardCallback callback){
//        HttpRequestBuilder builder = new HttpRequestBuilder();
//        HttpRequest request = builder.newRequest()
//            .method("GET")
//            .url(BASE_URL_3 + "/all")
//            .build();
//
//        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
//            @Override
//            public void handleHttpResponse(HttpResponse httpResponse) {
//                String json = httpResponse.getResultAsString();
//                Json jsonParser = new Json();
//                try {
//                    Array<ScoreDTO> scores = jsonParser.fromJson(Array.class, ScoreDTO.class, json);
//                    callback.onSuccess(scores);
//                } catch (Exception e) {
//                    callback.onError("Failed to parse leaderboard.");
//                }
//            }
//
//            @Override
//            public void failed(Throwable t) {
//                callback.onError("Leaderboard request failed: " + t.getMessage());
//            }
//
//            @Override
//            public void cancelled() {
//                callback.onError("Leaderboard request was cancelled.");
//            }
//        });
//    }
}
