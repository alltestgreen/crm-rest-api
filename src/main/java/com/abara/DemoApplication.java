package com.abara;

import com.abara.model.Customer;
import com.abara.model.Role;
import com.abara.model.User;
import com.abara.service.CustomerService;
import com.abara.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerService customerService, UserService userService) {
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
