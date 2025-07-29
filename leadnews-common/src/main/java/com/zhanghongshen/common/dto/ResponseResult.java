package com.zhanghongshen.common.dto;

import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通用的结果返回类
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {

    private String host;

    private Integer code;

    private String errorMessage;

    private T data;

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.errorMessage = msg;
        this.data = data;
    }

    public static<T> ResponseResult<T> error(Integer code, String msg) {
        return new ResponseResult<>(code, msg);
    }

    public static<T> ResponseResult<T> error(AppHttpCodeEnum enums) {
        return new ResponseResult<>(enums.getCode(), enums.getErrorMessage());
    }

    public static<T> ResponseResult<T> error(AppHttpCodeEnum enums, String msg) {
        return new ResponseResult<>(enums.getCode(), msg);
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(AppHttpCodeEnum.SUCCESS.getCode(), null);
    }

    public static<T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(AppHttpCodeEnum.SUCCESS.getCode(), data);
    }

    public static<T> ResponseResult<T> success(Integer code, T data) {
        return new ResponseResult<>(code, data);
    }


    public boolean isSuccess() {
        return code != null && code.equals(AppHttpCodeEnum.SUCCESS.getCode());
    }
}
