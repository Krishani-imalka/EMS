package com.Java.EMS.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // Always allow public resources
        if (uri.equals("/login") || uri.equals("/")
                || uri.equals("/")
                || uri.startsWith("/forgot-password")
                || uri.startsWith("/CSS")
                || uri.startsWith("/JS")
                || uri.startsWith("/images")
                || uri.startsWith("/webjars")
                || uri.startsWith("/favicon")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInRole") == null) {
            response.sendRedirect("/login");
            return false;
        }

        String role = (String) session.getAttribute("loggedInRole");

        if (uri.startsWith("/admin")     && !role.equals("ADMIN"))     { response.sendRedirect("/login"); return false; }
        if (uri.startsWith("/organizer") && !role.equals("ORGANIZER")) { response.sendRedirect("/login"); return false; }
        if (uri.startsWith("/student")   && !role.equals("STUDENT"))   { response.sendRedirect("/login"); return false; }

        return true;
    }
}