package sbeam;

import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;


public class sbapp {

	protected int ViewNumber;
	protected int CalcNumber;
	protected int AngNumber;
	protected TOFPOEDocument session_document;

	protected TOF_Input_1_Dialog tof_input_1;
	protected TOF_Edit_Dialog_2 tof_input_2;
	protected List_Dialog list_dialog;

	protected Instr_Param_Dialog instr_params;

	protected String[] list_box_text;
	protected boolean FirstTOFViewOpen;
	protected boolean InstParamsSet;

	protected MainFrame mainWindow;

	protected boolean first_view;
	protected boolean IsNewFile;

	
	public sbapp() {
		IsNewFile = true;
	}

	public void InstrumentalParamsAreSet(boolean t_or_f) {
		InstParamsSet = t_or_f;
	}

	public int GetViewNumber() {
		return ViewNumber;
	}

	public TOFPOEDocument GetSessionDocument() {
		return session_document;
	}

	public void LoadTOFData(Scanner is, TOF_data open_TOF) {
		int i = 0, new_array_counter = 1;
		float dwell_time, input_value1, input_value2;
		float[] tof_pointer = new float[1000];
		float[] time_pointer = new float[1000];
		float[] reserve_pointer1, reserve_pointer2, placeholder1, placeholder2;
		boolean first = true;

		if (open_TOF.GetFormat() != 1) {
			while (is.hasNext()) {
				input_value1 = is.nextFloat();
				if (first) {
					i = 0;
					first = false;
				} else
					i++;

				if (i >= (new_array_counter * 1000)) {
					new_array_counter++;
					reserve_pointer1 = new float[new_array_counter * 1000];
					reserve_pointer2 = new float[new_array_counter * 1000];
					for (int j = 0; j < i; j++) {
						reserve_pointer1[j] = tof_pointer[j];
						reserve_pointer2[j] = time_pointer[j];
					}
					placeholder1 = tof_pointer;
					tof_pointer = reserve_pointer1;

					placeholder2 = time_pointer;
					time_pointer = reserve_pointer2;
				}
				tof_pointer[i] = input_value1;
				time_pointer[i] = 0;
			}
		} else {
			first = true;
			for (i = 0; i < 5; i++) {
				is.nextLine();
			}
			while (is.hasNext()) {
				input_value1 = is.nextFloat();
				if (first) {
					i = 0;
					first = false;
				} else
					i++;

				if (i >= (new_array_counter * 1000)) {
					new_array_counter++;
					reserve_pointer1 = new float[new_array_counter * 1000];
					reserve_pointer2 = new float[new_array_counter * 1000];
					for (int j = 0; j < i; j++) {
						reserve_pointer1[j] = tof_pointer[j];
						reserve_pointer2[j] = time_pointer[j];
					}
					placeholder1 = tof_pointer;
					tof_pointer = reserve_pointer1;

					placeholder2 = time_pointer;
					time_pointer = reserve_pointer2;
				}
				tof_pointer[i] = input_value1;
				input_value2 = is.nextFloat();
				is.nextLine();
				time_pointer[i] = input_value2;
			}
			dwell_time = time_pointer[1] - time_pointer[0]; // Determine
															// experimental
															// dwell time
			open_TOF.SetDwell(dwell_time);
			open_TOF.SetDwellScale(scale.μs);
		}
		open_TOF.SetTotChannels(i + 1);
		open_TOF.SetChannelCountsPointer(tof_pointer);
		open_TOF.SetTimePointer(time_pointer);
	}

	public void InputTOFData(TOF_data time_of_flight, TOF_Input_1_Dialog tof_input_dialog,
			File FileData, boolean FirstTime){
		scale temporary_scale;
		float dwell, offset;

		String title;
		String lab_angle;
		String detected_me;
		String polarization_angle;
		String degree_polarization;
		String dwell_time;
		String dwell_scale;
		String trigger_offset;
		String offset_scale;

		if(FirstTime)
			title = FileData.getName();
		else
			title = time_of_flight.GetTitle();

		// Get default (i.e. current) values from TOF
		lab_angle = "" + time_of_flight.GetLabAngle(); // 5 is # of sig digits in float
		detected_me = "" + time_of_flight.GetIon_m_e();
		polarization_angle = "" + time_of_flight.GetPolarizationAngle();
		degree_polarization = "" + time_of_flight.GetDepolarization();
		dwell = time_of_flight.GetDwell();
		offset = time_of_flight.GetOffset();

		dwell_time = "" + time_of_flight.GetDwell();
		trigger_offset = "" + time_of_flight.GetOffset();

		if(dwell < 10)
		{
			temporary_scale = scale.μs;
		}
		else
		{
			temporary_scale = scale.ns;
		}
		tof_input_dialog.SetDwellScale(temporary_scale);

		if((offset < 10) && (offset != 0.0))
		{
			temporary_scale = scale.μs;
		}
		else
		{
			temporary_scale = scale.ns;
		}

		// Set objects in dialog box to default values
		tof_input_dialog.SetTOFTitle(title);
		tof_input_dialog.SetLabAngle(lab_angle);
		tof_input_dialog.SetDetectedme(detected_me);
		tof_input_dialog.SetPolarizationAngle(polarization_angle);
		tof_input_dialog.SetDegreePolarization(degree_polarization);
		tof_input_dialog.SetDwellTime(dwell_time);
		tof_input_dialog.SetTriggerOffset(trigger_offset);
		tof_input_dialog.SetOffsetScale(temporary_scale);
		tof_input_dialog.SetLaserPolarized(time_of_flight.GetLaserPolarized());
		tof_input_dialog.execute();
	}

	public int InputTOFData(TOF_data tof, TOF_Edit_Dialog_2 tof_input_dialog,
			File f, boolean FirstTime, TOFPOEDocument document) {
		int return_value;
		String tof_title;
		String calc_title;

		String[] poe_title_list;
		POE_calc_data[] poe_calculation_data;
		POE_data poe;
		int i, total_num_poes, temp_num_tof_points;

		float temp_alpha = 0, temp_spd_rat = 0, temp_flt_len = 0, temp_bm_ang = 0, temp_det_ang = 0;
		float temp_ion_len = 0, temp_min_lab_vel = 0, temp_max_lab_vel = 0, temp_polar_ang = 0, temp_depol = 0;

		float[] temp_time_pointer;
		int temp_bm_vel_segs = 0, temp_ion_segs = 0, temp_lab_vel_segs = 0, temp_bm_ang_segs = 0, temp_det_ang_segs = 0;
		boolean temp_ion_gauss = false, temp_num_dens_calc = false, temp_polarized = false;

		if (FirstTime)
			tof_title = f.getName();
		else
			tof_title = tof.GetTitle();

		// Get current values from TOF
		temp_time_pointer = tof.RealTimePointer();
		temp_num_tof_points = tof.GetTotChannels();
		tof.GetBeamParams(temp_alpha, temp_spd_rat, temp_bm_vel_segs);
		tof.GetIonizationParams(temp_ion_gauss, temp_ion_segs);
		tof.GetInstrumentParams(temp_num_dens_calc, temp_flt_len, temp_bm_ang,
				temp_det_ang, temp_ion_len);
		tof.GetLabVelParams(temp_min_lab_vel, temp_max_lab_vel,
				temp_lab_vel_segs);
		tof.GetPolarizationParams(temp_polarized, temp_polar_ang, temp_depol);
		tof.GetAngleSegs(temp_bm_ang_segs, temp_det_ang_segs);

		calc_title = tof.GetCalcTitle();

		// Pass this information on to the dialog box
		poe_calculation_data = tof.GetPOECalcData();
		total_num_poes = document.GetNumPOEs();

		if (tof.GetAssociatedCalc() == -1) // i.e. if TOF was detached
		{
			poe_title_list = tof.GetCurrentPOETitles(total_num_poes);
		} else {
			poe_title_list = new String[total_num_poes];
			for (i = 0; i < total_num_poes; i++) {
				if (poe_calculation_data[i].is_included) {
					poe = document.GetPOEData(i);
					poe_title_list[i] = poe.GetTitle();
				} else {
					poe_title_list[i] = null;
				}
			}
		}
		tof_input_dialog.SetPOETitleArray(total_num_poes, poe_title_list);

		tof_input_dialog.SetTOFTitle(tof_title);
		tof_input_dialog.SetCalcTitle(calc_title);
		tof_input_dialog.SetPOECalcData(poe_calculation_data);
		tof_input_dialog.SetInstParams(temp_num_dens_calc, temp_flt_len,
				temp_bm_ang, temp_det_ang, temp_ion_len);
		tof_input_dialog.SetMainCalcParams(tof.GetLabAngle(), tof.GetIon_m_e(),
				temp_bm_ang_segs, temp_det_ang_segs);
		tof_input_dialog.SetIonInfo(temp_ion_gauss, temp_ion_segs);
		tof_input_dialog.SetPolarInfo(temp_polarized, temp_polar_ang,
				temp_depol);
		tof_input_dialog
				.SetBmParams(temp_alpha, temp_spd_rat, temp_bm_vel_segs);
		tof_input_dialog.SetLabVelParams(temp_min_lab_vel, temp_max_lab_vel,
				temp_lab_vel_segs);
		tof_input_dialog
				.SetTimeInfo(temp_time_pointer[0],
						temp_time_pointer[temp_num_tof_points - 1],
						temp_num_tof_points);
		if (tof.GetAssociatedCalc() == -1)
			tof_input_dialog.SetDetached(true);
		else
			tof_input_dialog.SetDetached(false);

		tof_input_dialog.Execute();
		return_value = 0;
		if (tof.GetAssociatedCalc() != -1) // i.e. if TOF was detached
		{
			// delete(poe_title_list);
		}

		return (return_value);
	}

	public String ScaleToString(scale temp_scale){
		 switch(temp_scale)
		   {
		   	case ps:
		      	return "pico";
		      case ns:
		      	return "nano";
		      case μs:
		      	return "micro";
		      default:
		      	return "milli";
		   }
	}

	public scale StringToScale(String string){
		if(string.equals("pico"))
			return scale.ps;
		if(string.equals("nano"))
			return scale.ns;
		if(string.equals("micro"))
			return scale.μs;

		// Default case:  when string is "milli"
		return scale.ms;
	}

	public void SetTOFData(TOF_data time_of_flight, TOF_Input_1_Dialog tof_input_dialog,
			TOFPOEDocument document){
		scale temporary_scale;


		time_of_flight.SetOffset(Float.parseFloat(tof_input_dialog.GetTriggerOffset()));
		// Add new data to TOF file
		time_of_flight.SetTitle(tof_input_dialog.GetTOFTitle());


		time_of_flight.SetIon_m_e(Float.parseFloat(tof_input_dialog.GetDetectedme()));
		time_of_flight.SetLabAngle(Float.parseFloat(tof_input_dialog.GetLabAngle()));
		if(tof_input_dialog.IsLaserPolarized())
		{
			time_of_flight.SetLaserPolarized(true);
			time_of_flight.SetPolarizationAngle(Float.parseFloat(tof_input_dialog.GetPolarizationAngle()));
			time_of_flight.SetDepolarization(Float.parseFloat(tof_input_dialog.GetDegreePolarization()));
		}
		else
		{
			time_of_flight.SetLaserPolarized(false);
		}

		if(tof_input_dialog.dwell_time_edit.isEnabled()){
			temporary_scale = tof_input_dialog.GetDwellScale();
			time_of_flight.SetDwellScale(temporary_scale);
			time_of_flight.SetDwell(Float.parseFloat(tof_input_dialog.GetDwellTime()));
		}
		
		temporary_scale = tof_input_dialog.GetOffsetScale();
		time_of_flight.SetOffsetScale(temporary_scale);
		time_of_flight.SetIonFlightTime(document.GetIonFlightConst());	
	}
	
	public void SetTOFData(TOF_data tof, TOF_Edit_Dialog_2 tof_input_dialog,
			TOFPOEDocument document){
		// Add new data to TOF file
		   tof.SetTitle(tof_input_dialog.GetTOFTitle());
	}
	
	public float RandomNumber(long idum){
		return (float) Math.random();
	}



	protected void InitMainWindow(){
		first_view = true;
		mainWindow = new MainFrame("Single Beam Convolution Program", this);
		mainWindow.Execute();
		System.out.println("X1: " + mainWindow.xposLabel);


		InstParamsSet = false;

		ViewNumber = 0; // Initializes the # of the view
		CalcNumber = 0; // Initializes the # of the forward convolution calculation
		AngNumber = 0; // Initializes the # of the angular distribution

		session_document = new TOFPOEDocument();
		session_document.SetXPosGadget(mainWindow.xposLabel);
		System.out.println("x2: " + session_document.GetXPosGadget());
		System.out.println("document: " + session_document);
		session_document.SetYPosGadget(mainWindow.yPosLabel);
		//session_document.SetSumSquareGadget(SumSquare_text_gadget);
		list_dialog = new List_Dialog(this.mainWindow,new String[]{},1);
		instr_params = new Instr_Param_Dialog(mainWindow);

		//UFCFileData = new TOpenSaveDialog.TData(OFN_HIDEREADONLY|OFN_FILEMUSTEXIST,
		//		"Uni. For. Con. Data (*.UFC)|*.ufc|", 0, "",
		//		"UFC");
	}

	protected void SetupInstrParamDialog(){
		String temporary_string;

		temporary_string = "" + session_document.GetIonFlightConst();
		instr_params.SetIonFlightConst(temporary_string);

		temporary_string = "" + session_document.GetIonizerLen();
		instr_params.SetIonizerLen(temporary_string);

		temporary_string = "" + session_document.GetFlightLen();
		instr_params.SetFlightLen(temporary_string);

		temporary_string = "" + session_document.GetBeamAng();
		instr_params.SetBeamAng(temporary_string);

		temporary_string = "" + session_document.GetDetectAng();
		instr_params.SetDetectAng(temporary_string);

		instr_params.SetTypeOfCalculation(session_document.GetDetectScheme());
		
		instr_params.Execute();

	}
	
	protected void StoreInstrParams(){
		session_document.SetIonFlightConst(Float.parseFloat(instr_params.GetIonFlightConst()));
		session_document.SetIonizerLen(Float.parseFloat(instr_params.GetIonizerLen()));
		session_document.SetFlightLen(Float.parseFloat(instr_params.GetFlightLen()));
		session_document.SetBeamAng(Float.parseFloat(instr_params.GetBeamAng()));
		session_document.SetDetectAng(Float.parseFloat(instr_params.GetDetectAng()));
		session_document.SetIonFlightTimes();
		session_document.SetDetectScheme(instr_params.GetCalcType());
		return;
	}

	protected int GetInversionData(TOF_data time_of_flight){
		String mass_1;
		String mass_2;
		String beta;
		String beam_velocity;

		TOF_Invert_Dialog tof_invert = new TOF_Invert_Dialog(mainWindow);

		mass_1 = "" + time_of_flight.GetMass1();
		mass_2 = "" + time_of_flight.GetMass2();
		beta = "" + time_of_flight.GetBetaForInversion();
		beam_velocity = "" + time_of_flight.GetInversionBeamVel();

		tof_invert.SetMass1(mass_1);
		tof_invert.SetMass2(mass_2);
		tof_invert.SetBeta(beta);
		tof_invert.SetBeamVelocity(beam_velocity);

		tof_invert.Execute();
		if(!tof_invert.ID){
			throw new Error("oh nos");
		}

		
			time_of_flight.SetMass1(Float.parseFloat(tof_invert.GetMass1()));
			time_of_flight.SetMass2(Float.parseFloat(tof_invert.GetMass2()));
			time_of_flight.SetBetaForInversion(Float.parseFloat(tof_invert.GetBeta()));
			time_of_flight.SetInversionBeamVel(Float.parseFloat(tof_invert.GetBeamVelocity()));
		
		return 0;
	}

	protected int GetPOEInfo(TOF_data tof, POE_data energy_distribution){
		int return_value = 0;
		String title;
		String min_energy;
		String max_energy;
		String num_poe_points;
		String temp_energy_units = "";
		
		float mass_1, mass_2, beam_velocity, lab_angle;
		float maximum_time, minimum_time, distance, lab_velocity;
		float mass_ratio_factor, min_possible_energy;
		float sin_lab_angle, cos_lab_angle;
		float u_squared, energy_from_time;

		float lab_vel_this_min_u;
		float convert_kcal_to_units = session_document.Convert_kcal_TO_session_units(1.0f);
		if(tof != null)
		{
			maximum_time = tof.GetMaximumTime();
			minimum_time = tof.GetMinimumTime();
			maximum_time /= 1.0e6; // Conversion to time in seconds
			minimum_time /= 1.0e6; // Conversion to time in seconds
			mass_1 = tof.GetMass1();
			mass_2 = tof.GetMass2();
			beam_velocity = tof.GetInversionBeamVel();
			lab_angle = tof.GetLabAngle();
			distance = session_document.GetFlightLen();

			distance /= 100.0;  // Convert to m/s

			mass_ratio_factor = (mass_1/mass_2) * (mass_1 + mass_2);
			mass_ratio_factor /= 4.184e6;   // For energy in kcal/mol
			sin_lab_angle = (float) Math.sin(lab_angle/ 180.0f * Math.PI);
			cos_lab_angle = (float) Math.cos(lab_angle/180.0f * Math.PI);

			if(Math.abs(lab_angle) <= 90)
			{
				min_possible_energy = (float) (0.5 * mass_ratio_factor * beam_velocity * beam_velocity * sin_lab_angle * sin_lab_angle);
			}
			else
			{
				min_possible_energy = (float) (0.5 * mass_ratio_factor * beam_velocity * beam_velocity);
			}

			lab_vel_this_min_u = beam_velocity * cos_lab_angle;

			lab_velocity = distance / maximum_time;
			if(lab_velocity > lab_vel_this_min_u)
			{
				u_squared = (lab_velocity * lab_velocity) +  (beam_velocity * beam_velocity)
						- (2 * lab_velocity * beam_velocity * cos_lab_angle);
				energy_from_time = (float) (0.5 * mass_ratio_factor * u_squared);
				min_possible_energy = Math.max(min_possible_energy, energy_from_time);
			}
			lab_velocity = distance / minimum_time;
			if((lab_velocity < lab_vel_this_min_u) && (lab_velocity > 0))
			{
				u_squared = (lab_velocity * lab_velocity) +  (beam_velocity * beam_velocity)
						- (2 * lab_velocity * beam_velocity * cos_lab_angle);
				energy_from_time = (float) (0.5 * mass_ratio_factor * u_squared);
				min_possible_energy = Math.max(min_possible_energy, energy_from_time);
			}
		}
		else
		{
			min_possible_energy = 0.0f;
		}

		POE_Info_Dialog_1 poe_info_1 = new POE_Info_Dialog_1(mainWindow);

		temp_energy_units = session_document.GetEnergyUnits(temp_energy_units);

		title = energy_distribution.GetTitle();
		min_energy = "" + (energy_distribution.GetMinimumEnergy() * convert_kcal_to_units);
		max_energy = "" + (energy_distribution.GetMaximumEnergy() * convert_kcal_to_units);
		num_poe_points = "" +  energy_distribution.GetTotNumPoints();

		poe_info_1.SetEnergyUnits(temp_energy_units);
		if(tof == null)
		{
			poe_info_1.SetClearEnergyLines();
		}
		poe_info_1.SetMinPossibleEnergy(min_possible_energy * convert_kcal_to_units);
		poe_info_1.SetTitle(title);
		poe_info_1.SetMinEnergy(min_energy);
		poe_info_1.SetMaxEnergy(max_energy);
		poe_info_1.SetNumPoints(num_poe_points);

		poe_info_1.Execute();
		// check
		if (poe_info_1.ID == false) {
			return -1;
		}

		energy_distribution.SetTitle(poe_info_1.GetTitle());
		energy_distribution.SetMinimumEnergy((Float.parseFloat(poe_info_1
				.GetMinEnergy()) / convert_kcal_to_units));
		energy_distribution.SetMaximumEnergy((Float.parseFloat(poe_info_1
				.GetMaxEnergy()) / convert_kcal_to_units));
		energy_distribution.SetTotNumPoints(Integer.parseInt(poe_info_1
				.GetNumPoints()));

		return return_value;
	}
	
	protected void CrappyInvertTOFs(TOF_data[] tofs, POE_data poe){
		for(int loopMe = 0; loopMe< poe.num_points; loopMe++){
			
		}
	}


	protected void InvertTOFs(TOF_data[] tofs, POE_data poe){
		TOF_data tof = tofs[0];
		boolean is_number_density_calc, laser_is_polarized;
		float[] energy_amplitudes;
		float[] energy_values;
		float[] tof_counts_pointer, tof_time_pointer;
		float dwell_time;
		int i, num_poe_points, num_tof_points;
		float time_point, min_energy, max_energy, energy_increment, distance;
		float min_time, max_time, polar_major, polar_minor, deg_polarization;
		float lab_angle, mass1, mass2, mass_ratio, w_theta_major, w_theta_minor;
		float polarization_angle, beta, beam_velocity, num_counts, denominator;
		float energy, u_squared, energy_from_time, lab_vel_this_min_u;

		float cos_pol_ang, sin_pol_ang;  // Angle between polar. vector and detector axis (clockw = pos)
		float cos_lab_ang, sin_lab_ang;  // Angle between lab angle and detector axis (clockw = pos)
		float cos_u_det_ang, sin_u_det_ang;  // Angle b/w recoiling frag and detector in cm frame (clockw = neg)

		float cos_theta_major, cos_theta_minor, min_possible_energy;
		float square_bm_vel_times_sin_lab_square, bm_vel_times_cos_lab_ang;
		float fast_time, slow_time, square_root_value;
		float low_t_count, high_t_count, lab_velocity, u_value, temp_amplitude;

		is_number_density_calc = session_document.GetDetectScheme();
		laser_is_polarized = tof.GetLaserPolarized();
		min_energy = poe.GetMinimumEnergy();
		max_energy = poe.GetMaximumEnergy();
		num_poe_points = poe.GetTotNumPoints();
		tof_counts_pointer = tof.ChannelCountsPointer();
		tof_time_pointer = tof.RealTimePointer();
		num_tof_points = tof.GetTotChannels();
		min_time = tof_time_pointer[0];
		System.out.println("Min time: " + min_time);
		max_time = tof_time_pointer[num_tof_points - 1];
		dwell_time = tof_time_pointer[1] - min_time;

		min_time /= 1.0e6;    // Convert to time in seconds
		max_time /= 1.0e6;    // Convert to time in seconds
		dwell_time /= 1.0e6;  // Convert to time in seconds

		distance = session_document.GetFlightLen();

		distance /= 100.0;  // Convert to m from cm

		if(!laser_is_polarized)
		{
			polarization_angle = 0.0f;
			deg_polarization = 1.0f;
		}

		else
		{
			polarization_angle = tof.GetPolarizationAngle();
			deg_polarization = tof.GetDepolarization();
		}
		cos_pol_ang = (float) Math.cos(Math.PI * (polarization_angle)/180.0);
		sin_pol_ang = (float) Math.sin(Math.PI * (polarization_angle) / 180.0);

		// Scale the weakest and strongest polarizations so they sum to 1:
		polar_major = 1 / (1 + deg_polarization);
		polar_minor = 1 - polar_major;

		lab_angle = tof.GetLabAngle();
		mass1 = tof.GetMass1();
		mass2 = tof.GetMass2();
		mass_ratio = (mass1 / mass2) * (mass1 + mass2); // Used in converting to energy space
		mass_ratio /= 4.184e6;  // Conversion factor so units are kcal*s^2 / m^2 * mol

		beta = tof.GetBetaForInversion();
		beam_velocity = tof.GetInversionBeamVel();   // In m/s

		cos_lab_ang = (float) Math.cos(Math.PI / 180.0 *(lab_angle));
		sin_lab_ang = (float) Math.sin(Math.PI / 180.0 *(lab_angle));

		square_bm_vel_times_sin_lab_square = beam_velocity * beam_velocity  // In m^2 / s^2
				* sin_lab_ang * sin_lab_ang;
		bm_vel_times_cos_lab_ang = beam_velocity * cos_lab_ang;   // In m/s

		energy_increment = (max_energy - min_energy) / (num_poe_points - 1);

		energy_amplitudes = new float[num_poe_points];
		energy_values = new float[num_poe_points];

		if(Math.abs(lab_angle) <= 90)
		{
			min_possible_energy = (float) (0.5 * mass_ratio * square_bm_vel_times_sin_lab_square);
		}
		else
		{
			min_possible_energy = (float) (0.5 * mass_ratio * beam_velocity * beam_velocity);
		}

		lab_vel_this_min_u = beam_velocity * cos_lab_ang;

		lab_velocity = distance / max_time;
		if(lab_velocity > lab_vel_this_min_u)
		{
			u_squared = (lab_velocity * lab_velocity) +  (beam_velocity * beam_velocity)
					- (2 * lab_velocity * beam_velocity * cos_lab_ang);
			energy_from_time = (float) (0.5 * mass_ratio * u_squared);
			min_possible_energy = Math.max(min_possible_energy, energy_from_time);
		}
		lab_velocity = distance / min_time;
		if((lab_velocity < lab_vel_this_min_u) && (lab_velocity > 0))
		{
			u_squared = (lab_velocity * lab_velocity) +  (beam_velocity * beam_velocity)
					- (2 * lab_velocity * beam_velocity * cos_lab_ang);
			energy_from_time = (float) (0.5 * mass_ratio * u_squared);
			min_possible_energy = Math.max(min_possible_energy, energy_from_time);
		}

		for(i = 0; i < num_poe_points; i++)
		{
			energy = min_energy + (i * energy_increment);
			energy_values[i] = energy;
			System.out.println("At energy value " + energy);
			if(energy < min_possible_energy)
			{
				energy_amplitudes[i] = 0.0f;
			}
			else
			{
				square_root_value = (2 * energy / mass_ratio) - square_bm_vel_times_sin_lab_square;
				u_value = (float) Math.sqrt(2 * energy / mass_ratio);
				sin_u_det_ang = (beam_velocity * sin_lab_ang) / u_value;

				if(square_root_value < 0.0)    // To ensure no uncalculable values due to round off error
				{
					energy_amplitudes[i] = 0.0f;
				}
				else
				{
					// One Newton circle can result in two possible times--(one may be negative!)
					fast_time = (float) (distance / (bm_vel_times_cos_lab_ang + Math.sqrt(square_root_value)));
					slow_time = (float) (distance / (bm_vel_times_cos_lab_ang - Math.sqrt(square_root_value)));
					System.out.println("\tcorresponding to times: " + fast_time + "\tand\t" + slow_time);
					if(fast_time >= min_time)  // If TOF begins at some time greater than zero
					{
						// Interpolate a number of counts for this time from the two surrounding time points
						// in the TOF spectrum
						time_point = (fast_time - min_time) / dwell_time;
						low_t_count = tof_counts_pointer[(int) time_point];
						high_t_count = tof_counts_pointer[(int) time_point + 1];
						num_counts = low_t_count + (time_point - (int) time_point) * (high_t_count - low_t_count);

						// Find trigonometric values; for fast time, u-vector -- detector angle
						// will always be less that 90 degrees; for slow time, it will be greater than 90
						// So, cos of the angle will be positive for the fast time
						cos_u_det_ang = (float) Math.sqrt(1 - sin_u_det_ang * sin_u_det_ang);
						cos_theta_major = (cos_pol_ang * cos_u_det_ang - sin_pol_ang * sin_u_det_ang);
						cos_theta_minor = (sin_pol_ang * cos_u_det_ang + cos_pol_ang * sin_u_det_ang);

						w_theta_major = 1 + beta; //* P2(cos_theta_major);  // Not normalized; P(E) is eventually
						w_theta_minor = 1 + beta; //* P2(cos_theta_minor);  // normalized anyway.

						denominator = polar_major * w_theta_major +  polar_minor * w_theta_minor;

						// Find amplitude sans constant scaling factors such as mass_ratio and distance
						if(is_number_density_calc == true)
						{
							temp_amplitude = (float) ((Math.pow(fast_time, 3) * u_value * num_counts) / denominator);
						}
						else
						{
							temp_amplitude = (float) ((Math.pow(fast_time, 4) * u_value * num_counts) / denominator);
						}
						System.out.println("Estimating at t= " + time_point + "s. Energy is " + temp_amplitude + "kcal/mol");
					}
					else
					{
						temp_amplitude = 0.0f;
					}
					// If slow time is an experimentally useful (and possible) value:
					if((slow_time >= min_time) && (slow_time >= 0.0) && (slow_time <= max_time))
					{
						// Interpolate a number of counts for this time from the two surrounding time points
						time_point = (slow_time - min_time) / dwell_time;
						low_t_count = tof_counts_pointer[(int) time_point];
						high_t_count = tof_counts_pointer[(int) time_point + 1];
						num_counts = low_t_count + (time_point - (int) time_point) * (high_t_count - low_t_count);

						// Find trigonometric values; for fast time, u-vector -- detector angle
						// will always be less that 90 degrees; for slow time, it will be greater than 90
						// So, cos of the angle will be negative for the fast time
						cos_u_det_ang = (float) - Math.sqrt(1 - sin_u_det_ang * sin_u_det_ang);
						cos_theta_major = (cos_pol_ang * cos_u_det_ang - sin_pol_ang * sin_u_det_ang);
						cos_theta_minor = (sin_pol_ang * cos_u_det_ang + cos_pol_ang * sin_u_det_ang);

						w_theta_major = 1 + beta;// * P2(cos_theta_major);  // Not normalized; P(E) is eventually
						w_theta_minor = 1 + beta;// * P2(cos_theta_minor);  // normalized anyway.

						denominator = polar_major * w_theta_major +  polar_minor * w_theta_minor;

						if(is_number_density_calc == true)
						{
							temp_amplitude += (Math.pow(fast_time, 3) * u_value * num_counts) / denominator;
						}
						else
						{
							temp_amplitude += (Math.pow(fast_time, 4) * u_value * num_counts) / denominator;
						}
						temp_amplitude /= 2.0;  // Average over value for fast and slow time
					}
					energy_amplitudes[i] = temp_amplitude;
				}
			}
		}
		poe.SetPOEPointer(energy_amplitudes);
		poe.SetEnergyPointer(energy_values);
		poe.NormalizePOE(null);
	}

	protected int[] FillTOFListBox(int show_type, int count){
		// show_type = 0 ==> show all tofs
		// show_type = 1 ==> show only unused tofs
		// show_type = 2 ==> show only altered tofs
		// show_type = 3 ==> show only real tofs
		TOF_data time_of_flight;
		int[] index_of_tofs;

		int total_number_of_tofs = session_document.GetNumTOFs();
		int tof_number;
		int i;

		count = 0;
		if (show_type == 0) {
			index_of_tofs = new int[total_number_of_tofs];
			list_box_text = new String[total_number_of_tofs];
		} else {
			tof_number = NumberTOFs(show_type);
			index_of_tofs = new int[tof_number];
			list_box_text = new String[tof_number];
		}

		for (i = 0; i < total_number_of_tofs; i++) {
			time_of_flight = session_document.GetTOFData(i);

			if (show_type == 0) {
				list_box_text[count] = time_of_flight.GetTitle();
				index_of_tofs[count] = i;
				(count)++;
			} else {
				if (show_type == 1) {
					if (time_of_flight.GetNumAssociatedViews() == 0) {
						list_box_text[count] = time_of_flight.GetTitle();
						index_of_tofs[count] = i;
						(count)++;
					}
				} else {
					if (((show_type == 2) && (time_of_flight.GetIsAltered()))
							|| ((show_type == 3) && (time_of_flight
									.GetIsRealTOF()))) {
						list_box_text[count] = time_of_flight.GetTitle();
						index_of_tofs[count] = i;
						(count)++;
					}
				}
			}
		}
		return index_of_tofs;
	}
	protected int[] FillPOEListBox(boolean show_all_poes){
		POE_data poe;
		int[] index_of_poes;
		Calc_data calc;
		POE_calc_data[] this_poe_calc_data;
		int count = 0;
		int total_number_of_poes = session_document.GetNumPOEs();
		int number_of_calcs = session_document.GetNumCalcs();
		int poe_number = 0;
		int i, j;
		boolean add_one;

		// If want to list all P(E)'s which are currently contained in the document
		if(show_all_poes)
		{
			index_of_poes = new int[total_number_of_poes];
			list_box_text = new String[total_number_of_poes];
		}

		// If only want to show those P(E)'s which are not currently in use in a POEView or Calc_data
		else
		{
			poe_number = NumberUnusedPOEs();
			index_of_poes = new int[poe_number];
			list_box_text = new String[poe_number];
		}


		for(i = 0; i < total_number_of_poes; i++)
		{
			poe = session_document.GetPOEData(i);

			if(show_all_poes)
			{
				list_box_text[count] = poe.GetTitle();
				index_of_poes[count] = i;
				count++;
			}
			else
			{
				add_one = false;
				if(poe.GetNumAssociatedViews() == 0)
				{
					add_one = true;
					for(j = 0; j < number_of_calcs; j++)
					{
						calc = session_document.GetCalcData(j);
						this_poe_calc_data = calc.GetPOECalcData();
						if(this_poe_calc_data[i].is_included == true)
						{
							add_one = false;
						}
					}
				}
				if(add_one)
				{
					list_box_text[count] = poe.GetTitle();
					index_of_poes[count] = i;
					count++;
				}
			}
		}
		return index_of_poes;
	}
	
	protected int[] FillAngListBox(boolean show_all_ang_dists){
		Ang_data ang_dist;
		int[] index_of_angs;
		int i, total_number_of_angs, num_unused_angs, count;

		total_number_of_angs = session_document.GetNumAngs();
		count = 0;
		if (show_all_ang_dists) {
			index_of_angs = new int[total_number_of_angs];
			list_box_text = new String[total_number_of_angs];
		} else {
			num_unused_angs = NumberUnusedAngs();
			index_of_angs = new int[num_unused_angs];
			list_box_text = new String[num_unused_angs];
		}

		for (i = 0; i < total_number_of_angs; i++) {
			ang_dist = session_document.GetAngData(i);

			if (show_all_ang_dists) {
				list_box_text[count] = ang_dist.GetTitle();
				index_of_angs[count] = i;
				count++;
			} else {
				if (ang_dist.GetNumAssociatedViews() == 0) {
					list_box_text[count] = ang_dist.GetTitle();
					index_of_angs[count] = i;
					count++;
				}
			} // End of else (i.e. if only want ang_dists in this view)
		} // End of iterating through all ang_dists
			// list_dialog.SetListBoxList(list_box_text);
		return index_of_angs;
	}
	protected int[] FillCalcListBox(boolean show_all_calcs){
		Calc_data calculation;
		int[] index_of_calcs = null;
		int count = 0;
		int total_number_of_calcs = session_document.GetNumCalcs();
		int i;

		if(show_all_calcs)
		{
			index_of_calcs = new int[total_number_of_calcs];
			list_box_text = new String[total_number_of_calcs];
		}else{
			return null;
		}

		for(i = 0; i < total_number_of_calcs; i++)
		{
			calculation = session_document.GetCalcData(i);

			if(show_all_calcs)
			{
				list_box_text[count] = calculation.GetTitle();
				index_of_calcs[count] = i;
				count++;
			}

		}
		return index_of_calcs;
	}

	protected int NumberTOFs(int tof_types){
		// tof_types = 1 ==> return # of unused tofs
		// tof_types = 2 ==> return # of altered tofs
		// tof_types = 3 ==> return # of real tofs
		TOF_data time_of_flight;
		Ang_data angular_dist;
		int[] ang_tof_nums;
		boolean IsBeingUsed;
		int count = 0;
		int number_of_tofs = session_document.GetNumTOFs();
		int i, j, k;
		int this_tof_num;

		for(i = 0; i < number_of_tofs; i++)
		{

			time_of_flight = session_document.GetTOFData(i);
			this_tof_num = time_of_flight.GetTOFNum();
			if(tof_types == 1)
			{
				if(time_of_flight.GetNumAssociatedViews() == 0)
				{
					IsBeingUsed = false;
					// Check to make sure TOF is not part of an angular distribution
					for(j = 0; j < session_document.GetNumAngs(); j++)
					{
						angular_dist = session_document.GetAngData(j);
						ang_tof_nums = angular_dist.GetIncludedTOFNums();
						for(k = 0; k < angular_dist.GetNumTOFs(); k++)
						{
							if(this_tof_num == ang_tof_nums[k])
							{
								IsBeingUsed = true;
							}
						}
					}
					if(IsBeingUsed == false)
					{
						count++;
					}
				}
			}

			if(tof_types == 2)
			{
				if(time_of_flight.GetIsAltered())
					count++;
			}

			if(tof_types == 3)
			{
				if(time_of_flight.GetIsRealTOF())
					count++;
			}
		}  
		return count;
	}

	protected int NumberUnusedPOEs(){
		POE_data poe;
		Calc_data calc;
		POE_calc_data[] this_poe_calc_data;
		int count = 0;
		int number_of_poes = session_document.GetNumPOEs();
		int number_of_calcs = session_document.GetNumCalcs();
		int i, j;
		boolean add_one;

		for(i = 0; i < number_of_poes; i++)
		{
			add_one = false;
			poe = session_document.GetPOEData(i);
			if(poe.GetNumAssociatedViews() == 0)
			{
				add_one = true;
				// Check to make sure this P(E) isn't in use by a calculation
				for(j = 0; j < number_of_calcs; j++)
				{
					calc = session_document.GetCalcData(j);
					this_poe_calc_data = calc.GetPOECalcData();
					if(this_poe_calc_data[i].is_included == true)
					{
						add_one = false;
					}
				}
			}
			if(add_one == true)
				count++;
		}
		return count;
	}
	
	protected int NumberUnusedAngs(){
		int i, number_ang_dists, count;
		   Ang_data ang_dist;

		   count = 0;
		   number_ang_dists = session_document.GetNumAngs();
		   for(i = 0; i < number_ang_dists; i++)
		   {
		   	ang_dist = session_document.GetAngData(i);
		      if(ang_dist.GetNumAssociatedViews() == 0)
		      {
		      	count++;
		      }
		   }
		   return count;
	}
	
	protected boolean CompareTOFs(TOF_data tof_1, TOF_data tof_2){
		String temp_value_1;
		String temp_value_2;
		int tof1_num_points, tof2_num_points, dwell_1, dwell_2, offset_1, offset_2;
		float ion_m_e_1, ion_m_e_2;
		boolean same = true;

		tof1_num_points = tof_1.GetTotChannels();
		tof2_num_points = tof_2.GetTotChannels();
		dwell_1 = (int) tof_1.GetDwell();
		dwell_2 = (int) tof_2.GetDwell();
		offset_1 = (int) tof_1.GetOffset();
		offset_2 = (int) tof_2.GetOffset();
		ion_m_e_1 = tof_1.GetIon_m_e();
		ion_m_e_2 = tof_2.GetIon_m_e();

		// Need to compare TOFs to be sure they can be added
		if((tof_1.GetDwellScale()) != (tof_2.GetDwellScale()))
		{
			same = false;
		}
		if((tof_1.GetOffsetScale()) != (tof_2.GetOffsetScale()))
		{
			same = false;
		}
		if(tof1_num_points != tof2_num_points)
		{
			same = false;
		}

		temp_value_1 = "" + dwell_1;
		temp_value_2 = "" + dwell_2;
		if(!temp_value_1.equals(temp_value_2))
		{
			same = false;
		}

		temp_value_1 = "" + offset_1;
		temp_value_2 = "" + offset_2;
		if(!temp_value_1.equals( temp_value_2) )
		{
			same = false;
		}

		temp_value_1 = "" +  ion_m_e_1;
		temp_value_2 = "" + ion_m_e_2;
		if(!temp_value_1.equals(temp_value_2))
		{
			same = false;
		}
		return same;
	}
	
	protected TOF_data TOFMath(TOF_data tof_1, TOF_data tof_2, boolean should_add){
		TOF_data new_tof = new TOF_data();
		int tof1_num_points, i;
		float[] new_tof_time_array, new_tof_amp_array, time_array;
		float[] tof1_amp_array, tof2_amp_array;

		tof1_num_points = tof_1.GetTotChannels();
		new_tof_time_array = new float[tof1_num_points];
		new_tof_amp_array = new float[tof1_num_points];
		time_array = tof_1.FlightTimePointer();
		tof1_amp_array = tof_1.ChannelCountsPointer();
		tof2_amp_array = tof_2.ChannelCountsPointer();
		if(should_add == true)
		{
			for(i = 0; i < tof1_num_points; i++)
			{
				new_tof_time_array[i] = time_array[i];
				new_tof_amp_array[i] = tof1_amp_array[i] + tof2_amp_array[i];
			}
			new_tof.SetTitle("TOF sum");
		}
		else
		{
			for(i = 0; i < tof1_num_points; i++)
			{
				new_tof_time_array[i] = time_array[i];
				new_tof_amp_array[i] = tof1_amp_array[i] - tof2_amp_array[i];
			}
			new_tof.SetTitle("TOF difference");
		}
		new_tof.SetDwell(tof_1.GetDwell());
		new_tof.SetOffset(tof_1.GetOffset());
		new_tof.SetTotChannels(tof1_num_points);
		new_tof.SetChannelCountsPointer(new_tof_amp_array);
		new_tof.SetTimePointer(new_tof_time_array);

		new_tof.SetLabAngle(tof_1.GetLabAngle());
		new_tof.SetDwellScale(tof_1.GetDwellScale());  
		new_tof.SetOffsetScale(tof_1.GetOffsetScale());
		new_tof.SetIon_m_e(tof_1.GetIon_m_e());
		new_tof.SetPolarizationAngle(tof_1.GetPolarizationAngle());
		new_tof.SetDepolarization(tof_1.GetDepolarization());
		new_tof.SetLaserPolarized(tof_1.GetLaserPolarized());
		new_tof.SetIsRealTOF(true);
		return new_tof;
	}

	protected void SaveFile(File f, boolean compatibilityMode){	
		try {
			f.createNewFile();
			session_document.saveLoc = f.getAbsolutePath();
			FileWriter fw;
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			session_document.OutputUFCData(bw, compatibilityMode);

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Event handlers
	/*protected void EvNewView(TView view){
		/*struct tm date_time;
		time_t timer;
		static long seed_value;
		int i, int_random_number;
		long second_value, min_value, day_value, mon_value;


		if(first_view)
		{
			time(timer);
			date_time = localtime(timer);
			second_value = date_time.tm_sec;
			min_value = date_time.tm_min;
			day_value = date_time.tm_mday;
			mon_value = date_time.tm_mon;
			seed_value = -(second_value * min_value * day_value * mon_value);

			for(i = 0; i < 12; i++)
			{
				RandomNumber(seed_value);
			}
		}

		int_random_number = (int)(55.0 * RandomNumber(seed_value));

		TColor color(255, 255, 255); // White background used so movement of points will look good
		TMDIChild child = new TMDIChild(Client, 0, view.GetWindow());
		if(view.GetViewMenu())
			child.SetMenuDescr(view.GetViewMenu());

			if(!strcmp(view.GetViewName(), "TOF Display View")) // Compares strings, returns 0 if
				// strings are identical
			{
				child.SetIcon(this, IDI_TOFICON);
				child.GetClientWindow().SetBkgndColor(COLORREF(color));
			}
			if(!strcmp(view.GetViewName(), "P(E) Display View"))
			{
				child.SetIcon(this, IDI_POEICON);
				child.GetClientWindow().SetBkgndColor(COLORREF(color));
			}
			child.Create();
			if(!strcmp(view.GetViewName(), "Angular Display View"))
			{
				child.SetIcon(this, IDI_ANGICON);
				child.GetClientWindow().SetBkgndColor(COLORREF(color));
			}

			if(!strcmp(view.GetViewName(), "Residual Display View"))
			{
				child.SetIcon(this, IDI_RESIDICON);
				child.GetClientWindow().SetBkgndColor(COLORREF(color));
			}
			if((int_random_number % 55) == 17)
			{
				child.SetIcon(this, ALIEN);
			}

			child.Create();
			first_view = false;
	}

	protected void EvCloseView(TView view){
		x_text_gadget.SetText("x pos.:  ");
		y_text_gadget.SetText("y pos.:  ");

		SumSquare_text_gadget.SetText("");
		return;
	}

	protected void CmSetInstrParams(){
		String string = "This will cause a recalculation of all forward convolutions.";
		int recalc_value = -1;
		int num_calcs = session_document.GetNumCalcs();
		if(num_calcs > 0)
		{
			recalc_value = GetMainWindow().MessageBox(string, "Change instrumental parameters?",
					MB_OKCANCEL | MB_ICONQUESTION);
		}

		if(recalc_value != IDCANCEL)
		{
			if(SetupInstrParamDialog() == IDOK)
			{
				StoreInstrParams();
				InstParamsSet = true;

				if(instr_params.GetParamsHaveChanged() == true)
				{
					session_document.RecalculateAllCalcs(); 
				}
				session_document.RepaintAllTOFViews();
			}
			else
				return;
		}
	}
	protected	void CmOutputInstParams();*/

	protected void DisplayNewTOF(){

		JFileChooser fc = new JFileChooser();
		int ID = fc.showOpenDialog(mainWindow);
		if (ID != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		File f = fc.getSelectedFile();
		TOF_data time_of_flight = new TOF_data();

		String extension;
		extension = f.getName().substring(f.getName().length() - 4);
		if (extension.equals(".tuf")) {
			time_of_flight.SetTOFFormat(0);
		} else {
			time_of_flight.SetTOFFormat(1);
		}
		
		try {
			Scanner scanMe = new Scanner(f);
			LoadTOFData(scanMe, time_of_flight);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		TOF_Input_1_Dialog tof_input_1 = new TOF_Input_1_Dialog(mainWindow);
		tof_input_1.execute();
		if(tof_input_1.ID == false){
			return;
		}

		SetTOFData(time_of_flight, tof_input_1, session_document);

		if (InstParamsSet == false) {
			SetupInstrParamDialog();

		}
		ViewNumber++;
		// Add this TOF to the document session_document
		session_document.AddTOFData(time_of_flight, true);
		session_document.ResetTOFsInTOFViews();
		session_document.UpdateViewNumber(ViewNumber);

		if (InstParamsSet == false) {
			StoreInstrParams();
			InstParamsSet = true;
		}
		
		TOFView t = new TOFView(session_document, mainWindow);
		time_of_flight.AddAssociatedView(t);
		t.SetTOFsInView();

		t.execute();
	    time_of_flight.SetIsVisible(-1); //CHANGE: change var when visible

		/*
	   	 // Need to initialize a TOFView to be associated with this document and display the
	   	 // TOF in the view.
	    	}
	      else {
	      	tof_input_1.DeleteUnusedInfo();

	      }
	   delete tof_input_1;
	   
	   return;*/
	}
	protected  void DisplayLoadedTOF(){
		TOF_data time_of_flight;
		int[] index_array;
		int count = 0;
		index_array = FillTOFListBox(0, count);
		list_dialog = new List_Dialog(this.mainWindow,list_box_text,1);

		list_dialog.SetCaption("Choose a TOF to display:");

		list_dialog.Execute();
		if(list_dialog.ID != true){
			return;
		}
		//check
			
		time_of_flight = session_document.GetTOFData(index_array[list_dialog
				.GetChosenIndex()[0]]);
		if (time_of_flight.GetIsRealTOF()) {
			tof_input_1 = new TOF_Input_1_Dialog(mainWindow);

			InputTOFData(time_of_flight, tof_input_1, null, false);
			// check

			// Place input data from dialog into storage in a TOF_data object
			SetTOFData(time_of_flight, tof_input_1, session_document);

			// Since this will be placed in a new view, need to increment
			// ViewNumber:
			ViewNumber++;

			TOFView t = new TOFView(session_document, mainWindow);
			time_of_flight.AddAssociatedView(t);
			t.SetTOFsInView();
			t.execute();

			session_document.UpdateViewNumber(ViewNumber);

			// Need to initialize a TOFView to be associated with this document
			// and display the
			// TOF in the view.
			// GetDocManager().MatchTemplate("*.ufc").CreateView(session_document);
			time_of_flight.SetIsVisible(-1); // CHANGE: change var when visible

			System.out.println(t.getSize() + "\t" + t.getLocation());
			tof_input_1 = null;
		}
		else
		{
			tof_input_2 = new TOF_Edit_Dialog_2(this.mainWindow);
			if(InputTOFData(time_of_flight, tof_input_2, null, false, session_document) == 0)  // Last two parameters.TOF already loaded
			{
				SetTOFData(time_of_flight, tof_input_2, session_document); // Retrieves updated TOF data from dialog box
				// Since this will be placed in a new view, need to increment ViewNumber:
				ViewNumber++;
				
				TOFView t = new TOFView(session_document, mainWindow);
				time_of_flight.AddAssociatedView(t);
				t.SetTOFsInView();
				t.execute();

				session_document.UpdateViewNumber(ViewNumber);

				// Need to initialize a TOFView to be associated with this document and display the
				// TOF in the view.
				time_of_flight.SetIsVisible(-1); //CHANGE: change var when visible
			}
			tof_input_2 = null;
		}
		list_box_text = null;
	}
	
	protected  void DeleteLoadedTOFs(){
		int[] index_array, chosen_line_array;
		int i, num_chosen, count = 0;
		String message;
		String num_string;
		List_Dialog many_list_dialog;

		index_array = FillTOFListBox(1, count);   // Only show tof's which are not in use

		many_list_dialog = new List_Dialog(mainWindow, list_box_text, 0);

		many_list_dialog.SetCaption("Delete TOFs which are not in use:");
		many_list_dialog.Execute();
		
		if(many_list_dialog.ID != true){
			return;
		}
		
		num_chosen = many_list_dialog.list_box.getSelectedIndices().length;

		if(num_chosen == 1)
		{
			message = "Are you sure you want to delete this TOF?";
		}
		else
		{
			num_string = ""+ num_chosen;
			message = "Are you sure you want to delete these ";
			message += num_string;
			message += " TOFs?";
		}

		if(JOptionPane.showConfirmDialog(mainWindow, message) == JOptionPane.OK_OPTION){
			chosen_line_array = many_list_dialog.GetChosenIndex();

			// Delete the TOFs backwards so indices of earlier TOFs don't
			// get decremented before they get deleted correctly
			for(i = (num_chosen - 1); i >= 0; i--)
			{
				session_document.DeleteTOF(index_array[chosen_line_array[i]]);
			}
			session_document.ResetTOFsInTOFViews();
			list_box_text = null;
			return;
		}else{
			list_box_text = null;
			return;
		}
		
	}

	protected  void InvertTOFtoPE(){
		TOF_data[] tofs;
		POE_data poe = new POE_data();
		int count = 0;

		int[] index_array;
		index_array = FillTOFListBox(0, count);  // List all TOFs in list box
		list_dialog = new List_Dialog(mainWindow,list_box_text,-1);
		list_dialog.SetListBoxList(list_box_text);
		list_dialog.SetCaption("Choose a TOF to invert:");
		list_dialog.Execute();
		if(list_dialog.ID != true)   {
			return;
		}
		tofs = new TOF_data[list_dialog.GetChosenIndex().length];
		for(int loopMe = 0; loopMe < list_dialog.GetChosenIndex().length; loopMe++){
			tofs[loopMe] = session_document.GetTOFData(index_array[list_dialog.GetChosenIndex()[loopMe]]);
		}
		poe.SetTitle("");
		if(GetInversionData(tofs[0]) == 0)  // Function which displays a dialog and gets data for inversion
		{
			if(GetPOEInfo(tofs[0], poe) == 0)
			{
				InvertTOFs(tofs, poe);

				// Since this will be placed in a new view, need to increment ViewNumber:
				ViewNumber++;
				session_document.AddPOEData(poe, false);

				POEView t = new POEView(session_document, mainWindow);
				poe.AddAssociatedView(t);
				t.SetPOEsInView();
				t.Execute();

				session_document.ResetPOEsInPOEViews();
				session_document.UpdateViewNumber(ViewNumber);

			}
		}
		return;
	}

	protected  void SmoothTOF(){
		Smooth_List_Dialog dialog = new Smooth_List_Dialog(mainWindow);
		TOF_data tof;
		int[] index_array;
		int number_to_average, count = 0;
		index_array = FillTOFListBox(0, count);  // List all TOFs in list box
		dialog.SetListBoxList(list_box_text);
		dialog.SetCaption("Smooth TOF by averaging adjacent points:");
		dialog.Execute();
		if(dialog.ID != true)
		{
			return;
		}
		number_to_average = dialog.GetNumToAverage();
		tof = session_document.GetTOFData(index_array[dialog.GetChosenIndex()[0]]);
		tof.AverageTOFData(number_to_average);

		session_document.RepaintAllTOFViews();
	}

	protected  void CmRestoreTOF(){
		TOF_data tof;
		int[] index_array;
		int count = 0;
		index_array = FillTOFListBox(2, count);   // Show all tofs
		list_dialog.SetListBoxList(list_box_text);
		list_dialog.SetCaption("Select a TOF to restore:");
		
		list_dialog.Execute();
		if(list_dialog.ID != true)
		{
			return;
		}

		tof = session_document.GetTOFData(index_array[list_dialog.GetChosenIndex()[0]]);
		tof.RestoreTOFData();
		session_document.RepaintAllTOFViews();
	}

	protected  void CmRemoveTOFBackground(){
		/*Remove_Background_Dialog dialog = new Remove_Background_Dialog(this.GetMainWindow(),
				REMOVE_BACKGROUND_DIALOG);
		TOF_data tof;
		int[] index_array;
		float time1, time2;
		int count;
		index_array = FillTOFListBox(0, count);   // Show all tofs
		dialog.SetListBoxList(count, list_box_text);
		dialog.SetCaption("Remove TOF background:");
		if(dialog.Execute() != IDOK)
		{
			return;
		}

		tof = session_document.GetTOFData(index_array[dialog.GetChosenIndex()]);
		time1 = dialog.GetSmallerTime();
		time2 = dialog.GetLargerTime();

		if(time1 < tof.GetMinimumTime())
		{
			time1 = tof.GetMinimumTime();
		}

		if(time2 > tof.GetMaximumTime())
		{
			time2 = tof.GetMaximumTime();
		}

		tof.RemoveBackground(time1, time2);
		session_document.RepaintAllTOFViews();
		delete index_array;
		delete list_box_text;
		delete dialog;*/
	}
/*
	protected  void CmOutputTOF(){
		TOF_data tof;
		int i, j, k, count;
		int[] index_array;
		TOpenSaveDialog.TData FileData;
		float time_pointer;
		float counts_pointer;
		float all_tofs_pointer;
		int[] num_channels;
		int this_num_channels, num_total_poes, num_included_poes;


		FileData = new TOpenSaveDialog.TData(OFN_HIDEREADONLY | OFN_FILEMUSTEXIST |
				OFN_OVERWRITEPROMPT, "Graph TOF (*.tgr)|*.tgr|",
				0, "", "tgr");

		// List all possible TOFs to choose one to save as *.tgr file
		index_array = FillTOFListBox(0, count);
		list_dialog.SetListBoxList(count, list_box_text);
		list_dialog.SetCaption("Choose a TOF to Save for Graphing:");

		if(list_dialog.Execute() != IDOK)   {
			delete index_array;    // Memory allocated in FillTOFListBox
			delete list_box_text;    // Memory allocated in FillTOFListBox
			return;
		}

		// Open a dialog to allow a choice of where and under what name to save the file
		if((new TFileSaveDialog(this.GetMainWindow(), FileData)).Execute() == IDOK)
		{
			ofstream os(FileData.FileName);

			if(!os)
				this.GetMainWindow().MessageBox("Unable to open file for saving", "File Error", MB_OK | MB_ICONEXCLAMATION);
			else
			{
				tof = session_document.GetTOFData(index_array[list_dialog.GetChosenIndex()]);
				time_pointer = tof.RealTimePointer();


				if(session_document.GetDetectScheme() == true)
				{
					os << "Flight time (µs)," << "     " << "N(t) (arb. units)\n";
				}
				else
				{
					os << "Flight time (µs)," << "     " << "I(t) (arb. units)\n";
				}

				if(tof.GetIsRealTOF())
				{
					counts_pointer = tof.ChannelCountsPointer();
					for(i = 0; i < tof.GetTotChannels(); i++)
					{
						os << time_pointer[i] << ",     " << counts_pointer[i] << "\n";
					}
				}
				else
				{
					counts_pointer = tof.GetTotalTOF();
					all_tofs_pointer = tof.GetTOFPointers();
					num_channels = tof.GetNumChannelsArray();
					num_total_poes = tof.GetNumCurrentPOEs();
					num_included_poes = tof.GetNumIncludedPOEs();

					for(i = 0; i < tof.GetTotChannels(); i++)
					{
						os << time_pointer[i] << ",     " << counts_pointer[i];
						for(j = 0; j < num_total_poes; j++)
						{
							if(all_tofs_pointer[j] != 0)
							{
								this_num_channels = num_channels[j];
								if(this_num_channels == 1)
									this_num_channels = 0;    // Don't draw TOF for the individual channel if only one channel exists
								for(k = this_num_channels; k >= 0 ; k--)   // Backwards, so total P(E) TOF drawn last
								{
									if((k != 0) || (num_included_poes > 1))   // i.e. skip plotting individual TOFs for each
										// contributing P(E) if only one contributes
									{
										os << ",     " << all_tofs_pointer[j][k][i];
									}
								}
							}
						}
						os << "\n";
					}
				}
			}
		}
	}

*/
	protected  void PerformTOFSubtraction(){
		int[] index_array, view_tof_numbers;
		int count = 0;
		TOF_Subtract_Dialog tof_subtract;
		TOF_data tof_1, tof_2, new_tof, temp_tof;
		List_Dialog tof_multi_list_dialog;


		index_array = FillTOFListBox(3, count); // 3 . show only real TOFs
		tof_multi_list_dialog = new List_Dialog(mainWindow, list_box_text, 2);
		tof_multi_list_dialog.SetCaption("Choose two real TOFs for subtraction:");

		tof_multi_list_dialog.Execute();
		if(tof_multi_list_dialog.ID != true) return;
		
		view_tof_numbers = tof_multi_list_dialog.GetChosenIndex();

		tof_1 = session_document.GetTOFData(index_array[view_tof_numbers[0]]);
		tof_2 = session_document.GetTOFData(index_array[view_tof_numbers[1]]);

		if(CompareTOFs(tof_1, tof_2) == false)
		{
			JOptionPane.showMessageDialog(mainWindow, "TOFs must have same dwell time, offset, ion m/e, and number of points. Cannot perform subtraction with these TOFs!");
			return;
		}
		
		tof_input_1 = new TOF_Input_1_Dialog(mainWindow);


		tof_subtract = new TOF_Subtract_Dialog(mainWindow, tof_1.GetTitle(), tof_2.GetTitle());

		tof_subtract.Execute();
		
		if(tof_subtract.ID != true) return;

		if(!tof_subtract.title1.getText().equals(tof_1.GetTitle()))
		{
			temp_tof = tof_2;
			tof_2 = tof_1;
			tof_1 = temp_tof;
		}

		new_tof = TOFMath(tof_1, tof_2, false);

		InputTOFData(new_tof, tof_input_1, null, false);

		// Place input data from dialog into storage in a TOF_data object
		SetTOFData(new_tof, tof_input_1, session_document);

		// Since this will be placed in a new view, need to increment
		// ViewNumber:
		ViewNumber++;
	


		// Add this TOF to the document session_document
		session_document.AddTOFData(new_tof, true);
		session_document.ResetTOFsInTOFViews();
		session_document.UpdateViewNumber(ViewNumber);
		
		
		TOFView t = new TOFView(session_document, mainWindow);
		new_tof.AddAssociatedView(t);
		t.SetTOFsInView();

		t.execute();


	}

	protected void PerformTOFAddition() {

		int[] index_array, view_tof_numbers;
		int count = 0;
		TOF_data tof_1, tof_2, new_tof;
		List_Dialog tof_multi_list_dialog;

		index_array = FillTOFListBox(3, count); // 3 . show only real TOFs
		tof_multi_list_dialog = new List_Dialog(mainWindow, list_box_text, 2);
		tof_multi_list_dialog
				.SetCaption("Choose two real TOFs for subtraction:");

		tof_multi_list_dialog.Execute();
		if (tof_multi_list_dialog.ID != true)
			return;

		view_tof_numbers = tof_multi_list_dialog.GetChosenIndex();

		tof_1 = session_document.GetTOFData(index_array[view_tof_numbers[0]]);
		tof_2 = session_document.GetTOFData(index_array[view_tof_numbers[1]]);

		if (CompareTOFs(tof_1, tof_2) == false) {
			JOptionPane
					.showMessageDialog(
							mainWindow,
							"TOFs must have same dwell time, offset, ion m/e, and number of points. Cannot perform subtraction with these TOFs!");
			return;
		}
		tof_input_1 = new TOF_Input_1_Dialog(mainWindow);

		new_tof = TOFMath(tof_1, tof_2, true);
		InputTOFData(new_tof, tof_input_1, null, false);

		// Place input data from dialog into storage in a TOF_data object
		SetTOFData(new_tof, tof_input_1, session_document);

		// Since this will be placed in a new view, need to increment
		// ViewNumber:
		ViewNumber++;

		// Add this TOF to the document session_document
		session_document.AddTOFData(new_tof, true);
		session_document.ResetTOFsInTOFViews();
		session_document.UpdateViewNumber(ViewNumber);

		TOFView t = new TOFView(session_document, mainWindow);
		new_tof.AddAssociatedView(t);
		t.SetTOFsInView();

		t.execute();
	}

	protected void Save() {
		if (IsNewFile)
			SaveAs();
		else
			SaveFile(new File(session_document.saveLoc), false);
	}
	
	protected void SaveInCompatibilityMode(){
		if (IsNewFile) {
			session_document.saveLoc = "";
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int returnVal = fc.showSaveDialog(mainWindow);
		if(returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".ufc")) {
		    f = new File(f.getAbsolutePath() + ".ufc");  
		}
		SaveFile(f, true);
	}

	protected void SaveAs() {
		if (IsNewFile) {
			session_document.saveLoc = "";
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int returnVal = fc.showSaveDialog(mainWindow);
		if(returnVal != JFileChooser.APPROVE_OPTION){
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".ufc")) {
		    f = new File(f.getAbsolutePath() + ".ufc");  
		}
		SaveFile(f, false);
	}

	protected void Open() {
		String message;
		String old_file_name;
		old_file_name = session_document.saveLoc;

		if (session_document.GetHasChanged()) {
			message = "Save changes to ";
			if (IsNewFile) {
				message += "this file";
			} else {
				message += new File(old_file_name).getName();
			}
			message += "?";

			/*
			 * switch(GetMainWindow().MessageBox(message, "File Has Changed!",
			 * MB_YESNOCANCEL | MB_ICONQUESTION)) { case IDCANCEL:
			 * UFCFileData.FileName = old_file_name; return; case IDYES:
			 * CmFileSave(); break; default: // Just continue to open file
			 * dialog box break; }
			 */
		}

		session_document = new TOFPOEDocument();
		session_document.SetXPosGadget(this.mainWindow.xposLabel);
		session_document.SetYPosGadget(this.mainWindow.yPosLabel);
		//session_document.SetSumSquareGadget(SumSquare_text_gadget);
		session_document.saveLoc = "";
		IsNewFile = true;

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int i = fc.showOpenDialog(mainWindow);
		if (i == JFileChooser.APPROVE_OPTION) {
			Scanner is;
			try {
				is = new Scanner(fc.getSelectedFile());
				session_document.saveLoc = fc.getSelectedFile().getAbsolutePath();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			switch (session_document.LoadUFCData(is)) {
			case 0: // Error = 0 . File not in correct *.ufc format
				System.out
						.println("File not in correct *.ufc format File Error");
				break;
			case 1: // Error = 1 . File only correct to load TOF data
				System.out
						.println("File not in correct format.  Only TOFs correctly loaded.File Error");
				break;
			default: // Everything is OK; don't do anything
				IsNewFile = false;
				// CHANGE: add displaying feature here
				TOF_data toffers;
				for (int loopMe = 0; loopMe < session_document.GetNumTOFs(); loopMe++) {
					toffers = session_document.GetTOFData(loopMe);
					switch (toffers.GetIsVisible()) {
					case -2:
						break;
					case -1:
						ViewNumber++;

						TOFView t = new TOFView(session_document, mainWindow);
						toffers.AddAssociatedView(t);
						t.SetTOFsInView();
						t.execute();
						session_document.UpdateViewNumber(ViewNumber);
						break;
					default:
						
						/*
						 * toffers.AddAssociatedView(session_document.toffers.
						 * GetIsVisible());
						 * session_document.ResetTOFsInTOFViews();
						 */
						break;
					}
				}

				POE_data poffers;
				for (int loopMe = 0; loopMe < session_document.GetNumPOEs(); loopMe++) {
					poffers = session_document.GetPOEData(loopMe);
					switch (poffers.GetIsVisible()) {
					case -2:
						break;
					case -1:
						ViewNumber++;

						POEView p = new POEView(session_document, mainWindow);
						poffers.AddAssociatedView(p);
						session_document.UpdateViewNumber(ViewNumber);
						p.SetPOEsInView();
						p.Execute();
						break;
					default:
						break;
					}
				}

				TileVertically();
				// END CHANGE
				break;
			}
		}

	}
	/*
	protected void CeFileOpen(TCommandEnabler& commandEnabler);
	protected void CeFileSave(TCommandEnabler& commandEnabler);
	protected void CeFileSaveAs(TCommandEnabler& commandEnabler);

	protected void CmDispResid();
*/
	protected void Graphically(){
		POE_data poe = new POE_data();
		int i;
		float min_energy,	max_energy,	num_poe_points, energy_increment;
		float[] energy_amplitudes;
		float[] energy_values;

		poe.SetTitle("POE1");
		if(GetPOEInfo(null, poe) == 0)
		{
			min_energy = poe.GetMinimumEnergy();
			max_energy = poe.GetMaximumEnergy();
			num_poe_points = poe.GetTotNumPoints();
			energy_increment = (max_energy - min_energy) / (num_poe_points - 1);

			energy_values = new float[(int) num_poe_points];
			energy_amplitudes = new float[(int) num_poe_points];

			for(i = 0; i < num_poe_points; i++)
			{
				energy_values[i] = min_energy + (i * energy_increment);
				energy_amplitudes[i] = 0.0f;
			}
			poe.SetPOEPointer(energy_amplitudes);
			poe.SetEnergyPointer(energy_values);
			poe.NormalizePOE(null);

			ViewNumber++;

			

			POEView p = new POEView(session_document, mainWindow);
			
			poe.AddAssociatedView(p);
			session_document.AddPOEData(poe, true);
			session_document.ResetPOEsInPOEViews();
			session_document.UpdateViewNumber(ViewNumber);
			p.SetPOEsInView();
			p.Execute();

			
			//GetDocManager().MatchTemplate("*.ufc").GetNextTemplate().CreateView(session_document);
			poe.SetIsVisible(-1);	//CHANGE: visibility = -1 upon new display
			poe = null;

		}
		return;
	}

	protected void OutputPEtoFile() {
		POE_data poe;
		int[] index_array;
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("UFC file", "ufc"));
		int returnVal = fc.showSaveDialog(mainWindow);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File f = fc.getSelectedFile();
		if (!f.getName().endsWith(".poe")) {
			f = new File(f.getAbsolutePath() + ".poe");
		}

		// List all possible P(E)'s to choose one to save as *.poe file
		index_array = FillPOEListBox(true);
		list_dialog = new List_Dialog(mainWindow, list_box_text, 1);
		list_dialog.SetCaption("Choose a P(E) to Save:");
		list_dialog.Execute();

		if (!list_dialog.ID) {
			return;
		}

		try {
			f.createNewFile();
			FileWriter fw;
			fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			poe = session_document.GetPOEData(index_array[list_dialog
					.GetChosenIndex()[0]]);
			poe.SaveAsPOEFile(bw);

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	/*

	protected void CmPOEForGraphing(){
		int i;
		POE_data poe;
		int[] index_array;
		TOpenSaveDialog::TData FileData;
		float energy_pointer;
		float amplitude;

		FileData = new TOpenSaveDialog.TData(OFN_HIDEREADONLY | OFN_FILEMUSTEXIST |
				OFN_OVERWRITEPROMPT, "Graph P(E) (*.pgr)|*.pgr|",
				0, "", "pgr");

		// List all possible P(E)'s to choose one to save as *.pgr file
		index_array = FillPOEListBox(true);
		list_dialog.SetCaption("Choose a P(E) to Save for Graphing:");

		if(list_dialog.Execute() != IDOK)   {
			delete index_array;    // Memory allocated in FillPOEListBox
			delete list_box_text;    // Memory allocated in FillPOEListBox
			return;
		}

		// Open a dialog to allow a choice of where and under what name to save the file
		if((new TFileSaveDialog(this.GetMainWindow(), FileData)).Execute() == IDOK)
		{
			ofstream os(FileData.FileName);

			if(!os)
				this.GetMainWindow().MessageBox("Unable to open file for saving", "File Error", MB_OK | MB_ICONEXCLAMATION);
				else
				{
					poe = session_document.GetPOEData(index_array[list_dialog.GetChosenIndex()]);
					energy_pointer = poe.GetEnergyPointer();
					amplitude = poe.GetPOEPointer();

					os << "Trans. Energy (kcal/mol)," << "     " << "P(E)\n";
					for(i = 0; i < poe.GetTotNumPoints(); i++)
					{
						os << energy_pointer[i] << ",     " << amplitude[i] << "\n";
					}
				}
		}
	}
*/
	protected void OpenDisplaypoeFile() {
		POE_data poe = new POE_data();

		JFileChooser fc = new JFileChooser();
		int ID = fc.showOpenDialog(mainWindow);
		if (ID != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fc.getSelectedFile();

		Scanner is = null;
		try {
			is = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		poe.LoadPOEFile(is);

		ViewNumber++;
		POEView p = new POEView(session_document, mainWindow);

		poe.AddAssociatedView(p);
		session_document.AddPOEData(poe, true);
		session_document.ResetPOEsInPOEViews();
		session_document.UpdateViewNumber(ViewNumber);
		p.SetPOEsInView();
		p.Execute();

		session_document.UpdateViewNumber(ViewNumber);

		poe.SetIsVisible(-1);
	}

	protected void DisplayStoredPE(){
		POE_data poe;
		int[] index_array;
		index_array = FillPOEListBox(false);
		list_dialog = new List_Dialog(mainWindow, list_box_text, 1);
		list_dialog.SetCaption("Choose a P(E) to display:");

		list_dialog.Execute();
		if(list_dialog.ID != true)  {
			return;
		}
		poe = session_document.GetPOEData(index_array[list_dialog.GetChosenIndex()[0]]);

		// Since this will be placed in a new view, need to increment ViewNumber:
		ViewNumber++;
		
		System.out.println("doc: " + session_document + "\nx: " + session_document.GetXPosGadget());
		POEView p = new POEView(session_document, mainWindow);
		
		poe.AddAssociatedView(p);
		session_document.AddPOEData(poe, true);
		session_document.ResetPOEsInPOEViews();
		session_document.UpdateViewNumber(ViewNumber);
		p.SetPOEsInView();
		p.Execute();

		session_document.UpdateViewNumber(ViewNumber);

		// Need to initialize a POEView to be associated with this document and display the
		// POE in the view.
		poe.SetIsVisible(-1);	//CHANGE: visibilty = -1 upon display
	}

	protected	void DeleteStoredPE(){
		int[] index_array;
		index_array = FillPOEListBox(false);   // Only show tof's which are not in use
		list_dialog = new List_Dialog(mainWindow, list_box_text, 1);
		list_dialog.SetCaption("Delete a P(E) which is not in use:");
		list_dialog.Execute();
		if(list_dialog.ID!= true)   {
			return;
		}
		//switch (this.GetMainWindow().MessageBox("Are you sure you want to delete this P(E)?", "Confirmation:",
			//	MB_YESNO | MB_ICONHAND))
				//{
				//case IDNO:
					//return;
				//default:
					session_document.DeletePOE(index_array[list_dialog.GetChosenIndex()[0]]);
					session_document.ResetPOEsInPOEViews();
					return;
			//	}
	}

	protected  void CmPOESetEnergyUnits(){
		Energy_Units_Dialog energy_dialog;
		energy_dialog = new Energy_Units_Dialog(mainWindow);

		energy_dialog.SetSelectedUnits(session_document.energy_unit);

		energy_dialog.Execute();
		if(energy_dialog.ID != true)
		{
			session_document.SetEnergyUnits(energy_dialog.GetSelectedUnits());
		}
	}

	protected void CalculateTOFsforagivenme() {
		float ionizer_len = session_document.GetIonizerLen(); // To send to
																// calc_data for
																// initialization
		int num_current_poes = session_document.GetNumPOEs();
		CalculateMainDialog calc_dialog;
		Calc_data calculation = new Calc_data(CalcNumber + 1, num_current_poes,
				ionizer_len, this.mainWindow);

		calculation.SetFlightLen(session_document.GetFlightLen());
		calculation.SetBeamAng(session_document.GetBeamAng());
		calculation.SetDetectorAng(session_document.GetDetectAng());
		calculation.SetIsNumDensityCalc(session_document.GetDetectScheme());

		// The following is done in the seemingly weird order so that changes to
		// the
		// ionizer length, flight lengh, etc. in the main calculation dialog box
		// can be directly applied
		// to the calculation object through the document object. The object
		// must first
		// be sent to the document, where it is copied. It is then called from
		// the document,
		// where the calculation is passed by reference and changes which are
		// made are
		// made directly in the document version of the calculation.
		int index = session_document.AddCalcData(calculation);
		calculation = session_document.GetCalcData(index);

		calc_dialog = new CalculateMainDialog(this.mainWindow, this);
		calc_dialog.SetDialogData(calculation);
		calc_dialog.SetAreInstParamsSet(InstParamsSet);

		calc_dialog.Execute();
		if(calc_dialog.ID == false){
			return;
		}
		// check
		CalcNumber++;
		calculation.SetCalcNumber(CalcNumber);

		// session_document.DeleteCalc(index);

	}

	protected  void ChangeCalculationParameters(){
		int[] index_array;
		int index;

		Calc_data calculation;
		CalculateMainDialog calc_dialog;
		index_array = FillCalcListBox(true);   // Show all Calcs
		list_dialog = new List_Dialog(mainWindow,list_box_text, 1);
		list_dialog.SetCaption("Choose a Calculation to update:");
		list_dialog.Execute();
		if(!list_dialog.ID){
			return;
		}
		else
		{
			index = index_array[list_dialog.GetChosenIndex()[0]];
			calculation = session_document.GetCalcData(index);

			calc_dialog = new CalculateMainDialog(mainWindow, this);
			calc_dialog.DetachOldTOFs(true);


			calc_dialog.SetDialogData(calculation);
			calc_dialog.SetAreInstParamsSet(InstParamsSet);

			calc_dialog.Execute();
			
			if(!calc_dialog.ID){
				if(calculation.GetNumContribPOEs() == 0)
				{
					session_document.DeleteCalc(index);
				}
			}
		}
	}

	protected  void CmPolarAngDist(){
		AngularDialog polar_ang_dialog = new AngularDialog(mainWindow);
		int num_tofs, i, j, num_inc_tofs;


		Ang_data angular_dist;
		int[] tof_num_array, TOFNumArray;
		int[] temp_tof_num_array, tempTOFNumArray;
		float[] angle_array = null;

		float temp_angle, remainder, temp_angle_over_360;
		float start_time, end_time;

		TOF_data[] tofs;
		num_tofs = session_document.GetNumTOFs();
		tofs = new TOF_data[num_tofs];

		for(i = 0; i < num_tofs; i++)
		{                                                                      
			tofs[i] = session_document.GetTOFData(i);
		}

		polar_ang_dialog.setTitle("Polarization angular distribution:");
		polar_ang_dialog.SetDialogData(num_tofs, tofs, false); // Last param.is not lab angle dist.

		polar_ang_dialog.Execute();
		//check
		
			ViewNumber++;
			AngNumber = session_document.GetLastAngNumber();
			AngNumber++;

			if(polar_ang_dialog.GetIsEntireRange() == false)
			{
				start_time = polar_ang_dialog.GetStartTime();
				end_time = polar_ang_dialog.GetEndTime();
			}
			else
			{
				start_time = 0;
				end_time = -1;
			}
			angular_dist = new Ang_data(false, AngNumber, start_time, end_time);

			num_inc_tofs = polar_ang_dialog.GetNumIncludedTOFs();
			tof_num_array = polar_ang_dialog.GetIncludedTOFArray();
			TOFNumArray = polar_ang_dialog.GetTOFNumArray();

			// Filter out TOFs which aren't polarized
			for(i = 0; i < num_inc_tofs; i++)
			{
				if(tofs[tof_num_array[i]].GetLaserPolarized() == false)
				{
					// Remove this tof_num from array
					num_inc_tofs--;
					for(j = i; j < num_inc_tofs; j++)
					{
						tof_num_array[j] = tof_num_array[j + 1];
						TOFNumArray[j] = TOFNumArray[j + 1];
					}
					i--;
				}
			}

			if(num_inc_tofs != 0)
			{
				temp_tof_num_array = new int[num_inc_tofs];
				tempTOFNumArray = new int[num_inc_tofs];
				for(i = 0; i < num_inc_tofs; i++)
				{
					temp_tof_num_array[i] = tof_num_array[i];
					tempTOFNumArray[i] = TOFNumArray[i];
				}
				
				tof_num_array = temp_tof_num_array;
				TOFNumArray = tempTOFNumArray;

				angle_array = new float[num_inc_tofs];
			}

			for(i = 0; i < num_inc_tofs; i++)
			{
				// Get all angles between 0 and 360 degrees
				temp_angle = tofs[tof_num_array[i]].GetPolarizationAngle();
				if((temp_angle > 360) || (temp_angle < 0))
				{
					temp_angle_over_360 = (float) (temp_angle / 360.0);
					remainder = temp_angle_over_360 - (int)temp_angle_over_360;
					temp_angle = (float) (remainder * 360.0);

					if(temp_angle < 0)
					{
						temp_angle += 360.0;
					}
				}
				angle_array[i] = temp_angle;
			}

			if(num_inc_tofs != 0)
			{
				angular_dist.SetAngleArray(angle_array);
				angular_dist.SetIncludedTOFs(num_inc_tofs, TOFNumArray);

				// Add this Angular distribution to the document session_document
				session_document.AddAngData(angular_dist);
				session_document.UpdateViewNumber(ViewNumber);
				session_document.UpdateAngNumber(AngNumber);
				
				AngView a = new AngView(session_document, mainWindow);
				angular_dist.AddAssociatedView(a);
				a.SetAngDistsInView();
				
				a.execute();
				//GetDocManager().MatchTemplate("*.ufc").GetNextTemplate().GetNextTemplate().CreateView(session_document);
			}
		
	}

	protected  void ShowNewAngularDistribution(){
		AngularDialog lab_ang_dialog = new AngularDialog(mainWindow);
		int num_tofs, i, num_inc_tofs;
		float temp_angle, remainder, temp_angle_over_360;
		boolean[] is_tof_included_array;
		int included_count;

		int[] tof_num_array, TOFNumArray;
		//int *temp_tof_num_array, *tempTOFNumArray;

		float[] angle_array;
		float start_time, end_time;

		Ang_data angular_dist;

		TOF_data[] tofs;


		num_tofs = session_document.GetNumTOFs();
		tofs = new TOF_data[num_tofs];

		for(i = 0; i < num_tofs; i++)
		{
			tofs[i] = session_document.GetTOFData(i);
		}

		lab_ang_dialog.setTitle("Lab angular distribution:");
		lab_ang_dialog.SetDialogData(num_tofs, tofs, true); // Last param.is lab angle dist.


		lab_ang_dialog.Execute();
		// check
		// is_tof_included_array = lab_ang_dialog.GetIsTOFIncluded();
		// Since this will be placed in a new view, need to increment
		// ViewNumber:
		ViewNumber++;
		AngNumber = session_document.GetLastAngNumber();
		AngNumber++;

		if (lab_ang_dialog.GetIsEntireRange() == false) {
			start_time = lab_ang_dialog.GetStartTime();
			end_time = lab_ang_dialog.GetEndTime();
		} else {
			start_time = 0;
			end_time = -1;
		}

		angular_dist = new Ang_data(true, AngNumber, start_time, end_time);

		num_inc_tofs = lab_ang_dialog.GetNumIncludedTOFs();
		tof_num_array = lab_ang_dialog.GetIncludedTOFArray();
		TOFNumArray = lab_ang_dialog.GetTOFNumArray();
		angle_array = new float[num_inc_tofs];

		included_count = 0;
		for (i = 0; i < num_inc_tofs; i++) {
			// if(is_tof_included_array[i])
			// {
			// Get all angles between 0 and 360 degrees
			temp_angle = tofs[tof_num_array[i]].GetLabAngle();
			if ((temp_angle > 360) || (temp_angle < 0)) {
				temp_angle_over_360 = (float) (temp_angle / 360.0);
				remainder = temp_angle_over_360 - (int) temp_angle_over_360;
				temp_angle = (float) (remainder * 360.0);

				if (temp_angle < 0) {
					temp_angle += 360.0;
				}
			}
			angle_array[i] = temp_angle;
			// included_count++;
			// }
		}

		angular_dist.SetAngleArray(angle_array);

		angular_dist.SetIncludedTOFs(num_inc_tofs, TOFNumArray);

		// Add this Angular distribution to the document session_document
		session_document.AddAngData(angular_dist);
		// session_document.ResetTOFsInTOFViews();
		session_document.UpdateViewNumber(ViewNumber);
		session_document.UpdateAngNumber(AngNumber);

		AngView a = new AngView(session_document, mainWindow);
		angular_dist.AddAssociatedView(a);
		a.SetAngDistsInView();

		a.execute();

	}

	protected void DisplayLoadedAngularDistribution(){
		Ang_data ang_dist;
		int[] index_array;
		
		index_array = FillAngListBox(true);

		list_dialog = new List_Dialog(this.mainWindow,list_box_text,1);
		list_dialog.SetCaption("Choose an angular distribution to display:");

		list_dialog.Execute();
		if(list_dialog.ID != true){
			return;
		}
		ang_dist = session_document.GetAngData(index_array[list_dialog.GetChosenIndex()[0]]);

		// Since this will be placed in a new view, need to increment ViewNumber:
		ViewNumber++;

		session_document.UpdateViewNumber(ViewNumber);
		
		AngView a = new AngView(session_document, mainWindow);
		ang_dist.AddAssociatedView(a);
		a.SetAngDistsInView();

		a.execute();
	}

	protected void DeleteAngularDistribution(){
		int[] index_array;
		
		index_array = FillAngListBox(false); 

		list_dialog = new List_Dialog(this.mainWindow,list_box_text,1);
		list_dialog.SetCaption("Delete an angular distribution which is not in use:");
		list_dialog.Execute();
		
		if(list_dialog.ID != true){
			return;
		}
		/*switch (this.GetMainWindow().MessageBox("Are you sure you want to delete this angular distribution?",
				"Confirmation:",  MB_YESNO | MB_ICONHAND))
				{
				case IDNO:
					return;
				default:
					session_document.DeleteAng(index_array[list_dialog.GetChosenIndex()]);
					session_document.ResetAngsInAngViews();
					return;
				}*/
		session_document.DeleteAng(index_array[list_dialog.GetChosenIndex()[0]]);
		session_document.ResetAngsInAngViews();
	}
/*
	protected void CmAngForGraphing(){
		int i;
		Ang_data ang;
		int[] index_array;
		TOpenSaveDialog.TData FileData;
		float angle_pointer;
		float amplitude;

		FileData = new TOpenSaveDialog.TData(OFN_HIDEREADONLY | OFN_FILEMUSTEXIST |
				OFN_OVERWRITEPROMPT, "Graph Ang. Data (*.agr)|*.agr|",
				0, "", "agr");

		// List all possible angs to choose one to save as *.agr file
		index_array = FillAngListBox(true);
		list_dialog.SetCaption("Choose an Angular Distribution to Save for Graphing:");

		if(list_dialog.Execute() != IDOK)
		{
			return;
		}

		// Open a dialog to allow a choice of where and under what name to save the file
		if((new TFileSaveDialog(this.GetMainWindow(), FileData)).Execute() == IDOK)
		{
			ofstream os(FileData.FileName);

			if(!os)
				this.GetMainWindow().MessageBox("Unable to open file for saving", "File Error", MB_OK | MB_ICONEXCLAMATION);
				else
				{
					ang = session_document.GetAngData(index_array[list_dialog.GetChosenIndex()]);
					angle_pointer = ang.GetAngleArray();
					amplitude = ang.GetIntegratedTOFArray();

					os << "Angle (degrees)," << "     " << "T(theta)\n";
					for(i = 0; i < ang.GetNumTOFs(); i++)
					{
						os << angle_pointer[i] << ",     " << amplitude[i] << "\n";
					}
				}
		}
	}

	protected void CmAbout(){
		
	}*/
	protected void TileVertically() {
		// How many frames do we have?
		ArrayList<JInternalFrame> allframes = mainWindow.internalFrames;
		int count = allframes.size();
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			rows++;
			if (rows * cols < count) {
				cols++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = mainWindow.pane.getRootPane().getSize();

		int w = size.width / cols;
		int h = (size.height-20) / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes.get((i * cols) + j);

				f.setLocation(x, y);
				f.setSize(w+f.getInsets().left+f.getInsets().right, h+f.getInsets().bottom);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}

	}
	
	protected void TileHorizontally() {
		// How many frames do we have?
		ArrayList<JInternalFrame> allframes = mainWindow.internalFrames;
		int count = allframes.size();
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = mainWindow.pane.getRootPane().getSize();

		int w = size.width / cols;
		int h = (size.height-20) / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes.get((i * cols) + j);

				f.setLocation(x, y);
				f.setSize(w+f.getInsets().left+f.getInsets().right, h+f.getInsets().bottom);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PrintStream out;
		try {
			String path = sbapp.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
	        path = path.substring(0, path.length()-1);
			if(path.lastIndexOf('\\') != -1){
				System.out.println("Windows");
	        	path = path.substring(1, path.lastIndexOf('\\'));
	        }else if(path.lastIndexOf("/") != -1){
	        	System.out.println("Mac");
	        	if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
	        		path = path.substring(1, path.length());
	        	}
	        	path = path.substring(0, path.lastIndexOf('/'));
	        }
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
			Date date = new Date();
			
	        path += "/sbeamlogs" ;
	        new File(path).mkdir();
	        path+= "/" + dateFormat.format(date) + ".log";
			out = new PrintStream(new FileOutputStream(path));
			System.setOut(out);
			System.setErr(out);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (FileNotFoundException | URISyntaxException |  UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sbapp s = new sbapp();
		s.InitMainWindow();
		
	}
}
