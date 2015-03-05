package com.github.held03.GraphCrawler;

import java.awt.geom.Point2D;

public class ResultElement {

	public final Point2D absLocation;
	
	public final double absAngle;
	
	public ResultElement(double absAngle) {
		absLocation = null;
		this.absAngle = absAngle;
		
	}
	
	public ResultElement(Point2D absLocation, double absAngle) {
		this.absLocation = absLocation;
		this.absAngle = absAngle;
		
	}

}
