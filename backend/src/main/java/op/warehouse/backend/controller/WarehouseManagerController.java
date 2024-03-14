package op.warehouse.backend.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.dto.RoleLoginDTO;
import op.warehouse.backend.dto.UserLoginDTO;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.entity.User;
import op.warehouse.backend.entity.WarehouseManager;
import op.warehouse.backend.repository.WarehouseManagerRepository;
import op.warehouse.backend.util.JSONResult;
import op.warehouse.backend.util.JwtUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/warehouse-manager")
public class WarehouseManagerController {
    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Autowired
    private JwtUtil jwtUtil;


    // 查询所有用户
    @GetMapping
    @RequiresAuthentication
    public JSONResult getAllUsers() {
        return JSONResult.ok(warehouseManagerRepository.findAll());
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    public JSONResult getUserById(@PathVariable Long id) {
        Optional<WarehouseManager> user = warehouseManagerRepository.findById(id);
        System.out.println(user);
        if(user.isPresent()) {
            return JSONResult.ok(user);
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
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
            return ResultDTO.error("用户名不存在");
        }

        if (!user.getPassword().equals(password)) {
            return ResultDTO.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(username, roleType);
        response.setHeader(JwtUtil.HEADER, token);
        response.setHeader("Access-control-Expost-Headers", JwtUtil.HEADER);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return ResultDTO.success(map);
    }

    @PostMapping
    public JSONResult createUser(@RequestBody WarehouseManager user) {
        //TODO: add this password encoding logic here later when security is configured
//        String encodedPassword = passwordEncoder.encode(user.getPassword());
//        user.setPassword(encodedPassword);
        System.out.println(user.getPassword());
        System.out.println(user.getUsername());
        System.out.println(user.getEmail());
        System.out.println(user.getPhoneNumber());
        return JSONResult.ok(warehouseManagerRepository.save(user));
    }

    // 更新用户信息（包括密码）
    @PutMapping("/{id}")
    public JSONResult updateUser(@PathVariable Long id, @RequestBody WarehouseManager userDetails) {
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
            return JSONResult.ok(updatedUser);
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }

    @PatchMapping("/{id}")
    @PermitAll      //TODO: remove this if the security config is done
    public JSONResult updateUserPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
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
            return JSONResult.ok(updatedUser);
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    @RequiresAuthentication
    public JSONResult deleteUser(@PathVariable Long id) {
        Optional<WarehouseManager> optionalUser = warehouseManagerRepository.findById(id);
        if(optionalUser.isPresent()) {
            warehouseManagerRepository.deleteById(id);
            return JSONResult.ok();
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }
}
