package op.warehouse.backend.controller;

import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.entity.CargoType;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.entity.Warehouse;
import op.warehouse.backend.repository.CargoTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cargo-types")
public class CargoTypeController {
    @Autowired
    CargoTypeRepository cargoTypeRepository;

    @GetMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO getAllCargoTypes() {
        return ResultDTO.success(cargoTypeRepository.findAllToMap());
    }

    @GetMapping("/trees")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO getAllCargoTrees() {
        return ResultDTO.success(cargoTypeRepository.findAllToTree());
    }

    @GetMapping("/{id}/trees")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO getSubTrees(@PathVariable Long id) {
        CargoType type = cargoTypeRepository.findById(id).orElse(null);
        if (type == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find cargo-type with id " + id);
        }
        return ResultDTO.success(type.toMapRecursively());
    }

    @PostMapping("/{id}/children")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO createChildType(@PathVariable Long id, @RequestBody CargoType newType) {
        CargoType type = cargoTypeRepository.findById(id).orElse(null);
        if (type == null) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "Cannot find cargo-type with id " + id);
        }
        newType.setFatherType(type);
        return ResultDTO.success(cargoTypeRepository.save(newType).toMap());
    }


    @PostMapping("/roots")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    public ResultDTO createChildType(@RequestBody CargoType newType) {
        newType.setFatherType(null);
        return ResultDTO.success(cargoTypeRepository.save(newType).toMap());
    }
}
