package sbeam;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

public class AngularDialog extends javax.swing.JDialog implements
		ActionListener, ListSelectionListener {

	protected JCheckBox real_tof_checkbox, calc_tof_checkbox, angle_checkbox;
	protected JRadioButton entire_tof_range_radio, limited_tof_range_radio;
	protected JFormattedTextField lower_time_edit, upper_time_edit;

	protected JList<String> angle_listbox, not_inc_listbox, included_listbox;

	protected JLabel not_inc_type_static, not_inc_labang_static,
			not_inc_polang_static;
	protected JLabel included_type_static, included_labang_static,
			included_polang_static;
	protected JLabel not_inc_depol_static, included_depol_static;

	protected JButton add_button, remove_button, ok, cancel;

	protected boolean IsLabAngleDialog;
	protected boolean EntireRangeChosen;

	protected boolean show_real, show_calc, show_angle;
	protected boolean[] is_included;

	protected String[] total_tof_list;
	protected String[] not_inc_TOF_list, included_TOF_list;
	protected String[] angle_list;
	protected String[] angle_pointer;

	protected int[] inc_TOF_nums, not_inc_TOF_nums;

	protected boolean[] is_real_tof;

	protected String[] tof_type, lab_angles, polar_angles, depol;

	protected TOF_data[] tofs;

	protected int total_number_tofs, num_included_tofs;
	protected int chosen_angle_number;

	protected float start_time, end_time;

	protected boolean ID;

	protected MainFrame parent;

	public AngularDialog(MainFrame parent) {
		this.parent = parent;

		real_tof_checkbox = new JCheckBox();
		calc_tof_checkbox = new JCheckBox();
		angle_checkbox = new JCheckBox();
		real_tof_checkbox.addActionListener(this);
		calc_tof_checkbox.addActionListener(this);
		angle_checkbox.addActionListener(this);

		entire_tof_range_radio = new JRadioButton();
		entire_tof_range_radio.addActionListener(this);
		limited_tof_range_radio = new JRadioButton();
		limited_tof_range_radio.addActionListener(this);
		ButtonGroup b = new ButtonGroup();
		b.add(entire_tof_range_radio);
		b.add(limited_tof_range_radio);

		NumberFormat format = NumberFormat.getNumberInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Double.class);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);

		lower_time_edit = new JFormattedTextField(formatter);
		upper_time_edit = new JFormattedTextField(formatter);

		angle_listbox = new JList<String>(new DefaultListModel<String>());
		not_inc_listbox = new JList<String>(new DefaultListModel<String>());
		included_listbox = new JList<String>(new DefaultListModel<String>());

		not_inc_listbox.addListSelectionListener(this);
		included_listbox.addListSelectionListener(this);

		not_inc_type_static = new JLabel("Type:");
		not_inc_labang_static = new JLabel("Lab Angle(degrees):");
		not_inc_polang_static = new JLabel("Polarization Angle(degrees):");
		included_type_static = new JLabel("Type:");
		included_labang_static = new JLabel("Lab Angle(degrees):");
		included_polang_static = new JLabel("Polarization Angle(degrees):");
		not_inc_depol_static = new JLabel("Depolarization(0-1):");
		included_depol_static = new JLabel("Depolarization(0-1):");
		// degree_static = new TStatic(this, IDC_ANGDEGREESSTATIC);

		add_button = new JButton("Add");
		remove_button = new JButton("Remove");
		ok = new JButton("OK");
		cancel = new JButton("Cancel");
		add_button.addActionListener(this);
		remove_button.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);

		chosen_angle_number = 0;
		EntireRangeChosen = true;

		start_time = 0;
		end_time = 100;

	}

	// public void SetAngleChoice(boolean is_lab) {IsLabAngleDialog = is_lab;}

	public void SetDialogData(int num_tofs, TOF_data[] sent_tofs, boolean is_lab) {
		int i, j;
		float temp_lab_angle, temp_polar_angle, temp_depol;

		IsLabAngleDialog = is_lab;

		int angle_count;
		boolean add_to_angle_list;

		TOF_data current_tof;

		total_number_tofs = num_tofs;
		angle_count = 0;

		tofs = sent_tofs;

		total_tof_list = new String[total_number_tofs];
		is_real_tof = new boolean[total_number_tofs];
		is_included = new boolean[total_number_tofs];
		tof_type = new String[total_number_tofs];
		lab_angles = new String[total_number_tofs];
		polar_angles = new String[total_number_tofs];
		depol = new String[total_number_tofs];

		inc_TOF_nums = new int[total_number_tofs];
		not_inc_TOF_nums = new int[total_number_tofs];

		if (IsLabAngleDialog) {
			angle_pointer = polar_angles;
		} else {
			angle_pointer = lab_angles;
		}

		angle_list = new String[total_number_tofs];

		for (i = 0; i < total_number_tofs; i++) {
			current_tof = tofs[i];
			is_included[i] = false;

			tof_type[i] = "";
			lab_angles[i] = "";
			polar_angles[i] = "";
			depol[i] = "";
			angle_list[i] = "";

			total_tof_list[i] = current_tof.GetTitle();
			temp_lab_angle = current_tof.GetLabAngle();
			if (current_tof.GetLaserPolarized()) {
				temp_polar_angle = current_tof.GetPolarizationAngle();
				temp_depol = current_tof.GetDepolarization();
				polar_angles[i] = "" + temp_polar_angle;
				depol[i] = "" + temp_depol;
			} else {
				polar_angles[i] = "None";
				depol[i] = "None";
			}
			is_real_tof[i] = current_tof.GetIsRealTOF();

			lab_angles[i] = "" + temp_lab_angle;

			if (i == 0) {
				angle_list[0] = angle_pointer[0];
				angle_count++;
			} else {
				add_to_angle_list = true;
				for (j = 0; j < angle_count; j++) {
					if (angle_list[j].equals(angle_pointer[i])) // i.e. if this
																// angle not
																// already in
																// list
					{
						add_to_angle_list = false;
						j = angle_count;
					}
				}
				if (add_to_angle_list) {
					angle_list[angle_count] = angle_pointer[i];
					angle_count++;
				}
			}

			if (is_real_tof[i]) {
				tof_type[i] = "Real TOF";
			} else {
				tof_type[i] = "Calculated TOF";
			}
		}

		for (i = angle_count; i < total_number_tofs; i++) {
			angle_list[i] = "";
		}

		int k;
		show_real = true;
		real_tof_checkbox.setSelected(true);
		show_calc = true;
		calc_tof_checkbox.setSelected(true);
		show_angle = false;

		// CmAngleCheckBox();

		if (IsLabAngleDialog) {
			// angle_checkbox
			// ("Show only TOFs with polarization angle (degrees) =");
		} else {
			// angle_checkbox.SetCaption("Show only TOFs with lab angle (degrees) =");
		}

		for (k = 0; k < total_number_tofs; k++) {
			if (angle_list[k] != null) {
				// if(strcmp(angle_list[i], "None") != 0)
				// {
				((DefaultListModel<String>) angle_listbox.getModel())
						.addElement(angle_list[k]);
				// }
			}
		}

		if (show_angle) {
			angle_checkbox.setSelected(true);
			angle_listbox.setEnabled(true);
			angle_listbox.setSelectedIndex(chosen_angle_number);
		} else {
			angle_checkbox.setSelected(false);
			angle_listbox.setEnabled(false);
			angle_listbox.clearSelection();
		}

		lower_time_edit.setText("");
		upper_time_edit.setText("");

		lower_time_edit.setEnabled(false);
		upper_time_edit.setEnabled(false);

		entire_tof_range_radio.setSelected(true);
		limited_tof_range_radio.setSelected(false);

		EntireRangeChosen = true;

		FillListBoxes();
	}

	public int GetNumIncludedTOFs() {
		return num_included_tofs;
	}

	public int[] GetIncludedTOFArray() {
		return inc_TOF_nums;
	}

	public int[] GetTOFNumArray() {
		int[] array_of_TOFNums;
		int i;

		array_of_TOFNums = new int[num_included_tofs];

		for (i = 0; i < num_included_tofs; i++) {
			if (inc_TOF_nums[i] != -1) {
				array_of_TOFNums[i] = tofs[inc_TOF_nums[i]].GetTOFNum();
			}
		}
		return array_of_TOFNums;
	}

	public boolean[] GetIsTOFIncluded() {
		return is_included;
	}

	public boolean GetIsEntireRange() {
		return EntireRangeChosen;
	}

	public float GetStartTime() {
		return start_time;
	}

	public float GetEndTime() {
		return end_time;
	}

	protected void SetupWindow() {
		JPanel contTop = new JPanel();
		contTop.setBorder(BorderFactory.createTitledBorder("TOF List Filters:"));
		contTop.setLayout(new BoxLayout(contTop, BoxLayout.X_AXIS));
		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
		temp.add(
				getLabelledPanel(real_tof_checkbox, "Show real TOF's?", false),
				BorderLayout.NORTH);
		temp.add(
				getLabelledPanel(calc_tof_checkbox, "Show calculated TOF's?",
						false), BorderLayout.SOUTH);
		contTop.add(temp);
		contTop.add(getLabelledPanel(angle_checkbox,
				"Show only TOF's with polarization angle (degrees):", false));
		contTop.add(new JScrollPane(angle_listbox));

		JPanel mid = new JPanel();
		mid.setLayout(new BoxLayout(mid, BoxLayout.X_AXIS));
		mid.setBorder(BorderFactory
				.createTitledBorder("Integrated TOF Time Range:"));
		mid.add(getLabelledPanel(entire_tof_range_radio, "Entire TOF Range",
				false));
		mid.add(getLabelledPanel(limited_tof_range_radio,
				"Integrate TOF's from ", false));
		mid.add(getLabelledPanel(lower_time_edit, "µs to ", false));
		mid.add(getLabelledPanel(upper_time_edit, "µs.", false));

		JPanel botMid = new JPanel();
		botMid.setLayout(new BoxLayout(botMid, BoxLayout.X_AXIS));
		botMid.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(), "TOF's not included:"));
		botMid.add(new JScrollPane(not_inc_listbox));
		JPanel tempRight = new JPanel(new BorderLayout());
		tempRight.setLayout(new BoxLayout(tempRight, BoxLayout.Y_AXIS));
		tempRight.add(not_inc_type_static);
		tempRight.add(not_inc_labang_static);
		tempRight.add(not_inc_polang_static);
		tempRight.add(not_inc_depol_static);
		tempRight.add(add_button);
		botMid.add(tempRight);

		JPanel bot = new JPanel();
		bot.setLayout(new BoxLayout(bot, BoxLayout.X_AXIS));
		bot.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(), "Included TOF's:"));
		bot.add(new JScrollPane(included_listbox));
		tempRight = new JPanel();
		tempRight.setLayout(new BoxLayout(tempRight, BoxLayout.Y_AXIS));
		tempRight.add(included_type_static);
		tempRight.add(included_labang_static);
		tempRight.add(included_polang_static);
		tempRight.add(included_depol_static);
		tempRight.add(remove_button);
		bot.add(tempRight);

		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
		pan.add(cancel);
		pan.add(ok);
		pan.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(contTop);
		this.add(mid);
		this.add(botMid);
		this.add(bot);
		this.add(pan);
	}

	private JPanel getLabelledPanel(Component c, String s, boolean labelFirst) {
		JPanel tempPan = new JPanel();
		tempPan.setLayout(new BoxLayout(tempPan, BoxLayout.X_AXIS));
		if (labelFirst) {
			tempPan.add(new JLabel(s));
			tempPan.add(c);
		} else {
			tempPan.add(c);
			tempPan.add(new JLabel(s));
		}
		return tempPan;
	}

	public void Execute() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(false);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}

	protected void FillListBoxes() {
		int i;
		boolean in_list;
		int angle_choice = 0;

		String angle_string = "";

		int inc_count, not_inc_count;

		((DefaultListModel<String>) included_listbox.getModel()).clear();
		((DefaultListModel<String>) not_inc_listbox.getModel()).clear();

		inc_count = 0;
		not_inc_count = 0;

		if (show_angle) {
			angle_choice = angle_listbox.getSelectedIndex();
			if (angle_choice >= 0) {
				angle_string = ((DefaultListModel<String>) angle_listbox
						.getModel()).elementAt(angle_choice);
			}
		}

		// Run through each TOF and find which ones to put in each list
		for (i = 0; i < total_number_tofs; i++) {
			in_list = false;
			// Put real TOFs in one of the lists if show_real is true
			if (show_real && is_real_tof[i]) {
				in_list = true;
			}

			// Put calculated TOFs in one of the lists if show_real is true
			if (show_calc && !is_real_tof[i]) {
				in_list = true;
			}

			// If filtering out angles, don't put any of the above in the list
			// if they
			// don't have the correct angle
			if (in_list == true) {
				if (show_angle) {
					if (angle_choice >= 0) {
						if (angle_string.equals(angle_pointer[i])) {
							in_list = false;
						}
					}
				}
			}

			if (in_list == true) {
				if (is_included[i]) {
					((DefaultListModel<String>) included_listbox.getModel())
							.addElement(total_tof_list[i]);
					inc_TOF_nums[inc_count] = i;
					inc_count++;
				} else {
					((DefaultListModel<String>) not_inc_listbox.getModel())
							.addElement(total_tof_list[i]);
					not_inc_TOF_nums[not_inc_count] = i;
					not_inc_count++;
				}
			}
		}

		for (i = inc_count; i < total_number_tofs; i++) {
			inc_TOF_nums[i] = -1;
		}

		for (i = not_inc_count; i < total_number_tofs; i++) {
			not_inc_TOF_nums[i] = -1;
		}

		num_included_tofs = inc_count;

		if (inc_TOF_nums[0] == -1) // i.e. if nothing in included list
		{
			included_listbox.clearSelection();
			included_listbox.setEnabled(false);
		} else {
			included_listbox.setEnabled(true);
			included_listbox.setSelectedIndex(0);
		}

		if (not_inc_TOF_nums[0] == -1) // i.e. if nothing in not included list
		{
			not_inc_listbox.clearSelection();
			not_inc_listbox.setEnabled(false);
		} else {
			not_inc_listbox.setEnabled(true);
			not_inc_listbox.setSelectedIndex(0);
		}
		// EvLbnSelIncludedList();
		// EvLbnSelNotIncList();
	}

	protected void CmOk() {
		float first_time, second_time;
		if (!EntireRangeChosen) {
			// Save info for starting and ending times

			first_time = Float.parseFloat(lower_time_edit.getText());
			second_time = Float.parseFloat(upper_time_edit.getText());

			start_time = Math.min(first_time, second_time);
			end_time = Math.max(first_time, second_time);
		}

		this.dispose();
	}

	protected void CmCancel() {
		this.dispose();
	}

	protected void CmAddTOF() {
		int selected_index, selected_tof;

		selected_index = not_inc_listbox.getSelectedIndex();
		if (selected_index >= 0) {
			selected_tof = not_inc_TOF_nums[selected_index];
			is_included[selected_tof] = true;
			FillListBoxes();
		}
	}

	protected void CmRemoveTOF() {
		int selected_index, selected_tof;

		selected_index = included_listbox.getSelectedIndex();
		if (selected_index >= 0) {
			selected_tof = inc_TOF_nums[selected_index];
			is_included[selected_tof] = false;
			FillListBoxes();
		}
	}

	protected void CmRealTOFCheckBox() {

		if (real_tof_checkbox.isSelected()) {
			show_real = true;
		}

		if (real_tof_checkbox.isSelected() == false) {
			show_real = false;
		}

		FillListBoxes();
	}

	protected void CmCalcTOFCheckBox() {

		if (calc_tof_checkbox.isSelected()) {
			show_calc = true;
		}

		if (calc_tof_checkbox.isSelected() == false) {
			show_calc = false;
		}

		FillListBoxes();
	}

	protected void CmAngleCheckBox() {
		if (angle_checkbox.isSelected()) {
			show_angle = true;
			angle_listbox.setEnabled(true);
			angle_listbox.setSelectedIndex(chosen_angle_number);
		}

		if (angle_checkbox.isSelected() == false) {
			show_angle = false;
			angle_listbox.setEnabled(false);
			chosen_angle_number = angle_listbox.getSelectedIndex();
			angle_listbox.setSelectedIndex(-1);
		}

		FillListBoxes();
	}

	protected void CmEntireRangeRadio() {
		if (!EntireRangeChosen) {
			// Save info for starting and ending times

			start_time = Float.parseFloat(lower_time_edit.getText());
			end_time = Float.parseFloat(upper_time_edit.getText());

			lower_time_edit.setText("");
			upper_time_edit.setText("");

			lower_time_edit.setEnabled(false);
			upper_time_edit.setEnabled(false);

			entire_tof_range_radio.setSelected(true);
			limited_tof_range_radio.setSelected(false);

			EntireRangeChosen = true;
		}
	}

	protected void CmLimitedRangeRadio() {
		if (EntireRangeChosen) {
			// Save info for starting and ending times

			lower_time_edit.setEnabled(true);
			upper_time_edit.setEnabled(true);

			lower_time_edit.setText("" + start_time);
			upper_time_edit.setText("" + end_time);

			entire_tof_range_radio.setSelected(false);
			limited_tof_range_radio.setSelected(true);

			EntireRangeChosen = false;
		}
	}

	/*
	 * protected void EvLbnDblClkNotIncList();
	 * 
	 * protected void EvLbnDblClkIncludedList();
	 * 
	 * protected void EvLbnSelAngleList();
	 * 
	 * protected void EvLbnSelNotIncList();
	 * 
	 * protected void EvLbnSelIncludedList();
	 */

	public static void main(String[] args) {
		AngularDialog a = new AngularDialog(null);
		a.Execute();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(ok)) {
			// pass info back to brains and move on
			ID = true;
			CmOk();
		} else if (e.getSource().equals(cancel)) {
			ID = false;
			CmCancel();
		} else if (e.getSource().equals(add_button)) {
			CmAddTOF();
		} else if (e.getSource().equals(remove_button)) {
			CmRemoveTOF();
		} else if (e.getSource().equals(limited_tof_range_radio)) {
			CmLimitedRangeRadio();
		} else if (e.getSource().equals(entire_tof_range_radio)) {
			CmEntireRangeRadio();
		} else if (e.getSource().equals(real_tof_checkbox)) {
			CmRealTOFCheckBox();
		} else if (e.getSource().equals(calc_tof_checkbox)) {
			CmCalcTOFCheckBox();
		} else if (e.getSource().equals(angle_checkbox)) {
			CmAngleCheckBox();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(included_listbox)) {
			int selection, tof_num;

			selection = included_listbox.getSelectedIndex();
			included_type_static.setText("");
			included_labang_static.setText("");
			included_polang_static.setText("");
			included_depol_static.setText("");
			if (selection != -1) {
				tof_num = inc_TOF_nums[selection];
				included_type_static.setText("Type: " + tof_type[tof_num]);
				included_labang_static.setText("Lab Angle(degrees): "
						+ lab_angles[tof_num]);
				included_polang_static.setText("Polarization Angle(degrees): "
						+ polar_angles[tof_num]);
				included_depol_static.setText(depol[tof_num]);
				remove_button.setEnabled(true);
			} else {
				remove_button.setEnabled(false);
			}

		} else if (e.getSource().equals(not_inc_listbox)) {
			int selection, tof_num;

			selection = not_inc_listbox.getSelectedIndex();
			not_inc_type_static.setText("");
			not_inc_labang_static.setText("");
			not_inc_polang_static.setText("");
			not_inc_depol_static.setText("");
			if (selection != -1) {
				tof_num = not_inc_TOF_nums[selection];
				not_inc_type_static.setText("Type: " + tof_type[tof_num]);
				not_inc_labang_static.setText("Lab Angle(degrees): "
						+ lab_angles[tof_num]);
				not_inc_polang_static.setText("Polarization Angle(degrees): "
						+ polar_angles[tof_num]);
				not_inc_depol_static.setText("Depolarization(0-1): "
						+ depol[tof_num]);
				add_button.setEnabled(true);
			} else {
				add_button.setEnabled(false);
			}
		}

	}
}
