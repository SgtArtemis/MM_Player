import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A class designed to keep track of the playlists.
 */
public class PlaylistHandler {


	Hashtable<String, ArrayList<String>> listOfPlaylists;

	public PlaylistHandler() {
		listOfPlaylists = new Hashtable<String, ArrayList<String>>();
	}

	public void addToPlaylist(String playlistName, String songName) {

		//If playlists exists, add the specified track to the specified list, if not, create a new list for that name
		ArrayList<String> tempList = listOfPlaylists.get(playlistName);
		tempList.add(songName);
		listOfPlaylists.put(playlistName, tempList);

	}
	
	public boolean removeListFromPlaylists(String playlistName) {
		try {
			listOfPlaylists.remove(playlistName);
			return true;
		}
		catch (Exception e) {
			return false;

		}
	}
	
	//TODO - Remove songs from a specifik playlist?

	public void createAndAddPlaylist(String playlistName) {
		listOfPlaylists.put(playlistName, new ArrayList<String>());
	}

	//Get all the songs in a specific playlist
	public String[] getSpecificPlaylist(String playlistName) {

		int size = listOfPlaylists.get(playlistName).size();
		String [] listOfSongs = new String [size];

		//Basically a transfer from the ArrayList to an Array.
		for(int i = 0; i < size; i++)
			listOfSongs[i] = listOfPlaylists.get(playlistName).get(i);

		return listOfSongs;

	}

	public String[] getPlaylists() {

		String [] playlists = new String [listOfPlaylists.size()];
		Enumeration<String> enumKeys = listOfPlaylists.keys();

		int i = 0;

		//Iterate over all the keys, aka the names of the playlists.
		while(enumKeys.hasMoreElements()) {

			playlists[i] = enumKeys.nextElement();
			i++;

		}		

		return playlists;
	}
}