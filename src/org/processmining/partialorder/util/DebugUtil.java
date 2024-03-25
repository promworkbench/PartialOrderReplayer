package org.processmining.partialorder.util;

import java.util.Map;
import java.util.Map.Entry;

import nl.tue.astar.util.PartiallyOrderedTrace;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;
import org.processmining.partialorder.dal.models.EnumDataAccessType;

public class DebugUtil {
	public static boolean doprint = true;

	public static void println(String string) {
		if (!doprint){
			return;
		}
		System.out.println(string);

	}

	public static void printlnPOTrace(String string, PartiallyOrderedTrace po) {
		if (!doprint){
			return;
		}
		System.out.println(string + ": " + po.toString());
		for (int i = 0; i < po.getSize(); i++) {
			int[] a = po.getPredecessors(i);
			String preds = "[";
			for(int j : a){
				preds += j + ",";
			}
			preds+= "]";
			System.out.println(" preds of " + i + " : " + preds );
		}
	}

	public static void print(Map<String, Map<String, Integer>> numInput) {
		if (!doprint){
			return;
		}
		for(Entry<String, Map<String, Integer>> entry : numInput.entrySet()){
			for( Entry<String, Integer> value : entry.getValue().entrySet()){
				System.out.println("[" + entry.getKey() + "," + value.getKey()+"] = " + value.getValue()); 
			} 
		}
		
	}

	public static void printIOmap(Map<String, Map<String, EnumDataAccessType>> map) {
		if (!doprint){
			return;
		}
		for(Entry<String, Map<String, EnumDataAccessType>> entry : map.entrySet()){
			for( Entry<String, EnumDataAccessType> value : entry.getValue().entrySet()){
				System.out.println("[" + entry.getKey() + "," + value.getKey()+"] = " + value.getValue()); 
			} 
		}
	}

	public static void print(String s, XTrace t) {
		StringBuilder b = new StringBuilder();
		b.append(s + "[");
		for(int i = 0; i < t.size(); i++){
			b.append(XConceptExtension.instance().extractName(t.get(i)) + ",");
		}
		b.append("]");
		System.out.println(b.toString());
		
	}
	
	
	

}
