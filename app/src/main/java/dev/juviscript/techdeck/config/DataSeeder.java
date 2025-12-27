package dev.juviscript.techdeck.config;

import dev.juviscript.techdeck.models.Role;
import dev.juviscript.techdeck.models.ServiceType;
import dev.juviscript.techdeck.models.User;
import dev.juviscript.techdeck.repositories.ServiceTypeRepository;
import dev.juviscript.techdeck.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds the database with default data on application startup.
 * Only runs in 'dev' profile to avoid polluting production data.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedServiceTypes();
        seedDefaultUsers();
        log.info("✅ Database seeding completed!");
    }

    private void seedServiceTypes() {
        if (serviceTypeRepository.count() > 0) {
            log.info("Service types already exist, skipping seed...");
            return;
        }

        List<ServiceType> serviceTypes = List.of(
                createServiceType("TV Mounting", "Wall mount installation for flat-screen TVs", 
                        new BigDecimal("150.00"), 60),
                createServiceType("TV Mounting With Concealment", "TV mounting with in-wall cable concealment", 
                        new BigDecimal("250.00"), 90),
                createServiceType("Surround Sound Installation", "Full surround sound speaker system setup", 
                        new BigDecimal("300.00"), 120),
                createServiceType("Soundbar Installation", "Soundbar mounting and configuration", 
                        new BigDecimal("100.00"), 45),
                createServiceType("Network Setup", "Home network configuration and optimization", 
                        new BigDecimal("200.00"), 90),
                createServiceType("WiFi Optimization", "WiFi coverage analysis and improvement", 
                        new BigDecimal("150.00"), 60),
                createServiceType("Smart Home Consultation", "Smart home planning and recommendations", 
                        new BigDecimal("100.00"), 45),
                createServiceType("Smart Home Installation", "Smart device installation and setup", 
                        new BigDecimal("175.00"), 60),
                createServiceType("Smart Thermostat Install", "Smart thermostat installation and programming", 
                        new BigDecimal("125.00"), 45),
                createServiceType("Smart Lighting Setup", "Smart lighting installation and automation", 
                        new BigDecimal("150.00"), 60),
                createServiceType("Security Camera Installation", "Security camera mounting and configuration", 
                        new BigDecimal("200.00"), 90),
                createServiceType("Video Doorbell Installation", "Video doorbell installation and setup", 
                        new BigDecimal("100.00"), 45),
                createServiceType("Home Theater Setup", "Complete home theater system configuration", 
                        new BigDecimal("400.00"), 180),
                createServiceType("Projector Installation", "Projector mounting and screen setup", 
                        new BigDecimal("250.00"), 120),
                createServiceType("Cable Management", "Cable organization and concealment", 
                        new BigDecimal("100.00"), 60),
                createServiceType("Device Configuration", "Setup and configuration of smart devices", 
                        new BigDecimal("75.00"), 30),
                createServiceType("Troubleshooting", "Diagnosis and repair of AV/smart home issues", 
                        new BigDecimal("100.00"), 60),
                createServiceType("Follow-Up Visit", "Return visit for adjustments or additional work", 
                        new BigDecimal("75.00"), 30)
        );

        serviceTypeRepository.saveAll(serviceTypes);
        log.info("🌱 Seeded {} service types", serviceTypes.size());
    }

    private void seedDefaultUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping seed...");
            return;
        }

        List<User> users = List.of(
                createUser("Admin", "User", "admin@techyeah.com", "admin123", Role.ADMIN),
                createUser("Tech", "One", "tech1@techyeah.com", "tech123", Role.TECHNICIAN),
                createUser("Tech", "Two", "tech2@techyeah.com", "tech123", Role.TECHNICIAN)
        );

        userRepository.saveAll(users);
        log.info("🌱 Seeded {} users", users.size());
        log.info("📧 Default admin login: admin@techyeah.com / admin123");
    }

    private ServiceType createServiceType(String name, String description, BigDecimal baseRate, int durationMinutes) {
        ServiceType serviceType = new ServiceType();
        serviceType.setName(name);
        serviceType.setDescription(description);
        serviceType.setBaseRate(baseRate);
        serviceType.setBaseDurationInMinutes(durationMinutes);
        serviceType.setActive(true);
        return serviceType;
    }

    private User createUser(String firstName, String lastName, String email, String password, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        return user;
    }
}
