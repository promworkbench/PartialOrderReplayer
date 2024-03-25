package org.processmining.partialorder.ptrace.param;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.partialorder.ptrace.param.PTraceParameter.PTraceType;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class PTraceParameterDialog extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5959388817885508338L;

	protected PTraceParameter param;
	protected JComboBox<PTraceType> selectNameCombo;

	public PTraceParameterDialog(UIPluginContext context, final PTraceParameter param) {
		super();
		initLayout();
		this.param = param;

		addComponentTitle("<html><h2>Select builder parameters</h2>", "0, 0");
		addComponentSelectPTraceBuilder();

	}

	private void addComponentTitle(String label, String position) {
		add(SlickerFactory.instance().createLabel(label), position);
	}

	private void initLayout() {
		double size[][] = { { TableLayoutConstants.FILL }, { 30, 50, 30, 50, 30 } };
		setLayout(new TableLayout(size));
	}

	private void addComponentSelectPTraceBuilder() {
		addComponentTitle("Select p-trace builder: ", "0, 1");

		PTraceType[] types = PTraceType.values();
		selectNameCombo  = new JComboBox<PTraceParameter.PTraceType>(types);
		SlickerDecorator.instance().decorate(selectNameCombo);
		selectNameCombo.setSelectedItem(param.getType());
		selectNameCombo.addActionListener(this);

		this.add(selectNameCombo, "0, 2");

		//		addComponentTitle("Select clone log: ", "0, 3");
		//		final JCheckBox box = new JCheckBox("update the original log with partial order info");
		//		SlickerDecorator.instance().decorate(box);
		//		box.addActionListener(new ActionListener() {			
		//			public void actionPerformed(ActionEvent e) {
		//				boolean selected = box.isSelected();
		//				param.setCloningLog(!selected);
		//				
		//			}
		//		});
		//		box.setSelected(true);
		//		this.add(box, "0, 4");
	}
	
	public PTraceType getSelectedPTraceType(){
		return (PTraceType) selectNameCombo.getSelectedItem();
	}

	public void actionPerformed(ActionEvent e) {
		PTraceType selectedType = getSelectedPTraceType();
		param.setType(selectedType);
	}

}
