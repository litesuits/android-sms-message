package com.ginshell.sms;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private ArrayList<Message> msgList = new ArrayList<Message>();
    private MessageAdapter mAdapter;
    private final int MAX_COUNT = 1000000;
    private int todayNums;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = (ListView) findViewById(R.id.list);
        mAdapter = new MessageAdapter();
        list.setAdapter(mAdapter);
        getActionBar().setTitle("短信");
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMsg();
    }

    private void loadMsg() {
        new AsyncTask<Object, Object, ArrayList<Message>>() {

            @Override
            protected ArrayList<Message> doInBackground(Object... params) {
                ArrayList<Message> list = LiteOrm.via(MainActivity.this).query(new QueryBuilder(Message.class).appendOrderDescBy("time"));
                if (list.size() > MAX_COUNT) {
                    LiteOrm.via(MainActivity.this).deleteAll(Message.class);
                }
                for (Message m : list) {
                    //Log.i(TAG, "time: " + m.time);
                    if (m.time != null && DateUtils.isToday(m.time.getTime())) todayNums++;
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<Message> messages) {
                if (messages != null) {
                    msgList = messages;
                    mAdapter.notifyDataSetChanged();
                    getActionBar().setTitle("短信(已发：" + msgList.size() + "  今日：" + todayNums + ")");
                }
            }
        }.execute();
    }

    class MessageAdapter extends BaseAdapter {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public int getCount() {
            return msgList.size();
        }

        @Override
        public Message getItem(int position) {
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.li_info_list, null);
                vh = new ViewHolder();
                vh.head = (ImageView) convertView.findViewById(R.id.head);
                vh.phone = (TextView) convertView.findViewById(R.id.phone);
                vh.body = (TextView) convertView.findViewById(R.id.body);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            Message c = getItem(position);
            if (c != null) {
                vh.phone.setText(c.phone);
                vh.body.setText(c.body);
                vh.time.setText(format.format(c.time));
            }
            return convertView;
        }

        class ViewHolder {
            public ImageView head;
            public TextView  phone;
            public TextView  body;
            public TextView  time;
        }
    }
}
