package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import op.warehouse.backend.repository.CargoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@Setter
@Table(name = "cargo_class")
public class CargoClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String name;

    @Column(length = 1024, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "cargo_type_id")
    private CargoType cargoType;

    @OneToMany(fetch = FetchType.EAGER)
    private List<CargoItem> cargoItems;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("cargoTypeId", this.cargoType.getId());
        map.put("cargoType", this.cargoType.toMap());
        return map;
    }

    public int getItemCount() {
        return this.cargoItems.size();
    }


}
