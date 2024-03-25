package org.processmining.partialorder.zexperiment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.partialorder.util.MarkingFactory;
import org.processmining.plugins.petrinet.manifestreplayer.EvClassPattern;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.DefTransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.ITransClassifier;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClasses;

public class M2HCaseStudy {
	

	public static PNManifestReplayerParameter autoComputeHospitalLogParameters(UIPluginContext context, Petrinet net, XLog log) {
		Marking initMarking = MarkingFactory.createInitialMarking(context, net);
		Marking[] finalMarkings = MarkingFactory.createFinalMarkings(context, net);

		// create mapping
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, XLogInfoImpl.STANDARD_CLASSIFIER);
		XEventClasses eventClasses = logInfo.getEventClasses();

		ITransClassifier transClassifier = new DefTransClassifier();
		TransClasses transClasses = new TransClasses(net, transClassifier);

		Map<TransClass, Integer> mapTrans2Cost = new HashMap<TransClass, Integer>();
		Map<XEventClass, Integer> mapEvClass2Cost = new HashMap<XEventClass, Integer>();

		Map<String, TransClass> mapLabel2Trans = new HashMap<String, TransClass>();
		for (Transition t : net.getTransitions()) {
			mapLabel2Trans.put(t.getLabel(), transClasses.getClassOf(t));
			System.out.println(t.getLabel());

			
			
//			if(t.getLabel().equals("Endoscopie")){
//				mapTrans2Cost.put(transClasses.getClassOf(t), 50);
//			} else if(t.getLabel().equals("OK Anker")){
//				mapTrans2Cost.put(transClasses.getClassOf(t), 50);
//			} else {
				mapTrans2Cost.put(transClasses.getClassOf(t), 2);
//			}
		}

		Map<String, EvClassPattern> mapLabel2Pattern = new HashMap<String, EvClassPattern>();
		for (XEventClass c : eventClasses.getClasses()) {
			EvClassPattern p = new EvClassPattern();
			p.add(c);
			mapLabel2Pattern.put(c.toString(), p);
			System.out.println(c.toString());

			
//			if(c.toString().equals("dikke darm-coloscopie met poliepectomie+complete")){
//				mapEvClass2Cost.put(c, 50);
//			} else 
			if(c.toString().equals("OK Anker verrichtingen+complete")){
				mapEvClass2Cost.put(c, 600);
			} else if(c.toString().equals("Lab Test LCHE+complete")){
				mapEvClass2Cost.put(c, 2);
			} else if(c.toString().equals("Lab Test-LHMA+complete")){
				mapEvClass2Cost.put(c, 2);
			} else if(c.toString().contains("consult algemeen")){
				mapEvClass2Cost.put(c, 2);
			} else {
				mapEvClass2Cost.put(c, 200);
			}
			
			
			
		}

		Map<TransClass, Set<EvClassPattern>> mapTrans2List = new HashMap<TransClass, Set<EvClassPattern>>(4);
		
		Set<EvClassPattern> setPattern = new HashSet<EvClassPattern>();
		setPattern.add(mapLabel2Pattern.get("dikke darm-coloscopie met poliepectomie+complete"));
//		setPattern.add(mapLabel2Pattern.get("dikke darm-coloscop. met argonplasmacoag+complete")); // cutted100
		mapTrans2List.put(mapLabel2Trans.get("Endoscopie"), new HashSet<EvClassPattern>(setPattern));

		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("ct thoraxz m c+complete"));
		setPattern.add(mapLabel2Pattern.get("ct abdomenz m c+complete"));
		mapTrans2List.put(mapLabel2Trans.get("CT abdomen"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("Consult coloncare verpleegkundige+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Coloncare verpleegkundige"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("Consult coloncare verpleegkundige+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Coloncare verpleegkundig"), new HashSet<EvClassPattern>(setPattern));
		
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("thorax2 r+complete"));
		setPattern.add(mapLabel2Pattern.get("thorax1 r+complete"));
		mapTrans2List.put(mapLabel2Trans.get("X thorax"), new HashSet<EvClassPattern>(setPattern));
		

		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("ontslag ziekenhuis+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Ontslag"), new HashSet<EvClassPattern>(setPattern));
		
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("OK Anker verrichtingen+complete"));
		mapTrans2List.put(mapLabel2Trans.get("OK Anker"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("electrocardiografie+complete"));
		mapTrans2List.put(mapLabel2Trans.get("ECG"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("verv.consult-dietetiek (15 min)+complete")); 
//		setPattern.add(mapLabel2Pattern.get("eerste consult-dietetiek (15 min)+complete"));  // v5CuttHLL
		setPattern.add(mapLabel2Pattern.get("voedingstoest.en nieuw diet. co. 15 min+complete")); 
		mapTrans2List.put(mapLabel2Trans.get("dietetiek"), new HashSet<EvClassPattern>(setPattern));
		
		
		setPattern.clear();
//		setPattern.add(mapLabel2Pattern.get("mri onderbuik+complete")); // cutted100
		setPattern.add(mapLabel2Pattern.get("mri tractus urogenitalis+complete"));
		setPattern.add(mapLabel2Pattern.get("mri colon+complete")); 
		setPattern.add(mapLabel2Pattern.get("mri abdomen+complete"));
		mapTrans2List.put(mapLabel2Trans.get("MRI"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("Lab Test-LCHE+complete"));
		setPattern.add(mapLabel2Pattern.get("Lab Test-LHMA+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Lab"), new HashSet<EvClassPattern>(setPattern));
		
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("PANE+complete"));
		mapTrans2List
		.put(mapLabel2Trans.get("PANE (pre klinische screening)"), new HashSet<EvClassPattern>(setPattern));
		

		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("opname ziekenhuis+complete"));		
		mapTrans2List.put(mapLabel2Trans.get("Opname"), new HashSet<EvClassPattern>(setPattern));
		
	
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("Order OK+complete"));
		mapTrans2List.put(mapLabel2Trans.get("OK order"), new HashSet<EvClassPattern>(setPattern));

		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - HEE+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - HEE+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Poli heelkunde"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - MDL+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - MDL+complete"));
		mapTrans2List.put(mapLabel2Trans.get("MDL spoed poli"), new HashSet<EvClassPattern>(setPattern));
	
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - MDL+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - MDL+complete"));
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - HEE+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - HEE+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Consult Behandelplan voorstel"), new HashSet<EvClassPattern>(setPattern));
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - HEE+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - HEE+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Gesprek chirurg"), new HashSet<EvClassPattern>(setPattern));
		
		
		setPattern.clear();
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - HEE+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - HEE+complete"));
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen - MDL+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen - MDL+complete"));
		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen+complete"));
		setPattern.add(mapLabel2Pattern.get("1e consult algemeen+complete"));
		mapTrans2List.put(mapLabel2Trans.get("Poli"), new HashSet<EvClassPattern>(setPattern));
		
//		setPattern.clear();
//		setPattern.add(mapLabel2Pattern.get("Vervolgconsult algemeen+complete"));
//		setPattern.add(mapLabel2Pattern.get("1e consult algemeen+complete"));
		
		// Should not put transitions that do not have mapping. 
//		mapTrans2List.put(mapLabel2Trans.get("tr12"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("skip"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr35"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr41"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr42"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr43"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr44"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr45"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr46"), new HashSet<EvClassPattern>());
//		mapTrans2List.put(mapLabel2Trans.get("tr47"), new HashSet<EvClassPattern>());
		
//		assert transClasses.getTransClasses().size() == mapTrans2List.size();
//		assert mapTrans2Cost.keySet().size() == transClasses.getTransClasses().size();
//		assert mapEvClass2Cost.keySet().size() == eventClasses.getClasses().size();
		
		TransClass2PatternMap mapping = new TransClass2PatternMap(log, net, XLogInfoImpl.STANDARD_CLASSIFIER,
				transClasses, mapTrans2List);

		PNManifestReplayerParameter parameter = new PNManifestReplayerParameter(mapTrans2Cost, mapEvClass2Cost,
				mapping, 200000, initMarking, finalMarkings);
		return parameter;
	}

}
