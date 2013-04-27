package mm;

import java.awt.event.*;

import javax.swing.*;

import javazoom.jl.decoder.JavaLayerException;

/**
 * Class to handle the bar at the bottom of the frame.
 * This panel should (is going to?) contain all the buttons such as Pause, Play, Next, Volume etc
 *
 * Since this is quite an important part of the program, we felt it needed it's own class.
 *
 * @author Marcus Heine and Mark Hobro
 *
 * TODO - Do this eh?
 */
class TrackInfoBar extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
	
	private JButton playpause = new JButton("Play/Pause");
	
	private MusicFileByFrames player;

	public TrackInfoBar() {
			this.add(playpause);
			playpause.addActionListener(this);
			
			try {
				player = new MusicFileByFrames("C:\\Users\\Marcus\\Music\\Musik\\Avicii - Summerburst ID.mp3");
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if(a.getSource() == playpause)
		{
			System.out.println("Hej");
			try {
				player.play();
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
