package localisation;

import Objects.Direction;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;

/**
 * Behaviour for detecting junctions
 */
public class JunctionDetection implements Behavior {
	
	private LightSensor left;
	private LightSensor right;
	private int leftValue;
	private int rightValue;
	private DifferentialPilot pilot;
	//Light value threshold
	private int threshold;
	//Moves
	private int NORTH = 0;
	private int WEST = 1;
	private int SOUTH = 2;
	private int EAST = 3;
	private int robotDirection = NORTH;
	private float NORTH_DISTANCE;
	private float SOUTH_DISTANCE;
	private float WEST_DISTANCE;
	private float EAST_DISTANCE;
	private float DISTANCE;
	private boolean firstTime=false;
	private float heading;
	
	//private ArrayList<PosProb> Localisation.locsArray;
	private float cellSize = Localisation.locs.getCellSize();
	
	private OpticalDistanceSensor ranger;
	
	public JunctionDetection(DifferentialPilot pilot, LightSensor left, LightSensor right, double speed, OpticalDistanceSensor distanceSensor, int threshold){
		this.left=left;
		this.right=right;
		this.pilot=pilot;
		this.ranger=distanceSensor;
		this.threshold = threshold;
		//this.Localisation.locsArray = Localisation.locsArray;
		//Localisation.locs.setArray(this.Localisation.locsArray);
		pilot.setTravelSpeed(speed);
	}
	/**	
	 * Generates calibrated values
	 * On a black line = 45
	 * Not on a black line = 35			
	 */	
	
	@Override
	public boolean takeControl(){
		
		generateLightValues();
		if((leftValue < threshold) && (rightValue < threshold) && Localisation.locs.size()>1){
			return true;
		}
		
		return false;
	}
	

	public void action() {
		pilot.travel(0.1f);
		//LCD.drawString(ranger.getRange() + " " + Localisation.locs.getCellSize() , 0, 6);
		if(!firstTime || ranger.getRange()<Localisation.locs.getCellSize()){
			
			for(int i=0;i<4;i++){
				
				//LCD.clear();
				//LCD.drawString(+ Localisation.locs.size() + "      " + ranger.getRange(), 0, 7);
				if(checkEnd(Localisation.locs.size()))
					return;
				executeMove(RotationDirections.LEFT);
				Delay.msDelay(1000);
//				if(Localisation.locs.size() < 7){
//							
			}for(int i1 = 0; i1 < Localisation.locs.size(); i1++){
//						//LCD.drawString(Localisation.locs.getPoints(i1).getxCoord() + " " + Localisation.locs.getPoints(i1).getyCoord(), 0, i1+1);
//					}
//				}
//				Delay.msDelay(3000);
				
			}
			//pilot.stop();
			
			firstTime = true;
		}
		
		if(checkEnd(Localisation.locs.size()))
			return;
		
		if(ranger.getRange()<Localisation.locs.getCellSize()){
			//LCD.drawString("I am in with: " + ranger.getRange() , 0, 0);
			if(heading == 90){
				turnDecision(WEST_DISTANCE, EAST_DISTANCE);
					
			}else if(heading == 180){
				turnDecision(NORTH_DISTANCE, SOUTH_DISTANCE);
				
			}else if(heading == -90){
				turnDecision(EAST_DISTANCE, WEST_DISTANCE);
				
			}else if(heading == 0){
				turnDecision(SOUTH_DISTANCE, NORTH_DISTANCE);
			}
			 
		}else{
			executeMove(RotationDirections.FORWARD);
			Localisation.locs.updateLocations(heading);
		}
		
		if(checkEnd(Localisation.locs.size()))
			return;
		LCD.clear();
		LCD.drawString("" + Localisation.locs.size(), 0, 7);
		Delay.msDelay(1000);
	}

	@Override
	public void suppress() {
		
	}
	
	//Turns left
		private void moveLeft(){
			pilot.rotate(126);
		}
		//Turns right
		private void moveRight(){
			pilot.rotate(-126);			
		}
		//Continues going forward
		private void moveForward(){
			pilot.forward();
		}
		
	
	private void generateLightValues(){
		leftValue = left.getLightValue();
		rightValue = right.getLightValue();
	}
	
	private void executeMove(int move){
		setDirections();
		//LCD.drawString(DISTANCE + " ", 0, testCount);
		Localisation.locs.setLocations(heading, DISTANCE);
		if(move==RotationDirections.LEFT){
			
			moveLeft();
			if(robotDirection==EAST)
				robotDirection=NORTH;
			else			
				robotDirection+=1;
		}
		else if(move==RotationDirections.RIGHT){
			moveRight();
			if(robotDirection == NORTH)
				robotDirection = EAST;
			else
				robotDirection-=1;
		}
		else
			moveForward();
		
		setDirections();
		
	}
	
	private void turnDecision(float leftPosition, float rightPosition){
		if(leftPosition > cellSize && rightPosition > cellSize){ 
			if(leftPosition < rightPosition){
				executeMove(RotationDirections.LEFT);
				Localisation.locs.updateLocations(heading);
			}else{
				executeMove(RotationDirections.RIGHT);
				Localisation.locs.updateLocations(heading);
			}
		}else if(leftPosition > cellSize){
			executeMove(RotationDirections.LEFT);
			Localisation.locs.updateLocations(heading);
		}else if(rightPosition > cellSize){
			executeMove(RotationDirections.RIGHT);
			Localisation.locs.updateLocations(heading);
		}else{
			executeMove(RotationDirections.LEFT);
			executeMove(RotationDirections.LEFT);
			Localisation.locs.updateLocations(heading);
		}
		//LCD.clear();
		//LCD.drawString("h: " + heading, 0, 0);
	}
	
	private void setDirections(){
		if(robotDirection==NORTH){
			heading = 90;
			NORTH_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
		else if(robotDirection==EAST){
			heading = 0;
			EAST_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
		else if(robotDirection==SOUTH){
			heading = -90;
			SOUTH_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
		else if(robotDirection==WEST){
			heading = 180;
			WEST_DISTANCE=ranger.getRange();
			DISTANCE=ranger.getRange();
		}
	}
	
	private boolean checkEnd(int size){
		//LCD.drawString(Localisation.locs.getPoints(0).getxCoord() + " " + Localisation.locs.getPoints(0).getyCoord(), 0, 4);
		return size <= 1;
	}
	
	public ProbableLocations getlocs(){
		return Localisation.locs;
	}
	
	public Direction getDir(){
		if(robotDirection==NORTH)
			return Direction.NORTH;
		else if(robotDirection==EAST)
			return Direction.EAST;
		else if(robotDirection==SOUTH)
			return Direction.SOUTH;
		else
			return Direction.WEST;
	}
}

