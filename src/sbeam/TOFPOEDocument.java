package sbeam;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JLabel;

public class TOFPOEDocument {

	protected ArrayList<TOF_data> tofs;      // An array of TOF's; can be updated when new ones added
	protected ArrayList<POE_data> poes;      // An array of POE's; can be updated when new ones added
	protected ArrayList<Calc_data> calcs;     // An array of Calcs; can be updated when new ones added
	protected ArrayList<Ang_data> angs;     // And array of angular data; can be updated when new ones are added
	protected ArrayList<Resid_data> resids;

	protected int ViewNumber, AngNumber;
	protected int current_tof_number, current_poe_number;


	protected TOF_data[][] calculation_tofs;
	// Instrumental parameter data; used for all objects in this document!
	protected float ion_flight_const;
	protected float ionizer_length;
	protected float flight_length;
	protected float beam_ang_width;
	protected float detector_ang_width;
	protected int moving_poe_num, energy_unit;

	protected float conversion_factor;

	protected boolean is_number_density;
	protected boolean DocumentHasChanged;

	protected JLabel x_pos_gadget;
	protected JLabel y_pos_gadget;
	protected JLabel sum_square_gadget;
	protected boolean dirty;
	protected String saveLoc;

	public TOFPOEDocument() {
		// TODO Auto-generated constructor stub
		tofs = new ArrayList<TOF_data>();
		poes = new ArrayList<POE_data>();
		calcs = new ArrayList<Calc_data>();
		angs = new ArrayList<Ang_data>();
		resids = new ArrayList<Resid_data>();
		ViewNumber = 0;
		AngNumber = 0;
		current_tof_number = 0;
		current_poe_number = 0;

		calculation_tofs= null;

		is_number_density = true;

		ion_flight_const = 0.0f;
		ionizer_length = 0.0f;
		flight_length = 0.0f;
		beam_ang_width = 0.0f;
		detector_ang_width = 0.0f;
		energy_unit = 0;

		DocumentHasChanged = true;  // Used now to test save function!!!!!!!!!!!!!!!!!!!!
	}

	public boolean Open(int mode, String path){
		   return true;
	}
	
	public boolean Close(){
		   return true;
	}
	
	public boolean IsOpen(){
		return(tofs != null);
	}

	public boolean Commit(boolean force){
		// Nothing just yet!
		   return true;
	}
	
	public boolean Revert(boolean clear){
		// Nothing just yet!
		   return true;
	}

	public TOF_data GetTOFData(int index){
		//if(!IsOpen() && !Open(ofRead | ofWrite))
			//return null;

		// If a TOF exists for this index, return the address of the indexed TOF
		return index < tofs.size() ? tofs.get(index) : null;
	}

	public POE_data GetPOEData(int index){
		//if(!IsOpen() && !Open(ofRead | ofWrite))
		  // 	return null;

		   // If a POE exists for this index, return the address of the indexed POE
		   return index < poes.size() ? poes.get(index) : null;
	}

	public Calc_data GetCalcData(int index){
		//if(!IsOpen() && !Open(ofRead | ofWrite))
			//return null;

		// If a Calc exists for this index, return the address of the indexed Calc
		return index < calcs.size() ? calcs.get(index) : null;
	}

	public Ang_data GetAngData(int index){
		//if(!IsOpen() && !Open(ofRead | ofWrite))
			//return null;

		// If a Calc exists for this index, return the address of the indexed Calc
		return index < angs.size() ? angs.get(index) : null;	
	}

	public Resid_data GetResidData(int index){
		//if(!IsOpen() && !Open(ofRead | ofWrite))
			//return null;

		// If a Calc exists for this index, return the address of the indexed Calc
		return index < resids.size() ? resids.get(index) : null;
	}

	public void SetEnergyUnits(int unit_int){
		// Convert from old energy unit to kcal
		conversion_factor = Convert_kcal_FROM_session_units(1.0f);
		energy_unit = unit_int;
		conversion_factor = Convert_kcal_TO_session_units(conversion_factor);

		//NotifyViews(vnConversionFactor, 0);
		RepaintAllPOEViews();
		// energy_unit = 0 => kcal/mol
		// energy_unit = 1 => kJ/mol
		// energy_unit = 2 => cm-1
		// energy_unit = 3 => eV
	}

	public String GetEnergyUnits(String unit_string){
		if(unit_string != null)
		{
			switch(energy_unit)
			{
			case 0:
				unit_string = "kcal/mol";
				break;
			case 1:
				unit_string = "kJ/mol";
				break;
			case 2:
				unit_string = "wavenumbers";
				break;
			default:
				unit_string = "eV";
			}
		}
		return unit_string;
	}

	public float Convert_kcal_TO_session_units(float old_number){
		switch(energy_unit)
		{
		case 0:
			return old_number;
		case 1:
			return (old_number * 4.184f);
		case 2:
			return (old_number * 349.64f);
		default:
			return (old_number * 0.043348f);
		}
	}

	public float Convert_kcal_FROM_session_units(float old_number){
		switch(energy_unit)
		{
		case 0:
			return old_number;
		case 1:
			return (old_number / 4.184f);
		case 2:
			return (old_number / 349.64f);
		default:
			return (old_number / 0.043348f);
		}
	}

	public float GetConvertsionFactor()  {
		return conversion_factor;
	} // Used by POE_Views

	public void DeleteView(int view_id){
		// Move through all views to delete the one associated with view_number
		/*JFrame this_view;
		int this_view_id;

		this_view = GetViewList();
		this_view_id = this_view.GetViewId();

		while(this_view_id != view_id)
		{
			this_view = NextView(this_view);
			this_view_id = this_view.GetViewId();
		}
		this_view = null;*/
	}

	public void OutputUFCData(BufferedWriter os, boolean compatMode) {
		int i, num_tofs, num_poes;

		num_tofs = tofs.size();
		num_poes = poes.size();

		try {
			os.write("...SBEAM *.ufc file...\n\n");

			os.write(num_tofs + "\n");

			for (i = 0; i < num_tofs; i++) {
				os.write(GetTOFData(i).out(compatMode) + "\n\n");
			}

			os.write(ion_flight_const + " " + ionizer_length + " "
					+ flight_length + " ");
			os.write(beam_ang_width + " " + detector_ang_width + " "
					+ ((compatMode)?((is_number_density)?1:0):is_number_density) + "\n");

			os.write("\n...SBEAM *.ufc file...\n\n");
			os.write(num_poes + "\n)");

			for (i = 0; i < num_poes; i++) {
				os.write(GetPOEData(i).out(compatMode) + "\n\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int LoadUFCData(Scanner is) {
		TOF_data new_tof;
		POE_data new_poe;

		int i, num_loaded_tofs, num_loaded_poes;
		String temp_string;

		temp_string = is.nextLine();
		if (!temp_string.equals("...SBEAM *.ufc file...")) {
			return 0; // Return value of zero => Incorrect file format
		}
		is.nextLine();

		num_loaded_tofs = is.nextInt();
		System.out.println("loading " + num_loaded_tofs + " tofs");
		for (i = 0; i < num_loaded_tofs; i++) {
			// Get a line of "\n"
			temp_string = is.nextLine();
			new_tof = new TOF_data();
			new_tof = TOF_data.in(is);
			AddTOFData(new_tof, false);
			if (i == (num_loaded_tofs - 1)) {
				current_tof_number = new_tof.GetTOFNum();
			}

		}

		SetIonFlightConst(is.nextFloat());
		SetIonizerLen(is.nextFloat());
		SetFlightLen(is.nextFloat());
		SetBeamAng(is.nextFloat());
		SetDetectAng(is.nextFloat());
		SetDetectScheme((is.hasNextBoolean()) ? is.nextBoolean(): (is.nextInt() == 1) ? true:false);
		SetIonFlightTimes();

		// Get a line of "\n"
		is.nextLine();
		is.nextLine();
		temp_string = is.nextLine();
		if (!temp_string.equals("...SBEAM *.ufc file...")) {
			return 1; // Return value of one => Incorrect file format after TOFs
						// are loaded
		}

		is.nextLine();
		num_loaded_poes = is.nextInt();
		for (i = 0; i < num_loaded_poes; i++) {
			// Get a line of "\n"
			temp_string = is.nextLine();
			new_poe = new POE_data();
			new_poe = POE_data.in(is);
			AddPOEData(new_poe, false);
			if (i == (num_loaded_poes - 1)) {
				current_poe_number = new_poe.GetPOENum();
			}

			temp_string = is.nextLine();
		}
		temp_string = is.nextLine();

		return -1; // Return value of -1 => Load of *.ufc was successful
	}

	public int AddTOFData(TOF_data tof, boolean new_tof_num){
		int index;

		if(new_tof_num)
		{
			current_tof_number++;
			tof.SetTOFNum(current_tof_number);
		}
		index = tofs.size();
		tofs.add(tof);
		//SetDirty(true);
		//ResetTOFsInTOFViews();
		return index;
	}

	public int AddPOEData(POE_data poe, boolean new_poe_num){
		int i, j, index, num_calcs, num_total_tofs;
		if(new_poe_num)
		{
			current_poe_number++;
			poe.SetPOENum(current_poe_number);
		}
		index = poes.size();
		Calc_data calculation;
		TOF_data this_tof;
		POE_calc_data[] old_calculation_data, new_calculation_data;
		poes.add(poe);

		// Update information in Calc_datas:
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			old_calculation_data = calculation.GetPOECalcData();

			new_calculation_data = new POE_calc_data[index + 1];
			for(j = 0; j < index; j++)
			{
				new_calculation_data[j] = old_calculation_data[j];
			}

			// Initialize final element!
			new_calculation_data[index] = new POE_calc_data();
			new_calculation_data[index].beta_param = 0.0f;
			new_calculation_data[index].num_channels = 1;
			new_calculation_data[index].is_included = false;
			new_calculation_data[index].mass_1 = new float[1];
			new_calculation_data[index].mass_1[0] = 1.0f;
			new_calculation_data[index].mass_2 = new float[1];
			new_calculation_data[index].mass_2[0] = 1.0f;
			new_calculation_data[index].rel_weight = new float[1];
			new_calculation_data[index].rel_weight[0] = 1.0f;
			new_calculation_data[index].mass_ratio = null;

			calculation.AddPOE();
			calculation.SetPOECalcData(new_calculation_data);
			calculation.ReplacePOECalcData(true);
		}

		num_total_tofs = tofs.size();

		for(i = 0; i < num_total_tofs; i++)
		{
			// Iterate through all TOFs and find those which are calculated from P(E)'s
			this_tof = this.GetTOFData(i);
			if(this_tof.GetIsRealTOF() == false)
			{
				if(this_tof.GetAssociatedCalc() != -1)
				{
					old_calculation_data = this_tof.GetPOECalcData();

					new_calculation_data = new POE_calc_data[index + 1];
					for(j = 0; j < index; j++)
					{
						new_calculation_data[j] = old_calculation_data[j];
					}

					// Initialize final element!
					new_calculation_data[index] = new POE_calc_data();
					new_calculation_data[index].beta_param = 0.0f;
					new_calculation_data[index].num_channels = 1;
					new_calculation_data[index].is_included = false;
					new_calculation_data[index].mass_1 = new float[1];
					new_calculation_data[index].mass_1[0] = 1.0f;
					new_calculation_data[index].mass_2 = new float[1];
					new_calculation_data[index].mass_2[0] = 1.0f;
					new_calculation_data[index].rel_weight = new float[1];
					new_calculation_data[index].rel_weight[0] = 1.0f;
					new_calculation_data[index].mass_ratio = null;

					this_tof.AddPOEArray(index + 1);
					this_tof.SetPOECalcData(new_calculation_data);
				}
			}
		}
		//SetDirty(true);
		return index;

	}

	public int AddCalcData(Calc_data calc){
		int index;
		index = calcs.size();
		calcs.add(calc);
		//SetDirty(true);
		return index;
	}

	public int AddAngData(Ang_data ang){
		int index;
		index = angs.size();
		angs.add(ang);
		//SetDirty(true);
		//NotifyViews(vnUpdateAngView, 0);
		return index;
	}

	public int AddResidData(Resid_data resid){
		int index;
		index = resids.size();
		resids.add(resid);
		//SetDirty(true);
		ResetAllResids();
		return index;
	}
	
	public  void RemoveTOFFromView(int tof_index, int view_number){
		TOF_data time_of_flight = this.GetTOFData(tof_index);
		time_of_flight.DeleteAssociatedView(time_of_flight.GetAssociatedView(view_number));
	}

	public  void RemovePOEFromView(POE_data energy_distribution, POEView p){
		energy_distribution.DeleteAssociatedView(p);
	}

	public  void RemoveAngFromView(Ang_data d, AngView v){
		//Ang_data angular_distribution = this.GetAngData(ang_index);
		d.DeleteAssociatedView(v);
	}

	public void RepaintAllTOFViews(){
		//NotifyViews(vnRepaintTOFView, 0);
		for(int loopMe = 0; loopMe < tofs.size(); loopMe++){
			for(int loopMeIn = 0; loopMeIn < tofs.get(loopMe).AssociatedTOFViews.size(); loopMeIn++){
				tofs.get(loopMe).AssociatedTOFViews.get(loopMeIn).updateContent();
			}
		}
	}

	public void RepaintAllPOEViews(){
		//NotifyViews(vnRepaintPOEView, 0);
		for(int loopMe = 0; loopMe < poes.size(); loopMe++){
			for(int loopMeIn = 0; loopMeIn < poes.get(loopMe).AssociatedPOEViews.size(); loopMeIn++){
				poes.get(loopMe).AssociatedPOEViews.get(loopMeIn).updateContent();
			}
		}
	}

	public void ResetTOFsInTOFViews(){
		//NotifyViews(vnChangeTOFs, 0);
		RedrawAllResids();
	}

	public  void ResetPOEsInPOEViews(){
		//NotifyViews(vnChangePOEs, 0);
	}

	public void ResetAngsInAngViews(){
		//NotifyViews(vnUpdateAngView, 0);
	}
	public void RedrawAllResids(){
		//NotifyViews(vnUpdateResidView, 0);
	}

	public void ResetAllResids(){
		//NotifyViews(vnResetResidView, 0);
	}

	public void ResetAllTOFColors(int poe_index){
		POE_data this_poe;
		TOF_data this_tof;
		Color this_poe_color;
		int i, num_total_tofs;

		this_poe = this.GetPOEData(poe_index);
		this_poe_color = this_poe.GetPOEColor();
		num_total_tofs = tofs.size();

		for(i = 0; i < num_total_tofs; i++)
		{
			// Iterate through all TOFs and find those which are calculated from P(E)'s
			this_tof = this.GetTOFData(i);
			if(this_tof.GetIsRealTOF() == false)
			{
				if(this_tof.GetAssociatedCalc() != -1)
				{
					this_tof.SetIndividualColor(this_poe_color, poe_index);
				}
			}
		}
		//NotifyViews(vnChangeTOFs, 0);
	}

	public void DeleteTOF(int index){
		TOF_data tof;
		Calc_data calc;
		//Ang_data *ang;
		int calc_number, i;

		tof = GetTOFData(index);
		if(!tof.GetIsRealTOF())
		{
			calc_number = tof.GetAssociatedCalc();
		}
		else
		{
			calc_number = -1;
		}

		if(calc_number != -1)
		{
			for(i = 0; i < calcs.size(); i++)
			{
				calc = GetCalcData(i);
				if(calc.GetCalcNumber() == calc_number)
				{
					calc.RemoveTOF(tof.GetTOFNum());
				}
			}
		}

		for(i = 0; i < angs.size(); i++)
		{
			GetAngData(i).RemoveTOF(tof.GetTOFNum());
		}  

		tof.DeleteTOFDataInfo();
		tofs.remove(index);
		return;
	}

	public void DeletePOE(int index){
		int i, j, num_calcs, num_poes, num_total_tofs;
		TOF_data this_tof;
		Calc_data calculation;
		POE_calc_data[] old_calculation_data, new_calculation_data;
		POE_data poe;


		num_poes = poes.size();
		poe = GetPOEData(index);
		poe.DeletePOEDataInfo();
		poes.remove(index);

		// Update information in Calc_datas:
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			old_calculation_data = calculation.GetPOECalcData();

			new_calculation_data = new POE_calc_data[num_poes - 1];
			for(j = 0; j < num_poes; j++)
			{
				if(j < index)
				{
					new_calculation_data[j] = old_calculation_data[j];
				}
				if(j > index)
				{
					new_calculation_data[j - 1] = old_calculation_data[j];
				}
			}

			//calculation.SetTotalNumPOEs(num_poes - 1);
			calculation.RemovePOE(index);
			calculation.SetPOECalcData(new_calculation_data);
			calculation.ReplacePOECalcData(true);
		}

		// Reset all TOF_datas which are calculated from P(E)'s to have correct # of P(E)'s
		num_total_tofs = tofs.size();

		for(i = 0; i < num_total_tofs; i++)
		{
			// Iterate through all TOFs and find those which are calculated from P(E)'s
			this_tof = this.GetTOFData(i);
			if(this_tof.GetIsRealTOF() == false)
			{

				if(this_tof.GetAssociatedCalc() != -1)
				{
					old_calculation_data = this_tof.GetPOECalcData();

					new_calculation_data = new POE_calc_data[num_poes - 1];
					for(j = 0; j < num_poes; j++)
					{
						if(j < index)
						{
							new_calculation_data[j] = old_calculation_data[j];
						}
						if(j > index)
						{
							new_calculation_data[j - 1] = old_calculation_data[j];
						}
					}

					this_tof.RemovePOEArray(index);
					this_tof.SetPOECalcData(new_calculation_data);
				}
			}
		}
		return;
	}

	public void DeleteCalc(int index) {
		calcs.remove(index); 
		return;
	}

	public void DeleteAng(int index){
		angs.remove(index);
		return;	
	}

	public void DeleteResid(int index)  {resids.remove(index); return;}
	public int GetNumTOFs()  {return tofs.size();}
	public int GetNumPOEs()  {return poes.size();}
	public int GetNumCalcs()  {return calcs.size();}
	public int GetNumAngs()  {return angs.size();}
	
	public int GetNumAngs(boolean is_vs_lab_angle){
		Ang_data ang_dist;
		int i, count;
		count = 0;
		for(i = 0; i < angs.size(); i++)
		{
			ang_dist = GetAngData(i);
			if(ang_dist.GetIsVsLabAngle() == is_vs_lab_angle)
			{
				count++;
			}
		}
		return count;	
	}

	public int GetNumResids()  {return resids.size();}

	public void CalcTOFDeltaFunctions(int poe_num, int point_number){
		// Want this function to first find which calculations contain this P(E),
		// calculate an overall TOF for the single point which has been moved in POEView
		// for each TOF in each calculation, and tell each view containing each respective
		// TOF to draw in the new TOF from the . function P(E).

		int i, j, total_number_calcs, total_number_tofs, count, calculation_number;
		Calc_data this_calculation;

		TOF_data[] tofs_this_calculation = null;
		TOF_data this_tof;

		int number_of_tofs;

		moving_poe_num = poe_num;


		total_number_tofs = tofs.size();
		total_number_calcs = calcs.size();
		calculation_tofs = new TOF_data[total_number_calcs][];
		for(i = 0; i < total_number_calcs; i++)
		{
			this_calculation = this.GetCalcData(i);

			if(this_calculation.GetPOECalcData()[poe_num].is_included)
			{
				calculation_number = this_calculation.GetCalcNumber();
				number_of_tofs = this_calculation.GetNumTOFs();
				System.out.println("NUM TOFS FOR " + this_calculation.GetTitle() + ": " + number_of_tofs);
				if(number_of_tofs != 0)
				{
					calculation_tofs[i] = new TOF_data[number_of_tofs];
					tofs_this_calculation = calculation_tofs[i];
					count = 0;
					for(j = 0; j < total_number_tofs; j++)
					{
						this_tof = this.GetTOFData(j);
						if(this_tof.GetIsRealTOF() == false)
						{
							if(this_tof.GetAssociatedCalc() == calculation_number)
							{
								tofs_this_calculation[count] = this_tof;
								count++;
							}
						}
					}
				}
				this_calculation.SetCalcTOFArray(tofs_this_calculation);
				this_calculation.CalcTOFDeltaFunctions(poe_num, this.GetPOEData(poe_num), point_number);
			}
		}
		//NotifyViews(vnAddTOF.Functions, 0);
	}

	public void RecalculateAllCalcs(int not_include_number){
		int i, calc_num;
		   Calc_data calculation;


		   for(i = 0; i < calcs.size(); i++)
		   {
		   	calculation = GetCalcData(i);
		      calc_num = calculation.GetCalcNumber();

		      if((calculation.GetNumTOFs() != 0) && (calc_num != not_include_number))
		      {
		      	StoreDetachedTOFData(calculation);
		      	CalculateTOFs(calc_num);
		      }
		   }
	}

	public void FindNewTOFs(float new_amplitude, boolean is_endpoint){
		int i, j, number_of_tofs;
		int total_number_calcs = calcs.size();

		Calc_data this_calculation;

		for(i = 0; i < total_number_calcs; i++)
		{
			this_calculation = this.GetCalcData(i);

			if(this_calculation.GetPOECalcData()[moving_poe_num].is_included)
			{
				number_of_tofs = this_calculation.GetNumTOFs();
				for(j = 0; j < number_of_tofs; j++)
				{
					calculation_tofs[i][j].AddOnDeltaTOF(new_amplitude, is_endpoint);
				}

			}
		}
	//	NotifyViews(vnRemoveTOFDeltaFunctions, 0);
		//NotifyViews(vnUpdateAngView, 0);
	}
	
	public void ChangeTOFs(float amplitude_pointer){
		//NotifyViews(vnChangeTOFDeltaFunctions, (long) amplitude_pointer);
	}

	public void UpdateViewNumber(int view_number) {ViewNumber = view_number;}
	public void UpdateAngNumber(int ang_number) {AngNumber = ang_number;}

	public void SetIonFlightConst(float flt_const) {ion_flight_const = flt_const;}
	
	public void SetIonizerLen(float ioniz_len){
		int i, num_calcs;
		Calc_data calculation;

		if(ionizer_length == ioniz_len)
			return;  // No changes need to be made here!

		ionizer_length = ioniz_len;  // Set the data in the document

		// Update the data in all the calculations
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			calculation.SetIonizerLen(ioniz_len);
		}
	}

	public void SetFlightLen(float flt_len){
		int i, num_calcs;
		Calc_data calculation;

		if(flight_length == flt_len)
			return;  // No changes need to be made here!

		flight_length = flt_len;  // Set the data in the document

		// Update the data in all the calculations
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			calculation.SetFlightLen(flt_len);
		}	
	}

	public void SetBeamAng(float bm_ang){
		int i, num_calcs;
		Calc_data calculation;

		if(beam_ang_width == bm_ang)
			return;  // No changes need to be made here!
		beam_ang_width = bm_ang;  // Set the data in the document

		// Update the data in all the calculations
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			calculation.SetBeamAng(bm_ang);
		}
	}

	public void SetDetectAng(float det_ang){
		int i, num_calcs;
		Calc_data calculation;

		if(detector_ang_width == det_ang)
			return;  // No changes need to be made here!

		detector_ang_width = det_ang;  // Set the data in the document

		// Update the data in all the calculations
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			calculation.SetDetectorAng(det_ang);
		}
	}

	public void SetDetectScheme(boolean is_num_density){
		int i, num_calcs;
		Calc_data calculation;
		if(is_number_density == is_num_density)
			return;  // No changes need to be made here!

		is_number_density = is_num_density;  // Set the data in the document

		// Update the data in all the calculations
		num_calcs = calcs.size();
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			calculation.SetIsNumDensityCalc(is_number_density);
		}
	}

	public float GetIonFlightConst()  {return ion_flight_const;}
	public float GetIonizerLen() {return ionizer_length;}
	public float GetFlightLen() {return flight_length;}
	public float GetBeamAng() {return beam_ang_width;}
	public float GetDetectAng() {return detector_ang_width;}
	public boolean GetDetectScheme() {return is_number_density;}

	public void SetIonFlightTimes(){
		int i;
		int num_tofs = tofs.size();
		for(i = 0; i < num_tofs; i++)
		{
			GetTOFData(i).SetIonFlightTime(ion_flight_const);
		}
		RepaintAllTOFViews();
	}

	public int GetViewNumber()   {return ViewNumber;}
	public int GetLastAngNumber()  {return AngNumber;}

	public boolean CanClose() {return true;}

	public void StoreDetachedTOFData(Calc_data calculation){
		String[] current_poe_titles;
		int i, j;
		int num_tofs = tofs.size();
		int num_poes = poes.size();

		TOF_data tof;

		int calc_number = calculation.GetCalcNumber();

		// Run through all TOFs and determine if any were attached to this calculation
		// If so, send all current POE titles to TOF for permanent storage
		for(i = 0; i < num_tofs; i++)
		{
			tof = GetTOFData(i);
			if(tof.GetIsRealTOF() == false)
			{
				if(tof.GetAssociatedCalc() == calc_number)
				{
					tof.SetAssociatedCalc(-1);
					current_poe_titles = new String[num_poes];
					for(j = 0; j < num_poes; j++)
					{
						current_poe_titles[j] = GetPOEData(j).GetTitle();
					}
					tof.SetCurrentPOETitles(num_poes, current_poe_titles, calculation.GetPOECalcData());
				}
			}
		}
		ResetTOFsInTOFViews();
	}

	public void CalculateTOFs(int calc_number){
		TOF_data[] new_tofs;                             
		POE_data[] current_poes;

		Calc_data calculation;
		int i,  index = 0;
		Integer num_new_tofs;

		int[] included_tof_num_array;

		num_new_tofs = 0;

		int num_poes = poes.size();

		current_poes = new POE_data[num_poes];


		for(i = 0; i < num_poes; i++)
		{
			current_poes[i] = GetPOEData(i);
		}


		int num_calcs = calcs.size();
		System.out.println(num_calcs + " calcs found");
		for(i = 0; i < num_calcs; i++)
		{
			calculation = GetCalcData(i);
			if(calculation.GetCalcNumber() == calc_number)
			{
				index = i;
			}
		}

		calculation = GetCalcData(index);
		calculation.SetTOF2CosGamma();
		new_tofs = calculation.RunMainFlightTimeCalculation(current_poes, num_new_tofs, beam_ang_width, detector_ang_width);

		num_new_tofs = calculation.GetNumTOFs();
		if(new_tofs == null)
			return;

		included_tof_num_array = new int[num_new_tofs];
		System.out.println("Adding " + num_new_tofs + " new calculated tofs");
		for(i = 0; i < num_new_tofs; i++)
		{
			AddTOFData(new_tofs[i], true);
			included_tof_num_array[i] = new_tofs[i].GetTOFNum();
		}
		calculation.SetIncludedTOFNumArray(included_tof_num_array);
		ResetTOFsInTOFViews();
	}

	public void SetXPosGadget(JLabel app_gadget)
	{
		x_pos_gadget = app_gadget;
	}

	public JLabel GetXPosGadget()
	{
		return x_pos_gadget;
	}

	public void SetYPosGadget(JLabel app_gadget)
	{
		y_pos_gadget = app_gadget;
	}

	public JLabel GetYPosGadget()
	{
		return y_pos_gadget;
	}

	public void SetSumSquareGadget(JLabel app_gadget)
	{
		sum_square_gadget = app_gadget;
	}

	public JLabel GetSumSquareGadget()
	{
		return sum_square_gadget;
	}

	public boolean GetHasChanged()  {return DocumentHasChanged;}




}
