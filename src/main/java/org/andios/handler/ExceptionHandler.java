package org.andios.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by version on 17-3-24.
 * 统一异常处理
 */
@Component
public class ExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
             HttpServletResponse response, Object handler, Exception ex) {
        if(ex instanceof NullPointerException){
            System.out.println("null");
        }
        return null;
    }
}
