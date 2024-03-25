package org.processmining.partialorder.zexperiment;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.partialorder.plugins.vis.PAlignmentVisualizer;
import org.processmining.partialorder.ptrace.plugins.vis.PTracesVisualizer;
@Visualizer
public class PNRepResultMapVisualizer {
		
		@Visualizer
		@PluginVariant(requiredParameterLabels = { 0 })
		@Plugin(name = "Visualize AlignmentMap", returnLabels = { "Visualized PO Alignment graphs" }, returnTypes = { JComponent.class }, 
		parameterLabels = { "PO Traces" }, userAccessible = false)
		public JComponent visualize(PluginContext context, PNRepResultMap logReplayResults) {
			JTabbedPane tabbedPane = new JTabbedPane();
			
			PTracesVisualizer traceVis = new PTracesVisualizer();
			PAlignmentVisualizer alignVis = new PAlignmentVisualizer();
			
			
			for(String key : logReplayResults.keySet()){
				
				JComponent comp = traceVis.visualizeAsGraph(context, logReplayResults.get(key));
				tabbedPane.add("Trace " + key, comp);
				JComponent comp2 = alignVis.visualize(context, logReplayResults.get(key));
				tabbedPane.add("Align " + key, comp2);
			}
			return tabbedPane;
		}
}
