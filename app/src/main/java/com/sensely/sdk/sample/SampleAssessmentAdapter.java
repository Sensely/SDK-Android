package com.sensely.sdk.sample;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SampleAssessmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<String> mMenuList;
    private LayoutInflater mInflater;
    private SampleListner mSampleListner;

    interface SampleListner{
        void onItemClick(int index);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.sample_nas_assesment_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.textMenu.setText(mMenuList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    // data is passed into the constructor
    public SampleAssessmentAdapter(Context context, List<String> menuList, SampleListner itemListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mMenuList = menuList;
        this.context = context;
        this.mSampleListner = itemListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textMenu;

        ItemViewHolder(View itemView) {
            super(itemView);
            textMenu = itemView.findViewById(R.id.text_menu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mSampleListner != null) {
                mSampleListner.onItemClick(getAdapterPosition());
            }
        }
    }


}
