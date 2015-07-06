package onyx;

import java.awt.*;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class TimeSlider extends BasicSliderUI {


    public TimeSlider(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle t = trackRect;
        Rectangle tn = thumbRect;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //Border
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRoundRect(t.x, t.y, t.width+4, 14, 14, 14);
        
        //Bakom thumb
        g2d.setColor(new Color(55, 55, 50));
        g2d.fillRoundRect(t.x+2, t.y+2, t.width, 10, 10, 10);
        
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRoundRect(t.x, t.y, tn.x, 14, 14, 14);
        
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle t = thumbRect;
        g2d.setColor(Color.black);
        g2d.fillOval(t.x, t.y, 14, 14);
    }
}