package com.example.audioplayer.slideView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.audioplayer.R;
import DB.DBHelper;

import java.util.ArrayList;
import java.util.List;
import Bean.*;

public class MusicListDetailAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<songSimplify> songList;
    private DBHelper dbHelper;
    private LayoutInflater inflater;

    private String musicListId;
    private List<String> idList,nameList,artistList;


    public MusicListDetailAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(String musicListId, ArrayList<songSimplify> songList,DBHelper dbHelper) {
        //歌单编号
        this.musicListId = musicListId;
        this.songList = songList;
        parseSongList();
        this.dbHelper = dbHelper;
        notifyDataSetChanged();
    }

    public void parseSongList(){

        idList = new ArrayList<>();
        nameList = new ArrayList<>();
        artistList = new ArrayList<>();

        for(int i = 0; i<songList.size();i++){
            idList.add(String.valueOf(songList.get(i).getSongId()));
            nameList.add(songList.get(i).getSongName());
            artistList.add(songList.get(i).getArtistName());
        }

    }

    @Override
    public int getCount() {
        if (songList == null) {
            return 0;
        }
        return songList.size();
    }

    @Override
    public songSimplify getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MusicListDetailAdapter.ViewHolder viewHolder = null;
        deleteView slideView = (deleteView) convertView;
        if (slideView == null) {
            View itemView = inflater.inflate(R.layout.music_list_detail_adapter, null);

            slideView = new deleteView(context);
            slideView.setContentView(itemView);
            viewHolder = new MusicListDetailAdapter.ViewHolder(slideView);
            slideView.setTag(viewHolder);
        } else {
            viewHolder = (MusicListDetailAdapter.ViewHolder) slideView.getTag();
        }
        slideView.shrink();//设置删除按钮恢复原来的位置，即消失
        viewHolder.tv_music_name.setText(nameList.get(position));
        viewHolder.tv_music_artists.setText(artistList.get(position));

        viewHolder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("data","position:"+position);
                removeCurrentMusic(position);
                songList.remove(position);
                idList.remove(position);
                nameList.remove(position);
                artistList.remove(position);

                notifyDataSetChanged();
            }
        });

        return slideView;
    }

    static class ViewHolder {
        public TextView tv_music_name;
        public TextView tv_music_artists;
        public ViewGroup deleteHolder;
        ViewHolder(View view) {
            tv_music_name = (TextView) view.findViewById(R.id.music_list_detail_music_name);
            tv_music_artists = (TextView) view.findViewById(R.id.music_list_detail_music_artists);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }

    public void removeCurrentMusic(int position){
        //int deleteId = Integer.parseInt(id.get(position));
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(DBHelper.DELETE_FROM_MUSIC_DETAIL_ONE,new String[]{idList.get(position),musicListId});
        cursor.getCount();
    }
}
