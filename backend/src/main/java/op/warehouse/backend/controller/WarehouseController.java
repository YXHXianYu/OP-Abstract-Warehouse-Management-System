package op.warehouse.backend.controller;

import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.entity.*;
import op.warehouse.backend.repository.CargoTypeRepository;
import op.warehouse.backend.repository.WarehouseAreaRepository;
import op.warehouse.backend.repository.WarehouseManagerRepository;
import op.warehouse.backend.repository.WarehouseRepository;
import op.warehouse.backend.util.SecurityUtilities;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {
    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    WarehouseManagerController warehouseManagerController;

    @Autowired
    WarehouseAreaRepository warehouseAreaRepository;

    @Autowired
    CargoTypeRepository cargoTypeRepository;

    @GetMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO getAllWarehouses() {
        WarehouseManager manager = (WarehouseManager) SecurityUtilities.getAuthUser();
        return ResultDTO.success(manager.getWarehouse());
    }

    @GetMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO getWarehouseById(@PathVariable Long id) {
        ResultDTO unauthorized = SecurityUtilities.getReadOthersResultDTO(id);
        if (unauthorized != null) return unauthorized;
        Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find warehouse with id " + id);
        }
        return ResultDTO.success(warehouse);
    }

    @PostMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO createWarehouse(@RequestBody Warehouse warehouse) {
        WarehouseManager manager = (WarehouseManager) SecurityUtilities.getAuthUser();
        warehouse.setManager(manager);
        return ResultDTO.success(warehouseRepository.save(warehouse));
    }


    /*
        AREA
     */
    @GetMapping("/{id}/areas")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO getAreas(@PathVariable Long id) {
        ResultDTO unauthorized = SecurityUtilities.getReadOthersResultDTO(id);
        if (unauthorized != null) return unauthorized;
        Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find warehouse with id " + id);
        }
        return ResultDTO.success(warehouse.getWarehouseAreas());
    }

    @GetMapping("/{id}/areas/{areaId}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO getSingleAreas(@PathVariable Long id, @PathVariable Long areaId) {
        ResultDTO unauthorized = SecurityUtilities.getReadOthersResultDTO(id);
        if (unauthorized != null) return unauthorized;
        Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find warehouse with id " + id);
        }
        //判断 areaID 的存在
        WarehouseArea area = warehouseAreaRepository.getReferenceById(areaId);
        //判断所属情况
        if (!warehouse.getWarehouseAreas().contains(area)) {
            return ResultDTO.error(ResponseCodeEnum.NOT_ACCEPTABLE, "Warehouse area with id " + areaId + " is not belongs to this warehouse");
        }
        return ResultDTO.success(area.toMap());
    }

    @PostMapping("/{id}/areas")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO createAreas(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ResultDTO unauthorized = SecurityUtilities.getReadOthersResultDTO(id);
        if (unauthorized != null) return unauthorized;
        Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
        if (warehouse == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find warehouse with id " + id);
        }
        if(!body.containsKey("cargoType")) {
            return ResultDTO.error(ResponseCodeEnum.NOT_ACCEPTABLE, "Cannot create a warehouse area without providing cargo-type's id");
        }
        if(!body.containsKey("name") || !body.containsKey("description") || !body.containsKey("capacity")) {
            return ResultDTO.error(ResponseCodeEnum.NOT_ACCEPTABLE, "There's(re) required field(s) amongst name, description or capacity that is(are) lack in the request body");
        }
        Long cargoId = ((Integer)body.get("cargoType")).longValue();
        Long capacity = ((Integer)body.get("capacity")).longValue();
        String name = (String) body.get("name");
        String description = (String) body.get("description");

        var cargoTypeQuery = cargoTypeRepository.findById(cargoId);
        if(cargoTypeQuery.isEmpty()) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find cargo-type with id " + cargoId);
        }
        WarehouseArea area = new WarehouseArea();

        area.setWarehouse(warehouse);
        area.setCargoType(cargoTypeQuery.get());
        area.setName(name);
        area.setDescription(description);
        area.setCapacity(capacity);

        warehouse.getWarehouseAreas().add(area);
        warehouseAreaRepository.save(area);
        warehouseRepository.save(warehouse);
        return ResultDTO.success(area.toMap());
    }
}
