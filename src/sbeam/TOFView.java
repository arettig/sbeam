package sbeam;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;
import javax.swing.*;

public class TOFView extends JInternalFrame implements MouseInputListener, InternalFrameListener{

	protected int ViewNumber;
	protected float starting_time, ending_time, old_starting_time, old_ending_time;

	protected float y_offset, x_spacing, y_axis, x_axis, x_offset;
	protected float y_axis_maximum, y_axis_minimum, y_axis_spacing;
	protected int origin_y_minus_offset, origin_y_minus_y_axis_plus_offset;

	protected int[] all_tofs_num_ion_channels;

	protected int[] stored_points;
	protected int[] this_poe_stored_points;

	protected int[] stationary_points_array_x_pos;
	protected int[] stationary_points_array_y_pos;
	protected int[] stationary_poe_point_array;

	protected int[] moving_points_array_x_pos;

	protected float[] y_spacings_array;
	protected float[] minimum_array;
	protected float[] maximum_array;

	protected TOF_data[] current_tofs;

	protected float old_amplitude;
	protected Point origin;

	protected int moving_poe_num, total_num_poes, num_tofs_in_view;

	protected Color blue, red;

	protected String[] included_tof_title_array;
	protected int scaling_tof, current_scaling_TOF, current_scaling_TOF_actual;

	protected float min_counts_scaling_TOF, max_counts_scaling_TOF;
	protected float moving_min_counts_scaling_TOF, moving_max_counts_scaling_TOF;
	protected float fixed_min_scaling_TOF_counts, fixed_max_scaling_TOF_counts;
	protected float average_counts_in_range;
	protected int temp_scaling_tof;
	protected boolean scale_to_tof, average_baseline;

	protected boolean show_min_line, show_max_line, move_min_line, move_max_line, is_message_from_dialog;
	protected boolean okay_was_clicked, range_has_changed, reset_percents, lines_present;
	protected boolean BaselineTimesNotSet, MinMaxHaveChanged, ResidsHaveChanged, RedrawYAxis;
	protected boolean IsDrawn, max_min_never_been_set;

	protected float temp_baseline_time1, temp_baseline_time2, baseline_time1, baseline_time2;

	protected int moving_min_y_position, moving_max_y_position, erased_y_position;
	protected int minimum_y_position, maximum_y_position;

	protected float min_y_pos_percent, max_y_pos_percent;
	protected float moving_min_y_pos_percent, moving_max_y_pos_percent;

	protected int y_value_of_scaling_min, y_value_of_scaling_max;

	protected int[] tof_index_array;

	protected JLabel x_pos_gadget, y_pos_gadget;
	protected String x_gadget_text, y_gadget_text;

	protected TOFPOEDocument TOFPOEDoc;
	protected TOF_data time_of_flight;

	protected boolean IsDirty;
	

	protected float original_point_amplitude;
	protected float[] changing_delta_function_array;


	protected float[] overall_individual_tof_array;

	protected float[] overall_tof;

	protected int[] this_chan_stored_points;

	protected boolean[] is_tof_real, is_tof_changing;
	protected int[] num_poe_array;

	protected boolean[] skip_space;

	protected int[] moving_old_point, stationary_old_point;

	protected Color tof_changing_color;

	protected Color AxisColor;

	protected TOF_Input_1_Dialog tof_input_1;
	protected TOF_Edit_Dialog_2 tof_input_2;
	protected Param_Dialog param_dialog;
	protected List_Dialog tof_list_dialog;
	protected List_Dialog tof_multi_list_dialog;
	protected ScaleTOFDialog scale_dialog;
	protected String[] list_box_text;

	protected ArrayList<TOF_data> AssociatedTOFs;
	protected MainFrame mainWindow;


	public TOFView(TOFPOEDocument doc, MainFrame parent) {
		// TODO Auto-generated constructor stub
		//Printer = new TPrinter;
		super("Title", true, true, true);
		mainWindow = parent;
		IsDrawn = false;
		BaselineTimesNotSet = true;
		MinMaxHaveChanged = true;
		ResidsHaveChanged = true;
		max_min_never_been_set = true;
		erased_y_position = -1;
		reset_percents = false;
		lines_present = false;

		y_spacings_array = null;
		minimum_array = null;
		maximum_array = null;

		tof_index_array = null;
		
		this.addFocusListener(parent);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		TOFPOEDoc = doc;
		x_pos_gadget = TOFPOEDoc.GetXPosGadget();
		y_pos_gadget = TOFPOEDoc.GetYPosGadget();

		x_gadget_text = "";
		y_gadget_text = "";

		time_of_flight = null;

		blue = new Color(0, 0, 255);
		red = new Color(255, 0, 0);

	
		//SetViewMenu(new TMenuDescr(IDM_TOFMENU));

		param_dialog = new Param_Dialog(parent, 1);  //(Is auto deleted when
		//window is closed.)
		//param_dialog.SetType(1);
		scale_to_tof = false;  // Don't scale all TOFs to a single TOF
		average_baseline = false;
		scaling_tof = 0;
		temp_scaling_tof = -1;

		num_tofs_in_view = 0;

		show_min_line = false;
		show_max_line = false;
		move_min_line = false;
		move_max_line = false;
		is_message_from_dialog = false;
		okay_was_clicked = false;
		range_has_changed = false;

		scale_dialog = new ScaleTOFDialog(mainWindow, this);
		//scale_dialog.SetBoolPointers(show_min_line, show_max_line, move_min_line,
				//move_max_line, is_message_from_dialog,
				//okay_was_clicked, range_has_changed);
		scale_dialog.SetListBoxList(num_tofs_in_view, included_tof_title_array, scaling_tof);
		scale_dialog.SetTempScalingTOF(temp_scaling_tof);

		
		tof_list_dialog = new List_Dialog(parent, null, 1);
		tof_multi_list_dialog = new List_Dialog(parent, null, 2);
		tof_input_1 = null;
		tof_input_2 = null;

		y_offset = 0;
		y_axis = 0;
		x_spacing = 0;

		old_starting_time = 0;
		old_ending_time = 0;
		//scaled_min_counts = 0;
		//scaled_max_counts = 0;

		origin = null;

		AssociatedTOFs = new ArrayList<TOF_data>();
		ViewNumber = TOFPOEDoc.GetViewNumber();
		TOFPOEDoc.ResetTOFsInTOFViews();
		tof_changing_color = new Color/*(147, 37, 21)*/(255, 0, 0);

		AxisColor = Color.BLACK;
		
		this.addInternalFrameListener(this);
		//this.getContentPane().add(new tofViewPane());
		//this.putClientProperty("JInternalFrame.frameType", "normal");
	}

	public void execute() {
		mainWindow.addFrame(this);
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(400, 200));
		this.pack();
		//this.setModalExclusionType();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFocusable(true);
		this.setEnabled(true);
		this.setVisible(true);
	}

	public void updateContent(){
		Graphics g = this.getGraphics();
		System.out.println("Painting again");
		this.Draw_TOF(g);
	}
	public static String StaticName()
	{
		return "TOF Display View";
	}

	public String GetViewName()
	{
		return StaticName();
	}

	public boolean SetDocTitle(String docname, int index)
	{
		this.setTitle(docname);
		return true;
	}


	// bool SetViewNumber(unsigned int number)  {ViewNumber = number;}
	/*public void LoadTOFData(ifstream is, TOF_data open_TOF){
		
	}
	public int InputTOFData(TOF_data time_of_flight, TOF_Input_1_Dialog tof_input_dialog, TOpenSaveDialog.TData FileData, bool FirstTime){

	}
	public int InputTOFData(TOF_data time_of_flight, TOF_Edit_Dialog_2 tof_input_dialog, TOpenSaveDialog.TData FileData, bool FirstTime, TOFPOEDocument document){

	}
	public void ScaleToString(String new_scale, scale temp_scale){

	}
	public scale StringToScale(String string){

	}
	public void SetTOFData(TOF_data time_of_flight, TOF_Input_1_Dialog tof_input_dialog, TOFPOEDocument document){

	}

	public void SetTOFData(TOF_data time_of_flight, TOF_Edit_Dialog_2 tof_input_dialog, TOFPOEDocument document){

	}
*/

	protected boolean CanClose(){
		boolean return_value;
		int i, num_associated_tofs, num_resids;
		Resid_data residual;
		String message;

		num_associated_tofs = AssociatedTOFs.size();
		if(num_associated_tofs == 1)
		{
			message = "The TOF will remain in memory.";
		}
		else
		{
			message = "The TOFs will remain in memory.";
		}
		return true;

		/*switch(MessageBox(message, "Close this display?",
				MB_OKCANCEL | MB_ICONQUESTION))
				{
				case IDCANCEL:
					return_value = false;                              

					break;
				default:
					for(i = 0; i < num_associated_tofs; i++)
					{
						TOFPOEDoc.RemoveTOFFromView((int) (AssociatedTOFs)[i], ViewNumber);
						// Each TOF can only be in view once!
						TOFPOEDoc.GetTOFData((int)(AssociatedTOFs)[i]).SetIsVisible(-2);//CHANGE:set not visible
					}
					TOFPOEDoc.ResetTOFsInTOFViews();
					num_resids = TOFPOEDoc.GetNumResids();
					for(i = 0; i < num_resids; i++)
					{
						residual = TOFPOEDoc.GetResidData(i);
						if(residual.GetAssociatedTOFView() == ViewNumber)
						{
							// Find out which TOFs are used in this residual
							residual.SelfDestruct();
						}
					}
					this.Destroy();
					TOFPOEDoc.ResetTOFsInTOFViews();  
					return_value = true;
				}
		time_of_flight = 0;

		return return_value;*/
	}

	protected int[] FillListBox(boolean all_nonview_tofs, boolean is_single){
		int[] index_of_tofs;
		int count = 0;
		int total_number_of_tofs = TOFPOEDoc.GetNumTOFs();
		int i, j;
		int number_of_associated_views;
		boolean this_tof_in_this_view;

		int tof_number = AssociatedTOFs.size();
		if((tof_number == total_number_of_tofs) && (all_nonview_tofs == true))
			return null;

		if(all_nonview_tofs)
		{
			index_of_tofs = new int[total_number_of_tofs - tof_number];
			list_box_text = new String[total_number_of_tofs - tof_number];
		}
		else
		{
			index_of_tofs = new int[tof_number];
			list_box_text = new String[tof_number];
		}

		for(i = 0; i < total_number_of_tofs; i++)
		{
			time_of_flight = TOFPOEDoc.GetTOFData(i);
			number_of_associated_views = time_of_flight.GetNumAssociatedViews();
			this_tof_in_this_view = false;
			if(all_nonview_tofs == false)
			{
				for(j = 0; j < number_of_associated_views; j++)
				{
					if(this.equals(time_of_flight.GetAssociatedView(j)))
					{
						list_box_text[count] = time_of_flight.GetTitle();
						index_of_tofs[count] = i;
						count++;
					}
				}
			}
			else
			{
				for(j = 0; j < number_of_associated_views; j++)
				{
					if(this.equals(time_of_flight.GetAssociatedView(j)))
					{
						this_tof_in_this_view = true;
					}
				}
				if(this_tof_in_this_view == false)
				{
					list_box_text[count] = time_of_flight.GetTitle();
					index_of_tofs[count] = i;
					count++;
				}
			} // End of else (i.e. if only want tofs in this view
		} // End of iterating through all tofs

		if(is_single)
			tof_list_dialog.SetListBoxList(list_box_text);
			else
				tof_multi_list_dialog.SetListBoxList(list_box_text);
				time_of_flight = null;
				return index_of_tofs;
	}

	protected void SetTOFsInView(){
		TOF_data time_of_flight;
		int i, j;
		
		int number_of_associated_views;
		int total_number_tofs = TOFPOEDoc.GetNumTOFs();
		this.setTitle("");
		boolean first = true;
		AssociatedTOFs.clear();
		int count;

		int num_resids, k, temp_tof_num, temp_number;
		Resid_data residual;
		int[] this_resid_actual_tof_nums, this_resid_view_tof_nums;
		TOF_data[] this_resid_tofs;
		TOF_data temp_tof;

		String[] temp_tof_list;

		current_scaling_TOF = -1;
		count = 0;
		for(i = 0; i < total_number_tofs; i++)
		{
			time_of_flight = TOFPOEDoc.GetTOFData(i);
			number_of_associated_views = time_of_flight.GetNumAssociatedViews();

			for(j = 0; j < number_of_associated_views; j++)
			{
				if(this.equals(time_of_flight.GetAssociatedView(j)))
				{
					if(!AssociatedTOFs.isEmpty() && !(AssociatedTOFs.get(0).is_real_TOF)){
						AssociatedTOFs.add(0, time_of_flight);
					}else{
						AssociatedTOFs.add(time_of_flight);
					}
					if(scale_to_tof)
					{
						if(time_of_flight.GetTOFNum() == current_scaling_TOF_actual)
						{
							current_scaling_TOF = AssociatedTOFs.indexOf(time_of_flight);
						}
					}
					count++;
					if(this.getTitle().length() < 100)
					{
						if(first)
						{
							this.setTitle(this.getTitle()+ "TOF: " + time_of_flight.GetTitle());
							first = false;
						}
						else
						{
							this.setTitle(this.getTitle()+ "; " + time_of_flight.GetTitle());

						}
					}

				}
			}
		}

		if(this.getTitle().length() > 100)
		{
			this.setTitle("...\0");
		}
		//SetDocTitle("", 0);
		num_tofs_in_view = AssociatedTOFs.size();


		if(current_scaling_TOF == -1)
		{
			scale_to_tof = false;
		}

		y_spacings_array = new float[num_tofs_in_view]; // Array of y_spacing scalings for a TOF
		minimum_array = new float[num_tofs_in_view]; // Array of minima for the TOFs
		maximum_array = new float[num_tofs_in_view]; // Array of maxima for the TOFs

		MinMaxHaveChanged = true;
		ResidsHaveChanged = true;
		//temp_tof_list = included_tof_title_array;

		temp_tof_list = new String[num_tofs_in_view];
	
		tof_index_array = new int[num_tofs_in_view];

		count = 0;

		for(i = 0; i < total_number_tofs; i++)
		{
			time_of_flight = TOFPOEDoc.GetTOFData(i);
			number_of_associated_views = time_of_flight.GetNumAssociatedViews();

			for(j = 0; j < number_of_associated_views; j++)
			{
				if(this.equals(time_of_flight.GetAssociatedView(j)))
				{
					temp_tof_list[count] = time_of_flight.GetTitle();
					tof_index_array[count] = i;
					count++;
				}
			}
		}
		
		included_tof_title_array = temp_tof_list;

		/*if((scale_dialog.GetStatus() == true) || (num_tofs_in_view == 1))
		{
			scale_dialog.ResetTOFTitleList(num_tofs_in_view, included_tof_title_array);
		}*/


		// Run through all residuals and find correct TOF
		num_resids = TOFPOEDoc.GetNumResids();
		for(i = 0; i < num_resids; i++)
		{
			residual = TOFPOEDoc.GetResidData(i);
			if(residual.GetTitle() != null)   // Will be NULL if the residual is self-destructed
			{
				if(residual.GetAssociatedTOFView().equals(this))
				{
					this_resid_actual_tof_nums = residual.GetActualTOFNums();
					this_resid_view_tof_nums = residual.GetViewTOFNums();
					this_resid_tofs = residual.GetTOFs();
					for(j = 0; j < 2; j++)
					{
						temp_tof_num = this_resid_actual_tof_nums[j];
						for(k = 0; k < num_tofs_in_view; k++)
						{
							temp_tof = AssociatedTOFs.get(k);
							if(temp_tof_num == temp_tof.GetTOFNum())
							{
								this_resid_view_tof_nums[j] = k;
								this_resid_tofs[j] = temp_tof;
								k = num_tofs_in_view;
							}
						}
					}

					// Sort the two TOFs so a real one comes first if possible
					if((this_resid_tofs[0].GetIsRealTOF() == false) && (this_resid_tofs[0].GetIsRealTOF() == true))
					{
						temp_tof = this_resid_tofs[1];
						this_resid_tofs[1] = this_resid_tofs[0];
						this_resid_tofs[0] = temp_tof;

						temp_number = this_resid_view_tof_nums[1];
						this_resid_view_tof_nums[1] = this_resid_view_tof_nums[0];
						this_resid_view_tof_nums[0] = temp_number;

						temp_number = this_resid_actual_tof_nums[1];
						this_resid_actual_tof_nums[1] = this_resid_actual_tof_nums[0];
						this_resid_actual_tof_nums[0] = temp_number;
					}
				}
			}
		}
	}

	protected void Draw_TOF(Graphics g){
		// Will do this in the view so can control the size of the TOF
		int i;
		TOF_data tof;
		Resid_data residual;

		int[] view_tof_nums;  // Holds 2 numbers of TOFs used in each residual

		int num_tofs, this_num_channels, num_resids;
		int x_pos, y_pos, j, k, m; 
		int origin_x, old_y_position, old_x_position, tof_index;

		float time_value;
		float minimum_time, maximum_time, lower_lim, upper_lim;
		float[] extrema;
		float maximum = 0, minimum, y_spacing, window_width, window_height;
		float font_width, font_height, graph_top, graph_left;
		float[] tof_pointer, real_time_pointer;
		Color TOFColor;

		boolean first_point;
		Graphics2D g2 = (Graphics2D)g;

		// Draw x and y axes relative to the total window size
		
		Rectangle window_rect = this.getContentPane().getBounds();
		window_rect.setLocation(this.getInsets().left, this.getInsets().top);
		window_width = (float) (window_rect.getWidth());
		window_height = (float) (window_rect.getHeight());
		
		//font_width = (float) Math.max(2, 0.03*window_width);
		font_height = (float) Math.max(8, 0.06*window_height);

		if(font_height > 16)
		{
			//font_width = Math.min(6, font_width);
			font_height = 16;
		}
		else
		{
			//font_width = (float) Math.min(font_width, (font_height / 2.4));
		}

		Font font = g2.getFont().deriveFont(font_height); 
		Font sidefont = g2.getFont().deriveFont(0.9f*font_height);
		
		graph_top = this.getInsets().top;
		graph_left = this.getInsets().left;

		x_axis =  window_width -  font_height;
		x_offset = 0.01f * x_axis;
		y_axis = window_height - font_height ;
		y_offset = 0.02f * y_axis;
		
		origin = new Point((int) (graph_left + font_height),(int) (graph_top + (int) y_axis));

		origin_y_minus_offset = origin.y - (int)y_offset;
		origin_y_minus_y_axis_plus_offset = (int)(origin.y - y_axis + y_offset);
		origin_x = origin.x;
		
		g2.drawLine(origin.x, origin.y, origin.x, origin.y-(int) y_axis);
		g2.drawLine(origin.x, origin.y, origin.x+(int)x_axis, origin.y);		




		// Draw ticks on x axis

		g2.drawLine(origin.x, origin.y, origin.x, origin.y+2);
		g2.drawLine(origin.x+(int)(x_axis-x_offset), origin.y, origin.x+(int)(x_axis-x_offset), origin.y+2);

		// Draw ticks on y axis 
		
		g2.drawLine(origin.x, origin.y-(int)y_offset, origin.x-2, origin.y-(int)y_offset);
		g2.drawLine(origin.x, origin.y+(int)(-y_axis+y_offset), origin.x-2, origin.y+(int)(-y_axis+y_offset));


		Rectangle x_axis_rect = new Rectangle(origin.x, origin.y+4, origin.x+ (int)x_axis, origin.y+50);
		Rectangle y_axis_rect = new Rectangle(origin.x + (int)(-4-font_height), origin.y, origin.x-4, origin.y-(int)(y_axis));

		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics(font);
		g2.drawString("Flight Time (µs)", (int)x_axis_rect.getCenterX()-metrics.stringWidth("Flight Time (µs)"), origin.y+font_height);

		g2.setFont(sidefont);
		AffineTransform orig = g2.getTransform();
		g2.rotate(Math.PI/2);
		g2.drawString("N(t) (arb. units)", origin.y-(y_axis/2)-metrics.stringWidth("N(t) (arb. units)"), -(int)(origin.x-font_height) );
		g2.setTransform(orig);

		if(AssociatedTOFs.size() == 0){
			return;
		}
		tof = AssociatedTOFs.get(0);  // First TOF in display!


		// Determine which points to draw
		minimum_time = Float.parseFloat(param_dialog.GetDefault1());
		maximum_time = Float.parseFloat(param_dialog.GetDefault2());
		lower_lim = Float.parseFloat(param_dialog.GetValue1());
		upper_lim = Float.parseFloat(param_dialog.GetValue2());
		if ((lower_lim < (int) (minimum_time - 1)) || (upper_lim > (int) (maximum_time + 1))
				|| (lower_lim >= upper_lim))
		{
			///////////////////////////////
			// Insert error message here //
			///////////////////////////////

			real_time_pointer = tof.RealTimePointer();
			starting_time = real_time_pointer[0];
			ending_time = real_time_pointer[tof.GetTotChannels() - 1];

			String lower_lim_recall = "";  // To recall old lower limit
			String upper_lim_recall = "";  // To recall new lower limit
			lower_lim_recall = "" + starting_time;
			upper_lim_recall = "" + ending_time;
			param_dialog.SetValue1(lower_lim_recall);
			param_dialog.SetValue2(upper_lim_recall);
		}
		else
		{
			starting_time = lower_lim;
			ending_time = upper_lim;
		}


		// Run through each TOF in this view and find max and min values
		// Start with the scaling TOF if there is one

		if(scale_to_tof)
		{
			time_of_flight = AssociatedTOFs.get(current_scaling_TOF);
			extrema = time_of_flight.GetMaxMinCounts(starting_time, ending_time);
			maximum = extrema[0];
			minimum = extrema[1];

			if(Math.abs(maximum - minimum) < 1* Math.pow(10, -30))
			{
				y_spacing = (y_axis - 2 * y_offset)/2;
			}
			else
			{
				y_spacing = (y_axis - 2 * y_offset)/(maximum - minimum);
			}

			y_axis_minimum = minimum;
			y_axis_maximum = maximum;
			y_axis_spacing = y_spacing;

			y_spacings_array[current_scaling_TOF] = y_spacing;
			minimum_array[current_scaling_TOF] = fixed_min_scaling_TOF_counts;
			maximum_array[current_scaling_TOF] = fixed_max_scaling_TOF_counts;

			y_value_of_scaling_min = (int) (origin_y_minus_offset - (fixed_min_scaling_TOF_counts - minimum) * y_spacing);
			y_value_of_scaling_max = (int) (origin_y_minus_offset - (fixed_max_scaling_TOF_counts - minimum) * y_spacing);
		}
		else
		{
			y_value_of_scaling_min = origin_y_minus_offset;
			y_value_of_scaling_max = (int) (origin_y_minus_offset - y_axis + 2 * y_offset);
		}


		// Only draw in TOFs which should be shown in this view
		num_tofs = AssociatedTOFs.size();

		if(param_dialog.GetHasChanged() == true)
		{
			MinMaxHaveChanged = true;
			ResidsHaveChanged = true;
		}

		for(m = 0; m < num_tofs; m++)
		{
			tof = AssociatedTOFs.get(m);
			if(tof.GetIsRealTOF())
			{
				TOFColor = tof.GetTOFColor();
				g2.setColor(TOFColor);

				tof_pointer = tof.ChannelCountsPointer();
				// Draw TOF points using small dots.

				real_time_pointer = tof.RealTimePointer();
				float time_value_2;
				int max_tof_channels = tof.GetTotChannels();
				x_spacing = (x_axis - x_offset)/(ending_time - starting_time);
				if(MinMaxHaveChanged)
				{
					if(scale_to_tof)
					{
						extrema = tof.GetMaxMinCounts(0, 0);
					}
					else
					{
						extrema = tof.GetMaxMinCounts(starting_time, ending_time);
					}

					maximum = extrema[0];
					if(scale_to_tof && average_baseline)
					{
						minimum = tof.GetAverageCounts(baseline_time1, baseline_time2);
					}
					else
					{
						minimum = extrema[1];
					}

					if(Math.abs(maximum - minimum) < 1e-30)
					{
						y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / 2;
					}
					else
					{
						y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / (maximum - minimum);
					}

					if((scale_to_tof) && (m == current_scaling_TOF))
					{
						y_spacing = y_spacings_array[m];
						minimum = minimum_array[m];
					}
					else
					{
						y_spacings_array[m] = y_spacing;
						minimum_array[m] = minimum;
						maximum_array[m] = maximum;
					}
				}
				else
				{
					y_spacing = y_spacings_array[m];
					minimum = minimum_array[m];
				}

				for(k = 0; k < max_tof_channels; k++)
				{

					time_value = real_time_pointer[k];
					if((time_value >= starting_time) && (time_value <= ending_time))
					{
						y_pos = y_value_of_scaling_min - (int) ((tof_pointer[k] - minimum) * y_spacing);
						if((erased_y_position == -1) || (Math.abs(y_pos - erased_y_position) <= 2))
						{
							if((y_pos <= origin_y_minus_offset) && (y_pos >= origin_y_minus_y_axis_plus_offset))
							{
								x_pos = origin_x + (int) ((time_value - starting_time) * x_spacing);
								g2.drawOval(x_pos, y_pos, 4, 4);//4 = width, height
							}else{
							}
						}
					}else{
					}
				}
			}
		} // Move through all TOFs and only draw the real ones first


		// Now draw the calculated ones
		for(m = 0; m < num_tofs; m++)
		{
			tof = AssociatedTOFs.get(m);
			if(tof.GetIsRealTOF() == false)
			{
				int[] num_channels = tof.GetNumChannelsArray();
				float[][][] all_tofs_pointer = tof.GetTOFPointers();
				Color[] TOFColors = tof.GetTOFColors();
				TOFColor = tof.GetTOFColor(); // Color of overall TOF
				g2.setColor(TOFColor);
				
				real_time_pointer = tof.RealTimePointer();
				int max_tof_channels = tof.GetTotChannels();
				x_spacing = (x_axis - x_offset)/(ending_time - starting_time);
				if(MinMaxHaveChanged)
				{
					if(scale_to_tof)
					{
						extrema = tof.GetMaxMinCounts(0, 0);
					}
					else
					{
						extrema = tof.GetMaxMinCounts(starting_time, ending_time);
					}

					maximum = extrema[0];
					if(scale_to_tof && average_baseline)
					{
						minimum = tof.GetAverageCounts(baseline_time1, baseline_time2);
					}
					else
					{
						minimum = extrema[1];
					}

					if(Math.abs(maximum - minimum) < 1e-30)
					{
						y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / 2;
					}
					else
					{
						y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / (maximum - minimum);
					}

					if((scale_to_tof) && (m == current_scaling_TOF))
					{
						y_spacing = y_spacings_array[m];
						minimum = minimum_array[m];
					}
					else
					{
						y_spacings_array[m] = y_spacing;
						minimum_array[m] = minimum;
						maximum_array[m] = maximum;
					}
				}

				else
				{
					y_spacing = y_spacings_array[m];
					minimum = minimum_array[m];
				}

				for(i = 0; i < tof.GetNumCurrentPOEs(); i++)
				{
					if(all_tofs_pointer[i] != null)
					{
						this_num_channels = num_channels[i];
						if(this_num_channels == 1)
							this_num_channels = 0;    // Don't draw TOF for the individual channel if only one channel exists
							for(j = this_num_channels; j >= 0 ; j--)   // Backwards, so total P(E) TOF drawn last
							{

								if((j != 0) || (tof.GetNumIncludedPOEs() > 1))   // i.e. skip plotting individual TOFs for each
									// contributing P(E) if only one contributes
								{
									if(j == 0){
										//Pen = new TPen((TOFColors[i]), 1, PS_SOLID);
									}else{
										//Pen = new TPen((TOFColors[i]), 1, pen_style[(j - 1) % 4]);
									}
									tof_pointer = all_tofs_pointer[i][j];


									first_point = true;

									old_y_position = 0;
									old_x_position = 0;
									Point og = new Point(0,0);
									for(k = 0; k < max_tof_channels; k++)
									{
										time_value = real_time_pointer[k];
										if((time_value >= starting_time) && (time_value <= ending_time))
										{
											x_pos = origin_x + (int) ((time_value - starting_time)* x_spacing);
											y_pos = y_value_of_scaling_min - (int)((tof_pointer[k] - minimum) *y_spacing);
											if(erased_y_position == -1)
											{
												if((y_pos > origin_y_minus_offset) || (y_pos < origin_y_minus_y_axis_plus_offset))
												{
													first_point = true;
												}
												else
												{
													if(first_point)
													{
														og.x = x_pos;
														og.y = y_pos;
														first_point = false;
													}
													g2.drawLine(og.x, og.y, x_pos, y_pos);
													og.x = x_pos;
													og.y = y_pos;
												}
											}

											else
											{
												if(first_point)
												{
													first_point = false;
												}
												else
												{
													if(((old_y_position - erased_y_position) * (y_pos - erased_y_position)) <= 0)
													{
														g2.drawLine(old_x_position, old_y_position, x_pos, y_pos);
													}
												}
												old_y_position = y_pos;
												old_x_position = x_pos;
											}

										}
									}
								}
							}
					}
				}

				// Now draw the final overall TOF
				g2.setColor(tof.GetTOFColor());

				tof_pointer = tof.GetTotalTOF();

				first_point = true;
				old_y_position = 0;
				old_x_position = 0;
				Point og = new Point(0,0);
				for(k = 0; k < max_tof_channels; k++)
				{
					time_value = real_time_pointer[k];
					if((time_value >= starting_time) && (time_value <= ending_time))
					{
						y_pos = y_value_of_scaling_min - (int)((tof_pointer[k] - minimum)*y_spacing);
						x_pos = origin_x + (int) ((time_value - starting_time) * x_spacing);
						if(erased_y_position == -1)
						{
							if((y_pos > origin_y_minus_offset) || (y_pos < origin_y_minus_y_axis_plus_offset))
							{
								first_point = true;
							}
							else
							{
								if(first_point)
								{
									og.x = x_pos;
									og.y = y_pos;
									first_point = false;
								}
								g2.drawLine(og.x, og.y, x_pos, y_pos);
								og.x = x_pos;
								og.y = y_pos;
							}
						}
						else
						{
							if(first_point)
							{
								first_point = false;
							}
							else
							{
								if(((old_y_position - erased_y_position) * (y_pos - erased_y_position)) <= 0)
								{
									g2.drawLine(old_x_position, old_y_position, x_pos, y_pos);
								}
							}
							old_y_position = y_pos;
							old_x_position = x_pos;
						}

					}else{
					}
				}
			}
		}

		int right_edge;
		String string;
		right_edge = (int) Math.min((x_axis + 2 * metrics.stringWidth("1000.00")), (window_width - 2 - origin.x));
		Rectangle start_time_rect = new Rectangle(origin.x -4, origin.y+2, origin.x+4, origin.y+25);
		Rectangle end_time_rect = new Rectangle((int) (origin.x + (right_edge - 5 * metrics.stringWidth("1000.00"))), origin.y+ 2,
				origin.x+right_edge, origin.y+25);

		if(scale_to_tof == false)
		{
			y_axis_minimum = minimum_array[0];
			y_axis_maximum = maximum_array[0];
			y_axis_spacing = y_spacings_array[0];
		}

		if((y_axis_maximum == 0) || (((y_axis_maximum - y_axis_minimum) / y_axis_maximum) < 1e-8))
		{
			y_axis_minimum = 0;
		}

		g2.setColor(Color.black);
		string = "" + y_axis_minimum;
		Rectangle min_rect = new Rectangle((int)(origin.x + (-2 - font_height)), (int)(origin.y+ (-y_offset + 2)),
				origin.x -4, (int)(origin.y + (-4-y_offset)));
		g2.setFont(sidefont);
		g2.drawString(string, (int) min_rect.getCenterX(), (int)min_rect.getCenterY());

		string = "" + y_axis_maximum;
		Rectangle max_rect = new Rectangle((int)(origin.x + (-2 - font_height)), (int)(origin.y +((string.length() * 0.7 * metrics.stringWidth("1000.00"))+y_offset-y_axis)),
				origin.x -4, (int)(origin.y + ((3 * metrics.stringWidth("1000.00"))+y_offset-y_axis)));
		g2.drawString(string, (int) max_rect.getCenterX(), (int)max_rect.getCenterY());

		g2.setFont(font);
		string = "" + starting_time;
		g2.drawString(string, start_time_rect.x, start_time_rect.y);
		string = "" + ending_time;
		g2.drawString(string, (int) end_time_rect.getCenterX(), (int)end_time_rect.getCenterY());

		IsDrawn = true;
		if(max_min_never_been_set)
		{
			moving_min_y_pos_percent = 0.0f;
			moving_max_y_pos_percent = 1.0f;
			min_y_pos_percent = 0.0f;
			max_y_pos_percent = 1.0f;
			minimum_y_position = origin.y - 1;
			maximum_y_position = (int)(origin.y - y_axis + 1);
			moving_min_y_position = minimum_y_position;
			moving_max_y_position = maximum_y_position;
			max_min_never_been_set = false;
		}

		// The following loop is entered if the time range of the TOF has changed (meaning the
				// position of the lines relative to the TOFs may have changed as well) or if the
		// size of the view has changed.
		if((param_dialog.GetHasChanged() == true) || reset_percents)
		{
			if(scale_to_tof)
			{

				moving_min_y_position = (int) (y_value_of_scaling_min - (moving_min_counts_scaling_TOF - minimum_array[current_scaling_TOF])
						* y_spacings_array[current_scaling_TOF]);

				moving_max_y_position = (int) (y_value_of_scaling_min - (moving_max_counts_scaling_TOF - minimum_array[current_scaling_TOF])
						* y_spacings_array[current_scaling_TOF]);

				minimum_y_position = (int) (y_value_of_scaling_min - (min_counts_scaling_TOF - minimum_array[current_scaling_TOF])
						* y_spacings_array[current_scaling_TOF]);

				maximum_y_position = (int) (y_value_of_scaling_min - (max_counts_scaling_TOF - minimum_array[current_scaling_TOF])
						* y_spacings_array[current_scaling_TOF]);
			}
			else
			{
				if(temp_scaling_tof >= 0)
				{
					minimum_y_position = y_value_of_scaling_min - (int)((average_counts_in_range -
							minimum_array[temp_scaling_tof]) * y_spacings_array[temp_scaling_tof]);
					moving_min_y_position = minimum_y_position;
				}
				else
				{
					minimum_y_position = (origin.y - 1) + (int)(min_y_pos_percent * (2.0 - y_axis));
					moving_min_y_position = (origin.y - 1) + (int)(moving_min_y_pos_percent * (2.0 - y_axis));
				}
				maximum_y_position = (origin.y - 1) + (int)(max_y_pos_percent * (2.0 - y_axis));
				moving_max_y_position = (origin.y - 1) + (int)(moving_max_y_pos_percent * (2.0 - y_axis));
			}
			reset_percents = false;
		}


		if(this.scale_dialog.move_minimum_line)
		{
			if(moving_min_y_position <= (origin.y - 1))
			{
				g2.setColor(Color.red);
				System.out.println("Minima: " + moving_min_y_position);
				g2.drawLine(origin_x, moving_min_y_position, (int) (origin_x + x_axis), moving_min_y_position);
				g2.setColor(Color.black);
			}
			lines_present = true;
		}

		if(this.scale_dialog.move_maximum_line)
		{
			if(moving_max_y_position >= (int)(origin.y - y_axis + 1))
			{
				g2.setColor(Color.red);
				g2.drawLine(origin_x, moving_max_y_position, (int) (origin_x+x_axis), moving_max_y_position);
				g2.setColor(Color.black);
			}
			lines_present = true;
		}

		// Send proper information to associated residuals
		if(ResidsHaveChanged)
		{
			num_resids = TOFPOEDoc.GetNumResids();
			float slope0, slope1, intercept0, intercept1;
			float scale_minimum, scale_maximum, temp_minimum, temp_maximum;
			for(i = 0; i < num_resids; i++)
			{
				residual = TOFPOEDoc.GetResidData(i);
				if(residual.GetAssociatedTOFView().equals(this))
				{
					// Find out which TOFs are used in this residual
					view_tof_nums = residual.GetViewTOFNums();

					// Find slope and intercept for conversion of this TOF data to values comparable
					// to the scaling TOF
					// i.e. scaled_value = m * actual_value + b
					// Then, can determine scaled_value from an actual value input

					if(scale_to_tof)
					{
						scale_minimum = minimum_array[current_scaling_TOF];
						scale_maximum = maximum_array[current_scaling_TOF];

						temp_minimum = minimum_array[view_tof_nums[0]];
						temp_maximum = maximum_array[view_tof_nums[0]];

						slope0 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
						intercept0 = (float) ((scale_minimum + scale_maximum - slope0 * (temp_minimum + temp_maximum)) / 2.0);

						temp_minimum = minimum_array[view_tof_nums[1]];
						temp_maximum = maximum_array[view_tof_nums[1]];

						slope1 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
						intercept1 = (float) ((scale_minimum + scale_maximum - slope1 * (temp_minimum + temp_maximum)) / 2.0);

					}
					else
					{
						slope0 = 1.0f;
						intercept0 = 0.0f;

						scale_minimum = minimum_array[view_tof_nums[0]];
						scale_maximum = maximum_array[view_tof_nums[0]];

						temp_minimum = minimum_array[view_tof_nums[1]];
						temp_maximum = maximum_array[view_tof_nums[1]];

						slope1 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
						intercept1 = (float) ((scale_minimum + scale_maximum - slope1 * (temp_minimum + temp_maximum)) / 2.0);
					}
					residual.SetScalingParams(slope0, intercept0, slope1, intercept1);
				}
			}
			TOFPOEDoc.RedrawAllResids();
			ResidsHaveChanged = false;
		}
		MinMaxHaveChanged = false;
		return;
	}
	// and so forth

	/*protected void EvSize(int i, TSize win_size){
		// Rescale the values of the min and max lines to adjust for the window size
		MinMaxHaveChanged = true;
		reset_percents = true;
		this.Invalidate();
		TWindow::EvSize(first, win_size);  // Call the TWindow version for this function
	}*/

	/*protected void EvRButtonUp(int modKeys, Point point){
		int origin_y = origin.y;
		// Store new maximum, minimum information and tell dialog that button has been pressed
		if(move_min_line)
		{
			// The y value of point should give the new minimum value to which all
			// TOFs in this view should be scaled.

			minimum_y_position = point.y;
			if(minimum_y_position <= maximum_y_position)
			{
				minimum_y_position = maximum_y_position + 1;
			}
			if(minimum_y_position >= origin_y)
			{
				minimum_y_position = origin_y - 1;
			}


			if(scale_to_tof)
			{
				min_counts_scaling_TOF = ((y_value_of_scaling_min - minimum_y_position) /
						y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];
				moving_min_counts_scaling_TOF = min_counts_scaling_TOF;
			}
			else
			{
				min_y_pos_percent = ((float)(moving_min_y_position + 1 - origin_y)) / (2.0 - y_axis);
				moving_min_y_pos_percent = min_y_pos_percent;
			}

			moving_min_y_position = minimum_y_position;

			// Tell dialog to change back the minimum button to its original state
			scale_dialog.ResetMinimumButton();
			this.Invalidate();
		}
		if(move_max_line)
		{
			// The y value of point should give the new minimum value to which all
			// TOFs in this view should be scaled.
			maximum_y_position = point.y;
			if(maximum_y_position >= minimum_y_position)
			{
				maximum_y_position = minimum_y_position - 1;
			}

			if(maximum_y_position <= (origin_y - y_axis))
			{
				maximum_y_position = origin_y - y_axis + 1;
			}

			if(scale_to_tof)
			{
				max_counts_scaling_TOF = ((y_value_of_scaling_min - maximum_y_position) /
						y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];
				moving_max_counts_scaling_TOF = max_counts_scaling_TOF;
			}
			else
			{
				max_y_pos_percent = ((float)(moving_max_y_position + 1 - origin_y)) / (2.0 - y_axis);
				moving_max_y_pos_percent = max_y_pos_percent;
			}

			moving_max_y_position = maximum_y_position;
			// Tell dialog to change back the minimum button to its original state
			scale_dialog.ResetMaximumButton();
			this.Invalidate();
		}
	}*/

	/*protected void EvMouseMove(int modKeys, TPoint point){
		int origin_x;
		int origin_y;
		erased_y_position = -1;
		boolean should_invalidate = false;

		String temp_text = "";


		float x_time, y_value;




		int tof_index;

		if(IsDrawn)
		{
			origin_x = origin.x;
			origin_y = origin.y;
			if(is_message_from_dialog)
			{
				if(!move_min_line && !move_max_line)
				{


					// If lines should be present but aren't
					if((show_min_line || show_max_line) && !lines_present)
					{
						should_invalidate = true;  // Upon redrawing, the lines will be shown
					}

					// If lines are present but shouldn't be
					if(!show_min_line && !show_max_line && lines_present)
					{
						should_invalidate = true;
					}

					// Change the position of the minimum line to match the baseline of the currently
					// selected scaling TOF over the chosen baseline time range
					if(range_has_changed)
					{
						//temp_scaling_tof = scale_dialog.GetCurrentSelectedIndex();
						tof_index = (int) (AssociatedTOFs)[temp_scaling_tof];
						time_of_flight = TOFPOEDoc.GetTOFData(tof_index);

						average_counts_in_range = time_of_flight.GetAverageCounts(temp_baseline_time1, temp_baseline_time2);

						// Convert this average # of counts into a y_value
						minimum_y_position = y_value_of_scaling_min - (int)((average_counts_in_range -
								minimum_array[temp_scaling_tof]) * y_spacings_array[temp_scaling_tof]);

						if(maximum_y_position >= minimum_y_position)
						{

							maximum_y_position = minimum_y_position -1;
							if(scale_to_tof)
							{
								max_counts_scaling_TOF = ((y_value_of_scaling_min - maximum_y_position) /
										y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];
							}
							else
							{
								max_y_pos_percent = ((float)(maximum_y_position + 1 - origin_y)) / (2.0 - y_axis);
							}
						}


						if(scale_to_tof)
						{
							min_counts_scaling_TOF = ((y_value_of_scaling_min - minimum_y_position) /
									y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];
						}
						else
						{
							min_y_pos_percent = ((float)(minimum_y_position + 1 - origin_y)) / (2.0 - y_axis);
						}
						range_has_changed = false;
						should_invalidate = true;
					}

					// If the positions of the lines have moved since they were last stored, reset the
					// positions to be drawn equal to the positions which were last stored

					// This is used when a dialog box button is pushed to reset either the maximum or
					// minimum line position
					if((moving_min_y_position != minimum_y_position) || (moving_max_y_position != maximum_y_position))
					{
						moving_min_y_position = minimum_y_position;
						moving_max_y_position = maximum_y_position;

						if(scale_to_tof)
						{
							moving_min_counts_scaling_TOF = min_counts_scaling_TOF;
							moving_max_counts_scaling_TOF = max_counts_scaling_TOF;
						}
						else
						{
							moving_min_y_pos_percent = min_y_pos_percent;
							moving_max_y_pos_percent = max_y_pos_percent;
						}

						should_invalidate = true;
					}


					if(should_invalidate)
					{
						this.Invalidate();
					}

					if(!show_min_line && !show_max_line && lines_present)
					{
						lines_present = false; // After redrawing, the lines won't be present anymore
					}

				}

				if(scale_dialog.GetStatus() == false)   // i.e. if the dialog box just closed
				{

					// If okay was clicked, store the data and find max and min values
					if(okay_was_clicked)
					{
						MinMaxHaveChanged = true;
						ResidsHaveChanged = true;
						scale_to_tof = scale_dialog.GetScale();
						average_baseline = scale_dialog.GetMin();

						// Set number of scaling TOF and find the # of counts which correspond to
						// the maximum and minimum value lines.
						if(scale_to_tof)
						{

							if(average_baseline)
							{
								temp_scaling_tof = scaling_tof;
								baseline_time1 = temp_baseline_time1;
								baseline_time2 = temp_baseline_time2;
								tof_index = (int) (AssociatedTOFs)[temp_scaling_tof];
								time_of_flight = TOFPOEDoc.GetTOFData(tof_index);

								average_counts_in_range = time_of_flight.GetAverageCounts(baseline_time1, baseline_time2);

								// Convert this average # of counts into a y_value
								minimum_y_position = y_value_of_scaling_min - (int)((average_counts_in_range -
										minimum_array[temp_scaling_tof]) * y_spacings_array[temp_scaling_tof]);

								if(maximum_y_position >= minimum_y_position)
								{
									maximum_y_position = minimum_y_position -1;
								}
							}

							current_scaling_TOF = scaling_tof;
							current_scaling_TOF_actual = TOFPOEDoc.GetTOFData((int) (AssociatedTOFs)[current_scaling_TOF]).GetTOFNum();

							min_counts_scaling_TOF = ((y_value_of_scaling_min - minimum_y_position) /
									y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];
							max_counts_scaling_TOF = ((y_value_of_scaling_min - maximum_y_position) /
									y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];

							moving_min_counts_scaling_TOF = min_counts_scaling_TOF;
							moving_max_counts_scaling_TOF = max_counts_scaling_TOF;
							fixed_min_scaling_TOF_counts = min_counts_scaling_TOF;
							fixed_max_scaling_TOF_counts = max_counts_scaling_TOF;
						}
						else
						{
							current_scaling_TOF = -1;
							current_scaling_TOF_actual = -1;
							moving_min_y_pos_percent = 0.0;
							moving_max_y_pos_percent = 1.0;
							min_y_pos_percent = 0.0;
							max_y_pos_percent = 1.0;
							minimum_y_position = origin.y - 1;
							maximum_y_position = (int)(origin.y - y_axis + 1);
							moving_min_y_position = minimum_y_position;
							moving_max_y_position = maximum_y_position;
						}

					}
					Invalidate();
				}
				is_message_from_dialog = false;
			}
			else
			{

				if((point.x >= origin_x) && point.x <= (origin_x + x_axis - (int)x_offset))
				{
					x_gadget_text = "x pos.:  ";

					// Convert the x position to a time in microseconds
					if(x_spacing)
					{
						x_time = ((point.x - origin_x) / x_spacing) + starting_time;
					}
					else
					{
						x_time = starting_time;
					}
					temp_text = "" + x_time;


					x_gadget_tex += temp_text;
					x_gadget_text += " µs";
				}
				else
				{
					x_gadget_text = "x pos.:  ";
				}

				x_pos_gadget.SetText(x_gadget_text);

				if((point.y <= origin_y) && (point.y >= (int)(origin_y - y_axis)))
				{
					y_gadget_text = "y pos.:  ";

					// Convert the y position to a y value
					if(y_axis_spacing)
					{
						y_value = ((origin_y_minus_offset - point.y) / y_axis_spacing) + y_axis_minimum;
					}
					else
					{
						y_value = y_axis_minimum;
					}
					if(point.y == (int) (origin_y + y_offset - y_axis))
					{
						y_value = y_axis_maximum;
					}
					if(point.y == origin_y_minus_offset)  // Second value is an int
					{
						y_value = y_axis_minimum;
					}
					temp_text = "" + y_value;


					y_gadget_text += temp_text);
				}
				else
				{
					y_gadget_text = "y pos.:  ";
				}
				y_pos_gadget.SetText(y_gadget_text);
				// If the minimum line is moving, change the moving line data, but don't change the
				// stationary line data
				if(move_min_line)
				{
					if(point.y <= maximum_y_position)
					{
						point.y = maximum_y_position + 1;
					}

					if(point.y >= origin_y)
					{
						point.y = origin_y - 1;
					}

					if(moving_min_y_position != point.y)
					{
						if(moving_min_y_position <= (origin.y - 1))
						{
							Eraser = new TClientDC(*this);
							Eraser.SelectObject(*erase_pen);

							Eraser.MoveTo(origin_x, moving_min_y_position);
							Eraser.LineTo(origin_x + x_axis, moving_min_y_position);
							erased_y_position = moving_min_y_position;
						}
						moving_min_y_position = point.y;
						if(scale_to_tof)
						{
							// Find position of min line relative to the scaling TOF
							moving_min_counts_scaling_TOF = ((y_value_of_scaling_min - moving_min_y_position) /
									y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];

						}
						else
						{
							moving_min_y_pos_percent = ((float)(moving_min_y_position + 1 - origin_y)) / (2.0 - y_axis);
						}
						Draw_TOF();
					}
				}
				if(move_max_line)
				{
					if(point.y >= minimum_y_position)
					{
						point.y = minimum_y_position - 1;
					}

					if(point.y <= (origin_y - y_axis))
					{
						point.y = origin_y - y_axis + 1;
					}

					if(moving_max_y_position != point.y)
					{
						if(moving_max_y_position >= (int)(origin.y - y_axis + 1))
						{
							Eraser = new TClientDC(*this);
							Eraser.SelectObject(*erase_pen);

							Eraser.MoveTo(origin_x, moving_max_y_position);
							Eraser.LineTo(origin_x + x_axis, moving_max_y_position);
							erased_y_position = moving_max_y_position;
						}
						moving_max_y_position = point.y;
						if(scale_to_tof)
						{
							// Find position of min line relative to the scaling TOF
							moving_max_counts_scaling_TOF = ((y_value_of_scaling_min - moving_max_y_position) /
									y_spacings_array[current_scaling_TOF]) + minimum_array[current_scaling_TOF];

						}
						else
						{
							moving_max_y_pos_percent = ((float)(moving_max_y_position + 1 - origin_y)) / (2.0 - y_axis);
						}
						Draw_TOF();
					}
				}
			}
		}
	}*/

	@Override
	public void paint(Graphics g){
		super.paint(g);
		//TWindow* main_window = GetApplication().GetMainWindow();
		//main_window.SetCursor(0, IDC_WAIT);
		erased_y_position = -1;
		Draw_TOF(g);
		//main_window.SetCursor(0, IDC_ARROW);
	}

	protected void DisplayResidual(){
		int view_tof_number = AssociatedTOFs.size();
		int[] index_array;

		Resid_data resid;
		TOF_data[] tof_pair;
		TOF_data placeholder;

		int temp_number;
		int[] view_tof_numbers;
		int[] actual_tof_numbers;
		int resid_view_number;
		float scale_minimum, scale_maximum, temp_minimum, temp_maximum;
		float slope0, intercept0, slope1, intercept1;

		tof_pair = new TOF_data[2];
		resid_view_number = TOFPOEDoc.GetViewNumber();

		index_array = FillListBox(false, false); // False . show only TOFs for this view in list box
		// False . for multiple selection list box
		if(view_tof_number > 2)
		{
			tof_list_dialog = new List_Dialog(mainWindow, list_box_text, 2);
			tof_multi_list_dialog.Execute();
			//check
			
		}


		if(view_tof_number == 2)
		{
			view_tof_numbers = new int[2];
			view_tof_numbers[0] = 0;
			view_tof_numbers[1] = 1;
		}
		else
		{
			view_tof_numbers = tof_multi_list_dialog.GetChosenIndex();
		}
		tof_pair[0] = AssociatedTOFs.get(view_tof_numbers[0]);
		tof_pair[1] = AssociatedTOFs.get(view_tof_numbers[1]);

		actual_tof_numbers = new int[2];
		actual_tof_numbers[0] = tof_pair[0].GetTOFNum();
		actual_tof_numbers[1] = tof_pair[1].GetTOFNum();


		// Sort the tofs so that a calculated TOF comes first (if one is included)
		if(tof_pair[0].GetIsRealTOF() == false)
		{
			placeholder = tof_pair[1];
			tof_pair[1] = tof_pair[0];
			tof_pair[0] = placeholder;

			temp_number = view_tof_numbers[1];
			view_tof_numbers[1] = view_tof_numbers[0];
			view_tof_numbers[0] = temp_number;

			temp_number = actual_tof_numbers[1];
			actual_tof_numbers[1] = actual_tof_numbers[0];
			actual_tof_numbers[0] = temp_number;
		}


		resid = new Resid_data(tof_pair);
		resid.SetViewTOFNums(view_tof_numbers);
		resid.SetActualTOFNums(actual_tof_numbers);
		resid.SetAssociatedTOFView(this);

		if(scale_to_tof)
		{
			scale_minimum = minimum_array[current_scaling_TOF];
			scale_maximum = maximum_array[current_scaling_TOF];

			temp_minimum = minimum_array[view_tof_numbers[0]];
			temp_maximum = maximum_array[view_tof_numbers[0]];

			slope0 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
			intercept0 = (float) ((scale_minimum + scale_maximum - slope0 * (temp_minimum + temp_maximum)) / 2.0);


			temp_minimum = minimum_array[view_tof_numbers[1]];
			temp_maximum = maximum_array[view_tof_numbers[1]];

			slope1 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
			intercept1 = (float) ((scale_minimum + scale_maximum - slope1 * (temp_minimum + temp_maximum)) / 2.0);

		}
		else
		{
			slope0 = 1.0f;
			intercept0 = 0.0f;

			scale_minimum = minimum_array[view_tof_numbers[0]];
			scale_maximum = maximum_array[view_tof_numbers[0]];

			temp_minimum = minimum_array[view_tof_numbers[1]];
			temp_maximum = maximum_array[view_tof_numbers[1]];

			slope1 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
			intercept1 = (float) ((scale_minimum + scale_maximum - slope1 * (temp_minimum + temp_maximum)) / 2.0);
		}

		resid.SetScalingParams(slope0, intercept0, slope1, intercept1);


		TOFPOEDoc.AddResidData(resid);
		TOFPOEDoc.UpdateViewNumber(resid_view_number + 1);
		ResidView rView = new ResidView(TOFPOEDoc, mainWindow);
		resid.SetAssociatedResidView(rView);
		rView.SetResidInView();
		rView.Execute();
	}

	protected void AxisRange(){
		int number_of_tofs = AssociatedTOFs.size();
		int i;
		float[] time_pointer;
		float min_time = 0, max_time = 0;

		String lower_time_limit;
		String upper_time_limit;
		String least_possible_time;
		String greatest_possible_time;

		if(true)
		{
			for(i = 0; i < number_of_tofs; i++)
			{
				time_of_flight = AssociatedTOFs.get(i);
				time_pointer = time_of_flight.RealTimePointer();
				if(i == 0)
				{
					min_time = time_pointer[0];
					max_time = time_pointer[(time_of_flight.GetTotChannels()) - 1];
					least_possible_time = "" + min_time;
					greatest_possible_time = "" + max_time;
				}
				else
				{
					min_time = Math.min(min_time, time_pointer[0]);
					max_time = Math.max(max_time, time_pointer[(time_of_flight.GetTotChannels()) - 1]);
				}
			}
			//param_dialog.Create();
			param_dialog = new Param_Dialog(mainWindow, 1);
			least_possible_time = "" + min_time;
			greatest_possible_time = "" + max_time;

			lower_time_limit = "" + starting_time;
			upper_time_limit = "" + ending_time;

			param_dialog.SetDefault1(least_possible_time);
			param_dialog.SetDefault2(greatest_possible_time);
			param_dialog.SetValue1(lower_time_limit);
			param_dialog.SetValue2(upper_time_limit);
			time_of_flight = null;
			param_dialog.Execute();
			if(param_dialog.ID == false){
				return;
			}
			starting_time = Float.parseFloat(param_dialog.edit1.getText());
			ending_time = Float.parseFloat(param_dialog.edit2.getText());
		}
	}

	protected void SetColors(){
		int view_tof_number = AssociatedTOFs.size();
		int[] index_array;
		TOF_data tof;


		index_array = FillListBox(false, true); // False . show only TOFs for this view in list box
		tof_list_dialog = new List_Dialog(mainWindow, list_box_text, 1);

		if(view_tof_number > 1)
		{
			tof_list_dialog.SetCaption("Choose a TOF:");
			tof_list_dialog.Execute();
			//check
			if(tof_list_dialog.ID != true){
				return;
			}
			
			tof = TOFPOEDoc.GetTOFData(index_array[tof_list_dialog.GetChosenIndex()[0]]);
		}
		else
		{
			tof = TOFPOEDoc.GetTOFData(index_array[0]);
		}
		
		Color c = JColorChooser.showDialog(this, "Choose Color", Color.black);
		if(c!=null){
			tof.SetTOFColor(c);
		}
		
		TOFPOEDoc.RepaintAllTOFViews();
	}
	
	protected void AppendNewTOF(){
		tof_input_1 = new TOF_Input_1_Dialog(mainWindow);
		time_of_flight = new TOF_data();

		// Use dialog box to open the file for streaming the data
		/*TOpenSaveDialog.TData TOFFileData = new TOpenSaveDialog::TData(OFN_HIDEREADONLY |
				OFN_FILEMUSTEXIST | OFN_OVERWRITEPROMPT,
				"TOF files (*.tof, *.tuf, *._tf)|*.tof; *.tuf; *._tf|STF files (*.stf)|*.stf|",
				0, "", "tof");
		if(TFileOpenDialog(GetApplication().GetMainWindow(), *TOFFileData).DoExecute() != IDOK)
		{
			tof_input_1.DeleteUnusedInfo();
			return;
		}
		ifstream is(TOFFileData.FileName);
		if (!is)
		{
			GetApplication().GetMainWindow().MessageBox("Unable to open file",
					"File Error", MB_OK | MB_ICONEXCLAMATION);
			tof_input_1.DeleteUnusedInfo();
			return;
		}
		else
		{
			char period = '.';
			String extension;
			extension = TOFFileData.FileName.substring(extension.indexOf('.'));

			if(extension.equals(".tuf") == 0)
			{
				time_of_flight.SetTOFFormat(0);
			}
			else
			{
				time_of_flight.SetTOFFormat(1);
			}
			LoadTOFData(is, time_of_flight);
			if(InputTOFData(time_of_flight, tof_input_1, TOFFileData, true) == IDOK)
			{

				SetTOFData(time_of_flight, tof_input_1, TOFPOEDoc);
				time_of_flight.AddAssociatedView(ViewNumber);
				TOFPOEDoc.AddTOFData(time_of_flight);
				time_of_flight.SetIsVisible(ViewNumber);	//CHANGE: save and reopen functionality
				TOFPOEDoc.ResetTOFsInTOFViews();
			}
			else
			{
				tof_input_1.DeleteUnusedInfo();
			}
		}
		this.Invalidate();
		num_tofs_in_view = AssociatedTOFs.GetItemsInContainer();*/
	}

	protected void TOFScalingInformation() {
		if (BaselineTimesNotSet == true) {
			temp_baseline_time1 = starting_time;
			temp_baseline_time2 = ending_time;
			BaselineTimesNotSet = false;
		}

		if (scale_dialog.GetStatus() == false) // i.e. if dialog not already
												// open
		{
			// Show dialog box and fill it with the correct information
			scale_dialog = new ScaleTOFDialog(mainWindow, this);
			scale_dialog.SetTimes(temp_baseline_time1, temp_baseline_time2);
			scale_dialog.SetBools(scale_to_tof, average_baseline);
			scale_dialog.ResetTOFTitleList(num_tofs_in_view,
					included_tof_title_array);
			this.mainWindow.addDialog(scale_dialog);
			scale_dialog.Execute();
		}
		
		is_message_from_dialog = false;
	}

	protected void changeScaling() {
		System.out.println("changing");
		// If okay was clicked, store the data and find max and min values
		if (okay_was_clicked) {
			MinMaxHaveChanged = true;
			ResidsHaveChanged = true;
			scale_to_tof = scale_dialog.GetScale();
			average_baseline = scale_dialog.GetMin();

			// Set number of scaling TOF and find the # of counts which
			// correspond to
			// the maximum and minimum value lines.
			if (scale_to_tof) {
				System.out.println("here2");
				if (average_baseline) {
					temp_scaling_tof = scaling_tof;
					baseline_time1 = temp_baseline_time1;
					baseline_time2 = temp_baseline_time2;
					time_of_flight = AssociatedTOFs.get(temp_scaling_tof);

					average_counts_in_range = time_of_flight.GetAverageCounts(
							baseline_time1, baseline_time2);

					// Convert this average # of counts into a y_value
					minimum_y_position = y_value_of_scaling_min
							- (int) ((average_counts_in_range - minimum_array[temp_scaling_tof]) * y_spacings_array[temp_scaling_tof]);

					if (maximum_y_position >= minimum_y_position) {
						maximum_y_position = minimum_y_position - 1;
					}
				}

				current_scaling_TOF = scaling_tof;
				current_scaling_TOF_actual = AssociatedTOFs.get(
						current_scaling_TOF).GetTOFNum();

				min_counts_scaling_TOF = ((y_value_of_scaling_min - minimum_y_position) / y_spacings_array[current_scaling_TOF])
						+ minimum_array[current_scaling_TOF];
				max_counts_scaling_TOF = ((y_value_of_scaling_min - maximum_y_position) / y_spacings_array[current_scaling_TOF])
						+ minimum_array[current_scaling_TOF];

				moving_min_counts_scaling_TOF = min_counts_scaling_TOF;
				moving_max_counts_scaling_TOF = max_counts_scaling_TOF;
				fixed_min_scaling_TOF_counts = min_counts_scaling_TOF;
				fixed_max_scaling_TOF_counts = max_counts_scaling_TOF;
			} else {
				current_scaling_TOF = -1;
				current_scaling_TOF_actual = -1;
				moving_min_y_pos_percent = 0.0f;
				moving_max_y_pos_percent = 1.0f;
				min_y_pos_percent = 0.0f;
				max_y_pos_percent = 1.0f;
				minimum_y_position = origin.y - 1;
				maximum_y_position = (int) (origin.y - y_axis + 1);
				moving_min_y_position = minimum_y_position;
				moving_max_y_position = maximum_y_position;
			}

		}
	}
	protected void AppendLoadedTOF()
	{
		int[] index_array;
		index_array = FillListBox(true, true); // True . show all non-displayed in list box
		// true . for single selection list box
		if(index_array != null)  // i.e. if some loaded TOFs not in this view already
		{
			tof_list_dialog = new List_Dialog(mainWindow, list_box_text, 1);

			tof_list_dialog.SetCaption("Choose a TOF:");

			tof_list_dialog.Execute();
			// check
			if (tof_list_dialog.ID != true)
				return;
			time_of_flight = TOFPOEDoc.GetTOFData(index_array[tof_list_dialog
					.GetChosenIndex()[0]]);
			time_of_flight.AddAssociatedView(this);
			TOFPOEDoc.ResetTOFsInTOFViews();
			this.SetTOFsInView();
			time_of_flight.SetIsVisible(ViewNumber); // CHANGE: save and reopen
														// functionality

			// this.Invalidate();
		}
	}

	protected void RemoveTOFFromDisplay() {
		int[] index_array;
		index_array = FillListBox(false, true); // False . show only TOFs for
												// this view in list box
		// true . single selection list box

		tof_list_dialog = new List_Dialog(mainWindow, list_box_text, 1);

		tof_list_dialog.SetCaption("Choose a TOF to remove:");

		int chosen_index, tof_index, num_resids, i, j;
		int[] view_tof_nums;
		Resid_data residual;

		tof_list_dialog.Execute();
		// check
		if(tof_list_dialog.ID != true) return;

		chosen_index = tof_list_dialog.GetChosenIndex()[0]; // Corresponds to
															// which TOF in this
															// view
		tof_index = index_array[chosen_index];
		// Run through all residuals and adjust them

		num_resids = TOFPOEDoc.GetNumResids();
		for (i = 0; i < num_resids; i++) {
			residual = TOFPOEDoc.GetResidData(i);
			if (residual.GetAssociatedTOFView().equals(this)) {
				// Find out which TOFs are used in this residual
				view_tof_nums = residual.GetViewTOFNums();
				for (j = 0; j < 2; j++) {
					if (view_tof_nums[j] > chosen_index) {
						view_tof_nums[j]--; // Adjust all indices above the
											// chosen index
					} else {
						if (view_tof_nums[j] == chosen_index) {
							residual.SelfDestruct();
						}
					}
				}
			}
		}

		TOF_data t = TOFPOEDoc.GetTOFData(tof_index);
		t.DeleteAssociatedView(this);
		AssociatedTOFs.remove(t);
		//TOFPOEDoc.ResetTOFsInTOFViews();
		TOFPOEDoc.RepaintAllTOFViews();
		TOFPOEDoc.GetTOFData(tof_index).SetIsVisible(-2); // CHANGE: when
										// removing from
															// view set to not
															// visible

		num_tofs_in_view = AssociatedTOFs.size();
		// this.Invalidate();
	}

	protected void EditViewTOFParameters() {
		int view_tof_number = AssociatedTOFs.size();
		int[] index_array;
		TOF_data tof;
		index_array = FillListBox(false, true); // False . show only TOFs in
												// this view in list box
		// true . single selection list box
		tof_list_dialog = new List_Dialog(mainWindow, list_box_text, 1);
		if (view_tof_number > 1) {
			tof_list_dialog.SetCaption("Choose a TOF:");

			tof_list_dialog.Execute();
			// check
			if (!tof_list_dialog.ID)
				return;

			tof = TOFPOEDoc.GetTOFData(index_array[tof_list_dialog
					.GetChosenIndex()[0]]);
		} else {
			tof = TOFPOEDoc.GetTOFData(index_array[0]);
		}

		if (tof.GetIsRealTOF()) {
			tof_input_1 = new TOF_Input_1_Dialog(mainWindow);
			mainWindow.brains.InputTOFData(tof, tof_input_1, null, false);
			// Last two parameters.TOF already loaded

			mainWindow.brains.SetTOFData(tof, tof_input_1, TOFPOEDoc);
			// Retrieves updated TOF data from dialog box
			this.SetTOFsInView();
			TOFPOEDoc.ResetTOFsInTOFViews();

		} else {
			tof_input_2 = new TOF_Edit_Dialog_2(mainWindow);
			if (mainWindow.brains.InputTOFData(tof, tof_input_2, null, false,
					TOFPOEDoc) == 0) {// Last two parameters.TOF already loaded

				mainWindow.brains.SetTOFData(tof, tof_input_2, TOFPOEDoc);
				// Retrieves updated TOF data from dialog box
				TOFPOEDoc.ResetTOFsInTOFViews();
			}
		}
		TOFPOEDoc.RepaintAllTOFViews();
	}
	
	protected void OpenFile(){
		
	}

	

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		int i, num_associated_tofs, num_resids;
		Resid_data residual;

		num_associated_tofs = AssociatedTOFs.size();

		for (i = 0; i < num_associated_tofs; i++) {
			AssociatedTOFs.get(i).DeleteAssociatedView(this);
			// Each TOF can only be in view once!
			AssociatedTOFs.get(i).SetIsVisible(-2);// CHANGE:set not visible
		}
		TOFPOEDoc.ResetTOFsInTOFViews();
		num_resids = TOFPOEDoc.GetNumResids();
		for (i = 0; i < num_resids; i++) {
			residual = TOFPOEDoc.GetResidData(i);
			if (residual.GetAssociatedTOFView().equals(this)) {
				// Find out which TOFs are used in this residual
				residual.SelfDestruct();
			}
		}

		TOFPOEDoc.ResetTOFsInTOFViews();

		mainWindow.internalClosed(this);
		time_of_flight = null;
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(this.scale_dialog.minima_button_depressed || this.scale_dialog.maxima_button_depressed){
			this.scale_dialog.getInput(this.getYValue(e.getPoint().y));
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public int getXValue(int x){
		float x_time = 0;
		if((x >= origin.x) && x <= (origin.x + x_axis - (int)x_offset))
		{

			// Convert the x position to a time in microseconds
			if(this.x_spacing != 0)
			{
				x_time = ((x - origin.x) / x_spacing) + starting_time;
			}
			else
			{
				x_time = starting_time;
			}

		}
		return (int)x_time;
		
	}

	public int getYValue(int y){
		float y_value = 0;
		if((y <= origin.y) && (y >= (int)(origin.y - y_axis)))
		{
			y_gadget_text = "";

			// Convert the y position to a y value
			if(y_axis_spacing != 0)
			{
				y_value = ((origin_y_minus_offset - y) / y_axis_spacing) + y_axis_minimum;
			}
			else
			{
				y_value = y_axis_minimum;
			}
			if(y == (int) (origin.y + y_offset - y_axis))
			{
				y_value = y_axis_maximum;
			}
			if(y == origin_y_minus_offset)  // Second value is an int
			{
				y_value = y_axis_minimum;
			}

		}

		return (int)y_value;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		Point point = e.getPoint();
		int origin_x = origin.x;
		int origin_y = origin.y;
		float x_time, y_value;
		String temp_text;
		
		x_gadget_text = "x pos.:  ";
		x_gadget_text += this.getXValue(point.x);
		x_gadget_text += " µs";
		x_pos_gadget.setText(x_gadget_text);

		
		y_gadget_text = "";
		y_gadget_text += this.getYValue(point.y);
		y_pos_gadget.setText(y_gadget_text);
		
		if(this.scale_dialog.minima_button_depressed){
			this.moving_min_y_position = point.y;
		}else if(this.scale_dialog.maxima_button_depressed){
			this.moving_max_y_position = point.y;
		}
		this.repaint();
		
	}

	//void CmPrintTOFWindow();

	// View notification functions
	/*protected boolean VnChangeTOFs(int index){
		SetTOFsInView();
		this.Invalidate();
		return true;
	}

	protected boolean VnRepaintTOFView(int index){
		MinMaxHaveChanged = true;
		ResidsHaveChanged = true;
		this.Invalidate();
		return true;
	}

	protected boolean VnAddTOFDeltaFunctions(int index){
		int i, tof_i, tof_j, tof_k, counter, poe_num;
		int x_pos, y_pos_delta, y_pos_overall, y_pos_this_chan, next_x_pos;
		int origin_x, x_pos_minus_origin_x, number_x_positions;
		int lower_lim, upper_lim, k;

		int num_tof_points; // i.e. number of time bins in the tof
		int old_point; // Used to determine if a space has been skipped in drawing lines
		int this_num_ion_channels;

		int this_y_pos;

		int[] this_tof_stored_points;
		int pen_style[4] = {PS_DASH, PS_DOT, PS_DASHDOT, PS_DASHDOTDOT};
		int[] this_tof_moving_x_array; // Temporary pointer to x positions of current TOF
		int[] this_tof_channels_array; // Pointer to the number of ion channels contributing to this calculated TOF
		int[] this_poe_chan_stored_points; // Temporary pointer to the stored points of the individual
		// ion channel TOFs for an individual P(E) TOF

		int[] this_chan_old_point;             // Arrays to hold old points so the redrawing
		int[] this_tof_stationary_old_points; // of the TOF can put spaces at the right place


		float this_tof_amplitude;
		float time_value, next_time_value;
		float maximum, minimum, y_spacing;

		float[] delta_tof_pointer, real_time_pointer;

		float[][] this_tof_delta_tofs_array;  // Used to temporarily store delta tof arrays
		float[][] this_tof_individual_poe_tofs;  // Used to temporarily store individual P(E) tofs arrays

		float[][][] this_tof_individual_tofs; // Used to temporarily store overall arrays.

		boolean first_point;
		boolean delta_pen_is_initialized;

		boolean[] redraw_x_positions;   // Array of points; true . redraw anything with this x_position
		boolean[] this_tof_skip_space;  // Placeholder for which spaces to skip when drawing overall TOF

		TOF_data this_tof;

		Color thisPOE_TOFColor;  // Temporary placeholder; pointer to a color of a P(E)
		Color[] thisPOE_TOFColor_array; // Pointer to array of all P(E) colors

		TPen[] this_tof_channel_pen_array;  // Temporary placeholder; array of pens for the P(E)'s from this TOF
		TPen[] this_poe_stationary_pen_array; // Same; array of pens for all the non-moving points
		// for each channel in a P(E) calculated TOF

		TPen[][] this_tof_stationary_pen_array; // Array of all the P(E) pens for a given calculated TOF

		TDC this_tof_EraseChangingTOF; // Tool to erase old overall TOF before redrawing
		TDC this_tof_DrawChangingTOF;  // Tool to draw the changing overall TOF

		TDC[] this_tof_draw_channel_array; // Array of tools to draw individual moving channels
		TDC[] this_tof_erase_channel_array; // Array of tools to erase individual channels upon moving
		TDC[] this_poe_stationary_channel_array; // Array of tools for a stationary P(E)'s ion channels

		TDC[][] this_tof_stationary_channel_array; // Array of above tools for a given TOF

		num_tofs_in_view = AssociatedTOFs.GetItemsInContainer();

		// Initialize pens and tools
		tof_pen_array = new TPen[num_tofs_in_view]; // One pen for each TOF
		channel_pen_array = new TPen[num_tofs_in_view]; // Array of ion channel pens for each TOF
		stationary_pen_array = new TPen[num_tofs_in_view]; // Set of pens for every channel and P(E)
		// for each calculated TOF

		draw_channel_array = new TDC[num_tofs_in_view];  // Array of tools using channel_pen_array


		tof_draw_array = new TDC[num_tofs_in_view]; // Array of tools to draw each TOF
		erase_channel_array = new TDC[num_tofs_in_view]; // Array of tools to erase individual channels
		stationary_channel_array = new TDC[num_tofs_in_view]; // Array of tools to draw stationary channels
		DrawChangingTOF = new TDC[num_tofs_in_view]; // Tools to draw moving overall TOFs
		EraseChangingTOF = new TDC[num_tofs_in_view];// Tools to erase moving overall TOFs before drawing

		stored_points = new int[num_tofs_in_view]; // Array of points which are stored for changing a TOF

		all_tofs_num_ion_channels = new int[num_tofs_in_view]; // # of ion channels contributing to each TOF

		stationary_points_array_x_pos = new int[num_tofs_in_view];
		stationary_points_array_y_pos = new int[num_tofs_in_view];
		stationary_poe_point_array = new int[num_tofs_in_view];
		moving_points_array_x_pos = new int[num_tofs_in_view];

		current_tofs = new TOF_data[num_tofs_in_view];

		is_tof_real = new boolean[num_tofs_in_view];
		is_tof_changing = new boolean[num_tofs_in_view];
		num_poe_array = new int[num_tofs_in_view];

		number_x_positions = (int) (x_axis - x_offset) + 1;

		redraw_x_positions = new boolean[number_x_positions];
		skip_space = new boolean[num_tofs_in_view];

		origin_x = origin.x;
		origin_y_minus_offset = origin.y - (int)y_offset;

		delta_pen_is_initialized = false;
		old_point = 0;


		this_poe_stored_points = new int[num_tofs_in_view]; // Stored array for changing individual P(E) TOFs
		changing_delta_function_array = new float[num_tofs_in_view];  // Array of all changing delta functions
		overall_individual_tof_array = new float[num_tofs_in_view];  // Stores array of all individual TOFs for each calculated TOF

		moving_old_point = new int[num_tofs_in_view];
		stationary_old_point = new int[num_tofs_in_view];

		// Initialize all x_positions such that they won't be redrawn
		for(i = 0; i < number_x_positions; i++)
		{
			redraw_x_positions[i] = false;
		}


		for(tof_i = 0; tof_i < num_tofs_in_view; tof_i++)
		{
			this_poe_stored_points[tof_i] = 0;
			draw_channel_array[tof_i] = 0;
			channel_pen_array[tof_i] = 0;
			stationary_pen_array[tof_i] = 0;
			erase_channel_array[tof_i] = 0;
			stationary_channel_array[tof_i] = 0;
			DrawChangingTOF[tof_i] = 0;
			EraseChangingTOF[tof_i] = 0;
			moving_old_point[tof_i] = 0;
			stationary_old_point[tof_i] = 0;

			stored_points[tof_i] = 0;

			current_tofs[tof_i] = TOFPOEDoc.GetTOFData((int) (*AssociatedTOFs)[tof_i]);
			this_tof = current_tofs[tof_i];

			is_tof_real[tof_i] = this_tof.GetIsRealTOF();
			is_tof_changing[tof_i] = this_tof.GetIsBeingChanged();

			if(this_tof.GetAssociatedCalc() == -1)
			{
				num_poe_array[tof_i] = this_tof.GetDetachedNumPOEs();
			}
			else
			{
				num_poe_array[tof_i] = TOFPOEDoc.GetNumPOEs();
			}

			y_spacing = y_spacings_array[tof_i];
			minimum = minimum_array[tof_i];
			maximum = maximum_array[tof_i];

			if(!(is_tof_real[tof_i]))
			{
				stationary_pen_array[tof_i] = new TPen**[num_poe_array[tof_i]];
				stationary_channel_array[tof_i] = new TDC**[num_poe_array[tof_i]];
				stationary_old_point[tof_i] = new int*[num_poe_array[tof_i]];

				this_tof_stationary_old_points = stationary_old_point[tof_i];

				this_tof_stationary_pen_array = stationary_pen_array[tof_i];
				this_tof_stationary_channel_array = stationary_channel_array[tof_i];
				all_tofs_num_ion_channels[tof_i] = this_tof.GetNumChannelsArray();
				this_tof_channels_array = all_tofs_num_ion_channels[tof_i];

				thisPOE_TOFColor_array = this_tof.GetTOFColors();


				for(poe_num = 0; poe_num < num_poe_array[tof_i]; poe_num++)
				{
					this_num_ion_channels = this_tof_channels_array[poe_num];
					this_tof_stationary_pen_array[poe_num] = 0;
					this_tof_stationary_channel_array[poe_num] = 0;
					if(this_num_ion_channels > 0)  // i.e. if this P(E) contributes to this TOF
					{
						if(this_num_ion_channels == 1)
						{
							this_num_ion_channels = 0;    // Don't draw TOF for the individual channel if only one channel exists
						}
						thisPOE_TOFColor = thisPOE_TOFColor_array[poe_num];

						// Set up pens for all P(E) TOFs which don't move when the P(E) point is moved
						this_tof_stationary_pen_array[poe_num] = new TPen[this_num_ion_channels + 1];
						this_tof_stationary_channel_array[poe_num] = new TDC[this_num_ion_channels + 1];
						this_poe_stationary_pen_array = this_tof_stationary_pen_array[poe_num];
						this_poe_stationary_channel_array = this_tof_stationary_channel_array[poe_num];


						this_tof_stationary_old_points[poe_num] = new int[this_num_ion_channels + 1];

						for(tof_j = this_num_ion_channels; tof_j >= 0; tof_j--)
						{
							if(tof_j != 0)
							{
								this_poe_stationary_pen_array[tof_j] = new TPen((thisPOE_TOFColor), 1, pen_style[(tof_j - 1) % 4]);
							}
							else
							{
								this_poe_stationary_pen_array[0] = new TPen((thisPOE_TOFColor), 1, PS_SOLID);
							}

							this_poe_stationary_channel_array[tof_j] = new TClientDC(this);
							this_poe_stationary_channel_array[tof_j].SelectObject(this_poe_stationary_pen_array[tof_j]);
						}
					}
				}

				this_tof_individual_tofs = this_tof.GetTOFPointers();
				overall_individual_tof_array[tof_i] = this_tof_individual_tofs;
				if(is_tof_changing[tof_i])
				{
					moving_poe_num = this_tof.GetWhichPOE();
					this_num_ion_channels = this_tof_channels_array[moving_poe_num];
					if(this_num_ion_channels == 1)
					{
						this_num_ion_channels = 0;    // Don't draw TOF for the individual channel if only one channel exists
					}

					moving_old_point[tof_i] = new int[this_num_ion_channels + 1];

					this_tof_individual_poe_tofs = this_tof_individual_tofs[moving_poe_num];
					changing_delta_function_array[tof_i] = this_tof.GetDeltaTOFArrays();
					this_tof_delta_tofs_array = changing_delta_function_array[tof_i];

					original_point_amplitude = this_tof.GetDeltaAmplitude();
					if(original_point_amplitude < 0)
						original_point_amplitude = 1.0;



					if(delta_pen_is_initialized == false)
					{
						thisPOE_TOFColor = thisPOE_TOFColor_array[moving_poe_num];
						delta_tof_pen = new TPen(*thisPOE_TOFColor, 1, PS_SOLID);
						draw_delta_tof = new TClientDC(*this);
						draw_delta_tof.SelectObject(*delta_tof_pen);

						erase_tof = new TClientDC(*this);
						erase_tof.SelectObject(*erase_pen);

						tof_changing_pen = new TPen(*tof_changing_color, 1, PS_SOLID);

						delta_pen_is_initialized = true;
					}


					real_time_pointer = this_tof.RealTimePointer();
					num_tof_points = this_tof.GetTotChannels();

					stored_points[tof_i] = new int[num_tof_points];
					this_tof_stored_points = stored_points[tof_i];

					overall_tof = this_tof.ChannelCountsPointer();

					counter = 0;

					// Draw the new delta function TOF array in the view
					DrawChangingTOF[tof_i] = new TClientDC(this);
					EraseChangingTOF[tof_i] = new TClientDC(this);

					this_tof_DrawChangingTOF = DrawChangingTOF[tof_i];
					this_tof_EraseChangingTOF = EraseChangingTOF[tof_i];


					this_tof_DrawChangingTOF.SelectObject(tof_changing_pen);
					this_tof_EraseChangingTOF.SelectObject(erase_pen);

					delta_tof_pointer = this_tof_delta_tofs_array[0];

					erase_channel_array[tof_i] = new TDC[this_num_ion_channels + 1];

					channel_pen_array[tof_i] = new TPen[this_num_ion_channels + 1];
					draw_channel_array[tof_i] = new TDC[this_num_ion_channels + 1];

					this_tof_draw_channel_array = draw_channel_array[tof_i];
					this_tof_channel_pen_array = channel_pen_array[tof_i];

					this_tof_erase_channel_array = erase_channel_array[tof_i];

					this_poe_stored_points[tof_i] = new int[this_num_ion_channels + 1];
					this_poe_chan_stored_points = this_poe_stored_points[tof_i];

					this_chan_old_point = new int[this_num_ion_channels + 1];
					// Set up arrays for each channel

					for(tof_j = this_num_ion_channels; tof_j >= 0; tof_j--)
					{
						this_chan_old_point[tof_j] = 0;
						this_poe_chan_stored_points[tof_j] = new int[num_tof_points];
						if(tof_j != 0)
						{
							this_tof_channel_pen_array[tof_j] = new TPen(tof_changing_color, 1, pen_style[(tof_j - 1) % 4]);


						}
						else
						{
							this_tof_channel_pen_array[0] = new TPen(tof_changing_color, 1, PS_SOLID);
						}
						this_tof_draw_channel_array[tof_j] = new TClientDC(this);
						this_tof_draw_channel_array[tof_j].SelectObject(this_tof_channel_pen_array[tof_j]);
						this_tof_erase_channel_array[tof_j] = new TClientDC(this);
						this_tof_erase_channel_array[tof_j].SelectObject(erase_pen);
					}



					first_point = true;
					moving_points_array_x_pos[tof_i] = new int[num_tof_points];
					this_tof_moving_x_array = moving_points_array_x_pos[tof_i];
					for(tof_k = 0; tof_k < num_tof_points; tof_k++)
					{
						time_value = real_time_pointer[tof_k];
						if(tof_k < (num_tof_points - 1))
						{
							next_time_value = real_time_pointer[tof_k + 1];
						}
						else
						{
							next_time_value = time_value;
						}
						if((time_value >= starting_time) && (time_value <= ending_time))
						{
							x_pos = origin_x + (int) ((time_value - starting_time) * x_spacing);
							next_x_pos = origin_x + (int) ((next_time_value - starting_time) * x_spacing);
							x_pos_minus_origin_x = x_pos - origin_x;

							lower_lim = min(-3, (x_pos - next_x_pos));
							upper_lim = max(3, (-lower_lim));

							lower_lim = max(-x_pos_minus_origin_x, lower_lim);
							upper_lim = min((number_x_positions - x_pos_minus_origin_x - 1), upper_lim);

							this_tof_amplitude = delta_tof_pointer[tof_k];

							if(this_tof_amplitude > (0.00001 * maximum))
							{
								for(k = lower_lim; k <= upper_lim; k++)
								{
									redraw_x_positions[x_pos_minus_origin_x + k] = true;
								}

								// Store the point number of this non-zero point
								this_tof_stored_points[counter] = tof_k;
								this_tof_moving_x_array[counter] = x_pos;

								// Find which channel(s) contribute to this non-zero TOF and draw them
								for(tof_j = this_num_ion_channels; tof_j >= 0; tof_j--)
								{
									if(this_tof_delta_tofs_array[tof_j][tof_k] != 0.0)
									{
										y_pos_this_chan = y_value_of_scaling_min - (int)((this_tof_individual_poe_tofs[tof_j][tof_k] - minimum) * y_spacing);
										if((y_pos_this_chan > (origin_y_minus_offset)) || (y_pos_this_chan < origin_y_minus_y_axis_plus_offset))
										{
											this_chan_old_point[tof_j] = tof_k - 1;
										}
										else
										{
											this_poe_chan_stored_points[tof_j][counter] = tof_k;
											if((first_point) || ((tof_k - this_chan_old_point[tof_j]) != 1))
											{
												this_tof_draw_channel_array[tof_j].MoveTo(x_pos, y_pos_this_chan);
											}

											this_tof_draw_channel_array[tof_j].LineTo(x_pos, y_pos_this_chan);
											this_chan_old_point[tof_j] = tof_k;
										}
									}
									else
									{
										this_poe_chan_stored_points[tof_j][counter] = -1;
									}
								}

								// Draw the region of the overall TOF which will be moving
								y_pos_overall = y_value_of_scaling_min - (int)((overall_tof[tof_k] - minimum) * y_spacing);
								if((y_pos_overall > origin_y_minus_offset) || (y_pos_overall < origin_y_minus_y_axis_plus_offset))
								{
									old_point = tof_k - 1;
								}
								else
								{
									if((first_point) || ((tof_k - old_point) != 1))
									{
										this_tof_DrawChangingTOF.MoveTo(x_pos, y_pos_overall);
									}
									this_tof_DrawChangingTOF.LineTo(x_pos, y_pos_overall);

									old_point = tof_k;
								}
								counter++;

							}
							y_pos_delta = origin_y_minus_offset - (int)(((this_tof_amplitude * original_point_amplitude) /*- minimum) * y_spacing);


							if(first_point)
							{
								draw_delta_tof.MoveTo(x_pos, y_pos_delta);
								first_point = false;
							}
							draw_delta_tof.LineTo(x_pos, y_pos_delta);
						}

					} // End of loop through all points in TOF

					this_tof_stored_points[counter] = -1;
				} // End of if statement for whether this TOF is being changed
			} // End of if statement for whether this is a calculated TOF


		} // End of first loop through all TOFs


		old_amplitude = original_point_amplitude;

		// Now, go back through ALL TOFs and store points which need to be redrawn in
		// an array called stationary_points[][].  Also store color information for the
		// total TOF colors in the array TDC** tof_draw_array.
		for(tof_i = 0; tof_i < num_tofs_in_view; tof_i++)
		{

			this_tof = current_tofs[tof_i];
			real_time_pointer = this_tof.RealTimePointer();
			num_tof_points = this_tof.GetTotChannels();
			minimum = minimum_array[tof_i];
			maximum = maximum_array[tof_i];
			y_spacing = y_spacings_array[tof_i];


			stationary_points_array_x_pos[tof_i] = new int[num_tof_points];
			stationary_points_array_y_pos[tof_i] = new int[num_tof_points];
			stationary_poe_point_array[tof_i] = new int[num_tof_points];

			counter = 0;
			old_point = 0;
			first_point = true;
			skip_space[tof_i] = new boolean[num_tof_points];
			this_tof_skip_space = skip_space[tof_i];
			for(tof_k = 0; tof_k < num_tof_points; tof_k++)
			{
				overall_tof = this_tof.ChannelCountsPointer();
				time_value = real_time_pointer[tof_k];
				if((time_value >= starting_time) && (time_value <= ending_time))
				{
					x_pos_minus_origin_x = (int) ((time_value - starting_time) * x_spacing);
					if(redraw_x_positions[x_pos_minus_origin_x] == true)
					{
						if(is_tof_real[tof_i])
						{
							stationary_points_array_x_pos[tof_i][counter] = origin_x + x_pos_minus_origin_x;

							stationary_points_array_y_pos[tof_i][counter] = y_value_of_scaling_min - (int)((overall_tof[tof_k] - minimum) * y_spacing);
							this_y_pos = stationary_points_array_y_pos[tof_i][counter];
							if((this_y_pos <= origin_y_minus_offset) && (this_y_pos >= origin_y_minus_y_axis_plus_offset))
							{
								counter++;
							}
						}
						else
						{
							float changing_delta_tof_value;
							if(is_tof_changing[tof_i])
							{
								changing_delta_tof_value = changing_delta_function_array[tof_i][0][tof_k];
							}
							else
							{
								changing_delta_tof_value = 0;
							}

							// Dont draw changes which won't be seen; the changes are still made in the actual
							// TOF when the right button is released, though
							if(changing_delta_tof_value <= (0.0001 * maximum))
							{
								stationary_poe_point_array[tof_i][counter] = tof_k;
								stationary_points_array_x_pos[tof_i][counter] = origin_x + x_pos_minus_origin_x;
								stationary_points_array_y_pos[tof_i][counter] = y_value_of_scaling_min - (int)((overall_tof[tof_k] - minimum) * y_spacing);
								this_y_pos = stationary_points_array_y_pos[tof_i][counter];
								if((this_y_pos <= origin_y_minus_offset) && (this_y_pos >= origin_y_minus_y_axis_plus_offset))
								{
									if(((tof_k - old_point) != 1) || (tof_k == 0))
									{
										this_tof_skip_space[counter] = true;
									}
									else
										this_tof_skip_space[counter] = false;
									counter++;
									old_point = tof_k;
								}
							}
						}


					}
				}
			}
			stationary_points_array_x_pos[tof_i][counter] = -1;
			stationary_points_array_y_pos[tof_i][counter] = -1;
			Color this_tof_color = this_tof.GetTOFColor();
			if(is_tof_real[tof_i])
				tof_pen_array[tof_i] = new TPen(this_tof_color, 4);
			else
				tof_pen_array[tof_i] = new TPen(this_tof_color, 1, PS_SOLID);
			tof_draw_array[tof_i] = new TClientDC(this);
			tof_draw_array[tof_i].SelectObject(tof_pen_array[tof_i]);
		}
		if(redraw_x_positions[0] || redraw_x_positions[1])
		{
			RedrawYAxis = true;
		}
		else
		{
			RedrawYAxis = false;
		}

		return true;

	}
	protected boolean VnChangeTOFDeltaFunctions(float new_amplitude){
		int old_point, new_point;
		int this_num_ion_channels;
		int poe_num;
		int x_pos, y_pos_erase, y_pos_draw;
		int tof_i, tof_j, tof_k;
		int other_poe_num_channels;
		int m;
		int this_x_pos, this_y_pos;
		int this_point;

		int[] this_poe_stationary_old_points;
		int[] this_tof_moving_old_points;
		int[] this_point_array;
		int[] this_tof_channels_array;
		int[] this_tof_stationary_x_array, *this_tof_stationary_y_array;
		int[] this_poe_point_array;
		int[] this_tof_moving_x_array;

		int[][] this_tof_stationary_old_points;
		int[][] this_poe_chan_stored_points;

		float this_delta_tof_value;
		float this_amplitude;
		float erase_amplitude, this_new_amplitude;
		float this_delta_tof_amplitude;
		float y_spacing;
		float point_amplitude;
		float new_point_amplitude = *new_amplitude;
		float minimum;

		float[] delta_tof_pointer;

		float[][] this_other_poe_tofs;
		float[][] this_tof_delta_tofs_array;  // Used to temporarily store delta tof arrays
		float[][] this_tof_individual_poe_tofs;  // Used to temporarily store individual P(E) tofs arrays

		float[][][] this_tof_individual_tofs; // Used to temporarily store overall arrays.

		boolean move_to_new_point;
		boolean first_point;
		boolean[] this_tof_skip_space;

		TOF_data this_tof;

		TDC this_temp_draw_stationary_tof;
		TDC this_tof_DrawChangingTOF;
		TDC this_tof_EraseChangingTOF;
		TDC this_draw_tof;

		TDC[] this_tof_draw_channel_array;
		TDC[] this_tof_erase_channel_array;
		TDC[] this_poe_stationary_channel_array;

		TDC[][] this_tof_stationary_channel_array;

		old_point = 0;
		if(new_point_amplitude < 0)
			new_point_amplitude = 1.0;

		// Redraw y axis if it is affected by changes
		if(RedrawYAxis)
		{
			Pen = new TPen(AxisColor, 2);
			DrawTOF = new TClientDC(*this);
			DrawTOF.SelectObject(*Pen);
			DrawTOF.MoveTo(*origin);
			DrawTOF.LineTo(origin.OffsetBy(0,-(int) y_axis));
		}

		// Don't do anything if a TOF in question hasn't been changed (i.e. just redraw)
		for(tof_i = 0; tof_i < num_tofs_in_view; tof_i++)
		{
			this_tof = current_tofs[tof_i];
			if(is_tof_real[tof_i])    // Just draw points which may be affected
			{
				this_tof_stationary_x_array = stationary_points_array_x_pos[tof_i];
				this_tof_stationary_y_array = stationary_points_array_y_pos[tof_i];

				this_draw_tof = tof_draw_array[tof_i];

				this_x_pos = this_tof_stationary_x_array[0];
				for(tof_k = 0; this_x_pos >= 0; tof_k++)
				{

					this_y_pos = this_tof_stationary_y_array[tof_k];

					this_draw_tof.MoveTo(this_x_pos, this_y_pos);
					this_draw_tof.LineTo(this_x_pos, this_y_pos);

					this_x_pos = this_tof_stationary_x_array[tof_k + 1];
				}
			}
			else
			{  // For those TOF's which are calculated
				this_tof_channels_array = all_tofs_num_ion_channels[tof_i];

				if(is_tof_changing[tof_i])
				{
					this_tof_stationary_old_points = stationary_old_point[tof_i];
					this_tof_moving_old_points = moving_old_point[tof_i];
					for(poe_num = 0; poe_num < num_poe_array[tof_i]; poe_num++)
					{

						this_poe_stationary_old_points = this_tof_stationary_old_points[poe_num];
						other_poe_num_channels = this_tof_channels_array[poe_num];
						if(other_poe_num_channels > 0)
						{
							if(other_poe_num_channels == 1)
								other_poe_num_channels = 0;
							for(tof_j = other_poe_num_channels; tof_j >= 0; tof_j--)
							{
								if(poe_num == moving_poe_num)
									this_tof_moving_old_points[tof_j] = 0;
								this_poe_stationary_old_points[tof_j] = 0;
							}
						}
					}
				}
				this_tof_stationary_x_array = stationary_points_array_x_pos[tof_i];
				this_tof_stationary_y_array = stationary_points_array_y_pos[tof_i];
				this_draw_tof = tof_draw_array[tof_i];
				this_tof_individual_tofs = overall_individual_tof_array[tof_i];

				minimum = minimum_array[tof_i];
				y_spacing = y_spacings_array[tof_i];

				this_tof_stationary_channel_array = stationary_channel_array[tof_i];

				this_x_pos = this_tof_stationary_x_array[0];
				this_poe_point_array = stationary_poe_point_array[tof_i];
				this_tof_skip_space = skip_space[tof_i];

				for(tof_k = 0; this_x_pos >= 0; tof_k++)
				{
					this_point = this_poe_point_array[tof_k];
					// Move through each P(E) which is not changing and draw it in stationary color
					for(poe_num = 0; poe_num < num_poe_array[tof_i]; poe_num++)
					{
						other_poe_num_channels = this_tof_channels_array[poe_num];
						if(other_poe_num_channels > 0)
						{
							if(other_poe_num_channels == 1)
								other_poe_num_channels = 0;
							this_poe_stationary_channel_array = this_tof_stationary_channel_array[poe_num];
							this_other_poe_tofs = this_tof_individual_tofs[poe_num];
							for(m = other_poe_num_channels; m >= 0; m--)
							{
								//this_amplitude = this_other_poe_tofs[m][this_point];
								y_pos_draw = y_value_of_scaling_min - (int)((this_other_poe_tofs[m][this_point] - minimum) * y_spacing);
								if((y_pos_draw <= origin_y_minus_offset) && (y_pos_draw >= origin_y_minus_y_axis_plus_offset))
								{
									if(this_tof_skip_space[tof_k])
									{
										this_poe_stationary_channel_array[m].MoveTo(this_x_pos, y_pos_draw);
									}
									this_poe_stationary_channel_array[m].LineTo(this_x_pos, y_pos_draw);
								}
							} // End of loop through P(E) channels
						} // Only do this if the P(E) contributes to the TOF (i.e. if # channels != 0)
					} // End of loop through all current P(E)'s


					if(this_tof_skip_space[tof_k])
					{
						this_draw_tof.MoveTo(this_x_pos, this_tof_stationary_y_array[tof_k]);
					}
					this_draw_tof.LineTo(this_x_pos, this_tof_stationary_y_array[tof_k]);

					this_x_pos = this_tof_stationary_x_array[tof_k + 1];
				}

			}

		}

		for(tof_i = 0; tof_i < num_tofs_in_view; tof_i++)
		{
			this_tof = current_tofs[tof_i];
			if(!(is_tof_real[tof_i]))
			{
				if(is_tof_changing[tof_i])
				{
					this_point_array = stored_points[tof_i];
					this_poe_chan_stored_points = this_poe_stored_points[tof_i];

					this_tof_delta_tofs_array = changing_delta_function_array[tof_i];

					point_amplitude = old_amplitude;

					overall_tof = this_tof.ChannelCountsPointer();
					y_spacing = y_spacings_array[tof_i];
					minimum = minimum_array[tof_i];

					this_tof_channels_array = all_tofs_num_ion_channels[tof_i];
					this_num_ion_channels = this_tof_channels_array[moving_poe_num];
					if(this_num_ion_channels == 1)
						this_num_ion_channels = 0;    // Don't draw TOF for the individual channel if only one channel exists

					delta_tof_pointer = this_tof_delta_tofs_array[0];


					this_tof_DrawChangingTOF = DrawChangingTOF[tof_i];
					this_tof_EraseChangingTOF = EraseChangingTOF[tof_i];

					this_tof_draw_channel_array = draw_channel_array[tof_i];

					this_tof_erase_channel_array = erase_channel_array[tof_i];
					this_tof_stationary_channel_array = stationary_channel_array[tof_i];


					this_tof_individual_tofs = overall_individual_tof_array[tof_i];

					first_point = true;
					new_point = this_point_array[0];
					this_tof_moving_x_array = moving_points_array_x_pos[tof_i];

					this_tof_stationary_old_points = stationary_old_point[tof_i];
					this_tof_moving_old_points = moving_old_point[tof_i];
					for(tof_k = 0; new_point >= 0; tof_k++)
					{

						if((new_point - old_point) != 1)
							move_to_new_point = true;
						else
							move_to_new_point = false;
						x_pos = this_tof_moving_x_array[tof_k];

						// Run through all other P(E) TOFs and draw them in their stationary color
						for(poe_num = 0; poe_num < num_poe_array[tof_i]; poe_num++)
						{
							if(poe_num != moving_poe_num)
							{
								other_poe_num_channels = this_tof_channels_array[poe_num];
								this_poe_stationary_old_points = this_tof_stationary_old_points[poe_num];
								if(other_poe_num_channels > 0)
								{
									if(other_poe_num_channels == 1)
										other_poe_num_channels = 0;
									this_tof_individual_poe_tofs = this_tof_individual_tofs[poe_num];
									this_poe_stationary_channel_array = this_tof_stationary_channel_array[poe_num];

									for(m = other_poe_num_channels; m >= 0; m--)
									{
										//this_amplitude = this_tof_individual_poe_tofs[m][new_point];
										y_pos_draw = y_value_of_scaling_min - (int)((this_tof_individual_poe_tofs[m][new_point] - minimum) * y_spacing);
										if(y_pos_draw <= origin_y_minus_offset)
										{
											if((new_point - this_poe_stationary_old_points[m]) != 1)
											{
												this_poe_stationary_channel_array[m].MoveTo(x_pos, y_pos_draw);
											}
											this_poe_stationary_channel_array[m].LineTo(x_pos, y_pos_draw);
											this_poe_stationary_old_points[m] = new_point;
										}
									}
								}
							}
						}

						this_tof_individual_poe_tofs = this_tof_individual_tofs[moving_poe_num];
						this_poe_stationary_channel_array = this_tof_stationary_channel_array[moving_poe_num];
						this_poe_stationary_old_points = this_tof_stationary_old_points[moving_poe_num];
						for(tof_j = this_num_ion_channels; tof_j >= 0; tof_j--)
						{

							this_amplitude = this_tof_individual_poe_tofs[tof_j][new_point];
							this_temp_draw_stationary_tof = this_poe_stationary_channel_array[tof_j];
							if(this_poe_chan_stored_points[tof_j][tof_k] == -1)
							{
								// Just draw the TOF in its original color

								y_pos_draw = y_value_of_scaling_min - (int)((this_amplitude - minimum) * y_spacing);
								if(y_pos_draw <= origin_y_minus_offset)
								{
									if((new_point - this_poe_stationary_old_points[tof_j]) != 1)
									{
										this_temp_draw_stationary_tof.MoveTo(x_pos, y_pos_draw);
									}
									this_temp_draw_stationary_tof.LineTo(x_pos, y_pos_draw);
									this_poe_stationary_old_points[tof_j] = new_point;
								}
							}
							else
							{
								// Erase the old TOF and draw the new one in the changing color
								//orig_amplitude = this[tof_j][tof_k];
								this_delta_tof_amplitude = this_tof_delta_tofs_array[tof_j][new_point];
								erase_amplitude = this_amplitude + (point_amplitude - original_point_amplitude) * this_delta_tof_amplitude;

								this_new_amplitude = this_amplitude + (new_point_amplitude - original_point_amplitude) * this_delta_tof_amplitude;

								y_pos_erase = y_value_of_scaling_min - (int)((erase_amplitude - minimum) * y_spacing);
								y_pos_draw = y_value_of_scaling_min - (int)((this_new_amplitude - minimum) * y_spacing);
								if(y_pos_erase > origin_y_minus_offset)
								{
									y_pos_erase = origin_y_minus_offset;
								}
								if(y_pos_draw > origin_y_minus_offset)
								{
									y_pos_draw = origin_y_minus_offset;
								}
								if((new_point - this_tof_moving_old_points[tof_j]) != 1)
								{
									this_tof_erase_channel_array[tof_j].MoveTo(x_pos, y_pos_erase);
									this_tof_draw_channel_array[tof_j].MoveTo(x_pos, y_pos_draw);

								}
								this_tof_erase_channel_array[tof_j].LineTo(x_pos, y_pos_erase);
								this_tof_draw_channel_array[tof_j].LineTo(x_pos, y_pos_draw);
								this_tof_moving_old_points[tof_j] = new_point;
							}
						}

						// Draws the changing P(E) "delta" function
						this_amplitude = delta_tof_pointer[new_point];
						y_pos_erase = origin_y_minus_offset - (int)(((this_amplitude * point_amplitude) /*- minimum) * y_spacing);
						y_pos_draw = origin_y_minus_offset - (int)(((this_amplitude * new_point_amplitude) /*- minimum) * y_spacing);

						if(first_point || move_to_new_point)
						{
							erase_tof.MoveTo(x_pos, y_pos_erase);
							draw_delta_tof.MoveTo(x_pos, y_pos_draw);
						}
						erase_tof.LineTo(x_pos, y_pos_erase);
						draw_delta_tof.LineTo(x_pos, y_pos_draw);

						// Finally, change the overall TOF by adding the correct amount!
						this_amplitude = overall_tof[new_point];
						this_delta_tof_value = delta_tof_pointer[new_point];
						erase_amplitude = this_amplitude + (point_amplitude - original_point_amplitude) * this_delta_tof_value;

						this_new_amplitude = this_amplitude + (new_point_amplitude - original_point_amplitude) * this_delta_tof_value;

						y_pos_erase = y_value_of_scaling_min - (int)((erase_amplitude - minimum) * y_spacing);
						y_pos_draw = y_value_of_scaling_min - (int)((this_new_amplitude - minimum) * y_spacing);
						if(y_pos_erase > origin_y_minus_offset)
						{
							y_pos_erase = origin_y_minus_offset;
						}
						if(y_pos_draw > origin_y_minus_offset)
						{
							y_pos_draw = origin_y_minus_offset;
						}
						if(first_point || move_to_new_point)
						{
							this_tof_EraseChangingTOF.MoveTo(x_pos, y_pos_erase);
							this_tof_DrawChangingTOF.MoveTo(x_pos, y_pos_draw);

							first_point = false;
						}
						this_tof_EraseChangingTOF.LineTo(x_pos, y_pos_erase);
						this_tof_DrawChangingTOF.LineTo(x_pos, y_pos_draw);


						old_point = new_point;
						new_point = this_point_array[tof_k + 1];

					}
				}
			}
		}

		if(show_min_line)
		{
			MinLineDraw = new TClientDC(*this);
			MinLineDraw.SelectObject(*MinLineDrawPen);

			MinLineDraw.MoveTo(origin.x, moving_min_y_position);
			MinLineDraw.LineTo(origin.x + x_axis, moving_min_y_position);
			delete MinLineDraw;
			lines_present = true;
		}

		if(show_max_line)
		{
			MaxLineDraw = new TClientDC(*this);
			MaxLineDraw.SelectObject(*MaxLineDrawPen);

			MaxLineDraw.MoveTo(origin.x, moving_max_y_position);
			MaxLineDraw.LineTo(origin.x + x_axis, moving_max_y_position);
			lines_present = true;
		}


		old_amplitude = new_point_amplitude;
		return true;
	}

	protected boolean VnRemoveTOFDeltaFunctions(int index){
		int tof_i, tof_j;
		int this_num_ion_channels;
		int poe_num;

		TPen[] this_poe_stationary_pen_array;
		TDC[] this_poe_stationary_channel_array;

		TPen[][] this_tof_stationary_pen_array;
		TDC[][] this_tof_stationary_channel_array;

		TDC[] this_tof_draw_channel_array;
		TPen[] this_tof_channel_pen_array;
		int[][] this_poe_chan_stored_points;
		TDC[] this_tof_erase_channel_array;

		int[] this_tof_channels_array;

		int[][] this_tof_stationary_old_points;


		for(tof_i = 0; tof_i < num_tofs_in_view; tof_i++)
		{
			this_tof_stationary_pen_array = stationary_pen_array[tof_i];
			this_tof_stationary_channel_array = stationary_channel_array[tof_i];
			this_tof_channels_array = all_tofs_num_ion_channels[tof_i];
			this_tof_stationary_old_points = stationary_old_point[tof_i];
			if(!is_tof_real[tof_i])
			{
				for(poe_num = 0; poe_num < num_poe_array[tof_i]; poe_num++)
				{
					this_num_ion_channels = this_tof_channels_array[poe_num];
					if(this_num_ion_channels > 0)
					{
						if(this_num_ion_channels == 1)
						{
							this_num_ion_channels = 0;
						}
						this_poe_stationary_pen_array = this_tof_stationary_pen_array[poe_num];
						this_poe_stationary_channel_array = this_tof_stationary_channel_array[poe_num];
					}
				}

				if(is_tof_changing[tof_i])
				{
					this_num_ion_channels = this_tof_channels_array[moving_poe_num];
					if(this_num_ion_channels == 1)
					{
						this_num_ion_channels = 0;
					}

					this_tof_draw_channel_array = draw_channel_array[tof_i];
					this_tof_channel_pen_array = channel_pen_array[tof_i];
					this_poe_chan_stored_points = this_poe_stored_points[tof_i];
					this_tof_erase_channel_array = erase_channel_array[tof_i];

				}
			}
		}

		MinMaxHaveChanged = true;
		ResidsHaveChanged = true;
		this.Invalidate();
		return true;
	}*/
	
	private class tofViewPane extends JPanel{
		
		public tofViewPane(){
			super();
			this.setBackground(Color.white);
		}
		@Override
		public void paintComponent(Graphics g){
			// Will do this in the view so can control the size of the TOF
			int i;
			TOF_data tof;
			Resid_data residual;

			int[] view_tof_nums;  // Holds 2 numbers of TOFs used in each residual

			int num_tofs, this_num_channels, num_resids;
			int x_pos, y_pos, j, k, m; 
			int origin_x, old_y_position, old_x_position, tof_index;

			float time_value;
			float minimum_time, maximum_time, lower_lim, upper_lim;
			float[] extrema;
			float maximum = 0, minimum, y_spacing, window_width, window_height;
			float font_width, font_height, graph_top, graph_left;
			float[] tof_pointer, real_time_pointer;
			Color TOFColor;

			boolean first_point;
			Graphics2D g2 = (Graphics2D)g;

			// Draw x and y axes relative to the total window size
			
			Rectangle window_rect = this.getBounds();
			window_rect.setLocation(this.getInsets().left, this.getInsets().top);
			window_width = (float) (window_rect.getWidth());
			window_height = (float) (window_rect.getHeight());
			
			//font_width = (float) Math.max(2, 0.03*window_width);
			font_height = (float) Math.max(8, 0.06*window_height);

			if(font_height > 16)
			{
				//font_width = Math.min(6, font_width);
				font_height = 16;
			}
			else
			{
				//font_width = (float) Math.min(font_width, (font_height / 2.4));
			}

			Font font = g2.getFont().deriveFont(font_height); 
			Font sidefont = g2.getFont().deriveFont(0.9f*font_height);
			
			graph_top = this.getInsets().top;
			graph_left = this.getInsets().left;

			x_axis =  window_width -  font_height;
			x_offset = 0.01f * x_axis;
			y_axis = window_height - font_height ;
			y_offset = 0.02f * y_axis;
			
			origin = new Point((int) (graph_left + font_height),(int) (graph_top + (int) y_axis));

			origin_y_minus_offset = origin.y - (int)y_offset;
			origin_y_minus_y_axis_plus_offset = (int)(origin.y - y_axis + y_offset);
			origin_x = origin.x;
			
			g2.drawLine(origin.x, origin.y, origin.x, origin.y-(int) y_axis);
			g2.drawLine(origin.x, origin.y, origin.x+(int)x_axis, origin.y);		




			// Draw ticks on x axis

			g2.drawLine(origin.x, origin.y, origin.x, origin.y+2);
			g2.drawLine(origin.x+(int)(x_axis-x_offset), origin.y, origin.x+(int)(x_axis-x_offset), origin.y+2);

			// Draw ticks on y axis 
			
			g2.drawLine(origin.x, origin.y-(int)y_offset, origin.x-2, origin.y-(int)y_offset);
			g2.drawLine(origin.x, origin.y+(int)(-y_axis+y_offset), origin.x-2, origin.y+(int)(-y_axis+y_offset));


			Rectangle x_axis_rect = new Rectangle(origin.x, origin.y+4, origin.x+ (int)x_axis, origin.y+50);
			Rectangle y_axis_rect = new Rectangle(origin.x + (int)(-4-font_height), origin.y, origin.x-4, origin.y-(int)(y_axis));

			g2.setColor(Color.red);
			g2.setFont(font);
			FontMetrics metrics = g2.getFontMetrics(font);
			g2.drawString("Flight Time (µs)", (int)x_axis_rect.getCenterX()-metrics.stringWidth("Flight Time (µs)"), origin.y+font_height);

			g2.setFont(sidefont);
			AffineTransform orig = g2.getTransform();
			g2.rotate(Math.PI/2);
			g2.drawString("N(t) (arb. units)", origin.y-(y_axis/2)-metrics.stringWidth("N(t) (arb. units)"), -(int)(origin.x-font_height) );
			g2.setColor(Color.black);
			g2.setTransform(orig);

			if(AssociatedTOFs.size() == 0){
				return;
			}
			tof = AssociatedTOFs.get(0);  // First TOF in display!


			// Determine which points to draw
			minimum_time = Float.parseFloat(param_dialog.GetDefault1());
			maximum_time = Float.parseFloat(param_dialog.GetDefault2());
			lower_lim = Float.parseFloat(param_dialog.GetValue1());
			upper_lim = Float.parseFloat(param_dialog.GetValue2());
			if ((lower_lim < (int) (minimum_time - 1)) || (upper_lim > (int) (maximum_time + 1))
					|| (lower_lim >= upper_lim))
			{
				///////////////////////////////
				// Insert error message here //
				///////////////////////////////

				real_time_pointer = tof.RealTimePointer();
				starting_time = real_time_pointer[0];
				ending_time = real_time_pointer[tof.GetTotChannels() - 1];

				String lower_lim_recall = "";  // To recall old lower limit
				String upper_lim_recall = "";  // To recall new lower limit
				lower_lim_recall = "" + starting_time;
				upper_lim_recall = "" + ending_time;
				param_dialog.SetValue1(lower_lim_recall);
				param_dialog.SetValue2(upper_lim_recall);
			}
			else
			{
				starting_time = lower_lim;
				ending_time = upper_lim;
			}


			// Run through each TOF in this view and find max and min values
			// Start with the scaling TOF if there is one

			if(scale_to_tof)
			{
				time_of_flight = AssociatedTOFs.get(current_scaling_TOF);
				extrema = time_of_flight.GetMaxMinCounts(starting_time, ending_time);
				maximum = extrema[0];
				minimum = extrema[1];

				if(Math.abs(maximum - minimum) < 1* Math.pow(10, -30))
				{
					y_spacing = (y_axis - 2 * y_offset)/2;
				}
				else
				{
					y_spacing = (y_axis - 2 * y_offset)/(maximum - minimum);
				}

				y_axis_minimum = minimum;
				y_axis_maximum = maximum;
				y_axis_spacing = y_spacing;

				y_spacings_array[current_scaling_TOF] = y_spacing;
				minimum_array[current_scaling_TOF] = fixed_min_scaling_TOF_counts;
				maximum_array[current_scaling_TOF] = fixed_max_scaling_TOF_counts;

				y_value_of_scaling_min = (int) (origin_y_minus_offset - (fixed_min_scaling_TOF_counts - minimum) * y_spacing);
				y_value_of_scaling_max = (int) (origin_y_minus_offset - (fixed_max_scaling_TOF_counts - minimum) * y_spacing);
			}
			else
			{
				y_value_of_scaling_min = origin_y_minus_offset;
				y_value_of_scaling_max = (int) (origin_y_minus_offset - y_axis + 2 * y_offset);
			}


			// Only draw in TOFs which should be shown in this view
			num_tofs = AssociatedTOFs.size();

			if(param_dialog.GetHasChanged() == true)
			{
				MinMaxHaveChanged = true;
				ResidsHaveChanged = true;
			}

			for(m = 0; m < num_tofs; m++)
			{
				tof = AssociatedTOFs.get(m);
				if(tof.GetIsRealTOF())
				{
					TOFColor = tof.GetTOFColor();
					g2.setColor(TOFColor);

					tof_pointer = tof.ChannelCountsPointer();
					// Draw TOF points using small dots.

					real_time_pointer = tof.RealTimePointer();
					float time_value_2;
					int max_tof_channels = tof.GetTotChannels();
					x_spacing = (x_axis - x_offset)/(ending_time - starting_time);
					if(MinMaxHaveChanged)
					{
						if(scale_to_tof)
						{
							extrema = tof.GetMaxMinCounts(0, 0);
						}
						else
						{
							extrema = tof.GetMaxMinCounts(starting_time, ending_time);
						}

						maximum = extrema[0];
						if(scale_to_tof && average_baseline)
						{
							minimum = tof.GetAverageCounts(baseline_time1, baseline_time2);
						}
						else
						{
							minimum = extrema[1];
						}

						if(Math.abs(maximum - minimum) < 1e-30)
						{
							y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / 2;
						}
						else
						{
							y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / (maximum - minimum);
						}

						if((scale_to_tof) && (m == current_scaling_TOF))
						{
							y_spacing = y_spacings_array[m];
							minimum = minimum_array[m];
						}
						else
						{
							y_spacings_array[m] = y_spacing;
							minimum_array[m] = minimum;
							maximum_array[m] = maximum;
						}
					}
					else
					{
						y_spacing = y_spacings_array[m];
						minimum = minimum_array[m];
					}

					for(k = 0; k < max_tof_channels; k++)
					{

						time_value = real_time_pointer[k];
						if((time_value >= starting_time) && (time_value <= ending_time))
						{
							y_pos = y_value_of_scaling_min - (int) ((tof_pointer[k] - minimum) * y_spacing);
							if((erased_y_position == -1) || (Math.abs(y_pos - erased_y_position) <= 2))
							{
								if((y_pos <= origin_y_minus_offset) && (y_pos >= origin_y_minus_y_axis_plus_offset))
								{
									x_pos = origin_x + (int) ((time_value - starting_time) * x_spacing);
									g2.drawOval(x_pos, y_pos, 4, 4);//4 = width, height
								}else{
								}
							}
						}else{
						}
					}
				}
			} // Move through all TOFs and only draw the real ones first


			// Now draw the calculated ones
			for(m = 0; m < num_tofs; m++)
			{
				tof = AssociatedTOFs.get(m);
				if(tof.GetIsRealTOF() == false)
				{
					int[] num_channels = tof.GetNumChannelsArray();
					float[][][] all_tofs_pointer = tof.GetTOFPointers();
					Color[] TOFColors = tof.GetTOFColors();
					TOFColor = tof.GetTOFColor(); // Color of overall TOF
					g2.setColor(TOFColor);
					
					real_time_pointer = tof.RealTimePointer();
					int max_tof_channels = tof.GetTotChannels();
					x_spacing = (x_axis - x_offset)/(ending_time - starting_time);
					if(MinMaxHaveChanged)
					{
						if(scale_to_tof)
						{
							extrema = tof.GetMaxMinCounts(0, 0);
						}
						else
						{
							extrema = tof.GetMaxMinCounts(starting_time, ending_time);
						}

						maximum = extrema[0];
						if(scale_to_tof && average_baseline)
						{
							minimum = tof.GetAverageCounts(baseline_time1, baseline_time2);
						}
						else
						{
							minimum = extrema[1];
						}

						if(Math.abs(maximum - minimum) < 1e-30)
						{
							y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / 2;
						}
						else
						{
							y_spacing = (y_value_of_scaling_min - y_value_of_scaling_max) / (maximum - minimum);
						}

						if((scale_to_tof) && (m == current_scaling_TOF))
						{
							y_spacing = y_spacings_array[m];
							minimum = minimum_array[m];
						}
						else
						{
							y_spacings_array[m] = y_spacing;
							minimum_array[m] = minimum;
							maximum_array[m] = maximum;
						}
					}

					else
					{
						y_spacing = y_spacings_array[m];
						minimum = minimum_array[m];
					}

					for(i = 0; i < tof.GetNumCurrentPOEs(); i++)
					{
						if(all_tofs_pointer[i] != null)
						{
							this_num_channels = num_channels[i];
							if(this_num_channels == 1)
								this_num_channels = 0;    // Don't draw TOF for the individual channel if only one channel exists
								for(j = this_num_channels; j >= 0 ; j--)   // Backwards, so total P(E) TOF drawn last
								{

									if((j != 0) || (tof.GetNumIncludedPOEs() > 1))   // i.e. skip plotting individual TOFs for each
										// contributing P(E) if only one contributes
									{
										if(j == 0){
											//Pen = new TPen((TOFColors[i]), 1, PS_SOLID);
										}else{
											//Pen = new TPen((TOFColors[i]), 1, pen_style[(j - 1) % 4]);
										}
										tof_pointer = all_tofs_pointer[i][j];


										first_point = true;

										old_y_position = 0;
										old_x_position = 0;
										Point og = new Point(0,0);
										for(k = 0; k < max_tof_channels; k++)
										{
											time_value = real_time_pointer[k];
											if((time_value >= starting_time) && (time_value <= ending_time))
											{
												x_pos = origin_x + (int) ((time_value - starting_time)* x_spacing);
												y_pos = y_value_of_scaling_min - (int)((tof_pointer[k] - minimum) *y_spacing);
												if(erased_y_position == -1)
												{
													if((y_pos > origin_y_minus_offset) || (y_pos < origin_y_minus_y_axis_plus_offset))
													{
														first_point = true;
													}
													else
													{
														if(first_point)
														{
															og.x = x_pos;
															og.y = y_pos;
															first_point = false;
														}
														g2.drawLine(og.x, og.y, x_pos, y_pos);
														og.x = x_pos;
														og.y = y_pos;
													}
												}

												else
												{
													if(first_point)
													{
														first_point = false;
													}
													else
													{
														if(((old_y_position - erased_y_position) * (y_pos - erased_y_position)) <= 0)
														{
															g2.drawLine(old_x_position, old_y_position, x_pos, y_pos);
														}
													}
													old_y_position = y_pos;
													old_x_position = x_pos;
												}

											}
										}
									}
								}
						}
					}

					// Now draw the final overall TOF
					g2.setColor(tof.GetTOFColor());

					tof_pointer = tof.GetTotalTOF();

					first_point = true;
					old_y_position = 0;
					old_x_position = 0;
					Point og = new Point(0,0);
					for(k = 0; k < max_tof_channels; k++)
					{
						time_value = real_time_pointer[k];
						if((time_value >= starting_time) && (time_value <= ending_time))
						{
							y_pos = y_value_of_scaling_min - (int)((tof_pointer[k] - minimum)*y_spacing);
							x_pos = origin_x + (int) ((time_value - starting_time) * x_spacing);
							if(erased_y_position == -1)
							{
								if((y_pos > origin_y_minus_offset) || (y_pos < origin_y_minus_y_axis_plus_offset))
								{
									first_point = true;
								}
								else
								{
									if(first_point)
									{
										og.x = x_pos;
										og.y = y_pos;
										first_point = false;
									}
									g2.drawLine(og.x, og.y, x_pos, y_pos);
									og.x = x_pos;
									og.y = y_pos;
								}
							}
							else
							{
								if(first_point)
								{
									first_point = false;
								}
								else
								{
									if(((old_y_position - erased_y_position) * (y_pos - erased_y_position)) <= 0)
									{
										g2.drawLine(old_x_position, old_y_position, x_pos, y_pos);
									}
								}
								old_y_position = y_pos;
								old_x_position = x_pos;
							}

						}else{
						}
					}
				}
			}

			int right_edge;
			String string;
			right_edge = (int) Math.min((x_axis + 2 * metrics.stringWidth("1000.00")), (window_width - 2 - origin.x));
			Rectangle start_time_rect = new Rectangle(origin.x -4, origin.y+2, origin.x+4, origin.y+25);
			Rectangle end_time_rect = new Rectangle((int) (origin.x + (right_edge - 5 * metrics.stringWidth("1000.00"))), origin.y+ 2,
					origin.x+right_edge, origin.y+25);

			if(scale_to_tof == false)
			{
				y_axis_minimum = minimum_array[0];
				y_axis_maximum = maximum_array[0];
				y_axis_spacing = y_spacings_array[0];
			}

			if((y_axis_maximum == 0) || (((y_axis_maximum - y_axis_minimum) / y_axis_maximum) < 1e-8))
			{
				y_axis_minimum = 0;
			}

			g2.setColor(Color.black);
			string = "" + y_axis_minimum;
			Rectangle min_rect = new Rectangle((int)(origin.x + (-2 - font_height)), (int)(origin.y+ (-y_offset + 2)),
					origin.x -4, (int)(origin.y + (-4-y_offset)));
			g2.setFont(sidefont);
			g2.drawString(string, (int) min_rect.getCenterX(), (int)min_rect.getCenterY());

			string = "" + y_axis_maximum;
			Rectangle max_rect = new Rectangle((int)(origin.x + (-2 - font_height)), (int)(origin.y +((string.length() * 0.7 * metrics.stringWidth("1000.00"))+y_offset-y_axis)),
					origin.x -4, (int)(origin.y + ((3 * metrics.stringWidth("1000.00"))+y_offset-y_axis)));
			g2.drawString(string, (int) max_rect.getCenterX(), (int)max_rect.getCenterY());

			g2.setFont(font);
			string = "" + starting_time;
			g2.drawString(string, start_time_rect.x, start_time_rect.y);
			string = "" + ending_time;
			g2.drawString(string, (int) end_time_rect.getCenterX(), (int)end_time_rect.getCenterY());

			IsDrawn = true;
			if(max_min_never_been_set)
			{
				moving_min_y_pos_percent = 0.0f;
				moving_max_y_pos_percent = 1.0f;
				min_y_pos_percent = 0.0f;
				max_y_pos_percent = 1.0f;
				minimum_y_position = origin.y - 1;
				maximum_y_position = (int)(origin.y - y_axis + 1);
				moving_min_y_position = minimum_y_position;
				moving_max_y_position = maximum_y_position;
				max_min_never_been_set = false;
			}

			// The following loop is entered if the time range of the TOF has changed (meaning the
					// position of the lines relative to the TOFs may have changed as well) or if the
			// size of the view has changed.
			if((param_dialog.GetHasChanged() == true) || reset_percents)
			{
				if(scale_to_tof)
				{

					moving_min_y_position = (int) (y_value_of_scaling_min - (moving_min_counts_scaling_TOF - minimum_array[current_scaling_TOF])
							* y_spacings_array[current_scaling_TOF]);

					moving_max_y_position = (int) (y_value_of_scaling_min - (moving_max_counts_scaling_TOF - minimum_array[current_scaling_TOF])
							* y_spacings_array[current_scaling_TOF]);

					minimum_y_position = (int) (y_value_of_scaling_min - (min_counts_scaling_TOF - minimum_array[current_scaling_TOF])
							* y_spacings_array[current_scaling_TOF]);

					maximum_y_position = (int) (y_value_of_scaling_min - (max_counts_scaling_TOF - minimum_array[current_scaling_TOF])
							* y_spacings_array[current_scaling_TOF]);
				}
				else
				{
					if(temp_scaling_tof >= 0)
					{
						minimum_y_position = y_value_of_scaling_min - (int)((average_counts_in_range -
								minimum_array[temp_scaling_tof]) * y_spacings_array[temp_scaling_tof]);
						moving_min_y_position = minimum_y_position;
					}
					else
					{
						minimum_y_position = (origin.y - 1) + (int)(min_y_pos_percent * (2.0 - y_axis));
						moving_min_y_position = (origin.y - 1) + (int)(moving_min_y_pos_percent * (2.0 - y_axis));
					}
					maximum_y_position = (origin.y - 1) + (int)(max_y_pos_percent * (2.0 - y_axis));
					moving_max_y_position = (origin.y - 1) + (int)(moving_max_y_pos_percent * (2.0 - y_axis));
				}
				reset_percents = false;
			}


			if(show_min_line)
			{
				if(moving_min_y_position <= (origin.y - 1))
				{
					g2.drawLine(origin_x, moving_min_y_position, (int) (origin_x + x_axis), moving_min_y_position);
				}
				lines_present = true;
			}

			if(show_max_line)
			{
				if(moving_max_y_position >= (int)(origin.y - y_axis + 1))
				{
					g2.drawLine(origin_x, moving_max_y_position, (int) (origin_x+x_axis), moving_max_y_position);
				}
				lines_present = true;
			}

			// Send proper information to associated residuals
			if(ResidsHaveChanged)
			{
				num_resids = TOFPOEDoc.GetNumResids();
				float slope0, slope1, intercept0, intercept1;
				float scale_minimum, scale_maximum, temp_minimum, temp_maximum;
				for(i = 0; i < num_resids; i++)
				{
					residual = TOFPOEDoc.GetResidData(i);
					if(residual.GetAssociatedTOFView().equals(this))
					{
						// Find out which TOFs are used in this residual
						view_tof_nums = residual.GetViewTOFNums();

						// Find slope and intercept for conversion of this TOF data to values comparable
						// to the scaling TOF
						// i.e. scaled_value = m * actual_value + b
						// Then, can determine scaled_value from an actual value input

						if(scale_to_tof)
						{
							scale_minimum = minimum_array[current_scaling_TOF];
							scale_maximum = maximum_array[current_scaling_TOF];

							temp_minimum = minimum_array[view_tof_nums[0]];
							temp_maximum = maximum_array[view_tof_nums[0]];

							slope0 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
							intercept0 = (float) ((scale_minimum + scale_maximum - slope0 * (temp_minimum + temp_maximum)) / 2.0);

							temp_minimum = minimum_array[view_tof_nums[1]];
							temp_maximum = maximum_array[view_tof_nums[1]];

							slope1 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
							intercept1 = (float) ((scale_minimum + scale_maximum - slope1 * (temp_minimum + temp_maximum)) / 2.0);

						}
						else
						{
							slope0 = 1.0f;
							intercept0 = 0.0f;

							scale_minimum = minimum_array[view_tof_nums[0]];
							scale_maximum = maximum_array[view_tof_nums[0]];

							temp_minimum = minimum_array[view_tof_nums[1]];
							temp_maximum = maximum_array[view_tof_nums[1]];

							slope1 = (scale_minimum - scale_maximum) / (temp_minimum - temp_maximum);
							intercept1 = (float) ((scale_minimum + scale_maximum - slope1 * (temp_minimum + temp_maximum)) / 2.0);
						}
						residual.SetScalingParams(slope0, intercept0, slope1, intercept1);
					}
				}
				TOFPOEDoc.RedrawAllResids();
				ResidsHaveChanged = false;
			}
			MinMaxHaveChanged = false;
			return;
		}
	}
}

