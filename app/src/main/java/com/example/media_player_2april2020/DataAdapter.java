package com.example.media_player_2april2020;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static ArrayList<String> musics;
    private OnNoteListener NoteListener;
    Log log;

    LayoutInflater inflater;


    public DataAdapter(OnNoteListener onNoteListener,Context context, ArrayList<String> musics) {
        this.NoteListener =onNoteListener;
        this.musics = musics;
        this.inflater = LayoutInflater.from(context);
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view,NoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String music = musics.get(position);
        String musicName = music.substring(music.lastIndexOf("/") + 1);
        String MusicNameWithoutMp3 = musicName.replace(".mp3","");
        holder.textRes.setText(MusicNameWithoutMp3);

    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public interface OnNoteListener {
        void  onNoteClick(int position);
        void onPlayListener(int position,ArrayList<MediaPlayer> mp,ImageView bt_play,ImageView bt_pause);
        void onPauseListener(int position,ArrayList<MediaPlayer> mp,ImageView bt_play,ImageView bt_pause);
    }

    public static ArrayList<MediaPlayer> CreateMusicPlayer () {
        ArrayList<MediaPlayer> mediaPlayers = new ArrayList<>();

        try {
            for (int i = 0; i<musics.size();i++) {
                mediaPlayers.add(new MediaPlayer());
            }
            for (int i = 0;i<mediaPlayers.size();i++ ){
                mediaPlayers.get(i).setDataSource(musics.get(i));
                mediaPlayers.get(i).prepareAsync();

            }
        }
        catch (NullPointerException | IOException e){
            e.printStackTrace();
        }

        return mediaPlayers ;
    }

    public static void PrepareAllMediaPlayer(ArrayList<MediaPlayer> mp ) {
        for (int i =0;i<musics.size();i++){
            try {
                mp.get(i).prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    TextView textRes;
    ImageView bt_play , bt_pause   ;
    DataAdapter.OnNoteListener onNoteListener ;
    final ArrayList<MediaPlayer> mp = DataAdapter.CreateMusicPlayer();


    public ViewHolder(View itemView, final DataAdapter.OnNoteListener onNoteListener) {
        super(itemView);
        textRes = itemView.findViewById(R.id.itemText);
        bt_play = itemView.findViewById(R.id.pause_play);
        bt_pause = itemView.findViewById(R.id.play_pause);


        this.onNoteListener = onNoteListener;
        itemView.setOnClickListener(this);

        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                onNoteListener.onPlayListener(position,mp,bt_play,bt_pause);


            }
        });
        bt_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                onNoteListener.onPauseListener(position,mp,bt_play,bt_pause);

            }
        });
    }

    @Override
    public void onClick(View v) {
    onNoteListener.onNoteClick(getAdapterPosition());
    }
}






