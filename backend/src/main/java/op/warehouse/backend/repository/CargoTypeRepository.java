package op.warehouse.backend.repository;

import op.warehouse.backend.entity.CargoType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface CargoTypeRepository extends JpaRepository<CargoType, Long> {
    public default List<Map<String, Object>> findAllToMap() {
        var cargoTypes = this.findAll();
        List<Map<String, Object>> result = cargoTypes.stream()
                .map(CargoType::toMap) // 使用方法引用简化转换为 Map 的操作
                .collect(Collectors.toList());
        return result;
    }

    public default List<Map<String, Object>> findAllToTree() {
        var roots = this.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        if(roots.isEmpty()) {
            return result;
        }
        for(var rootType : roots) {
            if(rootType.getFatherType()==null){
                result.add(rootType.toMapRecursively());
            }
        }
        return result;
    }
}
