package com.abara.service;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.repository.CustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerServiceImpl service;

    @Test
    public void save() {
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer customer = new Customer("name", "surname", testImage);

        service.save(customer);

        verify(repository, times(1)).save(customer);
    }

    @Test
    public void listAllCustomer() {
        Object[] customer1 = {BigInteger.valueOf(11), "Test Name 1"};
        Object[] customer2 = {BigInteger.valueOf(22), "Test Name 2"};

        given(repository.listAllCustomer()).willReturn(Stream.of(customer1, customer2).collect(Collectors.toList()));

        Map<Long, String> customerMap = service.listAllCustomer();
        assertEquals(2, customerMap.size());
        assertEquals("Test Name 1", customerMap.get(11L));
        assertEquals("Test Name 2", customerMap.get(22L));
    }

    @Test
    public void findById() {
        Long customerId = 55L;
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer customer = new Customer("name", "surname", testImage);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        Optional<Customer> result = service.findById(customerId);
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
    }

    @Test
    public void delete() {
        Long customerId = 55L;

        service.delete(customerId);

        verify(repository, times(1)).deleteById(customerId);
    }
}