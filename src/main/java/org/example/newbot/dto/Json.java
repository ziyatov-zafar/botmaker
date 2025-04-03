package org.example.newbot.dto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Getter
@Setter
@Log4j2
@ToString
public class Json {
    @SerializedName("display_name")
    private String address;
    @SerializedName("display_name")
    private Address country;
    public String jsonUrl(double lat, double lon) {
        return "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon + "&zoom=155&addressdetails=1";
    }
    public Json setAddress(double lat, double lon) {
        Gson gson = new Gson();
        URL url;
        URLConnection connection;
        BufferedReader reader = null;
        try {
            url = new URL("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon + "&zoom=155&addressdetails=1");
            String ur = "%s";
            connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json = "", line;
            while ((line = reader.readLine()) != null)
                json = json.concat(line);
            return gson.fromJson(json, Json.class);
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Error closing reader", e);
                }
            }
        }
        return null;
    }
}
