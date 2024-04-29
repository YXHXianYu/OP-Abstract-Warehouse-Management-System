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
@Table(name = "cargo_item")
public class CargoItem {
    public enum StateEnum {
        ON_BOARDING(3),
        READY(4),
        DOWN_BOARDING(7),
        LEAVED(8);

        StateEnum(int number) {
            this.number = number;
        }

        private final int number;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cargo_class_id")
    @JsonIgnore
    private CargoClass cargoClass;

    @ManyToOne
    @JoinColumn(name = "warehouse_area_id")
    @JsonIgnore
    private WarehouseArea warehouseArea;

    @Column(nullable = false)
    private String state = String.valueOf(StateEnum.ON_BOARDING);

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", this.id);
        result.put("cargoClassId", this.cargoClass.getId());
        result.put("warehouseAreaId", this.warehouseArea.getId());
        result.put("cargoClass", this.cargoClass.toMap());
        return result;
    }
}
