// a simple object to hold count of the variable used in the game
public class Counter {

	public boolean alive = true;	// if player is still alive
	public int bullets = 0;			// how many bullets are left
	public boolean paused = false;	// if game is paused
	public boolean bonus = false;	// if next target should be a bonus 
	public int noOfBadguys = 0;		// the number of badguys on the wolrd
	public int score = 0;			// the current score

	public Counter(){

	}

	// sets the first bonus ball when the score has reached 10
	public void setBonus(){
		bonus = score == 10;
	}

	// sets the game to plause/play
	public void setPaused() {
		paused = true;
	}

	// sets the game to plause/play
	public void setPlay() {
		paused = false;
	}

	// check if player is still alive and the game not paused
	public boolean play() {
		return (alive&&!paused);
	}

	// generate probability of a new bad guy
	public boolean generateBadGuy() {
		if (noOfBadguys<15){
			// the probabailiy for a new bad guy increases as the score increases
			double probability = score/3000.0;
			return (Math.random()<probability);
		}
		else return false;
	}

	// reset bonus flag when bonus ball is caught
	public void resetBonus() {
		bonus = false;
		bullets = 100;
	}

	// decrease bullets and set new bonus when bullets are finished
	public void shoot() {
		bullets--;
		bonus = bullets==0;
	}
}
