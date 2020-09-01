package com.example.media_player_2april2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DataAdapter.OnNoteListener {

    RecyclerView recyclerView ;
    Log log ;
    ImageView btPlayTopBar , btPauseTopBar;
    LinearLayout linearLayoutTopBar ;
    TextView textViewTopBar;


    private static  final  String[] PERMISSIONS ={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 12345;
    private static final int PERMISSIONS_COUNT = 1;


    @SuppressLint("NewApi")
    private boolean arePermissionsDeniewd() {
        for (int i = 0; i < PERMISSIONS_COUNT; i++) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (arePermissionsDeniewd()) {
            ((ActivityManager) (this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        } else {
            onResume();
        }
    }

    ArrayList<String> musicListDir  = new  ArrayList<>();


    private  void addMusicFilerFrom(String dirPath){
        final File musicDir = new File(dirPath);
        if (!musicDir.exists()){
            musicDir.mkdir();
            return;
        }

        final File[] files = musicDir.listFiles();
        for (File file: files){
            final String path = file.getAbsolutePath();

            if(path.endsWith(".mp3")){
                musicListDir.add(path);
            }
        }
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDeniewd()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }

        recyclerView = findViewById(R.id.recucle);
        btPlayTopBar = findViewById(R.id.PlayTopBar);
        btPauseTopBar = findViewById(R.id.PauseTopBar);
        linearLayoutTopBar = findViewById(R.id.LinerLayoutTopBar);
        textViewTopBar = findViewById(R.id.textTopBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        log.d(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)),"12");
        addMusicFilerFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

        DataAdapter dataAdapter = new DataAdapter(this,this, musicListDir);
        recyclerView.setAdapter(dataAdapter);





    }

    @Override
    public void onNoteClick(int position) {

    }




    ArrayList<MediaPlayer> musicUsed = new ArrayList<>();
    @Override
    public void onPlayListener(int position,ArrayList<MediaPlayer> mp,ImageView bt_play,ImageView bt_pause) {

        mp.get(position).start();
//

        musicUsed.add(mp.get(position));
        log.d(musicUsed.toString(),"used_++ "+ musicUsed.size());
        if (musicUsed.size() == 2){
            try {
                if (musicUsed.get(0).equals(musicUsed.get(1))){
                    musicUsed.get(0).stop();
                    musicUsed.get(0).prepare();
                    musicUsed.get(1).start();
                    musicUsed.remove(0);

                }else {
                    musicUsed.get(0).stop();
                    musicUsed.get(0).prepare();
                    musicUsed.remove(0);
                }
            }

            catch (IOException e){
                e.printStackTrace();
            }

        }

        String music = musicListDir.get(position);
        String musicName = music.substring(music.lastIndexOf("/") + 1);
        String MusicNameWithoutMp3 = musicName.replace(".mp3","");
        addNotification(MusicNameWithoutMp3);
        TopBarButtons(MusicNameWithoutMp3,mp.get(position));
    }
    public  void TopBarButtons(String nameSong, final MediaPlayer mptb){
        linearLayoutTopBar.setVisibility(View.VISIBLE);
        textViewTopBar.setText(nameSong);

        btPauseTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mptb.pause();
                    btPauseTopBar.setVisibility(View.GONE);
                    btPlayTopBar.setVisibility(View.VISIBLE);
            }
        });

        btPlayTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mptb.start();
                btPauseTopBar.setVisibility(View.VISIBLE);
                btPlayTopBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onPauseListener(int position,ArrayList<MediaPlayer> mp,ImageView bt_play,ImageView bt_pause) {
            mp.get(position).pause();



    }


    public void addNotification(String textMessage) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_base)
                        .setContentTitle("Media_bitch")
                        .setContentText(textMessage)
                        .addAction(R.drawable.ic_play,"play",null)
                        .addAction(R.drawable.ic_pause,"pause",null);


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}