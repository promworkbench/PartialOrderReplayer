package org.processmining.partialorder.plugins.vis.auxiliary;

@Deprecated
public class TableLayoutCoord {
	
	
	public int x = 0;
	public int y = 0;
	
	public String toString(){
		return x + ", " +  y;
	}
	
	public String incColumn() {
		x++;
		return toString();
	}
	
	public String incRow() {
		y++;
		return toString();
	}
}
