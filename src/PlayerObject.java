

import java.awt.geom.Point2D;

class PlayerObject extends PhysicalObject {
    
    int numberOfDirectionTextures = 72;
    float direction;
    private Point2D.Float mousePos;
    
    Point2D.Float oldPosition;
    
    public boolean brakes;
    
	//==================================================================================================
    public PlayerObject (Point2D.Float c, Point2D.Float m) {
        super (c.x, c.y+200, 10);
        mousePos = m;
        brakes = false;
        oldPosition = c;
    }
    
    
    public void revertPosition () {
    	this.setPosition(oldPosition);
    }
    
    public float getDirection() {
		return direction;
	}


    // move player in direction of mouse
	public void moveInDirection(float direction) {
		// do not move if brakes are set
		if (!brakes){
		oldPosition = this.getPosition();
		
		int xdir = 1;
		int ydir = 1;
		
		if (this.getPosition().x<mousePos.x) xdir = -1;
		if (this.getPosition().y<mousePos.y) ydir = -1;

		// increment position with distance that mouse pointer is from the player
		double centrex = xdir*Math.sqrt(this.getPosition().distance(mousePos)/180);;
		double centrey = ydir*Math.sqrt(this.getPosition().distance(mousePos)/180);;
		
		
		float xinc = (float)(Math.sin(Math.toRadians(direction))*2 - centrex);
		float yinc = (float)(Math.cos(Math.toRadians(direction))*(-2) - centrey);
		
		incrementPosition (xinc, yinc);
		}
		else
			this.applyFriction(0);
	}
	
	public String toString(){
		return (this.getPosition().toString());
	}
}
