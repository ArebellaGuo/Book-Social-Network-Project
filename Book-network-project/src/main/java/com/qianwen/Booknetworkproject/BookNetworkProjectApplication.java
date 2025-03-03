package com.qianwen.Booknetworkproject;

import com.qianwen.Booknetworkproject.entities.role.Role;
import com.qianwen.Booknetworkproject.entities.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookNetworkProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkProjectApplication.class, args);
	}
	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				Role role = new Role();
				role.setName("USER");
				roleRepository.save(role);
			}
		};
	}
}
