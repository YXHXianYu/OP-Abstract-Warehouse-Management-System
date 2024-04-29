package op.warehouse.backend.repository;

import op.warehouse.backend.entity.CargoClass;
import op.warehouse.backend.entity.CargoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface CargoClassRepository extends JpaRepository<CargoClass, Long> {
}
