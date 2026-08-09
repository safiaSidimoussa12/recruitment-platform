package com.jobboard.jobboard.shared.config;

import com.jobboard.jobboard.module.auth.CustomUserDetailsService;
import com.jobboard.jobboard.shared.domain.StatutCompte;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomUserDetailsService userDetailsService;
        private final com.jobboard.jobboard.shared.domain.UtilisateurRepository utilisateurRepository;
        private final com.jobboard.jobboard.module.recruteur.RecruteurRepository recruteurRepository;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .userDetailsService(userDetailsService)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/register/**", "/offres", "/offres/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/recruteur/**").hasRole("RECRUTEUR")
                                                .requestMatchers("/candidat/**", "/candidatures/**", "/messages/**")
                                                .hasAnyRole("CANDIDAT", "RECRUTEUR")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .successHandler((request, response, authentication) -> {
                                                        String role = authentication.getAuthorities().stream()
                                                                        .findFirst()
                                                                        .map(a -> a.getAuthority())
                                                                        .orElse("");

                                                        String email = authentication.getName();

                                                        // Vérifie si recruteur en attente
                                                        if (role.equals("ROLE_RECRUTEUR")) {
                                                                recruteurRepository.findByEmail(email).ifPresent(r -> {
                                                                        try {
                                                                                if (r.getStatut() == StatutCompte.EN_ATTENTE) {
                                                                                        request.getSession()
                                                                                                        .invalidate();
                                                                                        response.sendRedirect(
                                                                                                        "/login?pending");
                                                                                } else {
                                                                                        response.sendRedirect(
                                                                                                        "/recruteur/dashboard");
                                                                                }
                                                                        } catch (Exception e) {
                                                                                throw new RuntimeException(e);
                                                                        }
                                                                });
                                                                return;
                                                        }

                                                        if (role.equals("ROLE_ADMIN")) {
                                                                response.sendRedirect("/admin/dashboard");
                                                        } else {
                                                                response.sendRedirect("/offres");
                                                        }
                                                })
                                                .failureHandler((request, response, exception) -> {
                                                        String msg = exception.getClass().getSimpleName();
                                                        if (msg.contains("Disabled")) {
                                                                response.sendRedirect("/login?pending");
                                                        } else if (msg.contains("Locked")) {
                                                                response.sendRedirect("/login?suspended");
                                                        } else {
                                                                response.sendRedirect("/login?error");
                                                        }
                                                })
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.disable()));
                return http.build();
        }
}