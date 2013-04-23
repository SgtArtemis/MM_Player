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

	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu fileMenu = new JMenu();
	private JMenu prefsMenu = new JMenu();
	private JMenu helpMenu = new JMenu();

	private TrackGUI trackGUI;

	private PlaylistGUI playlistGUI;

	private TrackInfoBar trackInfoBar;

	private JLabel titleLabel;
	private JLabel trackInfoLabel;


	public MusicPlayerGUI() {

		setVisible(true);

		setLayout(new GridBagLayout());

		setSize(1100, 600);

		//setResizable(false);

		setLocationRelativeTo(null);

		setTitle("MM Music Player");

		menuLayout();

		mainLayout();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}


	private void mainLayout() {

		trackGUI = new TrackGUI();

		trackInfoBar = new TrackInfoBar();

		playlistGUI = new PlaylistGUI();

		titleLabel = new JLabel();
		trackInfoLabel = new JLabel();
		

		playlistGUI.getViewport().setBackground(Color.white);
		playlistGUI.setBorder(null);

		trackGUI.getViewport().setBackground(Color.black);
		trackGUI.setBorder(null);

		titleLabel.setBackground(new Color(25, 65, 123));
		titleLabel.setOpaque(true);

		trackInfoLabel.setBackground(new Color(125, 165, 13));
		trackInfoLabel.setOpaque(true);
		
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

		c.ipady = 20;
		c.ipadx = 20;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 2; //TODO 
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

	private void menuLayout(){

		setJMenuBar(menuBar);

		JMenuItem playMenuItem 		= new JMenuItem("Play");
		JMenuItem backMenuItem		= new JMenuItem("Back");
		JMenuItem exitMenuItem 		= new JMenuItem("Exit");
		JMenuItem prefsMenuItem 	= new JMenuItem("Preferences");
		JMenuItem helpMenuItem 		= new JMenuItem("Help");
		
		fileMenu.setText("File");
		helpMenu.setText("Help");
		prefsMenu.setText("Prefs");

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
				new MusicPlayerGUI();
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
