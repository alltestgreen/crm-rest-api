package com.abara;

import com.abara.entity.Customer;
import com.abara.entity.Role;
import com.abara.entity.User;
import com.abara.repository.CustomerRepository;
import com.abara.repository.UserRepository;
import com.abara.service.CustomerService;
import com.abara.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(value = "create.default.application.data")
public class DataInitializer implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public void run(ApplicationArguments args) {
        LOG.info("Populating database with default users.");

        createCustomerIfNotExist(new Customer("jSmith", "John", "Smith", "john.smith@company.com", null));
        createCustomerIfNotExist(new Customer("gclay", "Grace", "Clayson", "grace.clayson@company.com", null));
        createCustomerIfNotExist(new Customer("timthom", "Timothy", "Thompson", "timothy.tomphson@company.com", null));

        createUserIfNotExist(new User("admin", "admin", Stream.of(new Role("USER"), new Role("ADMIN")).collect(Collectors.toSet())));
        createUserIfNotExist(new User("user", "user", new Role("USER")));
    }

    private void createCustomerIfNotExist(Customer customer) {
        if (customerRepository.findByUsername(customer.getUsername()) != null) {
            LOG.debug("Customer already exists with username: " + customer.getUsername());
            return;
        }
        customerService.create(customer, "admin");
    }

    private void createUserIfNotExist(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            LOG.debug("User already exists with username: " + user.getUsername());
            return;
        }
        userService.create(user);
    }

}
