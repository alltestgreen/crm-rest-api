package com.abara.config;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.entity.Role;
import com.abara.entity.User;
import com.abara.service.CustomerService;
import com.abara.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SpringApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SpringApplicationConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Conditional(DefaultDataCondition.class)
    CommandLineRunner runner(CustomerService customerService, UserService userService) {
        LOG.info("Populating in-memory database with default data.");
        return args -> {

            byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/resources/images/red-dot.png"));
            CustomerImage customerImage1 = new CustomerImage("red-dot.png", "image/png", fileBytes);
            CustomerImage customerImage2 = new CustomerImage("user.png", "image/png", fileBytes);

            Customer customer1 = new Customer("John", "Smith", customerImage1);
            Customer customer2 = new Customer("Grace", "Clayson", customerImage2);
            Customer customer3 = new Customer("Timothy", "Thompson", null);

            customer1.setCreatedBy("admin");
            customer2.setCreatedBy("admin");
            customer3.setCreatedBy("admin");

            customerService.save(customer1);
            customerService.save(customer2);
            customerService.save(customer3);

            userService.create(new User("admin", "admin", Stream.of(new Role("USER"), new Role("ADMIN")).collect(Collectors.toSet())));
            userService.create(new User("user", "user", new Role("USER")));
        };
    }

}
