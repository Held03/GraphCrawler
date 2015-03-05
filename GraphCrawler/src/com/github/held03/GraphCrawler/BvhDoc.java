package com.github.held03.GraphCrawler;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BvhDoc {

	private static final double epsilonDelta = 1e-4;
	private static final double epsilon = 1e-4;
	
	private int frames;
	private int bones;
	private Function function;
	private double period;
	
	private double boneLength;
	
	public BvhDoc() {
		
	}
	
	public BvhDoc(int frames, int bones, Function function, double period, double boneLength) {
		this.frames = frames;
		this.bones = bones;
		this.function = function;
		this.period = period;
		this.boneLength = boneLength;
		
	}
	
	
	protected Result calc(final int curFrame) {

		final int bones = this.bones;

		final double t = curFrame * period / frames;

		final double lenSq = this.boneLength * this.boneLength;

		final Function f = function;
		
		final ResultElement[] results = new ResultElement[bones];
		
//		System.out.printf("Starting on x=%3.2f with %s bones.%n", t, bones);

		double x1, x2, y1, y2;

		/*
		 * Setting first x to start point
		 */
		x1 = t;

		/*
		 * y is always ?(x)
		 */
		y1 = f.f(x1);

		double absα = 0;

		/*
		 * Print header
		 */
//		System.out.printf("Location of the first Bone (x/y): %3.2f/%3.2f%n",
//				x1, y1);
//		System.out.println();
//		System.out.println("  Bone     rel      abs  ");

		/*
		 * Calc values for each bone.
		 */
		
		double xn = -1;
		
		double gX, fX = -1;
		
		double gδX, fδX;

		for (int i = 0; i < bones; i++) {

			/*
			 * Iterate next x by newton iteration.
			 * 
			 * Finding x for f(x) = 0
			 */
			
			
			int tries = 0;
			
			//long itis = 0;
			
			//System.out.println("Iterate from: x1: " + x1 + " y1:" + y1);
			
			for (tries = 0; tries < 20; tries++) {
			
				xn = x1 + boneLength / (tries + 1);
				
				for (int iter = 0; iter < 40; iter++) {
					// System.out.println(xn);
					
					//itis++;
					
					gX = f.f(xn);
					gδX = f.fδx(xn);
					
					if (Math.abs(gδX) < epsilonDelta)
						break;
					
					fX = (x1 - xn) * (x1 - xn) + (y1 - gX) * (y1 - gX) - lenSq;
					fδX = -2 * (x1 - xn + y1 * gδX - gX * gδX);
					
					xn = xn - fX / fδX;

					if ((xn > x1) && (Math.abs(fX) < epsilon))
						break;
				}
				
				if ((xn > x1) && (Math.abs(fX) < epsilon))
					break;
			}
			
			//System.out.println(" x2 > x1 B:" + i + "\t" + itis + "\t -> " + (xn > x1) + " \t" + (fX) + "\t\t" + tries);

			/*
			 * Set up new x
			 */
			x2 = xn;

			/*
			 * Calc y by sin(x)
			 */
			y2 = f.f(x2);
			
			//System.out.println("  x/y: " + x2 + "/" + y2);

			/*
			 * Calc angle by the different of the old point and the new one.
			 */
			absα = Math.atan((y2 - y1) / (x2 - x1));
			
			results[i] = new ResultElement(new Point2D.Double(x1 - t, y1), absα);
			
			
			x1 = x2;
			y1 = y2;
		}
		
		return new Result(results);
	}
	
	protected String getIndentedLine(int indent, String text) {
		String res = "";
		
		for (int i = 0; i < indent; i++) {
			res += "\t";
		}
		
		return res + text + "\n";
	}
	
	protected String fNum(double number) {
		return String.format("%.6f ", number);
	}
	
	protected String printBoneIter(int bone) {
		if (bone == bones) {
			String res = getIndentedLine(bone, "End Site");
			res += getIndentedLine(bone, "{");
			
			res += getIndentedLine(bone + 1, "OFFSET " + fNum(0) + fNum(0) + fNum(boneLength));

			res += getIndentedLine(bone, "}");
			
			
			return res;
		}
		
		String res = getIndentedLine(bone, String.format("JOINT Bone.%03d", bone));
		res += getIndentedLine(bone, "{");
		
		res += getIndentedLine(bone + 1, "OFFSET " + fNum(0) + fNum(0) + fNum(boneLength));
		res += getIndentedLine(bone + 1, "CHANNELS 3 Xrotation Yrotation Zrotation");
		
		res += printBoneIter(bone + 1);
		
		res += getIndentedLine(bone, "}");
		
		
		return res;
	}
	
	public void save(final File file) {
		
		int frames = this.frames;
		
		try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
		
			pw.print(getIndentedLine(0, "HIERARCHY"));
			pw.print(getIndentedLine(0, "ROOT Bone"));
			pw.print(getIndentedLine(0, "{"));
			
			pw.print(getIndentedLine(1, "OFFSET 0.000000 0.000000 0.000000"));
			pw.print(getIndentedLine(1, "CHANNELS 6 Xposition Yposition Zposition Xrotation Yrotation Zrotation"));
			
			pw.print(printBoneIter(1));
			
			pw.print(getIndentedLine(0, "}"));
			pw.print(getIndentedLine(0, "MOTION"));
			pw.print(getIndentedLine(0, String.format("Frames: %d", frames)));
			pw.print(getIndentedLine(0, "Frame Time: 0.040000"));
			
			Result r;
			String line;
			
			double rad2Deg = 180d / Math.PI;
			
			for (int frame = 0; frame < frames; frame++) {
				r = calc(frame);
				
				line = "";
				
				line += fNum(r.getFirstLocation().getX());
				line += fNum(-r.getFirstLocation().getY());
				line += fNum(0);
				
				for (int i = 0; i < r.getSize(); i++) {
					line += fNum(r.getRelAngle(i) * rad2Deg);
					line += fNum(0);
					line += fNum(0);
				}
				
				pw.print(getIndentedLine(0, line));
			}
			
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}

	/**
	 * @return the frames
	 */
	public int getFrames() {
		return frames;
	}

	/**
	 * @param frames the frames to set
	 */
	public void setFrames(int frames) {
		this.frames = frames;
	}

	/**
	 * @return the bones
	 */
	public int getBones() {
		return bones;
	}

	/**
	 * @param bones the bones to set
	 */
	public void setBones(int bones) {
		this.bones = bones;
	}

	/**
	 * @return the function
	 */
	public Function getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(Function function) {
		this.function = function;
	}

	/**
	 * @return the period
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(double period) {
		this.period = period;
	}

	/**
	 * @return the boneLength
	 */
	public double getBoneLength() {
		return boneLength;
	}

	/**
	 * @param boneLength the boneLength to set
	 */
	public void setBoneLength(double boneLength) {
		this.boneLength = boneLength;
	}
}
