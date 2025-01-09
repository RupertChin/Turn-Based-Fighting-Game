import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.*;
public class playerAttack implements MouseListener, KeyListener{
	static Image attackBar;
	public playerAttack () {
		attackBar = Toolkit.getDefaultToolkit().getImage("attackBar.gif");
	}
	public static void main (String [] args) {
		/*
		 * This method will control the attack bar
		 * When it is the player's turn to attack, an interface will appear with a vertical bar moving left and right across a "target"
		 * The player can click z to confirm the attack position.
		 * The attack will have the odds to do the most damage if it is clicked directly in the middle, a preset critical strike zone
		 * This attack bar will only move right once and left once
		 * If the attack is not activated after this period, the player will attack with the weakest attack possible
		 */
		
		
	}
	public void attackAnimation (Graphics g) {
		int x = 480;
		
		g.drawImage(attackBar, x, 0, (ImageObserver) this);
		while (x > 160) {
			x -= 5;
			g.drawImage(attackBar, x, 0, (ImageObserver) this);
		}
		while (x < 480) {
			x += 5;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
