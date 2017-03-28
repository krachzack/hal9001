package im500.main;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class PatternMatcher {
	AdvancedRobot robot;
	String enemyName;
	
	/**
	 * The log is ordered from the youngest to the oldest record
	 */
	static LinkedList<Record> log = new LinkedList<Record>();
	
	static final int MATCH_LENGTH = 7;
	static final double THRESHOLD = 0.01;
	static final double MATCH_THRESHOLD = 1;
	static final int MAX_LOG_SIZE = 200;

	public double predictedHeadingDelta;
	public double predictedVelocity;
	
	public double enemyHeading = 0;
	public double enemyVelocity = 0;
	
	double enemyX;
	double enemyY;
	
	public PatternMatcher(){
		System.out.println("Log size: " + log.size());
	}
	
	
	public void setRobot(AdvancedRobot robot){
		this.robot = robot;
	}
	
	public void onScanned(ScannedRobotEvent event) {
		enemyName = event.getName();
		
		// create record
		log.addFirst(new Record(event.getHeadingRadians() - enemyHeading, event.getVelocity()));
		enemyHeading = event.getHeadingRadians();
		enemyVelocity = event.getVelocity();
		
		if(log.size() > MAX_LOG_SIZE){
			log.removeLast();
		}
		
		/*
		double futureHeading = event.getHeadingRadians();
		
		// calculate position
		double selfX = robot.getX();
		double selfY = robot.getY();
		double toEnemyAngle = Angle.roboToMath(Utils.normalAbsoluteAngle(robot.getHeadingRadians() + event.getBearingRadians()));
		double toEnemyDistance = event.getDistance();
		enemyX = selfX + Math.cos(toEnemyAngle) * toEnemyDistance;
		enemyY = selfY + Math.sin(toEnemyAngle) * toEnemyDistance;
		
		robot.getGraphics().setColor(Color.RED);
		robot.getGraphics().drawOval((int)enemyX - 10, (int)enemyY - 10, 20, 20);
		
		
		List<Record> predictions = getPredictions(30);
		if(predictions != null){
			// draw predictions
			for(Record r : predictions){
				futureHeading += r.getHeadingDelta();
				enemyX += Math.cos(Angle.roboToMath(futureHeading))*r.velocity;
				enemyY += Math.sin(Angle.roboToMath(futureHeading))*r.velocity;
				robot.getGraphics().setColor(Color.RED);
				robot.getGraphics().drawOval((int)enemyX - 10, (int)enemyY - 10, 20, 20);
				System.out.println(enemyX + " " + enemyY);
			}
		}
		*/
	}
	
	/**
	 * returns future position in x ticks. null if no prediction
	 * @param inTicks
	 * @param ePos enemy position
	 * * @param eHeading enemy heading
	 * @return
	 */
	public double[] getFuturePosition(int inTicks, double[] ePos, double eHeading){
		List<Record> predictions = getPredictions(inTicks);
		ePos = Arrays.copyOf(ePos, ePos.length);
		if(predictions != null){
			int i = 0;
			for(Record r : predictions){
				eHeading += r.getHeadingDelta();
				ePos[0] += Math.cos(Angle.roboToMath(eHeading))*r.velocity;
				ePos[1] += Math.sin(Angle.roboToMath(eHeading))*r.velocity;
				
				robot.getGraphics().setColor(Color.RED);
				//float size = (i+1)/(inTicks+1) * 20;
				float size = (float) Math.pow(0.92,inTicks-i) * 20;
				robot.getGraphics().drawOval((int)(ePos[0] - size/2.0), (int)(ePos[1] - size/2.0), (int)size, (int)size);
				i++;
			}
			robot.getGraphics().setColor(Color.RED);
			robot.getGraphics().drawOval((int)ePos[0] - 10, (int)ePos[1] - 10, 20, 20);
			
			return ePos;
		}
		return null;
	}
	
	/**
	 * Returns x future record predictions. Ordered from earliest to latest.
	 * Returns null if no good match is found.
	 * @param futureRecords
	 * @param 
	 * @return
	 */
	public List<Record> getPredictions(int futureRecords){
		int bestMatchIndex = -1;
		double bestMatchDistSum = Double.MAX_VALUE;
		// iterates through previous records starting from 7th tick (MATCH_LENGTH) in the past
		for(int i = MATCH_LENGTH; i < log.size()-MATCH_LENGTH; i++){
			double squaredDistSum = 0;
			for(int j = 0; j < MATCH_LENGTH; j++){
				Record r = log.get(j+i);
				Record r2 = log.get(j);
				squaredDistSum += (r2.getHeadingDelta() - r.getHeadingDelta())*(r2.getHeadingDelta() - r.getHeadingDelta()) + (r2.getVelocity() - r.getVelocity())*(r2.getVelocity() - r.getVelocity());
				if(squaredDistSum < bestMatchDistSum){
					bestMatchDistSum = squaredDistSum;
					bestMatchIndex = i;
				}
			}
		}
		
		//System.out.println(bestMatchDistSum);
		if(bestMatchDistSum < MATCH_THRESHOLD && bestMatchIndex != -1 && bestMatchIndex+futureRecords < log.size()){
			List<Record> predictions = log.subList(bestMatchIndex, bestMatchIndex+futureRecords);

			return predictions;
		}
		
		return null;
	}

	class Record{
        private double headingDelta;
        private double velocity;
        
        public Record(double headingDelta, double velocity) {
	       this.headingDelta = headingDelta;
	       this.velocity = velocity;
        }
        
        public double getHeadingDelta(){
        	return headingDelta;
        }

        public double getVelocity(){
        	return velocity;
        }
	}
}
