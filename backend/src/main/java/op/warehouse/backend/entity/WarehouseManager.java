package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Setter
@Table(name = "user_warehouse_admin")
public class WarehouseManager implements User{

    @Override
    public RoleType getRoleType() {
        return RoleType.WAREHOUSE_MANAGER;
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 256, nullable = false, unique = true)
    private String username;

    @Column(length = 256, nullable = false)
    private String email;

    @Column(length = 32, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(length = 256, nullable = false)
    private String password;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    @JsonIgnore
    private List<Warehouse> warehouse;

    // Method to automatically set the registration date before persisting
    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
}
