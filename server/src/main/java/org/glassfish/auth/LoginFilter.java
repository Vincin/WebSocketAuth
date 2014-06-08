package org.glassfish.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Siva on 8/06/2014.
 */
public class LoginFilter implements javax.servlet.Filter {
    public void destroy() {
    }

    public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp, javax.servlet.FilterChain chain) throws javax.servlet.ServletException, IOException {
        System.out.println("LoginFilter... checking user auth......");
        HttpServletRequest request =
                (HttpServletRequest) req;
        HttpServletResponse response =
                (HttpServletResponse) resp;
        String browserReq = request.getParameter("browser");
        if(browserReq != null)
        {
            System.out.println("LoginFilter... user is from Browser, setting user auth cookie........");
            Cookie loginCookie = new Cookie("user","TestUser");
            //setting cookie to expiry in 30 mins
            loginCookie.setMaxAge(30*60*6);
            response.addCookie(loginCookie);
            chain.doFilter(req, resp);
            return;
        }
        String friendAllow = request.getParameter("friend");
        if(friendAllow != null)
        {
            System.out.println("LoginFilter... user is Friend, Allowing........");
            chain.doFilter(req, resp);
            return;
        }
        Cookie loginCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("Cookie: " + cookie.getName() + "=" + cookie.getValue());
                if (cookie.getName().equals("user")) {
                    loginCookie = cookie;
                    System.out.println("User cookie found...");
                    break;
                }
            }
        }
        if (loginCookie != null) {
            System.out.println("User is authorized.... processing request");
            chain.doFilter(req, resp);
        } else {
            System.out.println("User is Unauthorized.... Sending 401 status");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    public void init(javax.servlet.FilterConfig config) throws javax.servlet.ServletException {
        System.out.println("Initializing LoginFilter.........");
    }

}
