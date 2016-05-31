package sbeam;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import javax.swing.JDialog;
import javax.swing.JRadioButton;

public class Energy_Units_Dialog extends JDialog {

	protected int selected_units;

	protected JRadioButton kcalmol, kJmol, wavenumber, eV;
	protected boolean ID;

	public Energy_Units_Dialog(MainFrame parent) {
		// TODO Auto-generated constructor stub
		kcalmol = new JRadioButton();
		kJmol = new JRadioButton();
		wavenumber = new JRadioButton();
		eV = new JRadioButton();

	}

	public void SetSelectedUnits(int new_units) {
		selected_units = new_units;
	}

	public int GetSelectedUnits() {
		return selected_units;
	}

	protected void CmOk() {
		this.dispose();
	}

	/*
	 * protected void CmKcalMol(); protected void CmKJMol(); protected void
	 * CmWavenumber(); protected void CmEV();
	 */

	protected void SetupWindow() {

	}

	public void Execute() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(false);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}
}
