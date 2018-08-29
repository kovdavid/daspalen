package com.github.davsx.llearn.gui.activities.KindleImport;

import android.content.Intent;
import android.os.Bundle;
import com.github.davsx.llearn.data.KindleImport.KindleImportReceiver;
import com.github.davsx.llearn.gui.activities.BaseActivity;

public class KindleImportActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type.equals("message/rfc822")) {
            new KindleImportReceiver(intent).receiveBackStrings();
        }
        finish();
    }

}
