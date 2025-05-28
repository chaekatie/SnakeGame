package com.snakegame.vertical;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;

public class GameApi {

    private static final String BASE_URL = "http://localhost:8080/api/game";

    public interface GameStateCallback {
        void onSuccess(GameStateDTO gameState);
        void onError(Throwable t);
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
}
