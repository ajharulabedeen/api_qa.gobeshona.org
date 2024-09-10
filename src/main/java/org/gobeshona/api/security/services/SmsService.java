package org.gobeshona.api.security.services;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SmsService {

    private final OkHttpClient client = new OkHttpClient();

    private final String apiUrl = "https://api.bdbulksms.net/api.php"; // BD Bulk SMS API endpoint
    private final String token = "9360215324172598360436e7a8087b549605a7ca8bca97839180"; // Your token from the interface

    public String sendSms(String phoneNumber, String message) {
        // Create request body
        RequestBody requestBody = new FormBody.Builder()
                .add("token", token)
                .add("to", phoneNumber)
                .add("message", message)
                .build();

        // Create request
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string(); // Return the response from the server
            } else {
                return "Failed to send SMS: " + response.message(); // Handle failure
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while sending SMS: " + e.getMessage();
        }
    }
}
