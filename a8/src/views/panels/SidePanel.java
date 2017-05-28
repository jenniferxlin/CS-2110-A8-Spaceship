package views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

/** JPanel on the side of the screen for displaying stats. */
@SuppressWarnings("serial")
public class SidePanel extends JPanel {

	private Font labelFont, statFont;

	private HashMap<Integer, Stat> stats; // table of game stats that can be
										  // updated

	/** ids for updating different labels. */
	public static final int DISTANCE= 0, 
			TIME= 1, 
			SPEED= 2, 
			LAST_PLANET_NAME= 3, 
			LAST_PLANET_HOSTILITY= 4,
			LAST_PLANET_UPGRADE= 5, 
			NEXT_PLANET_NAME= 6, 
			NEXT_PLANET_HOSTILITY= 7, 
			NEXT_PLANET_UPGRADE= 8,
			STAGE= 9, 
			HP= 10, 
			CURRENT_EDGE_DISTANCE= 11;

	private JSlider speedSlider;

	/** Create a new side panel of dimension (width, height). */
	public SidePanel(int width, int height) {
		super();

		// GUI changes
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(width, height));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// set fonts
		labelFont= new Font("SansSerif", Font.BOLD, 14);
		statFont= new Font("SansSerif", Font.PLAIN, 16);

		// MARK - stats
		// initialize stats table
		stats= new HashMap<>();

		// create menu labels
		addLabel("Progress");
		addStat(STAGE, "", "Rescue stage");
		addStat(DISTANCE, "Distance traveled: ", "" + 0);
		addStat(TIME, "Time elapsed: ", "" + 0);
		addStat(SPEED, "Current speed: ", "" + 0);
		addStat(HP, "HP: ", "" + 3);
		addStat(CURRENT_EDGE_DISTANCE, "Current edge distance: ", "" + 0);
		addLabel(" ");
		addLabel("Previous planet");
		addStat(LAST_PLANET_NAME, " ", "");
		addStat(LAST_PLANET_HOSTILITY, " ", "");
		addStat(LAST_PLANET_UPGRADE, " ", "");
		addLabel(" ");
		addLabel("Next planet");
		addStat(NEXT_PLANET_NAME, " ", "");
		addStat(NEXT_PLANET_HOSTILITY, " ", "");
		addStat(NEXT_PLANET_UPGRADE, " ", "");

		// MARK - slider
		JPanel sliderFrame= new JPanel();
		sliderFrame.add(new JLabel("Ship Speed"));
		sliderFrame.setBackground(Color.WHITE);
		speedSlider= buildSpeedSlider();
		sliderFrame.add(speedSlider);
		add(sliderFrame);

	}

	/**
	 * Append a new label into the menu with text t. This label cannot be
	 * updated later.
	 */
	public void addLabel(String t) {
		JLabel label= new JLabel(t);
		label.setFont(labelFont);
		add(label);
	}

	/** Create and return a speed slider. */
	private JSlider buildSpeedSlider() {
		JSlider slider= new JSlider(JSlider.HORIZONTAL, 0, 10, 1);
		slider.setMajorTickSpacing(1);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		return slider;
	}

	/** Return speed splider */
	public JSlider getSpeedSlider() {
		return speedSlider;
	}

	/**
	 * Append a statistic to display on the menu. The number displayed can be
	 * updated later using the specified id.
	 * 
	 * @param id
	 *            ID associated with this stat. It doesn't matter what you
	 *            choose, as long as you use the same value when you update the
	 *            value in updateStatistic().
	 * @param name
	 *            visible name of statistic that the user can read
	 * @param value
	 *            initial value given to this statistic
	 */
	public void addStat(int id, String name, String value) {
		Stat stat= new Stat(name, value);
		stats.put(id, stat);
		add(stat.label);
	}

	/**
	 * Update an existing statistic.
	 * 
	 * @param id
	 *            id given to the statistic when it was initialized
	 * @param value
	 *            new value to give the statistic
	 */
	public void updateStat(int id, String value) {
		if (!stats.containsKey(id)) {
			System.err.println("Invalid statistic update: " + id);
			return;
		}

		stats.get(id).update(value);
	}

	/**
	 * Wrapper class containing JLabels and metadata describing name and value
	 * of a statistic.
	 */
	private class Stat {

		String name;
		String value;
		JLabel label;

		/**
		 * Initialize a statistic with a visible name statName and value val.
		 */
		Stat(String statName, String val) {
			name= statName;
			value= val;
			label= new JLabel(name + value);
			label.setFont(statFont);
		}

		/** Change the value of the statistic to val. */
		void update(String val) {
			value= val;
			label.setText(name + value);
		}
	}

}
