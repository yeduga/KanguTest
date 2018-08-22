package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 26/03/18.
 */

public class ListCheckBoxAdapter extends RecyclerView.Adapter<ListCheckBoxAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<DataList> dataLists;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_Title_list_lists) TextView textViewName;
        @BindView(R.id.checkBox_list_lists) CheckBox checkBox;
        @BindView(R.id.relative_body_list_lists) RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ListCheckBoxAdapter(Context mContext, ArrayList<DataList> dataLists) {
        this.mContext = mContext;
        this.dataLists = dataLists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lists_checkbox, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final DataList list = dataLists.get(position);
        holder.textViewName.setText(list.getName_list());
        if (list.isSelected() == true){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        // Log.d("SelectCheck ", "Position: " + position);
        holder.checkBox.setOnClickListener( new View.OnClickListener() { public void onClick(View v) { functCheckBox(holder, list, false); } });
        holder.relativeLayout.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) { functCheckBox(holder, list, true); }
        });
    }

    @Override
    public int getItemCount() {
        return dataLists.size();
    }

    private void functCheckBox(MyViewHolder holder, DataList list, boolean status){
        if (holder.checkBox.isChecked() == status){
            list.setSelected(false);
            holder.checkBox.setChecked(false);
        }else{
            list.setSelected(true);
            holder.checkBox.setChecked(true);
        }
    }
}