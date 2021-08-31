package io.talkplus.sample;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.talkplus.TalkPlus;

public class SampleFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().containsKey("talkplus")) {
            TalkPlus.processFirebaseCloudMessagingData(remoteMessage.getData());
        }
    }
}
