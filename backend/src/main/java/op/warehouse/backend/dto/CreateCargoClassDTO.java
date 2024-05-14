package op.warehouse.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCargoClassDTO {

    String name;
    String description;
    Long cargoTypeId;
}
