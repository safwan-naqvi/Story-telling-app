package com.comsats.aistoryteller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class RecommendedStoryAdapter extends RecyclerView.Adapter<RecommendedStoryAdapter.ViewHolder> {

    Context context;
    ArrayList<RecommendedStoryModel> products;

    public RecommendedStoryAdapter(Context context, ArrayList<RecommendedStoryModel> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public RecommendedStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.story_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedStoryAdapter.ViewHolder holder, int position) {
        RecommendedStoryModel product = products.get(position);
        Glide.with(context)
                .load(product.getStory_image())
                .into(holder.p_img);
        holder.pname.setText(product.getStory_name());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        ImageView p_img;
        TextView pname, price, dlt;
        RatingBar rating;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            p_img = (ImageView) itemView.findViewById(R.id.image);
            pname = (TextView) itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.rating);


        }
    }


}
