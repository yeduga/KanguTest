package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.CalendarListFragment;
import com.virtualtec.kangutest.Datas.DataAddress;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ProductsCategoryFragment;
import com.virtualtec.kangutest.ProfileFragment;
import com.virtualtec.kangutest.R;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by Android on 5/04/18.
 */

public class AddressAdapter extends BaseAdapter {

    protected ProfileFragment profileFragment;
    protected Context activity;
    protected int status;
    protected ArrayList<DataAddress> items;


    public AddressAdapter (Context activity, ArrayList<DataAddress> items, int status, ProfileFragment profileFragment) {
        this.activity = activity;
        this.items = items;
        this.status = status;
        this.profileFragment = profileFragment;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_address, parent, false);
        }

        final DataAddress dir = items.get(position);

        TextView name = (TextView) v.findViewById(R.id.name_listAddress);
        ImageButton btnEdit = (ImageButton) v.findViewById(R.id.imagebtn_edit_address);
        ImageButton btnRemove = (ImageButton) v.findViewById(R.id.imagebtn_remove_address);

        name.setText(dir.getAddress());

        if (status == 0){
            btnEdit.setVisibility(View.GONE); btnRemove.setVisibility(View.GONE);
        }else {
            btnEdit.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.VISIBLE);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragment.EditAddressSelected(dir);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragment.RemoveAddressSelected(dir);
            }
        });

        return v;
    }

}
