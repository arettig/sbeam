package sbeam;

import java.awt.Color;

public class Resid_data {

	private TOF_data[] resid_tofs;
	private int num_resid_points;  // Will equal number of points of 1st TOF
	private int[] view_tof_nums;  // Stores TOF #'s so TOFView will know which resid is needed
	private int[] actual_tof_nums;

	private float[] extrema = new float[2];
	private float[] resid_array, time_array, scaled_tof_counts0, scaled_tof_counts1;
	private float chi_squared;
	private  ResidView AssociatedResidViewNumber;
	protected TOFView AssociatedTOFViewNumber;
	private String title;
	private Color color;


	public Resid_data() {
		// TODO Auto-generated constructor stub
		resid_array = null;
		color = new Color(0, 0, 0);
	}

	public Resid_data(TOF_data[] two_tofs){
		title = "";
		time_array = null;

		title = "Residual:  " + two_tofs[0].GetTitle() + " minus " + two_tofs[1].GetTitle();

		resid_tofs = two_tofs;

		// Store number of points in the first TOF
		num_resid_points = resid_tofs[0].GetTotChannels();
		resid_array = new float[num_resid_points];
		scaled_tof_counts0 = new float[num_resid_points];
		scaled_tof_counts1 = new float[num_resid_points];


		color = new Color(0, 0, 0);
	}
	

	public ResidView GetAssociatedResidView()  {return AssociatedResidViewNumber;}
	public void SetAssociatedResidView( ResidView view_number)  {AssociatedResidViewNumber = view_number;}

	public TOFView GetAssociatedTOFView()  {return AssociatedTOFViewNumber;}
	public void SetAssociatedTOFView( TOFView view_number)  {AssociatedTOFViewNumber = view_number;}



	public void ResetTOFs(TOF_data[] two_tofs)   {resid_tofs = two_tofs;}
	public TOF_data[] GetTOFs()  {return resid_tofs;}
	public String GetTitle()  {return title;}
	public void SetScalingParams(float slope0, float intercept0, float slope1, float intercept1){
		// Run through each point in 1st TOF_data, interpolate a value for that time
		// in the second TOF_data, and find the difference between the two scaled values

		// Uses scaled_value0 = slope0 * actual_value0 + intercept0 and
		//       scaled_value1 = slope1 * actual_value1 + intercept1,
		// and finds the difference and the sum of the square differences
		int i;
		TOF_data tof0, tof1;

		float[] time_array0, time_array1, count_array0, count_array1;
		int num_points0, num_points1;
		float float_point, lo_value, time, interpolated_counts, difference;
		float start_time1, end_time1, time_spacing1;
		int int_point;

		if(title != null)   // title will be NULL if residual has self-destructed
		{
			tof0 = resid_tofs[0];
			tof1 = resid_tofs[1];

			time_array0 = tof0.RealTimePointer();
			time_array1 = tof1.RealTimePointer();



			if(tof0.GetIsRealTOF())
				count_array0 = tof0.ChannelCountsPointer();
				else
					count_array0 = tof0.GetTotalTOF();

					if(tof1.GetIsRealTOF())
						count_array1 = tof1.ChannelCountsPointer();
						else
							count_array1 = tof1.GetTotalTOF();


							num_points0 = tof0.GetTotChannels();
							num_points1 = tof1.GetTotChannels();

							num_resid_points = num_points0;

							
							resid_array = new float[num_resid_points];
							scaled_tof_counts0 = new float[num_resid_points];
							scaled_tof_counts1 = new float[num_resid_points];

							time_array = new float[num_points0];


							start_time1 = time_array1[0];
							end_time1 = time_array1[num_points1 - 1];

							time_spacing1 = (end_time1 - start_time1) / (num_points1 - 1);

							chi_squared = 0.0f;
							for(i = 0; i < num_points0; i++)
							{
								scaled_tof_counts0[i] = slope0 * count_array0[i] + intercept0;
								time = time_array0[i];
								time_array[i] = time;


								float_point = (time - start_time1) / time_spacing1;
								int_point = (int) float_point;

								lo_value = count_array1[int_point];


								if((int_point < num_points1) && (int_point >= 0))
								{
									interpolated_counts = lo_value + (float_point - int_point) * (count_array1[int_point + 1] - lo_value);
								}
								else
								{
									interpolated_counts = 0;
								}

								scaled_tof_counts1[i] = slope1 * interpolated_counts + intercept1;



								difference = scaled_tof_counts0[i] - scaled_tof_counts1[i];
								resid_array[i] = difference;
								chi_squared += (difference * difference);
							}
		}
	}

	public float[] GetMaxMinValues(float starting_time, float ending_time){
		int i;
		float this_time, this_resid;
		float temp_max = 0, temp_min = 0;
		float abs_max;
		boolean first = true;


		for(i = 0; i < num_resid_points; i++)
		{
			this_time = time_array[i];
			this_resid = resid_array[i];
			if((this_time >= starting_time) && (this_time <= ending_time))
			{
				if(first)
				{
					temp_min = this_resid;
					temp_max = this_resid;
					first = false;
				}
				else
				{
					temp_min = Math.min(temp_min, this_resid);
					temp_max = Math.max(temp_max, this_resid);
				}
			}
		}

		abs_max = Math.max(Math.abs(temp_min), temp_max);
		extrema[0] = abs_max;
		extrema[1] = -abs_max;
		return extrema;
	}

	public Color GetResidColor()  {return color;}
	public void SelfDestruct(){
		   resid_tofs = null;

		   resid_array = null;

		   scaled_tof_counts0 = null;

		   scaled_tof_counts1 = null;

		   time_array = null;

		   title = null;
	}
	
	public void SetResidColor(Color new_color)
	{
		color = new_color;
	}

	public void SetViewTOFNums(int[] new_pair)  {view_tof_nums = new_pair;}
	public void SetActualTOFNums(int[] new_pair)  {actual_tof_nums = new_pair;}
	public float GetChiSquared()  {return chi_squared;}

	public float[] GetResidArray()  {return resid_array;}
	public float[] GetTimeArray()  {return time_array;}
	public int GetNumResidPoints()  {return num_resid_points;}
	public int[] GetViewTOFNums()  {return view_tof_nums;}
	public int[] GetActualTOFNums()  {return actual_tof_nums;}

	/*public boolean operator ==(const Resid_data& other)
    {
    	return (&other == this);
    }    
    friend ostream& operator << (ostream& os, const Ang_data& ang);
    friend istream& operator >> (istream& is, Ang_data& tof);*/


}
