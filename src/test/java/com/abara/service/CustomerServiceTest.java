package com.abara.service;

import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
import com.abara.repository.CustomerRepository;
import com.abara.validation.EntityValidator;
import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private EntityValidator entityValidator;

    @InjectMocks
    private CustomerServiceImpl service;

    @Test
    public void create() {
        Long testId = 2L;
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer customer = new Customer("name", "surname", testImage);

        given(entityValidator.validate(customer)).willReturn(Optional.empty());
        Customer mock = mock(Customer.class);
        given(mock.getId()).willReturn(testId);
        given(repository.save(customer)).willReturn(mock);

        Long createdId = service.create(customer, "admin");

        verify(entityValidator, times(1)).validate(customer);
        verify(repository, times(1)).save(customer);

        assertEquals(testId, createdId);
    }

    @Test
    public void list() {
        Customer customer1 = new Customer("name1", "surname1", null);
        Customer customer2 = new Customer("name2", "surname2", null);

        given(repository.findAll()).willReturn(Stream.of(customer1, customer2).collect(Collectors.toList()));

        List<CustomerDetails> customerMap = service.list();
        assertEquals(2, customerMap.size());
        assertEquals(CustomerDetails.fromCustomer(customer1, null), customerMap.get(0));
        assertEquals(CustomerDetails.fromCustomer(customer2, null), customerMap.get(1));
    }

    @Test
    public void getDetailsById() throws URISyntaxException {
        Long customerId = 55L;
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer customer = new Customer("name", "surname", testImage);
        customer.setCreatedBy("admin");
        customer.setModifiedBy("user");
        URI imageUri = new URI("URI");

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        CustomerDetails customerDetails = service.getDetailsById(customerId, imageUri);
        assertNotNull(customerDetails);
        assertEquals(customer.getId(), customerDetails.getId());
        assertEquals(customer.getName(), customerDetails.getName());
        assertEquals(customer.getSurname(), customerDetails.getSurname());
        assertEquals(customer.getCreatedBy(), customerDetails.getCreatedBy());
        assertEquals(customer.getModifiedBy(), customerDetails.getModifiedBy());
        assertEquals(imageUri, customerDetails.getImageURI());
    }

    @Test
    public void delete() {
        Long customerId = 55L;
        Customer customer = new Customer("name", "surname", null);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        service.delete(customerId);

        verify(repository, times(1)).deleteById(customerId);
    }

    @Test
    public void uploadImage() throws IOException {
        Long customerId = 2L;
        String imageName = "imageName";
        String imageType = "image/png";
        byte[] imageBytes = {123};
        CustomerImage customerImage = new CustomerImage(imageName, imageType, imageBytes);
        Customer customer = new Customer("name", "surname", customerImage);
        customer.setId(customerId);
        MultipartFile multipartFile = new MockMultipartFile(imageName, imageName, imageType, imageBytes);

        given(entityValidator.validate(customerImage)).willReturn(Optional.empty());
        given(repository.findById(customerId)).willReturn(Optional.of(customer));
        given(repository.save(customer)).willReturn(customer);

        Long createdId = service.uploadImage(customerId, multipartFile);

        verify(entityValidator, times(1)).validate(customerImage);
        verify(repository, times(1)).save(customer);

        assertEquals(customerId, createdId);
    }

    @Test
    public void getImageById() {
        Long customerId = 55L;
        String imageName = "imageName";
        String imageType = "image/png";
        byte[] imageBytes = {123};
        CustomerImage testImage = new CustomerImage(imageName, imageType, imageBytes);
        Customer customer = new Customer("name", "surname", testImage);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        CustomerImage customerImage = service.getImageById(customerId);

        verify(repository, times(1)).findById(customerId);

        assertNotNull(customerImage);
        assertEquals(imageName, customerImage.getName());
        assertEquals(imageType, customerImage.getType());
        assertEquals(imageBytes, customerImage.getData());
    }

    @Test
    public void deleteImage() {
        Long customerId = 2L;
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer customer = new Customer("name", "surname", testImage);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        service.deleteImage(customerId);

        verify(repository, times(1)).save(customer);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testEntityNotFound() {
        Long customerId = 77L;

        given(repository.findById(customerId)).willReturn(Optional.empty());

        service.delete(customerId);
    }

    @Test(expected = ValidationException.class)
    public void testEntityNotValid() {
        Customer customer = mock(Customer.class);
        ValidationResult mockValidationResult = mock(ValidationResult.class);

        given(entityValidator.validate(customer)).willReturn(Optional.of(mockValidationResult));

        service.create(customer, "admin");
    }
}