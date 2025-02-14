package org.zkoss.zkspringboot.demo;

import static org.springframework.security.config.Customizer.withDefaults;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	@Value("${okta.oauth2.issuer}")
	private String issuer;
	@Value("${okta.oauth2.client-id}")
	private String clientId;
	public static final String ZUL_FILES = "/zkau/web/zul/**";
	public static final String[] ZK_RESOURCES = { "/zkau/web/*/js/**", "/zkau/web/*/zul/css/**", "/zkau/web/*/img/**" };
	// allow desktop cleanup after logout or when reloading login page
	public static final String REMOVE_DESKTOP_REGEX = "/zkau\\?dtid=.*&cmd_0=rmDesktop&.*";
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .cors(AbstractHttpConfigurer::disable)
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(auth -> auth
//	            // 🔒 Sécuriser toutes les autres pages `.zul`
//	            .requestMatchers("/zkau/web/zul/secure/**").authenticated()	        		
//	            // 🔓 Autoriser l'accès aux ressources statiques de ZK
//	            .requestMatchers("/zkau/web/*/js/**", "/zkau/web/*/zul/css/**", "/zkau/web/*/img/**").permitAll()
//	            // Ignore les requêtes spécifiques à rmDesktop
//	            .requestMatchers(new RegexRequestMatcher("/zkau\\?dtid=.*&cmd_0=rmDesktop&.*", "GET")).permitAll()	            
//	            // 🔓 Autoriser l'accès aux `.zul` publics
//	            .requestMatchers("/zkau/web/zul/public/**").permitAll()
//	            .requestMatchers("/zkau/web/zul/*.zul").permitAll()
	            // 🔒 Sécuriser les autres ressources privées
	            .requestMatchers("/secure/**").authenticated()
	            .anyRequest().permitAll()
	        )
	        .oauth2Login(withDefaults())
	        .logout(logout -> logout.addLogoutHandler(logoutHandler()));

	    return http.build();
	}

    private LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            try {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                response.sendRedirect(issuer + "v2/logout?client_id=" + clientId + "&returnTo=" + baseUrl+"/index");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
