package op.warehouse.backend.kafka;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void addWaitMission(Long cargoID) {
        Map<String, Object> m = new HashMap<>();
        m.put("cargoID", cargoID);
        //将m用fastjson转成json文本
        String message = JSON.toJSONString(m);

        kafkaTemplate.send("wait", message);
    }

}
