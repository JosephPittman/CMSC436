package com.example.joey.collabart;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {

    private final List<Bitmap> images;
    private final List<String> captions;
    private final int mRowLayout;

    public MyAdapter(List<Bitmap> i, List<String> c, int rowLayout) {
        captions = c;
        images = i;
        mRowLayout = rowLayout;
    }

    // Create ViewHolder which holds a View to be displayed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new MyAdapter.ViewHolder(v);
    }

    // Binding: The process of preparing a child view to display data corresponding to a position within the adapter.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //viewHolder.mName.setText(images.get(i));
        viewHolder.myImage.setImageBitmap(images.get(i));
        viewHolder.myCaption.setText(captions.get(i));
        Log.i("hello", "setting caption"+captions.get(i));
    }

    @Override
    public int getItemCount() {
        return (null == images) ? 0 : images.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView myImage;
        public final TextView myCaption;

        public ViewHolder(View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.pic);
            myCaption = itemView.findViewById(R.id.caption);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Display a Toast message indicting the selected item
        }
    }

}
