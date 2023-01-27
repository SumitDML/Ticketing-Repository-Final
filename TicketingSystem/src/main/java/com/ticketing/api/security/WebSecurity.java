package com.ticketing.api.security;

import com.ticketing.api.exception.RestAccessDeniedHandler;
import com.ticketing.api.exception.RestAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private transient final Environment environment;

    @Autowired
    public WebSecurity(final Environment environment) {
        super();
        this.environment = environment;
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors();
       http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/resetPassword").permitAll()
                .antMatchers( "/projects/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/sendOtp").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/tickets/createTicket").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/tickets/assignMembers").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/tickets/getTicket").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/tickets/listTickets").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/tickets/deleteTicket").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/tickets/updateTicket").permitAll()

                .anyRequest().authenticated().and()
                .addFilter(new AuthFilter(authenticationManager(), environment));
        http.headers().frameOptions().disable();
    }

    private RestAccessDeniedHandler accessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    private RestAuthEntryPoint authenticationEntryPoint() {
        return new RestAuthEntryPoint();
    }
}