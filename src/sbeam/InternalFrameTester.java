package sbeam;

import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class InternalFrameTester {

	public InternalFrameTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JDesktopPane pane = new JDesktopPane();
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(600, 400));
		pane.setVisible(true);
		frame.add(pane);
		JInternalFrame intFrame = new JInternalFrame();
		intFrame.setSize(new Dimension(200, 100));
		intFrame.setVisible(true);
		intFrame.setResizable(true);
		pane.add(intFrame);
		frame.pack();
		frame.setVisible(true);

	}

}
