package sbeam;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

public class ScaleTOFDialog extends JDialog implements ActionListener{

	protected boolean IsOpen;

	protected JRadioButton fill_graph_radio, scale_sing_tof_radio,
			baseline_radio, match_min_radio;
	protected JList<String> choose_tof_listbox;
	protected JButton select_minimum_button, select_maximum_button,
			apply_range_button, ok, cancel;
	protected JFormattedTextField time1_edit, time2_edit;

	protected String time_1;
	protected String time_2;

	protected String old_time_1_string;
	protected String old_time_2_string;

	protected boolean should_scale_to_TOF, average_baseline;
	protected boolean from_show_window;

	protected String[] text_of_list;
	protected int length_of_list;
	protected int chosen_index;

	protected int temporary_scaling_TOF;

	boolean show_minimum_line;

	protected boolean show_maximum_line;
	protected boolean move_minimum_line, move_maximum_line;
	protected boolean okay_clicked, baseline_range_changed;
	protected boolean message_from_dialog;

	protected boolean minima_button_depressed, maxima_button_depressed;

	protected float time1;

	protected float time2;
	protected MainFrame parent;
	protected TOFView parent_tof;

	public ScaleTOFDialog(MainFrame parent, TOFView tof) {
		// TODO Auto-generated constructor stub
		this.parent = parent;
		parent_tof = tof;

		IsOpen = false;
		this.setTitle("Scaling information for TOFs in this display:");

		NumberFormat format = NumberFormat.getNumberInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Double.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);
		time1_edit = new JFormattedTextField(formatter);
		time2_edit = new JFormattedTextField(formatter);

		fill_graph_radio = new JRadioButton();
		fill_graph_radio.addActionListener(this);
		scale_sing_tof_radio = new JRadioButton();
		scale_sing_tof_radio.addActionListener(this);
		ButtonGroup group1 = new ButtonGroup();
		group1.add(fill_graph_radio);
		group1.add(scale_sing_tof_radio);
		
		baseline_radio = new JRadioButton();
		baseline_radio.addActionListener(this);
		match_min_radio = new JRadioButton();
		match_min_radio.addActionListener(this);
		ButtonGroup group2 = new ButtonGroup();
		group2.add(baseline_radio);
		group2.add(match_min_radio);

		choose_tof_listbox = new JList<String>(new DefaultListModel<String>());

		select_minimum_button = new JButton("Select amplitude for minima scaling");
		select_minimum_button.addActionListener(this);
		select_maximum_button = new JButton("Select amplitude for maxima scaling");
		select_maximum_button.addActionListener(this);
		apply_range_button = new JButton("Apply to minimum line");
		apply_range_button.addActionListener(this);
		ok = new JButton("OK");
		ok.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		maxima_button_depressed = false;
		minima_button_depressed = false;

		from_show_window = true;

		length_of_list = 0;
		time1 = 0;
		time2 = 0;

		old_time_1_string = "";
		old_time_2_string = "";

	}
	
	public void SetupWindow(){
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		JPanel pane1 = new JPanel();
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
		pane1.add(getLabelledPanel(fill_graph_radio, "scale all TOFs to fill graph"));
		pane1.add(getLabelledPanel(scale_sing_tof_radio, "scale all TOFs to a single TOF"));
		
		JPanel pane2 = new JPanel();
		pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
		pane2.setBorder(BorderFactory
				.createTitledBorder("Choose a TOF to use for scaling:"));
		pane2.add(new JScrollPane(choose_tof_listbox));
		
		JPanel pane3 = new JPanel();
		pane3.setLayout(new BoxLayout(pane3, BoxLayout.Y_AXIS));
		pane3.setBorder(BorderFactory
				.createTitledBorder("Minima:"));
		pane3.add(getLabelledPanel(baseline_radio, "Match averaged baselines of TOFs:"));

		JPanel pane4 = new JPanel();
		pane4.setLayout(new BoxLayout(pane4, BoxLayout.Y_AXIS));
		pane4.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		JPanel pane4sub = new JPanel();
		pane4sub.setLayout(new BoxLayout(pane4sub, BoxLayout.X_AXIS));
		pane4sub.add(new JLabel("Time range of baseline: Between"));
		pane4sub.add(time1_edit);
		pane4sub.add(new JLabel("and"));
		pane4sub.add(time2_edit);
		pane4sub.add(new JLabel("μs"));
		pane4.add(pane4sub);
		pane4.add(apply_range_button);
		pane3.add(pane4);
		
		pane3.add(getLabelledPanel(match_min_radio, "Scale minima to a fixed amplitude in the scaling TOF"));
		
		JPanel pane5 = new JPanel();
		pane5.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		pane5.add(select_minimum_button);
		pane3.add(pane5);
		
		pane2.add(pane3);
		
		JPanel pane6 = new JPanel();
		pane6.setBorder(BorderFactory
				.createTitledBorder("Maxima:"));
		pane6.add(select_maximum_button);
		
		pane2.add(pane6);
		
		JPanel pane7 = new JPanel();
		pane7.setLayout(new BoxLayout(pane7, BoxLayout.X_AXIS));
		pane7.add(ok);
		pane7.add(cancel);
		
		this.getContentPane().add(pane1);
		this.getContentPane().add(pane2);
		this.getContentPane().add(pane7);
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
		//this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setModalityType(ModalityType.MODELESS);
		this.setVisible(true);
	}

	public void SetTimes(float first_time, float second_time) {
		time1 = first_time;
		time2 = second_time;
	}

	public void SetTime1(String input_text) {
		time_1 = input_text;
	}

	public void SetTime2(String input_text) {
		time_2 = input_text;
	}

	public void ResetTOFTitleList(int new_num_tofs, String[] new_array) {
		int i;
		int old_length_of_list = length_of_list;
		length_of_list = new_num_tofs;
		text_of_list = new_array;

		if (should_scale_to_TOF && IsOpen)
			chosen_index = choose_tof_listbox.getSelectedIndex();
		((DefaultListModel<String>) choose_tof_listbox.getModel()).clear();
		if (should_scale_to_TOF) {
			for (i = 0; i < length_of_list; i++) {
				((DefaultListModel<String>) choose_tof_listbox.getModel())
						.addElement(text_of_list[i]);
			}
			if (chosen_index >= length_of_list) {
				chosen_index = (length_of_list - 1);
			}
			choose_tof_listbox.setSelectedIndex(chosen_index);
		}
		if ((new_num_tofs == 1) && (old_length_of_list > 1)) {
			CmFillScale();
			CmOk();
		}
	}

	public void SetBools(boolean scale_to_TOF, boolean should_avg_baseline) {
		should_scale_to_TOF = scale_to_TOF;
		average_baseline = should_avg_baseline;
	}

	public void SetbooleanPointers(boolean show_min, boolean show_max,
			boolean move_min, boolean move_max, boolean from_dialog,
			boolean clicked_okay, boolean change_range) {
		show_minimum_line = show_min;
		show_maximum_line = show_max;
		move_minimum_line = move_min;
		move_maximum_line = move_max;
		message_from_dialog = from_dialog;
		okay_clicked = clicked_okay;
		baseline_range_changed = change_range;
	}

	public void SetTempScalingTOF(int index_number) {
		temporary_scaling_TOF = index_number;
	}

	public void SetListBoxList(int list_length, String[] list_text,
			int chosen_one) {
		text_of_list = list_text;
		length_of_list = list_length;
		chosen_index = chosen_one;
	}

	public void ResetMinimumButton() {
		minima_button_depressed = false;
		select_minimum_button.setText("Select amplitude for minima scaling");

		if (average_baseline == false) {
			select_maximum_button
					.setText("Select amplitude for maxima scaling");
			select_maximum_button.setEnabled(true);
		}
		move_minimum_line = false;
	}

	public void ResetMaximumButton() {
		maxima_button_depressed = false;
		select_maximum_button.setText("Select amplitude for maxima scaling");

		select_minimum_button.setText("Select amplitude for minima scaling");
		if (average_baseline == false) {
			select_minimum_button.setEnabled(true);
		}
		move_maximum_line = false;
	}

	public boolean GetScale() {
		return this.scale_sing_tof_radio.isSelected();
	}

	public boolean GetMin() {
		return average_baseline;
	}

	public int GetCurrentSelectedIndex() {
		return choose_tof_listbox.getSelectedIndex();
	}

	public boolean GetStatus() {
		return IsOpen;
	}

	public boolean ShowWindow(int cmdShow) {
		if (should_scale_to_TOF) {
			CmScaleToTOF();
		} else {
			CmFillScale();
		}

		IsOpen = true;
		return true;
	}

	public void getInput(int in){
		System.out.println(in);
		if(this.minima_button_depressed){
			time1 = in;
		}else if(this.maxima_button_depressed){
			time2 = in;
		}
		
	}
	
	// void EvMouseMove(unsigned int modKeys, TPoint& point);
	protected void EvClose() {
		CmCancel();
	}

	protected void CmOk() {

		if (should_scale_to_TOF)
			chosen_index = choose_tof_listbox.getSelectedIndex();

		temporary_scaling_TOF = -1;
		show_minimum_line = false;
		show_maximum_line = false;
		move_minimum_line = false;
		move_maximum_line = false;

		okay_clicked = true;

		if (should_scale_to_TOF && average_baseline) {
			time_1 = time1_edit.getText();
			time_2 = time2_edit.getText();

			time1 = Float.parseFloat(time_1);
			time2 = Float.parseFloat(time_2);
		}

		IsOpen = false;
		message_from_dialog = true;
		this.parent_tof.okay_was_clicked = true;
		this.parent_tof.changeScaling();
		this.dispose();
	}

	protected void CmCancel() {
		temporary_scaling_TOF = -1;
		show_minimum_line = false;
		show_maximum_line = false;
		move_minimum_line = false;
		move_maximum_line = false;

		okay_clicked = false;
		IsOpen = false;
		message_from_dialog = true;
		this.dispose();
	}

	protected void CmApplyButton() {
		time_1 = time1_edit.getText();
		time_2 = time2_edit.getText();

		time1 = Float.parseFloat(time_1);
		time2 = Float.parseFloat(time_2);

		temporary_scaling_TOF = choose_tof_listbox.getSelectedIndex();

		if (!time_1.equals(old_time_1_string)) {
			time_1 = "" + time1;
			time1_edit.setText(time_1);
			old_time_1_string = time_1;
		}

		if (!time_2.equals(old_time_2_string)) {
			time_2 = "" + time2;
			time2_edit.setText(time_2);
			old_time_2_string = time_2;
		}

		if (maxima_button_depressed) {
			ResetMaximumButton();
		}

		// Tell the parent window that a change has occurred through a MOUSEMOVE
		// command
		baseline_range_changed = true;
		message_from_dialog = true;
		
	}

	protected void CmFillScale() {
		// Set setSelected(true) to correct radio box and disable everything.

		if (should_scale_to_TOF && IsOpen) {
			chosen_index = choose_tof_listbox.getSelectedIndex();
			// Store information from average baseline if it is present
			if (average_baseline) {
				time_1 = time1_edit.getText();
				time_2 = time2_edit.getText();

				time1 = Float.parseFloat(time_1);
				time2 = Float.parseFloat(time_2);
			}
		}

		should_scale_to_TOF = false;

		baseline_radio.setSelected(false);
		baseline_radio.setEnabled(false);
		match_min_radio.setSelected(false);
		match_min_radio.setEnabled(false);

		((DefaultListModel<String>) choose_tof_listbox.getModel()).clear();
		choose_tof_listbox.setEnabled(false);

		select_maximum_button.setText("Select amplitude for maxima scaling");
		select_minimum_button.setText("Select amplitude for minima scaling");

		apply_range_button.setEnabled(false);
		select_minimum_button.setEnabled(false);
		select_maximum_button.setEnabled(false);
		minima_button_depressed = false;
		maxima_button_depressed = false;

		time1_edit.setText("");
		time1_edit.setEnabled(false);
		time2_edit.setText("");
		time2_edit.setEnabled(false);

		show_minimum_line = false;
		show_maximum_line = false;
		move_minimum_line = false;
		move_maximum_line = false;
		if (IsOpen) {
			message_from_dialog = true;
		}
	}

	protected void CmScaleToTOF() {
		int i;
		// Set setSelected(true) to correct radio box and enable everything.

		if (IsOpen) {
			should_scale_to_TOF = true;
		}

		baseline_radio.setEnabled(true);
		match_min_radio.setEnabled(true);
		if (average_baseline) {
			CmBaseline();
		} else {
			CmChooseMin();
		}

		((DefaultListModel<String>) choose_tof_listbox.getModel()).clear();
		choose_tof_listbox.setEnabled(true);

		for (i = 0; i < length_of_list; i++) {
			((DefaultListModel<String>) choose_tof_listbox.getModel())
					.addElement(text_of_list[i]);
		}
		if (chosen_index >= length_of_list) {
			chosen_index = length_of_list - 1;
		}
		choose_tof_listbox.setSelectedIndex(chosen_index);

		// select_minimum_button.setEnabled(true);
		select_maximum_button.setEnabled(true);

		show_minimum_line = true;
		show_maximum_line = true;
		move_minimum_line = false;
		move_maximum_line = false;
		message_from_dialog = true;
	}

	protected void CmBaseline() {

		average_baseline = true;

		time_1 = "" + time1;
		time_2 = "" + time2;

		time1_edit.setText(time_1);
		time1_edit.setEnabled(true);
		time2_edit.setText(time_2);
		time2_edit.setEnabled(true);

		apply_range_button.setEnabled(true);

		if (minima_button_depressed) {
			select_minimum_button
					.setText("Select amplitude for minima scaling");
			minima_button_depressed = false;
			select_maximum_button
					.setText("Select amplitude for maxima scaling");
			select_maximum_button.setEnabled(true);
		}
		select_minimum_button.setEnabled(false);

		show_minimum_line = true;
		show_maximum_line = true;
		move_minimum_line = false;
	}

	protected void CmChooseMin() {
		temporary_scaling_TOF = -1;

		// Store time information for later
		if (average_baseline) {
			time_1 = time1_edit.getText();
			time_2 = time2_edit.getText();

			time1 = Float.parseFloat(time_1);
			time2 = Float.parseFloat(time_2);
		}

		average_baseline = false;
		time1_edit.setText("");
		time1_edit.setEnabled(false);
		time2_edit.setText("");
		time2_edit.setEnabled(false);

		apply_range_button.setEnabled(false);

		if (maxima_button_depressed == false) {
			select_minimum_button.setEnabled(true);
		}
		show_minimum_line = true;
		show_maximum_line = true;
		move_minimum_line = false;
	}

	protected void CmMinimaButtonDepressed() {
		// Change the caption of the button and allow minimum movement in
		// TOFView		
		if (maxima_button_depressed == false) {
			if (minima_button_depressed == false) {
				minima_button_depressed = true;
				select_minimum_button
						.setText("Right click on graph minimum or click here to cancel");
				
				select_maximum_button
						.setText("(Currently setting minima scaling)");
				select_maximum_button.setEnabled(false);
				
				this.parent.listenForExtrema = this;
				move_minimum_line = true;
				move_maximum_line = false;
			} else {
				ResetMinimumButton();
				this.parent.listenForExtrema = null;
				message_from_dialog = true;
			}
		}
	}

	protected void CmMaximaButtonDepressed() {
		// Change the caption of the button and allow minimum movement in
		// TOFView
		if (minima_button_depressed == false) {
			if (maxima_button_depressed == false) {
				maxima_button_depressed = true;
				select_maximum_button
						.setText("Right click on graph maximum or click here to cancel");

				select_minimum_button
						.setText("(Currently setting maxima scaling)");
				select_minimum_button.setEnabled(false);

				this.parent.listenForExtrema = this;
				move_minimum_line = false;
				move_maximum_line = true;
			} else {
				ResetMaximumButton();
				this.parent.listenForExtrema = null;
				message_from_dialog = true;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource().equals(baseline_radio)){
			this.CmBaseline();
		}
		if(e.getSource().equals(fill_graph_radio)){
			this.CmFillScale();
		}
		if(e.getSource().equals(scale_sing_tof_radio)){
			this.CmScaleToTOF();
		}
		if(e.getSource().equals(match_min_radio)){
			this.CmChooseMin();
		}
		if(e.getSource().equals(apply_range_button)){
			this.CmApplyButton();
		}
		if(e.getSource().equals(select_maximum_button)){
			this.CmMaximaButtonDepressed();
		}
		if(e.getSource().equals(select_minimum_button)){
			this.CmMinimaButtonDepressed();
		}
		if(e.getSource().equals(ok)){
			this.CmOk();
		}
		if(e.getSource().equals(cancel)){
			this.CmCancel();
		}
	}

}
