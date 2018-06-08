package com.example.chow.weatherv2;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EarthQuakeWarnings extends AppCompatActivity {

    private quakeArrayAdapter adapter = null;

        private static final int LIST_quake = 1;

        private android.os.Handler handler = new android.os.Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LIST_quake: {
                        List<quake> quakes = (List<quake>)msg.obj;
                        refreshQuakeList(quakes);
                        break;
                    }
                }
            }
        };
        private void refreshQuakeList(List<quake> quakes) {
            adapter.clear();
            adapter.addAll(quakes);
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_earthquake);

            getQuakeFromFirebase();
            ListView Quake= (ListView)findViewById(R.id.listview_earthQuake);

            adapter = new quakeArrayAdapter(this, new ArrayList<quake>());
            Quake.setAdapter(adapter);


        }

        class FirebaseThread extends Thread {

            private DataSnapshot dataSnapshot;
            public FirebaseThread(DataSnapshot dataSnapshot) {
                this.dataSnapshot = dataSnapshot;
            }

            @Override
            public void run() {
                List<quake> lsquake = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DataSnapshot dsTime = ds.child("台灣時間");
                    DataSnapshot dsLocale = ds.child("位置");
                    DataSnapshot dsExtent = ds.child("規模");
                    DataSnapshot dsDepth = ds.child("深度（公里）");

                    String Time = "Time : " + (String)dsTime.getValue();
                    String locale = "locale : " + (String)dsLocale.getValue();
                    String extent = "extent : " + (String)dsExtent.getValue();
                    String depth = "depth : " + (String)dsDepth.getValue();

                    quake aQuake = new quake();
                    aQuake.setTime(Time);
                    aQuake.setLocale(locale);
                    aQuake.setExtent(extent);
                    aQuake.setDepth(depth);
                    lsquake.add(aQuake);

                    Log.v("EarthQuake", Time + ";" + locale );

                    Message msg = new Message();
                    msg.what = LIST_quake;
                    msg.obj = lsquake;
                    handler.sendMessage(msg);

                }
            }
        }

        private void getQuakeFromFirebase() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    new FirebaseThread(dataSnapshot).start();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("earthQuake", databaseError.getMessage());
                }
            });
        }

        class quakeArrayAdapter extends ArrayAdapter<quake> {
            Context context;

            public quakeArrayAdapter(Context context, List<quake> items) {
                super(context, 0, items);
                this.context = context;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                LinearLayout itemlayout = null;
                if (convertView == null) {
                    itemlayout = (LinearLayout) inflater.inflate(R.layout.quake, null);
                } else {
                    itemlayout = (LinearLayout) convertView;
                }
                quake item = (quake) getItem(position);
                TextView tvDepth = (TextView) itemlayout.findViewById(R.id.tv_depth);
                tvDepth.setText(item.getDepth());
                TextView tvExtent = (TextView) itemlayout.findViewById(R.id.tv_extent);
                tvExtent.setText(item.getExtent());
                TextView tvLocale = (TextView) itemlayout.findViewById(R.id.tv_locale);
                tvLocale.setText(item.getLocale());
                TextView tvTime = (TextView) itemlayout.findViewById(R.id.tv_time);
                tvTime.setText(item.getTime());
                return itemlayout;
            }
        }

}