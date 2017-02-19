package localisation;

import java.awt.Point;

import Objects.Direction;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.config.WheeledRobotConfiguration;
import rp.robotics.DifferentialDriveRobot;
import rp.robotics.mapping.MapUtils;

//Main class
public class Localisation {
	private Behavior junction;
	private Behavior line;
	private Arbitrator arby;
	public static ProbableLocations locs;
	public static float heading;
	public Localisation(){
		super();
		final double speed = 0.18;
		final int threshold = 42;

		WheeledRobotConfiguration OUR_BOT = new WheeledRobotConfiguration(0.056f, 0.120f, 0.230f, Motor.B, Motor.C);
		DifferentialPilot pilot = new DifferentialDriveRobot(OUR_BOT).getDifferentialPilot();
		LightSensor left = new LightSensor(SensorPort.S1);
		LightSensor right = new LightSensor(SensorPort.S3);
		OpticalDistanceSensor range = new OpticalDistanceSensor(SensorPort.S2);
		locs  = new ProbableLocations(MapUtils.createMarkingWarehouseMap());
		
		line = new LineFollow(pilot, left, right, speed, threshold);
		junction = new JunctionDetection(pilot, left, right, speed, range, threshold);
		arby = new Arbitrator(new Behavior[] {line,junction}, true);
		arby.start();	
		
	}
	public int getSize(){
		//ProbableLocations locs = ((JunctionDetection)junction).getLocs();
		return locs.size();
	}
	
	public Point getPoint(){
		//ProbableLocations locs = ((JunctionDetection)junction).getLocs();
		return new Point(locs.getPoints(0).getxCoord(), locs.getPoints(0).getyCoord());
	}
	
	public Direction getDir(){
		return ((JunctionDetection)junction).getDir();
	}
}

