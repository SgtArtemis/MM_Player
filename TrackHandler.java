import java.io.File;
import java.io.FilenameFilter;

public class TrackHandler {

	private String[] filenames;
	private String DIRECTORY;

	public TrackHandler(String dir) {
		DIRECTORY = dir;
		listTracks();
	}

	public String[] getTracks() {
		return filenames;
	}

	public void setTracks(String [] s) {
		filenames = s;
	}

	public void listTracks() {

		File directory = new File(DIRECTORY);

		// Create a FilenameFilter and override its accept() method
		FilenameFilter filefilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// In case the name of the file ends with .mp3, return true.
				return name.endsWith(".mp3");
			}
		};

		filenames = directory.list(filefilter);

		//TODO - Note, if the directory is faulty, we get an exception here.
		try {
			for (int i = 0; i < filenames.length; i++) {
				String name = filenames[i].replace(".mp3", "");
				filenames[i] = name;
			}
		} catch(NullPointerException nullExec) {
			System.out.println("No files in the directory.");
		}

	}

}