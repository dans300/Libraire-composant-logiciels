package com.project.ecommerce.security;

import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequiresRole annotation = handlerMethod.getMethodAnnotation(RequiresRole.class);

            if (annotation != null) {
                String requiredRole = annotation.value().toString();
                try {
                    String token = request.getSession().getAttribute("token").toString();

                    if(!JwtUtils.validateToken(token, requiredRole)){
                        // Redirect the user or handle unauthorized access
                        response.sendRedirect("/login?redirect-to="+request.getRequestURI());
                        return false;
                    }
                }
                catch (Exception e){
                    System.out.println("exception "+e.getMessage());
                    response.sendRedirect("/login?redirect-to="+request.getRequestURI());
                    return false;
                }
            }
        }

        return true;
    }

    // Other methods can be overridden as needed
}