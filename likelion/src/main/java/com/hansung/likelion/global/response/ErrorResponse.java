package com.hansung.likelion.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hansung.likelion.global.response.code.BaseResponseCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
// 필드들이 순서대로 답변이 제공될 수 있도록, 순서를 지정해주는 어노테이션,
@JsonPropertyOrder({"isSuccess", "timestamp", "code", "httpStatus", "message", "data"})
public class ErrorResponse<T> extends BaseResponse {

    private final int httpStatus;
    private final T data; // T는 제네릭 타입, 어떤 타입이 올지 모르니까 제네릭으로 선언

    @Builder
    public ErrorResponse(T data, BaseResponseCode baseResponseCode) {
        super(false, baseResponseCode.getCode(), baseResponseCode.getMessage());
        this.httpStatus = baseResponseCode.getHttpStatus();
        this.data = data;
    }

    @Builder
    public ErrorResponse(T data, BaseResponseCode baseResponseCode, String message) { // 메세지 커스텀 가능
        super(false, baseResponseCode.getCode(), message);
        this.httpStatus = baseResponseCode.getHttpStatus();
        this.data = data;
    }

    // ---------- 정적 팩토리 메서드 ----------
    public static ErrorResponse<?> from(BaseResponseCode baseResponseCode) { // data X, baseResponseCode
        // <?>는 와일드카드(Wildcard) : "어떤 타입이든 상관없다" 또는 "타입을 지정할 필요가 없다" 라는 의미
        // 데이터가 없으니 데이터의 타입(T)을 굳이 정할 필요가 없습니다.
        return new ErrorResponse<>(null, baseResponseCode);
    }

    public static ErrorResponse<?> of(BaseResponseCode baseResponseCode, String message) {// data X, message 커스텀
        return new ErrorResponse<>(null, baseResponseCode, message);
    }

    public static <T> ErrorResponse<T> of(BaseResponseCode baseResponseCode, T data) { // data O, baseResponseCode
        return new ErrorResponse<>(data, baseResponseCode);
    }

    public static <T> ErrorResponse<T> of(BaseResponseCode baseResponseCode, T data, String message) { // data O, message 커스텀
        return  new ErrorResponse<>(data, baseResponseCode, message);
    }
}
