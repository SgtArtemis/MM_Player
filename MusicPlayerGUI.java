package mm;

/**
 * Projekt - INDA12 - Vårterminen 2013
 * 
 * @author Marcus Heine & Mark Hobro
 * 
 */

/**
 * TODO - A lot.
 */

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.*;

public class MusicPlayerGUI extends JFrame{

  private static final long serialVersionUID = 1L;

	private JMenuBar menuBar;

	private TrackGUI trackGUI;

	private PlaylistGUI playlistGUI;

	private TrackInfoBar trackInfoBar;

	private JLabel titleLabel;
	private JLabel trackInfoLabel;


	public MusicPlayerGUI() {

		setVisible(true);

		setLayout(new GridBagLayout());

		this.setSize(1100, 600);

		this.setResizable(false);

		setLocationRelativeTo(null);

		setTitle("MM Music Player");

		menuLayout();

		mainLayout();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}


	private void mainLayout() {

		int width = this.getWidth();
		int height = this.getHeight();

		System.out.println(width + ", " + height);

		//setJMenuBar(menuBar);


		trackGUI = new TrackGUI();

		trackInfoBar = new TrackInfoBar();

		playlistGUI = new PlaylistGUI();

		JScrollPane listGUI = new JScrollPane();

		//"C:\\Users\\Marcus\\Pictures\\MM\\testLabel.png"

		titleLabel = new JLabel();
		//titleLabel.setBackground(new Color(0, 60, 100));

		trackInfoLabel = new JLabel();

		//		playlistGUI.setLocation(0, 30);
		//		playlistGUI.setSize(new Dimension(260, 450));
		//		
		//		trackGUI.setLocation(265, 50);
		//		trackGUI.setSize(new Dimension(900, 300));
		//
		//		trackInfoBar.setLocation(0, 500);
		//trackInfoBar.setPreferredSize(new Dimension(1095, 200));

		//titleLabel.setLocation(50, 600);

		GridBagConstraints c = new GridBagConstraints();

		//playlistGUI.setPreferredSize(new Dimension(250, 400));
		playlistGUI.getViewport().setBackground(Color.white);
		listGUI.getViewport().setBackground(Color.white);

		//trackGUI.setPreferredSize(new Dimension(800, 400));
		trackGUI.getViewport().setBackground(Color.black);

		//titleLabel.setPreferredSize(new Dimension(830, 100));
		titleLabel.setBackground(new Color(25, 65, 123));
		titleLabel.setOpaque(true);

		//trackInfoLabel.setPreferredSize(new Dimension(250, 40));
		trackInfoLabel.setBackground(new Color(125, 165, 13));
		trackInfoLabel.setOpaque(true);

		//c.fill = GridBagConstraints.BOTH;
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 400;
		c.ipadx = 250;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 0;
		System.out.println(c.toString());
		add(listGUI, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 80;
		//c.weightx = 0.5;
		//c.weighty = 0.1;
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
		//c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 2;
		c.gridx = 1;
		c.gridy = 1;
		add(trackGUI, c);

		//c.fill = GridBagConstraints.VERTICAL;
		c.ipady = 20;
		c.ipadx = 20;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 2;
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
		c.gridx = 0;
		c.gridy = 4;
		add(trackInfoBar, c);

		//		add(playlistGUI);
		//		add(titleLabel);
		//		add(trackGUI);
		//		add(trackInfoLabel);
		//		add(trackInfoBar);
	}

	private void menuLayout(){

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu();
		JMenu prefsMenu = new JMenu();
		JMenu helpMenu = new JMenu();

		JMenuItem playMenuItem;
		JMenuItem backMenuItem;
		JMenuItem exitMenuItem;
		JMenuItem prefsMenuItem;
		JMenuItem helpMenuItem;

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

		menuBar.add(fileMenu);
		menuBar.add(prefsMenu);
		menuBar.add(helpMenu);

		menuBar.setVisible(true);

	}


	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MusicPlayerGUI main = new MusicPlayerGUI();
			}
		});
	}

}

class TrackGUI extends JScrollPane {

	private JList<String> lista = new JList<String>();
	private String [] filenames;

	private static final long serialVersionUID = 1L;

	public TrackGUI(){

		//TODO - Fixa
		File directory = new File("C:\\Users\\Marcus\\Music\\Musik\\");
		
		//create a FilenameFilter and override its accept-method
		FilenameFilter filefilter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				//Ifall namnet slutar på .mp3, returnera true
				return name.endsWith(".mp3");

			}
		};

		filenames = directory.list(filefilter);

		lista.setListData(filenames);

		this.add(lista);


	}
}

class PlaylistGUI extends JScrollPane {

	private static final long serialVersionUID = 1L;

	//private JList<String> playlist = new JList<String>();
}

class TrackInfoBar extends JPanel {

	private static final long serialVersionUID = 1L;

	public TrackInfoBar() {

		setVisible(true);
		setSize(500, 200);
		setBackground(new Color(0, 90, 160));
	}

}

class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private JMenu fileMenu = new JMenu();
	private JMenu prefsMenu = new JMenu();
	private JMenu helpMenu = new JMenu();

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
