package sbeam;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;

public class TOF_data {

	// Information common to both real and calculated TOFs
	protected String title; // Title of TOF
	protected float dwell;   // Dwell time (channel width) of multichannel scalar
	protected float offset; // Offset time (time delay between trigger and laset shot)
	protected scale dwell_scale, offset_scale;  // Time scale of dwell time and offest
	// (ps, ns, us, ms, etc)
	protected int num_tot_channels;  // Total # of MCS channels contained in TOF
	protected float ion_m_e, lab_angle;
	protected float[] actual_flight_time_micro; // Real flight time (as determined from offset & ion
	// flight time) for flight from interaction region to
	// ionization.  Time in microseconds.

	protected float[] extrema;
	protected boolean is_real_TOF;
	protected Color time_of_flight_color;
	protected int is_Visible; //CHANGE: -2 = not visible, -1 = visible, 0+ = appended to viewnumber of value

	// Information specific to real TOFs
	protected float primary_mass1, primary_mass2;  // Masses of primary fragments for inversion
	protected float beta_for_inversion, peak_beam_velocity;
	protected boolean polarized_laser;
	protected float polarization_angle, ion_flight_time;
	protected float[] channel_counts, tof_flight_time;
	protected int format;

	// Data particular to calculated TOFs
	protected float energy_increment; // Used when adjusting TOFs after P(E)
	protected int num_current_poes, number_included_poes;
	protected int[] number_channels;
	protected float[][][] individual_tofs;
	protected String[] detach_poe_titles;
	protected int detach_num_poes;

	protected boolean is_being_changed;
	protected float[][] delta_tofs;
	protected int delta_tof_poe_number;
	protected float delta_tof_poe_amplitude;

	protected int associated_calc_number, TOF_num; // TOF_num used to track TOF when TOFs are added & deleted

	protected Color[] tof_colors; // For several TOFs calculated from different P(E)'s

	// Information stored for calculated TOF
	protected POE_calc_data[] calc_data_for_poes;
	protected int num_beam_angle_segs, num_det_angle_segs, num_lab_vel_segs;
	protected int num_ionization_segs, num_beam_vel_segs;
	protected String calculation_title;

	protected boolean is_number_density_calc, is_ionization_gaussian;
	protected float depolarization, minimum_lab_vel, maximum_lab_vel;
	protected float flight_length, beam_angle_width, detector_angle_width, ionizer_length;
	protected float beam_vel_alpha, beam_vel_speed_ratio;

	protected float[] unaltered_TOF_data;
	protected boolean TOF_has_been_altered;
	protected int num_points_unaltered_tof;
	protected float unaltered_dwell;

	protected ArrayList<TOFView> AssociatedTOFViews;
	
	
	public TOF_data() {
		// TODO Auto-generated constructor stub
		is_real_TOF = true;
		is_being_changed = false;
		delta_tofs = null;
		delta_tof_poe_number = 0;
		delta_tof_poe_amplitude = 0;
		num_current_poes = 0;
		number_channels = null;
		individual_tofs = null;

		calc_data_for_poes = null;
		detach_poe_titles = null;

		tof_colors = null;
		energy_increment = 0;
		calculation_title = null;
		title = null;
		dwell = 1.0f;
		is_Visible = -2;	//CHANGE: default value
		extrema = new float[2];

		TOF_has_been_altered = false;
		unaltered_TOF_data = null;
		num_points_unaltered_tof = -1;
		unaltered_dwell = 0;

		offset = 0.0f; 

		num_tot_channels = 0;
		channel_counts = null;
		tof_flight_time = null;
		format = 1;
		dwell_scale = scale.ns;
		offset_scale = scale.ns;
		ion_m_e = 1.0f;
		lab_angle = 10;
		polarization_angle = 90;
		depolarization = 1.0f;

		primary_mass1 = 1.0f;
		primary_mass2 = 1.0f;
		peak_beam_velocity = 0.0f;
		beta_for_inversion = 0.0f;

		// Initialize boolean variables
		polarized_laser = false;

		actual_flight_time_micro = null;

		AssociatedTOFViews = new ArrayList<TOFView>();
		//AssociatedCalcs = 0;
		time_of_flight_color = new Color(0,0,0);
	}

	public void DeleteTOFDataInfo(){
		// Called when final delete of TOF data is called.  Necessary since
		// array structure of TOF datas requires frequent moving of TOF pointer
		// and subsequent calling of the destructor.  Thus, deletion only can
		// occur in another function.  Probably, this could have been avoided had
		// an array of pointers to TOF_datas been created as opposed to an
		// array of TOF_datas.
	
	}

	// Implementations to allow setting (changing) of TOF data
	public void SetTitle(String newtitle){
		title = newtitle;
	}
	
	public void SetDwell(float newdwell)  {
		dwell = newdwell;
	}

	public void SetOffset(float newoffset)	{
		offset = newoffset;
	}

	public void SetTotChannels(int dummy) {
		num_tot_channels = dummy;
	}

	public void SetChannelCountsPointer(float[] array)
	{
		channel_counts = array;
	}

	public void SetTimePointer(float[] array)
	{
		tof_flight_time = array;
	}

	public void SetIsVisible(int vis)	{is_Visible = vis;}	//CHANGE: setter
	public void SetTOFColor(Color new_color)  {time_of_flight_color = new_color;}
	public void SetDwellScale(scale time_scale) {dwell_scale = time_scale;}
	public void SetOffsetScale(scale time_scale) {offset_scale = time_scale;}
	public void SetIon_m_e(float ion_mass) {ion_m_e = ion_mass;}
	public void SetMass1(float mass1)  {primary_mass1 = mass1;}
	public void SetMass2(float mass2)  {primary_mass2 = mass2;}
	public void SetLabAngle(float angle)  {lab_angle = angle;}
	public void SetLaserPolarized(boolean is_laser_polarized) {polarized_laser = is_laser_polarized;}
	
	public void SetRealFlightTime(){
		int i, scaling_factor;
		float offset_in_microseconds;
		float dwell_in_microseconds;
		float starting_time_in_microseconds;

		actual_flight_time_micro = new float[num_tot_channels];

		scaling_factor = (int) dwell_scale.value() + 6;  // To convert to microseconds
		dwell_in_microseconds = (float) (dwell * Math.pow(10, scaling_factor));
		if(is_real_TOF)
		{
			starting_time_in_microseconds = (float) (tof_flight_time[0] * Math.pow(10, scaling_factor));
		}
		else
		{
			starting_time_in_microseconds = 0.0f;
		}

		scaling_factor = (int) offset_scale.value() + 6; // To convert to microseconds
		offset_in_microseconds = (float) (offset * Math.pow(10, scaling_factor));

		for(i = 0; i < num_tot_channels; i++)
		{
			actual_flight_time_micro[i] = ((float)i * dwell_in_microseconds) - ion_flight_time
					+ offset_in_microseconds + starting_time_in_microseconds;
		}
	}
	
	public void SetIonFlightTime(float flight_constant)
	{
		if(is_real_TOF)
		{
			ion_flight_time = (float) (flight_constant * Math.sqrt(ion_m_e));
		}
		else
		{
			ion_flight_time = 0.0f;
		}
		SetRealFlightTime();
	}

	public void SetBetaForInversion(float beta)  {beta_for_inversion = beta;}
	public void SetInversionBeamVel(float velocity)  {peak_beam_velocity = velocity;}
	public void SetPolarizationAngle(float angle) {polarization_angle = angle;}
	public void SetDepolarization(float dpol) {depolarization = dpol;}
	// Default value of 1 . completely unpolarized
	public void SetTOFFormat(int tof_format) {format = tof_format;}


	// Set Functions particular to calculated TOF:
	public void SetIsRealTOF(boolean real_tof)  {is_real_TOF = real_tof;}
	public void SetTOFPointers(int num_poes, int[] num_channels, float[][][] individ_TOFs, float[] overall_TOF){
		num_current_poes = num_poes;
		number_channels = num_channels;
		individual_tofs = individ_TOFs;
		channel_counts = overall_TOF;
	}

	// Information which describes how TOF was calculated:
	public void SetBeamParams(float alpha_param, float spd_rat, int num_bm_vel_segs)
	{
		beam_vel_alpha = alpha_param;
		beam_vel_speed_ratio = spd_rat;
		num_beam_vel_segs = num_bm_vel_segs;
	}

	public void GetBeamParams(Float alpha_param, Float spd_rat, Integer num_bm_vel_segs)
	{
		alpha_param = beam_vel_alpha;
		spd_rat = beam_vel_speed_ratio;
		num_bm_vel_segs = num_beam_vel_segs;
	}

	public void SetIonizationParams(boolean is_ioniz_gauss, int num_ion_segs)
	{
		is_ionization_gaussian = is_ioniz_gauss;
		num_ionization_segs = num_ion_segs;
	}

	public void GetIonizationParams(Boolean is_ioniz_gauss, Integer num_ion_segs)
	{
		is_ioniz_gauss = is_ionization_gaussian;
		num_ion_segs = num_ionization_segs;
	}

	public void SetInstrumentParams(boolean number_density_calc, float flt_len, float bm_ang_width,
			float det_ang_width, float ion_len)
	{
		is_number_density_calc = number_density_calc;
		flight_length = flt_len;
		beam_angle_width = bm_ang_width;
		detector_angle_width = det_ang_width;
		ionizer_length = ion_len;
	}

	public void GetInstrumentParams(Boolean number_density_calc, Float flt_len, Float bm_ang_width,
			Float det_ang_width, Float ion_len)
	{
		number_density_calc = is_number_density_calc;
		flt_len = flight_length;
		bm_ang_width = beam_angle_width;
		det_ang_width = detector_angle_width;
		ion_len = ionizer_length;
	}

	public void SetLabVelParams(float min_lab_vel, float max_lab_vel, int num_lv_segs)
	{
		minimum_lab_vel = min_lab_vel;
		maximum_lab_vel = max_lab_vel;
		num_lab_vel_segs = num_lv_segs;
	}

	public void GetLabVelParams(Float min_lab_vel, Float max_lab_vel, Integer num_lv_segs)
	{
		min_lab_vel = minimum_lab_vel;
		max_lab_vel = maximum_lab_vel;
		num_lv_segs = num_lab_vel_segs;
	}

	public void SetPolarizationParams(boolean polarized, float polar_ang, float depol)
	{
		polarized_laser = polarized;
		polarization_angle = polar_ang;
		depolarization = depol;
	}

	public void GetPolarizationParams(Boolean polarized, Float polar_ang, Float depol)
	{
		polarized = polarized_laser;//is_laser_polarized;
		polar_ang = polarization_angle;
		depol = depolarization;
	}

	public void SetCalcTitle(String calc_title)
	{
		calculation_title = "";
		calculation_title = calc_title;
	}

	public String GetCalcTitle()  {return calculation_title;}

	public void SetAngleSegs(int num_bm_ang_segs, int num_det_ang_segs)
	{
		num_beam_angle_segs = num_bm_ang_segs;
		num_det_angle_segs = num_det_ang_segs;
	}

	public void GetAngleSegs(int num_bm_ang_segs, int num_det_ang_segs)
	{
		num_bm_ang_segs = num_beam_angle_segs;
		num_det_ang_segs = num_det_angle_segs;
	}

	public void SetPOECalcData(POE_calc_data[] data_for_poes)
	{
		calc_data_for_poes = data_for_poes;
	}

	public POE_calc_data[] GetPOECalcData()  {return calc_data_for_poes;}

	public void SetTOFColors(Color[] new_color_array)  {tof_colors = new_color_array;}
	public void SetNumIncludedPOEs(int new_num)  {number_included_poes = new_num;}
	
	public void SetIndividualColor(Color new_color, int poe_number){
		if(individual_tofs[poe_number] != null)
			tof_colors[poe_number] = new_color;
	}

	public void SetAssociatedCalc(int new_num)
	{
		int i, new_length;
		String new_title;
		if((new_num == -1) && (associated_calc_number != -1))
		{
			if(title != null)
			{
				new_title = title + ", Detached";
				title = new_title;
			}
		}
		associated_calc_number = new_num;
	}
	public void SetCurrentPOETitles(int num_poes, String[] current_poe_titles, POE_calc_data[] calc_data){
		int i, j;
		POE_calc_data[] new_poe_calc_data;

		detach_poe_titles = new String[num_poes];
		detach_num_poes = num_poes;

		Color[] new_tof_colors;
		calc_data_for_poes = calc_data;

		new_poe_calc_data = new POE_calc_data[num_poes];
		new_tof_colors = new Color[num_poes];
		int temp_num_channels;
		for(i = 0; i < num_poes; i++)
		{
			detach_poe_titles[i] = "";
			detach_poe_titles[i] = current_poe_titles[i];

			if(tof_colors[i] != null)
			{
				new_tof_colors[i] = tof_colors[i];
			}
			else
			{
				new_tof_colors[i] = null;
			}

			// Copy the POE_calc_data so subsequent changes won't be reflected here
			new_poe_calc_data[i].is_included = calc_data_for_poes[i].is_included;
			if(new_poe_calc_data[i].is_included)
			{
				new_poe_calc_data[i].beta_param = calc_data_for_poes[i].beta_param;
				new_poe_calc_data[i].num_channels = calc_data_for_poes[i].num_channels;
				temp_num_channels = new_poe_calc_data[i].num_channels;

				new_poe_calc_data[i].mass_1 = new float[temp_num_channels];
				new_poe_calc_data[i].mass_2 = new float[temp_num_channels];
				new_poe_calc_data[i].rel_weight = new float[temp_num_channels];
				new_poe_calc_data[i].mass_ratio = new float[temp_num_channels];
				for(j = 0; j < temp_num_channels; j++)
				{
					new_poe_calc_data[i].mass_1[j] = calc_data_for_poes[i].mass_1[j];
					new_poe_calc_data[i].mass_2[j] = calc_data_for_poes[i].mass_2[j];
					new_poe_calc_data[i].rel_weight[j] = calc_data_for_poes[i].rel_weight[j];
					new_poe_calc_data[i].mass_ratio[j] = calc_data_for_poes[i].mass_ratio[j];
				}
			}
		}
		calc_data_for_poes = new_poe_calc_data;
		tof_colors = new_tof_colors;
	}

	public String[] GetCurrentPOETitles(int number_poes)
	{
		number_poes = detach_num_poes;
		return detach_poe_titles;
	}

	public int GetDetachedNumPOEs()  {return detach_num_poes;}
	
	public void AddPOEArray(int num_poes){
		int i;
		float[][][] new_individ_tofs;
		Color[] new_tof_colors;
		int[] new_num_channels;
		num_current_poes = num_poes;

		new_individ_tofs = new float[num_poes][][];
		new_tof_colors = new Color[num_poes];
		new_num_channels = new int[num_poes];
		for(i = 0; i < (num_poes - 1); i++)
		{
			new_individ_tofs[i] = individual_tofs[i];
			new_tof_colors[i] = tof_colors[i];
			new_num_channels[i] = number_channels[i];
		}
		new_individ_tofs[num_poes - 1] = null;
		new_tof_colors[num_poes - 1] = null;
		new_num_channels[num_poes - 1] = 0;


		individual_tofs = new_individ_tofs;
		tof_colors = new_tof_colors;
		number_channels = new_num_channels;
	}

	public void RemovePOEArray(int poe_number){
		float[][][] new_tof_array;
		int[] new_channel_array;
		Color[] new_color_array;
		int i, j;
		num_current_poes--;

		new_tof_array = new float[num_current_poes][][];
		new_channel_array = new int[num_current_poes];
		new_color_array = new Color[num_current_poes];

		for(i = 0; i < num_current_poes; i++)
		{
			if(i < poe_number)
			{
				j = i;
			}
			else
			{
				j = i + 1;
			}
			new_tof_array[i] = individual_tofs[j];
			new_channel_array[i] = number_channels[j];
			new_color_array[i] = tof_colors[j];
		}

		
		individual_tofs = new_tof_array;
		number_channels = new_channel_array;
		tof_colors = new_color_array;
	}

	public void SetDeltaTOFArrays(float[][] delta_tofs_array, int poe_num, float num_amplitude, float energy_inc)
	{
		is_being_changed = true;
		delta_tofs = delta_tofs_array;
		delta_tof_poe_number = poe_num;
		delta_tof_poe_amplitude = num_amplitude;
		if(num_amplitude < 0)
			delta_tof_poe_amplitude = 1.0f;
		energy_increment = energy_inc;
	}

	public void AddOnDeltaTOF(float poe_new_amplitude, boolean is_end_point){
		int i, j, this_number_channels;

		float amplitude_change, divisor;
		float[][] this_poe_tofs = individual_tofs[delta_tof_poe_number];

		float[] this_chan_tofs, this_chan_delta_tofs;
		float add_on;


		if(poe_new_amplitude < 0)   // This will only occur if the TOF is zero everywhere
		{
			divisor = 1.0f;
			if(is_end_point)
			{
				// Factor of 2 is needed due to trapezoid rule type integration
				amplitude_change = (float) (2.0 / energy_increment);
			}
			else
			{
				amplitude_change = (float) (1.0 / energy_increment);
			}
		}
		else
		{
			amplitude_change = (poe_new_amplitude - delta_tof_poe_amplitude);
			if(is_end_point)
			{
				if(amplitude_change > -(2.0 / energy_increment))   // i.e. if the P(E) has not just been set identically to zero
				{
					// Factor of 2 is needed due to trapezoid rule type integration
					divisor = (float) (1.0 / (1.0 + (0.5 * amplitude_change * energy_increment)));
				}
				else
				{
					divisor = 0; // So overall TOF will equal zero
				}
			}
			else
			{
				if(amplitude_change > -(1.0 / energy_increment))   // i.e. if the P(E) has not just been set identically to zero
				{
					divisor = (float) (1.0 / (1.0 + (amplitude_change * energy_increment)));
				}
				else
				{
					divisor = 0; // So overall TOF will equal zero
				}
			}
		}
		this_number_channels = number_channels[delta_tof_poe_number];
		if(this_number_channels == 1)
		{
			this_number_channels = 0;
		}
		for(i = 0; i <= this_number_channels; i++)
		{
			this_chan_tofs = this_poe_tofs[i];
			this_chan_delta_tofs = delta_tofs[i];
			for(j = 0; j <  num_tot_channels; j++)
			{
				add_on = this_chan_delta_tofs[j] * amplitude_change;
				if(i == 0)
					channel_counts[j] -= this_chan_tofs[j];  // Subtract off old P(E) TOF from overall TOF
				this_chan_tofs[j] += add_on;
				this_chan_tofs[j] *= divisor;
				if(i == 0)
					channel_counts[j] += this_chan_tofs[j];
			}
		}
		is_being_changed = false;
		// Delete all the new delta TOFs which were created
	}

	public void SetIsBeingChanged(boolean new_bool)
	{
		if(associated_calc_number == -1)
		{
			is_being_changed = false;
		}
		else
		{
			is_being_changed = new_bool;
		}
	}

	// Implementations to allow calling up of TOF data
	public String GetTitle() {return title;}
	public float GetDwell() {return dwell;}
	public float GetOffset()  {return offset;}

	public float[] GetMaxMinCounts(float starting_time, float ending_time){
		int i, j, k;
		float current_value, max_counts=0, min_counts =0;

		int num_channel_tofs;


		float[][] this_poe_tofs;
		float[] this_channel_tof;
		float this_time_point;

		if((starting_time == 0) && (ending_time == 0))
		{
			starting_time = actual_flight_time_micro[0];
			ending_time = actual_flight_time_micro[num_tot_channels - 1];
		}

		boolean first = true;
		max_counts = 0;
		for(i = 0; i < num_tot_channels; i++)
		{
			this_time_point = actual_flight_time_micro[i];
			if((this_time_point >= starting_time) && (this_time_point <= ending_time))
			{
				current_value = channel_counts[i];
				if(first)
				{
					min_counts = current_value;
					first = false;
				}
				min_counts = Math.min(min_counts, current_value);
				max_counts = Math.max(max_counts, current_value);
			}
		}

		if((starting_time != 0) || (ending_time != 0))
		{
			if((is_real_TOF == false) && (min_counts > 0)) // See if any of the included TOFs have smaller mins
			{
				for(i = 0; i < num_tot_channels; i++)
				{
					this_time_point = actual_flight_time_micro[i];
					if((this_time_point >= starting_time) && (this_time_point <= ending_time))
					{
						for(j = 0; j < num_current_poes; j++)
						{
							this_poe_tofs = individual_tofs[j];
							if(this_poe_tofs != null)
							{
								num_channel_tofs = number_channels[j];
								if(num_channel_tofs == 1)      // Look only at the overall P(E) TOF if only one channel
								{
									this_channel_tof = this_poe_tofs[0];
									current_value = this_channel_tof[i];
									min_counts = Math.min(min_counts, current_value);
								}
								else
								{
									for(k = 1; k <= num_channel_tofs; k++)
									{
										this_channel_tof = this_poe_tofs[k];
										current_value = this_channel_tof[i];
										min_counts = Math.min(min_counts, current_value);
									}
								}
							}
						}
					}
				}
			}
		}
		extrema[0] = max_counts;
		extrema[1] = min_counts;
		return(extrema);
	}

	public int GetTotChannels() {return num_tot_channels;}
	public float[] ChannelCountsPointer() {return channel_counts;}
	public float[] FlightTimePointer() {return tof_flight_time;}
	public float[] RealTimePointer() {return actual_flight_time_micro;}
	public Color GetTOFColor() {return time_of_flight_color;}
	public scale GetDwellScale() {return dwell_scale;}
	public scale GetOffsetScale() {return offset_scale;}
	public float GetIon_m_e() {return ion_m_e;}
	public float GetMass1() {return primary_mass1;}
	public float GetMass2() {return primary_mass2;}
	public float GetLabAngle()  {return lab_angle;}
	public float GetBetaForInversion() {return beta_for_inversion;}
	public float GetPolarizationAngle() {return polarization_angle;}
	public float GetDepolarization()  {return depolarization;}
	public float GetIonFlightTime() {return ion_flight_time;}
	public float GetInversionBeamVel() {return peak_beam_velocity;}

	public float GetIntegratedTotal(float start_time, float end_time){
		int i, lower_point, upper_point;
		float sum, first_time, time_spacing;

		// Use trapeziod rule integration method
		if(end_time < 0)
		{
			sum = (float) ((channel_counts[0] + channel_counts[num_tot_channels - 1]) / 2.0);
			for(i = 1; i < (num_tot_channels - 1); i++)
			{
				sum += channel_counts[i];
			}
		}
		else
		{
			first_time = actual_flight_time_micro[0];
			time_spacing = actual_flight_time_micro[1] - first_time;

			if(start_time < first_time)
			{
				start_time = first_time;
			}
			if(end_time > actual_flight_time_micro[num_tot_channels - 1])
			{
				end_time = actual_flight_time_micro[num_tot_channels - 1];
			}
			// Determine which points correspond to given start and end times
			lower_point = (int) ((start_time - first_time) / time_spacing);
			upper_point = (int) ((end_time - first_time) / time_spacing);
			sum = (float) ((channel_counts[lower_point] + channel_counts[upper_point]) / 2.0);
			for(i = (lower_point + 1); i < (upper_point); i++)
			{
				sum += channel_counts[i];
			}
		}

		return sum;
	}

	public float GetAverageCounts(float time1, float time2){
		int i;
		float lower_time, upper_time;
		float array_starting_time;
		float dwell_in_microsecs;

		float sum;
		float average;

		int lower_point, upper_point;

		lower_time = Math.min(time1, time2);
		upper_time = Math.max(time1, time2);

		array_starting_time = actual_flight_time_micro[0];
		dwell_in_microsecs = actual_flight_time_micro[1] - array_starting_time;

		lower_point = (int)((lower_time - array_starting_time) / dwell_in_microsecs);
		upper_point = (int)((upper_time - array_starting_time) / dwell_in_microsecs);



		if(lower_point < 0)
		{
			lower_point = 0;
		}

		if(upper_point > (num_tot_channels - 1))
		{
			upper_point = num_tot_channels - 1;
		}

		sum = 0;
		for(i = lower_point; i <= upper_point; i++)
		{
			sum += channel_counts[i];
		}
		average = sum / (upper_point - lower_point + 1);


		return average;
	}

	public float[] GetTotalTOF() {return channel_counts;}  // Used for calculated TOFs
	public int GetNumCurrentPOEs() {return num_current_poes;}
	public int[] GetNumChannelsArray() {return number_channels;}
	public float GetMinimumTime() {return actual_flight_time_micro[0];}
	public float GetMaximumTime() {return actual_flight_time_micro[num_tot_channels - 1];}

	public int GetFormat() {return format;}
	public boolean GetLaserPolarized() {return polarized_laser;}
	public int GetIsVisible()	{return is_Visible;} //CHANGE: getter

	// View functions
	public TOFView GetAssociatedView(int index){
		if(index < AssociatedTOFViews.size())
			return AssociatedTOFViews.get(index);
		else
			return null;
		// The view will need to iterate through all of these to determine if this should appear
		// in that view (i.e. if that view matches a view in which the TOF is displayed)
	}
	
	public void AddAssociatedView(TOFView v){
	
		AssociatedTOFViews.add(v);
	}

	void DeleteAssociatedView(TOFView t){
		AssociatedTOFViews.remove(t);
		return;
	}

	public int GetNumAssociatedViews()
	{
		return(AssociatedTOFViews.size());
	}

	// Needed for streaming, TArray as vector formations
	/*public boolean operator ==(const TOF_data other)
	{
		return (other == this);
	}
	*/
	public String out(boolean compat){
		TOF_data tof = this;
		int i, j, k, num_channels;
		float[][] this_poe_indiv_tofs;
		float[] this_chan_indiv_tofs;
		Color this_poe_color;
		POE_calc_data[] detached_calc_data;
		String[] poe_title_array;
		String os = "";
		
		os +=  tof.title + "\n";
		os +=  tof.TOF_num + " "+ tof.lab_angle + " ";
		os += (compat)?((tof.polarized_laser)?1:0):tof.polarized_laser;
		if(tof.polarized_laser)
		{
			os += " " + tof.polarization_angle + " " + tof.depolarization + " ";
		}
		os +=  "\n" + tof.ion_m_e + " " + tof.ion_flight_time + " " + tof.offset + " " ; 
		if(!compat) os += tof.is_Visible; //CHANGE: save format
		os += "\n";
		os +=  tof.dwell + " " + tof.dwell_scale + "\n";
		os +=  tof.offset + " " + tof.offset_scale + "\n";
		os +=  tof.beta_for_inversion + " " + tof.peak_beam_velocity + " ";
		os +=  tof.primary_mass1 + " " + tof.primary_mass2 + " ";
		os +=  "\n" + tof.num_tot_channels;

		for(i = 0; i < tof.num_tot_channels; i++)
		{
			if((i % 10) == 0)
				os +=  "\n";
			os +=  tof.channel_counts[i] + " " + tof.tof_flight_time[i] + " ";
		}

		os +=  "\n" + (int) tof.time_of_flight_color.getRed() + " ";
		os +=  (int) tof.time_of_flight_color.getGreen() + " ";
		os +=  (int) tof.time_of_flight_color.getBlue() + " ";

		os += "\n" + ((compat)?((tof.TOF_has_been_altered)?1:0):tof.TOF_has_been_altered) + " ";

		if(tof.TOF_has_been_altered)
		{
			os +=  tof.num_points_unaltered_tof + " " + tof.unaltered_dwell;
			for(i = 0; i < tof.num_points_unaltered_tof; i++)
			{
				if((i % 20) == 0)
					os +=  "\n";
				os +=  tof.unaltered_TOF_data[i] + " ";
			}
		}

		os += "\n" + ((compat)?((tof.is_real_TOF)?1:0):tof.is_real_TOF) + " ";
		if(!tof.is_real_TOF)
		{
			os +=  tof.associated_calc_number + " ";
			os +=  tof.energy_increment + " " + tof.num_current_poes + " ";
			os +=  tof.number_included_poes + "\n";

			os +=  tof.num_beam_angle_segs + " " + tof.num_det_angle_segs + " ";
			os +=  tof.num_lab_vel_segs + " " + tof.num_ionization_segs + " ";
			os +=  tof.num_beam_vel_segs + "\n";

			os += ((compat)?((tof.is_number_density_calc)?1:0) + " " + ((tof.is_ionization_gaussian)?1:0):tof.is_number_density_calc + " " +tof.is_ionization_gaussian) + " ";
			os +=  tof.depolarization + " " + tof.minimum_lab_vel + " ";
			os +=  tof.maximum_lab_vel + " " + tof.flight_length + " ";
			os +=  tof.beam_angle_width + " " + tof.detector_angle_width + " ";   
			os +=  tof.ionizer_length + " " + tof.beam_vel_alpha + " ";
			os +=  tof.beam_vel_speed_ratio + "\n";

			for(i = 0; i < tof.num_current_poes; i++)
			{
				num_channels = tof.number_channels[i];
				this_poe_indiv_tofs = tof.individual_tofs[i];
				os +=  "\n" + num_channels + " ";
				if(num_channels != 0)
				{
					for(j = 0; j < (num_channels + 1); j++)
					{
						this_chan_indiv_tofs = this_poe_indiv_tofs[j];
						for(k = 0; k < tof.num_tot_channels; k++)
						{
							if((k % 20) == 0)
								os +=  "\n";
							os +=  this_chan_indiv_tofs[k] + " ";
						}
					}
				}
			}

			if(tof.associated_calc_number == -1) //i.e. calc is detached
			{
				// Output POE_calc_data information for detached TOF
				// (this is not necessary if the calculation is still attached,
				// since that POE_calc_data is stored by the calculation and
				// can therefore be passed to the TOF by pointer once it is reloaded
				// to the calculation.)
				os +=  "\n" + tof.calculation_title + "\n";
				os +=  tof.detach_num_poes + " ";
				detached_calc_data = tof.calc_data_for_poes;
				poe_title_array = tof.detach_poe_titles;
				for(i = 0; i < tof.detach_num_poes; i++)
				{
					// Output POE title first
					os +=  "\n" + poe_title_array[i] + "\n";
					// Output POE calc data member
					os +=  detached_calc_data[i].is_included + " ";
					if(detached_calc_data[i].is_included)
					{
						this_poe_color = tof.tof_colors[i];

						os +=  "\n" + detached_calc_data[i].beta_param + " ";
						os +=  detached_calc_data[i].num_channels + " ";
						for(j = 0; j < detached_calc_data[i].num_channels; j++)
						{
							os +=  "\n" + detached_calc_data[i].mass_1[j] + " ";
							os +=  detached_calc_data[i].mass_2[j] + " ";
							os +=  detached_calc_data[i].rel_weight[j] + " ";
							os +=  detached_calc_data[i].mass_ratio[j] + " ";
						}

						os +=  "\n" + (int) this_poe_color.getRed() + " ";
						os +=  (int) this_poe_color.getGreen() + " ";
						os +=  (int) this_poe_color.getBlue() + " ";
					}
				}
			}
		}
		return os;
	}

	public static TOF_data in(Scanner is) {
		int i, j, k, number_of_channels;
		int red, green, blue;
		String title_temp;
		float[] tof_chan_counts, loaded_flight_time;
		int[] num_chan_array;
		float[][][] individ_tofs;
		float[][] this_poe_indiv_tofs;
		float[] this_chan_indiv_tofs;
		String[] poe_titles;
		POE_calc_data[] calc_data_array;
		Color[] temp_color_array;
		TOF_data tof = new TOF_data();

		tof.title = is.nextLine();
		System.out.println("Loading in tof: " + tof.title);
		tof.TOF_num = is.nextInt();
		tof.lab_angle = is.nextFloat();
		if(is.hasNextBoolean()){
			tof.polarized_laser = is.nextBoolean();
		}else{
			tof.polarized_laser = (is.nextInt() == 1) ? true:false;
		}

		if (tof.polarized_laser) {
			tof.polarization_angle = is.nextFloat();
			tof.depolarization = is.nextFloat();
		}
		is.nextLine();

		tof.ion_m_e = is.nextFloat();
		tof.ion_flight_time = is.nextFloat();
		tof.offset = is.nextFloat();
		// CHANGE:add visibilty to open feature
		String temp = is.nextLine();
		if (!temp.equals("")) {
			tof.is_Visible = Integer.parseInt(temp.trim());
		} else {
			tof.is_Visible = -2;
		}
		// END CHANGE
		tof.dwell = is.nextFloat();	
		if(is.hasNextInt()){
			tof.dwell_scale = scale.valueOf(is.nextInt());
		}else{
			tof.dwell_scale = scale.valueOf(is.next());
		}
		is.nextLine();
		tof.offset = is.nextFloat();
		if(is.hasNextInt()){
			tof.offset_scale = scale.valueOf(is.nextInt());
		}else{
			tof.offset_scale = scale.valueOf(is.next());
		}
		is.nextLine();
		tof.beta_for_inversion = is.nextFloat();
		tof.peak_beam_velocity = is.nextFloat();
		tof.primary_mass1 = is.nextFloat();
		tof.primary_mass2 = is.nextFloat();
		is.nextLine();
		number_of_channels = is.nextInt();
		is.nextLine();

		tof.num_tot_channels = number_of_channels;
		tof_chan_counts = new float[number_of_channels];
		loaded_flight_time = new float[number_of_channels];

		for (i = 0; i < number_of_channels; i++) {
			tof_chan_counts[i] = is.nextFloat();
			loaded_flight_time[i] = is.nextFloat();
		}
		is.nextLine();

		tof.channel_counts = tof_chan_counts;
		tof.tof_flight_time = loaded_flight_time;

		red = is.nextInt();
		green = is.nextInt();
		blue = is.nextInt();
		is.nextLine();
		Color color = new Color(red, green, blue);
		tof.time_of_flight_color = color;

		tof.TOF_has_been_altered = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;
		is.nextLine();

		if (tof.TOF_has_been_altered) {
			tof.num_points_unaltered_tof = is.nextInt();
			tof.unaltered_dwell = is.nextFloat();
			is.nextLine();
			tof.unaltered_TOF_data = new float[tof.num_points_unaltered_tof];
			for (i = 0; i < tof.num_points_unaltered_tof; i++) {
				tof.unaltered_TOF_data[i] = is.nextFloat();
				is.nextLine();
			}
		}

		tof.is_real_TOF = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;;
		if (!tof.is_real_TOF) {
			int tempInt = is.nextInt();
			tof.associated_calc_number = tempInt;//(tempInt == -1)? -1: 1000000;
			tof.energy_increment = is.nextFloat();
			tof.num_current_poes = is.nextInt();
			tof.number_included_poes = is.nextInt();
			is.nextLine();

			tof.num_beam_angle_segs = is.nextInt();
			tof.num_det_angle_segs = is.nextInt();
			tof.num_lab_vel_segs = is.nextInt();
			tof.num_ionization_segs = is.nextInt();
			tof.num_beam_vel_segs = is.nextInt();
			is.nextLine();

			tof.is_number_density_calc = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;;
			tof.is_ionization_gaussian = (is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false;;
			tof.depolarization = is.nextFloat();
			tof.minimum_lab_vel = is.nextFloat();
			tof.maximum_lab_vel = is.nextFloat();
			tof.flight_length = is.nextFloat();
			tof.beam_angle_width = is.nextFloat();
			tof.detector_angle_width = is.nextFloat();
			tof.ionizer_length = is.nextFloat();
			tof.beam_vel_alpha = is.nextFloat();
			tof.beam_vel_speed_ratio = is.nextFloat();
			is.nextLine();

			num_chan_array = new int[tof.num_current_poes];
			individ_tofs = new float[tof.num_current_poes][][];
			for (i = 0; i < tof.num_current_poes; i++) {
				num_chan_array[i] = is.nextInt();
				number_of_channels = num_chan_array[i];
				if (number_of_channels != 0) {
					individ_tofs[i] = new float[number_of_channels + 1][];
					this_poe_indiv_tofs = individ_tofs[i];

					for (j = 0; j < (number_of_channels + 1); j++) {
						this_poe_indiv_tofs[j] = new float[tof.num_tot_channels];
						this_chan_indiv_tofs = this_poe_indiv_tofs[j];
						for (k = 0; k < tof.num_tot_channels; k++) {
							this_chan_indiv_tofs[k] = is.nextFloat();
						}
					}
				} else {
					individ_tofs[i] = null;
				}
			}
			tof.individual_tofs = individ_tofs;
			tof.number_channels = num_chan_array;

			if (tof.associated_calc_number == -1) // i.e. calc is detached
			{
				// Output POE_calc_data information for detached TOF
				// (this is not necessary if the calculation is still attached,
				// since that POE_calc_data is stored by the calculation and
				// can therefore be passed to the TOF by pointer once it is
				// reloaded
				// to the calculation.)
				is.nextLine();
				tof.SetCalcTitle(is.nextLine());
				System.out.println("Loading in detached tof: " + tof.GetCalcTitle());

				tof.detach_num_poes = is.nextInt();
				poe_titles = new String[tof.detach_num_poes];

				calc_data_array = new POE_calc_data[tof.detach_num_poes];
				temp_color_array = new Color[tof.detach_num_poes];

				for (i = 0; i < tof.detach_num_poes; i++) {
					// Get POE Title
					is.nextLine();
					title_temp = is.nextLine();

					poe_titles[i] = title_temp;
					//System.out.println("title: " +title_temp);
					calc_data_array[i] = new POE_calc_data();
					calc_data_array[i].is_included = (is.hasNextBoolean()) ? is.nextBoolean(): ((is.nextInt() == 1) ? true:false);
					if (calc_data_array[i].is_included) {
						calc_data_array[i].beta_param = is.nextFloat();
						calc_data_array[i].num_channels = is.nextInt();
						number_of_channels = calc_data_array[i].num_channels;
						calc_data_array[i].mass_1 = new float[number_of_channels];
						calc_data_array[i].mass_2 = new float[number_of_channels];
						calc_data_array[i].rel_weight = new float[number_of_channels];
						calc_data_array[i].mass_ratio = new float[number_of_channels];

						for (j = 0; j < number_of_channels; j++) {
							is.nextLine();
							calc_data_array[i].mass_1[j] = is.nextFloat();
							calc_data_array[i].mass_2[j] = is.nextFloat();
							calc_data_array[i].rel_weight[j] = is.nextFloat();
							calc_data_array[i].mass_ratio[j] = is.nextFloat();
						}
						is.nextLine();
						red = is.nextInt();
						green = is.nextInt();
						blue = is.nextInt();
						temp_color_array[i] = new Color(red, green, blue);
						
						//is.nextLine();
					} else {
						calc_data_array[i].mass_1 = null;
						calc_data_array[i].mass_2 = null;
						calc_data_array[i].rel_weight = null;
						calc_data_array[i].mass_ratio = null;
						temp_color_array[i] = null;
					}
				}
				tof.calc_data_for_poes = calc_data_array;
				tof.tof_colors = temp_color_array;
				tof.detach_poe_titles = poe_titles;
			}
		}
		is.nextLine(); // Gets any characters at end of tof.
		return tof;
	}

	// Get functions particular to calculated TOF
	public boolean GetIsRealTOF() {return is_real_TOF;}
	public float[][][] GetTOFPointers() {return individual_tofs;}
	public Color[] GetTOFColors() {return tof_colors;}
	public int GetNumIncludedPOEs() {return number_included_poes;}
	public int GetAssociatedCalc() {return associated_calc_number;}

	public boolean GetIsBeingChanged()
	{
		if(associated_calc_number == -1)
		{
			is_being_changed = false;
		}
		return is_being_changed;
	}
	public float[][] GetDeltaTOFArrays() {return delta_tofs;}
	public int GetWhichPOE()  {return delta_tof_poe_number;}
	public float GetDeltaAmplitude() {return delta_tof_poe_amplitude;}


	public void AverageTOFData(int num_averaging_points){
		float[] newly_smoothed_tof;
		int i, j, count, point_num, num_new_points;
		float sum;


		num_new_points = num_tot_channels / num_averaging_points;
		if((num_tot_channels % num_averaging_points) != 0)
		{
			num_new_points++;
		}

		newly_smoothed_tof = new float[num_new_points];

		for(i = 0; i < num_new_points; i++)
		{
			sum = 0.0f;
			count = 0;
			for(j = 0; j < num_averaging_points; j++)
			{
				point_num = num_averaging_points * i + j;
				if(point_num < num_tot_channels)
				{
					sum += channel_counts[point_num];
					count++;
				}
			}
			newly_smoothed_tof[i] = sum / count;
		}


		// Delete oldest data and store old data
		if(TOF_has_been_altered)
		{
		}
		else
		{
			unaltered_TOF_data = channel_counts;
			num_points_unaltered_tof = num_tot_channels;
			unaltered_dwell = dwell;
		}

		num_tot_channels = num_new_points;
		channel_counts = newly_smoothed_tof;
		dwell = num_averaging_points;

		SetRealFlightTime();
		TOF_has_been_altered = true;
	}
	
	public void RemoveBackground(float time1, float time2){
		float[] new_tof;
		int i;
		float subtract_value;


		new_tof = new float[num_tot_channels];
		subtract_value = GetAverageCounts(time1, time2);

		for(i = 0; i < num_tot_channels; i++)
		{
			new_tof[i] = channel_counts[i] - subtract_value;
		}


		// Delete oldest data and store old data
		if(TOF_has_been_altered)
		{
		}
		else
		{
			unaltered_TOF_data = channel_counts;
			num_points_unaltered_tof = num_tot_channels;
			unaltered_dwell = dwell;
		}

		channel_counts = new_tof;
		TOF_has_been_altered = true;
	}

	public void RestoreTOFData(){
		channel_counts = unaltered_TOF_data;
		dwell = unaltered_dwell;
		num_tot_channels = num_points_unaltered_tof;

		unaltered_TOF_data = null;
		num_points_unaltered_tof = -1;
		unaltered_dwell = 0;
		TOF_has_been_altered = false;
		SetRealFlightTime();
	}
	
	public boolean GetIsAltered()   {return TOF_has_been_altered;}

	public void SetTOFNum(int number)  {TOF_num = number;}
	public int GetTOFNum()  {return TOF_num;}


}
