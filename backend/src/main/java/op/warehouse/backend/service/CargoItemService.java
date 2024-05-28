package op.warehouse.backend.service;

import op.warehouse.backend.entity.CargoItem;
import op.warehouse.backend.entity.InOutOrder;
import op.warehouse.backend.kafka.MessageSender;
import op.warehouse.backend.repository.CargoItemRepository;
import op.warehouse.backend.repository.InOutOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoItemService {
    @Autowired
    CargoItemRepository cargoItemRepository;

    @Autowired
    InOutOrderRepository inOutOrderRepository;

    @Autowired
    MessageSender messageSender;

    public int changeState(Long cargoItemId, CargoItem.StateEnum state) {
        var optionalCargoItem = cargoItemRepository.findById(cargoItemId);
        if(optionalCargoItem.isEmpty()) {
            return 404;
        }
        var item = optionalCargoItem.get();
        item.setState(String.valueOf(state));
        cargoItemRepository.save(item);
        // 如果同一个进出库单的所有货物均已经出入库完毕，那么修改其状态为完成
        if(state.equals(CargoItem.StateEnum.READY) || state.equals(CargoItem.StateEnum.LEAVED)){
            var orderList = item.getInOutOrderList();
            var isOutOrder = state.equals(CargoItem.StateEnum.LEAVED);
            orderList.forEach((order) -> {
                if(order.getIsOutOrder().equals(isOutOrder)) {
                    boolean hasNotFinishItem = order.getCargoItemList().stream().anyMatch((cargoItem -> {
                        var itemState = CargoItem.StateEnum.valueOf(cargoItem.getState());
                        return !(
                                itemState.equals(CargoItem.StateEnum.READY)
                                ||
                                itemState.equals(CargoItem.StateEnum.LEAVED)
                        );
                    }));
                    if(!hasNotFinishItem) {
                        order.setState(InOutOrder.StateEnum.FINISHED);
                        inOutOrderRepository.save(order);
                    }
                }
            });
        }
        if(state.equals(CargoItem.StateEnum.ON_BOARD_WAITING)) {
            messageSender.addWaitMission(item.getId());
        }
        return 200;
    }
}
