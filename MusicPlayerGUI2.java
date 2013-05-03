package mm;

/**
 * Projekt - INDA12 - Vårterminen 2013
 *
 * @author Marcus Heine & Mark Hobro
 *
 * TODO - Note that this version uses Java 7.
 * 
 * Mark, du kan ju ändra så att JLists inte blir parametriserad; jag får warnings om jag inte har som det är nu.
 */

//import PopupListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//import java.io.*;

//import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;

import javazoom.jl.decoder.JavaLayerException;

//import javazoom.jl.decoder.JavaLayerException;

public class MusicPlayerGUI2 extends JFrame implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;

	/** Define the colours */
	public static final Color TRACKLIST_GREY = new Color(55, 55, 55);
	public static final Color LABEL_GREY = new Color(63, 63, 63);
	public static final Color PLAYLIST_GREY = new Color(70, 70, 70);
	public static final Color WINDOW_GREY = new Color(150, 150, 150);

	public static final Font MAIN_FONT = new Font("Tahoma", Font.PLAIN, 13);

	private WindowMenuBar menuBar = new WindowMenuBar();

	public PopupListener popupListener;

	//Just nu har jag initierat både String[] och JList här, kanske lite dumt men det funkar iaf.
	public JList<String> tracklist;
	public JList<String> playlist;

	public String [] playlistStrings;
	public String [] tracklistStrings;

	private TrackGUI trackGUI;

	private PlaylistGUI playlistGUI;
	
	private MusicPlayer player;

	private JPanel trackInfoBar;

	private JLabel titleLabel;
	private JLabel trackInfoLabel;
	private JLabel placeholder;

	public JMenuItem menuPlay = new JMenuItem("Play");
	public JMenuItem menuQueue = new JMenuItem("Queue");
	public JMenuItem menuList = new JMenuItem("Add to playlist");
	public JMenuItem menuStar = new JMenuItem("Star");

	private JButton playpause = new JButton("Play/Pause");

	private String directory;
	
	private boolean playing;
	private boolean newTrack;
	
	
	
	private String trackName = "";

	//private int fulhack;

	public MusicPlayerGUI2() {

		setVisible(true);

		setLayout(new GridBagLayout());

		setSize(1100, 660);
		setMinimumSize(new Dimension(800, 580));

		//setResizable(false);

		setLocationRelativeTo(null);

		setTitle("MM Music Player");

		initComponents();

		mainLayout();

		setColours();

		setBorders();

		createPopupMenu();

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//TODO - This is where the directory for playing songs is set
		//directory = "C:\\Users\\Mark\\Songs\\Basto! - Stormchaser (Original Mix).mp3";
		directory = "C:\\Users\\Marcus\\Music\\Musik\\";

		player = new MusicPlayer();
		this.playing = false;
		this.newTrack = true;
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

		//TODO - Ändra för att se scrollbaren
		//Om scrollbaren ska vara synlig eller inte; jag gillar när den inte är det ^^
		trackGUI.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		trackGUI.addMouseListener(this);

		tracklist.addMouseListener(this);

		titleLabel = new JLabel();
		trackInfoLabel = new JLabel();
		placeholder = new JLabel();

		trackInfoBar = new JPanel();
		trackInfoBar.add(playpause);
		playpause.addActionListener(this);

		setJMenuBar(menuBar);

	}




	/** Method that sets the layout, defining where all the components should be placed */
	private void mainLayout() {

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.ipady = 400;
		c.ipadx = 200;
		c.gridheight = 3;
		//c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.0;
		c.weighty = 10.0;
		c.gridx = 0;
		c.gridy = 0;
		add(playlistGUI, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 60;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		///c.anchor = GridBagConstraints.PAGE_START;
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

		//c.fill = GridBagConstraints.NONE;
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

		//c.fill = GridBagConstraints.BOTH;
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

		playlist.setBackground(PLAYLIST_GREY);
		playlist.setForeground(Color.WHITE);
		tracklist.setFixedCellHeight(20);

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

		//Create a border for each component. This is probably not the best solution, but it is a solution that works.
		//TODO - Fix this?
		Border listBorder = BorderFactory.createMatteBorder(3, 3, 3, 0, Color.BLACK);
		Border tracklistBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK);
		Border infoLabelBorder = BorderFactory.createMatteBorder(0, 3, 3, 0, Color.BLACK);
		Border titleLabelBorder = BorderFactory.createMatteBorder(3, 3, 0, 3, Color.BLACK);
		Border barBorder = BorderFactory.createMatteBorder(0, 3, 3, 3, Color.BLACK);
		Border placeBorder = BorderFactory.createMatteBorder(3, 3, 0, 3, Color.BLACK);

		Border testBorder = BorderFactory.createMatteBorder(5, 5, 5, 5, TRACKLIST_GREY);

		playlistGUI.setBorder(listBorder);
		trackInfoLabel.setBorder(infoLabelBorder);

		trackGUI.setBorder(tracklistBorder);
		titleLabel.setBorder(titleLabelBorder);
		trackInfoBar.setBorder(barBorder);
		placeholder.setBorder(placeBorder);

		tracklist.setBorder(testBorder);
	}


	public void createPopupMenu() {

		//Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		int x = (int) b.getX();
		int y = (int) b.getY();

		System.out.println("X: " + x + "    Y: " + y);

		menuPlay.addActionListener(this);
		menuQueue.addActionListener(this);
		menuList.addActionListener(this);
		menuStar.addActionListener(this);

		popup.add(menuPlay);
		popup.add(menuQueue);
		popup.add(menuStar);
		popup.add(menuList);

		//Add listener to the text area so the popup menu can come up.
		popupListener = new PopupListener(popup);
		tracklist.addMouseListener(popupListener);
		titleLabel.addMouseListener(popupListener);

		//This code snippet check that if the right mouse is clicked, the item in the JList where you clicked is selected
		tracklist.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					tracklist.setSelectedIndex(tracklist.locationToIndex(e.getPoint()));
				}
			}
		});
	}


	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MusicPlayerGUI2();
			}
		});
	}

	//TODO - ActionPerformed for the PopupMenu
	@Override
	public void actionPerformed(ActionEvent a) {
		

		if (a.getSource() == menuPlay) {
			System.out.println("Play the track" + tracklist.getSelectedValue());
		}

		if (a.getSource() == menuQueue) {
			System.out.println("Queued the track " + tracklist.getSelectedValue());
		}

		//TODO - Här hamnar du när man trycker på play/pause-knappen
		if (a.getSource() == playpause){
			
			
			System.out.println(trackName);
			if(!trackName.equals(tracklist.getSelectedValue() + ".mp3"))
				newTrack = true;
			
			trackName = tracklist.getSelectedValue() + ".mp3";
			
			if(!newTrack && !playing){
				System.out.println("3");
				player.resumePlaying();
				playing = true;
			}
			else if(playing && !newTrack){
				System.out.println("2");
				player.pause();
				playing = false;
			}


			//TODO Hade en snyggare lösning innan men tog bort den i mina tester, fixar sen!

			if(newTrack)
			{
				System.out.println("First instance of a the track " + trackName);
				try {
					player.play(directory + trackName);
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newTrack = false;
				playing = true;
			}



			//System.out.println("Attempting to play track " + tracklist.getSelectedValue());
		}

	}

	//TODO - MouseEvent; this handles doubleclicks.
	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() >= 2)
		{
			System.out.println("Du har dubbelklickat på en låt: " + tracklist.getSelectedValue());
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

}


