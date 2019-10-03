package MediaService;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.audioplayer.MainActivity;
import com.example.audioplayer.playerActivity.PlayerActivity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import Bean.song;
import Config.GlobalData;

public class MediaService extends Service {
    public MediaPlayer mediaPlayer;
    public int songId = -1;
    public int seekLength;
    public int duration = 0;
    private Timer timer;
    private TimerTask task;
    private song loadedSong;

    public ArrayList<song> playList = null;
    public int position = 0;

    public int playLoopType = 1;
    public static final int PLAY_LOOP = 1;
    public static final int PLAY_RANDOM = 2;
    public static final int PLAY_LISTPLAY = 3;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        loadedSong = null;
        playLoopType = PLAY_LOOP;
        myReset();
    }

    private void playPositionMusic() {
        try {

            mediaPlayer.reset();

            loadedSong = playList.get(position);
            songId = playList.get(position).getId();
            mediaPlayer.setDataSource(GlobalData.dirPath + "/music_" + playList.get(position).getId() + ".mp3");

            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
            songId = playList.get(position).getId();
            mediaPlayer.start();

            Message message = Message.obtain();
            message.what = GlobalData.MSG_SONG_NEWSONGINFO;
            Bundle bundle = new Bundle();
            bundle.putSerializable("songInfo", playList.get(position));
            message.setData(bundle);
            PlayerActivity.handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void myReset() {
        mediaPlayer.reset();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private class MyBinder extends Binder implements exposedServices {

        @Override
        public boolean playFromStart(int id, String mediaPath) {
            try {
                myReset();
                mediaPlayer.setDataSource(mediaPath);
                mediaPlayer.prepare();
                duration = mediaPlayer.getDuration();
                updateSeekBar();
                songId = id;
                mediaPlayer.start();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean resume() {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(seekLength);
                System.out.println("##currentSongId:" + songId);
                updateSeekBar();
                mediaPlayer.start();
                return true;
            }
            return false;
        }

        @Override
        public boolean pause() {
            if (mediaPlayer.isPlaying()) {
                seekLength = mediaPlayer.getCurrentPosition();
                System.out.println("##currentSongId:" + songId);
                stopUpdateSeekBar();
                mediaPlayer.pause();
                return true;
            }
            return false;
        }

        @Override
        public boolean stop() {
            mediaPlayer.reset();
            return true;
        }

        @Override
        public boolean prev() {
            try{
                if(playList != null){
                    stopUpdateSeekBar();
                    position = (position - 1) >= 0 ? position - 1 : playList.size()-1;
                    playPositionMusic();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean next() {
            try{
                if(playList != null){
                    stopUpdateSeekBar();
                    position = (position + 1) == playList.size() ? 0 : position +1;
                    playPositionMusic();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean isPlaying() {
            if (mediaPlayer.isPlaying())
                return true;
            return false;
        }

        @Override
        public int getSeekLength() {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        @Override
        public void updateSeekBar() {
            //保持音乐播放器seekbar刷新服务仅存在一个
            if (timer != null && task != null)
                return;
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.what = GlobalData.MSG_CURRENT_POSITION;
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentPosition", currentPosition);
                    Log.d("#MEDIA", "currentPosition:" + currentPosition);
                    message.setData(bundle);
                    PlayerActivity.handler.sendMessage(message);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopUpdateSeekBar();
                            if (playList == null) {
                                Log.d("check", "null playlist");
                            }
                            seekLength = 0;
                            switch (playLoopType) {
                                case PLAY_LOOP: {
                                    updateSeekBar();
                                    mediaPlayer.start();
                                    break;
                                }
                                case PLAY_RANDOM: {
                                    if (playList != null) {
                                        updateSeekBar();
                                        int newPosition = position;
                                        while (newPosition == position) {
                                            newPosition = new Random().nextInt(playList.size() - 1);
                                        }
                                        Log.d("position", "#Random position:" + newPosition);
                                        position = newPosition;
                                        playPositionMusic();
                                    }
                                    break;
                                }
                                case PLAY_LISTPLAY: {
                                    if (playList != null) {
                                        updateSeekBar();
                                        position = position + 1 == playList.size() ? 0 : position + 1;
                                        Log.d("position", "#LISTPLAY position:" + position);
                                        playPositionMusic();
                                    }
                                    break;
                                }
                            }
                        }
                    });
                }
            };
            timer.schedule(task, 500, 500);
        }

        @Override
        public void seekToPosition(int currentPosition) {
            mediaPlayer.seekTo(currentPosition);
            seekLength = currentPosition;
        }

        @Override
        public void stopUpdateSeekBar() {
            if (timer != null)
                timer.cancel();

            if (task != null)
                task.cancel();

            timer = null;
            task = null;
        }

        @Override
        public int getSongId() {
            return songId;
        }

        @Override
        public void setSong(song song) {
            loadedSong = song;
        }

        @Override
        public song getSong() {
            return loadedSong;
        }

        @Override
        public int getLoopMode() {
            return playLoopType;
        }

        @Override
        public void setLoopMode(int mode) {
            playLoopType = mode;
        }

        @Override
        public ArrayList<song> getPlayList() {
            return playList;
        }

        @Override
        public void setPlayList(ArrayList<song> pL) {
            playList = pL;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public void setPosition(int posi) {
            position = posi;
        }
    }
}
