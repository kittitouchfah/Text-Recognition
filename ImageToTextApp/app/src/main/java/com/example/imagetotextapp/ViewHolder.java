package com.example.imagetotextapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView mText, mUser ,mFilename,mDate;
    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v,getAdapterPosition());

            }
        });

        //item long click listener
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v,getAdapterPosition());
                return false;
            }

        });

        mText = itemView.findViewById(R.id.rText);
        mUser = itemView.findViewById(R.id.rUser);
        mDate = itemView.findViewById(R.id.rDate);
        mFilename = itemView.findViewById(R.id.rFilename);

    }

    private ViewHolder.ClickListener mClickListener;
    //iterface for click listener
    public interface ClickListener {
        void onItemClick(View view , int postition);
        void onItemLongClick(View view , int postition);

    }
    public void setmClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }


}

