package op.warehouse.backend.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@Setter
@Table(name = "inoutorder")
public class InOutOrder {

    public enum StateEnum {
        NOT_ASSIGNED(0),
        WAITING(1),
        IN_PROCESS(2),
        FINISHED(3);

        StateEnum(int number) {
            this.number = number;
        }

        public final int number; //0/1/2/3分别表示未分配/未处理/处理中/已完成
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String description;

    @ManyToMany(fetch=FetchType.EAGER)
    @JsonIgnore
    private List<CargoItem> cargoItemList;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private Boolean isOutOrder;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "create_user_id")
    @JsonIgnore
    private WarehouseManager createdUser;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "picker_user_id")
    @JsonIgnore
    private WarehouseManager pickerUser;

    @Column(nullable = false)
    private StateEnum state = StateEnum.NOT_ASSIGNED;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", this.id);
        result.put("isOutOrder", this.isOutOrder);
        result.put("createdTime", this.createdTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        result.put("createdUser", this.createdUser.toMap());
        result.put("cargoItemList", this.cargoItemList.stream().map(CargoItem::toMap));
        result.put("description", this.description);
        result.put("state", this.state.number);
        result.put("pickerUser", this.pickerUser==null ? null : this.pickerUser.toMap());
        return result;
    }
}
