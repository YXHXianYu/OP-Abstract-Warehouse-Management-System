package op.warehouse.backend.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.entity.*;
import op.warehouse.backend.repository.*;
import op.warehouse.backend.service.UserService;
import op.warehouse.backend.util.SecurityUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/in-out-orders")
@Slf4j
public class InOutOrderController {
    @Autowired
    private InOutOrderRepository inOutOrderRepository;

    @Autowired
    private CargoClassRepository cargoClassRepository;

    @Autowired
    private CargoItemRepository cargoItemRepository;

    @Autowired
    private WarehouseAreaRepository warehouseAreaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WarehouseManagerRepository warehouseManagerRepository;

    @Getter
    @Setter
    public static class CreateInOutOrderRequestDTO {
        @Getter
        @Setter
        public static class InOutOrderCargoClassDTO {
            private long cargoClassId;
            private int amount;
        }
        private Boolean isOutOrder;
        private Long warehouseAreaId;
        private String description;
        private List<InOutOrderCargoClassDTO> cargoClassList;
    }

    @GetMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO getAll() {
        return ResultDTO.success(inOutOrderRepository.findAll().stream().map(InOutOrder::toMap));
    }

    @GetMapping("/{id}")
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    ResultDTO getOne(@PathVariable Long id) {
        return inOutOrderRepository.findById(id)
                .map(item -> ResultDTO.success(item.toMap()))
                .orElseGet(() -> ResultDTO.error(
                        ResponseCodeEnum.NOT_FOUND,
                        "id为 " + id + " 的进出库单不存在。"
                ));
    }

    @PostMapping
    @RequiresRoleType(RoleType.WAREHOUSE_MANAGER)
    @Transactional
    ResultDTO createInOutOrder(@RequestBody CreateInOutOrderRequestDTO requestDTO) {
        log.info(String.valueOf(requestDTO.isOutOrder));
        if(requestDTO.isOutOrder) { //出库
            var inOutOrder = new InOutOrder();
            inOutOrder.setIsOutOrder(requestDTO.isOutOrder);
            inOutOrder.setDescription(requestDTO.description);
            inOutOrder.setCargoItemList(new ArrayList<>());
            inOutOrder.setState(InOutOrder.StateEnum.NOT_ASSIGNED);
            User user = SecurityUtilities.getAuthUser();
            inOutOrder.setCreatedUser((WarehouseManager) user);
            for (var cargoClassDTO : requestDTO.cargoClassList) { //遍历请求中的每一个class
                //先判断class是否存在
                var optionalCargoClass = cargoClassRepository.findById(cargoClassDTO.cargoClassId);
                // 检测商品类是否存在
                if(optionalCargoClass.isEmpty()) {
                    return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id为 " + cargoClassDTO.cargoClassId + " 的商品类不存在。");
                }
                //存在则创建变量
                var cargoClass = optionalCargoClass.get();
                var example = new CargoItem();
                example.setCargoClass(cargoClass);
                var cargoItemList = cargoItemRepository.findAll();
                List<CargoItem> readyItemList = new ArrayList<>();
                for(var item : cargoItemList) {
                    log.warn(item.toString());
                    log.warn(String.valueOf(item.getCargoClass().getId()));
                    if (item.getCargoClass().getId().equals(cargoClass.getId()) && item.getState().equals(String.valueOf(CargoItem.StateEnum.READY))) {
                        log.warn("通过");
                        readyItemList.add(item);
                    }
                }
                var readyCargoItemList = readyItemList;
                //索取的商品数量多于实际存在的商品
                if (cargoClassDTO.amount > readyCargoItemList.size()) {
                    log.info(cargoClass.toString());
                    log.info(cargoClass.getCargoItems().toString());
                    log.info(readyCargoItemList.toString());
                    return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id为 " + cargoClassDTO.cargoClassId + " 的商品类存货数量不足：需要" + cargoClassDTO.amount + "，实际拥有" + readyCargoItemList.size() + "。");
                }
                //挑选并锁定商品
                for(int i = 0; i < cargoClassDTO.amount; i++) {
                    inOutOrder.getCargoItemList().add(readyCargoItemList.get(i));
                }
            }
            //处理收集到的货物
            for( var item : inOutOrder.getCargoItemList() ) {
                //更新状态
                item.setState(String.valueOf(CargoItem.StateEnum.DOWN_BOARDING));
                //保存
                cargoItemRepository.save(item);
            }
            inOutOrder = inOutOrderRepository.save(inOutOrder);
            return ResultDTO.success("成功添加出货单", inOutOrder.toMap());



        } else { //入库
            var optionalWarehouseArea = warehouseAreaRepository.findById(requestDTO.warehouseAreaId);
            if(optionalWarehouseArea.isEmpty()) {
                return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id为 " + requestDTO.warehouseAreaId + " 的库区不存在。");
            }
            var warehouseArea = optionalWarehouseArea.get();
            long amount = 0;
            for (var cargoClassDTO : requestDTO.cargoClassList) {    //遍历请求中的每一个class
                //判断是否存在
                if(!cargoClassRepository.existsById(cargoClassDTO.cargoClassId)) {
                    return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id为 " + cargoClassDTO.cargoClassId + " 的商品类不存在。");
                }
                //累加数量
                amount += cargoClassDTO.amount;
            }
            //判断库存容量
            var itemCount = warehouseArea.getCargoItemList().size();
            if (warehouseArea.getCapacity() < amount + itemCount) {
                return ResultDTO.error("当前库区的剩余容量不足以存放所有货品：需要 " + amount + "，剩余 " + (warehouseArea.getCapacity() - itemCount) + "。");
            }
            //生成并添加货物
            var inOutOrder = new InOutOrder();
            inOutOrder.setIsOutOrder(requestDTO.isOutOrder);
            inOutOrder.setDescription(requestDTO.description);
            inOutOrder.setCargoItemList(new ArrayList<>());
            inOutOrder.setState(InOutOrder.StateEnum.NOT_ASSIGNED);
            User user = SecurityUtilities.getAuthUser();
            inOutOrder.setCreatedUser((WarehouseManager) user);
            for (var cargoClassDTO : requestDTO.cargoClassList) { //遍历请求中的每一个class
                //获取class
                var cargoClass = cargoClassRepository.findById(cargoClassDTO.cargoClassId).get();
                for(int i = 0; i < cargoClassDTO.amount; i++) {
                    var generatedCargoItem = new CargoItem();
                    generatedCargoItem.setCargoClass(cargoClass);
                    generatedCargoItem.setWarehouseArea(warehouseArea);
                    generatedCargoItem.setState(String.valueOf(CargoItem.StateEnum.ON_BOARDING));
                    generatedCargoItem = cargoItemRepository.save(generatedCargoItem);
                    inOutOrder.getCargoItemList().add(generatedCargoItem);
                }
                //挑选并锁定商品
            }
            inOutOrder = inOutOrderRepository.save(inOutOrder);
            return ResultDTO.success("成功添加进货单", inOutOrder.toMap());
        }
    }

    @Getter
    @Setter
    private static class UpdateDTO {
        private Long pickerUserId;
        private Long state;
    }

    @PatchMapping("/{id}")
    ResultDTO update(@PathVariable Long id, @RequestBody UpdateDTO updateDTO) {
        var optionalInOutOrder = inOutOrderRepository.findById(id);
        if(optionalInOutOrder.isEmpty()) {
            return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id为 " + id + " 的进出库单不存在。");
        }
        var inOutOrder = optionalInOutOrder.get();
        //update to 1
        if (updateDTO.state == 0) {
            for(var item : inOutOrder.getCargoItemList()) {
                if(inOutOrder.getIsOutOrder()){
                    item.setState(String.valueOf(CargoItem.StateEnum.DOWN_BOARDING));
                } else {
                    item.setState(String.valueOf(CargoItem.StateEnum.ON_BOARDING));
                }
                cargoItemRepository.save(item);
            }
            inOutOrder.setState(InOutOrder.StateEnum.NOT_ASSIGNED);
        } else if(updateDTO.state == 1) {
            if (updateDTO.pickerUserId == null) {
                return ResultDTO.error(ResponseCodeEnum.NOT_ACCEPTABLE, "应当指定拣货员来完成任务分配动作。");
            }
            var optionalUser = warehouseManagerRepository.findById(updateDTO.pickerUserId);
            if(optionalUser.isEmpty()) {
                return ResultDTO.error(ResponseCodeEnum.NOT_FOUND, "id为 " + id + " 的拣货员不存在。");
            }
            var user = optionalUser.get();
            inOutOrder.setPickerUser(user);
            for(var item : inOutOrder.getCargoItemList()) {
                if(inOutOrder.getIsOutOrder()){
                    item.setState(String.valueOf(CargoItem.StateEnum.DOWN_BOARDING));
                } else {
                    item.setState(String.valueOf(CargoItem.StateEnum.ON_BOARDING));
                }
                cargoItemRepository.save(item);
            }
            inOutOrder.setState(InOutOrder.StateEnum.WAITING);
        } else if (updateDTO.state == 2) {
            for(var item : inOutOrder.getCargoItemList()) {
                if(inOutOrder.getIsOutOrder()){
                    item.setState(String.valueOf(CargoItem.StateEnum.DOWN_BOARDING));
                } else {
                    item.setState(String.valueOf(CargoItem.StateEnum.ON_BOARDING));
                }
                cargoItemRepository.save(item);
            }
            inOutOrder.setState(InOutOrder.StateEnum.IN_PROCESS);
        } else if (updateDTO.state == 3) {
            for(var item : inOutOrder.getCargoItemList()) {
                if(inOutOrder.getIsOutOrder()){
                    item.setState(String.valueOf(CargoItem.StateEnum.LEAVED));
                } else {
                    item.setState(String.valueOf(CargoItem.StateEnum.READY));
                }
                cargoItemRepository.save(item);
            }
            inOutOrder.setState(InOutOrder.StateEnum.FINISHED);
        } else {
            return ResultDTO.error(ResponseCodeEnum.NOT_ACCEPTABLE, "状态值 " + updateDTO.state + " 不合法，应当为0/1/2/3。");
        }
        inOutOrder = inOutOrderRepository.save(inOutOrder);
        return ResultDTO.success(inOutOrder.toMap());
    }
}
