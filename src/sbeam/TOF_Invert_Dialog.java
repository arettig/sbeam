package sbeam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.text.NumberFormatter;

public class TOF_Invert_Dialog extends JDialog implements ActionListener {

	protected MainFrame parent;

	protected boolean IsOpen;

	protected JFormattedTextField mass1_edit, mass2_edit, beta_edit,
			beam_velocity_edit;
	protected JButton ok, cancel;
	protected String mass1;
	protected String mass2;
	protected String beta;
	protected String beam_velocity;
	public boolean ID;

	public TOF_Invert_Dialog(MainFrame p) {
		// TODO Auto-generated constructor stub
		super(p);
		parent = p;
		IsOpen = false;
		this.setTitle("Parameters needed for inversion:");

		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Double.class);
		formatter.setMinimum(Double.MIN_VALUE);
		formatter.setMaximum(Double.MAX_VALUE);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);

		mass1_edit = new JFormattedTextField(formatter);
		mass2_edit = new JFormattedTextField(formatter);
		beta_edit = new JFormattedTextField(formatter);
		beam_velocity_edit = new JFormattedTextField(formatter);

		ok = new JButton("ok");
		cancel = new JButton("cancel");
		ok.addActionListener(this);
		cancel.addActionListener(this);
		ID = false;
	}

	public void SetMass1(String input_text) {
		mass1 = input_text;
	}

	public void SetMass2(String input_text) {
		mass2 = input_text;
	}

	public void SetBeta(String input_text) {
		beta = input_text;
	}

	public void SetBeamVelocity(String input_text) {
		beam_velocity = input_text;
	}

	public String GetMass1() {
		return mass1;
	}

	public String GetMass2() {
		return mass2;
	}

	public String GetBeta() {
		return beta;
	}

	public String GetBeamVelocity() {
		return beam_velocity;
	}

	public boolean GetStatus() {
		return IsOpen;
	}

	protected void SetupWindow() {
		mass1_edit.setText(mass1);
		mass2_edit.setText(mass2);
		beta_edit.setText(beta);
		beam_velocity_edit.setText(beam_velocity);
		
		JPanel cont = new JPanel();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(getLabelledPanel(mass1_edit, "Mass 1: "));
		cont.add(getLabelledPanel(mass2_edit, "Mass 2: "));
		cont.add(getLabelledPanel(beta_edit, "Beta: "));
		cont.add(getLabelledPanel(beam_velocity_edit, "Beam Velocity: "));
		
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		this.add(cont, BorderLayout.NORTH);
		this.add(pan, BorderLayout.SOUTH);
	}

	private JPanel getLabelledPanel(Component c, String s) {
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		tempPan.add(new JLabel(s));
		tempPan.add(c);
		return tempPan;
	}
	
	public void Execute(){
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(false);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			ID = true;
			this.dispose();
		} else if (e.getSource().equals(cancel)) {
			ID = false;
			this.dispose();
		}
	}

}
