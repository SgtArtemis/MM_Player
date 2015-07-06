package onyx;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopupHandler extends MouseAdapter {
	public int mouseX = 0;
	public int mouseY = 0;
	private JPopupMenu menu;

	public PopupHandler(JPopupMenu m) {
		menu = m;
	}

	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	private void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}