package sbeam;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class Smooth_List_Dialog extends List_Dialog {

	protected JFormattedTextField num_edit;

	protected int number_to_average;

	public Smooth_List_Dialog(MainFrame parent) {
		// TODO Auto-generated constructor stub
		super(parent, null, 1);
		list_box = new JList<String>();
		num_edit = new JFormattedTextField();
	}

	public int GetNumToAverage() {
		return number_to_average;
	}

	protected void CmOk() {

	}

	protected void SetupWindow() {
		this.add(new JScrollPane(list_box));
	}

}
