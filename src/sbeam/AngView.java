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

import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;

public class AngView extends JInternalFrame implements InternalFrameListener, MouseInputListener {

	protected float starting_angle, ending_angle;
	protected float x_axis, y_axis;
	protected float x_spacing, y_spacing;
	protected float x_offset, y_offset;
	protected int starting_x, starting_y;
	protected float maximum, minimum;

	protected boolean recalc_integrated_tofs;

	protected JLabel x_pos_gadget;
	protected String x_gadget_text;
	protected JLabel y_pos_gadget;
	protected String y_gadget_text;

	protected Point mouse_position;

	protected Param_Dialog param_dialog;
	protected List_Dialog ang_list_dialog;
	protected String[] list_box_text;

	protected ArrayList<Ang_data> AssociatedAngs;

	protected Color AngColor;

	protected TOFPOEDocument TOFPOEDoc;

	protected int ViewNumber;
	protected MainFrame parent;

	public AngView(TOFPOEDocument doc, MainFrame p) {
		// TODO Auto-generated constructor stub
		super("Title", true, true, true);
		parent = p;
		this.addFocusListener(parent);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		TOFPOEDoc = doc;
		recalc_integrated_tofs = true;
		x_pos_gadget = TOFPOEDoc.GetXPosGadget();
		y_pos_gadget = TOFPOEDoc.GetYPosGadget();

		x_gadget_text = "";
		y_gadget_text = "";

		mouse_position = new Point();

		this.setTitle("Angular Distribution:");

		param_dialog = new Param_Dialog(p, 3); // (Is auto deleted when
												// window is closed.)
		param_dialog.SetType(3);
		ang_list_dialog = new List_Dialog(p, new String[0], 1);

		AssociatedAngs = new ArrayList<Ang_data>();
		ViewNumber = TOFPOEDoc.GetViewNumber();
		SetAngDistsInView();

		starting_angle = 0;
		ending_angle = 360;
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
		this.addInternalFrameListener(this);
	}

	public static String StaticName() {
		return "Angular Display View";
	}

	public boolean SetDocTitle(String docname, int index) {
		this.setTitle(docname);
		return true;
	}

	public String GetViewName() {
		return StaticName();
	}

	public void SetShouldRecalcTOFs(boolean t_or_f) {
		recalc_integrated_tofs = t_or_f;
	}

	protected boolean CanClose() {
		boolean return_value;
		int i, num_associated_angs;
		String message;

		num_associated_angs = AssociatedAngs.size();
		if (num_associated_angs == 1) {
			message = "The angular distribution will remain in memory.";
		} else {
			message = "The angular distributions will remain in memory.";
		}

		for (i = 0; i < num_associated_angs; i++) {
			TOFPOEDoc.RemoveAngFromView(AssociatedAngs.get(i), this);
		}
		TOFPOEDoc.ResetPOEsInPOEViews(); 
		
		parent.internalClosed(this);
		return_value = true;

		return true;
	}

	protected int[] FillListBox(boolean all_nonview_ang_dists,
			boolean is_vs_lab_angle) {
		Ang_data ang_dist;
		int[] index_of_angs;
		int count = 0;
		int total_number_of_angs = TOFPOEDoc.GetNumAngs();
		int i, j, number_of_associated_views;
		boolean this_ang_in_this_view;

		int num_ang_dists = AssociatedAngs.size();
		if ((num_ang_dists == total_number_of_angs)
				&& (all_nonview_ang_dists == true))
			return null;

		if (all_nonview_ang_dists) {
			index_of_angs = new int[total_number_of_angs - num_ang_dists];
			list_box_text = new String[total_number_of_angs - num_ang_dists];
		} else {
			index_of_angs = new int[num_ang_dists];
			list_box_text = new String[num_ang_dists];
		}

		for (i = 0; i < total_number_of_angs; i++) {
			ang_dist = TOFPOEDoc.GetAngData(i);
			number_of_associated_views = ang_dist.GetNumAssociatedViews();
			this_ang_in_this_view = false;
			if (all_nonview_ang_dists == false) {
				for (j = 0; j < number_of_associated_views; j++) {
					if (this.equals(ang_dist.GetAssociatedView(j))) {

						list_box_text[count] = ang_dist.GetTitle();
						index_of_angs[count] = i;
						count++;
					}
				}
			} else {
				for (j = 0; j < number_of_associated_views; j++) {
					if (this.equals(ang_dist.GetAssociatedView(j))) {
						this_ang_in_this_view = true;
					}
				}
				if (this_ang_in_this_view == false) {
					if (ang_dist.GetIsVsLabAngle() == is_vs_lab_angle) {
						list_box_text[count] = ang_dist.GetTitle();
						index_of_angs[count] = i;
						count++;
					}
				}
			} // End of else (i.e. if only want tofs in this view
		} // End of iterating through all tofs
		ang_list_dialog.SetListBoxList(list_box_text);
		return index_of_angs;
	}

	protected void SetAngDistsInView() {
		Ang_data ang_dist;
		int i, j;
		int number_of_associated_views;
		int total_number_angs = TOFPOEDoc.GetNumAngs();
		String Title = "";
		boolean first = true;
		AssociatedAngs.clear();

		for (i = 0; i < total_number_angs; i++) {
			ang_dist = TOFPOEDoc.GetAngData(i);
			number_of_associated_views = ang_dist.GetNumAssociatedViews();

			for (j = 0; j < number_of_associated_views; j++) {
				if (this.equals(ang_dist.GetAssociatedView(j))) {
					AssociatedAngs.add(ang_dist);
					if (Title.length() < 100) {
						if (first) {
							Title += ang_dist.GetTitle();
							first = false;
						} else {
							Title += "; ";
							Title += ang_dist.GetTitle();
						}
					}
				}
			}
		}
		if (Title.length() > 100) {
			Title = "...\0";
		}
		this.setTitle(Title);
		recalc_integrated_tofs = true;
	}

	protected void Draw_AngDist(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int i, j;
		Ang_data ang_dist;
		TOF_data temp_tof;

		int x_pos, y_pos, k;
		float minimum_angle, maximum_angle;
		float window_width, window_height;
		float lower_lim, upper_lim;
		float[] extrema;
		float font_width, font_height;
		float graph_top, graph_left;
		Point origin;
		float[] integrated_TOF_pointer;
		float[] angle_pointer;

		Color AngColor;
		// Draw x and y axes

		Rectangle window_rect = this.getRootPane().getBounds();
		window_width = (float) (window_rect.getWidth());
		window_height = (float) (window_rect.getHeight());

		font_width = (float) Math.max(2, 0.03 * window_width);
		font_height = (float) Math.max(8, 0.06 * window_height);

		if ((font_height > 16)) {
			font_width = Math.min(6, font_width);
			font_height = 16;
		} else {
			font_width = (float) Math.min(font_width, (font_height / 2.4));
		}

		graph_top = this.getInsets().top;
		graph_left = this.getInsets().left;// max(16, 0.038*window_width);

		x_axis = (float) (0.98 * window_width - graph_left);
		x_offset = (float) (0.01 * x_axis);
		y_axis = window_height - font_height - 4;
		y_offset = (float) (0.02 * y_axis);
		starting_x = (int) graph_left;
		starting_y = (int) (graph_top + (int) y_axis);
		origin = new Point(starting_x, starting_y);

		// Draw x and y axes
		g2.drawLine(origin.x, origin.y, origin.x, (int) (origin.y - y_axis));
		g2.drawLine(origin.x, origin.y, (int) (origin.x + x_axis), origin.y);

		// Draw ticks on x axis
		g2.drawLine(origin.x, origin.y, origin.x, origin.y + 2);
		g2.drawLine((int) (origin.x + x_axis - x_offset), origin.y,
				(int) (origin.x + x_axis - x_offset), origin.y + 2);

		// Draw ticks on y axis
		g2.drawLine(origin.x, (int) (origin.y - y_offset), origin.x - 2,
				(int) (origin.y - y_offset));
		g2.drawLine(origin.x, (int) (origin.y - y_axis + y_offset),
				origin.x - 2, (int) (origin.y - y_axis + y_offset));

		// TFont numberfont("Times Roman", (int) (0.6 * font_height), (int) (0.6
		// * font_width), 0);

		Font font = g2.getFont().deriveFont(font_height);
		Font sidefont = g2.getFont().deriveFont((.9f * font_height));
		FontMetrics metrics = g2.getFontMetrics(font);

		// TRect y_axis_rect(origin.OffsetBy((int)(-2 - font_height), (int)((24
		// * font_width - y_axis)/2.0)), origin.OffsetBy(0 , -(int)(y_axis)));
		g2.drawString("Angle (degrees)",
				(int) (origin.x + x_axis / 2.0 - metrics
						.stringWidth("Angle (degrees)")), origin.y + 25);

		// DrawAng.DrawText("Integrated TOF (arb. units)", -1, y_axis_rect,
		// DT_LEFT | DT_NOCLIP);

		int num_angs = AssociatedAngs.size();
		float[] temp_integrated_array;
		int num_ang_tofs, total_num_tofs, this_TOFNum;
		int[] tof_num_array;
		float start_time, end_time;

		if (recalc_integrated_tofs) {
			total_num_tofs = TOFPOEDoc.GetNumTOFs();
			// Move through all included Ang_datas and reset the integrated TOFs
			for (i = 0; i < num_angs; i++) {
				ang_dist = AssociatedAngs.get(i);
				temp_integrated_array = ang_dist.GetIntegratedTOFArray();
				num_ang_tofs = ang_dist.GetNumTOFs();
				tof_num_array = ang_dist.GetIncludedTOFNums();

				start_time = ang_dist.GetStartTime();
				end_time = ang_dist.GetEndTime();

				if (temp_integrated_array == null) {
					temp_integrated_array = new float[num_ang_tofs];
					ang_dist.FillIntegratedTOFArray(temp_integrated_array);
				}
				for (j = 0; j < total_num_tofs; j++) {
					temp_tof = TOFPOEDoc.GetTOFData(j);
					this_TOFNum = temp_tof.GetTOFNum();
					for (k = 0; k < num_ang_tofs; k++) {
						if (tof_num_array[k] == this_TOFNum) {
							temp_integrated_array[k] = temp_tof
									.GetIntegratedTotal(start_time, end_time);
						}
					}
				}
				ang_dist.SortArrays();
				recalc_integrated_tofs = false;
			}
		}
		ang_dist = AssociatedAngs.get(0); // First Ang in display!

		// Determine which points to draw
		minimum_angle = Float.parseFloat(param_dialog.GetDefault1());
		maximum_angle = Float.parseFloat(param_dialog.GetDefault2());
		lower_lim = Float.parseFloat(param_dialog.GetValue1());
		upper_lim = Float.parseFloat(param_dialog.GetValue2());

		if ((lower_lim < (int) (minimum_angle - 1))
				|| (upper_lim > (int) (maximum_angle + 1))
				|| (lower_lim >= upper_lim)) {
			// /////////////////////////////
			// Insert error message here //
			// /////////////////////////////

			angle_pointer = ang_dist.GetAngleArray();
			starting_angle = angle_pointer[0];
			ending_angle = angle_pointer[ang_dist.GetNumTOFs() - 1];

			String lower_lim_recall; // To recall old lower limit
			String upper_lim_recall; // To recall new lower limit
			lower_lim_recall = "" + starting_angle;
			upper_lim_recall = "" + ending_angle;
			param_dialog.SetValue1(lower_lim_recall);
			param_dialog.SetValue2(upper_lim_recall);
		} else {
			starting_angle = lower_lim;
			ending_angle = upper_lim;
		}
		// Only draw in Ang dists which should be shown in this view
		for (i = 0; i < num_angs; i++) {
			ang_dist = AssociatedAngs.get(i);
			extrema = ang_dist.GetMaxMinValues(starting_angle, ending_angle);
			if (i == 0) {
				maximum = extrema[0];
				minimum = extrema[1];
			} else {
				maximum = Math.max(maximum, extrema[0]);
				minimum = Math.min(minimum, extrema[1]);
			}
		}

		for (i = 0; i < num_angs; i++) {
			ang_dist = AssociatedAngs.get(i);
			AngColor = ang_dist.GetAngColor();
			g2.setColor(AngColor);

			// Draw Ang. distribution points using small dots.
			integrated_TOF_pointer = ang_dist.GetIntegratedTOFArray();
			angle_pointer = ang_dist.GetAngleArray();
			float angle;
			int num_ang_points = ang_dist.GetNumTOFs();

			if (ending_angle == starting_angle) {
				x_spacing = 0;
			} else {
				x_spacing = (x_axis - x_offset)
						/ (ending_angle - starting_angle);
			}

			if (maximum == 0)
				y_spacing = 0/* (y_axis - 2 * y_offset)/2 */;
			else
				y_spacing = (y_axis - 2 * y_offset) / maximum;

			for (k = 0; k < num_ang_points; k++) {
				angle = angle_pointer[k];
				if ((angle >= starting_angle) && (angle <= ending_angle)) {
					x_pos = origin.x
							+ (int) ((angle - starting_angle) * x_spacing);
					y_pos = origin.y
							- (int) (integrated_TOF_pointer[k] * y_spacing)
							- (int) y_offset;

					g2.drawOval(x_pos, y_pos, 4, 4);
				}
			}
		}

		int right_edge;
		right_edge = (int) Math.min((x_axis + 2 * font_width),
				(window_width - 2 - origin.x));
		/*
		 * TRect start_angle_rect(origin.OffsetBy(-4, 2), origin.OffsetBy(4,
		 * 25)); TRect end_angle_rect(origin.OffsetBy((right_edge - 5 *
		 * font_width), 2), origin.OffsetBy(right_edge, 25));
		 * 
		 * TRect min_rect(origin.OffsetBy((-2 - font_height), ((font_width /
		 * 2)-y_offset)), origin.OffsetBy(-4, (-4-y_offset))); TRect
		 * max_rect(origin.OffsetBy((-2 - font_height), ((3 *
		 * font_width)+y_offset-y_axis)), origin.OffsetBy(-4,
		 * (-4+y_offset-y_axis))); DrawAng.SelectObject(font); String string;
		 * string = "" + starting_angle; DrawAng.DrawText(string, -1,
		 * start_angle_rect, DT_CENTER | DT_NOCLIP); string = "" + ending_angle;
		 * DrawAng.DrawText(string, -1, end_angle_rect, DT_CENTER | DT_NOCLIP);
		 * 
		 * DrawAng.SelectObject(sidefont); DrawAng.DrawText("0", -1, min_rect,
		 * DT_LEFT | DT_NOCLIP); string = "" + maximum; DrawAng.DrawText(string,
		 * -1, max_rect, DT_LEFT | DT_NOCLIP);
		 * 
		 * DrawAng = 0;
		 */
		return;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Draw_AngDist(g);
	}

	protected void AxisRange() {
		Ang_data ang_dist;
		int number_of_ang_dists = AssociatedAngs.size();
		int i;
		float[] angle_pointer;
		float min_angle = 0, max_angle = 0;
		String lower_angle_limit;
		String upper_angle_limit;
		String least_possible_angle;
		String greatest_possible_angle;

		if (param_dialog.GetStatus() == false) {
			for (i = 0; i < number_of_ang_dists; i++) {
				ang_dist = AssociatedAngs.get(i);
				angle_pointer = ang_dist.GetAngleArray();
				if (i == 0) {
					min_angle = angle_pointer[0];
					max_angle = angle_pointer[(ang_dist.GetNumTOFs()) - 1];

					// gcvt(min_angle, 6, least_possible_angle);
					// gcvt(max_angle, 6, greatest_possible_angle);
				} else {
					min_angle = Math.min(min_angle, angle_pointer[0]);
					max_angle = Math.max(max_angle,
							angle_pointer[(ang_dist.GetNumTOFs()) - 1]);
				}
			}
			// param_dialog.Create();
			least_possible_angle = "" + 0;
			greatest_possible_angle = "" + 360;
			lower_angle_limit = "" + starting_angle;
			upper_angle_limit = "" + ending_angle;

			param_dialog.SetDefault1(least_possible_angle);
			param_dialog.SetDefault2(greatest_possible_angle);
			param_dialog.SetValue1(lower_angle_limit);
			param_dialog.SetValue2(upper_angle_limit);
			param_dialog.Execute();
		}
	}

	protected void SetColors() {
		int num_ang_dists = AssociatedAngs.size();
		int[] index_array;
		int index;
		Color AngColor = Color.black;
		// TChooseColorDialog::TData colors;
		Ang_data ang_dist;

		
		index_array = FillListBox(false, false); // False . show only angs for
													// this view in list box

		if (num_ang_dists > 1) {
			ang_list_dialog.SetCaption("Choose an angular distribution:");
			ang_list_dialog.Execute();
			// check

			index = index_array[ang_list_dialog.GetChosenIndex()[0]];
			ang_dist = TOFPOEDoc.GetAngData(index);
		} else {
			index = index_array[0];
			ang_dist = TOFPOEDoc.GetAngData(index);
		}

		Color c = JColorChooser.showDialog(this, "Choose Color", Color.black);
		if(c!=null){
			ang_dist.SetAngColor(c);
		}
		

		TOFPOEDoc.RepaintAllPOEViews();
		TOFPOEDoc.ResetAllTOFColors(index);
	}

	protected void AppendNewAngularDistribution() {
		AngularDialog ang_dialog = new AngularDialog(parent);
		int num_tofs, i, j, num_inc_tofs, current_ang_number;
		boolean is_vs_lab_angle;

		Ang_data angular_dist;
		int[] tof_num_array, TOFNumArray;
		int /** temp_tof_num_array, */
		[] tempTOFNumArray;
		float[] angle_array = null;

		float temp_angle, remainder, temp_angle_over_360;
		float start_time, end_time;

		TOF_data[] tofs;
		num_tofs = TOFPOEDoc.GetNumTOFs();
		tofs = new TOF_data[num_tofs];

		for (i = 0; i < num_tofs; i++) {
			tofs[i] = TOFPOEDoc.GetTOFData(i);
		}

		is_vs_lab_angle = AssociatedAngs.get(0).GetIsVsLabAngle();
		if (is_vs_lab_angle) {
			ang_dialog.setTitle("Lab angular distribution:");
			ang_dialog.SetDialogData(num_tofs, tofs, true); // Last param.is not
															// lab angle dist.
		} else {
			ang_dialog.setTitle("Polarization angular distribution:");
			ang_dialog.SetDialogData(num_tofs, tofs, false); // Last param.is
																// lab angle
																// dist.
		}

		ang_dialog.Execute();
		// check

		current_ang_number = TOFPOEDoc.GetLastAngNumber();

		if (ang_dialog.GetIsEntireRange() == false) {
			start_time = ang_dialog.GetStartTime();
			end_time = ang_dialog.GetEndTime();
		} else {
			start_time = 0;
			end_time = -1;
		}

		angular_dist = new Ang_data(is_vs_lab_angle, (current_ang_number + 1),
				start_time, end_time);

		num_inc_tofs = ang_dialog.GetNumIncludedTOFs();
		tof_num_array = ang_dialog.GetIncludedTOFArray();
		TOFNumArray = ang_dialog.GetTOFNumArray();

		// Filter out TOFs which aren't polarized
		if (!is_vs_lab_angle) {
			for (i = 0; i < num_inc_tofs; i++) {
				if (tofs[tof_num_array[i]].GetLaserPolarized() == false) {
					// Remove this tof_num from array
					num_inc_tofs--;
					for (j = i; j < num_inc_tofs; j++) {
						tof_num_array[j] = tof_num_array[j + 1];
						TOFNumArray[j] = TOFNumArray[j + 1];
					}
					i--;
				}
			}

			if (num_inc_tofs != 0) {
				// temp_tof_num_array = new int[num_inc_tofs];
				tempTOFNumArray = new int[num_inc_tofs];
				for (i = 0; i < num_inc_tofs; i++) {
					// temp_tof_num_array[i] = tof_num_array[i];
					tempTOFNumArray[i] = TOFNumArray[i];
				}
				// tof_num_array = temp_tof_num_array;
				TOFNumArray = tempTOFNumArray;

			}
		}

		if (num_inc_tofs != 0) {
			angle_array = new float[num_inc_tofs];
		}

		for (i = 0; i < num_inc_tofs; i++) {
			// Get all angles between 0 and 360 degrees
			if (is_vs_lab_angle) {
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
			} else {
				temp_angle = tofs[tof_num_array[i]].GetPolarizationAngle();
				if ((temp_angle > 360) || (temp_angle < 0)) {
					temp_angle_over_360 = (float) (temp_angle / 360.0);
					remainder = temp_angle_over_360 - (int) temp_angle_over_360;
					temp_angle = (float) (remainder * 360.0);

					if (temp_angle < 0) {
						temp_angle += 360.0;
					}
				}
				angle_array[i] = temp_angle;
			}
		}

		if (num_inc_tofs != 0) {
			angular_dist.SetAngleArray(angle_array);
			angular_dist.SetIncludedTOFs(num_inc_tofs, TOFNumArray);
			angular_dist.AddAssociatedView(this);

			// Add this Angular distribution to the document
			// session_document
			TOFPOEDoc.AddAngData(angular_dist);
		}

	}

	protected void AppendLoadedAngularDistribution() {
		int[] index_array;
		boolean is_vs_lab_angle;
		Ang_data ang_dist;
		is_vs_lab_angle = AssociatedAngs.get(0).GetIsVsLabAngle();

		index_array = FillListBox(true, is_vs_lab_angle); // True . show all
															// non-displayed in
															// list box
		if (index_array != null) // i.e. if some loaded TOFs not in this view
									// already
		{
			if (is_vs_lab_angle) {
				ang_list_dialog
						.SetCaption("Choose a lab angular distribution:");
			} else {
				ang_list_dialog
						.SetCaption("Choose a polarization angular distribution:");
			}

			ang_list_dialog.Execute();
			// check
			ang_dist = TOFPOEDoc.GetAngData(index_array[ang_list_dialog
					.GetChosenIndex()[0]]);
			ang_dist.AddAssociatedView(this);
			this.SetAngDistsInView();
			TOFPOEDoc.ResetAngsInAngViews();

		}
	}

	protected void RemoveAngularDistributionFromDisplay() {
		int[] index_array;
		index_array = FillListBox(false, false); // False . show only Angs for
													// this view in list box
		ang_list_dialog.SetCaption("Choose an Angular Distribution to remove:");

		int chosen_index, ang_index;

		ang_list_dialog.Execute();
		// check

		chosen_index = ang_list_dialog.GetChosenIndex()[0]; // Corresponds to
															// which ang in this
															// view
		ang_index = index_array[chosen_index];
		TOFPOEDoc.RemoveAngFromView(AssociatedAngs.get(chosen_index), this);

		VnUpdateAngView(0);
	}

	// View notification functions
	protected boolean VnUpdateAngView(int index) {
		recalc_integrated_tofs = true;
		SetAngDistsInView();
		return true;
	}

	public void execute() {
		parent.addFrame(this);
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(400, 200));
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setFocusable(true);
		this.setEnabled(true);
		this.setVisible(true);

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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		String temp_text;
		float x_angle, y_value;
		Point point = e.getPoint();

		if ((point.x >= starting_x)
				&& (point.x <= (starting_x + x_axis - x_offset))) {
			x_gadget_text = "";

			// Convert the x position to a time in microseconds
			if (x_spacing != 0) {
				x_angle = ((point.x - starting_x) / x_spacing) + starting_angle;
			} else {
				x_angle = starting_angle;
			}
			temp_text = "" + x_angle;

			x_gadget_text += temp_text;
			x_gadget_text += " degrees";
		} else {
			x_gadget_text = "";
		}
		x_pos_gadget.setText(x_gadget_text);

		if ((point.y <= starting_y) && (point.y >= (starting_y - y_axis))) {
			y_gadget_text = "";

			if (y_spacing != 0) {
				y_value = ((starting_y - y_offset - point.y) / y_spacing)/*
																		 * +
																		 * minimum
																		 */;
			} else {
				y_value = 0/* minimum */;
			}
			if (point.y == (int) (starting_y + y_offset - y_axis)) {
				y_value = maximum;
			}
			if (point.y == ((int) (starting_y - y_offset))) // Second value is
															// an int
			{
				y_value = 0;
			}
			temp_text = "" + y_value;

			y_gadget_text += temp_text;
		} else {
			y_gadget_text = "";
		}
		y_pos_gadget.setText(y_gadget_text);
	}

}
