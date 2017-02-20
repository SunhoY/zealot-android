package io.harry.zealot.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UrlShortenerApi {
    @POST("urlshortener/v1/url")
    Call<Map<String, Object>> shortenedUrl(@Body Map<String, String> body, @Query("key") String apiKey);
}
