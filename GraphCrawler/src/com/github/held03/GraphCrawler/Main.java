package com.github.held03.GraphCrawler;

import java.io.File;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		BvhDoc doc = new BvhDoc();

		System.out.println("Generating...");
		
		doc.setBones(20);
		doc.setBoneLength(0.5);
		doc.setFrames(120);
		doc.setPeriod(10 * Math.PI);
		doc.setFunction(new Function() {
			
			@Override
			public double f(double x) {
				return Math.sin(x) + 0.333 * Math.sin(x * 0.2 + 0.2);
			}
			
			@Override
			public double fδx(double x) {
				return Math.cos(x) + 0.333 * 0.2 * Math.cos(x * 0.2 + 0.2);
			}
		});
		
		doc.save(new File("/home/held03/tmp/snakeAnim.bvh"));
		
//		try {
//			Runtime.getRuntime().exec("gedit /home/held03/tmp/snakeAnim.bvh");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("Done.");
	}

}
