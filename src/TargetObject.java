import java.awt.geom.Point2D;


public class TargetObject extends PhysicalObject {
	private Point2D.Float centre;
	public boolean isbonus = false;

	public TargetObject(float x, float y, Point2D.Float c) {
		super(x, y, 10);
		centre = c;
	}

	// every timestep rolls target closer to the edge
	public void doTimeStep (){
		// its speed increases as it get further from the centre
		float v = (float) Math.pow(super.getPosition().distance (centre)/1000, 1.8);
		this.applyForceInDirection(super.getDegreesTo (centre), v);

		super.doTimeStep();
    }
}
