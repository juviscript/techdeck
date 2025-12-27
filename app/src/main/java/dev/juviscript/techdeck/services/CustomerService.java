package dev.juviscript.techdeck.services;

import dev.juviscript.techdeck.models.Customer;
import dev.juviscript.techdeck.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Get all customers
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    /**
     * Get customer by email
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Create a new customer
     */
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + customer.getEmail());
        }
        return customerRepository.save(customer);
    }

    /**
     * Update an existing customer
     */
    public Customer updateCustomer(UUID id, Customer updatedCustomer) {
        return customerRepository.findById(id)
                .map(customer -> {
                    // Check if email changed and is already in use by someone else
                    if (!customer.getEmail().equalsIgnoreCase(updatedCustomer.getEmail())
                            && customerRepository.existsByEmail(updatedCustomer.getEmail())) {
                        throw new IllegalArgumentException("Email already in use: " + updatedCustomer.getEmail());
                    }

                    customer.setFirstName(updatedCustomer.getFirstName());
                    customer.setLastName(updatedCustomer.getLastName());
                    customer.setEmail(updatedCustomer.getEmail());
                    customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
                    customer.setNotes(updatedCustomer.getNotes());
                    return customerRepository.save(customer);
                })
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
    }

    /**
     * Delete a customer by ID
     */
    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    /**
     * Check if email is available
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !customerRepository.existsByEmail(email);
    }
}