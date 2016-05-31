package sbeam;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class Ang_data {


	private int[] included_TOFNums;
	private int num_included_tofs;
	private float[] extrema = new float[2];
	private float[] integrated_TOF_array, angle_array;
	private ArrayList<AngView> AssociatedAngViews;
	private String title;
	private boolean is_vs_lab_angle;
	private Color color;

	private float tof_start_time, tof_end_time;


	public Ang_data() {
		// TODO Auto-generated constructor stub
		integrated_TOF_array = null;
		angle_array = null;
		included_TOFNums = null;
		AssociatedAngViews = new ArrayList<AngView>();
		color = new Color(0, 0, 0);
	}


	public Ang_data(boolean ang_dist, int ang_number, float start_time, float end_time)
	{
		String temp_string;
		is_vs_lab_angle = ang_dist;
		title = "";

		tof_start_time = start_time;
		tof_end_time = end_time;

		if(is_vs_lab_angle)
		{
			title = "Lab angular distribution #";
		}
		else
		{
			title = "Polarization angular distribution #";
		}

		temp_string = "" + ang_number;
		title += temp_string;

		if(end_time < 0)
		{
			title += ", Entire TOF Range";
		}
		else
		{
			title += ", TOF Range:  ";
			title += "" + start_time + " to " + end_time + " µs";
		}
		integrated_TOF_array = null;
		angle_array = null;
		included_TOFNums = null;
		AssociatedAngViews = new ArrayList<AngView>();
		color = new Color(0, 0, 0);
	}

	public void SetIncludedTOFs(int num_tofs, int[] inc_tofs)
	{
		num_included_tofs = num_tofs;
		included_TOFNums = inc_tofs;

		if(num_tofs == 0)
		{
			integrated_TOF_array = null;

			angle_array = null;
		}
	}

	public void SetAngleArray(float[] new_array)
	{
		angle_array = new_array;
	}
	public String GetTitle()  {return title;}
	public int[] GetIncludedTOFNums()  {return included_TOFNums;}

	public void FillIntegratedTOFArray(float[] new_array){
		integrated_TOF_array = new_array;
	}

	public float[] GetMaxMinValues(float starting_angle, float ending_angle){
		int i;
		float current_value;
		float max_value, min_value = 0;
		boolean first = true;
		max_value = 0;
		for(i = 0; i < num_included_tofs; i++)
		{
			if((angle_array[i] >= starting_angle) && (angle_array[i] <= ending_angle))
			{
				current_value = integrated_TOF_array[i];
				if(first)
				{
					min_value = current_value;
					first = false;
				}
				min_value = Math.min(min_value, current_value);
				max_value = Math.max(max_value, current_value);
			}
			if(angle_array[i] > ending_angle)
			{
				extrema[0] = max_value;
				extrema[1] = min_value;
				return(extrema);
			}
		}
		extrema[0] = max_value;
		extrema[1] = min_value;
		return(extrema);
	}

	public float[] GetIntegratedTOFArray()  {return integrated_TOF_array;}
	public float[] GetAngleArray() {return angle_array;}
	public int GetNumTOFs()  {return num_included_tofs;}
	public Color GetAngColor()  {return color;}

	public void RemoveTOF(int TOF_number){
		int i;

		for(i = 0; i < num_included_tofs; i++)
		{
			if(TOF_number == included_TOFNums[i])
			{
				included_TOFNums[i] = -1;
			}
		}
	}

	public void SortArrays(){
		// Uses a "bubble sort" to arrange angles and the corresponding TOF numbers, etc, in order
	 	int i, passes;
	   boolean exchange = true;

	   float temp_angle, temp_integrated_tof;
	   int temp_tof_num;

	   passes = 1;

	   System.out.println(Arrays.toString(integrated_TOF_array));
	   while((passes < num_included_tofs) && (exchange == true))
	   {
	     exchange = false;
	     for(i = 0; i < (num_included_tofs - passes); i++)
	     {
	       if(angle_array[i] > angle_array[i + 1])
	       {
	         temp_angle = angle_array[i];
	         temp_integrated_tof = integrated_TOF_array[i];
	         temp_tof_num = included_TOFNums[i];

	         // Exchange two adjacent elements in each of the arrays
	         angle_array[i] = angle_array[i + 1];
	         angle_array[i + 1] = temp_angle;
	         integrated_TOF_array[i] = integrated_TOF_array[i + 1];
	         integrated_TOF_array[i + 1] = temp_integrated_tof;
	         included_TOFNums[i] = included_TOFNums[i + 1];
	         included_TOFNums[i + 1] = temp_tof_num;

	         exchange = true;
	       }
	     }
	     passes++;
	   };
	}
	
	public void SetAngColor(Color new_color)   {color = new_color;}

	//public boolean operator ==(const Ang_data other)  {return (other == this);}
	
	/*public ostream operator << (ostream os, Ang_data ang){
		int i;

	  	os << tof.title << "\n";
	   os << tof.lab_angle << "\n" << tof.polarized_laser << " ";
	   if(tof.polarized_laser)  {
	   	os << tof.polarization_angle << " " << tof.depolarization << " ";
	   }
	   os << "\n" << tof.ion_m_e << " " << tof.ion_flight_time << " " << tof.offset << "\n";
	   os << tof.dwell << " " << tof.dwell_scale << "\n";
	   os << tof.beta_for_inversion << " ";
	   os << tof.primary_mass1 << " ";
	   os << tof.primary_mass2 << " ";
	   os << "\n" << tof.num_tot_channels << "\n";

	   for(i = 0; i < tof.num_tot_channels; i++)  {
	   	os << tof.channel_counts[i] << "     " << tof.tof_flight_time[i] << "\n";
	   }
	   return os;
	}
	public istream operator >> (istream is, Ang_data tof){
		/* int i, number_of_channels;
		   char title_temp[200];
		 	float* tof_chan_counts;
			float* loaded_flight_time;


			is.getline(title_temp, 200);
		   if(tof.title != 0)
		   	delete tof.title;
		   tof.title = new char[200];
		   strcpy(tof.title, title_temp);

		   is >> tof.lab_angle >> tof.polarized_laser;
		   if(tof.polarized_laser)  {
		   	is >> tof.polarization_angle >> tof.depolarization;
		   }
		   is >> tof.ion_m_e >> tof.ion_flight_time >> tof.offset;
		   is >> tof.dwell >> (int) tof.dwell_scale;
		   is >> tof.beta_for_inversion;
			is >> tof.primary_mass1;
		   is >> tof.primary_mass2;
		   is >> number_of_channels;

		   tof.num_tot_channels = number_of_channels;
		 	tof_chan_counts = new float[number_of_channels];
		 	loaded_flight_time = new float[number_of_channels];

		   for(i = 0; i < tof.num_tot_channels; i++)  {
		   	is >> tof_chan_counts[i] >> loaded_flight_time[i];
		   }

		   if(tof.channel_counts != 0)
		   	delete tof.channel_counts;
		   if(tof.tof_flight_time != 0)
		   	delete tof.tof_flight_time;

		   tof.channel_counts = new float[number_of_channels];
		   tof.tof_flight_time = new float[number_of_channels];

		   tof.channel_counts = tof_chan_counts;
		   tof.tof_flight_time = loaded_flight_time;

		   tof_chan_counts = 0;
		   loaded_flight_time = 0;

		   delete tof_chan_counts;
		   delete loaded_flight_time;
		 	return is;   
		   return is;
	}*/

	public AngView GetAssociatedView( int index){
		if(index < AssociatedAngViews.size())
			return AssociatedAngViews.get(index);
		else
			return null;
	}

	public int AddAssociatedView(AngView a){
		int index = AssociatedAngViews.size();
		AssociatedAngViews.add(a);
		return index;
	}
	
	public void DeleteAssociatedView(AngView v){
		AssociatedAngViews.remove(v);
		   return;
	}
	
	public int GetNumAssociatedViews()
	{
		return(AssociatedAngViews.size());
	}

	public boolean GetIsVsLabAngle()  {return is_vs_lab_angle;}

	public float GetStartTime()  {return tof_start_time;}
	public float GetEndTime()  {return tof_end_time;}

	public void SetStartTime(float time)  {tof_start_time = time;}
	public void SetEndTime(float time)  {tof_end_time = time;}

	/*public void SaveAsAngFile(ofstream os){
		int i;

	  	os << title << "\n\n";
	   os << num_included_tofs << "\n";
	   os << min_energy << "     " << max_energy << "\n\n";

	   // Output the three components of the energy distributions color
	   os << (unsigned int) poe_color->Red() << "     ";
	   os << (unsigned int) poe_color->Green() << "     ";
	   os << (unsigned int) poe_color->Blue() << "\n\n";


	   for(i = 0; i < num_points; i++)  {
	   	os << poe_amplitudes[i] << "  \t";
	      if((((i + 1) % 5) == 0) || (i == num_points - 1))
	      	os << "\n";
	   }    
	}
	public void LoadAngFile(ifstream is){
		int i;
		   unsigned int red, green, blue;
		   char title_temp[200];

			is.getline(title_temp, 200);
		   if(title != 0)
		   	delete title;
			title = new char[strlen(title_temp) + 1];
		   strcpy(title, title_temp);

		   is >> num_points;
		   is >> min_energy >> max_energy;

		   is >> red >> green >> blue;

		 	poe_amplitudes = new float[num_points];
		 	energy_values = new float[num_points];

		   energy_spacing = (max_energy - min_energy) / (num_points - 1);


		   for(i = 0; i < num_points; i++)
		   {
		   	is >> poe_amplitudes[i];
		      energy_values[i] = min_energy + i*energy_spacing;
		   }

		   poe_color = new Color(red, green, blue);  
	}*/

}
