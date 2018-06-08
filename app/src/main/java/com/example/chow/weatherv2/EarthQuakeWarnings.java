package com.example.chow.weatherv2;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



//uninportnt

public class EarthQuakeWarnings extends AppCompatActivity {

    private quakeArrayAdapter adapter = null;

        private static final int LIST_quake = 1;

        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LIST_quake: {
                        List<quake> pets = (List<quake>)msg.obj;
                        refreshPetList(pets);
                        break;
                    }
                }
            }
        };

        private void refreshPetList(List<quake> pets) {
            adapter.clear();
            adapter.addAll(pets);

        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ListView Quake= (ListView)findViewById(R.id.listview_earthQuake);

            adapter = new quakeArrayAdapter(this, new ArrayList<quake>());
            Quake.setAdapter(adapter);

            getQuakeFromFirebase();
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

                    String Time = (String)dsTime.getValue();
                    String locale = (String)dsLocale.getValue();
                    String extent = (String)dsExtent.getValue();
                    String depth = (String)dsDepth.getValue();

                    quake aQuake = new quake();
                    aQuake.setTime(Time);
                    aQuake.setLocale(locale);
                    aQuake.setExtent(extent);
                    aQuake.setDepth(depth);
                    lsquake.add(aQuake);
                    /*
                                        Log.v("EarthQuake", Time + ";" + locale );
                                         */
                }
                /*
                Message msg = new Message();
                msg.what = LIST_quake;
                msg.obj = lsquake;
                handler.sendMessage(msg);
                */
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
                    Log.v("AdoptPet", databaseError.getMessage());
                }
            });
        }

        private Bitmap getImgBitmap(String imgUrl) {
            try {
                URL url = new URL(imgUrl);
                Bitmap bm = BitmapFactory.decodeStream(url.openConnection()
                        .getInputStream());
                return bm;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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
                    itemlayout = (LinearLayout) inflater.inflate(R.layout.pet_item, null);
                } else {
                    itemlayout = (LinearLayout) convertView;
                }
                quake item = (quake) getItem(position);
                TextView tvShelter = (TextView) itemlayout.findViewById(R.id.tv_shelter);
                tvShelter.setText(item.getShelter());
                TextView tvKind = (TextView) itemlayout.findViewById(R.id.tv_kind);
                tvKind.setText(item.getKind());
                return itemlayout;
            }
        }

}