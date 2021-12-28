package demo.jack.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    public static final String TAG = FCMService.class.getSimpleName()+"My";
    private String CHANNEL_ID = "Coder";

    public FCMService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /**檢查手機版本是否支援通知；若支援則新增"頻道"*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "DemoCode", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
        Map<String,String> s = remoteMessage.getData();
        String fragment = s.get("fragment");
        int fragmentId = getResources().getIdentifier(fragment,"id",getPackageName());
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.main_navigation)
                .setDestination(fragmentId)
                .createPendingIntent();

        /**建置通知欄位的內容*/
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(FCMService.this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(s.get("title"))
                .setContentText(s.get("body"))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent);

        /**發出通知*/
        NotificationManagerCompat notificationManagerCompat
                = NotificationManagerCompat.from(FCMService.this);
        notificationManagerCompat.notify(1,builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "裝置Token: "+s);
    }
}