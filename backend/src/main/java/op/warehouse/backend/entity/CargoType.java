package op.warehouse.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Long> children = null;
        if(childrenTypes != null){
            children = childrenTypes.stream().map(CargoType::getId).toList();
        }
        map.put("fatherType", this.fatherType==null ? null : this.fatherType.getId());
        map.put("childrenTypes", children);
        return map;
    }
    public Map<String, Object> toMapRecursively() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("description", this.description);
        List<Map<String, Object>> children = null;
        if(!(this.childrenTypes == null) && !(this.childrenTypes.isEmpty())) {
            children = new ArrayList<>();
            for(CargoType type : this.childrenTypes) {
                children.add(type.toMapRecursively());
            }
        }
        map.put("childrenTypes", children);
        return map;
    }
}
