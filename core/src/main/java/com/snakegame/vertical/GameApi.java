package com.snakegame.vertical;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.Preferences;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GameApi {

    private static final String BASE_URL = "http://localhost:8080/api/game";
    private static final String BASE_URL_2 = "http://localhost:8080/api/auth";
    private static final String BASE_URL_3 = "http://localhost:8080/api/scores";
    private static final String BASE_URL_4 = "http://localhost:8080/api/matches";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static boolean isLoggedIn;

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

    public interface GameScoreCallback {
        void onSuccess();
        void onError(Throwable t);
    }

    public interface MyScoreFilterCallback {
        void onSuccess(ScoreDTO[] scores);
        void onError(Throwable t);
    }

    public interface UserHighScoreCallback {
        void onSuccess(UserHighScoreDTO[] scores);
        void onError(Throwable error);
    }

    public interface MyScoreCallback {
        void onSuccess(ScoreDTO[] myScores);
        void onError(Throwable error);
    }

    public interface MatchDetailCallback {
        void onSuccess(String success);
        void onError(Throwable t);
    }

    public interface GetMatchCallback {
        void onSuccess(MatchDTO[] matchDetail);
        void onError(Throwable t);
    }

    public interface ScoreFilterCallback {
        void onSuccess(UserHighScoreDTO[] scores);
        void onError(Throwable t);
    }

    private static String getAuthToken() {
        Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
        return prefs.getString(AUTH_TOKEN_KEY, "");
    }

    private static void setAuthToken(String token) {
        Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
        prefs.putString(AUTH_TOKEN_KEY, token);
        isLoggedIn = true;
        prefs.flush();
    }

    public static void clearAuthToken(){
        Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
        prefs.remove(AUTH_TOKEN_KEY);
        isLoggedIn = false;
        prefs.flush();
    }

    public static boolean getLoginFlag() { return isLoggedIn; }

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
                    setAuthToken(response);
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

    public static void getUserHighScores(final UserHighScoreCallback callback){
        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_3 + "/highscores")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("STATUS CODE: " + statusCode);
                System.out.println("JSON RESPONSE: " + json);

                try {
                    UserHighScoreDTO[] scores = new Json().fromJson(UserHighScoreDTO[].class, json);
                    Gdx.app.postRunnable(() -> callback.onSuccess(scores));
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request cancelled")));
            }
        });
    }

    public static void getUserScores(final MyScoreCallback callback){
        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_3 + "/my")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                try {
                    String json = httpResponse.getResultAsString();
                    int statusCode = httpResponse.getStatus().getStatusCode();
                    System.out.println("STATUS CODE: " + statusCode);
                    System.out.println("JSON RESPONSE: " + json);
                    ScoreDTO[] myScores = new Json().fromJson(ScoreDTO[].class, json);
                    Gdx.app.postRunnable(() -> callback.onSuccess(myScores));
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request cancelled")));
            }
        });

    }

    public static void setBorderlessMode(boolean borderless) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            Gdx.app.error("GameApi", "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        HttpRequest request = requestBuilder.newRequest()
            .method("POST")
            .url(BASE_URL + "/borderless")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .content(String.valueOf(borderless))
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                Gdx.app.log("GameApi", "Borderless mode set to: " + borderless);
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameApi", "Failed to set borderless mode: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("GameApi", "Borderless mode request cancelled");
            }
        });
    }

    public static void getAllScoresByTime (String filterType, ScoreFilterCallback callback) {
        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_3 + "/filter?type=" + filterType)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("STATUS CODE: " + statusCode);
                System.out.println("JSON RESPONSE: " + json);

                try {
                    UserHighScoreDTO[] scores = new Json().fromJson(UserHighScoreDTO[].class, json);
                    Gdx.app.postRunnable(() -> callback.onSuccess(scores));
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request cancelled")));
            }
        });
    }

    public static void saveUserScore(String username, int score, GameScoreCallback callback) {
        System.out.println("##### saveUserScore() CALLED");

        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("POST")
            .url(BASE_URL_3)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        String json = "{ \"username\": \"" + username + "\", \"score\": " + score + " }";
        request.setContent(json);

        System.out.println("USERNAME: "+ username);
        System.out.println("SCORE: "+ score);

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("STATUS CODE (save score): " + statusCode);
                System.out.println("JSON RESPONSE (save score): " + json);

                if(statusCode == 200){
                    Gdx.app.postRunnable(() -> callback.onSuccess());
                } else {
                    Gdx.app.postRunnable(() ->
                        callback.onError(new Exception("Submit failed: " + statusCode)));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request Cancelled!")));
            }
        });
    }

    public static void getMyScoresByWeek (String username, MyScoreFilterCallback callback) {
        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_3 + "/user/weekly?username=" + username)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("STATUS CODE: " + statusCode);
                System.out.println("JSON RESPONSE: " + json);

                try {
                    ScoreDTO[] scores = new Json().fromJson(ScoreDTO[].class, json);
                    Gdx.app.postRunnable(() -> callback.onSuccess(scores));
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request cancelled")));
            }
        });
    }

    public static void getMyScoresByMonth (String username, MyScoreFilterCallback callback) {
        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_3 + "/user/monthly?username=" + username)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("STATUS CODE: " + statusCode);
                System.out.println("JSON RESPONSE: " + json);

                try {
                    ScoreDTO[] scores = new Json().fromJson(ScoreDTO[].class, json);
                    Gdx.app.postRunnable(() -> callback.onSuccess(scores));
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request cancelled")));
            }
        });
    }

    public static void saveGameMatch(MatchDTO matchDetail, MatchDetailCallback callback){
        System.out.println("###### saveGameMatch() CALLED");

        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("POST")
            .url(BASE_URL_4)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json); // make sure it outputs proper JSON
        String jsonString = json.toJson(matchDetail);
        System.out.println("JSON STRING: " + jsonString);
        request.setContent(jsonString);

        System.out.println("MATCH DETAILS: " + jsonString);

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();
                System.out.println("STATUS CODE (save match): " + statusCode);
                System.out.println("JSON RESPONSE (save match): " + json);

                if(statusCode == 200){
                    Gdx.app.postRunnable(() -> callback.onSuccess(json));
                } else {
                    Gdx.app.postRunnable(() ->
                        callback.onError(new Exception("Submit failed (save match): " + statusCode)));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request Cancelled!")));
            }
        });
    }

    public static void getGameMatch(GetMatchCallback callback){
        String token = getAuthToken();
        System.out.println("TOKEN: " + token);
        if (token.isEmpty()) {
            System.out.println("GameApi" + "Not authenticated. Please login first.");
            return;
        }

        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_4 + "/me")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                try {
                    String json = httpResponse.getResultAsString();
                    int statusCode = httpResponse.getStatus().getStatusCode();
                    System.out.println("STATUS CODE: " + statusCode);
                    System.out.println("JSON RESPONSE: " + json);
                    MatchDTO[] matchDetails = new Json().fromJson(MatchDTO[].class, json);
                    Gdx.app.postRunnable(() -> callback.onSuccess(matchDetails));
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError(e));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request cancelled")));
            }
        });
    }
}
