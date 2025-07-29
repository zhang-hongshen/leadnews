package com.zhanghongshen.common.exception;


import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ExceptionCatch {

    @ExceptionHandler(Exception.class)
    public ResponseResult<?> exception(Exception e){
        log.error("catch exception:{}",e.getMessage());
        return ResponseResult.error(AppHttpCodeEnum.SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseResult<?> exception(CustomException e){
        log.error("catch CustomException: {}", e.getMessage());
        return ResponseResult.error(e.getAppHttpCodeEnum());
    }
}
