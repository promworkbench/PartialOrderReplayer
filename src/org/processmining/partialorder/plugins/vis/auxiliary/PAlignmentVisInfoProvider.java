package org.processmining.partialorder.plugins.vis.auxiliary;



import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.partialorder.models.replay.POAlignmentDataProvider;
import org.processmining.partialorder.models.replay.POSyncReplayResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

public class PAlignmentVisInfoProvider {
	private Set<String> misAlignedMoves;
	private Map<String, Color> move2Color;

	public PAlignmentVisInfoProvider(POAlignmentDataProvider data) {

		misAlignedMoves = new HashSet<String>();
		move2Color = new HashMap<String, Color>();
		for (SyncReplayResult res : data.getLogReplayResult()) {
			if (!(res instanceof POSyncReplayResult)) {
				throw (new IllegalArgumentException());
			}
//			POSyncReplayResult pores = (POSyncReplayResult) res;
			List<StepTypes> steps = res.getStepTypes();
//			TIntList indeces = pores.getIndeces();
			// reformat node instance list : list of strings
			List<String> result = new LinkedList<String>();
			for (Object obj : res.getNodeInstance()) {
				String name = null;
				if (obj instanceof Transition) {
					name = ((Transition) obj).getLabel();
				} else if (obj instanceof String) {
					name = (String) obj;

				} else {
					name = obj.toString();
				}
				//XX TODO: adhoc solution!!! why there is + after eventclass name?
				result.add(name.replace("+", ""));

			}

			for (int i = 0; i < steps.size(); i++) {
				if (steps.get(i).equals(StepTypes.MREAL) || steps.get(i).equals(StepTypes.L)) {
					misAlignedMoves.add(result.get(i));
				}
			}

		}

		colorGenerator();

	}

	private void colorGenerator() {
		Random rand = new Random();

		for (String move : misAlignedMoves) {
//			System.out.println(move);
			float r = (float) (0.4 * rand.nextFloat() + 0.5);
			float g = (float) (0.4 * rand.nextFloat() + 0.5);
			float b = (float) (0.4 * rand.nextFloat() + 0.5);
			Color randomColor = new Color(r, g, b);
			move2Color.put(move, randomColor);
		}

	}

	public Set<String> getAllMisAlignedMoves() {
		return misAlignedMoves;
	}

	public Color getMisAlignedMoveColor(String eventClass) {
		String name = eventClass.replace("+", "");
		if (move2Color.containsKey(name)) {
			return move2Color.get(name);
		}
		return null;
	}

}
