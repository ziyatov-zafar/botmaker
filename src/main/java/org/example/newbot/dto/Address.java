package org.example.newbot.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Address {
    private String road;
    private String house_number;
    private String city;
    private String county;
    private String neighbourhood;
    private String country;
    @SerializedName("country_code")
    private String code;
}
