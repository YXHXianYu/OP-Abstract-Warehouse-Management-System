//package op.warehouse.backend.controller;
//
//import op.warehouse.backend.dto.ResultDTO;
//import op.warehouse.backend.entity.InOutOrder;
//import op.warehouse.backend.service.CargoItemService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//public class TestController {
//
//    @Autowired
//    CargoItemService cargoItemService;
//
//    @GetMapping("/test")
//    public ResultDTO test(Long id) {
//        List<InOutOrder> inOutOrderList = cargoItemService.getInOutOrdersByCargoItemID(id);
//        return ResultDTO.success(inOutOrderList);
//    }
//
//}
