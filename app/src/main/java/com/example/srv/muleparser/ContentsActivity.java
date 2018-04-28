package com.example.srv.muleparser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.RangeValueIterator;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);


        Intent intent = getIntent();
        ArrayList list = (ArrayList) getIntent().getStringArrayListExtra("boardList");

        ContentTask task = new ContentTask(this);
        task.execute(list);

    }


    private class ContentTask extends AsyncTask{
        private ProgressDialog progressDialog;
        private Context context;
        public ContentTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("데이터 불러오는중..");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);

            progressDialog.dismiss();


            ListView listView = (ListView) findViewById(R.id.contentListView);
            ContentListAapter adapter = new ContentListAapter(context, (List)object);
            listView.setAdapter(adapter);

            Toast.makeText(context, "작업 완료", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            int progress = (Integer) values[0];
            progressDialog.setMessage("데이터 수신 중...");
            progressDialog.setProgress(progress);
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected List doInBackground(Object[] objects) {

            List htmlList = null;
            List list = (List)objects[0];
            if(list != null && list.size() > 0) {
                progressDialog.setMax(list.size());
                htmlList = new ArrayList();
                for(int i = 0; i < list.size(); i++) {

                    Map data = (Map)list.get(i);
                    String url = "http://www.mule.co.kr/instrument/2/" + (String)data.get("url");
                    Log.d("URL", url);
                    try {
                        Document doc = Jsoup.connect(url).get();

                        String title = doc.select("p.title").text();
                        Log.d("TITLE", title);

                        Elements rows = doc.select("div.read_con span.bbscontent");

                        String contents = rows.html();
                        Log.d("HTML", contents);
/*
                        Elements elements = doc.select("div.read_con table tbody tr");
                        for(int x = 0; x < elements.size(); x++){

                            Iterator<Element> iterator = elements.select("td").iterator();

                            while(iterator.hasNext()) {
                                String src = iterator.next().select("img").attr("src");

                                //String src = elements.select("td img").attr("src");
                                if (src.indexOf("http://") > -1) {
                                    contents += "<img src='" + src + "' width='100'/>";
                                } else {
                                    src = "http://www.mule.co.kr" + src;
                                    contents += "<img src='" + src + "' width='100'/>";
                                }
                            }
                        }
*/
                        Map map = new HashMap();
                        map.put("title", title);
                        map.put("contents", contents);
                        htmlList.add(map);


                        publishProgress(i);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }

            return htmlList;
        }
    }
}
