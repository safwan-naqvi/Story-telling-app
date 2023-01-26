package com.comsats.aistoryteller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


public class StoriesAdapter extends FirebaseRecyclerAdapter<StoriesModel, StoriesAdapter.myviewholder> {


    public Context context;

    public StoriesAdapter(@NonNull Activity context, FirebaseRecyclerOptions<StoriesModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull StoriesModel model) {
        final String pstkey = getRef(position).getKey();

        holder.pname.setText(model.getStory_name());
        Glide.with(holder.p_img.getContext()).load(model.getStory_image()).into(holder.p_img);


        holder.rating.setRating(Float.valueOf(model.getStory_rating()));
/*
        holder.dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setTitle("Delete this item?");
                alertDialog.setMessage("Are you sure you want to delete this?");
                alertDialog.setIcon(R.drawable.logo);

                alertDialog.setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do the stuff..
                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("venues");

                                dbref.child(pstkey).removeValue();
                            }
                        }
                ).show()
                ;


            }


        });
*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MediaPlayerActivity.class);

                intent.putExtra("key", pstkey);

                intent.putExtra("name", model.getStory_name());
                intent.putExtra("story", model.getStory_audio());
                intent.putExtra("image", model.getStory_image());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
        return new myviewholder(view);


    }

    class myviewholder extends RecyclerView.ViewHolder {
        View itemView;

        ImageView p_img;
        TextView pname, price, dlt;
        RatingBar rating;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            p_img = (ImageView) itemView.findViewById(R.id.image);
            pname = (TextView) itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.rating);


        }
    }


}
