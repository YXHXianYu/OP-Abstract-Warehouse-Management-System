package op.warehouse.backend.controller;

import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.dto.CreateCargoClassDTO;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.entity.CargoClass;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.repository.CargoClassRepository;
import op.warehouse.backend.repository.CargoTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cargo-classes")
public class CargoClassController {

    @Autowired
    private CargoClassRepository cargoClassRepository;

    @Autowired
    private CargoTypeRepository cargoTypeRepository;

    @PostMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO createCargoClass( @RequestBody CreateCargoClassDTO createDTO) {
        if (createDTO.getName() == null || createDTO.getName().isBlank()) {
            return ResultDTO.error("名称不能为空");
        }
        var optionalCargoType = cargoTypeRepository.findById(createDTO.getCargoTypeId());
        if (optionalCargoType.isEmpty()) {
            return ResultDTO.error("提供的商品类别为空或者不存在");
        } else {
            var cargoType = optionalCargoType.get();
            var target = new CargoClass();
            target.setName(createDTO.getName());
            target.setDescription(createDTO.getDescription());
            target.setCargoType(cargoType);
            target = cargoClassRepository.save(target);
            return ResultDTO.success(target.toMap());
        }
    }

    @GetMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO getCargoClassList() {
        return ResultDTO.success(cargoClassRepository.findAll().stream().map(CargoClass::toMap));
    }

    @GetMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO getCargoClass(@PathVariable Long id) {
        var optionalCargoClass = cargoClassRepository.findById(id);
        return optionalCargoClass.map(
                item -> ResultDTO.success(item.toMap()
                )
        ).orElseGet(
                () -> ResultDTO.error(
                    ResponseCodeEnum.NOT_FOUND,
                    "找不到id为 " + id + " 的商品类。"
        ));
    }

    @DeleteMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO deleteCargoClass(@PathVariable Long id) {
        return cargoClassRepository.findById(id).map(
                item -> {
                    var itemCount = item.getCargoItems().size();
                    if(itemCount > 0) {
                        return ResultDTO.error("id为 " + id + " 的商品类含有 " + itemCount + " 件商品，请将其移除后重试。");
                    }
                    cargoClassRepository.delete(item);
                    return ResultDTO.success("成功删除id为 " + id + " 的商品类。");
                }
        ).orElseGet(
                () -> ResultDTO.error(
                        ResponseCodeEnum.NOT_FOUND,
                        "找不到id为 " + id + " 的商品类。"
                ));
    }
}
