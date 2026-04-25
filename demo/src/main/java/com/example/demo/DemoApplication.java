package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.model.User;
import com.example.demo.model.Role;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.flywaydb.core.Flyway;
import javax.sql.DataSource;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "IEMS API", version = "1.0", description = "Integrated Internship & Exam Management System"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @Bean
    public Flyway flyway(javax.sql.DataSource dataSource) {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load();
    }

    @Bean
    public CommandLineRunner initData(UserRepository repo, PasswordEncoder encoder, Flyway flyway) {
        return args -> {
            try {
                System.out.println(">>> Manually triggering Flyway migration...");
                flyway.migrate();
            } catch (Exception e) {
                System.err.println(">>> Flyway migration failed: " + e.getMessage());
            }

            // Ensure Admin exists
            if (repo.findByEmailIgnoreCase("shaurya@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setName("Shaurya Admin");
                admin.setEmail("shaurya@gmail.com");
                admin.setPassword(encoder.encode("1234"));
                admin.setRole(Role.ADMIN);
                repo.save(admin);
            }

            // Ensure Student exists
            if (repo.findByEmailIgnoreCase("test@gmail.com").isEmpty()) {
                User student = new User();
                student.setName("Test Student");
                student.setEmail("test@gmail.com");
                student.setPassword(encoder.encode("1234"));
                student.setRole(Role.STUDENT);
                repo.save(student);
            }
            System.out.println(">>> Database migrated: Default users ensured.");
        };
    }

}
