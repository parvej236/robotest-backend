package com.robotest.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Forwards all non-API, non-static requests to index.html
 * so that Vue Router can handle client-side navigation.
 */
@Controller
public class SpaFallbackController {

    @RequestMapping(value = {
            "/",
            "/login",
            "/register",
            "/dashboard",
            "/profile",
            "/contests",
            "/contests/**",
            "/leaderboard",
            "/rules",
            "/about",
            "/verify-email",
            "/reset-password",
            "/forgot-password",
            "/oauth2/**",
            "/admin",
            "/admin/**"
    })
    public String forward(HttpServletRequest request) {
        return "forward:/index.html";
    }
}
