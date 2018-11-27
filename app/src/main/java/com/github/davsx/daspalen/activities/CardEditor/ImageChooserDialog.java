package com.github.davsx.daspalen.activities.CardEditor;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageChooserDialog extends AlertDialog {

    private static final String TAG = "ImageChooserDialog";

    private Context context;
    private ChosenBitmapHandler chosenBitmapHandler;
    private Request initialRequest;
    private OkHttpClient httpClient;
    private List<String> thumbnailUrlQueue = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();
    private ImageChooserAdapter adapter;
    private RecyclerView recyclerView;
    private Handler mainHandler;

    ImageChooserDialog(Context context) {
        super(context);
        this.context = context;
        mainHandler = new Handler(context.getMainLooper());
    }

    @Override
    protected void onStop() {
        thumbnailUrlQueue.clear();
        imageUrlList.clear();
        httpClient.dispatcher().cancelAll();
        super.onStop();
    }

    @Override
    public void show() {
        httpClient.newCall(initialRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Could not request images JSON", Toast.LENGTH_SHORT).show();
                        ImageChooserDialog.this.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                String json;
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Could not request images JSON", Toast.LENGTH_SHORT).show();
                            ImageChooserDialog.this.dismiss();
                        }
                    });
                    return;
                }

                onInitialRequestResponse(json);
            }
        });

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_image_chooser, null);

        adapter = new ImageChooserAdapter(context, new ChosenThumbnailHandler() {
            @Override
            public void handle(int index) {
                httpClient.dispatcher().cancelAll();
                downloadImage(imageUrlList.get(index));
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        setView(view);
        super.show();
    }

    private void onInitialRequestResponse(String json) {
        try {
            JSONObject top = new JSONObject(json);
            JSONArray items = top.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                String mime = items.getJSONObject(i).getString("mime");
                String imageLink = items.getJSONObject(i).getString("link");
                String thumbnail = items.getJSONObject(i).getJSONObject("image").getString("thumbnailLink");

                if (mime.startsWith("image/")) {
                    imageUrlList.add(imageLink);
                    thumbnailUrlQueue.add(thumbnail);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, json);
            e.printStackTrace();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Could not deserialize JSON", Toast.LENGTH_SHORT).show();
                    ImageChooserDialog.this.dismiss();
                }
            });
            return;
        }

        downloadThumbnailFromQueue();
    }

    private void downloadThumbnailFromQueue() {
        if (thumbnailUrlQueue.isEmpty()) {
            return;
        }

        String url = thumbnailUrlQueue.remove(0);

        Request request = new Request.Builder()
                .url(url)
                .tag(DaspalenConstants.OKHTTP_TAG)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.w(TAG, "Could not download image: " + call.request().url().toString() + " " + e);
                downloadThumbnailFromQueue();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                final Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Could not download image", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addImage(bitmap);
                    }
                });
                downloadThumbnailFromQueue();
            }
        });
    }

    private void downloadImage(String url) {
        Request request = new Request.Builder()
                .url(url)
                .tag(DaspalenConstants.OKHTTP_TAG)
                .build();

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Downloading image")
                .setPositiveButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpClient.dispatcher().cancelAll();
                                ImageChooserDialog.this.dismiss();
                            }
                        });
                        dismiss();
                    }
                })
                .create();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(TAG, "Could not download image: " + e);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageChooserDialog.this.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                final Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Could not download image", Toast.LENGTH_SHORT).show();
                    return;
                }

                chooseBitmap(bitmap);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    private void chooseBitmap(Bitmap bitmap) {
        float maxSize = 600;

        float scaleX = maxSize / bitmap.getWidth();
        float scaleY = maxSize / bitmap.getHeight();
        float scale = scaleX > scaleY ? scaleX : scaleY;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                ((int) (bitmap.getWidth() * scale)),
                ((int) (bitmap.getHeight() * scale)),
                false);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                chosenBitmapHandler.handle(scaledBitmap);
            }
        });
        dismiss();
    }

    void setChosenBitmapHandler(ChosenBitmapHandler handler) {
        this.chosenBitmapHandler = handler;
    }

    void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    void setInitialRequest(Request request) {
        this.initialRequest = request;
    }

}
