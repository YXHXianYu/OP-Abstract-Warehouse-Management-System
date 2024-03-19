package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import op.warehouse.backend.repository.WarehouseManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@Setter
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, unique = true, nullable = false)
    private String name;

    @Column(length = 256, nullable = false)
    private String description;

    @Column(length = 256, nullable = false)
    private String address;

    @Column(length = 256, nullable = false)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonIgnore
    private WarehouseManager manager = null;


    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name = "area_to_warehouse")
    @JsonIgnore
    private List<WarehouseArea> warehouseAreas;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("address", this.address);
        map.put("phoneNumber", this.phoneNumber);
        return map;
    }
}
