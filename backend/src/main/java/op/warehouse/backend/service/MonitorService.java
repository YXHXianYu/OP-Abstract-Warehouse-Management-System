package op.warehouse.backend.service;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import op.warehouse.backend.entity.CargoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/webSocketServer/{uID}/{ioID}")
public class MonitorService {


//    // 这里使用静态，让 service 属于类
    private static CargoItemService cargoItemService;

    // 注入的时候，给类的 service 注入
    @Autowired
    public void setEvaluateServer(CargoItemService cargoItemService) {
        MonitorService.cargoItemService = cargoItemService;
    }


//    private static String projectPath; //项目地址
//
//    @Value("${path.projectPath}") public void setProjectPath(String projectPath) {
//        MonitorService.projectPath = projectPath;
//    }

    /**
     * 与客户端的连接会话，需要通过他来给客户端发消息
     */
    private Session session;

    /**
     * 当前用户ID
     */
    private String uID;

    private String ioID;

    /**
     *  concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     *  虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    private static CopyOnWriteArraySet<MonitorService> webSockets =new CopyOnWriteArraySet<>();

    /**
     *用来存在线连接用户信息
     */
    private static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();

    /**
     * 连接成功方法
     * @param session 连接会话
     * @param uID 用户编号
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uID") String uID, @PathParam("ioID") String ioID){
        try {
            this.session = session;

            List<CargoItem> cargos = cargoItemService.getCargoItemsByOrderID(Long.parseLong(ioID));
            String msg = JSON.toJSONString(cargos);
            session.getAsyncRemote().sendText(msg);

            this.uID = uID;
            this.ioID = ioID;
            webSockets.add(this);
            sessionPool.put(uID, session);
            log.info("【websocket消息】 用户："+ uID + " 连接成功...");
        } catch (Exception e) {
            log.error("---------------WebSocket连接异常---------------");
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose(){
        try {
            webSockets.remove(this);
            sessionPool.remove(this.uID);
            log.info("【websocket消息】 用户："+ this.uID + " 断开连接...");
        } catch (Exception e) {
            log.error("---------------WebSocket断开异常---------------");
        }
    }

    @OnMessage
    public void onMessage(@PathParam("uID") String userId, @RequestBody String body){
        try {
//
//            session.getAsyncRemote().sendText(sb.toString());
        } catch (Exception e) {
            log.error("---------------WebSocket消息异常---------------");
            e.printStackTrace();
        }
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    public static void sendInfo(String ioID, String message) {
        for (MonitorService webSocket : webSockets) {
            try {
                if (ioID.equals(webSocket.ioID)) {
                    webSocket.session.getAsyncRemote().sendText(message);
                    log.info("【websocket消息】向{}发送消息：" + message, webSocket.uID);
                }
            } catch (Exception e) {
                log.error("【websocket消息】广播消息异常");
            }
        }
    }
}

