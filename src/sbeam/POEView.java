package sbeam;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;

public class POEView extends JInternalFrame implements MouseInputListener, InternalFrameListener{

	protected MainFrame parent;
	protected String Title;
   	protected float starting_energy, ending_energy;
   	protected float x_axis, y_axis;
   	protected float x_spacing, y_spacing;
   	protected float x_offset, y_offset;
   	protected int starting_x, starting_y;
   	protected float current_average_E;
   	protected float maximum, minimum;

   	protected float delta_energy;
      
   	protected boolean GrabbedPoint;
   	protected boolean LeftButtonDepressed, RightButtonDepressed;
   	protected boolean ArePOEsFrozen;

   	protected Point original_dot = new Point();
   	protected Point old_dot_position;
   	protected POE_data change_poe;
   	protected int change_poe_index;
   	protected int change_poe_point_number;

  	protected int[][] plot_points_x;
   	protected int[][] plot_points_y;

   	protected int[][] rel_pts_x;
   	protected int[][] rel_pts_y;

   	protected int[] num_close_pts;
   	protected int num_poes;
   	
   	protected Point changeIndices;

   	protected boolean KeyIsDepressed;

   	protected JLabel x_pos_gadget;
   	protected String x_gadget_text;
   	protected JLabel y_pos_gadget;
   	protected String y_gadget_text;

   	protected Point mouse_position;

   	protected Color AxisColor;

   	protected Param_Dialog param_dialog;
   	protected List_Dialog poe_list_dialog;
   	protected String[] list_box_text;

   	protected ArrayList<POE_data> AssociatedPOEs;

   	protected Color POEColor;
   	
   	protected int ViewNumber;
   	protected int UnfrozenPOENumView, UnfrozenPOENumActual;
   	
 	protected TOFPOEDocument TOFPOEDoc;
 	protected POE_data energy_distribution;
 	protected POE_data changing_poe;


 	public POEView(TOFPOEDocument doc, MainFrame p) {
 		// TODO Auto-generated constructor stub
 		super("Title", true, true, true);
 		parent = p;
 		TOFPOEDoc = doc;
 		x_pos_gadget = TOFPOEDoc.GetXPosGadget();
 		y_pos_gadget = TOFPOEDoc.GetYPosGadget();
 		System.out.println("X: " + x_pos_gadget);
 		this.addFocusListener(parent);
 		this.addInternalFrameListener(this);
 		
 		x_gadget_text = "";
 		y_gadget_text = "";

 		mouse_position = new Point();

 		this.setTitle("POE View");
 		//SetViewMenu(new TMenuDescr(IDM_POEMENU));

 		param_dialog = new Param_Dialog(parent, 2);

 		poe_list_dialog = new List_Dialog(parent, null, 1);

 		AssociatedPOEs = new ArrayList<POE_data>();
 		ViewNumber = TOFPOEDoc.GetViewNumber();
 		SetPOEsInView();
 		GrabbedPoint = false;
 		LeftButtonDepressed = false;
 		RightButtonDepressed = false;
 		starting_energy = 0;
 		ending_energy = 0;
 		x_axis = 0;
 		y_axis = 0;
 		x_spacing = 0;
 		x_offset = 0;
 		starting_x = -1;
 		y_spacing = 0;
 		y_offset = 0;
 		starting_y = 0;
 		minimum = 0;
 		maximum = 0;
 		original_dot = null;
 		old_dot_position = null;
 		change_poe = null;
 		change_poe_index = 0;
 		change_poe_point_number = 0;

 		rel_pts_x = null;
 		rel_pts_y = null;

 		num_close_pts = null;
 		num_poes = 0;

 		KeyIsDepressed = false;
 		ArePOEsFrozen = false;
 		UnfrozenPOENumView = -1;
 		UnfrozenPOENumActual = -1;

 		AxisColor = Color.black;
 		this.addMouseListener(this);
 		this.addMouseMotionListener(this);
 	}
 	
 	public void Execute(){
 		parent.addFrame(this);
		this.setPreferredSize(new Dimension(400, 200));
		this.setBackground(Color.white);
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFocusable(true);
		this.setEnabled(true);
		this.setVisible(true);
 	}
 	
 	public void updateContent(){
 		Graphics g = this.getGraphics();
 		Draw_POE(g);
 	}
 	public static String StaticName()
 	{
 		return "P(E) Display View";
      }

      public boolean SetDocTitle(String docname, int index)
      {
      	this.setTitle(docname);
      	Title = docname;
         return true;
      }

      public String GetViewName()
      {
      	return StaticName();
      }


	protected boolean CanClose() {
		boolean return_value = true;
		int i, num_associated_poes;
		String message;

		num_associated_poes = AssociatedPOEs.size();
		if (num_associated_poes == 1) {
			message = "The energy distribution will remain in memory.";
		} else {
			message = "The energy distributions will remain in memory.";
		}

		for (i = 0; i < num_associated_poes; i++) {
			TOFPOEDoc.RemovePOEFromView(AssociatedPOEs.get(i), this); // Each P(E) can only be in view once!
			AssociatedPOEs.get(i).SetIsVisible(-2);// CHANGE:set not visible
		}
		TOFPOEDoc.ResetPOEsInPOEViews();
		return_value = true;
		parent.internalClosed(this);

		return return_value;
	}

      protected int[] FillListBox(boolean all_nonview_poes){
    	  POE_data poe;
    	  int[] index_of_poes;
    	  int count = 0;
    	  int total_number_of_poes = TOFPOEDoc.GetNumPOEs();
    	  int i, j;
    	  int number_of_associated_views;
    	  boolean this_poe_in_this_view;

    	  int poe_number = AssociatedPOEs.size();
    	  if((poe_number == total_number_of_poes) && (all_nonview_poes == true))
    		  return null;

    	  if(all_nonview_poes)
    	  {
    		  index_of_poes = new int[total_number_of_poes - poe_number];
    		  list_box_text = new String[total_number_of_poes - poe_number];
    	  }
    	  else
    	  {
    		  index_of_poes = new int[poe_number];
    		  list_box_text = new String[poe_number];
    	  }

    	  for(i = 0; i < total_number_of_poes; i++)
    	  {
    		  poe = TOFPOEDoc.GetPOEData(i);
    		  number_of_associated_views = poe.GetNumAssociatedViews();
    		  this_poe_in_this_view = false;
    		  if(all_nonview_poes == false)
    		  {
    			  for(j = 0; j < number_of_associated_views; j++)
    			  {
    				  if(this.equals(poe.GetAssociatedView(j)))
    				  {
    					  list_box_text[count] = poe.GetTitle();
    					  index_of_poes[count] = i;
    					  count++;
    				  }
    			  }
    		  }
    		  else
    		  {
    			  for(j = 0; j < number_of_associated_views; j++)
    			  {
    				  if(this.equals(poe.GetAssociatedView(j)))
    				  {
    					  this_poe_in_this_view = true;
    				  }
    			  }
    			  if(this_poe_in_this_view == false)
    			  {
    				  list_box_text[count] = poe.GetTitle();
    				  index_of_poes[count] = i;
    				  count++;
    			  }
    		  } // End of else (i.e. if only want tofs in this view
    	  } // End of iterating through all tofs
    	  poe_list_dialog.SetListBoxList(list_box_text);
    	  return index_of_poes;
      }

      protected void SetPOEsInView(){
    	  POE_data poe;
    	  int i, j;
    	  int number_of_associated_views;
    	  int total_number_poes = TOFPOEDoc.GetNumPOEs();
    	  String blank = "";
    	  this.setTitle("");
    	  boolean first = true;
    	  AssociatedPOEs.clear();

    	  int count = 0;

    	  Title = blank;

    	  UnfrozenPOENumView = -1;  // Sets to default of -1 until different value found
    	  for(i = 0; i < total_number_poes; i++)
    	  {
    		  poe = TOFPOEDoc.GetPOEData(i);
    		  number_of_associated_views = poe.GetNumAssociatedViews();

    		  for(j = 0; j < number_of_associated_views; j++)
    		  {
    			  if(this.equals(poe.GetAssociatedView(j)))
    			  {
    				  AssociatedPOEs.add(poe);
    				  if(ArePOEsFrozen)
    				  {
    					  if(poe.GetPOENum() == UnfrozenPOENumActual)
    					  {
    						  UnfrozenPOENumView = count;
    					  }
    				  }
    				  count++;
    				  if(Title.length() < 100)
    				  {
    					  if(first)
    					  {
    						  Title += "P(E):  ";
    						  Title += poe.GetTitle();
    						  first = false;
    					  }
    					  else
    					  {
    						  Title += "; ";
    						  Title += poe.GetTitle();
    					  }
    				  }
    			  }
    		  }
    	  }

    	  if(UnfrozenPOENumView == -1)  // i.e. if the unfrozen one has been deleted
    	  {
    		  ArePOEsFrozen = false;
    		  UnfrozenPOENumActual = -1;
    	  }

    	  if(Title.length() > 100)
    	  {
    		  Title = "...\0";
    	  }

    	  SetDocTitle(Title, 1);
      }

      protected void Draw_POE(Graphics g){
    	  Graphics2D g2 = (Graphics2D)g;
    	  // Will do this in the view so can control the size of the TOF
    	  int i;
    	  POE_data poe;

    	  String units_string, text_string;

    	  int x_pos, y_pos, k, num_poe_points;
    	  float minimum_energy, maximum_energy;
    	  float window_width, window_height;
    	  float energy_value;
    	  float lower_lim, upper_lim;
    	  float[] extrema;
    	  float font_width, font_height;
    	  float graph_top, graph_left;
    	  Point origin;
    	  float[] poe_pointer;
    	  float[] energy_pointer;

    	  int[] this_plot_points_x, this_plot_points_y;
    	  String lower_lim_recall, upper_lim_recall;

    	  int right_edge;
    	  String string;

    	  // Draw x and y axes
    	  g2.setColor(AxisColor);

    	  Rectangle window_rect = this.getContentPane().getBounds();
    	  window_rect.setLocation(this.getInsets().left, this.getInsets().top);
    	  window_width = (float) (window_rect.width);
    	  window_height = (float) (window_rect.height);

    	  font_width = (float) Math.max(2, 0.03*window_width);
    	  font_height = (float) Math.max(8, 0.06*window_height);

    	  if((font_height > 16))
    	  {
    		  font_width = Math.min(6, font_width);
    		  font_height = 16;
    	  }
    	  else
    	  {
    		  font_width = (float) Math.min(font_width, (font_height / 2.4));
    	  }

    	  graph_top = this.getInsets().top;
    	  graph_left = this.getInsets().left;//max(16, 0.038*window_width);

    	  x_axis = (float) (0.98 * window_width);
    	  x_offset = (float) (0.01 * x_axis);
    	  y_axis = window_height - font_height - 4 ;
    	  y_offset = (float) (0.02 * y_axis);
    	  starting_x = (int) graph_left;
    	  starting_y = (int) (graph_top + (int)y_axis);
    	  origin = new Point(starting_x, starting_y);

    	  // Draw x and y axes
    	  g2.drawLine(origin.x, origin.y, origin.x, (int) (origin.y-y_axis));
    	  g2.drawLine(origin.x, origin.y, (int) (origin.x+x_axis), origin.y);

    	  // Draw ticks on x axis
    	  g2.drawLine(origin.x, origin.y, origin.x, origin.y+2);
    	  g2.drawLine((int)(origin.x+(x_axis-x_offset)), origin.y, (int)(origin.x+(x_axis-x_offset)), origin.y+2);

    	  // Draw ticks on y axis
    	  g2.drawLine(origin.x, (int)(origin.y-y_offset), origin.x-2, (int) (origin.y-y_offset));
    	  g2.drawLine(origin.x, (int)(origin.y-y_axis-y_offset), origin.x-2, (int)(origin.y-y_axis+y_offset));

    	  Font font = g2.getFont().deriveFont(font_height);
    	  Font sidefont = g2.getFont().deriveFont((.9f*font_height));

    	  g2.setFont(font);
  		FontMetrics metrics = g2.getFontMetrics(font);
    	  units_string = ""; 
    	  text_string = "";

    	  units_string = TOFPOEDoc.GetEnergyUnits(units_string);
    	  text_string = "Translational Energy (";
    	  text_string += units_string;
    	  text_string += ")";
    	  g2.drawString(text_string, window_width/2+graph_left-metrics.stringWidth(text_string), window_height+graph_top);

    	  g2.setFont(sidefont);
    	  g2.drawString("P(E) (arb. units)", window_height/2+graph_top-font_height, -graph_left-font_height);

    	  poe = AssociatedPOEs.get(0);  // First POE in display!


    	  float convert_kcal_to_units = TOFPOEDoc.Convert_kcal_TO_session_units(1.0f);
    	  float convert_units_to_kcal = TOFPOEDoc.Convert_kcal_FROM_session_units(1.0f);

    	  // Determine which points to draw
    	  minimum_energy = Float.parseFloat(param_dialog.GetDefault1()) * convert_units_to_kcal;
    	  maximum_energy = Float.parseFloat(param_dialog.GetDefault2()) * convert_units_to_kcal;


    	  lower_lim = Float.parseFloat(param_dialog.GetValue1()) * convert_units_to_kcal;
    	  upper_lim = Float.parseFloat(param_dialog.GetValue2()) * convert_units_to_kcal;
    	  energy_pointer = poe.GetEnergyPointer();
    	  starting_energy = lower_lim;
    	  ending_energy = upper_lim;
    	  if ((lower_lim < (int) (minimum_energy - 1)) || (upper_lim > (int) (maximum_energy + 1))
    			  || (lower_lim >= upper_lim) || ((ending_energy - starting_energy) < (energy_pointer[1] - energy_pointer[0])))
    	  {
    		  ///////////////////////////////
    		  // Insert error message here //
    		  ///////////////////////////////



    		  starting_energy = energy_pointer[0];
    		  ending_energy = energy_pointer[poe.GetTotNumPoints() - 1];

    		  lower_lim_recall = "";  // To recall old lower limit
    		  upper_lim_recall = "";  // To recall old upper limit
    		  lower_lim_recall = "" + (starting_energy * convert_kcal_to_units);
    		  upper_lim_recall = "" + (ending_energy * convert_kcal_to_units);
    		  param_dialog.SetValue1(lower_lim_recall);
    		  param_dialog.SetValue2(upper_lim_recall);
    	  }

    	  // Only draw in POEs which should be shown in this view

    	  int num_poes = AssociatedPOEs.size();

    	  plot_points_x = new int[num_poes][];
    	  plot_points_y = new int[num_poes][];

    	  for(i = 0; i < num_poes; i++)
    	  {
    		  poe = AssociatedPOEs.get(0);
    		  extrema = poe.GetMaxMinAmplitude(starting_energy, ending_energy);
    		  if(i == 0)
    		  {
    			  maximum = extrema[0];
    			  minimum = extrema[1];
    		  }
    		  else
    		  {
    			  maximum = Math.max(maximum, extrema[0]);
    			  minimum = Math.min(minimum, extrema[1]);
    		  }
    	  }

    	  for(i = 0; i < num_poes; i++)
    	  {
    		  poe = AssociatedPOEs.get(0);
    		  POEColor = poe.GetPOEColor();


    		  g2.setColor(POEColor);
    		  // Draw POE points using small dots.
    		  poe_pointer = poe.GetPOEPointer();
    		  energy_pointer = poe.GetEnergyPointer();

    		  num_poe_points = poe.GetTotNumPoints();

    		  plot_points_x[i] = new int[num_poe_points];
    		  plot_points_y[i] = new int[num_poe_points];

    		  this_plot_points_x = plot_points_x[i];
    		  this_plot_points_y = plot_points_y[i];

    		  x_spacing = (x_axis - x_offset)/(ending_energy - starting_energy);
    		  if(maximum == minimum)
    			  y_spacing  = 0/*(y_axis - 2 * y_offset)/2*/;
    		  else
    			  y_spacing = (y_axis - 2 * y_offset)/maximum;

    		  for(k = 0; k < num_poe_points; k++)
    		  {
    			  energy_value = energy_pointer[k];
    			  if((energy_value >= starting_energy) && (energy_value <= ending_energy))
    			  {
    				  x_pos = origin.x + (int) ((energy_value - starting_energy) * x_spacing);
    				  y_pos = origin.y - (int)(poe_pointer[k] * y_spacing) - (int)y_offset;
    				  this_plot_points_x[k] = x_pos;
    				  this_plot_points_y[k] = y_pos;

    				  g2.drawOval(x_pos, y_pos, 4, 4);
    			  }
    			  else
    			  {
    				  this_plot_points_x[k] = -1;
    				  this_plot_points_x[k] = -1;
    			  }
    		  }

    	  }


    	  right_edge = (int) Math.min((x_axis + 2 * font_width), (window_width - 2 - origin.x));
    	  g2.setFont(font);
    	  
    	  string = "" + (starting_energy * convert_kcal_to_units);
    	  g2.drawString(string, origin.x, origin.y+25);
    	  string = "" + (ending_energy * convert_kcal_to_units);
    	  g2.drawString(string, right_edge-metrics.stringWidth(string), origin.y+25);

    	  g2.setFont(sidefont);
    	  g2.drawString("0", origin.y+metrics.stringWidth("0")/2-y_offset, origin.x-font_height);
    	  string = "" + maximum;
    	  g2.drawString(origin.y+ string,metrics.stringWidth(string)+y_offset-y_axis, origin.x-font_height);
    	  
    	  //System.out.println(Arrays.deepToString(plot_points_x));
    	  return;
    	  
      }

      // and so forth

     /* protected void EvSize(int i, TSize s){
    	  this.Invalidate();
    	  TWindow.EvSize(first, win_size);  // Call the TWindow version for this function
      }

      protected void EvRButtonUp(int modKeys, TPoint point){
    	  float[] amplitude_pointer;
    	  float amplitude;

    	  boolean is_end_point;
    	  int i;
    	  if(LeftButtonDepressed == false)
    	  {
    		  if(GrabbedPoint == true)
    		  {
    			  if(point.y > (starting_y - (0.8*y_offset)))
    			  {
    				  point.y = (starting_y - (0.8*y_offset));
    			  }
    			  if(point.y < (starting_y - y_axis))
    			  {
    				  point.y = (starting_y - y_axis);
    			  }
    			  this.ReleaseCapture();

    			  // Convert new point to an energy amplitude
    			  amplitude_pointer = change_poe.GetPOEPointer();

    			  if(y_spacing != 0.0)
    			  {
    				  amplitude = ((starting_y - point.y - (int)y_offset) / y_spacing);
    			  }
    			  if(amplitude < 0)
    			  {
    				  amplitude = 0;
    			  }

    			  if(KeyIsDepressed)
    				  amplitude *= 2;

    			  if(y_spacing == 0.0)
    			  {
    				  if(current_average_E == 0.0)
    				  {
    					  amplitude = -1.0;
    				  }
    				  else
    				  {
    					  amplitude = 1.0;
    				  }
    				  amplitude_pointer[change_poe_point_number] = 1.0;
    			  }
    			  else
    			  {
    				  amplitude_pointer[change_poe_point_number] = amplitude;
    			  }

    			  if((change_poe_point_number == 0) || (change_poe_point_number == (change_poe.GetTotNumPoints() - 1)))
    			  {
    				  is_end_point = true;
    			  }
    			  else
    			  {
    				  is_end_point = false;
    			  }
    			  change_poe.NormalizePOE();
    			  TOFPOEDoc.FindNewTOFs(amplitude, is_end_point);

    			  GrabbedPoint = false;
    			  RightButtonDepressed = false;

    			  TOFPOEDoc.RepaintAllPOEViews();
    		  }
    	  }
      }

      protected void EvLButtonDown(int modKeys, TPoint point){
    	  float x_energy, y_value;
    	  String temp_text;
    	  POE_data poe;
    	  int i, k, num_poe_points;
    	  int x_difference, y_difference;
    	  int sqr_dist, smallest_sqr_distance;
    	  boolean first_good_point = true;
    	  int good_poe, good_point, poe_index;

    	  int[] this_poe_plot_points_x, this_poe_plot_points_y;
    	  int this_poe_point_x, this_poe_point_y;

    	  int moving_dot_red, moving_dot_green, moving_dot_blue;
    	  int new_red, new_green, new_blue;

    	  // Only do this if right button is not already depressed
    	  if(RightButtonDepressed == false)
    	  {
    		  if(GrabbedPoint == false)  // i.e. if no point is already being moved
    		  {
    			  if((plot_points_x == 0) || (plot_points_y == 0))
    			  {
    				  Draw_POE();
    			  }

    			  num_poes = AssociatedPOEs.GetItemsInContainer();


    			  // Find P(E) whose y value is closest to and within 4 y spaces to the point clicked on
    			  for(i = 0; i < num_poes; i++)
    			  {
    				  poe = TOFPOEDoc.GetPOEData((int) (AssociatedPOEs)[i]);
    				  num_poe_points = poe.GetTotNumPoints();
    				  this_poe_plot_points_x = plot_points_x[i];
    				  this_poe_plot_points_y = plot_points_y[i];
    				  for(k = 0; k < num_poe_points; k++)
    				  {
    					  this_poe_point_x = this_poe_plot_points_x[k];
    					  this_poe_point_y = this_poe_plot_points_y[k];

    					  if((this_poe_point_x >= 0) && (this_poe_point_y >= 0))
    					  {
    						  x_difference = this_poe_point_x - point.x;
    						  y_difference = this_poe_point_y - point.y;
    						  sqr_dist = x_difference * x_difference + y_difference * y_difference;

    						  // This section finds the nearest point which is within 4 spaces of the mouse
    						  // and records which P(E) it comes from and to which point in the P(E) it
    						  // corresponds
    						  if(sqr_dist <= 16)
    						  {
    							  if((!ArePOEsFrozen) || (i == UnfrozenPOENumView))
    							  {
    								  if(first_good_point == true)
    								  {
    									  smallest_sqr_distance = sqr_dist;
    									  first_good_point = false;
    									  good_poe = i;
    									  good_point = k;
    								  }
    								  else
    								  {
    									  if(sqr_dist <= smallest_sqr_distance)
    									  {
    										  smallest_sqr_distance = sqr_dist;
    										  good_poe = i;
    										  good_point = k;
    									  }
    								  }
    							  }
    						  }
    					  }
    				  }

    			  }
    			  if(first_good_point == true)
    				  return;   // No points are within 4 spaces of the mouse

    			  poe_index = (int) (AssociatedPOEs)[good_poe];
    			  change_poe = TOFPOEDoc.GetPOEData(poe_index);
    			  change_poe_point_number  = good_point;

    			  // Show the energy value of this point in the status bar of the frame window
    			  x_gadget_text = "x pos.:  ";

    			  // Convert the x position to an energy in correct units
    			  x_energy = change_poe.GetEnergyPointer()[change_poe_point_number];//((original_dot.x - starting_x) / x_spacing) + starting_energy;
    			  x_energy = TOFPOEDoc.Convert_kcal_TO_session_units(x_energy);
    			  temp_text = "" + x_energy;


    			  x_gadget_text += temp_text;
    			  x_gadget_text += " ";
    			  TOFPOEDoc.GetEnergyUnits(temp_text);
    			  x_gadget_text += temp_text;

    			  x_pos_gadget.SetText(x_gadget_text);

    			  y_gadget_text = "y pos.:  ";

    			  // Convert the y position to a value
    			  y_value = change_poe.GetPOEPointer()[change_poe_point_number];
    			  gcvt(y_value, 4, temp_text);


    			  y_gadget_text += temp_text;
    			  y_pos_gadget.SetText(y_gadget_text);

    			  // Make a pair of new arrays of 3 points which store data to be used in calculating
    			  // a TOF
    			  original_dot.x = plot_points_x[good_poe][change_poe_point_number];
    			  original_dot.y = plot_points_y[good_poe][change_poe_point_number];

    			  // Determine if this P(E) is being used in a calculation right now
    			  // and calculate the TOF for this point as a delta function
    			  TOFPOEDoc.CalcTOFDeltaFunctions(poe_index, change_poe_point_number);


    			  // Set the moving dot to the color of the P(E)
    			  TColor moving_dot_color = (change_poe.GetPOEColor());
    			  this.SetCapture();
    			  moving_dot_red = moving_dot_color.Red();
    			  moving_dot_green = moving_dot_color.Green();
    			  moving_dot_blue = moving_dot_color.Blue();

    			  // Choose color of stationary dot by relating to the actual P(E) color
    			  new_red = ((moving_dot_red + 76) % 220);       // Use %220 so can't get a white dot!
    			  new_green = ((moving_dot_green -112) % 220);
    			  new_blue = ((moving_dot_blue * 7) % 220);

    			  TColor original_dot_color(new_red, new_green, new_blue);

    			  OldPOEPen = new TPen(original_dot_color, 4);

    			  OldPOE = new TClientDC(this);
    			  OldPOE.SelectObject(*OldPOEPen);
    			  OldPOE.MoveTo(original_dot);
    			  OldPOE.LineTo(original_dot);
    			  GrabbedPoint = true;
    			  LeftButtonDepressed = true;
    		  }
    	  }
      }

      protected void EvLButtonUp(int modKeys, TPoint point){
    	  int i;

    	  if(RightButtonDepressed == false)
    	  {
    		  if(GrabbedPoint == true)
    		  {
    			  GrabbedPoint = false;
    			  LeftButtonDepressed = false;
    			  this.ReleaseCapture();

    			  plot_points_x = 0;
    			  plot_points_y = 0;

    			  OldPOEPen = 0;
    			  OldPOE = 0;

    			  change_poe = 0;

    			  TOFPOEDoc.RepaintAllPOEViews();
    			  TOFPOEDoc.RepaintAllTOFViews();
    		  }
    	  }
      }

      protected void EvMouseMove(int modKeys, TPoint point){
    	  int i, k;
    	  float amplitude;
    	  int[] this_rel_points_x, this_rel_points_y;

    	  String temp_text;
    	  float x_energy, y_value;
    	  POE_data poe;
    	  TColor poe_color;
    	  TPen poe_pen;
    	  TDC draw_dot;


    	  if((GrabbedPoint == true) && (RightButtonDepressed == true))
    	  {
    		  if(point.y > (starting_y - (0.8*y_offset)))
    		  {
    			  point.y = (starting_y - (0.8*y_offset));
    		  }
    		  if(point.y < (starting_y - y_axis))
    		  {
    			  point.y = (starting_y - y_axis);
    		  }

    		  if((old_dot_position.y != point.y) || (dummy == 0))
    		  {
    			  DeletePOE.MoveTo(old_dot_position);
    			  DeletePOE.LineTo(old_dot_position);

    			  if(old_dot_position.x < starting_x + 3)
    			  {
    				  // Dot is touching axis; redraw axis as dot moves
    				  poe_pen = new TPen(AxisColor, 2);
    				  draw_dot = new TClientDC(this);
    				  draw_dot.SelectObject(poe_pen);
    				  draw_dot.MoveTo(starting_x, starting_y);
    				  draw_dot.LineTo(starting_x, starting_y - (int) y_axis);

    				  delete poe_pen;
    				  delete draw_dot;
    			  }
    			  if(old_dot_position.y > starting_y - 3)
    			  {

    				  poe_pen = new TPen(AxisColor, 2);
    				  draw_dot = new TClientDC(this);
    				  draw_dot.SelectObject(poe_pen);
    				  draw_dot.MoveTo(starting_x, starting_y);
    				  draw_dot.LineTo(starting_x + (int) x_axis, starting_y);

    				  delete poe_pen;
    				  delete draw_dot;
    			  }


    			  MovePOE.MoveTo(original_dot.x, point.y);
    			  MovePOE.LineTo(original_dot.x, point.y);

    			  // Redraw all points which were erased when point was moved
    			  for(i = 0; i < num_poes; i++)
    			  {
    				  int number_pts = num_close_pts[i];
    				  if(number_pts > 0)
    				  {
    					  poe = TOFPOEDoc.GetPOEData((int) (AssociatedPOEs)[i]);
    					  poe_color = poe.GetPOEColor();
    					  poe_pen = new TPen(poe_color, 4);
    					  draw_dot = new TClientDC(this);
    					  draw_dot.SelectObject(poe_pen);
    					  this_rel_points_x = rel_pts_x[i];
    					  this_rel_points_y = rel_pts_y[i];
    					  for(k = 0; k < number_pts; k++)
    					  {
    						  int x_val = this_rel_points_x[k];
    						  int y_val = this_rel_points_y[k];
    						  if((y_val != original_dot.y) || (x_val != original_dot.x))
    						  {
    							  draw_dot.MoveTo(x_val, y_val);
    							  draw_dot.LineTo(x_val, y_val);
    						  }
    					  }
    					  delete poe_pen;
    					  delete draw_dot;
    				  }
    			  }

    			  OldPOE.MoveTo(original_dot);
    			  OldPOE.LineTo(original_dot);

    			  old_dot_position.x = original_dot.x;
    			  old_dot_position.y = point.y;

    			  if(y_spacing != 0.0)
    				  amplitude = ((starting_y - point.y - (int)y_offset) / y_spacing)/* + minimum;
    			  if(amplitude < 0)
    			  {
    				  amplitude = 0;
    			  }

    			  if(KeyIsDepressed)
    				  amplitude *= 2;
    			  if(y_spacing == 0.0)
    				  amplitude = -1.0;

    			  TOFPOEDoc.ChangeTOFs((amplitude));
    		  }
    	  }


    	  if(GrabbedPoint == false)
    	  {
    		  if((starting_x >= 0) && (point.x >= starting_x) && (point.x <= (starting_x + x_axis - x_offset)))
    		  {
    			  x_gadget_text = "x pos.:  ";

    			  // Convert the x position to an energy in correct units
    			  if(x_spacing)
    			  {
    				  x_energy = ((point.x - starting_x) / x_spacing) + starting_energy;
    			  }
    			  else
    			  {
    				  x_energy = starting_energy;
    			  }
    			  x_energy = TOFPOEDoc.Convert_kcal_TO_session_units(x_energy);
    			  temp_text = "" + x_energy;


    			  x_gadget_text += temp_text;
    			  x_gadget_text += " ";
    			  TOFPOEDoc.GetEnergyUnits(temp_text);
    			  x_gadget_text += temp_text;
    		  }
    		  else
    		  {
    			  x_gadget_text= "x pos.:  ";
    		  }

    		  x_pos_gadget.SetText(x_gadget_text);
    	  }

    	  if((GrabbedPoint == false) || (RightButtonDepressed == true))
    	  {
    		  if(point.y <= starting_y)//&& (point.y >= (starting_y - y_axis + y_offset)))
    		  {
    			  y_gadget_text= "y pos.:  ";
    			  if(y_spacing)
    			  {
    				  y_value = ((starting_y - y_offset - point.y) / y_spacing) /*+ minimum;
    			  }
    			  else
    			  {
    				  y_value = 0;
    			  }
    			  if(y_value < 0)
    			  {
    				  y_value = 0;
    			  }
    			  temp_text = "" + y_value;


    			  y_gadget_text += temp_text;
    		  }
    		  else
    		  {
    			  y_gadget_text = "y pos.:  ";
    		  }
    		  y_pos_gadget.SetText(y_gadget_text);
    	  }
      }

      protected void EvKeyDown(int key, int repeatCount, int flags){
    	  if(KeyIsDepressed == false)
    	  {
    		  if(((char)key == 'd') || ((char)key == 'D'))
    		  {
    			  KeyIsDepressed = true;
    			  if(RightButtonDepressed)
    			  {
    				  this.EvMouseMove(0, old_dot_position);
    			  }
    		  }
    	  }
      }

      protected void EvKeyUp(int key, int repeatCount, int flags){
    	  if(((char)key == 'd') || ((char)key == 'D'))
    	  {
    		  KeyIsDepressed = false;
    		  if(RightButtonDepressed)
    		  {
    			  this.EvMouseMove(0, old_dot_position);
    		  }
    	  }
      }
*/
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Draw_POE(g);
	}

	protected void AxisRange() {
		POE_data poe;
		int number_of_poes = AssociatedPOEs.size();
		int i;
		float convert_kcal_to_units = TOFPOEDoc
				.Convert_kcal_TO_session_units(1.0f);
		// float convert_units_to_kcal =
		// TOFPOEDoc.Convert_kcal_FROM_session_units(1.0);
		float[] energy_pointer;
		float min_energy = 0, max_energy = 0;
		String lower_energy_limit;
		String upper_energy_limit;
		String least_possible_energy;
		String greatest_possible_energy;

		if (param_dialog.GetStatus() == false) {
			for (i = 0; i < number_of_poes; i++) {
				poe = AssociatedPOEs.get(i);
				energy_pointer = poe.GetEnergyPointer();
				if (i == 0) {
					min_energy = energy_pointer[0];
					max_energy = energy_pointer[(poe.GetTotNumPoints()) - 1];
				} else {
					min_energy = Math.min(min_energy, energy_pointer[0]);
					max_energy = Math.max(max_energy,
							energy_pointer[(poe.GetTotNumPoints()) - 1]);
				}
			}
			param_dialog = new Param_Dialog(parent, 2);
			least_possible_energy = "" + (min_energy * convert_kcal_to_units);
			greatest_possible_energy = ""
					+ (max_energy * convert_kcal_to_units);
			lower_energy_limit = "" + (starting_energy * convert_kcal_to_units);
			upper_energy_limit = "" + (ending_energy * convert_kcal_to_units);

			param_dialog.SetDefault1(least_possible_energy);
			param_dialog.SetDefault2(greatest_possible_energy);
			param_dialog.SetValue1(lower_energy_limit);
			param_dialog.SetValue2(upper_energy_limit);
			param_dialog
					.ConvertEnergyValues(TOFPOEDoc.energy_unit, 1.0f);
			param_dialog.Execute();
		}
	}

	protected void SetColors() {
		int view_poe_number = AssociatedPOEs.size();
		int[] index_array;
		int index;
		Color POEColor = Color.black;
		POE_data poe;

		index_array = FillListBox(false); // False . show only TOFs for this
											// view in list box

		if (view_poe_number > 1) {
			poe_list_dialog.SetCaption("Choose an energy distribution:");
			poe_list_dialog.Execute();
			if (poe_list_dialog.ID != true) {
				return;
			}
			index = index_array[poe_list_dialog.GetChosenIndex()[0]];
			poe = TOFPOEDoc.GetPOEData(index);
		} else {
			index = index_array[0];
			poe = TOFPOEDoc.GetPOEData(index);
		}

		Color c = JColorChooser.showDialog(this, "Choose Color", Color.black);
		if (c != null) {
			poe.SetPOEColor(c);
		}

		TOFPOEDoc.RepaintAllPOEViews();
		TOFPOEDoc.ResetAllTOFColors(index);
	}

      protected void FreezePEs() {
    	  int[] index_array;
    	  index_array = FillListBox(false); // False . show only P(E)s for this
    	  // view in list box

    	  poe_list_dialog.SetCaption("Freeze all P(E)'s EXCEPT:");

    	  poe_list_dialog.Execute();
    	  if(poe_list_dialog.ID)
    	  {
    		  UnfrozenPOENumView = poe_list_dialog.GetChosenIndex()[0];
    		  UnfrozenPOENumActual = TOFPOEDoc.GetPOEData(index_array[UnfrozenPOENumView]).GetPOENum();

    		  ArePOEsFrozen = true;
    	  }
    	  else
    	  {
    		  ArePOEsFrozen = false;
    		  UnfrozenPOENumView = -1;
    		  UnfrozenPOENumActual = -1;
    	  }
      }

      protected void UnfreezePEs(){
    	  ArePOEsFrozen = false;
    	  UnfrozenPOENumView = -1;
    	  UnfrozenPOENumActual = -1;
      }

      protected void AppendStoredPE(){
    	  POE_data poe;
    	  int[] index_array;
    	  index_array = FillListBox(true); // True . show all non-displayed in list box
    	  if(index_array != null)  // i.e. if some loaded TOFs not in this view already
    	  {
    		  poe_list_dialog.SetCaption("Choose a P(E):");

    		  poe_list_dialog.Execute();
    		  if(poe_list_dialog.ID)
    		  {
    			  poe = TOFPOEDoc.GetPOEData(index_array[poe_list_dialog.GetChosenIndex()[0]]);
    			  poe.AddAssociatedView(this);
    			  SetPOEsInView();
    		  }
    	  } 
      }
      
      protected void RemovePEFromDisplay(){
    	  int[] index_array;
    	  index_array = FillListBox(false); // False . show only P(E)s for this view in list box

    	  poe_list_dialog.SetCaption("Choose a P(E) to remove:");

    	  poe_list_dialog.Execute();
    	  if(poe_list_dialog.ID)
    	  {
    		  TOFPOEDoc.RemovePOEFromView(TOFPOEDoc.GetPOEData(index_array[poe_list_dialog.GetChosenIndex()[0]]), this);
    		  TOFPOEDoc.ResetPOEsInPOEViews();
    	  }
      }
/*
      // View notification functions
      protected boolean VnChangePOEs(int index){
    	  SetPOEsInView();
    	  this.Invalidate();
    	  return true;
      }

      protected boolean VnRepaintPOEView(int index){
    	  this.Invalidate();
    	  return true;
      }
      protected boolean VnConversionFactor(int index){
    	  int unit_num;
    	  float mult_factor;

    	  unit_num = TOFPOEDoc.GetEnergyUnits();
    	  mult_factor = TOFPOEDoc.GetConvertsionFactor();
    	  param_dialog.ConvertEnergyValues(unit_num, mult_factor);
    	  return true;
      }*/

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

		if(e.getButton() == MouseEvent.BUTTON3){
			float x_energy, y_value;
			Point point = e.getPoint();
	    	  String temp_text;
	    	  POE_data poe;
	    	  int i, k, num_poe_points;

	    	  int x_difference, y_difference;
	    	  int sqr_dist, smallest_sqr_distance = 0;
	    	  boolean first_good_point = true;
	    	  int good_poe = 0, good_point = 0, poe_index, counter;

	    	  int[] this_plot_points_x, this_plot_points_y;
	    	  int[] this_rel_points_x, this_rel_points_y;
	    	  int this_point_x, this_point_y;

	    	  int moving_dot_red, moving_dot_green, moving_dot_blue;
	    	  int new_red, new_green, new_blue;

	    	  this.requestFocus();
	    	  if(LeftButtonDepressed == false)
	    	  {
	    		  if(GrabbedPoint == false)  // i.e. if no point is already being moved
	    		  {
	    			  if((plot_points_x == null) || (plot_points_y == null))
	    			  {
	    				 this.updateContent();
	    			  }

	    			  num_poes = AssociatedPOEs.size();
	    			  num_close_pts = new int[num_poes];
	    			  rel_pts_x = new int[num_poes][];
	    			  rel_pts_y = new int[num_poes][];


	    			  // Find P(E) whose y value is closest to and within 4 y spaces to the point clicked on
	    			  for(i = 0; i < num_poes; i++)
	    			  {
	    				  counter = 0;
	    				  poe = AssociatedPOEs.get(i);
	    				  num_poe_points = poe.GetTotNumPoints();
	    				  this_plot_points_x = plot_points_x[i];
	    				  this_plot_points_y = plot_points_y[i];
	    				  for(k = 0; k < num_poe_points; k++)
	    				  {
	    					  this_point_x = this_plot_points_x[k];
	    					  this_point_y = this_plot_points_y[k];
	    					  if((this_point_x >= 0) && (this_point_y >= 0))
	    					  {
	    						  x_difference = this_point_x - point.x;
	    						  y_difference = this_point_y - point.y;

	    						  // Count how many points may be in the range of this point upon moving it
	    						  // (so they can be redrawn)
	    						  if(Math.abs(x_difference) <= 7)
	    						  {
	    							  counter++;
	    						  }

	    						  sqr_dist = x_difference * x_difference + y_difference * y_difference;

	    						  // This section finds the nearest point which is within 4 spaces of the mouse
	    						  // and records which P(E) it comes from and to which point in the P(E) it
	    						  // corresponds
	    						  if(sqr_dist <= 150)
	    						  {
	    							  if((!ArePOEsFrozen) || (i == UnfrozenPOENumView))
	    							  {
	    								  if(first_good_point == true)
	    								  {
	    									  smallest_sqr_distance = sqr_dist;
	    									  first_good_point = false;
	    									  good_poe = i;
	    									  good_point = k;
	    								  }
	    								  else
	    								  {
	    									  if(sqr_dist <= smallest_sqr_distance)
	    									  {
	    										  smallest_sqr_distance = sqr_dist;
	    										  good_poe = i;
	    										  good_point = k;
	    									  }
	    								  }
	    							  }
	    						  }
	    					  }
	    				  }
	    				  num_close_pts[i] = counter;  // Stores the number of "close" points from this P(E)
	    				  // so they can be redrawn when the point movement
	    				  // affects them.
	    			  }
	    			  if(first_good_point == true){
	    				 
	    				  return;   // No points are within 4 spaces of the mouse
	    			  }

	    			  // Find coordinates for all "close" points so they can be redrawn later
	    			  for(i = 0; i < num_poes; i++)
	    			  {
	    				  rel_pts_x[i] = new int[num_close_pts[i]];
	    				  rel_pts_y[i] = new int[num_close_pts[i]];
	    				  poe = AssociatedPOEs.get(i);
	    				  num_poe_points = poe.GetTotNumPoints();
	    				  counter = 0;

	    				  this_plot_points_x = plot_points_x[i];
	    				  this_plot_points_y = plot_points_y[i];
	    				  this_rel_points_x = rel_pts_x[i];
	    				  this_rel_points_y = rel_pts_y[i];

	    				  for(k = 0; k < num_poe_points; k++)
	    				  {
	    					  this_point_x = this_plot_points_x[k];
	    					  if(this_point_x >= 0)
	    					  {
	    						  x_difference = this_point_x - point.x;

	    						  if(Math.abs(x_difference) <= 7)
	    						  {
	    							  this_rel_points_x[counter] = this_point_x;
	    							  this_rel_points_y[counter] = this_plot_points_y[k];
	    							  counter++;
	    						  }
	    					  }
	    				  }
	    			  }
	    			  poe_index = 0;
	    			  change_poe = AssociatedPOEs.get(good_poe);


	    			  current_average_E = change_poe.NormalizePOE(null);  // Returns average value of E
	    			  change_poe_point_number  = good_point;



	    			  // Make a pair of new arrays of 3 points which store data to be used in calculating
	    			  // a TOF

	    			 original_dot = new Point(0,0);
	    			  original_dot.x = plot_points_x[good_poe][change_poe_point_number];
	    			  original_dot.y = plot_points_y[good_poe][change_poe_point_number];
	    			  changeIndices = new Point(good_poe, change_poe_point_number);

	    			  // Show the energy value of this point in the status bar of the frame window
	    			  x_gadget_text = "";

	    			  // Convert the x position to an energy
	    			  x_energy = change_poe.GetEnergyPointer()[change_poe_point_number];//((original_dot.x - starting_x) / x_spacing) + starting_energy;
	    			  x_energy = TOFPOEDoc.Convert_kcal_TO_session_units(x_energy);
	    			  temp_text = "" + x_energy;


	    			  x_gadget_text += temp_text;
	    			  x_gadget_text += " ";
	    			  temp_text = TOFPOEDoc.GetEnergyUnits(temp_text);
	    			  x_gadget_text += temp_text;

	    			  x_pos_gadget.setText(x_gadget_text);


	    			  y_gadget_text = "";

	    			  // Convert the y position to a value
	    			  y_value = change_poe.GetPOEPointer()[change_poe_point_number];
	    			  temp_text = "" + y_value;


	    			  y_gadget_text += temp_text;
	    			  y_pos_gadget.setText(y_gadget_text);


	    			  // Determine if this P(E) is being used in a calculation right now
	    			  // and calculate the TOF for this point as a delta function
	    			  TOFPOEDoc.CalcTOFDeltaFunctions(poe_index, change_poe_point_number);

	    			  old_dot_position = original_dot;
	    			  // Set the moving dot to the color of the P(E)
	    			  Color moving_dot_color = change_poe.GetPOEColor();
	    			  //this.SetCapture();
	    			  moving_dot_red = moving_dot_color.getRed();
	    			  moving_dot_green = moving_dot_color.getGreen();
	    			  moving_dot_blue = moving_dot_color.getBlue();

	    			  // Choose color of stationary dot by relating to the actual P(E) color
	    			  new_red = Math.abs((moving_dot_red + 76) % 220);       // Use %220 so can't get a white dot!
	    			  new_green = Math.abs((moving_dot_green -112) % 220);
	    			  new_blue = Math.abs((moving_dot_blue * 7) % 220);

	    			  Color original_dot_color = new Color(new_red, new_green, new_blue);
	    			  // White dot used to cover up old dot
	    			  Color white_dot = new Color(255, 255, 255);

	    			 /* MovePOEPen = new TPen(moving_dot_color, 4);

	    			  OldPOEPen = new TPen(original_dot_color, 4);

	    			  OldPOE = new TClientDC(*this);

	    			  DeletePOEPen = new TPen(white_dot, 4);

	    			  DeletePOE = new TClientDC(this);
	    			  MovePOE.SelectObject(MovePOEPen);
	    			  OldPOE.SelectObject(OldPOEPen);
	    			  DeletePOE.SelectObject(DeletePOEPen);
	    			  OldPOE.MoveTo(original_dot);
	    			  OldPOE.LineTo(original_dot);*/
	    			  this.parent.validate();
	    			  this.parent.repaint();
	    			  GrabbedPoint = true;
	    			  RightButtonDepressed = true;
	    		  }
	    	  }
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3){
			 float[] amplitude_pointer;
	    	  float amplitude = 0;
	    	  Point point = e.getPoint();
	    	  boolean is_end_point;
	    	  int i;
	    	  if(LeftButtonDepressed == false)
	    	  {
	    		  if(GrabbedPoint == true)
	    		  {
	    			  if(point.y > (starting_y - (0.8*y_offset)))
	    			  {
	    				  point.y = (int) (starting_y - (0.8*y_offset));
	    			  }
	    			  if(point.y < (starting_y - y_axis))
	    			  {
	    				  point.y = (int) (starting_y - y_axis);
	    			  }
	    			  //this.ReleaseCapture();

	    			  // Convert new point to an energy amplitude
	    			  amplitude_pointer = change_poe.GetPOEPointer();

	    			  if(y_spacing != 0.0)
	    			  {
	    				  amplitude = ((starting_y - point.y - (int)y_offset) / y_spacing);
	    			  }
	    			  if(amplitude < 0)
	    			  {
	    				  amplitude = 0;
	    			  }

	    			  if(KeyIsDepressed)
	    				  amplitude *= 2;

	    			  if(y_spacing == 0.0)
	    			  {
	    				  if(current_average_E == 0.0)
	    				  {
	    					  amplitude = -1.0f;
	    				  }
	    				  else
	    				  {
	    					  amplitude = 1.0f;
	    				  }
	    				  amplitude_pointer[change_poe_point_number] = 1.0f;
	    			  }
	    			  else
	    			  {
	    				  amplitude_pointer[change_poe_point_number] = amplitude;
	    			  }

	    			  if((change_poe_point_number == 0) || (change_poe_point_number == (change_poe.GetTotNumPoints() - 1)))
	    			  {
	    				  is_end_point = true;
	    			  }
	    			  else
	    			  {
	    				  is_end_point = false;
	    			  }
	    			  change_poe.NormalizePOE(null);
	    			  TOFPOEDoc.FindNewTOFs(amplitude, is_end_point);

	    			  GrabbedPoint = false;
	    			  RightButtonDepressed = false;

	    			  //TOFPOEDoc.RepaintAllPOEViews();
	    			  //TOFPOEDoc.RepaintAllTOFViews();
	    		  }
	    	  }
		}
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
		// TODO Auto-generated method stub
		int i, k;
		float amplitude = 0;
		int[] this_rel_points_x, this_rel_points_y;

		String temp_text;
		float x_energy, y_value;
		POE_data poe;
		Point point = e.getPoint();

		if ((GrabbedPoint == true) && e.getButton() == MouseEvent.BUTTON3) {
			if (point.y > (starting_y - (0.8 * y_offset))) {
				point.y = (int) (starting_y - (0.8 * y_offset));
			}
			if (point.y < (starting_y - y_axis)) {
				point.y = (int) (starting_y - y_axis);
			}

			if ((old_dot_position.y != point.y)) {

				if (old_dot_position.x < starting_x + 3) {
					// Dot is touching axis; redraw axis as dot moves

				}
				if (old_dot_position.y > starting_y - 3) {

				}

				// float[] temp = change_poe.GetPOEPointer();
				// temp[change_poe_point_number] = (point.y+y_offset) /
				// y_spacing;
				// change_poe.SetPOEPointer(temp);

				// Redraw all points which were erased when point was moved
				if (point.y > (starting_y - (0.8 * y_offset))) {
					point.y = (int) (starting_y - (0.8 * y_offset));
				}
				if (point.y < (starting_y - y_axis)) {
					point.y = (int) (starting_y - y_axis);
				}
				// this.ReleaseCapture();

				// Convert new point to an energy amplitude
				float[] amplitude_pointer = change_poe.GetPOEPointer();

				if (y_spacing != 0.0) {
					amplitude = ((starting_y - point.y - (int) y_offset) / y_spacing);
				}
				if (amplitude < 0) {
					amplitude = 0;
				}

				if (y_spacing == 0.0) {
					if (current_average_E == 0.0) {
						amplitude = -1.0f;
					} else {
						amplitude = 1.0f;
					}
					amplitude_pointer[change_poe_point_number] = 1.0f;
				} else {
					amplitude_pointer[change_poe_point_number] = amplitude;
				}

				TOFPOEDoc.RepaintAllPOEViews();

				old_dot_position.x = original_dot.x;
				old_dot_position.y = point.y;

				if (y_spacing != 0.0)
					amplitude = ((starting_y - point.y - (int) y_offset) / y_spacing)/*
																					 * +
																					 * minimum
																					 */;
				if (amplitude < 0) {
					amplitude = 0;
				}

				if (KeyIsDepressed)
					amplitude *= 2;
				if (y_spacing == 0.0)
					amplitude = -1.0f;

				TOFPOEDoc.ChangeTOFs((amplitude));
				TOFPOEDoc.RepaintAllTOFViews();
				this.parent.validate();
				this.parent.repaint();
				for(Calc_data c : this.AssociatedPOEs.get(0).AssociatedCalcs){
					
				}
			}
			this.updateContent();
		}

		if (GrabbedPoint == false) {

		}

		if ((GrabbedPoint == false) || (RightButtonDepressed == true)) {

		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point point = e.getPoint();
		float x_energy = 0, y_value = 0;
		String temp_text = "";
		if((starting_x >= 0) && (point.x >= starting_x) && (point.x <= (starting_x + x_axis - x_offset)))
		  {
			  x_gadget_text = "";

			  // Convert the x position to an energy in correct units
			  if(x_spacing != 0)
			  {
				  x_energy = ((point.x - starting_x) / x_spacing) + starting_energy;
			  }
			  else
			  {
				  x_energy = starting_energy;
			  }
			  x_energy = TOFPOEDoc.Convert_kcal_TO_session_units(x_energy);
			  temp_text = "" + x_energy;


			  x_gadget_text += temp_text;
			  x_gadget_text += " ";
			  temp_text = "" + TOFPOEDoc.GetEnergyUnits(temp_text);
			  x_gadget_text += temp_text;
		  }
		  else
		  {
			  x_gadget_text= "";
		  }

		  x_pos_gadget.setText(x_gadget_text);
		  
		  if(point.y <= starting_y)//&& (point.y >= (starting_y - y_axis + y_offset)))
  		  {
  			  y_gadget_text= "";
  			  if(y_spacing != 0)
  			  {
  				  y_value = ((starting_y - y_offset - point.y) / y_spacing) /*+ minimum*/;
  			  }
  			  else
  			  {
  				  y_value = 0;
  			  }
  			  if(y_value < 0)
  			  {
  				  y_value = 0;
  			  }
  			  temp_text = "" + y_value;


  			  y_gadget_text += temp_text;
  		  }
  		  else
  		  {
  			  y_gadget_text = "";
  		  }
  		  y_pos_gadget.setText(y_gadget_text);
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
		this.CanClose();
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
		
}
