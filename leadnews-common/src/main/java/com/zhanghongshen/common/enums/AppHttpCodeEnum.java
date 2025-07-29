package com.zhanghongshen.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppHttpCodeEnum {

    //
    SUCCESS(200,"Success"),
    // Login  1 - 50
    NEED_LOGIN(1,"Operation need login."),
    LOGIN_PASSWORD_ERROR(2,"Wrong password"),
    // TOKEN 50~100
    TOKEN_INVALID(50,"Invalid token"),
    TOKEN_EXPIRE(51,"Expired token"),
    TOKEN_REQUIRE(52,"Token is required"),
    // SIGN验签 100~120
    SIGNATURE_INVALID(100,"Invalid signature"),
    SIGNATURE_TIMEOUT(101,"Expired signature"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"Parameter is required."),
    PARAM_INVALID(501,"Invalid parameter"),
    PARAM_IMAGE_FORMAT_ERROR(502,"The image's format is wrong."),
    SERVER_ERROR(503,"Server Error"),
    // App Data error 1000~2000
    DATA_EXIST(1000,"Data Exist"),
    AP_USER_DATA_NOT_EXIST(1001,"ApUser Data Not Exist"),
    DATA_NOT_EXIST(1002,"Data not exist"),
    // Admin Data error 3000~3500
    NO_OPERATOR_AUTH(3000,"No operation auth"),
    NEED_ADMIND(3001,"Admin permission is required"),

    // 自媒体文章错误 3501~3600
    MATERIASL_REFERENCE_FAIL(3501, "Material reference fail");

    private final int code;
    private final String errorMessage;

}