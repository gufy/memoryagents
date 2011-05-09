package memagents.memory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import memagents.food.FoodGenerator;
import memagents.memory.gng.LineGNG;
import memagents.memory.gng.MAComputeGNG;
import memagents.memory.gng.NodeGNG;

public class GNGMemory extends Memory implements Runnable {
	
	protected HashMap<Integer, MAComputeGNG> gngEngines;
	protected Thread thread;
	
	public GNGMemory(int width, int height) {
		super(width, height);
		
		gngEngines = new HashMap<Integer, MAComputeGNG>();
		
		for (int foodKind = 0; foodKind < FoodGenerator.getSize(); foodKind++) {
			MAComputeGNG gngEngine = new MAComputeGNG();
			// default: 0.8 1.0E-5 0.0050 6.0E-4 600 8
			gngEngine.setParams(0.8f, 1.0E-5f, 0.005f, 6.0E-4f, 600, 88, 8);
			gngEngines.put(foodKind, gngEngine);
		}
		

		thread = new Thread(this, "GNGMemory");
		thread.start();
	}
	
	public void learn(int foodKind, ArrayList<Point> food) {
		gngEngines.get(foodKind).setDescreteSignals(food);
	}
	
	public Point[] getSample(int foodKind) {
		MAComputeGNG gngEngine = gngEngines.get(foodKind);
		gngEngine.computeExpectedDistribution();
		Point[] sample = new Point[NUM_SAMPLES];
		Random random = new Random();
		int randomX = 0;
		int randomY = 0;
		
		for (int i = 0; i < NUM_SAMPLES; i++) {
			randomX = (int)(gngEngine.getExpectedValueX() + random.nextGaussian() * gngEngine.getExpectedVariance());
			randomY = (int)(gngEngine.getExpectedValueY() + random.nextGaussian() * gngEngine.getExpectedVariance());
			
			sample[i] = new Point(randomX, randomY);
		}
		
		return sample;
	}
	
	public HashMap<Integer, NodeGNG[]> getNodes() {
		HashMap<Integer, NodeGNG[]> nodes = new HashMap<Integer, NodeGNG[]>();
		
		for (int foodKind = 0; foodKind < FoodGenerator.getSize(); foodKind++) {
			MAComputeGNG gngEngine = gngEngines.get(foodKind);
			NodeGNG[] n = gngEngine.getNodes();
			nodes.put(foodKind, n);
		} 
		
		return nodes;
	}
	
	public ArrayList<LineGNG> getLines() {
		ArrayList<LineGNG> lines = new ArrayList<LineGNG>();
		
		for (int foodKind = 0; foodKind < FoodGenerator.getSize(); foodKind++) {
			MAComputeGNG gngEngine = gngEngines.get(foodKind);
			LineGNG[] n = gngEngine.getLines();
			for (int i = 0; i < gngEngine.getNumLines(); i++) {
				lines.add(n[i]);
			}
		} 
		
		return lines;
	}
	
	public Point[] getExpectedCenters() {
		Point[] centers = new Point[FoodGenerator.getSize()];
		int i = 0;
		for (int foodKind = 0; foodKind < FoodGenerator.getSize(); foodKind++) {
			gngEngines.get(foodKind).computeExpectedDistribution();
			centers[i] = new Point((int)gngEngines.get(foodKind).getExpectedValueX(), (int)gngEngines.get(foodKind).getExpectedValueY());
			i++;
		}
		return centers;
	}
	
	public Point getExpectedCenter(int foodKind) {
		gngEngines.get(foodKind).computeExpectedDistribution();
		return new Point((int)gngEngines.get(foodKind).getExpectedValueX(), (int)gngEngines.get(foodKind).getExpectedValueY());
	}
	
	public void run() {
		while (true) {
			for (int foodKind = 0; foodKind < FoodGenerator.getSize(); foodKind++) {
				gngEngines.get(foodKind).learn();
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				
			}
		}
	}
}