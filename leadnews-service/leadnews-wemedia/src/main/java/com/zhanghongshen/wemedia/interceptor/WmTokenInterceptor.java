package com.zhanghongshen.wemedia.interceptor;

import com.zhanghongshen.model.wemedia.pojo.WmUser;
import com.zhanghongshen.wemedia.context.UserContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class WmTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if(!StringUtils.isEmpty(userId)){
            WmUser user = new WmUser();
            user.setId(Long.valueOf(userId));
            UserContextHolder.setUserId(Long.valueOf(userId));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContextHolder.clear();
    }
}
