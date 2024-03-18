package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@Setter
@Table(name = "cargo_type")
public class CargoType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, unique = true, nullable = false)
    private String name;

    @Column(length = 256, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "cargo_type_tree")
    @JsonIgnore
    private CargoType fatherType;

    @OneToMany
    @JoinColumn(name = "cargo_type_tree")
    private List<CargoType> childrenTypes;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("fatherType", this.fatherType);
        map.put("childrenTypes", this.childrenTypes);
        return map;
    }
}
