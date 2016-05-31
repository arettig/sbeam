package sbeam;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class POE_data {

	protected int num_points;
	protected float min_energy, max_energy;

	protected int is_Visible; //CHANGE: -2 = not visible, -1 = visible, 0+ = appended to viewnumber of value

	protected int POE_num;

	protected String title;
	protected float[] poe_amplitudes, energy_values;

	protected float energy_spacing, average_energy;
	protected float[] extrema = new float[2];

	protected ArrayList<POEView> AssociatedPOEViews;
	protected ArrayList<Calc_data> AssociatedCalcs;
	protected Color poe_color;

	
	public POE_data() {
		// TODO Auto-generated constructor stub
		num_points = 100;
		min_energy = 0;
		max_energy = 200;

		average_energy = 0.0f;

		AssociatedPOEViews = new ArrayList<POEView>();
		AssociatedCalcs = new ArrayList<Calc_data>();
		title = null;
		poe_color = new Color(0,0,0);
		poe_amplitudes = null;
		energy_values = null;
		energy_spacing = 0;

		is_Visible = -2;	//CHANGE: default value
	}

	public void DeletePOEDataInfo(){
	
	}

	public void SetIsVisible(int vis)	{is_Visible = vis;}	//CHANGE: setter
	public int GetIsVisible()	{return is_Visible;} //CHANGE: getter

	public void SaveAsPOEFile(BufferedWriter os) throws IOException{
		int i;

		os.write(title + "\n\n");
		os.write(num_points + "\n");
		os.write(min_energy + "     " + max_energy + "\n\n");

		// Output the three components of the energy distributions color
		os.write((int) poe_color.getRed() + "     ");
		os.write((int) poe_color.getGreen() + "     ");
		os.write((int) poe_color.getBlue() + "\n\n");

		for (i = 0; i < num_points; i++) {
			os.write(poe_amplitudes[i] + "  \t");
			if ((((i + 1) % 5) == 0) || (i == num_points - 1))
				os.write("\n");
		}
		//return os;
	}
	
	public void LoadPOEFile(Scanner is){
		int i;
		int red, green, blue;
		String title_temp;

		title_temp = is.nextLine();
		is.nextLine();
		title = title_temp;

		num_points = is.nextInt();
		is.nextLine();
		min_energy = is.nextFloat();
		max_energy = is.nextFloat();
		is.nextLine();
		is.nextLine();

		red = is.nextInt();
		green = is.nextInt();
		blue = is.nextInt();
		is.nextLine();
		is.nextLine();

		poe_amplitudes = new float[num_points];
		energy_values = new float[num_points];

		energy_spacing = (max_energy - min_energy) / (num_points - 1);

		for (i = 0; i < num_points; i++) {
			poe_amplitudes[i] = is.nextFloat();
			if ((((i + 1) % 5) == 0) || (i == num_points - 1)) {
				is.nextLine();
			}
			energy_values[i] = min_energy + i * energy_spacing;
		}

		poe_color = new Color(red, green, blue);
	}

	public void SetTitle(String new_title)
	{
		title = new_title;
	}
	public void SetTotNumPoints(int new_value)  {num_points = new_value;}
	public void SetMinimumEnergy(float new_value)  {min_energy = new_value;}
	public void SetMaximumEnergy(float new_value)  {max_energy = new_value;}

	public void SetPOEPointer(float[] array)
	{
		poe_amplitudes = array;
	}

	public  void SetEnergyPointer(float[] array)
	{
		energy_values = array;
		energy_spacing = array[1] - array[0];
	}
	public void SetPOEColor(Color new_color)
	{
		poe_color = new_color;
	}
	public String GetTitle() {return title;}
	public int GetTotNumPoints()  {return num_points;}
	public float GetMinimumEnergy()  {return min_energy;}
	public float GetMaximumEnergy()  {return max_energy;}

	public  float[] GetPOEPointer()  {return poe_amplitudes;}
	public float[] GetEnergyPointer() {return energy_values;}

	public float GetEnergySpacing() {return energy_spacing;}

	public float[] GetMaxMinAmplitude(float starting_energy, float ending_energy){
		int i;
		float current_value;
		float max_amplitude=0, min_amplitude=0;
		boolean first = true;
		max_amplitude = 0;
		for(i = 0; i < num_points; i++)
		{
			if((energy_values[i] >= starting_energy) && (energy_values[i] <= ending_energy))
			{
				current_value = poe_amplitudes[i];
				if(first) {
					min_amplitude = current_value;
					first = false;
				}
				min_amplitude = Math.min(min_amplitude, current_value);
				max_amplitude = Math.max(max_amplitude, current_value);
			}
			if(energy_values[i] > ending_energy)
			{
				extrema[0] = max_amplitude;
				extrema[1] = min_amplitude;
				return(extrema);
			}
		}
		extrema[0] = max_amplitude;
		extrema[1] = min_amplitude;
		return(extrema);
	}
	
	public Color GetPOEColor() {return poe_color;}

	public  float NormalizePOE(float[] energy_increment){
		int i;
		float poe_integral, e_avg_integral;
	   float min_e_amplitude = poe_amplitudes[0];
	   float max_e_amplitude = poe_amplitudes[num_points - 1];
	   /*if(energy_increment != 0)
	   {
	     *energy_increment = (max_energy - min_energy) / (num_points - 1);
	   }      */

	   // Use trapezoid rule to perform integrations
	   poe_integral = 0.5f * (min_e_amplitude + max_e_amplitude);
	   e_avg_integral = 0.5f * (min_energy * min_e_amplitude + max_energy * max_e_amplitude);

	   for(i = 1; i < (num_points - 1); i++)
	   {
	   	poe_integral += poe_amplitudes[i];
			e_avg_integral += energy_values[i] * poe_amplitudes[i];
	   }

	   poe_integral *= energy_spacing;
	   e_avg_integral *= energy_spacing;

	   if(poe_integral != 0)
	   {
	    for(i = 0; i < num_points; i++)
	    {
	   	 poe_amplitudes[i] /= poe_integral;
	    }

	    average_energy = (e_avg_integral / poe_integral);
	   }
	   else
	   {
	   	average_energy = 0;
	   }
	   return average_energy;
	}


	// View functions
	public POEView GetAssociatedView(int index){
		// Add a statement here regarding whether this POE is associated with any views, etc.
		// (maybe!)
		if(index < AssociatedPOEViews.size())
			return AssociatedPOEViews.get(index);
		else
			return null;
		// The view will need to iterate through all of these to determine if this should appear
		// in that view (i.e. if that view matches a view in which the POE is displayed)
	}
	public int AddAssociatedView(POEView p){
		int index;
		index = AssociatedPOEViews.size();
		AssociatedPOEViews.add(p);
		return index;
	}

	public void DeleteAssociatedView(POEView p){
		AssociatedPOEViews.remove(p);
		return;
	}
	
	public int GetNumAssociatedViews()
	{
		return(AssociatedPOEViews.size());
	}

	// Calc functions
	public Calc_data GetAssociatedCalc(int index){
		// Add a statement here regarding whether this POE is associated with any views, etc.
		// (maybe!)
		if(index < AssociatedCalcs.size())
			return AssociatedCalcs.get(index);
		else
			return null;
		// The calculation may also need to iterate through all of these to determine if this should
		// be included in that calc (i.e. if that calc matches a calc in which the POE is used)
	}

	public int AddAssociatedCalc(Calc_data c){
		int index;
		index = AssociatedCalcs.size();
		AssociatedCalcs.add(c);
		return index;
	}

	public void DeleteAssociatedCalc(int index){
		AssociatedCalcs.remove((float) index);
		return;
	}

	public int GetNumAssociatedCalcs()
	{
		return(AssociatedCalcs.size());
	}


	// Needed for any class which will be used in TArrayAsVector
	/*public bool operator ==(const POE_data& other)
			{
		return (other == this);
			}*/
	
	public String out(boolean compat){
		int i;
		String returner = "";

		returner += this.title + "\n\n";
		returner += this.POE_num + "\n";
		returner += this.num_points + "     "+ this.min_energy + "     " + this.max_energy + "     ";
		if(!compat) returner += this.is_Visible; //CHANGE: visibilty in save file
		returner += "\n\n";

		// Output the three components of the energy distributions color
		returner += (int) this.poe_color.getRed() + "     ";
		returner += (int) this.poe_color.getGreen() + "     ";
		returner += (int) this.poe_color.getBlue() + "\n\n";


		for(i = 0; i < this.num_points; i++)  {
			returner += this.poe_amplitudes[i] + "  \t";
			if((((i + 1) % 20) == 0) || (i == this.num_points - 1))
				returner += "\n";
		}
		
		return returner;
	}
	public static POE_data in(Scanner is) {
		int i, number_of_points;
		float energy_increment;
		int red, green, blue;
		String title_temp;
		float[] amplitude;
		float[] energy;
		float minimum_energy, maximum_energy;
		POE_data poe = new POE_data();

		poe.title = is.nextLine();
		is.nextLine();

		poe.POE_num = is.nextInt();
		is.nextLine();

		number_of_points = is.nextInt();
		minimum_energy = is.nextFloat();
		maximum_energy = is.nextFloat();
		// CHANGE:open feature
		String temp = is.nextLine();
		if (!temp.equals("")) {
			poe.is_Visible = Integer.parseInt(temp.trim());
		} else {
			poe.is_Visible = -2;
		}
		System.out.println("Visibility: " + poe.is_Visible);
		// END CHANGE
		is.nextLine();

		poe.num_points = number_of_points;
		poe.min_energy = minimum_energy;
		poe.max_energy = maximum_energy;

		red = is.nextInt();
		green = is.nextInt();
		blue = is.nextInt();
		is.nextLine();
		is.nextLine();

		amplitude = new float[number_of_points];
		energy = new float[number_of_points];

		energy_increment = (maximum_energy - minimum_energy)
				/ (number_of_points - 1);

		for (i = 0; i < number_of_points; i++) {
			amplitude[i] = is.nextFloat();
			energy[i] = minimum_energy + i * energy_increment;
		}

		Color color = new Color(red, green, blue);
		poe.poe_color = color;
		poe.poe_amplitudes = amplitude;
		poe.energy_values = energy;
		poe.energy_spacing = energy_increment;
		is.nextLine(); // Gets last '/n' in POE

		return poe;
	}

			// POE_num needed to identify this P(E) from others
	public void SetPOENum(int number)  {POE_num = number;}
	public int GetPOENum()  {return POE_num;}

}
