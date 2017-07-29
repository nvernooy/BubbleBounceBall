import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

// and object to store and retrieve highscores
public class Highscore {
	private File highscorefile;
	private Scanner read;
	private PrintWriter write;
	int newscore;
	int high;
	
	
	// new object with paramater being the final score
	public Highscore (int score) throws Exception{
		newscore = score;
		high = 0;
		highscorefile = new File ("highscore.txt");		

		// write the final score to the file
		write = new PrintWriter (new FileOutputStream (highscorefile));
		write.write(newscore);
		write.close();
	}
	
	// get the high score from the file
	public int getHighScore() throws Exception{
		read = new Scanner(new FileInputStream(highscorefile));
		
		high = 0;
		while (read.hasNextInt()){
			int temp = read.nextInt();
			if (high>temp){
				high = temp;
			}
		}
				
		read.close();
		return (high);
	}
}
