package ict.board.config.security;

import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import ict.board.domain.member.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.remember-me.key}")
    private String rememberMeKey;

    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RememberMeServices rememberMeServices
    )
            throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login","/logout", "/css/**", "/*.ico", "/members/new",
                                "/members/email-verification", "/members/register",
                                "/members/sendVerificationCode", "/staff-join/7345", "/access-denied"
                        ,"/favicon.ico", "/resources/**", "/error").permitAll()
                        .requestMatchers("/access/**").hasRole("ADMIN")
                        .requestMatchers("/manage/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/staff-only/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .userDetailsService(userDetailsService)
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(604800)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .deleteCookies("remember-me")
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    log.error("Access Denied: {}", accessDeniedException.getMessage());
                                    log.error("Request URL: {}", request.getRequestURL());
                                    log.error("User: {}", request.getUserPrincipal());
                                    accessDeniedHandler().handle(request, response, accessDeniedException);
                                })
                                .authenticationEntryPoint(authenticationEntryPoint())
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/session-expired")
                        .sessionRegistry(sessionRegistry())
                );

        return http.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices rememberMeServices =
                new PersistentTokenBasedRememberMeServices(
                        rememberMeKey,
                        userDetailsService,
                        persistentTokenRepository()
                );
        rememberMeServices.setTokenValiditySeconds(604800);
        return rememberMeServices;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member instanceof IctStaffMember) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ((IctStaffMember) member).getRole().name()));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + Role.NONE.name()));
        }
        log.info("Authorities for user {}: {}", member.getEmail(), authorities);
        return authorities;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}