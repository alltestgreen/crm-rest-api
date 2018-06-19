package com.abara;

import com.abara.model.Customer;
import com.abara.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerService customerService) {
        return args -> {
            customerService.save(new Customer("John", "Smith", null));
            customerService.save(new Customer("Grace", "Clarkson", null));
            customerService.save(new Customer("Timothy", "Thompson", null));
        };
    }
}
