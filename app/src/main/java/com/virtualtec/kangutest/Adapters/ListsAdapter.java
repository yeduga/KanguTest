package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ListDetailsFragment;
import com.virtualtec.kangutest.R;
import com.virtualtec.kangutest.ScheduleListFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 16/03/18.
 */

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.MyViewHolder>{

    private Activity mContext;
    private ArrayList<DataList> dataLists;
    private int typeViewLists;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_name_gridList) TextView textViewName;
        @BindView(R.id.textview_label1_gridList) TextView textViewCreation;
        @BindView(R.id.textview_label2_gridList) TextView textViewDelivery;
        @BindView(R.id.btn_viewlist_gridList) Button btnViewList;
        @BindView(R.id.btn_schedulelist_gridList) Button btnscheduleList;
        @BindView(R.id.relative_gridList) RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ListsAdapter(Activity mContext, ArrayList<DataList> dataLists, int typeViewLists) {
        this.mContext = mContext;
        this.dataLists = dataLists;
        this.typeViewLists = typeViewLists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(typeViewLists == 0){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_lists, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_lists_horizontal, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DataList list = dataLists.get(position);
        holder.textViewName.setText(list.getName_list());
        holder.textViewCreation.setText("Creación: " + list.getDate_creation());
        holder.textViewDelivery.setText("Entrega: " + list.getDate_delivery());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { OpenListDetails(list); }
        });
        holder.btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { OpenListDetails(list); }
        });

        holder.btnscheduleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayList", list);
                ScheduleListFragment nextFrag= new ScheduleListFragment();
                nextFrag.setArguments(bundle);
                ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    private void OpenListDetails(DataList list) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("arrayList", list);
        ListDetailsFragment nextFrag= new ListDetailsFragment();
        nextFrag.setArguments(bundle);
        ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public int getItemCount() {
        return dataLists.size();
    }
}
