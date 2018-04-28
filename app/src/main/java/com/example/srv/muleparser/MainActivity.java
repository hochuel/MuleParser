package com.example.srv.muleparser;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List boardList = null;
    private ListView listView = null;
    ListAdapter adapter = null;
    Handler mHandler = new Handler();

    private int boardNum = 0;
    private SwipeRefreshLayout swipeContainer;


    public List getBoardList() {
        return boardList;
    }

    public void setBoardList(List boardList) {
        this.boardList = boardList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

            boardList = new ArrayList();

            Object[] obj = {"http://m.mule.co.kr/marketplace/index"};
            UrlParseDataTask task = new UrlParseDataTask(this);
            task.execute(obj);

            listView = findViewById(R.id.listView);
            adapter = new ListAdapter(this, boardList);
            listView.setAdapter(adapter);
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    //Log.d("BOARDNUM", ""+boardNum);
                    Map map = (Map)boardList.get(0);
                    boardNum = Integer.parseInt(map.get("num").toString());
                    new GetDataThread("http://m.mule.co.kr/marketplace/index").start();
                    Log.d("TAG", "TOP");

                    swipeContainer.setRefreshing(false);
                }
            });
/*
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
*/
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private class GetDataThread extends Thread{
        private String url;

        public GetDataThread(String url){
            this.url = url;
        }

        public void run() {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements rows = doc.select("div section ul li").not(".pre");
//Log.d("THREAD TAG", rows.html());
                for(int i = 0; i < 15; i++){
                    Map map = new HashMap();
                    String num = "";
                    String title = "";
                    String linkPage = "";

                    Element element = rows.get(i);

                    //Log.d("HTML<=>" +i +":", element.html());

                    Elements iterElem = element.getElementsByTag("a");
                    title = iterElem.text();
                    linkPage = iterElem.attr("href");

                    int strlen = iterElem.attr("href").lastIndexOf("idx");

                    num = linkPage.substring(strlen + 4, linkPage.length());
                    Log.d("num", "" + num);

                    if(Integer.parseInt(num) > boardNum) {
                        Log.d("새로고침", num);

                        //boardList.clear();

                        map.put("num", num);
                        map.put("url", linkPage);
                        map.put("title", title);

                        boardList.add(0, map);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private class UrlParseDataTask extends AsyncTask {
        ProgressDialog progressDialog;
        private Context mContext;


        public UrlParseDataTask(Context context){
            mContext =context;
        }

        @Override
        protected List doInBackground(Object[] objects) {
            String url = (String)objects[0];
            try {

                Document doc = Jsoup.connect(url).get();
                Elements rows = doc.select("div section ul li").not(".pre");

                //Log.d("SIZE", "" + rows.size());

                for(int i = 0; i < 15; i++){

                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    Map map = new HashMap();
                    String num = "";
                    String title = "";
                    String linkPage = "";

                    Element element = rows.get(i);

                    //Log.d("HTML<=>" +i +":", element.html());

                    Elements iterElem = element.getElementsByTag("a");
                    title = iterElem.text();
                    linkPage = iterElem.attr("href");

                    int strlen = iterElem.attr("href").lastIndexOf("idx");

                    num = linkPage.substring(strlen + 4, linkPage.length());

                    map.put("num", num);
                    map.put("url", linkPage);
                    map.put("title", title);
                    boardList.add(map);

                }

            }catch(IOException e){
                e.printStackTrace();
            }
            return boardList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("접속 시작");
            progressDialog.setCancelable(false);
            //progressDialog.setProgress( 0 );
            progressDialog.setMax(15);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Object object) {
            //onPostExecute() 함수는 doInBackground() 함수가 종료되면 실행됨
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
            Toast.makeText(mContext, "작업 완료", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            int progress = (Integer) values[0];
            progressDialog.setMessage("데이터 수신 중...");
            progressDialog.setProgress(progress);
        }
    }
}
