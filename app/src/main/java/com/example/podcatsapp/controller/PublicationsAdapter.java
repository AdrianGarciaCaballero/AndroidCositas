package com.example.podcatsapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.podcatsapp.R;
import com.example.podcatsapp.model.Publication;

import java.util.List;

public class PublicationsAdapter extends RecyclerView.Adapter<PublicationsAdapter.ViewHolder>{
    private List<String> publicationList;  // Example data type. Replace with your data type.
    private Context context;

    public PublicationsAdapter(Context context, List<String> publicationList) {
        this.context = context;
        this.publicationList = publicationList;
    }

    // class view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pubTitle;
        public TextView pubDescription;
        public ImageView pubImage;

        public ViewHolder(View itemView) {
            super(itemView);

            pubTitle = (TextView) itemView.findViewById(R.id.item_tv_description);
            pubDescription = (TextView) itemView.findViewById(R.id.item_tv_description);
            pubImage = (ImageView) itemView.findViewById(R.id.item_iv_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  Toast.makeText(Co,"" + itemView.getId(), Toast.LENGTH_SHORT).show();
                    System.out.println( itemView.getId());
                }
            });
        }
    }
    // Store a member variable for the contacts
    private List<Publication> mPublications;

    //  the constructor
    public PublicationsAdapter(List<Publication> publications) {
        mPublications = publications;
    }




    //implementing all func
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View pubView = inflater.inflate(R.layout.item_pub, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(pubView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publication publication = mPublications.get(position);

        // Set item views based on your views and data model
        TextView pubTitletitle = holder.pubTitle;
         TextView pubDescription = holder.pubTitle;
         ImageView pubImage = null;


        pubTitletitle.setText(publication.getTitle());
        pubDescription.setText(publication.getDescription());
        //pubImage.setImageResource(R.drawable.cat3);
        holder.itemView.setOnClickListener(v -> {
            // Displaying a Toast message with the item's position and text.
            Toast.makeText(context, "Clicked on: " + publication, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return mPublications.size();
    }


}
