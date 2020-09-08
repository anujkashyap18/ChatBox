package com.vt.chatbox.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vt.chatbox.ChatActivity;
import com.vt.chatbox.R;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	public MyFirebaseMessagingService() {
	}

	@Override
	public void onNewToken(@NonNull String s) {
		super.onNewToken(s);

		Log.d(getClass().getSimpleName(), "TOKEN : " + s);

	}

	@Override
	public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
		final Intent intent = new Intent(this, ChatActivity.class);

		Map<String, String> data = remoteMessage.getData();
		String title = data.get("title");
		String message = data.get("body");

		sendNotification(message, title);

	}

	public void sendNotification(String messageBody, String title) {
		Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

		String channelId = getApplicationContext().getResources().getString(R.string.app_name);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(getApplicationContext(), channelId)
						.setSmallIcon(R.drawable.ic_round_account_circle_24)
						.setContentTitle(title)
						.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
						.setAutoCancel(true)
						.setContentText(messageBody)
						.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
						.setSound(defaultSoundUri)
						.addAction(android.R.drawable.ic_menu_view, "View", pendingIntent)
						.setDefaults(NotificationCompat.DEFAULT_ALL)
						.setPriority(NotificationCompat.PRIORITY_HIGH);

		NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0, notificationBuilder.build());
	}
}
