import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.LinkedList;

public class BadGuyObject extends PhysicalObject {

	private PlayerObject player;
	private Point2D.Float centre;
	private LinkedList<Point2D.Float> lines = new LinkedList<Point2D.Float>(); // a list of the path taken
	private double radius;

	public BadGuyObject(float x, float y, PlayerObject p, Point2D.Float c, int r){
		super (x, y, 10);
		player = p;
		centre = c;
		radius  = r/(2.0);
		lines.add(p.getPosition());
	}


	// return the list as an array
	public Float[] pathLine (){
		Point2D.Float[] lin = new Point2D.Float[lines.size()];
		for (int i = 0; i<lines.size(); i++)
			lin[i] = lines.get(i);

		return lin;
	}


	// the badguy moves to a point, first checking other conditions
	private void moveToPoint(Point2D.Float p, Point2D.Float tv){
		float v = (float) Math.pow(super.getPosition().distance (centre)/200, 1.5);

		// if the target velocity is null it means the target is around a corner
		// so the badguy simply moves to the corner
		if (tv == null){
			this.applyForceInDirection(super.getDegreesTo (p)+90, v);
		}
		else{
			// otherwise the target is in sight
			// so compute the interception point
			double distance = this.position.distance(p);
			double velc = this.velocity.distance(tv);
			float time = (float) (distance/velc);

			// intercept point
			Point2D.Float intercept = new Point2D.Float(p.x+time*tv.x, p.y+time*tv.y);

			this.applyForceInDirection(super.getDegreesTo (intercept)+90, v);
		}
		// cap maximum velocity
		if (this.velocity.x>2.0)this.velocity.x = Math.copySign (1.8f, this.velocity.x);
		if (this.velocity.y>2.0)this.velocity.y = Math.copySign (1.8f, this.velocity.y);
	}

	// decide what point to move to, and what path to take
	public void decisionMaking(ObstacleObject obstacle, PhysicalObject target) {
		// TODO Auto-generated method stub
		try{
			LinkedList<Point2D.Float> path = new LinkedList<Point2D.Float>();
			Point2D.Float velcty = null;
			// first check if there are no obstacles
			if (obstacle == null){
				// if it is a target, set on alert and chase it
				if (target instanceof TargetObject)
					this.setActiveTexture(1);
				else
					this.setActiveTexture(0);

				// if too close to the edge move to the centre
				if (this.getPosition().distance (centre)>(radius-50.0))
					path.addFirst(centre);
				// otherwise move to the target
				else{
					path.addFirst(target.getPosition());
					velcty = target.velocity;
				}
			}
			// if there are obstacles
			else if (target instanceof TargetObject){
				// set on alert for target
				this.setActiveTexture(1);

				// if there are obstacle in the way compute path
				if (obstacle.isInPath(this.getPosition(), target.getPosition()))
					path = obstacle.getPath(this.position, target.getPosition());
				else{
					path.add(target.getPosition());
					velcty = target.velocity;
				}
			}
			// next check how far from the edge you are
			else {
				this.setActiveTexture(0);
				if (this.getPosition().distance (centre)>(radius-50.0))
					path.addFirst(centre);
				// only now free to chase player
				// if there are obstacle in the way compute path
				else if (obstacle.isInPath(this.getPosition(), player.getPosition())){
					path = obstacle.getPath(this.position, player.getPosition());
				}
				else{
					path.addFirst(player.getPosition());
					velcty = player.velocity;
				}
			}

			// move to the first point
			lines = path;
			this.moveToPoint(path.getFirst(), velcty);
		}
		catch(Exception e){}
	}


	public Point2D.Float getPosition(){
		float x = this.position.x+this.getCurrentTexture().getWidth()/2;
		float y = this.position.y+this.getCurrentTexture().getHeight()/2;

		return new Point2D.Float(x, y);
	}
}
