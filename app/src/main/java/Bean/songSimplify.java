package Bean;

import java.io.Serializable;
import java.util.ArrayList;

public class songSimplify implements Serializable {
    private int songId;
    private String songName;
    private String artistName;
    private String albumName;
    private int duration;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public song parseIntoSong(){
        song song = new song();
        ArrayList<artist> alist = new ArrayList<>();
        artist artist = new artist();
        artist.setId(-1);
        artist.setName(artistName);
        alist.add(artist);
        song.setId(songId);
        song.setName(songName);
        song.setArtists(alist);
        song.setDuration(duration);
        song.setAlbumName(albumName);
        return song;
    }
}
