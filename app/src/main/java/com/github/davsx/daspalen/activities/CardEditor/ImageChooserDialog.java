package com.github.davsx.daspalen.activities.CardEditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageChooserDialog extends AlertDialog {

    private static final String TAG = "ImageChooserDialog";

    private Context context;
    private ChosenImageHandler handler;
    private Request initialRequest;
    private OkHttpClient httpClient;
    private List<String> imageQueue = new ArrayList<>();
    private ImageChooserAdapter adapter;
    private RecyclerView recyclerView;

    ImageChooserDialog(Context context) {
        super(context);
        this.context = context;
        this.httpClient = new OkHttpClient();
        this.httpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
    }

    @Override
    protected void onStop() {
        super.onStop();
        httpClient.cancel(DaspalenConstants.OKHTTP_TAG);
    }

    @Override
    public void show() {
        httpClient.newCall(initialRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(context, "Could not request images JSON", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onResponse(Response response) {
                String json;
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Could not request images JSON", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject top = new JSONObject(json);
                    JSONArray items = top.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        String mime = items.getJSONObject(i).getString("mime");
                        String link = items.getJSONObject(i).getString("link");
                        if (mime.startsWith("image/")) {
                            imageQueue.add(link);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, json);
                    e.printStackTrace();
                    Toast.makeText(context, "Could not deserialize JSON", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                downloadImageFromQueue();
            }
        });

        Rect displayRect = new Rect();
        Window w = ((CardEditorActivity) context).getWindow();
        w.getDecorView().getWindowVisibleDisplayFrame(displayRect);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_image_chooser, null);
        view.setMinimumHeight((int) (displayRect.height() * 0.9f));

        adapter = new ImageChooserAdapter(context, new ChosenImageHandler() {
            @Override
            public void handle(Bitmap bitmap) {
                ImageChooserDialog.this.handler.handle(bitmap);
                dismiss();
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        setView(view);
        super.show();
    }

    private void downloadImageFromQueue() {
        if (imageQueue.isEmpty()) {
            return;
        }

        String url = imageQueue.remove(0);

        Request request = new Request.Builder()
                .url(url)
                .tag(DaspalenConstants.OKHTTP_TAG)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.w(TAG, "Could not download image: " + request.url().toString() + " " + e);
                downloadImageFromQueue();
            }

            @Override
            public void onResponse(Response response) {
                final Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Could not download image", Toast.LENGTH_SHORT).show();
                    return;
                }

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addImage(bitmap);
                    }
                });
                downloadImageFromQueue();
            }
        });
    }

    void setChosenImageHandler(ChosenImageHandler handler) {
        this.handler = handler;
    }

    void setRequest(Request request) {
        this.initialRequest = request;
    }
}
