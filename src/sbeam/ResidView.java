package sbeam;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

public class ResidView extends JInternalFrame{


	protected float starting_time, ending_time;
	protected float x_axis, y_axis;
	protected float x_spacing, y_spacing;
	protected float x_offset, y_offset;
	protected int starting_x, starting_y;
	protected float maximum, minimum;

	protected JLabel x_pos_gadget;
	protected String x_gadget_text;
	protected JLabel y_pos_gadget;
	protected String y_gadget_text;
	protected JLabel sum_square_gadget;
	protected String sum_square_gadget_text;

	protected Point mouse_position;

	protected Param_Dialog param_dialog;

	protected Color ResidColor;

	protected int AssociatedResid;

	protected int ViewNumber;

	protected TOFPOEDocument TOFPOEDoc;
	protected Resid_data residual;
	protected MainFrame parent;

	public ResidView(TOFPOEDocument doc, MainFrame parent) {
		// TODO Auto-generated constructor stub
		TOFPOEDoc = doc;
		this.parent = parent;
		x_pos_gadget = TOFPOEDoc.GetXPosGadget();
		y_pos_gadget = TOFPOEDoc.GetYPosGadget();
		sum_square_gadget = TOFPOEDoc.GetSumSquareGadget();

		x_gadget_text = "";
		y_gadget_text = "";
		sum_square_gadget_text = "";

		param_dialog = new Param_Dialog(parent, 4);  //(Is auto deleted when
		//window is closed.)
		param_dialog.SetType(4);
		ViewNumber = TOFPOEDoc.GetViewNumber();
		

		starting_time = 0;
		ending_time = -1;
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
	
	public static String StaticName()
	{
		return "Residual Display View";
	}

	public boolean SetDocTitle(String docname , int index)
	{
		this.setTitle(docname);
		return true;
	}

	public String GetViewName()
	{
		return StaticName();
	}

	protected void SetResidInView(){
		Resid_data resid;
		int i;
		int total_number_resids = TOFPOEDoc.GetNumResids();
		String blank = "";
		String Title = "";

		Title = blank;

		for(i = 0; i < total_number_resids; i++)
		{
			resid = TOFPOEDoc.GetResidData(i);

			if(resid.GetAssociatedResidView().equals(this))
			{
				residual = resid;
				AssociatedResid = i;
				i = total_number_resids;
				if(resid.GetTitle() != null)
				{
					Title += residual.GetTitle();
				}
			}
		}
		SetDocTitle(Title, 0);
	}


	protected boolean CanClose(){
		boolean return_value;
		/*char* message;

		   message = "Are you sure you want to close this display?";

		   switch(MessageBox(message, "Close this display?",
		          MB_YESNO | MB_ICONQUESTION))
		   {
		   case IDNO:
		   	return_value = false;
		      break;
		   default:  */
		return_value = true;
		// }
		return return_value;	
	}

	protected void Draw_Resid(Graphics g){
		
		Graphics2D g2 = (Graphics2D) g;
		// Will do this in the view so can control the size of the TOF
		int x_pos, y_pos, k;
		float minimum_time, maximum_time;
		float window_width, window_height;
		float lower_lim, upper_lim, this_time;
		float[] extrema;
		float font_width, font_height;
		float graph_top, graph_left;
		Point origin;
		float[] time_pointer;
		float[] residual_pointer;
		int num_resid_points;

		Color ResidColor;

		// Draw x and y axes

		Rectangle window_rect = this.getBounds();
		window_width = (float) (window_rect.getWidth());
		window_height = (float) (window_rect.getHeight());

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

		graph_top = (float) Math.min(12, 0.07 * window_height);
		graph_left = 18;

		x_axis = (float) (0.98 * window_width - graph_left);
		x_offset = (float) (0.01 * x_axis);
		y_axis = window_height - font_height - 4 - graph_top;
		y_offset = (float) (0.02 * y_axis);
		starting_x = (int) graph_left;
		starting_y = (int) (graph_top + (int)y_axis);
		origin = new Point(starting_x, starting_y);



		// Draw x and y axes
		g2.drawLine(origin.x, origin.y, origin.x, (int) (origin.y-y_axis));
		g2.drawLine(origin.x, origin.y, (int) (origin.x+x_axis), origin.y);

		// Draw ticks on x axis
		g2.drawLine(origin.x, origin.y, origin.x, origin.y+2);
		g2.drawLine((int)(origin.x+x_axis-x_offset), origin.y, (int)(origin.x+x_axis-x_offset), origin.y+2);

		// Draw ticks on y axis
		g2.drawLine(origin.x, (int)(origin.y-y_offset), origin.x-2, (int)(origin.y-y_offset));
		g2.drawLine(origin.x, (int)(origin.y-y_axis+y_offset), origin.x-2, (int)(origin.y-y_axis+y_offset));
		

		//TFont numberfont("Times Roman", (int) (0.6 * font_height), (int) (0.6 * font_width), 0);

		  Font font = g2.getFont().deriveFont(font_height);
    	  Font sidefont = g2.getFont().deriveFont((.9f*font_height));
    	  g2.setFont(font);
    		FontMetrics metrics = g2.getFontMetrics(font);

		//TRect x_axis_rect(origin.OffsetBy(0, 2), origin.OffsetBy((int) x_axis, 25));
		//TRect y_axis_rect(origin.OffsetBy((int)(-2 - font_height), (int)((10 * font_width - y_axis)/2.0)), origin.OffsetBy(0 , -(int)(y_axis)));
		//DrawResid.DrawText("Flight Time (µs)", -1, x_axis_rect, DT_CENTER | DT_NOCLIP);


		//DrawResid.DrawText("Difference", -1, y_axis_rect, DT_LEFT | DT_NOCLIP);

		num_resid_points = residual.GetNumResidPoints();
		residual_pointer = residual.GetResidArray();
		time_pointer = residual.GetTimeArray();
		if(residual_pointer != null)
		{
			// Determine which points to draw
			minimum_time = Float.parseFloat(param_dialog.GetDefault1());
			maximum_time = Float.parseFloat(param_dialog.GetDefault2());
			lower_lim = Float.parseFloat(param_dialog.GetValue1());
			upper_lim = Float.parseFloat(param_dialog.GetValue2());
			if ((lower_lim < (int) (minimum_time - 1)) || (upper_lim > (int) (maximum_time + 1)) || (lower_lim >= upper_lim))
			{
				///////////////////////////////
				// Insert error message here //
				///////////////////////////////

				starting_time = time_pointer[0];
				ending_time = time_pointer[num_resid_points - 1];

				String lower_lim_recall;  // To recall old lower limit
				String upper_lim_recall;  // To recall new lower limit
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


			extrema = residual.GetMaxMinValues(starting_time, ending_time);
			maximum = extrema[0];
			minimum = extrema[1];

			ResidColor = residual.GetResidColor();

			// Draw Residual points using small dots.

			if(ending_time == starting_time)
			{
				x_spacing = 0;
			}
			else
			{
				x_spacing = (x_axis - x_offset)/(ending_time - starting_time);
			}

			if((maximum - minimum) <= 1e-30)
				y_spacing  = 0/*(y_axis - 2 * y_offset)/2*/;
			else
				y_spacing = (y_axis - 2 * y_offset)/ (maximum - minimum);

			for(k = 0; k < num_resid_points; k++)
			{
				this_time = time_pointer[k];
				if((this_time >= starting_time) && (this_time <= ending_time))
				{
					x_pos = origin.x + (int) ((this_time - starting_time) * x_spacing);
					y_pos = origin.y - (int)((residual_pointer[k] - minimum) * y_spacing) - (int)y_offset;

					g2.drawOval(x_pos, y_pos, 4, 4);
				}
			}

			String string;
			int right_edge;
			right_edge = (int) Math.min((x_axis + 2 * font_width), (window_width - 2 - origin.x));

			string = "" + starting_time;
			//TRect start_time_rect(origin.OffsetBy(-4, 2), origin.OffsetBy(4, 25));
			//DrawResid.DrawText(string, -1, start_time_rect, DT_CENTER | DT_NOCLIP);

			string = "" + ending_time;
			//TRect end_time_rect(origin.OffsetBy((right_edge - 5 * font_width), 2),
					//origin.OffsetBy(right_edge, 25));
			//DrawResid.DrawText(string, -1, end_time_rect, DT_CENTER | DT_NOCLIP);


			string = "" + minimum;
			//TRect min_rect(origin.OffsetBy((-2 - font_height), (-y_offset + 2)),
					//origin.OffsetBy(-4, (-4-y_offset)));
			//DrawResid.DrawText(string, -1, min_rect, DT_LEFT | DT_NOCLIP);


			string = "" + maximum;
			//TRect max_rect(origin.OffsetBy((-2 - font_height), ((strlen(string) * 0.7 * font_width)+y_offset-y_axis)),
					//origin.OffsetBy(-4, (-4+y_offset-y_axis)));
			//DrawResid.DrawText(string, -1, max_rect, DT_LEFT | DT_NOCLIP);


			// Draw a zero dotted line
			
			g2.drawLine(origin.x, (int)(origin.y-y_axis/2), (int)(origin.x+x_axis), (int)(origin.y-y_axis/2));
			
			String temp_text;
			sum_square_gadget_text = "Sum of squares:  ";
			temp_text = "" + residual.GetChiSquared();
			sum_square_gadget_text += temp_text;
			//sum_square_gadget.SetText(sum_square_gadget_text);
		}
		return;
	}
	// and so forth
	/*protected void EvSize(int first, TSize win_size){
		this.Invalidate();
	}
	
	protected void EvMouseMove(int modKeys, TPoint point){
		String temp_text;
		float x_time, y_value;

		if(residual.GetTitle() != 0)
		{
			if((point.x >= starting_x) && (point.x <= (starting_x + x_axis - x_offset)))
			{
				x_gadget_text = "x pos.:  ";

				// Convert the x position to a time in microseconds
				if(x_spacing != 0)
				{
					x_time = ((point.x - starting_x) / x_spacing) + starting_time;
				}
				else
				{
					x_time = starting_time;
				}
				temp_text = x_time;


				x_gadget_text += temp_text;
				x_gadget_text += "  µs";
			}
			else
			{
				x_gadget_text = "x pos.:  ";
			}
			x_pos_gadget.SetText(x_gadget_text);

			if((point.y <= starting_y) && (point.y >= (starting_y - y_axis)))
			{
				y_gadget_text = "y pos.:  ";

				// Convert the x position to a time in microseconds
				y_value = ((starting_y - y_offset - point.y) / y_spacing) + minimum;

				if(point.y == (int) (starting_y + y_offset - y_axis))
				{
					y_value = maximum;
				}
				if(point.y == (int) (starting_y - y_offset))
				{
					y_value = minimum;
				}
				temp_text = "" + y_value;


				y_gadget_text += temp_text;
			}
			else
			{
				y_gadget_text = "y pos.:  ";
			}
			y_pos_gadget.SetText(y_gadget_text);

			sum_square_gadget_text = "Sum of squares:  ";
			temp_text = "" + residual.GetChiSquared();
			sum_square_gadget_text += temp_text;
			sum_square_gadget.SetText(sum_square_gadget_text);
		}
	}*/

	protected void Paint(Graphics g)  {
		Draw_Resid(g);
	}
	protected void CmResidAxisRange(){
		float[] time_pointer;
		float min_time, max_time;
		String lower_time_limit;
		String upper_time_limit;
		String least_possible_time;
		String greatest_possible_time;


		if(param_dialog.GetStatus() == false)
		{
			time_pointer = residual.GetTimeArray();

			min_time = time_pointer[0];
			max_time = time_pointer[(residual.GetNumResidPoints()) - 1];


			least_possible_time = "" + min_time;
			greatest_possible_time = "" + max_time;
			lower_time_limit = "" + starting_time;
			upper_time_limit = "" + ending_time;

			param_dialog.SetDefault1(least_possible_time);
			param_dialog.SetDefault2(greatest_possible_time);
			param_dialog.SetValue1(lower_time_limit);
			param_dialog.SetValue2(upper_time_limit);
			param_dialog.Execute();
		}
	}

	protected void CmResidColorResid(){
		Color c = JColorChooser.showDialog(this, "Choose Color", Color.black);
		
		if (c != null) {
			residual.SetResidColor(c);
		}
	}

	// View notification functions
	/*protected boolean VnUpdateResidView(int index){
		if(residual.GetTitle() == 0)  // i.e. if the residual is gone
		{
			TOFPOEDoc.DeleteView(this.GetViewId());
			//this.CloseWindow();
			TOFPOEDoc.DeleteResid(AssociatedResid);
			TOFPOEDoc.ResetAllResids();
		}
		this.Invalidate();
		return true;
	}

	protected boolean VnResetResidView(int index){
		SetResidInView();
		this.Invalidate();
		return true;
	}*/



}
