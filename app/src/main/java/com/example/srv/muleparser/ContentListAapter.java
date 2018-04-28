package com.example.srv.muleparser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ContentListAapter extends BaseAdapter {
    private Context mContext;
    private List list;

    public ContentListAapter(Context mContext, List list){
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
            convertView = inflater.inflate(R.layout.contents_layout, null);
        }

        WebView webView = convertView.findViewById(R.id.webView);
        Map data = (Map)list.get(position);

        String mimeType = "text/html";
        String encoding = "EUC-KR";
        webView.loadData((String)data.get("contents"), mimeType, encoding);

        TextView txt_c_title = convertView.findViewById(R.id.txt_c_title);
        txt_c_title.setText((String)data.get("title"));
        return convertView;
    }
}
