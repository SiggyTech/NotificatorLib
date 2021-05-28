package com.siggytech.utils.communication.presentation.service;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.siggytech.utils.communication.model.DataMessageModel;
import com.siggytech.utils.communication.model.EventMessageModel;
import com.siggytech.utils.communication.model.PairRegisterModel;
import com.siggytech.utils.communication.model.async.ApiManager;
import com.siggytech.utils.communication.model.async.TaskMessage;
import com.siggytech.utils.communication.presentation.MessengerHelper;
import com.siggytech.utils.communication.presentation.channel.NotificationHelper;
import com.siggytech.utils.communication.presentation.register.Siggy;
import com.siggytech.utils.communication.util.AESUtils;
import com.siggytech.utils.communication.util.Utils;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String MESSAGE_GROUP = "idGroup";
    public static final String MESSAGE_TITLE = "title";
    public static final String MESSAGE_BODY = "body";
    public static final String MESSAGE_PART = "msgpart";
    public static final String NEW_MESSAGE = "newMessage";


    @Override
    public void onNewToken(@NonNull String s) {
        new Thread(() -> {
            String a = Siggy.getDeviceToken();
            if(a!=null && !"".equals(a)) {
                ApiManager apiManager = new ApiManager();
                TaskMessage message = apiManager.setFirebaseToken(new PairRegisterModel(a, s));
                Utils.traces( "On Pair register service: " + message.getMessage());
            } else  Utils.traces( "On Pair register service: device token null");
        }).start();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Utils.traces("MyFirebaseMessagingService onMessageReceived");
        try {

            Map<String, String> data = remoteMessage.getData();

            if(data!=null){
                String title = data.get(MESSAGE_TITLE);
                String body = data.get(MESSAGE_BODY);
                String part = data.get(MESSAGE_PART);
                int idGroup = convertIdGroup(data.get(MESSAGE_GROUP));

                if(idGroup > 0 && MessengerHelper.getChatListView()!=null &&
                        MessengerHelper.getChatListView().getIdGroup() != idGroup &&
                        MessengerHelper.getChatListView().getLifecycleEvent() == Lifecycle.Event.ON_RESUME){

                        showNotification(title,getMessageBody(body, part), idGroup);
                }
            }

            if(MessengerHelper.getChatSocket()!=null){
                Utils.traces("MyFirebaseMessagingService socket is not null");

                EventMessageModel eventMessageModel = new EventMessageModel();
                eventMessageModel.setEvent("test");
                eventMessageModel.setData(new DataMessageModel());
                boolean state = MessengerHelper.getChatSocket().send(Utils.getGson().toJson(eventMessageModel));
                if(state){
                    Utils.traces("MyFirebaseMessagingService web socket still alive");
                }else {
                    Utils.traces("MyFirebaseMessagingService web socket dead, begin startListenerWebSocket");
                    MessengerHelper.getChatListView().startListenerWebSocket();
                }
            }else {
                Utils.traces("MyFirebaseMessagingService chatSocket is null");
                if(MessengerHelper.getChatListView()!=null){
                    Utils.traces("MyFirebaseMessagingService getChatListView is not null");
                    MessengerHelper.getChatListView().startListenerWebSocket();
                }else Utils.traces("MyFirebaseMessagingService getChatListView is null");
            }

        }catch (Exception e){
            Utils.traces("MyFirebaseMessagingService: "+Utils.exceptionToString(e));
        }
    }

    private String getMessageBody(String body, String part) {
        try {
            if (part != null && !"".equals(part.trim())) return AESUtils.decText(part);
            else return body;
        }catch (Exception e) {
            Utils.traces(Utils.exceptionToString(e));
        }
        return body;
    }

    private void showNotification(String title, String body, int idNotification){
        if(title!=null) {

            Intent intent = new Intent(this,MessengerReceiver.class);
            intent.putExtra(NEW_MESSAGE,idNotification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
            NotificationCompat.Builder builder = notificationHelper.getNotification(title, body,pendingIntent);
            notificationHelper.getManager().notify(idNotification, builder.build());
        }
    }


    private int convertIdGroup(String idGroup){
        int id = 0;
        try{
            if(idGroup != null){
                id = Integer.parseInt(idGroup);
            }
        }catch (Exception e){
            Utils.traces("MyFirebaseMessagingService getIdGroup "+Utils.exceptionToString(e));
        }
        return id;
    }
}
