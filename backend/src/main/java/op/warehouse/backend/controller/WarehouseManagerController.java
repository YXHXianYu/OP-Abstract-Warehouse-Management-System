package op.warehouse.backend.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.dto.RoleLoginDTO;
import op.warehouse.backend.dto.UserLoginDTO;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.entity.User;
import op.warehouse.backend.entity.WarehouseManager;
import op.warehouse.backend.repository.WarehouseManagerRepository;
import op.warehouse.backend.util.JSONResult;
import op.warehouse.backend.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/warehouse-manager")
public class WarehouseManagerController {
    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static ResultDTO getReadOthersResultDTO(Long id) {
        // 获取当前 Subject
        Subject subject = SecurityUtils.getSubject();
        // 获取已认证的 Principal（用户对象）
        User authUser = (User) subject.getPrincipal();
        // 判断id是否合法
        if(!id.equals(authUser.getId())) {
            return ResultDTO.error(ResponseCodeEnum.UNAUTHORIZED, "Not permitted to check other's information");
        }
        return null;
    }


    // 查询所有用户
    @GetMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO getAllUsers() {
        return ResultDTO.success(warehouseManagerRepository.findAll());
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO getUserById(@PathVariable Long id) {
        ResultDTO UNAUTHORIZED = getReadOthersResultDTO(id);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Optional<WarehouseManager> user = warehouseManagerRepository.findById(id);
        System.out.println(user);
        if(user.isPresent()) {
            return ResultDTO.success(user);
        } else {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id 为 " + id + " 的用户不存在！");
        }
    }



    @PostMapping("/login")
    @PermitAll
    public ResultDTO authenticateUser(@RequestBody @Validated RoleLoginDTO userLoginDTO, HttpServletResponse response) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        RoleType roleType = RoleType.WAREHOUSE_MANAGER;
        User user = warehouseManagerRepository.findByUsername(username);
        if (user == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "用户名不存在");
        }

        if (!user.getPassword().equals(password)) {
            return ResultDTO.error(ResponseCodeEnum.ERROR, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(username, roleType);
        response.setHeader(JwtUtil.HEADER, token);
        response.setHeader("Access-control-Expost-Headers", JwtUtil.HEADER);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return ResultDTO.success(map);
    }

    @PostMapping
    public ResultDTO createUser(@RequestBody WarehouseManager user) {
        //TODO: add this password encoding logic here later when security is configured
//        String encodedPassword = passwordEncoder.encode(user.getPassword());
//        user.setPassword(encodedPassword);
        System.out.println(user.getPassword());
        System.out.println(user.getUsername());
        System.out.println(user.getEmail());
        System.out.println(user.getPhoneNumber());
        return ResultDTO.success(warehouseManagerRepository.save(user));
    }

    // 更新用户信息（包括密码）
    @PutMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO updateUser(@PathVariable Long id, @RequestBody WarehouseManager userDetails) {
        ResultDTO UNAUTHORIZED = getReadOthersResultDTO(id);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Optional<WarehouseManager> userOptional = warehouseManagerRepository.findById(id);

        if(userOptional.isPresent()) {
            WarehouseManager updatedUser = userOptional.get();
            updatedUser.setUsername(userDetails.getUsername());
            updatedUser.setEmail(userDetails.getEmail());
            updatedUser.setPhoneNumber(userDetails.getPhoneNumber());
            // 检查是否提供了密码
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
//                String encodedPassword = passwordEncoder.encode(userDetails.getPassword());
//                updatedUser.setPassword(encodedPassword);
                //TODO: add this password encoding logic here later when security is configured
                updatedUser.setPassword(userDetails.getPassword());
            }
            warehouseManagerRepository.save(updatedUser);
            return ResultDTO.success(updatedUser);
        } else {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id 为 " + id + " 的用户不存在！");
        }
    }

    @PatchMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO updateUserPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        ResultDTO UNAUTHORIZED = getReadOthersResultDTO(id);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Optional<WarehouseManager> optionalUser = warehouseManagerRepository.findById(id);

        if(optionalUser.isPresent()) {
            WarehouseManager userToUpdate = optionalUser.get();

            // 判断哪些字段需要被更新，并相应地更新它们
            if(updates.containsKey("username")) {
                userToUpdate.setUsername((String) updates.get("username"));
            }
            if(updates.containsKey("email")) {
                userToUpdate.setEmail((String) updates.get("email"));
            }
            if(updates.containsKey("phoneNumber")) {
                userToUpdate.setPhoneNumber((String) updates.get("phoneNumber"));
            }
            if(updates.containsKey("password") && !(((String)updates.get("password")).isEmpty())) {
//                String encodedPassword = passwordEncoder.encode((String)updates.get("password"));
//                userToUpdate.setPassword(encodedPassword);
                //TODO: add this password encoding logic here later when security is configured
                userToUpdate.setPassword((String)updates.get("password"));
            }
            // 可以添加更多字段的检查和更新
            User updatedUser = warehouseManagerRepository.save(userToUpdate);
            return ResultDTO.success(updatedUser);
        } else {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id 为 " + id + " 的用户不存在！");
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO deleteUser(@PathVariable Long id) {
        ResultDTO UNAUTHORIZED = getReadOthersResultDTO(id);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;
        Optional<WarehouseManager> optionalUser = warehouseManagerRepository.findById(id);
        if(optionalUser.isPresent()) {
            warehouseManagerRepository.deleteById(id);
            return ResultDTO.success();
        } else {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id 为 " + id + " 的用户不存在！");
        }
    }
}
