package mm;

/**
 * Projekt - INDA12 - Vårterminen 2013
 * 
 * @author Marcus Heine & Mark Hobro
 */

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;

public class MusicPlayerGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	
	/** Define the colours */
	public static final Color TRACKLIST_GREY = new Color(55, 55, 55);
	public static final Color LABEL_GREY = new Color(63, 63, 63);
	public static final Color PLAYLIST_GREY = new Color(70, 70, 70);
	public static final Color WINDOW_GREY = new Color(150, 150, 150);

	private MenuBar menuBar = new MenuBar();
	
	//Just nu har jag initierat både String[] och JList här, kanske lite dumt men det funkar iaf.
	public JList<String> tracklist;
	public JList<String> playlist;
	
	public String [] playlistStrings;
	public String [] tracklistStrings;

	private TrackGUI trackGUI;

	private PlaylistGUI playlistGUI;

	private TrackInfoBar trackInfoBar;

	private JLabel titleLabel;
	private JLabel trackInfoLabel;


	public MusicPlayerGUI() {

		setVisible(true);

		setLayout(new GridBagLayout());

		setSize(1100, 630);
		setMinimumSize(new Dimension(800, 580));
		
		//setResizable(false);

		setLocationRelativeTo(null);

		setTitle("MM Music Player");

		initComponents();

		mainLayout();
		
		setColours();
		
		setBorders();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/** 
	 * Initiate the components of the main window.
	 */
	private void initComponents() {
		
		PlaylistHandler ph = new PlaylistHandler();
		playlistStrings = ph.getPlaylists();
		
		TrackHandler th = new TrackHandler();
		tracklistStrings = th.getTracks();
		
		tracklist = new JList<String>();
		playlist = new JList<String>();
		
		trackGUI = new TrackGUI(tracklist);
		playlistGUI = new PlaylistGUI(playlist);

		tracklist.setListData(tracklistStrings);
		playlist.setListData(playlistStrings);

		titleLabel = new JLabel();
		trackInfoLabel = new JLabel();
		
		trackInfoBar = new TrackInfoBar();
		
		setJMenuBar(menuBar);
		
	}
	
	/** Method that sets the layout, defining where all the components should be placed */
	private void mainLayout() {
		
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.VERTICAL;
		c.ipady = 400;
		c.ipadx = 250;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		add(playlistGUI, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 80;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 1;
		c.gridy = 0;
		add(titleLabel, c);

		c.fill = GridBagConstraints.BOTH;
		c.ipady = 40;
		c.ipadx = 20;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 2;
		c.gridx = 1;
		c.gridy = 1;
		add(trackGUI, c);

		c.ipady = 20;
		c.ipadx = 20;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 2;
		add(trackInfoLabel, c);

		c.fill = GridBagConstraints.BOTH;
		c.ipady = 80;
		c.ipadx = 20;
		c.weightx = 1.0;
		c.weighty = -1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 3;
		c.gridheight = 1;
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
		
		playlist.setBackground(PLAYLIST_GREY);
		playlist.setForeground(Color.WHITE);
		tracklist.setFixedCellHeight(20);

		titleLabel.setBackground(WINDOW_GREY);
		titleLabel.setOpaque(true);

		trackInfoLabel.setBackground(LABEL_GREY);
		trackInfoLabel.setOpaque(true);
		
		trackInfoBar.setBackground(WINDOW_GREY);
		
	}
	
	/** Method to set all the borders */
	private void setBorders() {
		
		//Create a border for each component. This is probably not the best solution, but it is a solution that works.
		//TODO - Fix this?
		Border listBorder = BorderFactory.createMatteBorder(3, 3, 3, 0, Color.BLACK);
		Border tracklistBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK);
		Border infoLabelBorder = BorderFactory.createMatteBorder(0, 3, 3, 0, Color.BLACK);
		Border titleLabelBorder = BorderFactory.createMatteBorder(3, 3, 0, 3, Color.BLACK);
		Border barBorder = BorderFactory.createMatteBorder(0, 3, 3, 3, Color.BLACK);
		
		playlistGUI.setBorder(listBorder);
		trackInfoLabel.setBorder(infoLabelBorder);
		
		trackGUI.setBorder(tracklistBorder);
		titleLabel.setBorder(titleLabelBorder);
		trackInfoBar.setBorder(barBorder);
	}



	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MusicPlayerGUI();
			}
		});
	}

}


class PlaylistGUI extends JScrollPane {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for the PlaylistGUI, whose sole purpose is too pass it on
	 * to the constructor defined in JScrollPane.
	 * 
	 * It has to be done this way, because (apparently) the only way to add a
	 * JList to a JScrollPane is to define it when it is initiated.
	 * 
	 * @param c - The component to be placed within the JScrollPane.
	 */
	public PlaylistGUI(Component c) {
		super(c);
	}

}

/**
 * A class designed to keep track of the playlists.
 * TODO - How should this be implemented?
 */
class PlaylistHandler {
	
	//private ArrayList<String> playlistsArraylist; TODO - An arraylist is needed because we don't know how many lists we want.
	
	private String [] playlists = {"Playlist 1", "Playlist 2", "Playlist 3", "Playlist 4", "Playlist 5", "Playlist 6"};
	
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
	 * @param c - The component to be placed within the JScrollPane.
	 */
	public TrackGUI(Component c){
		super(c);
	}
	
	
}

class TrackHandler {
	
	private String [] filenames;
	
	public TrackHandler() {
		listTracks();
	}
	
	public String[] getTracks() {
		return filenames;
	}
	
	public void listTracks() {
		
		//TODO - This is the heart of the program - this is where the path is set.
		File directory = new File("C:\\Users\\Marcus\\Music\\Musik\\");

		//Create a FilenameFilter and override its accept() method
		FilenameFilter filefilter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				//In case the name of the file ends with .mp3, return true.
				return name.endsWith(".mp3");
			}
		};

		filenames = directory.list(filefilter);

		

	}
	
}

/**
 * Class to handle the bar at the bottom of the frame.
 * This panel should (is going to?) contain all the buttons such as Pause, Play, Next, VOlume etc
 * 
 * TODO - Do this eh?
 */
class TrackInfoBar extends JPanel {

	private static final long serialVersionUID = 1L;

	public TrackInfoBar() {

	}

}

/**
 * Class that handles the Menu at the top of the frame.
 * 
 * This is a separate class because at some point we probably want to add ActionListeners and whatnot.
 * TODO - Add that n' stuff.
 *
 */
class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private JMenu fileMenu = new JMenu("File");
	private JMenu prefsMenu = new JMenu("Preferences");
	private JMenu helpMenu = new JMenu("Help");

	public JMenuItem playMenuItem;
	public JMenuItem backMenuItem;
	public JMenuItem exitMenuItem;
	public JMenuItem prefsMenuItem;
	public JMenuItem helpMenuItem;

	public MenuBar() {

		playMenuItem 		= new JMenuItem("Play");
		backMenuItem		= new JMenuItem("Back");
		exitMenuItem 		= new JMenuItem("Exit");
		prefsMenuItem 		= new JMenuItem("Preferences");
		helpMenuItem 		= new JMenuItem("Help");

		fileMenu.add(playMenuItem);
		fileMenu.add(backMenuItem);
		fileMenu.add(exitMenuItem);
		prefsMenu.add(prefsMenuItem);
		helpMenu.add(helpMenuItem);

		add(fileMenu);
		add(prefsMenu);
		add(helpMenu);

	}
}
