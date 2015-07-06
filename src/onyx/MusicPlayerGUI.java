/**
 * Projekt - INDA12 - Vårterminen 2013
 * 
 * KTH - Introduktion till Datalogi
 * 
 * @author Marcus Heine & Mark Hobro
 * 
 */

/**
 * TODO:
 * 
 * Essential:
 * Volume handling - Custom slider [DONE]
 * Song fast-forwarding and rewinding via TimeSlider
 * 
 * Major:
 * Icon resizing - fuck using Paint.NET on Ubuntu 14.04 [DONE]
 * Implement search - take note when queueing for a search query
 * Separate song class?
 * Song timing - display next to timeTrackSlider - JLabels is probably the easiest option
 * 
 * Minor:
 * "New Playlist" icon [DONE]
 * Combine MusicPlayer and MusicFileByFrames? Change Frames class to Track?
 * 
 * Wouldn't it be nice:
 * Properly commented code
 * Multiple directories - Probably requires song class, where each song has a "path"
 * 
 * 
 */

package onyx;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Timer;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javazoom.jl.decoder.JavaLayerException;

public class MusicPlayerGUI extends JFrame implements ActionListener,
MouseListener, WindowListener, KeyListener, ComponentListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	private Timer tim;

	/** Spotify Colour Scheme */
	public static final Color COLOR_TRACKLIST = new Color(55, 55, 55);
	public static final Color COLOR_LABEL = new Color(63, 63, 63);
	public static final Color COLOR_PLAYLIST = new Color(70, 70, 70);
	public static final Color COLOR_WINDOW = new Color(120, 120, 120);
	
	/** Onyx Colour Scheme */
//	public static final Color COLOR_TRACKLIST = new Color(20, 80, 20);
//	public static final Color COLOR_LABEL = new Color(25, 90, 25);
//	public static final Color COLOR_PLAYLIST = new Color(20, 70, 20);
//	public static final Color COLOR_WINDOW = new Color(20, 70, 20);

	public static final Font MAIN_FONT = new Font("Verdana", Font.BOLD, 12);

	public String DIRECTORY = "";

	public JList<String> tracklist;
	public JList<String> playlist;

	// Arrays of strings, playlists and tracks respectively
	public String[] playlistStrings;
	public String[] tracklistStrings;

	private JOptionPane jop;
	private JDialog jdialog;

	private JScrollPane trackGUI;
	private JScrollPane playlistGUI;

	public PlaylistHandler ph = new PlaylistHandler();
	public TrackHandler th;

	private JMenuBar menuBar = new JMenuBar();

	public PopupHandler popupListener;

	private MusicPlayer player;
	
	private VolumeHandler volume = new VolumeHandler();

	private JPanel trackInfoBar = new JPanel();

	// Labels
	private JLabel titleLabel;
	private JLabel trackInfoLabel;
	private JLabel placeholder;

	// Menu items
	private JMenu menuList = new JMenu("Add to playlist");
	private JMenu fileMenu = new JMenu("File");
	private JMenu optionsMenu = new JMenu("Options");
	private JMenu helpMenu = new JMenu("Help");

	// Menu items
	public JMenuItem menuPlay;
	public JMenuItem menuQueue;
	public JMenuItem playMenuItem;
	public JMenuItem backMenuItem;
	public JMenuItem exitMenuItem;
	public JMenuItem cdMenuItem;
	public JMenuItem scrollbarMenuItem;
	public JMenuItem helpMenuItem;

	// Icons
	private ImageIcon playIcon;
	private ImageIcon pauseIcon;
	private ImageIcon nextIcon;
	private ImageIcon previousIcon;
	private ImageIcon shuffleTrueIcon;
	private ImageIcon shuffleFalseIcon;
	private ImageIcon repeatTrueIcon;
	private ImageIcon repeatOneIcon;
	private ImageIcon repeatFalseIcon;
	private ImageIcon playlistIcon;

	// Buttons
	private JButton playpauseButton;
	private JButton nextButton;
	private JButton previousButton;
	private JButton shuffleButton;
	private JButton repeatButton;
	private JButton playlistButton;

	// Slider to show position in song
	private JSlider trackTimeSlider;
	
	private JSlider volumeSlider;

	// Assorted booleans
	private boolean playing;
	private boolean newTrack;
	private boolean shuffle;
	private boolean repeat;
	private boolean repeatOne;

	private String trackName = "";
	
	private int TRACK_INDEX;
	
	private float currentVolume;

	// Variable used to keep track of the songs that have been played.
	private ArrayList<Integer> previousTrackIndex = new ArrayList<Integer>();

	// List for the queue-function
	private ArrayList<Integer> queueList = new ArrayList<Integer>();

	// List for the buttons to simplify the code
	private ArrayList<JButton> listOfButtons = new ArrayList<JButton>();

	public MusicPlayerGUI() throws IOException {

		setVisible(true);

		setLayout(new GridBagLayout());

		setMinimumSize(new Dimension(800, 500));
		setSize(new Dimension(900, 500));

		setLocationRelativeTo(null); // Center window

		setTitle("Onyx Music Player");

		attemptToLoadPreferences();

		askForDirectory();

		initMenuBar();

		initIcons();

		initComponents();

		mainLayout();

		updateBarLayout();

		setColours();

		setBorders();

		createPopupMenu();

		updatePopupMenu();

		addComponentListener(this);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new MusicPlayerGUI();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Initialize the components of the main window. */
	private void initComponents() {

		player = new MusicPlayer();

		th = new TrackHandler(DIRECTORY);

		playlistStrings = ph.getPlaylists();
		tracklistStrings = th.getTracks();

		tracklist = new JList<String>();
		playlist = new JList<String>();

		trackGUI = new JScrollPane(tracklist);
		playlistGUI = new JScrollPane(playlist);

		tracklist.setListData(tracklistStrings);
		playlist.setListData(playlistStrings);

		titleLabel = new JLabel();
		trackInfoLabel = new JLabel();
		placeholder = new JLabel();

		titleLabel.setText("Library");
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		trackInfoBar = new JPanel();

		menuQueue = new JMenuItem("Queue");
		menuPlay = new JMenuItem("Play");

		trackGUI.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		trackGUI.addMouseListener(this);

		playlistGUI.addMouseListener(this);

		tracklist.setSelectedIndex(0);

		trackTimeSlider = new JSlider(0, 5000, 0);
		trackTimeSlider.setPreferredSize(new Dimension(getBounds().width - 50, 20));
		trackTimeSlider.setEnabled(false);
		trackTimeSlider.addMouseListener(this);
		
		volumeSlider = new JSlider();
		volumeSlider.setPreferredSize(new Dimension(120, 20));
		volumeSlider.setUI(new VolumeSlider(volumeSlider));
		volumeSlider.setOpaque(false);

		playpauseButton.addActionListener(this);
		nextButton.addActionListener(this);
		previousButton.addActionListener(this);
		shuffleButton.addActionListener(this);
		repeatButton.addActionListener(this);
		playlistButton.addActionListener(this);

		tracklist.addMouseListener(this);
		playlist.addMouseListener(this);
		trackInfoLabel.addMouseListener(this);

		playlist.addKeyListener(this);

		this.addWindowListener(this);

		playing = false;
		shuffle = false;
		repeat = false;
		repeatOne = false;
		newTrack = true;

		setJMenuBar(menuBar);

	}

	/** Initialize the components in the Menu bar at the top of the window */
	private void initMenuBar() {

		playMenuItem = new JMenuItem("Play");
		backMenuItem = new JMenuItem("Main list");
		exitMenuItem = new JMenuItem("Exit");
		cdMenuItem = new JMenuItem("Change directory");
		helpMenuItem = new JMenuItem("Help");
		scrollbarMenuItem = new JMenuItem("Toggle scrollbar");

		playMenuItem.addActionListener(this);
		backMenuItem.addActionListener(this);
		exitMenuItem.addActionListener(this);
		cdMenuItem.addActionListener(this);
		helpMenuItem.addActionListener(this);
		scrollbarMenuItem.addActionListener(this);

		// Add individual JMenuItems to the appropriate JMenu
		fileMenu.add(playMenuItem);
		fileMenu.add(backMenuItem);
		fileMenu.add(exitMenuItem);

		optionsMenu.add(cdMenuItem);
		optionsMenu.add(scrollbarMenuItem);

		helpMenu.add(helpMenuItem);

		// Add the JMenu's to the actual Menu
		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);

		menuBar.setBackground(COLOR_WINDOW);
		menuBar.setBorder(BorderFactory.createMatteBorder(3, 3, 1, 3, Color.BLACK));

		fileMenu.setForeground(Color.black);
		optionsMenu.setForeground(Color.black);
		helpMenu.setForeground(Color.black);

	}

	/** Initialize the icons in the buttons. */
	private void initIcons() {

		/*
		 * This part of the code is far from obvious. Please consult
		 * http://vimeo.com/20685294 to see how we solved this. These items have
		 * been set to be included in the classpath. This was done using
		 * Eclipse. We're not quite sure how this would would be implemented
		 * without Eclipse.
		 */
		playIcon = new ImageIcon(getClass().getClassLoader().getResource("play.png"));
		pauseIcon = new ImageIcon(getClass().getClassLoader().getResource("pause.png"));
		nextIcon = new ImageIcon(getClass().getClassLoader().getResource("next.png"));
		previousIcon = new ImageIcon(getClass().getClassLoader().getResource("previous.png"));
		shuffleTrueIcon = new ImageIcon(getClass().getClassLoader().getResource("shuffle_true.png"));
		shuffleFalseIcon = new ImageIcon(getClass().getClassLoader().getResource("shuffle_false.png"));
		repeatTrueIcon = new ImageIcon(getClass().getClassLoader().getResource("repeat_all.png"));
		repeatOneIcon = new ImageIcon(getClass().getClassLoader().getResource("repeat_one.png"));
		repeatFalseIcon = new ImageIcon(getClass().getClassLoader().getResource("repeat_false.png"));
		playlistIcon = new ImageIcon(getClass().getClassLoader().getResource("newplaylist.png"));


		// Set the icons on the buttons
		playpauseButton = new JButton(playIcon);
		nextButton = new JButton(nextIcon);
		previousButton = new JButton(previousIcon);
		shuffleButton = new JButton(shuffleFalseIcon);
		repeatButton = new JButton(repeatFalseIcon);
		playlistButton = new JButton(playlistIcon);

		listOfButtons.add(nextButton);
		listOfButtons.add(playpauseButton);
		listOfButtons.add(previousButton);
		listOfButtons.add(shuffleButton);
		listOfButtons.add(repeatButton);
		listOfButtons.add(playlistButton);

		// Make each button a certain size and colour, and remove bordrs and focus
		for (JButton b : listOfButtons) {
			b.setBackground(COLOR_WINDOW);
			b.setPreferredSize(new Dimension(45, 45));
			b.setBorder(null);
			b.setOpaque(false);
			b.setContentAreaFilled(false);
			b.setBorderPainted(false);
			b.setFocusPainted(false);
		}

		// repeatButton And shuffleButton are supposed to be smaller
		repeatButton.setPreferredSize(new Dimension(35, 35));
		shuffleButton.setPreferredSize(new Dimension(35, 35));

	}

	/** Method which, if applicable, asks for the directory where the files are. */
	private void askForDirectory() {

		// If the DIRECTORY is *not* empty it means it's been changed by the
		// preferences
		if (DIRECTORY.equals("")) {

			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			String message = "<html><div align='center'>Hello and welcome to Onyx!<br>To get started, please navigate to the directory of your music files.</div></html>";
			JLabel msgLabel = new JLabel(message, JLabel.CENTER);
			JOptionPane.showMessageDialog(null, msgLabel, "Onyx Music Player",
					JOptionPane.INFORMATION_MESSAGE);

			int choice = fc.showOpenDialog(null);

			if (choice == JFileChooser.APPROVE_OPTION) {
				File dir = fc.getSelectedFile();

				this.DIRECTORY = dir.toString();

				// If you're using Windows and the directory is incomplete,
				// finish it.
				if (!DIRECTORY.endsWith("\\"))
					DIRECTORY = DIRECTORY.concat("\\");

			} else {
				System.exit(0);
			}
		}
	}

	/** Method that updates the popup menu in the TrackGUI. */
	private void updatePopupMenu() {

		String[] playlists = ph.getPlaylists();

		// Remove all playlists from the PopupMenu
		menuList.removeAll();

		// Add each playlist to the popupmenu, and give a it's name as the
		// actionCommand
		for (int i = 0; i < playlists.length; i++) {
			JMenuItem listItem = new JMenuItem(playlists[i]);
			menuList.add(listItem);
			listItem.setActionCommand(playlists[i]);
			listItem.addActionListener(this);

		}
	}

	/** Method that defines where the components should be placed. */
	private void mainLayout() {

		// Far from perfect, but it works.
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.ipady = 400;
		c.ipadx = 160;
		c.gridheight = 3;
		c.weightx = 0.0;
		c.weighty = 10.0;
		c.gridx = 0;
		c.gridy = 0;
		add(playlistGUI, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 60;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
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

		c.ipady = 50;
		c.ipadx = 20;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.gridy = 4;
		add(trackInfoBar, c);


	}

	/** Set the layout of the bar below the tracks */
	private void updateBarLayout() {
		SpringLayout sl = new SpringLayout();
		trackInfoBar.setLayout(sl);

		trackInfoBar.add(repeatButton);
		trackInfoBar.add(shuffleButton);
		trackInfoBar.add(shuffleButton);
		trackInfoBar.add(previousButton);
		trackInfoBar.add(playpauseButton);
		trackInfoBar.add(nextButton);
		trackInfoBar.add(volumeSlider);
		trackInfoBar.add(playlistButton);
		trackInfoBar.add(trackTimeSlider);
		

		int width = this.getWidth();

		// Define "springs" between the different components.
		sl.putConstraint(SpringLayout.WEST, previousButton, 20,
				SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.NORTH, previousButton, 0,
				SpringLayout.NORTH, this);

		sl.putConstraint(SpringLayout.WEST, playpauseButton, 2,
				SpringLayout.EAST, previousButton);
		sl.putConstraint(SpringLayout.NORTH, playpauseButton, 0,
				SpringLayout.NORTH, this);

		sl.putConstraint(SpringLayout.NORTH, nextButton, 0,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, nextButton, 2,
				SpringLayout.EAST, playpauseButton);
		
		sl.putConstraint(SpringLayout.NORTH, trackTimeSlider, 15,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, trackTimeSlider, 25,
				SpringLayout.EAST, nextButton);

		sl.putConstraint(SpringLayout.NORTH, repeatButton, 5,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, repeatButton, 5,
				SpringLayout.EAST, trackTimeSlider);

		sl.putConstraint(SpringLayout.NORTH, shuffleButton, 5,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, shuffleButton, 5,
				SpringLayout.EAST, repeatButton);
		
		sl.putConstraint(SpringLayout.NORTH, playlistButton, 0,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, playlistButton, 2,
				SpringLayout.EAST, shuffleButton);
		
		sl.putConstraint(SpringLayout.NORTH, volumeSlider, 15,
				SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.WEST, volumeSlider, 5,
				SpringLayout.EAST, playlistButton);
		

		trackTimeSlider.setPreferredSize(new Dimension(width - 450, 20));
		trackTimeSlider.setUI(new TimeSlider(trackTimeSlider));
		
		

	}

	/** Method to set all the colours */
	private void setColours() {

		tracklist.setBackground(COLOR_TRACKLIST);
		tracklist.setForeground(Color.BLACK);
		tracklist.setSelectionBackground(COLOR_WINDOW);
		tracklist.setFixedCellHeight(22);
		tracklist.setFont(MAIN_FONT);
		tracklist.setCellRenderer(new ListCellRenderer());

		playlist.setBackground(COLOR_PLAYLIST);
		playlist.setForeground(Color.BLACK);

		trackTimeSlider.setBackground(COLOR_WINDOW);
		trackTimeSlider.setForeground(COLOR_TRACKLIST);
		
		volumeSlider.setMaximum(95);
		volumeSlider.setMinimum(5);
		volumeSlider.setSnapToTicks(true); //TODO
		volumeSlider.setMajorTickSpacing(5);
		volumeSlider.setValue((int) (volume.getMasterOutputVolume() * 100));
		volumeSlider.addChangeListener(this); //We add the changeListener after the value has been set,as to not change to master volume
		volumeSlider.setFocusable(false);
		

		titleLabel.setBackground(COLOR_TRACKLIST);
		titleLabel.setForeground(Color.BLACK);
		titleLabel.setOpaque(true);

		trackInfoLabel.setBackground(COLOR_LABEL);
		trackInfoLabel.setForeground(Color.BLACK);
		trackInfoLabel.setOpaque(true);
		trackInfoLabel.setFont(MAIN_FONT);

		placeholder.setBackground(COLOR_WINDOW);
		placeholder.setOpaque(true);

		trackInfoBar.setBackground(COLOR_WINDOW);

	}

	/** Method to set all the borders */
	private void setBorders() {

		// Create a border for each component.
		Border listBorder = BorderFactory.createMatteBorder(3, 3, 3, 0,
				Color.BLACK);
		Border tracklistBorder = BorderFactory.createMatteBorder(3, 3, 3, 3,
				Color.BLACK);
		Border infoLabelBorder = BorderFactory.createMatteBorder(0, 3, 3, 0,
				Color.BLACK);
		Border titleLabelBorder = BorderFactory.createMatteBorder(3, 3, 0, 3,
				Color.BLACK);
		Border barBorder = BorderFactory.createMatteBorder(0, 3, 3, 3,
				Color.BLACK);
		Border placeBorder = BorderFactory.createMatteBorder(3, 3, 0, 3,
				Color.BLACK);
		Border emptyBorder = BorderFactory.createMatteBorder(0, 3, 0, 0,
				COLOR_TRACKLIST);

		playlistGUI.setBorder(listBorder);
		trackInfoLabel.setBorder(infoLabelBorder);
		trackGUI.setBorder(tracklistBorder);
		titleLabel.setBorder(titleLabelBorder);
		trackInfoBar.setBorder(barBorder);
		placeholder.setBorder(placeBorder);
		tracklist.setBorder(emptyBorder);
	}

	/** Method to instantiate the PopupMenu class */
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
		popupListener = new PopupHandler(popup);
		tracklist.addMouseListener(popupListener);
		titleLabel.addMouseListener(popupListener);
	}

	/** Method used to save the preferences between sessions. */
	private void savePreferences() throws FileNotFoundException {

		String[] playlists = ph.getPlaylists();

		// Clear the file; this is probably inefficient, but it works.
		PrintWriter writer = new PrintWriter("config.properties");
		writer.print("");
		writer.close();

		try {
			// Create a FileWriter; i.e a tool that writes text to a file.
			FileWriter fw = new FileWriter("config.properties", true);

			fw.write("directory=" + DIRECTORY + "\n"); // Appends the directory
			// name to the file

			for (int i = 0; i < playlists.length; i++) {

				// Append the number and name of the playlist
				fw.write("playlist=" + playlists[i] + "\n");
				String[] s = ph.getSpecificPlaylist(playlists[i]);

				// Append the string name of each song in the 'current' playlist
				for (String song : s) {
					fw.write("song=" + song + "\n");
				}

			}

			fw.write("Done."); // Not really necessary, but makes for easier
			// programming.

			System.out.println("Preferences and playlists saved.");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}

	}

	/** Method which attempts to read the file set by savePreferences(). */
	private void attemptToLoadPreferences() throws IOException {

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"config.properties"));
			String line = "";
			String nameOfPlaylist = "";
			String nameOfSong = "";

			ph = new PlaylistHandler();

			// As long as there is more to read, attempt to parse the
			// information.
			while ((line = br.readLine()) != null) {

				if (line.startsWith("directory=")) {
					line = line.replace("directory=", "");
					DIRECTORY = line;

					if (System.getProperty("os.name").startsWith("Win") && !DIRECTORY.endsWith("\\"))
						DIRECTORY = DIRECTORY.concat("\\");
					
					else if(!DIRECTORY.endsWith("/"))
						DIRECTORY = DIRECTORY.concat("/");
				}

				// If a playlist is found; get the name of the list and add it
				// to the GUI.
				if (line.startsWith("playlist=")) {
					nameOfPlaylist = line.substring(9);
					ph.createAndAddPlaylist(nameOfPlaylist);
				}

				// If a song is found, add it to the playlist. This works
				// because of the way we write the song names to this file.
				else if (line.startsWith("song=")) {
					nameOfSong = line.substring(5);
					ph.addToPlaylist(nameOfPlaylist, nameOfSong);
				}

				else if (line.equals("Done."))
					break;
			}

			System.out.println("Preferences loaded.");
			br.close();
		} catch (IOException exc) {
			System.out.println("No file found.");
		}

	}

	/**
	 * Method to change the directory, this should only be called from the Menu
	 * bar.
	 */
	private void changeDirectory() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		String message = "<html><div align='center'>Navigate to the correct directory. <br>After you have clicked \"Open\", please restart the program.</div></html>";
		JLabel msgLabel = new JLabel(message, JLabel.CENTER);
		JOptionPane.showMessageDialog(null, msgLabel, "Onyx Music Player",
				JOptionPane.INFORMATION_MESSAGE);

		int choice = fc.showOpenDialog(null);

		if (choice == JFileChooser.APPROVE_OPTION) {
			File dir = fc.getSelectedFile();

			this.DIRECTORY = dir.toString();

			// If you're using Windows and the directory "seems" incomplete,
			// finish it.
			if (System.getProperty("os.name").startsWith("Win") && !DIRECTORY.endsWith("\\"))
				DIRECTORY = DIRECTORY.concat("\\");
			else if (!DIRECTORY.endsWith("/"))
				DIRECTORY = DIRECTORY.concat("/");

		}
	}

	/** Method to help the shuffle-function. */
	private int getRandomTrackIndex() {
		Random rand = new Random();
		return rand.nextInt(tracklist.getModel().getSize());
	}

	/** Play the next track from the list of queued songs. */
	public void playFromQueueList() {
		tracklist.setSelectedIndex(queueList.get(0));
		playTrack();
		queueList.remove(0);
	}

	/** Play the next check, while checking if the list is shuffled or not. */
	public void playNextTrack() {

		if (queueList.size() != 0) {
			playFromQueueList();
		} else {
			if (shuffle == true) {
				setPreviousTrackIndex();
				if (repeatOne)
					tracklist.setSelectedIndex(tracklist.getSelectedIndex());
				else
					tracklist.setSelectedIndex(getRandomTrackIndex());
				playTrack();

			} else if (shuffle == false) {
				setPreviousTrackIndex();
				// TODO TESTA DETTA
				if (repeatOne)
					tracklist.setSelectedIndex(tracklist.getSelectedIndex());
				else if (repeat == true
						&& tracklist.getSelectedIndex() == tracklist.getModel()
						.getSize() - 1)
					tracklist.setSelectedIndex(0);
				else
					tracklist
					.setSelectedIndex(tracklist.getSelectedIndex() + 1);

				playTrack();
			}
		}
	}

	/** Method to keep track of and play the previously played tracks */
	public void playPreviousTrack() {
		if (previousTrackIndex.size() > 1) {
			tracklist.setSelectedIndex(previousTrackIndex
					.get(previousTrackIndex.size() - 2));
			previousTrackIndex.remove(previousTrackIndex.size() - 1);
			repaint();
			playTrack();
		}
	}

	/** Adds the previously played songs to the appropriate list. */
	private void setPreviousTrackIndex() {
		// Unless you are playing and pausing the same track over and over
		// again, this sets the previous track index

		previousTrackIndex.add(tracklist.getSelectedIndex());
		TRACK_INDEX = previousTrackIndex.get(previousTrackIndex.size() - 1);

	}

	/**
	 * Method which basically check if you're playing or not, defining whether
	 * to play or pause.
	 */
	public void pauseOrPlay() {

		if (!newTrack && !playing) {
			player.resumePlaying();
			playing = true;
			playpauseButton.setIcon(pauseIcon);
		} else if (playing && !newTrack) {
			playpauseButton.setIcon(playIcon);
			player.pause();
			playing = false;

		} else
			playpauseButton.setIcon(pauseIcon);

	}

	/** Method invoked when music is to be played */
	private void playTrack() {
		stopTimer();
		
		// If the selected track is NOT the same as the current song, it's a new
		// track.
		if (!trackName.equals(tracklist.getSelectedValue() + ".mp3"))
			newTrack = true;

		trackName = tracklist.getSelectedValue() + ".mp3";
		
		updateTimeOfSong();

		// If there's a new track OR we've set the track to repeat, play the
		// desired file.
		if (newTrack || (!newTrack && repeatOne)) {
			try {
				player.play(DIRECTORY + trackName);
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}

			newTrack = false;
			playing = true;
			playpauseButton.setIcon(pauseIcon);
		}

		setLabelText();

		startTimer();

	}

	/** Similar to above, this plays the same track. */
	private void playSameTrack() {
		stopTimer();
		trackName = tracklist.getSelectedValue() + ".mp3";
		updateTimeOfSong();
		try {
			player.play(DIRECTORY + trackName);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
		newTrack = false;
		playing = true;
		setLabelText();
		startTimer();
	}

	/** Method which checks if you should play the same track or the next one. */
	private void checkHowToPlay() {
		
		updateTimeOfSong();
		
		if (playing && TRACK_INDEX != tracklist.getSelectedIndex()) {
			pauseOrPlay();
			playTrack();
		} else {
			playSameTrack();
		}
		playpauseButton.setIcon(pauseIcon);
	}

	/** Set the name of the song in the label under the playlists. */
	private void setLabelText() {

		String trackName = (String) tracklist.getSelectedValue();
		
		String labelText = trackName.replace(" (", "<br>(");
		trackInfoLabel.setText("<html><div style=\"text-align: center;\">" + labelText + "<html>");
		

		if (trackName.length() < 24)
			trackInfoLabel.setFont(new Font("Verdana", Font.BOLD, 14));
		else if (trackName.length() < 46)
			trackInfoLabel.setFont(new Font("Verdana", Font.BOLD, 12));
		else
			trackInfoLabel.setFont(new Font("Verdana", Font.BOLD, 10));
		
		trackInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		

		setTitle("Onyx Music Player - " + (String) tracklist.getSelectedValue());
		

		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

	}

	/** Methods that "moves" the JSlider to represent a position in the song */
	private void moveSlider() {
		int totalFrames = player.getLengthInFrames();
		int currentFrame = player.getPlayingPosition();
		trackTimeSlider.setUI(new TimeSlider(trackTimeSlider));
		trackTimeSlider.setMaximum(totalFrames + 1);
		trackTimeSlider.setValue(currentFrame);
		
	}
	
	public void updateTimeOfSong() {
		try {
			if(!trackName.equals(""))
				System.out.println("TIME OF TRACK: " + player.getTrackLength(DIRECTORY + trackName));
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //TODO
	}

	/** Create a playlist */
	public void createPlaylist() {

		String playlistName = "";
		playlistName = JOptionPane.showInputDialog(null, "Name your list");

		if (playlistName != null) {
			while (playlistName.equals("")) {
				playlistName = JOptionPane.showInputDialog(null,
						"Enter a valid name.");

				if (playlistName == null)
					break;
			}
		}

		if (playlistName != null)
			ph.createAndAddPlaylist(playlistName);

		playlistStrings = ph.getPlaylists();
		playlist.setListData(playlistStrings);

		updatePopupMenu();

	}
	
	public void simulateShuffle() {
		if (shuffle == false) {
			shuffle = true;
			shuffleButton.setIcon(shuffleTrueIcon);
		} else if (shuffle == true) {
			shuffle = false;
			shuffleButton.setIcon(shuffleFalseIcon);
		}
	}

	/** Usual ActionPerformed method, this is where the stuff happens. */
	@Override
	public void actionPerformed(ActionEvent a) {

		if (a.getSource() == playlistButton) {
			createPlaylist();
		}
		
		// TODO
		if (a.getSource() == menuPlay) {
			setPreviousTrackIndex();
			checkHowToPlay();
		}

		if (a.getSource() == menuQueue) {
			queueList.add(tracklist.getSelectedIndex());
			trackName = trackName.replaceAll(".mp3", "");
			tracklist.setSelectedValue(trackName, false);
		}

		if (a.getSource() == playpauseButton) {
			if (TRACK_INDEX != tracklist.getSelectedIndex()) {
				setPreviousTrackIndex();
			}
			pauseOrPlay();
			if (!repeatOne) // Basically meaning, if RepeatOne is true, there's
				// no need to start a new track.
				playTrack();
		}

		if (a.getSource() == nextButton) {
			if (playing && TRACK_INDEX != tracklist.getSelectedIndex()) {
				pauseOrPlay();
				playNextTrack();
			} else {
				playNextTrack();
			}
		}

		if (a.getSource() == previousButton) {
			// If you have no songs that you previously have played or if you
			// are back at the first song you played, the button won't do
			// anything
			if (previousTrackIndex.size() != 0) {
				if (playing && TRACK_INDEX != tracklist.getSelectedIndex()) {
					pauseOrPlay();
					playPreviousTrack();
				} else {
					playPreviousTrack();
				}
			}
		}

		if (a.getSource() == shuffleButton) {
			simulateShuffle();
		}
		// Repeats the whole tracklist when it's done
		if (a.getSource() == repeatButton) {

			if (!repeat && !repeatOne) {
				repeat = true;
				repeatOne = false;
				repeatButton.setIcon(repeatTrueIcon);
			} else if (repeat && !repeatOne) {
				repeatOne = true;
				repeat = false;
				repeatButton.setIcon(repeatOneIcon);
			} else if (!repeat && repeatOne) {
				repeat = false;
				repeatOne = false;
				repeatButton.setIcon(repeatFalseIcon);
			}

		}

		if (a.getSource() == helpMenuItem) {
			createHelpWindow();
			jdialog.dispose();
		}

		if (a.getSource() == exitMenuItem) {
			try {
				savePreferences();
			} catch (FileNotFoundException e) {
			}

			System.exit(0);
		}

		if (a.getSource() == cdMenuItem) {
			changeDirectory();
		}

		if (a.getSource() == scrollbarMenuItem) {
			if (trackGUI.getVerticalScrollBar().getSize().width == 0) {
				trackGUI.getVerticalScrollBar().setPreferredSize(
						new Dimension(18, this.getHeight()));
				trackGUI.getVerticalScrollBar().setValue(
						trackGUI.getVerticalScrollBar().getValue() + 1);
			}

			else {
				trackGUI.getVerticalScrollBar().setPreferredSize(
						new Dimension(0, 0));
				trackGUI.getVerticalScrollBar().setValue(
						trackGUI.getVerticalScrollBar().getValue() - 1);
			}
		}

		if (a.getSource() == backMenuItem) {
			titleLabel.setText("Library");
			th.listTracks();
			tracklist.setListData(th.getTracks());
		}

		if (a.getSource() == playMenuItem) {
			setPreviousTrackIndex();
			checkHowToPlay();
		}

		/*
		 * This checks if we have clicked on any of the "Add to:" playlists.
		 * Because the PopupMenu is based the playlists (and is continously
		 * updated) we check if the returned command is equal to he name of the
		 * playlist. If yes, add the selected song to said playlist.
		 */
		for (String listChoice : ph.getPlaylists()) {
			if (a.getActionCommand().equals(listChoice)) {
				ph.addToPlaylist(listChoice,
						(String) tracklist.getSelectedValue());
				trackName = trackName.replaceAll(".mp3", "");
				tracklist.setSelectedValue(trackName, false);
			}
		}

	}

	/** Method which creates the help window */
	public void createHelpWindow() {

		String s = "This is a relatively simple music player, designed by Marcus and Mark.\n\n"
				+ "If you get a message that directory is faulty, make sure that:\n"
				+ "There are .mp3 files in the directory\n"
				+ "The directory looks something like C:\\User\\*Username*\\Music\\ \n \n"
				+ "If the directory still seems to be faulty, there are several videos online which can help you.\n\n"
				+ "Friendly reminder that this a simple program, don't expect too much from it\n"
				+ "Features such as drag-and-drop and moving the \"bar\" around may be implemented later. \n\n"
				+ "You can delete playlists using the DELETE button.\n\n"
				+ "If the program crashes, if you encounter unexpected errors or if there's something wrong\n"
				+ "you can email us at mheine@kth.se or mhobro@kth.se ";

		jop = new JOptionPane(s, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		jdialog = new JDialog();
		jdialog.setTitle("Help");
		jdialog.setModal(true);
		jdialog.setContentPane(jop);
		jdialog.pack();
		jdialog.setLocationRelativeTo(this);
		jdialog.setVisible(true);
	}

	/**
	 * Method for the timer, which checks if a song is done and also moves the
	 * JSlider.
	 */
	public void startTimer() {

		// Create a new timer and set it to check if the song has ended and to
		// move the track JSlider
		tim = new Timer();
		tim.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				moveSlider();

				if (tracklist.getSelectedValue() == null) {
					playpauseButton.setEnabled(false);
					nextButton.setEnabled(false);
					previousButton.setEnabled(false);
				} else {
					playpauseButton.setEnabled(true);
					nextButton.setEnabled(true);
					previousButton.setEnabled(true);
				}

				if (player.hasSongEnded()) {
					if (repeatOne) {
						tracklist.setSelectedIndex(tracklist.getSelectedIndex());
						playTrack();
					} else if (repeat == true
							&& tracklist.getSelectedIndex() == tracklist
							.getModel().getSize() - 1) {
						tracklist.setSelectedIndex(0);
						playTrack();
					} else {
						setPreviousTrackIndex();
						playNextTrack();
					}
				}

			}
		}, 200, 200);

	}

	/** Method to stop said timer. */
	public void stopTimer() {
		if (tim != null)
			tim.cancel();
	}

	/**
	 * Usual mouseClicked method, handles clicks in the PlaylistGUI and TrackGUI
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() >= 2) {
			if (me.getSource() == tracklist) { 
				// Double-clicked somewhere within the tracklist.
				setPreviousTrackIndex();
				checkHowToPlay();
				
			} else if (me.getSource() == playlist) { 
				// Double-clicked somewhere within the list of  playlists.
				if (playlist.getSelectedValue() != null) {

					titleLabel.setText((String) playlist.getSelectedValue());

					// Update the tracklist so that it shows the list of tracks
					// in the specified playlist.
					tracklist.setListData(ph
							.getSpecificPlaylist((String) playlist
									.getSelectedValue()));
				}
			} else { // Double-clicked in the label; scroll to this song.
				JScrollBar bar = trackGUI.getVerticalScrollBar();
				String[] tracks = th.getTracks();
				int trackPos = Arrays.asList(tracks).indexOf(
						tracklist.getSelectedValue());
				bar.setValue((trackPos * 20));
			}
		}
	}

	/** Method which handles/parses right-clicks. */
	@Override
	public void mousePressed(MouseEvent me) {
		// This code snippet check that if the right mouse is clicked, the item
		// in the JList where you clicked is selected
		if (SwingUtilities.isRightMouseButton(me)) {
			tracklist
			.setSelectedIndex(tracklist.locationToIndex(me.getPoint()));
		}
	}

	/**
	 * Method which handles the closing of the frame; this is where the
	 * preferences are saved.
	 */
	public void windowClosing(WindowEvent we) {

		try {
			savePreferences();
		} catch (FileNotFoundException e) {
		}

		System.exit(0);
	}

	/**
	 * Method which handles key presses.
	 * This concerns:
	 * DELETE - Delete playlists
	 * SPACEBAR - Play or pause
	 * CTRL + ARROWKEY - Next or previous
	 */
	@Override
	public void keyReleased(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_DELETE) {
			int reply = JOptionPane.showConfirmDialog(
					null,
					"Are you sure you want to delete "
							+ (String) playlist.getSelectedValue() + "?");
			if (reply == 0) {
				if (ph.removeListFromPlaylists((String) playlist
						.getSelectedValue())) {
					playlist.setListData(ph.getPlaylists());
					updatePopupMenu();
				}
			}

		}
		
		if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
			pauseOrPlay();
			System.out.println("Space was released");
		}
	}

	/**
	 * Method which handles the resizing of the frame; which updates the bar at
	 * the bottom.
	 */
	@Override
	public void componentResized(ComponentEvent cr) {
		updateBarLayout();
	}


	/** Methods required by the Interfaces */
	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	public void mouseReleased(MouseEvent me) {
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}

	public void keyPressed(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if((arg0.getSource() == volumeSlider) && volumeSlider.getValue() != currentVolume) {
			System.out.println("VOLUME: "  + volumeSlider.getValue());
			volume.setMasterOutputVolume((float)volumeSlider.getValue() / 100);
			currentVolume = volumeSlider.getValue();
		}
		
	}

}