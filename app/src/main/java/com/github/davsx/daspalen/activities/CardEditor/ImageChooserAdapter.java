package com.github.davsx.daspalen.activities.CardEditor;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.github.davsx.daspalen.R;

import java.util.ArrayList;
import java.util.List;

public class ImageChooserAdapter extends RecyclerView.Adapter<ImageChooserAdapter.ItemHolder> {

    private List<Bitmap> images = new ArrayList<>();
    private LayoutInflater inflater;
    private ChosenThumbnailHandler handler;
    private Context context;

    ImageChooserAdapter(Context context, ChosenThumbnailHandler handler) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.handler = handler;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dialog_image_chooser_list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Bitmap bitmap = images.get(position);
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    void addImage(Bitmap bitmap) {
        images.add(bitmap);
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        ItemHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Use image?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ImageChooserAdapter.this.handler.handle(getAdapterPosition());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }

    }

}
