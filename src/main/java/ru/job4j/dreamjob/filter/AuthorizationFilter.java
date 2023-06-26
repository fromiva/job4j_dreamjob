package ru.job4j.dreamjob.filter;

import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        String url = request.getRequestURI();
        if (isAlwaysPermitted(url)) {
            chain.doFilter(request, response);
            return;
        }
        boolean userLoggedIn = request.getSession().getAttribute("user") != null;
        if (!userLoggedIn) {
            String loginPageUrl = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isAlwaysPermitted(String url) {
        return url.startsWith("/users/register") || url.startsWith("/users/login");
    }
}
