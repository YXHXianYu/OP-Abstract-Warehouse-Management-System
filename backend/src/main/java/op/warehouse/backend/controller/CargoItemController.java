package op.warehouse.backend.controller;

import lombok.Getter;
import lombok.Setter;
import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.entity.CargoItem;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.repository.CargoClassRepository;
import op.warehouse.backend.repository.CargoItemRepository;
import op.warehouse.backend.repository.WarehouseAreaRepository;
import op.warehouse.backend.service.CargoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cargo-items")
public class CargoItemController {
    @Autowired
    private CargoItemRepository cargoItemRepository;

    @Autowired
    private CargoClassRepository cargoClassRepository;

    @Autowired
    private WarehouseAreaRepository warehouseAreaRepository;

    @Autowired
    private CargoItemService cargoItemService;

    @Getter
    @Setter
    public static class CreateDTO {
        private Long cargoClassId;
        private Long warehouseAreaId;
    }
    @PostMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO createCargoItems(@RequestBody CreateDTO createDTO) {
        if(createDTO.cargoClassId == null) {
            return ResultDTO.error("提供的商品类别为空");
        }
        var optionalCargoClass = cargoClassRepository.findById(createDTO.cargoClassId);
        if(optionalCargoClass.isEmpty()) {
            return ResultDTO.error("提供的商品类别不存在");
        }
        var cargoClass = optionalCargoClass.get();

        if(createDTO.warehouseAreaId == null) {
            return ResultDTO.error("提供的库区为空");
        }
        var optionalWarehouseArea = warehouseAreaRepository.findById(createDTO.warehouseAreaId);
        if(optionalWarehouseArea.isEmpty()) {
            return ResultDTO.error("提供的库区不存在");
        }
        var warehouseArea = optionalWarehouseArea.get();

        var item = new CargoItem();
        item.setCargoClass(cargoClass);
        item.setWarehouseArea(warehouseArea);
        item = cargoItemRepository.save(item);
        return ResultDTO.success(item.toMap());
    }

    @GetMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO listCargoItems() {
        return ResultDTO.success(
                cargoItemRepository.findAll().stream().map(CargoItem::toMap)
        );
    }

    @GetMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO getCargoItem(@PathVariable Long id) {
        return cargoItemRepository.findById(id).map(item ->
            ResultDTO.success(item.toMap())
        ).orElseGet(() -> ResultDTO.error(
                ResponseCodeEnum.NOT_FOUND,
                "找不到id为 " + id + " 的货物物品"
        ));
    }

    @DeleteMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO deleteCargoItem(@PathVariable Long id){
        return cargoItemRepository.findById(id).map(item -> {
            var cargoClass = item.getCargoClass();
            cargoItemRepository.delete(item);
            return ResultDTO.success(item.toMap());
        }).orElseGet(() -> ResultDTO.error(
                ResponseCodeEnum.NOT_FOUND,
                "找不到id为 " + id + " 的货物物品"
        ));
    }

    @PutMapping("/{id}/waiting-on-board")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO updateStateToInBoundWaiting(@PathVariable Long id) {
        var response = cargoItemService.changeState(id, CargoItem.StateEnum.ON_BOARD_WAITING);
        if(response == 404) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "找不到id为 " + id + " 的货物。");
        } else {
            return ResultDTO.success();
        }
    }
}
