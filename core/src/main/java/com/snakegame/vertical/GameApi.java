package com.snakegame.vertical;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.function.Consumer;

public class GameApi {

    private static final String BASE_URL = "http://localhost:8080/api/game";
    private static final String BASE_URL_2 = "http://localhost:8080/api/auth";
    private static final String BASE_URL_3 = "http://localhost:8080/api/scores";

    public interface GameStateCallback {
        void onSuccess(GameStateDTO gameState);
        void onError(Throwable t);
    }

    public interface LoginCallback {
        void onSuccess(String token);
        void onError(String error);
    }

    public interface LeaderboardCallback {
        void onSuccess(Array<ScoreDTO> scores);
        void onError(String error);
    }

    public static void fetchGameState(GameStateCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        HttpRequest request = requestBuilder.newRequest()
            .method("GET")
            .url(BASE_URL + "/state")
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                try {
                    String json = httpResponse.getResultAsString();
                    Json jsonParser = new Json();
                    GameStateDTO gameState = jsonParser.fromJson(GameStateDTO.class, json);
                    callback.onSuccess(gameState);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void failed(Throwable t) {
                callback.onError(t);
            }

            @Override
            public void cancelled() {
                callback.onError(new Exception("Request cancelled"));
            }
        });
    }

    public static void sendDirection(Direction direction) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        String directionJson = "\"" + direction.name() + "\"";

        HttpRequest request = requestBuilder.newRequest()
            .method("POST")
            .url(BASE_URL + "/direction")
            .header("Content-Type", "application/json")
            .content(directionJson) // Send as JSON
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                // Success
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("GameApi", "Failed to send direction: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("GameApi", "Direction request cancelled");
            }
        });
    }

    // Add update and reset methods
    public static void updateGame(GameStateCallback callback) {
        HttpRequest request = new HttpRequestBuilder().newRequest()
            .method("POST")
            .url(BASE_URL + "/update")
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

    public static void leaderboard(LeaderboardCallback callback){
        HttpRequestBuilder builder = new HttpRequestBuilder();
        HttpRequest request = builder.newRequest()
            .method("GET")
            .url(BASE_URL_3 + "/all")
            .build();

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String json = httpResponse.getResultAsString();
                Json jsonParser = new Json();
                try {
                    Array<ScoreDTO> scores = jsonParser.fromJson(Array.class, ScoreDTO.class, json);
                    callback.onSuccess(scores);
                } catch (Exception e) {
                    callback.onError("Failed to parse leaderboard.");
                }
            }

            @Override
            public void failed(Throwable t) {
                callback.onError("Leaderboard request failed: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                callback.onError("Leaderboard request was cancelled.");
            }
        });
    }
}
