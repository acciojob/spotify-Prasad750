package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {

        Artist artist=null;
        for(Artist a: artists)
        {
            if(a.getName().equals(artistName))
            {
               artist=a;
               break;
            }
        }
        if(artist==null)
        {
            artist=this.createArtist(artistName);
            artists.add(artist);
        }
        Album album=new Album(title);
        album.setReleaseDate(new Date());
        albums.add(album);

        artistAlbumMap.put(artist,albums);
        return album;



    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album=null;
        for(Album a:albums)
        {
            if(a.getTitle().equals(albumName))
            {
                album=a;
                break;
            }
        }
        if(album==null)
        {
            throw new Exception("Album does not exist");
        }
        Song song=new Song(title,length);
        songs.add(song);
        List<Song> songList=new ArrayList<>();
        if(albumSongMap.containsKey(album))
        {
            songList=albumSongMap.get(album);
        }
        songList.add(song);
        albumSongMap.put(album,songList);
        return song;


    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user=null;

        for(User u:users)
        {
            if(user.getMobile().equals(mobile))
            {
                user=u;
                break;
            }
        }
        if(user==null)
        {
            throw new Exception("User does not exist");
        }
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        List<Song> listOnLength=new ArrayList<>();
        for(Song s:songs)
        {
            if(s.getLength()==length)
            {
                listOnLength.add(s);
            }
        }
        playlistSongMap.put(playlist,listOnLength);

        List<User> userList=new ArrayList<>();
        userList.add(user);
        playlistListenerMap.put(playlist,userList);
        creatorPlaylistMap.put(user,playlist);

        List<Playlist> playlist1=new ArrayList<>();
        if(userPlaylistMap.containsKey(user))
        {
            playlist1=userPlaylistMap.get(user);
        }
        playlist1.add(playlist);
        userPlaylistMap.put(user,playlist1);

        return playlist;






    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=null;

        for(User u:users)
        {
            if(user.getMobile().equals(mobile))
            {
                user=u;
                break;
            }
        }
        if(user==null)
        {
            throw new Exception("User does not exist");
        }
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);

        List<Song> listOnTitles=new ArrayList<>();


            for(Song s :songs)
            {
                if(songTitles.contains(s.getTitle()))
                {
                    listOnTitles.add(s);
                }
            }

        playlistSongMap.put(playlist,listOnTitles);

        List<User> userList=new ArrayList<>();
        userList.add(user);
        playlistListenerMap.put(playlist,userList);


        creatorPlaylistMap.put(user,playlist);

        List<Playlist> playlist1=new ArrayList<>();
        if(userPlaylistMap.containsKey(user))
        {
            playlist1=userPlaylistMap.get(user);
        }
        playlist1.add(playlist);
        userPlaylistMap.put(user,playlist1);

        return playlist;



    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=null;
        for(User u: users)
        {
            if(user.getMobile().equals(mobile))
            {
                user=u;
            }
        }
        if (user==null)
        {
            throw new Exception("User does not exist");
        }
        Playlist playlist=null;
        for(Playlist p:playlists)
        {
            if(p.getTitle().equals(playlistTitle))
            {
                playlist=p;
            }
        }
        if(playlist==null)
        {
            throw new Exception("Playlist does not exist");
        }


        if(creatorPlaylistMap.containsKey(user))
        {
            return playlist;
        }

        List<User> listnerList=playlistListenerMap.get(playlist);

        for(User u:listnerList)
        {
            if(u==user)
            {
                return playlist;
            }
        }
        listnerList.add(user);
        playlistListenerMap.put(playlist,listnerList);

        List<Playlist> playlists1=new ArrayList<>();
        if(userPlaylistMap.containsKey(user))
        {
            playlists1=userPlaylistMap.get(user);
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=null;
        for(User u:users)
        {
            if(u.getMobile().equals(mobile))
            {
                user=u;
                break;
            }
        }
        if(user==null)
        {
            throw new Exception("User does not exist");
        }

        Song song=null;
        for(Song s:songs)
        {
            if(s.getTitle().equals(songTitle))
            {
                song=s;
                break;
            }
        }
        if(song==null)
        {
            throw new Exception("Song does not exist");
        }


        List<User> userList;
        if(songLikeMap.containsKey(song))
        {
             userList = songLikeMap.get(song);
            if(userList.contains(user))
            {
                return song;
            }
        }
        else {
           userList = new ArrayList<>();
        }


        int likes= song.getLikes()+1;
        song.setLikes(likes);
        userList.add(user);
        songLikeMap.put(song,userList);



            Album album=null;
            for(Album a:albumSongMap.keySet())
            {
                List<Song> songList=albumSongMap.get(a);
                if(songList.contains(song))
                {
                    album=a;
                    break;
                }

            }
            Artist artist=null;
            for(Artist a : artistAlbumMap.keySet())
            {
                List<Album> albumList=artistAlbumMap.get(a);
                if(albumList.contains(album))
                {
                    artist=a;
                    break;
                }
            }
            likes=artist.getLikes()+1;
            artist.setLikes(likes);
            artists.add(artist);
            return song;

    }

    public String mostPopularArtist() {
        int max=0;
        Artist artist=null;

        for (Artist a:artists)
        {
            if(max<=a.getLikes())
            {
                artist=a;
                max=a.getLikes();
            }
        }
        return artist==null ? null :artist.getName();

    }

    public String mostPopularSong() {
        int max=0;
        Song song=null;
        for (Song s:songLikeMap.keySet())
        {
            if(max<=s.getLikes())
            {
                max=s.getLikes();
                song=s;
            }
        }
        return song==null ? null :song.getTitle();
    }
}
