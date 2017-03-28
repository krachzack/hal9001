package im500.main;

public final class Angle {
	public static final double roboToMath(double roboAngleRadians) {
		return 0.5*Math.PI - roboAngleRadians;
	}
	
	public static final double mathToRobo(double mathAngleRadians) {
		return 0.5*Math.PI - mathAngleRadians; 
	}
}
