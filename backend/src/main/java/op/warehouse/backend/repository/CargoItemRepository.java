package op.warehouse.backend.repository;

import op.warehouse.backend.entity.CargoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoItemRepository extends JpaRepository<CargoItem, Long> {
}
