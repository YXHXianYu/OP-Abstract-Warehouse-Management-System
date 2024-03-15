package op.warehouse.backend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleLoginDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    private String username;
    private String password;
}
