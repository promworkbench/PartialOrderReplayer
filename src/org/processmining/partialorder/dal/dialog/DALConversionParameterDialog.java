package org.processmining.partialorder.dal.dialog;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.annotation.DALConversionAlgorithm;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.partialorder.dal.param.DALConversionParameters;
import org.processmining.partialorder.dal.plugins.conversion.DALConversionAlg;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class DALConversionParameterDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1196518499525959453L;
	private DALConversionParameters param;

	public DALConversionParameterDialog(final UIPluginContext context, final DALConversionParameters param) {
		super();
		initLayout();

		this.param = param;

		add(SlickerFactory.instance().createLabel("<html><h2>Select mining parameters</h2>"), "0, 0");

		addComponentForSelectIsStartInput();
		addComponentForSelectRatio();
		addComponentForSelectAlgorithm(context);
	}

	private void initLayout() {
		/*
		 * Get a layout containing a single column and two rows, where the top
		 * row height equals 30.
		 */
		double size[][] = { { TableLayoutConstants.FILL }, { 30, 50, 30, 50, 30, 50, 30 } };
		setLayout(new TableLayout(size));
	}

	private void addComponentForSelectIsStartInput() {
		add(SlickerFactory.instance().createLabel("Select input/output: "), "0, 1");
		final JCheckBox checkbox = new JCheckBox("is Start attribute an input attribute");
		checkbox.setSelected(param.isAttrOfStartInput());
		SlickerDecorator.instance().decorate(checkbox);

		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				param.setAttrOfStartInput(checkbox.isSelected());
			}
		});

		this.add(checkbox, "0, 2");
	}

	private void addComponentForSelectRatio() {
		final JLabel label = SlickerFactory.instance().createLabel("Select ratio: " + param.getRatio());
		add(label, "0, 3");

		final JSlider ratioSlider = new JSlider();
		ratioSlider.setName("Select ratio");
		SlickerDecorator.instance().decorate(ratioSlider);
		ratioSlider.setMinimum(0);
		ratioSlider.setMaximum(100);
		ratioSlider.setValue(param.getRatio());
		ratioSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				if (!ratioSlider.getValueIsAdjusting()) {
					param.setRatio(ratioSlider.getValue());
					label.setText("Select ratio: " + String.valueOf(ratioSlider.getValue()));
				}
			}
		});
		this.add(ratioSlider, "0, 4");
	}

	private void addComponentForSelectAlgorithm(UIPluginContext context) {
		add(SlickerFactory.instance().createLabel("Select conversion algorithm: "), "0, 5");
		List<DALConversionAlg> availAlgorithms = getAvailableDALConversionAlgs(context);

		final JComboBox<DALConversionAlg> combo = new JComboBox<DALConversionAlg>(
				availAlgorithms.toArray(new DALConversionAlg[availAlgorithms.size()]));
		combo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DALConversionAlg alg = (DALConversionAlg) combo.getSelectedItem();
				param.setConversionAlgorithm(alg);

			}
		});

		SlickerDecorator.instance().decorate(combo);
		this.add(combo, "0, 6");
	}
	

	private List<DALConversionAlg> getAvailableDALConversionAlgs(UIPluginContext context) {
		List<DALConversionAlg> availAlgorithms = new ArrayList<DALConversionAlg>();
		Set<Class<?>> conversionAlgSet = context.getPluginManager().getKnownClassesAnnotatedWith(
				DALConversionAlgorithm.class);
		for (Class<?> algClass : conversionAlgSet) {
			try {
				DALConversionAlg alg = (DALConversionAlg) algClass.newInstance();
				availAlgorithms.add(alg);
			} catch (InstantiationException e1) {
				// do nothing
			} catch (IllegalAccessException e1) {
				// do nothing
			} catch (Exception exc) {
				// do nothing
			}
		}
		return availAlgorithms;
	}

}
