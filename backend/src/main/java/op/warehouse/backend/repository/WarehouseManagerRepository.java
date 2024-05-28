package op.warehouse.backend.repository;


import op.warehouse.backend.entity.User;
import op.warehouse.backend.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseManagerRepository extends JpaRepository<WarehouseManager, Long> {
    User findByUsername(String username);
}
