package com.example.mobileapprob;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button fetchInfoButton;
    private TextView infoTextView;
    private OkHttpClient httpClient;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchInfoButton = findViewById(R.id.fetchInfoButton);
        infoTextView = findViewById(R.id.infoTextView);
        httpClient = new OkHttpClient();
        gson = new Gson();

        fetchInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchInfo();
            }
        });
    }

    private void fetchInfo() {
        Request request = new Request.Builder()
                .url("http://edns.ip-api.com/json")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoTextView.setText("Error: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final IpInfo ipInfo = gson.fromJson(response.body().charStream(), IpInfo.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayInfo(ipInfo);
                        }
                    });

                    // Check if edns is not null before calling the second API
                    if (ipInfo.getEdns() != null) {
                        // Call the second API using the IP address from the EDNS response
                        Request request2 = new Request.Builder()
                                .url("http://ip-api.com/json/" + ipInfo.getEdns().getIp())
                                .build();

                        httpClient.newCall(request2).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        infoTextView.append("\n\nError: " + e.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    final JsonObject jsonResponse = gson.fromJson(response.body().charStream(), JsonObject.class);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            displayAdditionalInfo(jsonResponse);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            infoTextView.append("\n\nError: " + response.message());
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                infoTextView.append("\n\nEDNS information is not available.");
                            }
                        });
                    }

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            infoTextView.setText("Error: " + response.message());
                        }
                    });
                }
            }
        });
    }

    private void displayInfo(IpInfo ipInfo) {
        String info = "DNS:\nIP: " + ipInfo.getDns().getIp() + "\nGeo: " + ipInfo.getDns().getGeo() +
                "\n\nEDNS:\nIP: " + ipInfo.getEdns().getIp() + "\nGeo: " + ipInfo.getEdns().getGeo();
        infoTextView.setText(info);
    }

    private void displayAdditionalInfo(JsonObject jsonResponse) {
        String additionalInfo = "\n\nAdditional Info:\nCountry: " + jsonResponse.get("country").getAsString() +
                "\nCountry Code: " + jsonResponse.get("countryCode").getAsString() +
                "\nRegion: " + jsonResponse.get("region").getAsString() +
                "\nRegion Name: " + jsonResponse.get("regionName").getAsString() +
                "\nCity: " + jsonResponse.get("city").getAsString() +
                "\nZip: " + jsonResponse.get("zip").getAsString() +
                "\nLatitude: " + jsonResponse.get("lat").getAsString() +
                "\nLongitude: " + jsonResponse.get("lon").getAsString() +
                "\nTimezone: " + jsonResponse.get("timezone").getAsString() +
                "\nISP: " + jsonResponse.get("isp").getAsString() +
                "\nOrganization: " + jsonResponse.get("org").getAsString() +
                "\nAS: " + jsonResponse.get("as").getAsString();
        infoTextView.append(additionalInfo);
    }
}