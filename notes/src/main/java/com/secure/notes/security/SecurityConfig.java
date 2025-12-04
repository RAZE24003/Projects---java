package com.secure.notes.security;

import com.secure.notes.models.AppRole;
import com.secure.notes.models.Role;
import com.secure.notes.models.User;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((requests) -> requests
//                .requestMatchers("/contact").permitAll()
//                .requestMatchers("/admin").denyAll()
//                .anyRequest().authenticated());
////        http.formLogin(withDefaults());
//        http.csrf(AbstractHttpConfigurer::disable);
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http.httpBasic(withDefaults());
//        return http.build();
//    }
//}

// UPDATED CONFIG
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,
        securedEnabled = true,jsr250Enabled = true)
public  class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity.authorizeHttpRequests((requests)
        ->requests.anyRequest().authenticated());
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.httpBasic(withDefaults());
        return httpSecurity.build();

    }

//    IN MEMORY USER DETAILS , STORED IN RAM
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource)
//    {
////        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(); -> in memory , and in this we will remove the datasource parameter
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource); //-> only single line to change from in memory to jdbc
//
//        if(!manager.userExists("user1"))
//        {
//            manager.createUser(
//                    User.withUsername("user1")
//                            .password("{noop}password1").roles("USER").build()
//            );
//        }
//        if(!manager.userExists("admin"))
//        {
//            manager.createUser(
//                    User.withUsername("admin")
//                            .password("{noop}adminPass").roles("ADMIN").build()
//            );
//        }
//        return manager;
//    }


//    WE HAVE CUSTOM ROLES SO NOT NEEDED THIS


@Bean
public CommandLineRunner initData(RoleRepository roleRepository,
                                  UserRepository userRepository) {
    return args -> {

        // Create ROLE_USER if not exists
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

        // Create ROLE_ADMIN if not exists
        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

        //  Create USER
        if (!userRepository.existsByUserName("user1")) {

            User user1 = new User("user1", "user1@example.com", "{noop}password1");

            user1.setAccountNonLocked(true);
            user1.setAccountNonExpired(true);
            user1.setCredentialsNonExpired(true);
            user1.setEnabled(true);

            user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user1.setAccountExpiryDate(LocalDate.now().plusYears(1));

            user1.setTwoFactorEnabled(false);
            user1.setSignUpMethod("email");
            user1.setRole(userRole);

            userRepository.save(user1);
        }

        //  Create ADMIN
        if (!userRepository.existsByUserName("admin")) {

            User admin = new User("admin", "admin@example.com", "{noop}adminPass");

            admin.setAccountNonLocked(true);
            admin.setAccountNonExpired(true);
            admin.setCredentialsNonExpired(true);
            admin.setEnabled(true);

            admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            admin.setAccountExpiryDate(LocalDate.now().plusYears(1));

            admin.setTwoFactorEnabled(false);
            admin.setSignUpMethod("email");
            admin.setRole(adminRole);

            userRepository.save(admin);
        }
    };
}

}