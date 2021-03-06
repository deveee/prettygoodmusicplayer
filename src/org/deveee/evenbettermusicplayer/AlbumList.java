/**
   Even Better Music Player
   Copyright (C) 2014  Tyler Smith
   Copyright (C) 2019  Dawid Gan
 
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.deveee.evenbettermusicplayer;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumList extends AbstractMusicList {
	public static final String ALBUM_NAME = "ALBUM_NAME";

	private List<Map<String,String>> albums;
	private BaseAdapter listAdapter;

	private String currentTheme;
	private String currentSize;
	private boolean fromNotification = false;


	private void populateAlbums(String artistName, String artistPath){
		albums = new ArrayList<Map<String,String>>();
		
		File artist = new File(artistPath);
		Log.d("EvenBetterMusicPlayer", "storage directory = " + artist);
		if(!artist.isDirectory() || (artist.listFiles() == null)){
			Log.e("EvenBetterMusicPlayer", "Invalid artist directory provided: " +  artistPath);
			Toast.makeText(getApplicationContext(), "The selected directory is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		List<File> albumFiles = new ArrayList<File>();
		for(File albumFile : artist.listFiles()){
			if(Utils.isValidAlbumDirectory(albumFile)){
				albumFiles.add(albumFile);
			} else {
				Log.v("EvenBetterMusicPlayer", "Found invalid album " + albumFile);
			}
		}
		
		Collections.sort(albumFiles, new Comparator<File>(){

			@Override
			public int compare(File lhs, File rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
			
		});
		
		albumFiles.add(0,new File("All"));
		
		for(File albumFile : albumFiles){
			String album = albumFile.getName();
			Log.v("EvenBetterMusicPlayer", "Adding album " + album);
			Map<String,String> map = new HashMap<String, String>();
			map.put("album", album);			
			albums.add(map);
		}
		
		// If albums size is 1, then there were no directories in this folder.
		// skip straight to listing songs.
		if(albums.size() == 1)
		{
			if (!fromNotification)
			{
				Intent intent = new Intent(AlbumList.this, SongList.class);
				intent.putExtra(ALBUM_NAME, "All");
				intent.putExtra(ArtistList.ARTIST_NAME, artist.getName());
				intent.putExtra(ArtistList.ARTIST_ABS_PATH_NAME, artistPath);
				startActivity(intent);
			}
			// In this case we don't want to add the AlbumList to the back stack
			// so call 'finish' immediately.
			finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    Intent intent = getIntent();
	    final String artist = intent.getStringExtra(ArtistList.ARTIST_NAME);
	    fromNotification = intent.getBooleanExtra("From_Notification", false);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(artist);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("pref_theme", getString(R.string.light));
        String size = sharedPref.getString("pref_text_size", getString(R.string.medium));
        Log.i("EvenBetterMusicPlayer", "got configured theme " + theme);
        Log.i("EvenBetterMusicPlayer", "got configured size " + size);
        currentTheme = theme;
        currentSize = size;
        // These settings were fixed in english for a while, so check for old style settings as well as language specific ones.
        if(theme.equalsIgnoreCase(getString(R.string.dark)) || theme.equalsIgnoreCase("dark")){
        	Log.i("EvenBetterMusicPlayer", "setting theme to " + theme);
        	if(size.equalsIgnoreCase(getString(R.string.small)) || size.equalsIgnoreCase("small")){
        		setTheme(R.style.EBMPDarkSmall);
        	} else if (size.equalsIgnoreCase(getString(R.string.medium)) || size.equalsIgnoreCase("medium")){
        		setTheme(R.style.EBMPDarkMedium);
        	} else {
        		setTheme(R.style.EBMPDarkLarge);
        	}
        } else if (theme.equalsIgnoreCase(getString(R.string.light)) || theme.equalsIgnoreCase("light")){
        	Log.i("EvenBetterMusicPlayer", "setting theme to " + theme);
        	if(size.equalsIgnoreCase(getString(R.string.small)) || size.equalsIgnoreCase("small")){
        		setTheme(R.style.EBMPLightSmall);
        	} else if (size.equalsIgnoreCase(getString(R.string.medium)) || size.equalsIgnoreCase("medium")){
        		setTheme(R.style.EBMPLightMedium);
        	} else {
        		setTheme(R.style.EBMPLightLarge);
        	}
        }
		setContentView(R.layout.activity_album_list);
		
		 // Get the message from the intent
	    Log.i("EvenBetterMusicPlayer", "Getting albums for " + artist);
	    
	    final String artistPath = intent.getStringExtra(ArtistList.ARTIST_ABS_PATH_NAME);
	    populateAlbums(artist, artistPath);
        
        listAdapter = new SimpleAdapter(this, albums, R.layout.ebmp_list_item, new String[] {"album"}, new int[] {R.id.EBMPListItemText});
	    ListView lv = (ListView) findViewById(R.id.albumListView);
        lv.setAdapter(listAdapter);
        
        // React to user clicks on item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                     long id) {
            	 TextView clickedView = (TextView) view.findViewById(R.id.EBMPListItemText);;
            	 Intent intent = new Intent(AlbumList.this, SongList.class);
            	 intent.putExtra(ALBUM_NAME, clickedView.getText());
            	 intent.putExtra(ArtistList.ARTIST_NAME, artist);
            	 intent.putExtra(ArtistList.ARTIST_ABS_PATH_NAME, artistPath);
            	 startActivity(intent);
             }
        });
        

	}
	
    @Override
	protected void onResume() {
		super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("pref_theme", getString(R.string.light));
        String size = sharedPref.getString("pref_text_size", getString(R.string.medium));
        Log.i("EvenBetterMusicPlayer", "got configured theme " + theme);
        Log.i("EvenBetterMusicPlayer", "Got configured size " + size);
        if(currentTheme == null){
        	currentTheme = theme;
        } 
        
        if(currentSize == null){
        	currentSize = size;
        }
        if(!currentTheme.equals(theme) || !currentSize.equals(size)){
        	finish();
        	startActivity(getIntent());
        }
	}

}
