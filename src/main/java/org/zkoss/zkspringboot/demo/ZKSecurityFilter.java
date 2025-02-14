package org.zkoss.zkspringboot.demo;

import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ZKSecurityFilter extends HttpFilter {
	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String path = request.getRequestURI();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        boolean isAnonymous = (authentication == null 
                || authentication instanceof AnonymousAuthenticationToken 
                || !authentication.isAuthenticated());

        if (path.startsWith("/zkau/web/zul/secure/") && isAnonymous) {
            // 🔥 Stocker la page d'origine en session avant la redirection OAuth2
            request.getSession().setAttribute("redirectUrl", path);
            
            // Rediriger vers Okta pour l'authentification
            response.sendRedirect("/oauth2/authorization/okta");
            return;
        }

		chain.doFilter(request, response);
	}
}
