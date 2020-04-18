package com.abara.service;

import com.abara.entity.Customer;
import com.abara.model.CustomerDetails;
import com.abara.model.CustomerImage;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private CustomerServiceImpl service;

    @Test
    public void create() {
        Long testId = 2L;
        Customer customer = new Customer("username", "name", "surname", "email@company.com", "imageUuid");

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
        Customer customer1 = new Customer("username1", "name1", "surname1", "email1@company.com", null);
        Customer customer2 = new Customer("username2", "name2", "surname2", "email2@company.com", null);

        given(repository.findAll()).willReturn(Stream.of(customer1, customer2).collect(Collectors.toList()));

        List<CustomerDetails> customerMap = service.list();
        assertEquals(2, customerMap.size());
        assertEquals(CustomerDetails.fromCustomer(customer1, null), customerMap.get(0));
        assertEquals(CustomerDetails.fromCustomer(customer2, null), customerMap.get(1));
    }

    @Test
    public void getDetailsById() throws URISyntaxException {
        Long customerId = 55L;
        Customer customer = new Customer("username", "name", "surname", "email@company.com", "imageUuid");
        customer.setCreatedBy("admin");
        customer.setModifiedBy("user");
        URI imageUri = new URI("URI");

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        CustomerDetails customerDetails = service.getDetailsById(customerId, imageUri);
        assertNotNull(customerDetails);
        assertEquals(customer.getId(), customerDetails.getId());
        assertEquals(customer.getUsername(), customerDetails.getUsername());
        assertEquals(customer.getName(), customerDetails.getName());
        assertEquals(customer.getSurname(), customerDetails.getSurname());
        assertEquals(customer.getEmail(), customerDetails.getEmail());
        assertEquals(customer.getCreatedBy(), customerDetails.getCreatedBy());
        assertEquals(customer.getModifiedBy(), customerDetails.getModifiedBy());
        assertEquals(imageUri, customerDetails.getImageURI());
    }

    @Test
    public void delete() {
        Long customerId = 55L;
        Customer customer = new Customer("username", "name", "surname", "email@company.com", "imageUuid");
        customer.setId(customerId);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));

        service.delete(customerId);

        verify(repository, times(1)).deleteById(customerId);
    }

    @Test
    public void uploadImage() throws Exception {
        Long customerId = 2L;
        String testUuid = "uuid";
        byte[] imageBytes = {123};
        Customer customer = new Customer("username", "name", "surname", "email@company.com", null);
        customer.setId(customerId);
        MultipartFile multipartFile = new MockMultipartFile("imageName", "originalFileName", "image/png", imageBytes);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));
        given(repository.save(customer)).willReturn(customer);
        given(storageService.storeImage(multipartFile)).willReturn(testUuid);

        String uuid = service.uploadImage(customerId, multipartFile);

        verify(repository, times(1)).save(customer);

        assertEquals(testUuid, uuid);
    }

    @Test
    public void getImageById() throws IOException {
        Long customerId = 55L;
        byte[] imageBytes = {123};
        String testUuid = "imageUuid";
        CustomerImage image = new CustomerImage("image/png", imageBytes);
        Customer customer = new Customer("username", "name", "surname", "email@company.com", testUuid);

        given(repository.findById(customerId)).willReturn(Optional.of(customer));
        given(storageService.getImage(testUuid)).willReturn(image);

        CustomerImage customerImage = service.getImageById(customerId);

        verify(repository, times(1)).findById(customerId);

        assertNotNull(customerImage);
        assertEquals(imageBytes, customerImage.getData());
        assertEquals("image/png", customerImage.getType());
    }

    @Test
    public void deleteImage() throws Exception {
        Long customerId = 2L;
        Customer customer = new Customer("username", "name", "surname", "email@company.com", "imageUuid");

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