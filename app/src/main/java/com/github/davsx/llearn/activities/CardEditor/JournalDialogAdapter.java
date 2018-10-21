package com.github.davsx.llearn.activities.CardEditor;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.persistence.entity.JournalEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JournalDialogAdapter extends RecyclerView.Adapter<JournalDialogAdapter.JournalViewHolder> {

    private List<JournalEntity> journals;
    private LayoutInflater layoutInflater;

    JournalDialogAdapter(Context context, List<JournalEntity> journals) {
        this.journals = journals;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_card_editor_journal_element, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntity journal = journals.get(position);
        if (journal == null) return;

        Long timestamp = journal.getTimestamp();
        Integer answer = journal.getAnswer();
        Integer cardType = journal.getCardType();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(timestamp));

        holder.textViewCardType.setText(cardType.equals(LLearnConstants.CARD_TYPE_LEARN) ? "L" : "R");
        holder.textViewDateTime.setText(dateString);

        if (answer.equals(LLearnConstants.JOURNAL_ANSWER_GOOD)) {
            holder.textViewAnswer.setText("GOOD");
            holder.textViewAnswer.setBackgroundResource(R.color.colorGreen);
            holder.textViewAnswer.setTextColor(Color.WHITE);
        } else if (answer.equals(LLearnConstants.JOURNAL_ANSWER_OK)) {
            holder.textViewAnswer.setText("OK");
            holder.textViewAnswer.setBackgroundResource(R.color.colorOrange);
            holder.textViewAnswer.setTextColor(Color.WHITE);
        } else {
            holder.textViewAnswer.setText("BAD");
            holder.textViewAnswer.setBackgroundResource(R.color.colorAccent);
            holder.textViewAnswer.setTextColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    class JournalViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCardType;
        private TextView textViewDateTime;
        private TextView textViewAnswer;

        JournalViewHolder(View itemView) {
            super(itemView);

            textViewCardType = itemView.findViewById(R.id.textview_card_type);
            textViewDateTime = itemView.findViewById(R.id.textview_datetime);
            textViewAnswer = itemView.findViewById(R.id.textview_answer);
        }
    }

}
