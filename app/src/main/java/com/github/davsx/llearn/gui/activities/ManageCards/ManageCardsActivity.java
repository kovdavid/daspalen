package com.github.davsx.llearn.gui.activities.ManageCards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.data.ManageCards.ManageCardsDataProvider;
import com.github.davsx.llearn.gui.activities.BaseActivity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

public class ManageCardsActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.create_card_button)
    FloatingActionButton createCardButton;
    private ManageCardsDataProvider dataProvider;
    private ManageCardsAdapter adapter;

    public static final String BUNDLE_ID_CARD_KEY = "ID_CARD";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);
        ButterKnife.bind(this);

        dataProvider = new ManageCardsDataProvider(CardRepository.getInstance(this));
        adapter = new ManageCardsAdapter(this, dataProvider);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        createCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateCardDialog();
            }
        });
    }

    private void showCreateCardDialog() {
        CreateCardDialog createCardDialog = new CreateCardDialog(ManageCardsActivity.this, dataProvider);
        createCardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ManageCardsActivity.this.adapter.notifyDataSetChanged();
            }
        });
        createCardDialog.show();
    }
}
