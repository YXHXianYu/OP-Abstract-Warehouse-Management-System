package op.warehouse.backend.repository;

import op.warehouse.backend.entity.WarehouseArea;
import op.warehouse.backend.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseAreaRepository extends JpaRepository<WarehouseArea, Long> {
}
