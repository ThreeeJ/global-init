package com.hansung.likelion.global.response.code;

/**
 * 공통 응답 코드 인터페이스(틀)
 */
public interface BaseResponseCode {
    String getCode();
    int getHttpStatus();
    String getMessage();
}
