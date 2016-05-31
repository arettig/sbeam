package sbeam;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;

public class TOF_Edit_Dialog_2 extends JDialog {

	private JFormattedTextField tof_title_edit;

	private JLabel calc_title_static, poe_ion_chan_static, poe_beta_static;
	private JLabel ion_chan_mass1_static, ion_chan_mass2_static,
			ion_chan_weight_static;
	private JLabel det_scheme_static, flight_length_static, bm_ang_static;
	private JLabel det_ang_static, ion_me_static, bm_segs_static,
			det_segs_static;
	private JLabel lab_ang_static, alpha_static, speed_ratio_static,
			bm_vel_segs_static;
	private JLabel ion_dist_static, ion_segs_static, lab_vel_min_static,
			lab_vel_max_static;
	private JLabel lab_vel_segs_static, laser_polar_static, polar_ang_static,
			depol_static;
	private JLabel start_time_static, end_time_static, num_time_points_static;
	private JLabel ion_len_static;

	private JList<String> poe_list_box, chan_list_box;

	private JCheckBox detached_check_box;

	private String tof_title;
	private String calc_title;
	private String[] poe_title_array;
	private POE_calc_data[] poe_calc_info;

	private boolean is_number_density, is_gaussian_ionization, is_polarized,
			is_detached;
	private float flight_len, beam_angle, detect_angle, lab_angle, ion_m_e;
	private float polar_angle, depolarization;
	private float alpha_param, speed_ratio, min_lab_vel, max_lab_vel;
	private float start_time, end_time, ionizer_len;

	private int num_beam_ang_segs, num_det_ang_segs, num_ionizer_segs,
			num_beam_vel_segs;
	private int num_lab_vel_segs, num_time_points;
	private int number_of_poes;

	private int[] poe_number_array;

	private int current_chosen_poe_index;
	protected MainFrame parent;
	protected boolean ID;

	public TOF_Edit_Dialog_2(MainFrame parent) {
		// TODO Auto-generated constructor stub
		this.parent = parent;
		this.setTitle("Time of Flight Info for Calculated TOF:");
		tof_title_edit = new JFormattedTextField();

		calc_title_static = new JLabel();
		poe_ion_chan_static = new JLabel();
		poe_beta_static = new JLabel();
		ion_chan_mass1_static = new JLabel();
		ion_chan_mass2_static = new JLabel();
		ion_chan_weight_static = new JLabel();
		det_scheme_static = new JLabel();
		flight_length_static = new JLabel();
		bm_ang_static = new JLabel();
		det_ang_static = new JLabel();
		ion_me_static = new JLabel();
		bm_segs_static = new JLabel();
		det_segs_static = new JLabel();
		lab_ang_static = new JLabel();
		alpha_static = new JLabel();
		speed_ratio_static = new JLabel();
		bm_vel_segs_static = new JLabel();
		ion_dist_static = new JLabel();
		ion_segs_static = new JLabel();
		lab_vel_min_static = new JLabel();
		lab_vel_max_static = new JLabel();
		lab_vel_segs_static = new JLabel();
		laser_polar_static = new JLabel();
		polar_ang_static = new JLabel();
		depol_static = new JLabel();
		start_time_static = new JLabel();
		end_time_static = new JLabel();
		num_time_points_static = new JLabel();
		ion_len_static = new JLabel();

		poe_list_box = new JList<String>(new DefaultListModel<String>());
		chan_list_box = new JList<String>(new DefaultListModel<String>());

		detached_check_box = new JCheckBox();

		tof_title = "";
		poe_number_array = null;

		current_chosen_poe_index = -1;

		int i;
		int count = 0;

		poe_number_array = new int[number_of_poes];

		tof_title_edit.setText(tof_title);

		calc_title_static.setText(calc_title);

		String temp_string = "";

		if (is_number_density) {
			det_scheme_static.setText("Ion Number Density");
		} else {
			det_scheme_static.setText("Ion Flux");
		}

		flight_length_static.setText("" + "" + flight_len);

		bm_ang_static.setText("" + beam_angle);

		det_ang_static.setText("" + detect_angle);

		ion_me_static.setText("" + ion_m_e);

		bm_segs_static.setText("" + num_beam_ang_segs);

		det_segs_static.setText("" + num_det_ang_segs);

		lab_ang_static.setText("" + lab_angle);

		alpha_static.setText("" + alpha_param);

		speed_ratio_static.setText("" + speed_ratio);

		bm_vel_segs_static.setText("" + num_beam_vel_segs);

		if (is_gaussian_ionization) {
			ion_dist_static.setText("" + "Is Gaussian.");
		} else {
			ion_dist_static.setText("" + "Is Not Gaussian.");
		}

		ion_segs_static.setText("" + num_ionizer_segs);

		lab_vel_min_static.setText("" + min_lab_vel);

		lab_vel_max_static.setText("" + max_lab_vel);

		lab_vel_segs_static.setText("" + num_lab_vel_segs);

		if (is_polarized) {
			laser_polar_static.setText("" + "Laser IS Polarized.");

			polar_ang_static.setText("" + polar_angle);

			depol_static.setText("" + depolarization);
		} else {
			laser_polar_static.setText("" + "Laser IS NOT Polarized.");
			polar_ang_static.setText("" + " ");
			depol_static.setText("" + " ");
		}

		start_time_static.setText("" + start_time);

		end_time_static.setText("" + end_time);

		num_time_points_static.setText("" + num_time_points);

		ion_len_static.setText("" + ionizer_len);

		((DefaultListModel<String>) poe_list_box.getModel()).clear();

		// Fill information in the list boxes
		for (i = 0; i < number_of_poes; i++) {
			if (poe_calc_info[i].is_included) {
				poe_number_array[count] = i;
				((DefaultListModel<String>) poe_list_box.getModel())
						.addElement(poe_title_array[i]);
				count++;
			}
		}
		poe_list_box.setSelectedIndex(0);
		CmPOETitleChosen();

		detached_check_box.setEnabled(false);
		if (is_detached)
			detached_check_box.setEnabled(true);
		else
			detached_check_box.setSelected(false);

	}

	public void SetTOFTitle(String input_text) {
		tof_title = input_text;
	}

	public void SetCalcTitle(String input_text) {
		calc_title = input_text;
	}

	public void SetPOECalcData(POE_calc_data[] calc_info) {
		poe_calc_info = calc_info;
	}

	public void SetInstParams(boolean IsNumDensity, float flt_len,
			float bm_ang, float det_ang, float ion_len) {
		is_number_density = IsNumDensity;
		flight_len = flt_len;
		beam_angle = bm_ang;
		detect_angle = det_ang;
		ionizer_len = ion_len;
	}

	public void SetMainCalcParams(float lab_ang, float ion_me, int bm_segs,
			int det_segs) {
		lab_angle = lab_ang;
		ion_m_e = ion_me;
		num_beam_ang_segs = bm_segs;
		num_det_ang_segs = det_segs;
	}

	public void SetIonInfo(boolean is_gaussian, int num_ion_segs) {
		is_gaussian_ionization = is_gaussian;
		num_ionizer_segs = num_ion_segs;
	}

	public void SetPolarInfo(boolean is_polar, float polar_ang, float depol) {
		is_polarized = is_polar;
		polar_angle = polar_ang;
		depolarization = depol;
	}

	public void SetBmParams(float alpha, float spd_rat, int num_bm_segs) {
		alpha_param = alpha;
		speed_ratio = spd_rat;
		num_beam_vel_segs = num_bm_segs;
	}

	public void SetLabVelParams(float min_vel, float max_vel, int num_lv_segs) {
		min_lab_vel = min_vel;
		max_lab_vel = max_vel;
		num_lab_vel_segs = num_lv_segs;
	}

	public void SetTimeInfo(float start, float end, int num_points) {
		start_time = start;
		end_time = end;
		num_time_points = num_points;
	}

	public void SetPOETitleArray(int num_total_poes, String[] titles) {
		number_of_poes = num_total_poes;
		poe_title_array = titles;
	}

	public void SetDetached(boolean t_or_f) {
		is_detached = t_or_f;
	}

	public String GetTOFTitle() {
		return tof_title;
	}

	private void SetupWindow(){
		
	}

	public void Execute(){
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		SetupWindow();
		this.pack();
		this.setResizable(false);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}
	
	private void CmOk() {
		tof_title = tof_title_edit.getText();
		ID = true;
		this.dispose();
	}

	private void CmCancel() {
		ID = false;
		this.dispose();
	}

	private void CmPOETitleChosen() {

		int chosen_index, chosen_poe_num, i, number_ionization_channels;

		String channel_name_string;
		String temp_string;

		chosen_index = poe_list_box.getSelectedIndex();
		if (current_chosen_poe_index != chosen_index) {
			current_chosen_poe_index = chosen_index;
			temp_string = "";
			chosen_poe_num = poe_number_array[chosen_index];
			number_ionization_channels = poe_calc_info[chosen_poe_num].num_channels;

			poe_ion_chan_static.setText("" + number_ionization_channels);
			poe_beta_static.setText(""
					+ poe_calc_info[chosen_poe_num].beta_param);

			// Add strings to channel list box
			((DefaultListModel<String>) chan_list_box.getModel()).clear();
			for (i = 0; i < number_ionization_channels; i++) {
				channel_name_string = "Channel #";
				channel_name_string += (i + 1);

				((DefaultListModel<String>) chan_list_box.getModel())
						.addElement(channel_name_string);
			}
			chan_list_box.setSelectedIndex(0);
			CmChannelChosen();
		}
	}

	private void CmChannelChosen() {
		int chosen_index, chosen_poe_num;
		String temp_string = "";

		chosen_index = chan_list_box.getSelectedIndex();
		chosen_poe_num = poe_number_array[current_chosen_poe_index];

		ion_chan_mass1_static.setText(""
				+ poe_calc_info[chosen_poe_num].mass_1[chosen_index]);

		ion_chan_mass2_static.setText(""
				+ poe_calc_info[chosen_poe_num].mass_2[chosen_index]);

		ion_chan_weight_static.setText(""
				+ poe_calc_info[chosen_poe_num].rel_weight[chosen_index]);

	}
	
	

}
