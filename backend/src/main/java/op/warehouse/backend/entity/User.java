package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(length = 256, nullable = false, unique = true)
    private String username;

    @Setter
    @Getter
    @Column(length = 256, nullable = false)
    private String email;

    @Setter
    @Getter
    @Column(length = 32, nullable = false)
    private String phoneNumber;

    @Setter
    @Getter
    @Column(nullable = false)
    private LocalDateTime registrationDate;


    @Setter
    @Getter
    @Column(length = 256, nullable = false)
    private String password;


    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    // Method to automatically set the registration date before persisting
    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
}

