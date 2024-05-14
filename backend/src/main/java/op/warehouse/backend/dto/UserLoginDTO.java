package op.warehouse.backend.dto;

import lombok.Data;
import op.warehouse.backend.entity.RoleType;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    private String username;
    private String password;
    private RoleType role;
}
