package op.warehouse.backend.service;


import lombok.extern.slf4j.Slf4j;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.entity.User;
import op.warehouse.backend.entity.WarehouseManager;
import op.warehouse.backend.repository.WarehouseManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    public User createUser(RoleType role, User user) {
        if(role == RoleType.WAREHOUSE_MANAGER) {
            return warehouseManagerRepository.save((WarehouseManager) user);
        } else {
            return null;
        }
    }

    public User getUserByUsername(RoleType role, String username) {
        if(role == RoleType.WAREHOUSE_MANAGER) {
            return warehouseManagerRepository.findByUsername(username);
        } else {
            return null;
        }
    }

    public void deleteUser(RoleType role, String username) {
        if(role == RoleType.WAREHOUSE_MANAGER) {
            warehouseManagerRepository.delete((WarehouseManager) getUserByUsername(role, username));
        }
    }
}
