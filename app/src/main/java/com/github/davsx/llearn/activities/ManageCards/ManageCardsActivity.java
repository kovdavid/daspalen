package com.github.davsx.llearn.activities.ManageCards;

import android.app.Activity;
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
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;

import javax.inject.Inject;

public class ManageCardsActivity extends Activity {

    private static final String TAG = "ManageCardsActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.create_card_button)
    FloatingActionButton createCardButton;

    private ManageCardsAdapter adapter;

    @Inject
    ManageCardsService manageCardsService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);
        ButterKnife.bind(this);

        Log.d(TAG, "onCreate");

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        adapter = new ManageCardsAdapter(this, manageCardsService);
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
        CreateCardDialog createCardDialog = new CreateCardDialog(ManageCardsActivity.this, manageCardsService);
        createCardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ManageCardsActivity.this.adapter.notifyDataSetChanged();
            }
        });
        createCardDialog.show();
    }
}
