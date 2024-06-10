package op.warehouse.backend.kafka;

import com.alibaba.fastjson.JSON;
import op.warehouse.backend.entity.CargoItem;
import op.warehouse.backend.entity.InOutOrder;
import op.warehouse.backend.service.CargoItemService;
import op.warehouse.backend.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    CargoItemService cargoItemService;

    @KafkaListener(topics = "accomplish-test", groupId = "testGroup")
    public void listenAccomplish(String message) {
        System.out.println("Received Accomplish message: " + message);
        Map<String, Object> m = JSON.parseObject(message, Map.class);
        Long cargoID = ((Number) m.get("cargoID")).longValue();
        Long ioID = ((Number) m.get("ioID")).longValue();
        int state = ((Number) m.get("state")).intValue();

        Map<String, Object> msg = new HashMap<>();
        msg.put("cargoID", cargoID);

        if(state == 4) {
            cargoItemService.changeState(cargoID, CargoItem.StateEnum.READY);
            cargoItemService.checkAccomplish(ioID);
            msg.put("state", CargoItem.StateEnum.READY);
        } else if (state == 3) {
            cargoItemService.changeState(cargoID, CargoItem.StateEnum.ON_BOARDING);
            msg.put("state", CargoItem.StateEnum.ON_BOARDING);
        } else if (state == 9) {
            cargoItemService.changeState(cargoID, CargoItem.StateEnum.LOADING);
            msg.put("state", CargoItem.StateEnum.LOADING);
        } else if (state == 10) {
            cargoItemService.changeState(cargoID, CargoItem.StateEnum.TRANSPORTING);
            msg.put("state", CargoItem.StateEnum.TRANSPORTING);
        } else if (state == 11) {
            cargoItemService.changeState(cargoID, CargoItem.StateEnum.SHELVING);
            msg.put("state", CargoItem.StateEnum.SHELVING);
        }

        MonitorService.sendInfo(String.valueOf(ioID), JSON.toJSONString(msg));
    }

//    @KafkaListener(topics = "start", groupId = "testGroup")
//    public void listenStart(String message) {
//        System.out.println("Received Start message: " + message);
//        Map<String, Object> m = JSON.parseObject(message, Map.class);
//        Long cargoID = ((Number) m.get("cargoID")).longValue();
//        cargoItemService.changeState(cargoID, CargoItem.StateEnum.ON_BOARDING);
//
//        List<InOutOrder> inOutOrderList = cargoItemService.getInOutOrdersByCargoItemID(cargoID);
//        Map<String, Object> msg = new HashMap<>();
//        msg.put("cargoID", cargoID);
//        msg.put("state", CargoItem.StateEnum.ON_BOARDING);
//        for (InOutOrder inOutOrder : inOutOrderList) {
//            MonitorService.sendInfo(String.valueOf(inOutOrder.getId()), JSON.toJSONString(msg));
//        }
//    }

}
