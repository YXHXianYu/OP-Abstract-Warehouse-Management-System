package op.warehouse.backend.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.dto.UserLoginDTO;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.entity.User;
import op.warehouse.backend.repository.WarehouseManagerRepository;
import op.warehouse.backend.service.UserService;
import op.warehouse.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @PermitAll
    public ResultDTO authenticateUser(@RequestBody @Validated UserLoginDTO userLoginDTO, HttpServletResponse response) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        RoleType roleType = userLoginDTO.getRole();
        User user = userService.getUserByUsername(roleType, username);
        if (user == null) {
            return ResultDTO.error("用户名不存在");
        }

        if (!user.getPassword().equals(password)) {
            return ResultDTO.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(username, roleType);
        response.setHeader(JwtUtil.HEADER, token);
        response.setHeader("Access-control-Expost-Headers", JwtUtil.HEADER);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("role", String.valueOf(userLoginDTO.getRole()));
        map.put("user", user.toMap());
        return ResultDTO.success(map);
    }
}
