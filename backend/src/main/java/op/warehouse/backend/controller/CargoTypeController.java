package op.warehouse.backend.controller;

import op.warehouse.backend.entity.CargoType;
import op.warehouse.backend.repository.CargoTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cargo-types")
public class CargoTypeController {
    @Autowired
    CargoTypeRepository cargoTypeRepository;


}
