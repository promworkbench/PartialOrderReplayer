package org.processmining.partialorder.ptrace.plugins.conversion;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.partialorder.models.dependency.PDependency;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.edge.POEdgeLog;
import org.processmining.partialorder.models.graph.node.POEventNode;
import org.processmining.partialorder.plugins.vis.PartialOrderGraphFactory;
import org.processmining.partialorder.plugins.vis.PartialVisualType;
import org.processmining.partialorder.ptrace.model.PTrace;

public class PTraceToGraphConversion {

	//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	//	@PluginVariant(variantLabel = "Convert to Workshop Graph, default", requiredParameterLabels = { 0 })
	public PartialOrderGraph convertDefault(PluginContext context, PTrace trace, PartialVisualType type) {
		return convert(trace, type, new XEventNameClassifier());
	}

	public PartialOrderGraph convert(PTrace ptrace, PartialVisualType type, XEventClassifier classifier) {
		XTrace trace = ptrace.getTrace();
		XConceptExtension ce = XConceptExtension.instance();
		PartialOrderGraph graph = new PartialOrderGraph(ce.extractName(trace), ptrace.getTraceIndex());
		TIntObjectMap<PONode> map = new TIntObjectHashMap<PONode>();

		// add nodes
		for (int i = 0; i < ptrace.size(); i++) {
			XEvent event = trace.get(i);
			PONode node = new POEventNode(ptrace.getTraceIndex(), graph, i, event);
			node.setLabel(classifier.getClassIdentity(event));
			graph.addNode(node);
			map.put(i, node);
		}

		// add edges
		for (PDependency relation : ptrace.getDependencies()) {
			if (relation.isDirect() || type.equals(PartialVisualType.AS_IS)
					|| type.equals(PartialVisualType.MAXIMAL_CLOSURE)) {
				POEdge e = new POEdgeLog(map.get(relation.getSource()),
						map.get(relation.getTarget()), relation);
				graph.addEdge(e);
			}
		}

		// add transitive edges
		if (type.equals(PartialVisualType.MAXIMAL_CLOSURE)) {
			PartialOrderGraphFactory.addTransitiveEdges(graph);
		}
		return graph;
	}


}
