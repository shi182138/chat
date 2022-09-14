package com.example.chatt.listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
//import com.example.chatt.pojo.*;
import com.example.chatt.pojo.*;
import com.example.chatt.pojo.vo.AddMemberRequestVo;
import com.example.chatt.pojo.vo.CurrentConversationVo;
import com.example.chatt.pojo.vo.NewMessageVo;
import com.example.chatt.pojo.vo.ValidateMessageResponseVo;
import com.example.chatt.service.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class SocketServerListener {
    private Logger logger = LoggerFactory.getLogger(SocketServerListener.class);
    @Autowired
    private SingleMessageService singleMessageService;
    @Autowired
    private GroupMessageService groupMessageService;
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private ValidateMessageService validateMessageService;
    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private SocketIOServer socketIOServer;
//    public static ConcurrentMap<String,SocketIOClient> socketIOClientMap = new ConcurrentHashMap<>();

    @OnConnect
    public void onConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
//         System.out.println("客户端唯一标识为：" + client.getSessionId());
//        logger.info("链接开启，urlParams：{}", urlParams);
//        String mac = client.getHandshakeData().getSingleUrlParam("mac");
//        socketIOClientMap.put("mac",client);
//        logger.info("已连接mac=",mac);
    }
    @OnDisconnect
    public void disconnect(SocketIOClient client){
        logger.info("eventOnDisConnect ---> 客户端唯一标识为：{}", client.getSessionId());
    }

    //加入聊天房间
    @OnEvent("join")
    public void join(SocketIOClient client, CurrentConversationVo currentConversationVo) {
        System.out.println("---------------->" + currentConversationVo.getRoomId());
        client.joinRoom(currentConversationVo.getRoomId());
    }
    @OnEvent("sendNewMessage")
    public void sendSingleMessage(SocketIOClient client, NewMessageVo newMessageVo){
        if (newMessageVo.getConversationType().equals("FRIEND")){
            SingleMessage singleMessage = new SingleMessage();
            BeanUtils.copyProperties(newMessageVo,singleMessage);
            singleMessage.setSenderId(newMessageVo.getSenderId());
            singleMessageService.addNewSingleMessage(singleMessage);
        }else if (newMessageVo.getConversationType().equals("GROUP")) {
            GroupMessage groupMessage = new GroupMessage();
            BeanUtils.copyProperties(newMessageVo,groupMessage);
            groupMessage.setSenderId(newMessageVo.getSenderId());
            groupMessageService.addNewGroupMessage(groupMessage);
        }
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(newMessageVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveMessage",newMessageVo);
            }
        }
    }
    @OnEvent("addMember")
    public void addMember(SocketIOClient socketIOClient, AddMemberRequestVo addMemberRequestVo) {
//         Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(addMemberRequestVo.getRoomIds().get(0)).getClients();
//         for (SocketIOClient client : clients) {
//             if (client != socketIOClient) {
//                 client.sendEvent("receiveAddMember", addMemberRequestVo.getMessage());
//             }
//         }
         for (int i=0; i<addMemberRequestVo.getRoomIds().size(); i++) {
             Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(addMemberRequestVo.getRoomIds().get(i)).getClients();
             for (SocketIOClient item : clients) {
                 if (item != socketIOClient) {
                     item.sendEvent("forceCramGroup");
                 }
             }
         }

    }

    @OnEvent("sendValidateMessage")
    public void sendValidateMessage(SocketIOClient socketIOClient, ValidateMessage validateMessage) {
        logger.info("sendValidateMessage ---> validateMessage：{}", validateMessage);
        ValidateMessage addValidateMessage = validateMessageService.addValidateMessage(validateMessage);
        if (addValidateMessage != null) {
            Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(validateMessage.getRoomId()).getClients();
            for (SocketIOClient client : clients) {
                if (client != socketIOClient) {
                    client.sendEvent("receiveValidateMessage",validateMessage);
                }
            }
        }
    }

    @OnEvent("sendAgreeFriendValidate")
    public void sendAgreeFriendValidate(SocketIOClient socketIOClient, ValidateMessageResponseVo validateMessageResponse) {
        System.out.println(validateMessageResponse);
        User_Friend user_friend = new User_Friend();
        user_friend.setUserM(new ObjectId(validateMessageResponse.getSenderId()));
        user_friend.setUserY(new ObjectId(validateMessageResponse.getReceiverId()));
        friendsService.addFriend(user_friend);
        // 用户同意加好友之后改变验证消息的状态
        validateMessageService.changeFriendValidateNewsStatus(validateMessageResponse.getId(),1);
        String roomId = validateMessageResponse.getRoomId();
        String receiverId = validateMessageResponse.getReceiverId();
        String senderId = validateMessageResponse.getSenderId();
        String senderRoomId = roomId.replaceAll(receiverId,senderId);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(senderRoomId).getClients();
        for (SocketIOClient client : clients) {
            if (client != socketIOClient) {
                client.sendEvent("receiveAgreeFriendValidate",validateMessageResponse);
            }
        }
    }

    //同意进群
    @OnEvent("sendAgreeGroupValidate")
    public void sendAgreeGroupValidate(SocketIOClient client, ValidateMessageResponseVo validateMessage) {
        //添加群成员
        groupUserService.addNewGroupUser(validateMessage);
        //改变群验证消息的状态为1
        validateMessageService.changeFriendValidateNewsStatus(validateMessage.getId(), 1);
        //========================
        String roomId = validateMessage.getRoomId();
        String receiverId = validateMessage.getReceiverId();
        String senderId = validateMessage.getSenderId();
        String senderRoomId = roomId.replaceAll(receiverId, senderId); //把接受者id替换为发送者id回到同意加好友的这方房间去更新我的群列表
        //应该通知 请求加群的人 去更新他的群列表
        //这里应该换回系统通知，因为客户端房间里只加入了系统通知，新加的群号还没加入到房间
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(senderRoomId).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveAgreeGroupValidate", validateMessage); //只通知发送者房间
            }
        }
    }
    //解散群或者退出群聊，则转发通知与这群关联的所有在线客户端 去更新我的群列表和最近会话中的群列表
    @OnEvent("sendQuitGroup")
    public void sendQuitGroup(SocketIOClient client, CurrentConversationVo conversationVo) {
        logger.info("sendQuitGroup ---> conversationVo：{}", conversationVo);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients(); //实际上同一房间只有2个客户端
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveQuitGroup", conversationVo);
            }
        }
    }

    //删除好友通知
    @OnEvent("sendDelGoodFriend")
    public void sendDelGoodFriend(SocketIOClient client, CurrentConversationVo conversationVo) {
        System.out.println("sendDelGoodFriend");
        System.out.println(conversationVo);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("receiveDelGoodFriend", conversationVo);
            }
        }
    }
    @OnEvent("apply")
    public void apply(SocketIOClient client,CurrentConversationVo conversationVo) {
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("apply", conversationVo);
            }
        }
    }
    @OnEvent("reply")
    public void reply(SocketIOClient client,CurrentConversationVo conversationVo) {
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("reply", conversationVo);
            }
        }
    }
    @OnEvent("1v1answer")
    public void answer(SocketIOClient client,CurrentConversationVo conversationVo) {
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1answer", conversationVo);
            }
        }
    }
    @OnEvent("1v1ICE")
    public void ICE(SocketIOClient client,CurrentConversationVo conversationVo) {
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1ICE", conversationVo);
            }
        }
    }
    @OnEvent("1v1offer")
    public void offer(SocketIOClient client,CurrentConversationVo conversationVo) {
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1offer", conversationVo);
            }
        }
    }
    @OnEvent("1v1hangup")
    public void hangup(SocketIOClient client,CurrentConversationVo conversationVo) {
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(conversationVo.getRoomId()).getClients();
        for (SocketIOClient item : clients) {
            if (item != client) {
                item.sendEvent("1v1hangup", conversationVo);
            }
        }
    }

}
