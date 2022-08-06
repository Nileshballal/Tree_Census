package com.nil.treeclause.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nil.treeclause.CommonClass;
import com.nil.treeclause.R;

import java.util.ArrayList;

public class CommonAdapter extends BaseAdapter {
    ArrayList<CommonClass> commonClassArrayList ;
    LayoutInflater mInflater;
    Context context;

    public CommonAdapter(Context context1, ArrayList<CommonClass> commonClassArrayList) {
        this.commonClassArrayList = commonClassArrayList;
        mInflater = LayoutInflater.from(context1);
        context = context1;


    }

    @Override
    public int getCount() {
        return commonClassArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commonClassArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
         ViewHolder holder=new ViewHolder();
    if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vwb_spinner_text, null);

            holder.txt_name = (TextView) convertView.findViewById(R.id.txt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_name.setText(commonClassArrayList.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        TextView txt_name;
    }
}
