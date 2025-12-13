package com.backend.Kata;

import com.backend.Kata.entities.Role;
import com.backend.Kata.entities.User;
import com.backend.Kata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class KataApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(KataApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Create default admin user if it doesn't exist
		userRepository.findByRole(Role.ADMIN).ifPresentOrElse(
			admin -> {
				System.out.println("Admin user already exists: " + admin.getEmail());
			},
			() -> {
				// Check if admin email already exists (might be a regular user)
				if (userRepository.findFirstByEmail("admin@admin.com").isEmpty()) {
					User admin = new User();
					admin.setName("Admin");
					admin.setEmail("admin@admin.com");
					admin.setPassword(passwordEncoder.encode("admin123"));
					admin.setRole(Role.ADMIN);
					userRepository.save(admin);
					System.out.println("========================================");
					System.out.println("Default admin user created!");
					System.out.println("Email: admin@admin.com");
					System.out.println("Password: admin123");
					System.out.println("========================================");
				} else {
					System.out.println("Email admin@admin.com already exists. Please update the role manually in database.");
				}
			}
		);
	}
}
