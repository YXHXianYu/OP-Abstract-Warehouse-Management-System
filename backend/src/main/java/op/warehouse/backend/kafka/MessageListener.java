package op.warehouse.backend.kafka;

import com.alibaba.fastjson.JSON;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @KafkaListener(topics = "accomplish", groupId = "testGroup")
    public void listenAccomplish(String message) {
        System.out.println("Received Accomplish message: " + message);
        Map<String, Object> m = JSON.parseObject(message, Map.class);
        int cargoID = (int) m.get("cargoID");

    }

    @KafkaListener(topics = "start", groupId = "testGroup")
    public void listenStart(String message) {
        System.out.println("Received Start message: " + message);
        Map<String, Object> m = JSON.parseObject(message, Map.class);
        int cargoID = (int) m.get("cargoID");
    }

}
