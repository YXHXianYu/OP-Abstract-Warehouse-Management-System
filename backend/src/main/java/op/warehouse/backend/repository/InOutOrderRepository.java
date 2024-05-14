package op.warehouse.backend.repository;

import op.warehouse.backend.entity.InOutOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InOutOrderRepository extends JpaRepository<InOutOrder, Long> {
}
