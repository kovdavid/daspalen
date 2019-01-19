package com.github.davsx.daspalen.service.CardNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import com.github.davsx.daspalen.utils.DateUtils;

import java.util.List;
import java.util.Random;

public class CardNotificationService {

    private static final String TAG = "daspalen|CardNotify";

    private DaspalenRepository repository;
    private SettingsService settingsService;

    public CardNotificationService(DaspalenRepository repository, SettingsService settingsService) {
        this.repository = repository;
        this.settingsService = settingsService;
    }

    public void showNotification(Context context) {
        List<Card> cards = repository.getCardNotificationCandidates(5);
        if (cards.size() == 0) {
            return;
        }

        int cardIndex = new Random(System.currentTimeMillis()).nextInt(cards.size());
        Card card = cards.get(cardIndex);

        Log.i(TAG, String.format("showNotification cardId:%d lastNotificationAt:%s",
                card.getCardId(),
                DateUtils.timestampToString(card.getCardNotificationEntity().getLastNotificationAt())));

        String message = String.format("<b>Front:</b> %s<br /><b>Back:</b> %s",
                card.getFrontText(), card.getBackText());

        Intent i = new Intent(context, CardNotificationReceiver.class);
        PendingIntent newNotificationIntent = PendingIntent.getBroadcast(context, 0, i, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, DaspalenConstants.CARD_NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.mipmap.daspalen_icon)
                .setContentTitle("Card notification")
                .setStyle(new NotificationCompat.BigTextStyle(builder).bigText(Html.fromHtml(message)))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .addAction(R.drawable.ic_launcher_background, "New notification", newNotificationIntent)
                .setAutoCancel(false);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    DaspalenConstants.CARD_NOTIFICATION_CHANNEL,
                    DaspalenConstants.CARD_NOTIFICATION_CHANNEL,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            builder.setChannelId(DaspalenConstants.CARD_NOTIFICATION_CHANNEL);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());

        card.setLastNotificationAt(System.currentTimeMillis());
        repository.updateCard(card);

        CardNotificationAlarmService.resetAlarm(context, settingsService);
    }

}
