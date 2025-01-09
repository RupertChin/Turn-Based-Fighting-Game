import javax.swing.*;

public class Enemy {

	public String imgFileName;
	public ImageIcon img;
	public int x;
	public int y;
	public int health;
	public int maxHealth;
	
	public Enemy(String imgFileName, int x, int y, int health) {
		this.imgFileName = imgFileName;
		this.x = x;
		this.y = y;
		this.health = health;
		this.maxHealth = health;
		
		img = new ImageIcon(imgFileName);
	}
	
//	public Enemy(String imgFileName) {
//		this.imgFileName = imgFileName;
//		this.x = 0;
//		this.y = 0;
//		
//		img = new ImageIcon(imgFileName);
//	}
//	
//	public Enemy() {
//		this.x = 0;
//		this.y = 0;
//		img = new ImageIcon();
//	}
	
	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	public void damage(int dmg) {
		health -= dmg;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
