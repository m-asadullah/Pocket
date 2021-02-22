package com.cubixos.pocket.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.models.Pocket;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PocketAdapter extends FirestoreRecyclerAdapter<Pocket, PocketAdapter.MathHolder> {

    public PocketAdapter( @NonNull FirestoreRecyclerOptions<Pocket> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MathHolder holder, int position, @NonNull Pocket pocketmodel) {
        Context context = holder.itemView.getContext();
        holder.proNameItem.setText(pocketmodel.getTitle());
        holder.proDesignationItem.setText(pocketmodel.getDescription());
        if (CheckInternet.isNetwork(context)){
            Picasso.get()
                    .load(pocketmodel.getImageUrl())
                    .placeholder(R.drawable.empty_profile_pic)
                    .error(R.drawable.ic_warning_sign_board)
                    .into(holder.imageViewFacultyItem);
        }else {
            Picasso.get()
                    .load(pocketmodel.getImageUrl())
                    .placeholder(R.drawable.empty_profile_pic)
                    .error(R.drawable.ic_warning_sign_board)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.imageViewFacultyItem);
        }
        holder.constraintLayoutFacultyItem.setOnClickListener(v -> {
            Context context1 = holder.itemView.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.youtube", "com.google.android.youtube.RedirectUriReceiverActivity");
            context1.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public MathHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pocket, parent, false);

        return new MathHolder(v);
    }

    static class MathHolder extends RecyclerView.ViewHolder {

        TextView proNameItem;
        TextView proDesignationItem;
        ImageView imageViewFacultyItem;
        ConstraintLayout constraintLayoutFacultyItem;
        public MathHolder(View itemView) {
            super(itemView);
            constraintLayoutFacultyItem = itemView.findViewById(R.id.constraint_layout_pocket_item);
            imageViewFacultyItem = itemView.findViewById(R.id.image_view_profile_pocket_item);
            proNameItem = itemView.findViewById(R.id.text_view_name_pocket_item);
            proDesignationItem = itemView.findViewById(R.id.text_view_designation_pocket_item);
        }
    }
}
