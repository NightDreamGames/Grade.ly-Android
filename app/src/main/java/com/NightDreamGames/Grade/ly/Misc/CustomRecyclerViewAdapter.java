package com.NightDreamGames.Grade.ly.Misc;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.NightDreamGames.Grade.ly.Activities.MainActivity;
import com.NightDreamGames.Grade.ly.R;

import java.util.ArrayList;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mData;
    private final ArrayList<String> mData2;
    private final LayoutInflater mInflater;
    private final int type;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public CustomRecyclerViewAdapter(Context context, ArrayList<String> data, ArrayList<String> data2, int type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mData2 = data2;
        this.type = type;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (type == 0)
            layout = R.layout.subject_row;
        else
            layout = R.layout.test_row;
        View view = mInflater.inflate(layout, parent, false);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            view.setBackground(AppCompatResources.getDrawable(MainActivity.sApplication, R.drawable.ripple_background_night));
        else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
            view.setBackground(AppCompatResources.getDrawable(MainActivity.sApplication, R.drawable.ripple_background_day));
        else {
            int currentNightMode = MainActivity.sApplication.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
                view.setBackground(AppCompatResources.getDrawable(MainActivity.sApplication, R.drawable.ripple_background_night));
            else
                view.setBackground(AppCompatResources.getDrawable(MainActivity.sApplication, R.drawable.ripple_background_day));
        }

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt1.setText(mData.get(position));
        holder.txt2.setText(mData2.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView txt1;
        public final TextView txt2;

        ViewHolder(View itemView) {
            super(itemView);
            txt1 = itemView.findViewById(R.id.rowTextView1);
            txt2 = itemView.findViewById(R.id.rowTextView2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getBindingAdapterPosition());
        }
    }
}
