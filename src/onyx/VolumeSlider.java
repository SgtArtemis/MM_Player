package onyx;

import java.awt.*;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class VolumeSlider extends BasicSliderUI {
	
	private Rectangle rThumb = thumbRect;
	private Rectangle rTrack = trackRect;


    public VolumeSlider(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        rThumb = thumbRect;
        rTrack = trackRect;
       
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        
        //"Border"
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRoundRect(rTrack.x, rTrack.y, rTrack.width, 14, 14, 14);
        
        //Background, or fill "behind" the thumbnail
        g2d.setColor(new Color(70, 70, 70));
        g2d.fillRoundRect(rTrack.x+2, rTrack.y+2, rTrack.width-4, 10, 10, 10);
        
        //Foreground, or filled "before" the thumb
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRoundRect(rTrack.x, rTrack.y, rThumb.x, 14, 14, 14);
        
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);       
    }
    
    
//    @Override
//    protected Dimension getThumbSize() {
//    	 rThumb = thumbRect;
//         rTrack = trackRect;
//    	//return new Dimension((int)rThumb.getWidth(), (int)rThumb.getHeight());
//    	return new Dimension(10, 10);
//    }
}