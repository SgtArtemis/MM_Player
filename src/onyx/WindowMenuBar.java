package onyx;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Class that handles the Menu at the top of the frame.
 * 
 * This is a separate class because at some point we probably want to add ActionListeners and whatnot.
 * TODO - Add that n' stuff.
 *
 */
class WindowMenuBar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JMenu fileMenu = new JMenu("File");
	private JMenu prefsMenu = new JMenu("Preferences");
	private JMenu helpMenu = new JMenu("Help");
	
	private static final Color WINDOW_GREY = new Color(150, 150, 150);
	private static final Color TEXT_GREY = new Color(50, 50, 50);

	public JMenuItem playMenuItem;
	public JMenuItem backMenuItem;
	public JMenuItem exitMenuItem;
	public JMenuItem prefsMenuItem;
	public JMenuItem helpMenuItem;
	
	public String DIRECTORY;
	
	private JOptionPane jop;
	private JDialog jdialog;
	
	//public TrackHandler th;

	public WindowMenuBar(String dir) {
		
		DIRECTORY = dir;
		initComponents();
		setLayout();

	}
	
	private void initComponents() {
		
		playMenuItem 		= new JMenuItem("Play");
		backMenuItem		= new JMenuItem("Main list");
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
		
		backMenuItem.addActionListener(this);
		
		helpMenuItem.addActionListener(this);
		jop = new JOptionPane("Fak u", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		jdialog = new JDialog();
		
		jdialog.setTitle("Help");
		jdialog.setModal(true);

		//TODO Lägg till den till huvudskärmen, hittade inget bra smidigt sätt så pallade inte just nu
		jdialog.setContentPane(jop);
		jdialog.pack();
		//jdialog.setLocationRelativeTo(null); 
		jdialog.setVisible(true);
		
	}

	private void setLayout() {
		
		Border border = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLUE);
		
		setBackground(WINDOW_GREY);
		setBorder(border);
		
		fileMenu.setForeground(TEXT_GREY);
		prefsMenu.setForeground(TEXT_GREY);
		helpMenu.setForeground(TEXT_GREY);
		
	}

	@Override
	//TODO Detta är fucked up :D
		public void actionPerformed(ActionEvent a) {
			if(a.getSource() == helpMenuItem){
				//jdialog.dispose();
			}
			if(a.getSource() == exitMenuItem){
				System.exit(0);
			}
			if(a.getSource() == backMenuItem){
				//TODO - Fixa sä att den här går tillbaka till huvudlistan
			}
		}
	
}