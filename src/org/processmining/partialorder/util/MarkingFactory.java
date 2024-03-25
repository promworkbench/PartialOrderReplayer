package org.processmining.partialorder.util;

import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;

public class MarkingFactory {

	public static Marking[] createFinalMarkings(UIPluginContext context, PetrinetGraph net) {
		Marking[] finalMarkings;
		ConnectionManager connManager = context.getConnectionManager();
		try {
			Collection<FinalMarkingConnection> conns = connManager.getConnections(FinalMarkingConnection.class,
					context, net);
			finalMarkings = new Marking[conns.size()];
			if (conns != null) {
				int i = 0;
				for (FinalMarkingConnection fmConn : conns) {
					finalMarkings[i] = fmConn.getObjectWithRole(FinalMarkingConnection.MARKING);
					i++;
				}
			}
		} catch (ConnectionCannotBeObtained excCon) {
			if (0 == JOptionPane
					.showConfirmDialog(
							new JPanel(),
							"No final marking is found for this model. Current manifest replay require final marking. Do you want to create one?",
							"No Final Marking", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				if (!createMarking(context, net, FinalMarkingConnection.class)) {
					return null;
				}
				;
				try {
					finalMarkings = new Marking[1];
					finalMarkings[0] = connManager.getFirstConnection(FinalMarkingConnection.class, context, net)
							.getObjectWithRole(FinalMarkingConnection.MARKING);
					if (finalMarkings[0] == null) {
						JOptionPane.showMessageDialog(new JPanel(), "No final marking is created. Please re-run the plugin.");
					}
					return finalMarkings;
				} catch (ConnectionCannotBeObtained e) {
					e.printStackTrace();
					finalMarkings = new Marking[0];
				}
			} else {
				return null;
			}
			;
		} catch (Exception exc) {
			finalMarkings = new Marking[0];
		}
		return finalMarkings;
	}

	public static Marking createInitialMarking(UIPluginContext context, PetrinetGraph net) {
		// generate algorithm selection GUI, look for initial marking and final markings
		Marking initialMarking;
		ConnectionManager connManager = context.getConnectionManager();
		// check existence of initial marking
		try {
			InitialMarkingConnection initCon = connManager.getFirstConnection(InitialMarkingConnection.class, context,
					net);

			initialMarking = (Marking) initCon.getObjectWithRole(InitialMarkingConnection.MARKING);
			if (initialMarking.isEmpty()) {
				JOptionPane
						.showMessageDialog(
								new JPanel(),
								"The initial marking is an empty marking. If this is not intended, remove the currently existing InitialMarkingConnection object and then use \"Create Initial Marking\" plugin to create a non-empty initial marking.",
								"Empty Initial Marking", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ConnectionCannotBeObtained exc) {
			if (0 == JOptionPane.showConfirmDialog(new JPanel(),
					"No initial marking is found for this model. Do you want to create one?", "No Initial Marking",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
				createMarking(context, net, InitialMarkingConnection.class);
				try {
					initialMarking = connManager.getFirstConnection(InitialMarkingConnection.class, context, net)
							.getObjectWithRole(InitialMarkingConnection.MARKING);
				} catch (ConnectionCannotBeObtained e) {
					e.printStackTrace();
					initialMarking = new Marking();
				}
			} else {
				initialMarking = new Marking();
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
			initialMarking = new Marking();
		}
		return initialMarking;
	}

	public static boolean createMarking(UIPluginContext context, PetrinetGraph net, Class<? extends Connection> classType) {
		boolean result = false;
		Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(
				ConnectionObjectFactory.class, classType, context.getClass(), true, false, false, net.getClass());
		PluginContext c2 = context.createChildContext("Creating connection of Type " + classType);
		Pair<Integer, PluginParameterBinding> pair = plugins.iterator().next();
		PluginParameterBinding binding = pair.getSecond();
		try {
			PluginExecutionResult pluginResult = binding.invoke(c2, net);
			pluginResult.synchronize();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c2.getParentContext().deleteChild(c2);
		}
		return result;
	}
}
