package com.microservicio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define la cadena de filtros de seguridad.
     * @param http Objeto HttpSecurity para configurar reglas.
     * @return La cadena de filtros configurada.
     * @throws Exception Si ocurre un error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Configuración de autorización de solicitudes
                .authorizeHttpRequests(authorize -> authorize
                        // Permite el acceso sin autenticación al registro de usuarios
                        .requestMatchers("/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/users/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Permite el acceso sin autenticación a la documentación de Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        // Todas las demás solicitudes requieren autenticación
                        .anyRequest().authenticated()
                )

                // Habilita la autenticación HTTP Basic
                .httpBasic(withDefaults());

        return http.build();
    }
}