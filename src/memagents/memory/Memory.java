package memagents.memory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import memagents.Simulation;
import memagents.agents.Agent;
import memagents.food.FoodGenerator;

abstract public class Memory implements IMemory {

	protected int numSamples = 5;
	
	protected int width;
	public int getWidth() { return width; }
	
	protected int height;
	public int getHeight() { return height; }
	
	protected Simulation simulation;
	
	protected Agent agent;
	
	public Memory(int width, int height, Simulation simulation, Agent agent) {
		this.width = width;
		this.height = height;
		this.simulation = simulation;
		this.numSamples = Simulation.ANSWER_SAMPLE;
		this.agent = agent;
	}
		
	public ExpectedGauss getExpectedGauss(int foodKind) { 
		HashMap<Integer, ExpectedGauss> gausses = getExpectedGausses();
		return gausses.get(foodKind);
	}
}
