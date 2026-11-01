package com.campusconnect;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.*;
@Configuration
public class SecurityConfig {
 @Bean UserDetailsService users(@Value("${ADMIN_USERNAME}") String username,@Value("${ADMIN_PASSWORD}") String password) {
  if(username.isBlank() || password.length()<12) throw new IllegalArgumentException("Admin configuration is required; password must contain at least 12 characters");
  var encoder=PasswordEncoderFactories.createDelegatingPasswordEncoder();
  return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
 }
 @Bean CorsConfigurationSource corsConfigurationSource(@Value("${FRONTEND_ORIGIN:http://localhost:3000}") String origin) {
  var uri=java.net.URI.create(origin);
  if(!List.of("http","https").contains(uri.getScheme()) || uri.getHost()==null || origin.contains("*") || (uri.getPath()!=null && !uri.getPath().isEmpty()) || uri.getQuery()!=null || uri.getFragment()!=null || uri.getUserInfo()!=null) throw new IllegalArgumentException("FRONTEND_ORIGIN must be one exact origin");
  var config=new CorsConfiguration(); config.setAllowedOrigins(List.of(origin));
  config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
  config.setAllowedHeaders(List.of("Authorization","Content-Type")); config.setAllowCredentials(false);
  var source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/api/**",config); return source;
 }
 @Bean SecurityFilterChain security(HttpSecurity http,@Value("${REQUIRE_HTTPS:false}") boolean requireHttps) throws Exception {
  http.cors(Customizer.withDefaults()).csrf(c->c.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.GET,"/api/labs","/api/labs/**","/api/software","/api/equipment","/api/search").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT,"/api/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PATCH,"/api/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE,"/api/**").hasRole("ADMIN").anyRequest().denyAll())
   .httpBasic(b->b.authenticationEntryPoint((req,res,e)->{res.setHeader("WWW-Authenticate","Basic realm=\"Campus Connect\""); ApiErrors.write(res,401,"Invalid credentials");}))
   .exceptionHandling(e->e.accessDeniedHandler((req,res,ex)->ApiErrors.write(res,403,"Access denied")))
   .headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
    .referrerPolicy(r->r.policy(ReferrerPolicy.NO_REFERRER)));
  if(requireHttps) http.requiresChannel(c->c.anyRequest().requiresSecure());
  return http.build();
 }
}
