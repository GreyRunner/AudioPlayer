package com.example.audioplayer.mineFragment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.audioplayer.R;
import com.example.audioplayer.playerActivity.PlayerActivity;
import com.example.audioplayer.slideView.MusicListDetailAdapter;
import com.example.audioplayer.slideView.MusicListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import Bean.song;
import Bean.songSimplify;
import Config.GlobalData;
import DB.DBHelper;
import WebServices.Distributer;

public class musicListDetail extends Activity {


    private DBHelper dbHelper;
    private MusicListView musicListView;
    private MusicListDetailAdapter musicListDetailAdapter;

    private int listId;
    private String listName;
    private ArrayList<songSimplify> songs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_detail);
        Intent intent = getIntent();
        listId = intent.getIntExtra("id",0);
        listName = intent.getStringExtra("name");

        initView();
    }


    private void initView(){
        musicListView = findViewById(R.id.music_list_detail_listview);
        initListView();

    }

    private void initListView(){
        refreshData();
        musicListDetailAdapter = new MusicListDetailAdapter(this);
        musicListDetailAdapter.setData(String.valueOf(listId),songs,new DBHelper(this,"mineMusic",null,1));
        musicListView.setAdapter(musicListDetailAdapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*
                Log.d("cc","listid:"+listId);
                Log.d("cc","listname:"+listName);
                Log.d("cc","songId:"+songs.get(position).getSongId());
                Log.d("cc","songName:"+songs.get(position).getSongName());
                Log.d("cc","artistName:"+songs.get(position).getArtistName());
                Log.d("cc","duration:"+songs.get(position).getDuration());
                */
                //传入position和播放列表所有歌曲信息（id）,由playeractivity获取歌曲;
                ArrayList<song> songList = new ArrayList<>();
                for(int i = 0 ;i<songs.size();i++)
                    songList.add(songs.get(i).parseIntoSong());
                Intent intent = new Intent(getBaseContext(),PlayerActivity.class);
                intent.putExtra("action","playList");
                intent.putExtra("songList",songList);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

    //获取歌单内歌曲数据
    private void refreshData(){
        dbHelper = new DBHelper(this,"mineMusic",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(DBHelper.SELECT_FROM_DETAIL + " where listId = ?", new String[]{String.valueOf(listId)});
        if(cursor != null) {
            songs = new ArrayList<>();
            while (cursor.moveToNext()){
                songSimplify song = new songSimplify();
                song.setSongId(cursor.getInt(1));
                song.setSongName(cursor.getString(2));
                song.setArtistName(cursor.getString(3));
                song.setAlbumName(cursor.getString(4));
                song.setDuration(cursor.getInt(5));
                songs.add(song);
            }
        }
        dbHelper.close();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
