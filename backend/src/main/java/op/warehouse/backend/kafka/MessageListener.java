package op.warehouse.backend.kafka;

import com.alibaba.fastjson.JSON;
import op.warehouse.backend.entity.CargoItem;
import op.warehouse.backend.service.CargoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    CargoItemService cargoItemService;

    @KafkaListener(topics = "accomplish", groupId = "testGroup")
    public void listenAccomplish(String message) {
        System.out.println("Received Accomplish message: " + message);
        Map<String, Object> m = JSON.parseObject(message, Map.class);
        Long cargoID = (Long) m.get("cargoID");
        cargoItemService.changeState(cargoID, CargoItem.StateEnum.READY);
    }

    @KafkaListener(topics = "start", groupId = "testGroup")
    public void listenStart(String message) {
        System.out.println("Received Start message: " + message);
        Map<String, Object> m = JSON.parseObject(message, Map.class);
        Long cargoID = (Long) m.get("cargoID");
        cargoItemService.changeState(cargoID, CargoItem.StateEnum.ON_BOARDING);
    }

}
