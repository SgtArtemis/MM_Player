package onyx;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;
	
	/* Spotify Colour Scheme */
	private final Color LIGHT = new Color(47, 47, 47);
	private final Color DARK = new Color(57, 57, 57);
	
//	private final Color LIGHT = new Color(25, 80, 25);
//	private final Color DARK = new Color(30, 70, 30);
	
	private final Color BLUE = new Color(175, 220, 255);

	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

		// Unless an item is selected, alternate the background between light
		// and dark grey.
		if (isSelected)
			c.setBackground(BLUE);

		else if (index % 2 == 0)
			c.setBackground(LIGHT);

		else
			c.setBackground(DARK);

		return c;
	}
}
