import java.awt.*;
import javax.swing.*;

public class Player {
	
	public String imgFileName1;
	public String imgFileName2;
	public ImageIcon img1, img2;
	public Rectangle rect;
	public int x;
	public int y;
	public int defaultX;
	public int defaultY;
	public int speed;
	public int yVel = 0;
	public int health = 100; // TODO maybe change, maybe add to constructor
	public boolean moveLeft, moveRight, moveUp, moveDown, immune;
	
	public Player(String imgFileName1, String imgFileName2, int x, int y, int width, int height, int speed) {
		this.imgFileName1 = imgFileName1;
		this.imgFileName2 = imgFileName2;
		this.x = x;
		this.y = y;
		rect = new Rectangle(x, y, width, height);
		defaultX = x;
		defaultY = y;
		this.speed = speed;
		
		img1 = new ImageIcon(imgFileName1);
		img2 = new ImageIcon(imgFileName2);
	}
	
//	public Player(String imgFileName) {
//		this.imgFileName = imgFileName;
//		this.x = 0;
//		this.y = 0;
//		
//		img = new ImageIcon(imgFileName);
//	}
//	
//	public Player() {
//		this.x = 0;
//		this.y = 0;
//		img = new ImageIcon();
//	}
	
	public void move(int dx, int dy) {
		x += dx;
		y += dy;
		rect.setLocation(x, y);
	}
	
	public void setDefaultPos() {
		x = defaultX;
		y = defaultY;
		rect.setLocation(x,y);
	}
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		rect.setLocation(x,y);
	}
	
	public void damage(int dmg) {
		health = Math.max(0, health - dmg);
	}
	
	public void heal(int amt) {
		health = Math.min(100, health + amt);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
