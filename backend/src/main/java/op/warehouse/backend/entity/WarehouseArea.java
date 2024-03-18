package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@Setter
@Table(name = "warehouse_area")
public class WarehouseArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, unique = true, nullable = false)
    private String name;

    @Column(length = 256, nullable = false)
    private String description;

    @Column
    private Long capacity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_to_type")
    @JsonIgnore
    private CargoType cargoType;

    @ManyToOne
    @JoinColumn(name = "area_to_warehouse")
    @JsonIgnore
    private Warehouse warehouse;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("capacity", this.capacity);
        map.put("cargoType", this.cargoType.getId());
        map.put("warehouse", this.warehouse.getId());
        return map;
    }
}
