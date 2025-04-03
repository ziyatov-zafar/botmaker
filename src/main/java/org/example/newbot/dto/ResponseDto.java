package org.example.newbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    public ResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
