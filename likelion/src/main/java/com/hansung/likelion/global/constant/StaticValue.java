package com.hansung.likelion.global.constant;

public class StaticValue { // 공통 응답 enum 정의 클래스
    // SUCCESS
    public static final int OK = 200;                       // 요청 성공
    public static final int CREATED = 201;                  // 생성됨

    // ERROR
    public static final int BAD_REQUEST = 400;              // 잘못된 요청
    public static final int UNAUTHORIZED = 401;             // 인증 필요
    public static final int FORBIDDEN = 403;                // 접근 금지
    public static final int NOT_FOUND = 404;                // 리소스 없음
    public static final int METHOD_NOT_ALLOWED = 405;       // 허용되지 않는 메서드
    public static final int CONFLICT = 409;                 // 충돌 발생
    public static final int INTERNAL_SERVER_ERROR = 500;    // 서버 내부 오류
}