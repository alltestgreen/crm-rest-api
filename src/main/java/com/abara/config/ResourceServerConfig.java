package com.abara.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers()
                .xssProtection().and()
                .frameOptions().disable().and()
                .authorizeRequests()
                .antMatchers("/api/customers/**").hasRole("USER")
                .antMatchers("/api/users/**").hasRole("ADMIN")
                .anyRequest().authenticated();
    }

}
