package luckysheet.impl;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.ISession;
import com.mendix.thirdparty.org.json.JSONObject;

public class TestEndpoint extends Endpoint {
  Set<Session> sessions = new HashSet<>();
  public static ILogNode LOG = Core.getLogger("Luckysheet");

  @Override
  public void onOpen(Session session, EndpointConfig config) {
    String sessionId = (String) config.getUserProperties().get("mxSessionId");
    ISession mxSession = Core.getSessionById(UUID.fromString(sessionId));
    String username = mxSession.getUserName();
    session.getUserProperties().put("username", username);
    sessions.add(session);
    session.addMessageHandler(new MessageHandler.Whole<String>() {
      @Override
      public void onMessage(String message) {
        String unMessage;
        try {
          unMessage = PakoGzipUtils.unCompressURI(message);

          LOG.info(username);
          LOG.info(unMessage);

          JSONObject jsonObject = new JSONObject(unMessage);

          // TODO 刷新后端文件
          // https://github.com/engalar/luckysheet-backend/blob/c1d6b6c0ba481de82d456bd6dbae18e7fe59e455/src/main/java/com/mars/ecsheet/service/impl/MessageProcess.java#L40

          for (Session iterateSession : sessions) {
            // 广播到除了发送者外的其它连接端
            if (session != iterateSession) {
              // 如果是mv,代表发送者的表格位置信息
              // https://dream-num.github.io/LuckysheetDocs/guide/operate.html#config-operation
              if ("mv".equals(jsonObject.getString("t"))) {
                iterateSession.getBasicRemote()
                    .sendText(new JSONObject(ResponseDTO.mv(username, username, unMessage)).toString());
                // 如果是切换sheet，则不发送信息
              } else if (!"shs".equals(jsonObject.getString("t"))) {
                iterateSession.getBasicRemote()
                    .sendText(new JSONObject(ResponseDTO.update(username, username, unMessage)).toString());
              }
            }
          }

          if ("test message".equals(message)) {
            try {
              session.getBasicRemote().sendText("test response:" + username);
              session.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else if ("rub".equals(message)) {
            return;
          }
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

      }
    });
  }

  @Override
  public void onClose(Session session, CloseReason closeReason) {
    LOG.info("Received onClose call with reason: " + closeReason);
    String username = (String) session.getUserProperties().get("username");
    for (Session iterateSession : sessions) {
      // 广播到除了发送者外的其它连接端
      if (session != iterateSession) {
        try {
          iterateSession.getBasicRemote()
              .sendText(new JSONObject(ResponseDTO.exit(username, username)).toString());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    sessions.remove(session);
  }
}
