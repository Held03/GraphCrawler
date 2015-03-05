/**
 * 
 */
package com.github.held03.GraphCrawler;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * @author held03
 *
 */
public class Result {

	private final ResultElement[] elements;
	
	public Result(Collection<ResultElement> results) {
		if (results == null) {
			throw new IllegalArgumentException("Result must not be null.");
		}
		
		elements = new ResultElement[results.size()];
		
		int i = 0;
		
		for (ResultElement e : results) {
			elements[i++] = e;
		}
	}
	
	public Result(ResultElement[] results) {
		if (results == null) {
			throw new IllegalArgumentException("Result must not be null.");
		}
		
		elements = results.clone();
		
	}
	
	public int getSize() {
		return elements.length;
	}
	
	public ResultElement getElement(int i) {
		return elements[i];
	}
	
	public Point2D getFirstLocation() {
		if (elements.length == 0) {
			throw new NoSuchElementException();
		}
		
		return elements[0].absLocation;
	}
	
	public double getAbsAngle(int i) {
		return elements[i].absAngle;
	}
	
	public double getRelAngle(int i) {
		if (i == 0)
			return getAbsAngle(0);
		
		return (getAbsAngle(i) - getAbsAngle(i-1));
	}

}
