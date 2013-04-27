package mm;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Class that handles the Menu at the top of the frame.
 * 
 * This is a separate class because at some point we probably want to add ActionListeners and whatnot.
 * TODO - Add that n' stuff.
 *
 */
class WindowMenuBar extends JMenuBar {

  private static final long serialVersionUID = 1L;

	private JMenu fileMenu = new JMenu("File");
	private JMenu prefsMenu = new JMenu("Preferences");
	private JMenu helpMenu = new JMenu("Help");

	public JMenuItem playMenuItem;
	public JMenuItem backMenuItem;
	public JMenuItem exitMenuItem;
	public JMenuItem prefsMenuItem;
	public JMenuItem helpMenuItem;

	public WindowMenuBar() {

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
