package ict.board.config;

import ict.board.config.argumentresolver.LoginMemberArgumentResolver;
import ict.board.interceptor.LoginCheckInterceptor;
import ict.board.interceptor.RoleCheckInterceptor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RoleCheckInterceptor roleCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/css/**", "/*.ico", "/members/new", "/logout",
                        "/members/email-verification", "/members/register","/newMan","/members/sendVerificationCode");

        // Adding the role check interceptor
        registry.addInterceptor(roleCheckInterceptor)
                .order(2)
                .addPathPatterns("/admin/**", "/staff-only/**", "/manage/**"); // Define paths that need role checks
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }
}