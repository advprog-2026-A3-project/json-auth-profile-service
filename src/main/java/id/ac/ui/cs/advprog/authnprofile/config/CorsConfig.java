package id.ac.ui.cs.advprog.authnprofile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration untuk CORS (Cross-Origin Resource Sharing)
 * Memungkinkan frontend dari domain lain mengakses backend API
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Izinkan request dari frontend development
                .allowedOrigins(
                        "http://localhost:3000",      // Local development
                        "http://localhost:8080",      // Alternative port
                        "http://127.0.0.1:3000"       // Localhost IP
                )
                // Izinkan HTTP methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Izinkan headers
                .allowedHeaders("*")
                // Izinkan credentials (cookies, authorization headers)
                .allowCredentials(true)
                // Cache preflight response selama 1 jam
                .maxAge(3600);

        // Untuk production, ganti dengan domain actual
        // registry.addMapping("/api/**")
        //        .allowedOrigins("https://yourdomain.com")
        //        .allowedMethods("GET", "POST", "PUT", "DELETE")
        //        .allowedHeaders("*")
        //        .allowCredentials(true)
        //        .maxAge(3600);
    }
}

