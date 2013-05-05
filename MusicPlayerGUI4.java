/**
 * Projekt - INDA12 - Vårterminen 2013
 *
 * @author Marcus Heine & Mark Hobro
 *
 * TODO - Note that this version uses Java 7.
 * 
 * Mark, du kan ju ändra så att JLists inte blir parametriserad; jag får warnings om jag inte har som det är nu.
 */

//TODO
//TODO VIKTIG JÄVLA TODO, fixa så att låtarna faktiskt automatiskt fortsätter när en låt tar slut och att repeat också fungerar för detta
//TODO

//import PopupListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.*;

//import java.io.*;
//import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;

import javazoom.jl.decoder.JavaLayerException;



public class MusicPlayerGUI2 extends JFrame implements ActionListener,
MouseListener {

	private static final long serialVersionUID = 1L;

	/** Define the colours */
	public static final Color TRACKLIST_GREY = new Color(55, 55, 55);
	public static final Color LABEL_GREY = new Color(63, 63, 63);
	public static final Color PLAYLIST_GREY = new Color(70, 70, 70);
	public static final Color WINDOW_GREY = new Color(150, 150, 150);

	public static final Font MAIN_FONT = new Font("Tahoma", Font.PLAIN, 13);

	//TODO - Här är det enda stället du behöver ändra directory.
	public String DIRECTORY = "C:\\Users\\Marcus\\Music\\Musik\\";
	//public String DIRECTORY = "C:\\Users\\Mark\\Songs\\";

	private WindowMenuBar menuBar = new WindowMenuBar();

	public PopupListener popupListener;

	// Just nu har jag initierat både String[] och JList här, kanske lite dumt men det funkar iaf.
	public JList tracklist;
	public JList playlist;

	public String[] playlistStrings;
	public String[] tracklistStrings;

	private TrackGUI trackGUI;

	private PlaylistGUI playlistGUI;

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

	private JSlider trackTimeSlider;

	private boolean playing;
	private boolean newTrack;

	private String trackName = "";

	//Variabel som används för att hålla kolla på föregående index i listan av låtar
	private ArrayList<Integer> previousTrackIndex = new ArrayList<Integer>();
	private int TRACK_INDEX;
	private boolean shuffle;
	private boolean repeat;

	public MusicPlayerGUI2() {

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
		
		savePreferences();

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		player = new MusicPlayer();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MusicPlayerGUI2();
			}
		});
	}

	/**
	 * Initiate the components of the main window.
	 */
	private void initComponents() {

		PlaylistHandler ph = new PlaylistHandler();
		playlistStrings = ph.getPlaylists();

		TrackHandler th = new TrackHandler(DIRECTORY);
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

		try {
			tracklist.setSelectedIndex(0);
		} catch(NullPointerException nullExcept) {
			System.out.println("There are no files in the directory.");
		}

		tracklist.addMouseListener(this);

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
		trackInfoBar.add(trackTimeSlider);
		

		for(int i = 0; i<playlistStrings.length; i++) {
			JMenuItem listItem = new JMenuItem(playlistStrings[i]);
			menuList.add(listItem);
			listItem.setActionCommand(playlistStrings[i]);
			listItem.addActionListener(this);

		}

		playpause.addActionListener(this);
		next.addActionListener(this);
		previous.addActionListener(this);
		shuffleButton.addActionListener(this);
		repeatButton.addActionListener(this);

		playing = false;
		shuffle = false;
		repeat = false;
		newTrack = true;

		setJMenuBar(menuBar);

	}

	/**
	 * Method that sets the layout, defining where all the components should be
	 * placed
	 */
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

	/** Method to instansiate the PopupMenu class*/
	public void createPopupMenu() {

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		int x = (int) b.getX();
		int y = (int) b.getY();

		System.out.println("X: " + x + "    Y: " + y);

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

	/** Method used to save the preferences between sessions TODO - Fix this*/
	private void savePreferences() {
		Properties prop = new Properties();
		
		String [] playlists = playlistStrings;
		 
    	try {
    		//For-loop is reversed to ensure correct file numbering
    		for(int i = 0; i < playlists.length; i++) {
    			prop.setProperty("playlistNumber" + i + "", playlists[i]);
    		}
 
    		//save properties to desired folder
    		prop.store(new FileOutputStream("C:\\Users\\Marcus\\Desktop\\config.properties"), null);
    		
    		System.out.println("Prefs created");
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
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
		if (tracklist.getSelectedIndex() == TRACK_INDEX) {
		} else {
			previousTrackIndex.add(tracklist.getSelectedIndex());
			TRACK_INDEX = previousTrackIndex.get(previousTrackIndex.size() - 1);
		}
	}

	/** Method invoked when music is to be played - TODO Städa här maybe?*/
	private void playTrack() {
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
		int frames = player.getLengthInFrames();
		return frames;
	}

	@Override /** Usual ActionPerformed class, this is where the stuff happens. */
	public void actionPerformed(ActionEvent a) {

		if(a.getActionCommand().equals("NEW PLAYLIST")) {
			System.out.println("Attempting to create new playlist"); //TODO - Creating playlists, fuck.
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
				previous.setEnabled(false);
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
		
		

	}

	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() >= 2) {
			System.out.println("Du har dubbelklickat på en låt: " + tracklist.getSelectedValue());
			setPreviousTrackIndex();
			playTrack();
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

	@Override
	public void mouseEntered(MouseEvent me) {}
	@Override
	public void mouseExited(MouseEvent me) {}
	@Override
	public void mouseReleased(MouseEvent me) {}

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
 * A class designed to keep track of the playlists. TODO - How should this be
 * implemented?
 */
class PlaylistHandler {

	// private ArrayList<String> playlistsArraylist;

	private String[] playlists = { "70's Classics", "House", "Dubstep", "Rock",
			"HipHop", "PARTY INDA CLUB" };

	public PlaylistHandler() {

	}

	public String[] getPlaylists() {
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
	private final String DIRECTORY;

	public TrackHandler(String dir) {
		DIRECTORY = dir;
		listTracks();
	}

	public String[] getTracks() {
		return filenames;
	}

	public void listTracks() {

		// TODO - This is the heart of the program - this is where the path is set.
		File directory = new File(DIRECTORY);

		// Create a FilenameFilter and override its accept() method
		FilenameFilter filefilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// In case the name of the file ends with .mp3, return true.
				return name.endsWith(".mp3");
			}
		};

		filenames = directory.list(filefilter);

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

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

		if (isSelected)
			c.setBackground(BLUE);

		else if (index % 2 == 0)
			c.setBackground(LIGHT);

		else
			c.setBackground(DARK);

		return c;
	}
}
