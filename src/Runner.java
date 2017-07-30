

import GameEngine.GameCanvas;
import javax.swing.*;

public class Runner {
    public static void main (String [] args) {

        JFrame frame = new JFrame("Survival Game");
        BBB ag = new BBB(100);
        ag.linkToFrame(frame);

        GameCanvas glc = new GameCanvas(ag);
        frame.add(glc);

        frame.setResizable(true);
        frame.setSize(800, 700);
        frame.setVisible(true);

        // need this so that you don't have to click on the window to gain focus ;)
        glc.requestFocusInWindow();
    }


}
