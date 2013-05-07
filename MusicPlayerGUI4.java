package mm;

/**
 * Projekt - INDA12 - Vårterminen 2013
 *
 * @author Marcus Heine & Mark Hobro
 *
 * 	Saker som kan vara värda att tänka på:
 * 	- Ibland får vi NullPointerException lite överallt; t.ex om man trycker på "Play" utan att ha markerat en låt.
 * 
 * 	MAIN TODO
 * 	Panel med knappar och sliders
 * 	- Fixa så att JSlidern uppdateras allt eftersom låten spelas.
 * 	- JSlider (eller liknande) för att ändra volym
 * 	- Knapparna - Placering och design
 * 
 * 	Uppspelning
 * 	- Synkronisering; hur funkar det och behöver vi det?
 * 	- Fixa så att Repeat-All funkar
 * 	- Implementera Repeat-One?
 * 	- Implementera att man ska kunna dra JSlidern och välja vartifrån i låten man ska spela? Svårt.
 * 	- Kö-funktion
 * 
 * 	Menubar
 * 	- Preferences?
 * 	- Help?
 *  	- "Back to Main List"
 *  
 *  	Övrigt
 *  	- Se till att JLabeln skriver ut vilken låt som spelas atm.
 *  	- Search/Filter-funktion
 *  	- Fixa så att man skriver in DIRECTORY via en JInputPane (?)
 *  	- Reklam? ;D
 * 
 */

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;
import java.util.Timer;

import javax.swing.*;
import javax.swing.border.Border;

import javazoom.jl.decoder.JavaLayerException;



public class MusicPlayerGUI extends JFrame implements ActionListener, MouseListener, WindowListener {

	private static final long serialVersionUID = 1L;

	private Timer tim;

	/** Define the colours */
	public static final Color TRACKLIST_GREY = new Color(55, 55, 55);
	public static final Color LABEL_GREY = new Color(63, 63, 63);
	public static final Color PLAYLIST_GREY = new Color(70, 70, 70);
	public static final Color WINDOW_GREY = new Color(150, 150, 150);

	public static final Font MAIN_FONT = new Font("Tahoma", Font.PLAIN, 13);

	//TODO - Här är det enda stället du behöver ändra directory.
	public String DIRECTORY = "C:\\Users\\Marcus\\Music\\Musik\\";
	//public String DIRECTORY = "C:\\Users\\Mark\\Songs\\";

	//TODO - Här bestäms vart "preferences" filen ska ligga; jag har lagt min på skrivbordet
	public final String FILE_DIRECTORY = "C:\\Users\\Marcus\\Desktop\\config.properties";

	//TODO - Parametrisera? Tål att tittas på om vi får tid
	public JList tracklist;
	public JList playlist;

	//Arrar of strings, playlists and tracks respectively
	public String[] playlistStrings;
	public String[] tracklistStrings;

	private TrackGUI trackGUI;

	private PlaylistGUI playlistGUI;

	public PlaylistHandler ph;
	public TrackHandler th;

	private WindowMenuBar menuBar = new WindowMenuBar(DIRECTORY);

	public PopupListener popupListener;

	private MusicPlayer player;

	private JPanel trackInfoBar;

	private JLabel titleLabel;
	private JLabel trackInfoLabel;
	private JLabel placeholder;

	public JMenuItem menuPlay = new JMenuItem("Play");
	public JMenuItem menuQueue = new JMenuItem("Queue");

	public JMenu menuList = new JMenu("Add to playlist");

	private JButton playpause = new JButton("Play");
	private JButton next = new JButton("Next");
	private JButton previous = new JButton("Previous");
	private JButton shuffleButton = new JButton("Shuffle");
	private JButton repeatButton = new JButton("Repeat");
	private JButton playlistButton = new JButton("Create new playlist");

	private JSlider trackTimeSlider;

	private boolean playing;
	private boolean newTrack;
	private boolean shuffle;
	private boolean repeat;

	private String trackName = "";

	//Variabel som används för att hålla kolla på föregående index i listan av låtar
	private ArrayList<Integer> previousTrackIndex = new ArrayList<Integer>();
	private int TRACK_INDEX;


	public MusicPlayerGUI() throws IOException {


		//setUndecorated(true); - Kanske, vi kan skapa eget "fönster" kanske typ vetnte
		//setResizable(false);

		setVisible(true);

		setLayout(new GridBagLayout());
		
		setMinimumSize(new Dimension(800, 580));
		setSize(new Dimension(1100, 660));
		
		//setResizable(false);

		setLocationRelativeTo(null);

		setTitle("MM Music Player");

		initComponents();

		mainLayout();

		setColours();

		setBorders();

		createPopupMenu();

		attemptToLoadPreferences();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		System.out.println("HEJ");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new MusicPlayerGUI();
				} catch (Exception e) {
					System.out.println("ERROR: " + e.getMessage());
				}
			}
		});
	}

	/**
	 * Initiate the components of the main window.
	 * @throws IOException 
	 */
	private void initComponents() {

		player = new MusicPlayer();
		
		th = new TrackHandler(DIRECTORY);
		ph = new PlaylistHandler();

		playlistStrings = ph.getPlaylists();
		tracklistStrings = th.getTracks();

		tracklist = new JList();
		playlist = new JList();

		trackGUI = new TrackGUI(tracklist);
		playlistGUI = new PlaylistGUI(playlist);

		tracklist.setListData(tracklistStrings);
		playlist.setListData(playlistStrings);

		int w = this.getBounds().width;

		//TODO - Ändra för att se scrollbaren
		//Om scrollbaren ska vara synlig eller inte; jag gillar när den inte är det ^^ fak uuuuu, mdi is the shit :D
		trackGUI.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		trackGUI.addMouseListener(this);

		playlistGUI.addMouseListener(this);

		try {
			tracklist.setSelectedIndex(0);
		} catch(NullPointerException nullExcept) {
			System.out.println("There are no files in the directory.");
		}

		tracklist.addMouseListener(this);
		playlist.addMouseListener(this);

		titleLabel = new JLabel();
		trackInfoLabel = new JLabel();
		placeholder = new JLabel();

		trackInfoBar = new JPanel();

		trackTimeSlider = new JSlider(0, (w-50), 0);
		trackTimeSlider.setPreferredSize(new Dimension(w-50, 20));

		trackInfoBar.add(previous);
		trackInfoBar.add(playpause);
		trackInfoBar.add(next);
		trackInfoBar.add(shuffleButton);
		trackInfoBar.add(playlistButton);
		trackInfoBar.add(trackTimeSlider);

		playpause.addActionListener(this);
		next.addActionListener(this);
		previous.addActionListener(this);
		shuffleButton.addActionListener(this);
		repeatButton.addActionListener(this);
		playlistButton.addActionListener(this);

		this.addWindowListener(this);

		playing = false;
		shuffle = false;
		repeat = false;
		newTrack = true;

		setJMenuBar(menuBar);

		updatePopupMenu();

	}


	private void updatePopupMenu() {

		String [] playlists = ph.getPlaylists();

		menuList.removeAll();

		for(int i = 0; i<playlists.length; i++) {
			JMenuItem listItem = new JMenuItem(playlists[i]);
			menuList.add(listItem);
			listItem.setActionCommand(playlists[i]);
			listItem.addActionListener(this);

		}
	}

	/** Method that defines where the components should be placed. */
	private void mainLayout() {

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.ipady = 400;
		c.ipadx = 200;
		c.gridheight = 3;
		// c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.0;
		c.weighty = 10.0;
		c.gridx = 0;
		c.gridy = 0;
		add(playlistGUI, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 60;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		// /c.anchor = GridBagConstraints.PAGE_START;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		add(titleLabel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 18;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 1;
		add(placeholder, c);

		c.fill = GridBagConstraints.BOTH;
		c.ipady = 40;
		c.ipadx = 20;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 2;
		c.gridx = 1;
		c.gridy = 2;
		add(trackGUI, c);

		// c.fill = GridBagConstraints.NONE;
		c.ipady = 1;
		c.ipadx = 1;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 3;
		add(trackInfoLabel, c);

		// c.fill = GridBagConstraints.BOTH;
		c.ipady = 68;
		c.ipadx = 20;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.gridy = 4;
		add(trackInfoBar, c);

	}

	/** Method to set all the colours */
	private void setColours() {

		tracklist.setBackground(TRACKLIST_GREY);
		tracklist.setForeground(Color.WHITE);
		tracklist.setSelectionBackground(WINDOW_GREY);
		tracklist.setFixedCellHeight(18);
		tracklist.setFont(MAIN_FONT);
		tracklist.setCellRenderer(new MyCellRenderer());

		playlist.setBackground(PLAYLIST_GREY);
		playlist.setForeground(Color.WHITE);
		tracklist.setFixedCellHeight(20);

		trackTimeSlider.setBackground(WINDOW_GREY);
		trackTimeSlider.setForeground(TRACKLIST_GREY);

		titleLabel.setBackground(TRACKLIST_GREY);
		titleLabel.setOpaque(true);

		trackInfoLabel.setBackground(LABEL_GREY);
		trackInfoLabel.setOpaque(true);

		placeholder.setBackground(WINDOW_GREY);
		placeholder.setOpaque(true);

		trackInfoBar.setBackground(WINDOW_GREY);


	}

	/** Method to set all the borders */
	private void setBorders() {

		// Create a border for each component. This is probably not the best
		// solution, but it is a solution that works.
		Border listBorder = BorderFactory.createMatteBorder(3, 3, 3, 0, Color.BLACK);
		Border tracklistBorder = BorderFactory.createMatteBorder(3, 3, 3, 3,Color.BLACK);
		Border infoLabelBorder = BorderFactory.createMatteBorder(0, 3, 3, 0, Color.BLACK);
		Border titleLabelBorder = BorderFactory.createMatteBorder(3, 3, 0, 3, Color.BLACK);
		Border barBorder = BorderFactory.createMatteBorder(0, 3, 3, 3, Color.BLACK);
		Border placeBorder = BorderFactory.createMatteBorder(3, 3, 0, 3, Color.BLACK);

		Border emptyBorder = BorderFactory.createMatteBorder(5, 5, 5, 5, TRACKLIST_GREY);

		playlistGUI.setBorder(listBorder);
		trackInfoLabel.setBorder(infoLabelBorder);

		trackGUI.setBorder(tracklistBorder);
		titleLabel.setBorder(titleLabelBorder);
		trackInfoBar.setBorder(barBorder);
		placeholder.setBorder(placeBorder);

		tracklist.setBorder(emptyBorder);
	}

	/** Method to instantiate the PopupMenu class*/
	public void createPopupMenu() {

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		menuPlay.addActionListener(this);
		menuQueue.addActionListener(this);
		menuList.addActionListener(this);

		popup.add(menuPlay);
		popup.add(menuQueue);
		popup.add(menuList);

		// Add listener to the text area so the popup menu can come up.
		popupListener = new PopupListener(popup);
		tracklist.addMouseListener(popupListener);
		titleLabel.addMouseListener(popupListener);
	}

	/** Method used to save the preferences between sessions. */
	private void savePreferences() throws FileNotFoundException {

		String [] playlists = ph.getPlaylists();

		//Clear the file; this is probably inefficient, but it works.
		PrintWriter writer = new PrintWriter(FILE_DIRECTORY);
		writer.print("");
		writer.close();



		try
		{
			//Create a FileWriter; i.e a tool that writes text to a file.
			FileWriter fw = new FileWriter(FILE_DIRECTORY, true);

			fw.write("directory=" + DIRECTORY + "\n"); //Appends the directory name to the file

			for(int i = 0; i < playlists.length; i++) {

				//Append the number and name of the playlist
				fw.write("playlist=" + playlists[i] + "\n");
				String [] s = ph.getSpecificPlaylist(playlists[i]);

				//Append the string name of each song in the 'current' playlist
				for(String song : s) {
					fw.write("song=" + song + "\n");
				}

			}
			
			fw.write("Done."); //Not really necessary, but makes for easier programming.
			
			System.out.println("Preferences and playlists saved.");
			fw.close();
		}
		catch(IOException ioe)
		{
			System.err.println("IOException: " + ioe.getMessage());
		}


	}


	private void attemptToLoadPreferences() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(FILE_DIRECTORY));
		String line = "";
		String nameOfPlaylist = "";
		String nameOfSong = "";

		//As long as there is more to read, attempt to parse the information.
		while ((line = br.readLine()) != null) {

			if(line.startsWith("directory=")) {
				line = line.replace("directory=", "");
				DIRECTORY = line;
				System.out.println("Directory has been read.");
			}

			//If a playlist is found; get the name of the list and add it to the GUI.
			if(line.startsWith("playlist=")) {
				nameOfPlaylist = line.substring(9);
				ph.createAndAddPlaylist(nameOfPlaylist);
			}

			//If a song is found, add it to the playlist. This works because of the way we write the song names to this file.
			else if(line.startsWith("song=")) {
				nameOfSong = line.substring(5);
				ph.addToPlaylist(nameOfPlaylist, nameOfSong);
			}

			else if(line.equals("Done."))
				break;
		}

		playlistStrings = ph.getPlaylists();
		playlist.setListData(playlistStrings);

		System.out.println("Preferences loaded.");
		br.close();
	}

	/** Method to help the shuffle-function */
	private int getRandomTrackIndex(){
		Random rand = new Random();
		int random = rand.nextInt(tracklist.getModel().getSize());
		return random;
	}

	private void playNextTrack() {
		if(shuffle == true){
			setPreviousTrackIndex();
			tracklist.setSelectedIndex(getRandomTrackIndex());
			playTrack();
		}
		else if(shuffle == false){
			setPreviousTrackIndex();
			//TODO Detta måste testas!
			if (repeat == true && tracklist.getSelectedIndex() == tracklist.getModel().getSize()-1){
				tracklist.setSelectedIndex(0);
				playTrack();
			}
			else{
				tracklist.setSelectedIndex(tracklist.getSelectedIndex() + 1);
				playTrack();
			}
		}
	}

	/** Method to keep track of and play the previously played tracks */
	private void playPreviousTrack() {
		tracklist.setSelectedIndex(previousTrackIndex.get(previousTrackIndex.size() - 1));
		previousTrackIndex.remove(previousTrackIndex.size() - 1);
		repaint();
		playTrack();
	}

	private void setPreviousTrackIndex() {
		//Unless you are playing and pausing the same track over and over again, this sets the previous track index
		if (tracklist.getSelectedIndex() != TRACK_INDEX) {
			previousTrackIndex.add(tracklist.getSelectedIndex());
			TRACK_INDEX = previousTrackIndex.get(previousTrackIndex.size() - 1);
		}
	}

	/** Method invoked when music is to be played - TODO Städa här maybe?*/
	private void playTrack() {
		stopTimer();

		System.out.println("Attempting to play track: " + tracklist.getSelectedValue());
		if (!trackName.equals(tracklist.getSelectedValue() + ".mp3"))
			newTrack = true;

		trackName = tracklist.getSelectedValue() + ".mp3";

		if (newTrack) {
			try {
				player.play(DIRECTORY + trackName);
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
			newTrack = false;
			playing = true;
		}

		startTimer();

	}

	/** Method which basically check if you're playing or not, defining whether to play or pause.*/
	private void pauseOrPlay() {

		if (!newTrack && !playing) {
			player.resumePlaying();
			playing = true;
			playpause.setText("Pause");
		} else if (playing && !newTrack) {
			player.pause();
			playing = false;
			playpause.setText("Play");
		}

	}


	//TODO Vet ej hur jag ska göra detta, har inte hittat så mkt på internet
	private void moveSlider(){
		int frames = getFrameCount();
		trackTimeSlider.setExtent(10);
	}

	private int getFrameCount(){
		return player.getLengthInFrames();
	}

	/** Create a playlist*/
	public void createPlaylist() {

		String playlistName = JOptionPane.showInputDialog(null, "Name your list");
		ph.createAndAddPlaylist(playlistName);

		playlistStrings = ph.getPlaylists();
		playlist.setListData(playlistStrings);

		updatePopupMenu();

	}



	@Override /** Usual ActionPerformed class, this is where the stuff happens. */
	public void actionPerformed(ActionEvent a) {


		if(a.getSource() == playlistButton) {
			createPlaylist();
		}

		if (a.getSource() == menuPlay) {
			setPreviousTrackIndex();
			playTrack();
		}

		if (a.getSource() == menuQueue) {
			//TODO - Fixa kö-funktion
			System.out.println("Queued the track " + tracklist.getSelectedValue());
		}

		if (a.getSource() == playpause) {
			setPreviousTrackIndex();
			pauseOrPlay();
			playTrack();
		}

		if (a.getSource() == next) {
			setPreviousTrackIndex();
			playNextTrack();
		}

		if (a.getSource() == previous) {
			//If you have no songs that you previously have played or if you are back at the first song you played, the button won't do anything
			//TODO Kanske kan göra den oklickbar här istället!
			if(previousTrackIndex.size() == 0){
				//previous.setEnabled(false);
			}
			else{
				playPreviousTrack();
			}
		}
		//TODO Kan ju lägga till så att det syns om shuffle är på eller inte, med färg eller text, whatever
		if (a.getSource() == shuffleButton) {
			if(shuffle == false){
				shuffle = true;
				System.out.println("Shuffle is set to on.");
			}
			else if(shuffle == true){
				shuffle = false;
				System.out.println("Shuffle is set to off.");
			}
		}
		//Repeats the whole tracklist when it's done
		if (a.getSource() == repeatButton){
			if(repeat == false){
				repeat = true;
				System.out.println("Repeat is set to on.");
			}
			else if(repeat == true){
				repeat = false;
				System.out.println("Repeat is set to off.");
			}
		}

		/*	This checks if we have clicked on any of the "Add to:" playlists.
		 * 	Because the PopupMenu is based the playlists (and is continously updated)
		 * 	we check if the returned command is equal to he name of the playlist.
		 * 	If yes, add the selected song to said playlist.
		 */
		for(String listChoice : ph.getPlaylists()) {
			if(a.getActionCommand().equals(listChoice)) {
				System.out.println("Attempting to add " + (String) tracklist.getSelectedValue() + " to " + listChoice);
				ph.addToPlaylist(listChoice, (String) tracklist.getSelectedValue());
			}
		}



	}

	public void startTimer() {

		//Create a new timer and set it to check if the song has ended twice every second.
		tim = new Timer();
		tim.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(player.hasSongEnded()) {
					setPreviousTrackIndex();
					playNextTrack();
				}

			}
		}, 500, 500);

	}

	public void stopTimer() {
		if(tim != null)
			tim.cancel();
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() >= 2) {
			if(tracklist.hasFocus()) { //Double-clicked somewhere within the tracklist.
				System.out.println("Du har dubbelklickat på en låt: " + tracklist.getSelectedValue());
				setPreviousTrackIndex();
				playTrack();
			}
			else { //Double-clicked somewhere within the list of playlists.
				if(playlist.getSelectedValue() != null) {
					System.out.println("Attempting to switch playlists");
					
					//Update the tracklist so that it shows the list of tracks in the specified playlist.
					tracklist.setListData(ph.getSpecificPlaylist((String) playlist.getSelectedValue()));
				}
			}
		}
	}




	@Override
	public void mousePressed(MouseEvent me) {
		// This code snippet check that if the right mouse is clicked, the item
		// in the JList where you clicked is selected
		if (SwingUtilities.isRightMouseButton(me)) {
			tracklist.setSelectedIndex(tracklist.locationToIndex(me.getPoint()));
		}
	}



	public void windowClosing(WindowEvent we) {

		try {
			savePreferences();
		} catch (FileNotFoundException e) {}
		
		System.exit(0);
	}


	//Methods required by the Interfaces
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}

	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}

}

class PlaylistGUI extends JScrollPane {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the PlaylistGUI, whose sole purpose is too pass it on to
	 * the constructor defined in JScrollPane.
	 * 
	 * It has to be done this way, because (apparently) the only way to add a
	 * JList to a JScrollPane is to define it when it is initiated.
	 * 
	 * @param c
	 *            - The component to be placed within the JScrollPane.
	 */
	public PlaylistGUI(Component c) {
		super(c);
	}

}






/**
 * A class designed to keep track of the playlists.
 */
class PlaylistHandler {


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







class TrackGUI extends JScrollPane {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the PlaylistGUI.
	 * 
	 * Passes the parameter to the constructor defined by JScrollPane.
	 * 
	 * @param c
	 *            - The component to be placed within the JScrollPane.
	 */
	public TrackGUI(Component c) {
		super(c);
	}

}



class TrackHandler {

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

class PopupListener extends MouseAdapter {
	public int mouseX = 0;
	public int mouseY = 0;
	private JPopupMenu menu;

	public PopupListener(JPopupMenu m) {
		menu = m;
	}

	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	private void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}

class MyCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;
	private final Color LIGHT = new Color(47, 47, 47);
	private final Color DARK = new Color(57, 57, 57);
	private final Color BLUE = new Color(175, 220, 255);

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if(isSelected)
			c.setBackground(BLUE);

		else if (index%2 == 0)
			c.setBackground(LIGHT);

		else 
			c.setBackground(DARK);

		return c;
	}
}
