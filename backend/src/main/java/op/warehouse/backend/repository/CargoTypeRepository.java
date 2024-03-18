package op.warehouse.backend.repository;

import op.warehouse.backend.entity.CargoType;
import op.warehouse.backend.entity.WarehouseManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoTypeRepository extends JpaRepository<CargoType, Long> {
}
