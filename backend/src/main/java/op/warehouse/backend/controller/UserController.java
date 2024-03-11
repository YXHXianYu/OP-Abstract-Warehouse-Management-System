package op.warehouse.backend.controller;

import jakarta.annotation.security.PermitAll;
import op.warehouse.backend.entity.User;
import op.warehouse.backend.repository.UserRepository;
import op.warehouse.backend.util.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    // 查询所有用户
    @GetMapping
    @PermitAll      //TODO: remove this if the security config is done
    public JSONResult getAllUsers() {
        return JSONResult.ok(userRepository.findAll());
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    @PermitAll      //TODO: remove this if the security config is done
    public JSONResult getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        System.out.println(user);
        if(user.isPresent()) {
            return JSONResult.ok(user);
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }


    public class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {
            // 默认构造函数
        }
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        // Getter 和 Setter 方法
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/login")
    @PermitAll
    public JSONResult authenticateUser(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if(user == null) {
            return JSONResult.errorMsg("用户名为 " + loginRequest.getUsername() + " 的用户不存在！");
        } else {
            var rawPassword = loginRequest.getPassword();
            var storedEncryptedPassword = user.getPassword();
            if(rawPassword.equals(storedEncryptedPassword)) {
                return JSONResult.errorMsg("用户输入的登陆密码不正确！");
            } else {
                //TODO: 添加token授予操作
                return JSONResult.ok(user);
            }
        }
    }

    @PostMapping
    @PermitAll
    public JSONResult createUser(@RequestBody User user) {
        //TODO: add this password encoding logic here later when security is configured
//        String encodedPassword = passwordEncoder.encode(user.getPassword());
//        user.setPassword(encodedPassword);
        return JSONResult.ok(userRepository.save(user));
    }

    // 更新用户信息（包括密码）
    @PutMapping("/{id}")
    @PermitAll      //TODO: remove this if the security config is done
    public JSONResult updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isPresent()) {
            User updatedUser = userOptional.get();
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
            userRepository.save(updatedUser);
            return JSONResult.ok(updatedUser);
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }

    @PatchMapping("/{id}")
    @PermitAll      //TODO: remove this if the security config is done
    public JSONResult updateUserPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();

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
            User updatedUser = userRepository.save(userToUpdate);
            return JSONResult.ok(updatedUser);
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    @PermitAll      //TODO: remove this if the security config is done
    public JSONResult deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return JSONResult.ok();
        } else {
            return JSONResult.errorMsg("id 为 " + id + " 的用户不存在！");
        }
    }
}
