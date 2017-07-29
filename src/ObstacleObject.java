import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.LinkedList;

// object representing an obstalce in the game
public class ObstacleObject extends PhysicalObject {
	Point2D.Float[] edges = new Point2D.Float[4];
	
	// weighted graph
	Graph edge; 
	
	public ObstacleObject(float x, float y) {
		super(x, y, 10);
		// TODO Auto-generated constructor stub
	}
	
	// do nothing when collided with
	@Override
	public void setVelocity(float x, float y){
		
	}
	
	
	// return true if the line insersects with the boundingbox of the obstacle
	public boolean isInPath(Point2D.Float start, Point2D.Float end){
		return (this.getAABoundingBox().intersectsLine(start.x, start.y, end.x, end.y));
	}
	
	
	// get the edges around this obstacle
	// and add to the weighted graph
	private void getEdges(){
		edge = new Graph();
		
		edges[0] = new Point2D.Float(this.position.x-this.getCurrentTexture().getWidth()/2-20,this.position.y-this.getCurrentTexture().getHeight()/2-20);
		edges[1] = new Point2D.Float(edges[0].x+this.getCurrentTexture().getWidth()+40, edges[0].y);
		edges[2] = new Point2D.Float(edges[1].x, edges[0].y+this.getCurrentTexture().getHeight()+40);
		edges[3] = new Point2D.Float(edges[0].x, edges[2].y);
		
		
		edge.addEdge(edges[0], edges[1]);
		edge.addEdge(edges[1], edges[0]);
		edge.addEdge(edges[1], edges[2]);
		edge.addEdge(edges[2], edges[1]);
		edge.addEdge(edges[2], edges[3]);
		edge.addEdge(edges[3], edges[2]);
		edge.addEdge(edges[3], edges[0]);
		edge.addEdge(edges[0], edges[3]);
	}


	// find the two points that are closest to the badguy
	// and add to the graph
	private void closestEdgesForBadguy(Float start) {
		for (Point2D.Float p: edges){
			if (!this.isInPath(start, p)){
    			edge.addEdge(start, p);
    		}
		}		
	}

	// find the two points that are closest to the target
	// and add to the graph
	private void closestEdgesForTarget(Float end) {
		for (Point2D.Float p: edges){
			if (!this.isInPath(end, p)){
    			edge.addEdge(p, end);
    		}
		}	
	}
	
	
	// get the shortest path between the two points
	public  LinkedList<Point2D.Float> getPath (Float start, Float end){
		this.getEdges();						// add edges around obstacle
		this.closestEdgesForBadguy (start);		// add startpoint
		this.closestEdgesForTarget (end);		// add endpoint

		LinkedList<Point2D.Float> path = this.edge.processRequest(start, end); // get list of points

		this.edge.clearAll();
		
		return path;
	}
}
