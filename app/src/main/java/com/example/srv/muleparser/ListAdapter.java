package com.example.srv.muleparser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private List list;

    public ListAdapter(Context mContext, List list){
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.firstlist_layout, null);
        }

        Map map = (Map)list.get(position);

        TextView txt_h_num = convertView.findViewById(R.id.txt_h_num);
        TextView txt_h_title = convertView.findViewById(R.id.txt_h_title);
        TextView txt_h_url = convertView.findViewById(R.id.txt_h_url);

        //Log.d("position", ""+position);
        //Log.d("txt_h_num", ""+map.get("num").toString());
        //Log.d("txt_h_title", ""+map.get("title").toString());
        //Log.d("txt_h_url", ""+map.get("url").toString());

        txt_h_num.setText(map.get("num").toString());
        txt_h_title.setText(map.get("title").toString());
        txt_h_url.setText(map.get("url").toString());

        return convertView;
    }
}
