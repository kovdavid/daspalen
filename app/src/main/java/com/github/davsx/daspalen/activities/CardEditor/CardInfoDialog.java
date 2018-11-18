package com.github.davsx.daspalen.activities.CardEditor;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.entity.CardNotificationEntity;
import com.github.davsx.daspalen.persistence.entity.CardQuizEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CardInfoDialog {

    private Context context;
    private Card card;

    private CardInfoDialog(Context context, Card card) {
        this.context = context;
        this.card = card;
    }

    public static void show(Context context, Card card) {
        new CardInfoDialog(context, card).showDialog();
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_card_info, null);

        prepareTableLayoutCardInfo(inflater,
                (TableLayout) dialogView.findViewById(R.id.table_layout_card_info));
        prepareTableLayoutQuizInfo(inflater,
                (TableLayout) dialogView.findViewById(R.id.table_layout_quiz_info));
        prepareTableLayoutNotificationInfo(inflater,
                (TableLayout) dialogView.findViewById(R.id.table_layout_notification_info));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(dialogView);
        builder.show();
    }

    private void prepareTableLayoutCardInfo(LayoutInflater inflater, TableLayout table) {
        CardEntity entity = card.getCardEntity();
        table.addView(createTableRowView(inflater, "Id", Long.toString(entity.getCardId())));
        table.addView(createTableRowView(inflater, "Front", entity.getFrontText()));
        table.addView(createTableRowView(inflater, "Back", entity.getBackText()));
        table.addView(createTableRowView(inflater, "ImageHash", entity.getImageHash()));
        table.addView(createTableRowView(inflater, "Enabled", Boolean.toString(entity.getEnabled())));
        table.addView(createTableRowView(inflater, "Local Version", Integer.toString(entity.getLocalVersion())));
        table.addView(createTableRowView(inflater, "Server Version", Integer.toString(entity.getServerVersion())));
        table.addView(createTableRowView(inflater, "Created At", timestampToString(entity.getCreatedAt())));
        table.addView(createTableRowView(inflater, "Updated At", timestampToString(entity.getUpdatedAt())));
    }

    private void prepareTableLayoutQuizInfo(LayoutInflater inflater, TableLayout table) {
        CardQuizEntity entity = card.getCardQuizEntity();
        table.addView(createTableRowView(inflater, "Type", entity.getQuizTypeString()));
        table.addView(createTableRowView(inflater, "Type Changes", Integer.toString(entity.getQuizTypeChanges())));
        table.addView(createTableRowView(inflater, "Learn Score", Integer.toString(entity.getLearnScore())));
        table.addView(createTableRowView(inflater, "Learn Updated", timestampToString(entity.getLastLearnQuizAt())));
        table.addView(createTableRowView(inflater, "Last Review", timestampToString(entity.getLastReviewAt())));
        table.addView(createTableRowView(inflater, "Next Review", timestampToString(entity.getNextReviewAt())));
        table.addView(createTableRowView(inflater, "Review Multiplier",
                Double.toString(entity.getReviewIntervalMultiplier())));
        table.addView(createTableRowView(inflater, "Good Reviews", Integer.toString(entity.getGoodReviews())));
        table.addView(createTableRowView(inflater, "Bad Reviews", Integer.toString(entity.getBadReviews())));
        table.addView(createTableRowView(inflater, "Local Version", Integer.toString(entity.getLocalVersion())));
        table.addView(createTableRowView(inflater, "Server Version", Integer.toString(entity.getServerVersion())));
        table.addView(createTableRowView(inflater, "Created At", timestampToString(entity.getCreatedAt())));
        table.addView(createTableRowView(inflater, "Updated At", timestampToString(entity.getUpdatedAt())));
    }

    private void prepareTableLayoutNotificationInfo(LayoutInflater inflater, TableLayout table) {
        CardNotificationEntity entity = card.getCardNotificationEntity();
        table.addView(createTableRowView(inflater, "Last Notification",
                timestampToString(entity.getLastNotificationAt())));
        table.addView(createTableRowView(inflater, "Enabled", Boolean.toString(entity.getEnabled())));
        table.addView(createTableRowView(inflater, "Local Version", Integer.toString(entity.getLocalVersion())));
        table.addView(createTableRowView(inflater, "Server Version", Integer.toString(entity.getServerVersion())));
        table.addView(createTableRowView(inflater, "Created At", timestampToString(entity.getCreatedAt())));
        table.addView(createTableRowView(inflater, "Updated At", timestampToString(entity.getUpdatedAt())));
    }

    private TableRow createTableRowView(LayoutInflater inflater, String title, String value) {
        TableRow view = (TableRow) inflater.inflate(R.layout.dialog_card_info_table_row, null);
        ((TextView) view.findViewById(R.id.text_view_row_title)).setText(title);
        ((TextView) view.findViewById(R.id.text_view_row_value)).setText(value);
        return view;
    }

    private String timestampToString(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(cal.getTime());
    }

}
