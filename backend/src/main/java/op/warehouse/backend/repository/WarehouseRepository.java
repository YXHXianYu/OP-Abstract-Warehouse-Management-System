package op.warehouse.backend.repository;

import op.warehouse.backend.entity.Warehouse;
import op.warehouse.backend.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
