package com.abara.config;

import com.abara.model.Customer;
import com.abara.model.Role;
import com.abara.model.User;
import com.abara.service.CustomerService;
import com.abara.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SpringApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Conditional(DefaultDataCondition.class)
    CommandLineRunner runner(CustomerService customerService, UserService userService) {
        System.out.println("Populate default data...");
        return args -> {
            Customer customer1 = new Customer("John", "Smith", null);
            Customer customer2 = new Customer("Grace", "Clarkson", null);
            Customer customer3 = new Customer("Timothy", "Thompson", null);
            customer1.setCreatedBy("admin");
            customer2.setCreatedBy("admin");
            customer3.setCreatedBy("admin");

            customerService.save(customer1);
            customerService.save(customer2);
            customerService.save(customer3);

            userService.save(new User("admin", "admin", Stream.of(new Role("USER"), new Role("ADMIN")).collect(Collectors.toSet())));
            userService.save(new User("user", "user", Collections.singleton(new Role("USER"))));
        };
    }

}
