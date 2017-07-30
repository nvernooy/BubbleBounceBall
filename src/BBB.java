import sun.audio.*;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Vector;
import GameEngine.Game;
import GameEngine.GameFont;
import GameEngine.GameObject;
import GameEngine.GameTexture;


// BubbleBounceBall game
public class BBB extends Game
{
	// Offset of the screen
	private Point2D.Float offset = new Point2D.Float(0,0);
	private Point2D.Float centre;	// centre of the game

	private Counter counter;		// object to hold counters
	private Highscore highscore;	// object to calculate highscore

	// A Collection of GameObjects in the world that will be used with the collision detection system
	private Vector<GameObject> objects = new Vector<GameObject>();

	// 2D grid of wolrd used for checking if obstacles are in the way
	private ObstacleObject[][] grid;

	// GameTextures
	private GameTexture worldtexture;
	private GameTexture targetTexture;
	private GameTexture badguyTexture;
	private GameTexture bonusTexture;
	private GameTexture bulletTexture;
	private GameTexture rockTexture;
	private GameTexture wallTexture;
	private GameTexture boomTexture;
	private GameTexture alertTexture;


	// The cooldown of the gun (set this to 0 for a cool effect :> )
	private int cooldown = 10;
	private int cooldownTimer = 1;

	// GameObjects
	private GameObject world; 		// the world
	private PlayerObject player; 	// the player
	private TargetObject target; 	// the target


	//GameFonts that will be used
	private GameFont score, serif, noOfBullets, finalmsg;

	// The position of the mouse
	private Point2D.Float mousePos = new Point2D.Float (0,0);

	// Information for the random line at the bottom of the screen
	Point2D.Float [] linePositions = {new Point2D.Float(0,0), new Point2D.Float(100,100)};
	float [][] lineColours = {{1.0f,1.0f,1.0f,1.0f},{1.0f,0.0f,0.0f,1.0f}};

	//==================================================================================================

	public BBB (int GFPS) {
		super(GFPS);
	}

	//==================================================================================================

	public void initStep(ResourceLoader loader) {
		//Loading up some fonts
		score = loader.loadFont(new Font("Courier", Font.PLAIN, 30) );
		serif = loader.loadFont(new Font("Courier", Font.PLAIN, 30) );
		noOfBullets = loader.loadFont(new Font("Courier", Font.PLAIN, 30) );
		finalmsg = loader.loadFont(new Font("Courier", Font.PLAIN, 270) );

		//Loading up our textures
		targetTexture = loader.loadTexture("Textures/bal1.png");
		badguyTexture = loader.loadTexture("Textures/badguy.png");
		bonusTexture = loader.load("Textures/bonus.png");
		bulletTexture = loader.loadTexture("Textures/bullet.png");
		worldtexture = loader.load("Textures/world.png");
		wallTexture = loader.load ("Textures/longwall.png");
		rockTexture = loader.load ("Textures/rock.png");
		boomTexture = loader.load ("Textures/boom.png");
		alertTexture = loader.load ("Textures/alert.png");

		counter = new Counter();
		centre = new Point2D.Float(worldtexture.getWidth()/2, worldtexture.getHeight()/2);

		world = new GameObject (0,0);
		world.addTexture(worldtexture,0,0);

		// Creating the player's ship
		player = new PlayerObject(centre, mousePos);
		player.addTexture(loader.load("Textures/player.png"), 16, 16);

		// creating fixed obstacles
		grid = new ObstacleObject[worldtexture.getWidth()/10][worldtexture.getHeight()/10];
		newObstacles();


		objects.add(player);
		objects.add(newTarget());
		objects.add(newBadGuy());
		counter.noOfBadguys++;
	}


	// create some obstacles in the world, only a certian number at fixed points
	private void newObstacles() {
		ObstacleObject ob1 = new ObstacleObject(centre.x+180, centre.y+100);
		ob1.addTexture(wallTexture);
		addObstacleToGrid(ob1);				// adding the obstacle to the grid used in pathfinding
		objects.add(ob1);


		ObstacleObject ob2 = new ObstacleObject(centre.x+200, centre.y-200);
		ob2.addTexture(rockTexture);
		addObstacleToGrid(ob2);
		objects.add(ob2);

		ObstacleObject ob3 = new ObstacleObject(centre.x-180, centre.y+200);
		ob3.addTexture(wallTexture);
		addObstacleToGrid(ob3);
		objects.add(ob3);

		ObstacleObject ob4 = new ObstacleObject(centre.x-190, centre.y-180);
		ob4.addTexture(wallTexture);
		addObstacleToGrid(ob4);
		objects.add(ob4);

		ObstacleObject ob5 = new ObstacleObject(centre.x-210, centre.y);
		ob5.addTexture(rockTexture);
		addObstacleToGrid(ob5);
		objects.add(ob5);

	}

	// add pionts in the grid where objects are
	private void addObstacleToGrid(ObstacleObject ob1) {
		int x = (int) ((ob1.getPosition().x-(ob1.getCurrentTexture().getWidth()/2))/10+0.5);
		int y = (int) ((ob1.getPosition().y-(ob1.getCurrentTexture().getHeight()/2))/10+0.5);

		int wid = ob1.getCurrentTexture().getWidth()/10;
		int height = ob1.getCurrentTexture().getHeight()/10;

		// for every point where the object is, add the object to the grid
		for (int i = 0; i<=wid; i++)
			for (int j = 0; j<=height; j++)
				grid[x+i][y+j] = ob1;
	}

	// method to play the sound clips in a specified URL
	public synchronized void playSound(final URL soundurl) {
		try {
			AudioStream clip = new AudioStream(soundurl.openStream());
			AudioPlayer.player.start(clip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// this method is used to fire a bullet
	public void fireBullet() throws Exception {
		// play the sound when a bullet is fired
		URL bulleturl =  BBB.class.getResource("beep2.wav");
		playSound(bulleturl);

		cooldownTimer = cooldown;

		float dir = 90+player.getDegreesTo(mousePos);
		BulletObject bullet =
			new BulletObject(
					player.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, player.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300, bulletTexture);

		bullet.applyForceInDirection(dir, 10f);

		objects.add(bullet);

		counter.shoot();
	}


	private void handleControls(GameInputInterface gii) throws Exception {

		//----------------------------------

		// This isn't so great, there are better and neater ways to do this, you are encouraged to implement a better one

		// right stops the player moving, left continues it
		if (gii.keyDown(KeyEvent.VK_RIGHT))	player.brakes = true;
		else if (gii.keyDown(KeyEvent.VK_LEFT))  player.brakes = false;


		// pause when spacebar is pressed pause game
		if (gii.keyDown(KeyEvent.VK_SPACE)) {
			counter.setPaused();
			System.out.println (counter.paused);
		}

		// when enter is pressed play game
		if (gii.keyDown(KeyEvent.VK_ENTER)) {
			counter.setPlay();

		}

		// quit if escape is pressed
		if (gii.keyDown(KeyEvent.VK_ESCAPE)){
			this.endGame();
		}


		if (cooldownTimer <= 0) {
			if(gii.mouseButtonDown(MouseEvent.BUTTON1) && counter.bullets>0) fireBullet(); // fire only if there are enought bullets left
		}

		cooldownTimer --;
	}

	//==================================================================================================

	public void logicStep(GameInputInterface gii) {
		try{
			// some examples of the mouse interface
			mousePos.x = (float)gii.mouseXScreenPosition() - offset.x;
			mousePos.y = (float)gii.mouseYScreenPosition() - offset.y;

			handleControls(gii);

			// continue logicstep only if still playing
			if (counter.play()){
				// if the player goes outside the bounds, he dies and the highscore is calculated
				if (centre.distance(player.getPosition())> (worldtexture.getWidth()-player.getCurrentTexture().getHeight())/2) {
					counter.alive = false;
					highscore = new Highscore (counter.score);
				}

				player.moveInDirection(90+player.getDegreesTo(mousePos));


				// if the score is 10 set first bonus ball
				if (counter.score == 10) counter.setBonus();

				// generate badguys randomly
				if (counter.generateBadGuy()) objects.add(newBadGuy());


				// NOTE: you must call doTimeStep for ALL game objects once per frame!
				// updating step for each object
				for (int i = 0 ; i < objects.size() ; i++) {
					// if the objects is a badguy decide what it should do now
					if (objects.elementAt(i) instanceof BadGuyObject)
						decisionMaking(objects.elementAt(i));

					objects.elementAt(i).doTimeStep();
					Point2D.Float objposition = new Point2D.Float(objects.elementAt(i).getPosition().x+(objects.elementAt(i).getCurrentTexture().getWidth())/2,objects.elementAt(i).getPosition().y+(objects.elementAt(i).getCurrentTexture().getHeight())/2);
					// destroy those outside gameworld
					if ((centre.distance(objposition)> (worldtexture.getWidth()-objects.elementAt(i).getCurrentTexture().getHeight())/2) && !(objects.elementAt(i) instanceof PlayerObject)) {
						objects.elementAt(i).setMarkedForDestruction(true);
					}


				}

				// setting the camera offset
				offset.x = -player.getPosition().x + (this.getViewportDimension().width/2);
				offset.y = -player.getPosition().y + (this.getViewportDimension().height/2);

				// check for collisions between objects
				collisions();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	//==================================================================================================


	public void renderStep(GameDrawer drawer) {
		try{
			//For every object that you want to be rendered, you must call the draw function with it as a parameter
			// NOTE: Always draw transparent objects last!
			// Offsetting the world so that all objects are drawn
			drawer.setWorldOffset(offset.x, offset.y);
			drawer.setColour(1.0f, 1.0f, 1.0f, 1.0f);

			//drawing world only
			drawer.draw(world, -1);


			// drawing all the objects in the game
			for (GameObject o: objects) {
				drawer.draw(o, 1.0f, 1.0f, 1.0f, 1.0f, 0);

				// if it is a badguy  object draw the pathlines
				if (o instanceof BadGuyObject){
					BadGuyObject temp = (BadGuyObject) o;
					Float[] path = temp.pathLine();			//get array of path points

					// test for each length of path and draw the lines
					if (path.length == 1){
						Float[] path1 = {temp.getPosition(), path[0]};
						drawer.draw(GameDrawer.LINES, path1, lineColours[0], 0.9f);

					}
					else if (path.length == 2){
						Float[] path1 = {temp.getPosition(), path[0]};
						drawer.draw(GameDrawer.LINES, path1, lineColours[0], 0.9f);

						Float[] path2 = {path[0], path[1]};
						drawer.draw(GameDrawer.LINES, path2, lineColours[0], 0.9f);

					}
					else if (path.length == 3){
						Float[] path1 = {temp.getPosition(), path[0]};
						drawer.draw(GameDrawer.LINES, path1, lineColours[0], 0.9f);

						Float[] path2 = {path[0], path[1]};
						drawer.draw(GameDrawer.LINES, path2, lineColours[0], 0.9f);

						Float[] path3 = {path[1], path[2]};
						drawer.draw(GameDrawer.LINES, path3, lineColours[0], 0.9f);
					}
				}

			}


			if (player != null) {
				Point2D.Float [] playerLines = {mousePos, player.getPosition()};
				drawer.draw(GameDrawer.LINES, playerLines, lineColours, 0.5f);
			}

			drawer.setColour(1.0f,1.0f,1.0f,1.0f);

			// Changing the offset to 0 so that drawn objects won't move with the camera
			drawer.setWorldOffset(0, 0);


			// draw these messages depending on wether the player is still alive
			if (player!=null&&counter.alive) {
				drawer.draw(score, ""+player.getDirection(), new Point2D.Float(20,100), 0f, 0f, 0f, 1f, 0.1f);
				drawer.draw(serif, ""+mousePos.x +":"+mousePos.y, new Point2D.Float(20,20), 0f, 0f, 0f, 1f, 0.7f, 0.1f);
			}
			else{
				drawer.draw(score, "YOURE DEAD", new Point2D.Float(20,100), 250f, 0f, 0f, 1f, 0.1f);
				drawer.draw(serif, "Press esc to quit", new Point2D.Float(20,20), 0f, 0f, 0f, 1f, 0.7f, 0.1f);

				int high = highscore.getHighScore();

				if (counter.score>high)
					drawer.draw(finalmsg, "CONGATULATIONS: YOU MADE A NEW HIGH SCORE!!", new Point2D.Float(50,400), 250f, 0f, 0f, 1f, 1f, 0.1f);
				else
					drawer.draw(finalmsg, "The high score is "+high+".You didnt make it", new Point2D.Float(50,400), 250f, 0f, 0f, 1f, 1f, 0.1f);
			}
			drawer.draw(score, "SCORE: "+counter.score, new Point2D.Float(20,60), 0f, 0f, 0f, 1f, 0.1f);
			drawer.draw(noOfBullets, "No of bullets: "+counter.bullets, new Point2D.Float(20,130), 0f, 0f, 0f, 1f, 0.1f);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}


	public boolean fine_tune_collision(GameObject o1, GameObject o2) {

		ByteBuffer bb1 = o1.getCurrentTexture().getByteBuffer();
		ByteBuffer bb2 = o2.getCurrentTexture().getByteBuffer();
		int w1 = o1.getCurrentTexture().getWidth();
		int w2 = o2.getCurrentTexture().getWidth();
		int h1 = o1.getCurrentTexture().getHeight();
		int h2 = o2.getCurrentTexture().getHeight();
		Rectangle r1 = o1.getIntAABoundingBox();
		Rectangle r2 = o2.getIntAABoundingBox();
		Point subimageoffset1 = o1.getSubImageOffset();
		Point subimageoffset2 = o2.getSubImageOffset();

		Rectangle inter = r2.intersection(r1);
		boolean collided = false;

		int r1bx = subimageoffset1.x + inter.x - r1.x;
		int r1by = subimageoffset1.y + inter.y - r1.y;
		int r2bx = subimageoffset2.x + inter.x - r2.x;
		int r2by = subimageoffset2.y + inter.y - r2.y;

		outer :
			for (int i = 0 ; i < inter.height ; i++) {
				for (int j = 0 ; j < inter.width ; j++) {
					int b1 = 0;
					b1 = (getAlphaAt(bb1, w1, h1, r1bx+j, r1by+i) == 0 ? 0 : 1);
					int b2 = 0;
					b2 = (getAlphaAt(bb2, w2, h2, r2bx+j, r2by+i) == 0 ? 0 : 1);

					if (b1 == 1 && b2 == 1) {
						collided = true;
						break outer;
					}
				}
			}
		return collided;
	}

	public byte getAlphaAt(ByteBuffer b, int w, int h, int x, int y) {
		int index = (4*(x+((h-y)*w)))+3;
		return (index < w*h*4 ? b.get(index) : 0);
	}


	// make new object target and position here at a random position
	private GameObject newTarget (){

		float x = centre.x + (float) ((Math.random()*worldtexture.getWidth()) - worldtexture.getWidth()/2);
		float y = centre.y + (float) ((Math.random()*worldtexture.getHeight()) - worldtexture.getHeight()/2);

		// create target at random position
		target = new TargetObject (x,y, centre);
		// load texture depending on if it should be bonus or not
		if (counter.bonus){
			target.addTexture(bonusTexture);
			target.isbonus = true;
		}
		else {
			target.addTexture(targetTexture);
			target.isbonus = false;
		}
		target.addTexture(boomTexture);

		return target;
	}


	// make new object badguy and position at random position same as target
	private GameObject newBadGuy (){
		float x = centre.x + (float) ((Math.random()*worldtexture.getWidth()) - worldtexture.getWidth()/2-50);
		float y = centre.y + (float) ((Math.random()*worldtexture.getHeight()) - worldtexture.getHeight()/2-50);

		GameObject badguy = new BadGuyObject (x,y, player, centre, worldtexture.getWidth());
		badguy.addTexture(badguyTexture, 0, 0);
		badguy.addTexture(alertTexture); 			// adding another texture that is displayed when it is chasing the bonus balls
		counter.noOfBadguys++;
		return badguy;
	}



	// check for collisions between objects*/
	// naive checks are done here before the actual pixel level testing in the collsionDetection method
	private void collisions () throws Exception{

		//checking each unit against each other unit for collisions
		for (int i = 0 ; i < objects.size() ; i++) {
			// edit: check if the object has already been destroyed before running it through the loop
			if (!(objects.elementAt(i).isMarkedForDestruction())){
				for (int j = i+1 ; j < objects.size() ; j++) {
					// same here
					if (!(objects.elementAt(j).isMarkedForDestruction())){
						GameObject o1 = objects.elementAt(i);
						GameObject o2 = objects.elementAt(j);

						// check first if the other object is in a 200 radius
						// it is not neccesary to check objects that are too far away
						if (o1.getPosition().distance(o2.getPosition())<200){
							if (fine_tune_collision(o1, o2)) {
								collidedWithEachOther (o1, o2);
							}
						}
					}
				}
			}
		}

		// destroying units that need to be destroyed
		for (int i = 0 ; i < objects.size() ; i++) {
			if (objects.elementAt(i).isMarkedForDestruction()) {
				// removing object from list of GameObjects
				if (objects.elementAt(i) instanceof TargetObject) objects.add(newTarget()); // if target was destroyed make new one
				else if (objects.elementAt(i) instanceof BadGuyObject) {
					counter.noOfBadguys--; 	// if badguy, decrease counter
					counter.noOfBadguys--;
				}
				objects.remove(i);
				i--;
			}
		}
	}


	// collisions between all the objects
	private void collidedWithEachOther (GameObject o1, GameObject o2){

		// collision between bullet and object
		if (o1 instanceof BulletObject || o2 instanceof BulletObject){
			// both bullet and whatever else destroyed
			o1.setMarkedForDestruction(true);
			o2.setMarkedForDestruction(true);
			// if its a wall dont destroy
			if (o1 instanceof ObstacleObject )o1.setMarkedForDestruction(false);
			else if (o2 instanceof ObstacleObject)o2.setMarkedForDestruction(false);
			else if (o1 instanceof TargetObject || o2 instanceof TargetObject) collideWithTarget(o1,o2);
			else if (o1 instanceof BadGuyObject || o2 instanceof BadGuyObject) counter.score++; // one point for shooting badguys
		}
		// collision between player and object
		else if (o1 instanceof TargetObject || o2 instanceof TargetObject) collideWithTarget(o1, o2);
		// collision between player and badguy (they just bounce of each other)
		else bounce (o1,o2);


	}


	// collisions between an object and a ball
	private void collideWithTarget(GameObject o1, GameObject o2) {

		if (o1 instanceof ObstacleObject || o2 instanceof ObstacleObject){
			target.applyFriction(0);
			if (o1.getPosition().distance(o2.getPosition())<25)
				target.setMarkedForDestruction(true);
		}
		else{
			target.setActiveTexture(1);
			// sound when target collided
			URL crashurl =BBB.class.getResource("crash.wav");
			playSound(crashurl);

			if (o1 instanceof TargetObject){
				o1.setMarkedForDestruction(true);
				if (o2 instanceof PlayerObject||o2 instanceof BulletObject){
					counter.score +=2;		// 2 points for getting target
					if (((TargetObject) o1).isbonus) {
						counter.resetBonus();// reset bonus counter - increase bullets
						counter.score++;	// another piont for shooting bonus
					}
				}
			}
			else{
				o2.setMarkedForDestruction(true);
				if (o1 instanceof PlayerObject || o1 instanceof BulletObject){
					counter.score +=2;
					if (((TargetObject) o2).isbonus){
						counter.resetBonus();
						counter.score++;
					}
				}
			}
		}
	}

	// objects bounce of each other
	private void bounce(GameObject o1, GameObject o2) {
		PhysicalObject temp1 = (PhysicalObject) o1;
		PhysicalObject temp2 = (PhysicalObject) o2;


		if (o1 instanceof ObstacleObject|| o2 instanceof ObstacleObject){
			if (o1 instanceof PlayerObject){
				player.revertPosition();
			}
			else if (o2 instanceof PlayerObject){
				player.revertPosition();
			}
			else if(o2 instanceof BadGuyObject){
				temp2.setVelocity (-(temp2.getVelocity().x)*1.3f, -(temp2.getVelocity().y)*1.3f);
			}
			else {
				temp1.setVelocity (-(temp1.getVelocity().x)*1.3f, -(temp1.getVelocity().y)*1.3f);
			}
		}// move player in opposite direction
		else{
			if (o1 instanceof PlayerObject)player.revertPosition();//	((PlayerObject) o1).moveInDirection (-(((PlayerObject) o1).getDirection()));
			else {
				// bounce badguy in opposite direction plus velocity of other
				//temp1.setMarkedForDestruction(true);
				temp1.setVelocity (-(temp1.getVelocity().x)+(temp2.getVelocity().x)/2, -(temp1.getVelocity().y)+(temp2.getVelocity().y)/2);
			}
			if (o2 instanceof PlayerObject)	player.revertPosition();//((PlayerObject) o2).moveInDirection (-(((PlayerObject) o2).getDirection()));
			else {
				//temp2.setMarkedForDestruction(true);
				temp2.setVelocity (-(temp2.getVelocity().x)+(temp1.getVelocity().x)/2, -(temp2.getVelocity().y)+(temp1.getVelocity().y)/2);
			}

			// if the badguys merged/ spawned on top of each other, destroy
			if (o1 instanceof BadGuyObject && o2 instanceof BadGuyObject){
				if (o1.getPosition().distance(o2.getPosition())<(badguyTexture.getHeight()-5))
					o1.setMarkedForDestruction(true);
			}
		}

		// play sound
		try {
			URL boinkurl =  BBB.class.getResource("boink3.wav");
			playSound(boinkurl);
		}
		catch(Exception e){counter.noOfBadguys = 100;}
	}



	// the badguys decide here what to do
	private void decisionMaking(GameObject o){
		BadGuyObject badguy = (BadGuyObject)o;

		// chasing a bonus ball is highest priority
		if (target.isbonus){
			ObstacleObject obs = isObstacleInArea(badguy, target); 	// check if there is a obstacle in the general area
			badguy.decisionMaking(obs, target);						// finalise decisions and paths in object method
		}
		// else chase the player
		else {
			ObstacleObject obs = isObstacleInArea(badguy, player);
			badguy.decisionMaking(obs, player);
		}
	}


	// check if there are obstacles in the area given by the line between the two objects
	private ObstacleObject isObstacleInArea(BadGuyObject badguy, GameObject t) {
		// starting position, the lowest point
		int x = (int) (Math.min(badguy.getPosition().x, t.getPosition().x)+0.5)/10;
		int y = (int) (Math.min(badguy.getPosition().y, t.getPosition().y)+0.5)/10;

		// dimensions of the area
		int widt = (int)( Math.abs(badguy.getPosition().x-t.getPosition().x)+0.5)/10;
		int ht = (int)(Math.abs(badguy.getPosition().y-t.getPosition().y)+0.5)/10;

		// the obstacle in the way
		ObstacleObject obs = null;

		// loop through the area until an obstacle is found there
		try {
			for (int i = 0; i<widt; i++){
				for (int j = 0; j<ht; j++){
					if (grid[x+i][y+j] !=null)
						if (grid[x+i][y+j].isInPath(badguy.getPosition(), t.getPosition()))
							obs =  grid[x+i][y+j];
					if (obs!=null) break;
				}
				if (obs!=null) break;
			}
		}
		catch (Exception e){}

		return obs;
	}
}








