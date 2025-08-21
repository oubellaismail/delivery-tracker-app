package com.delivery_tracker_app.app.dto.v1.common;

public record BaseResponse<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> BaseResponse<T> ok(String message, T data){
        return new BaseResponse<>(true, message, data);
    }

    public static <T> BaseResponse<T> fail(String message){
        return new BaseResponse<>(false, message, null);
    }
}
