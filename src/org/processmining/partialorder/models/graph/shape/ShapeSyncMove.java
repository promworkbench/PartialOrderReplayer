package org.processmining.partialorder.models.graph.shape;

import java.awt.geom.Point2D;

import org.processmining.models.shapes.Polygon;

public class ShapeSyncMove extends Polygon {

	protected Point2D[] getPoints(double x, double y, double width, double height) {
		Point2D[] points = new Point2D[5];
		double offset =  width * 0.12 ;
		
		points[0] = new Point2D.Double(x + (offset - 1), y);
		points[1] = new Point2D.Double(x + (offset * 5.8 - 1), y );
		
		points[2] = new Point2D.Double(x + (width - 1), y + (height - 1)/ 2);
		
		points[3] = new Point2D.Double(x + (offset * 5.8 - 1), y + (height - 1));
		points[4] = new Point2D.Double(x + (offset - 1), y + (height - 1));
	
		
		return points;
	}

}
