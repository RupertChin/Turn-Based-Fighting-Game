import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.*;
import java.util.Arrays;

//Jason Deng and Robert Yin
//ICS3U ISU game project
//Sustale
//Sustale is a game based on themes extracted from the popular games undertale and among us
//The graphics are all hand-drawn, with the exception of the JButtons and text JLabels
//The game has 2 levels of varying difficulties, which use different attacks and have bosses with different amounts of health

//In the game, the character is a small among us icon, controlled by the arrow keys
//The among us is only controlled with arrow keys when it is being attacked
//Furthermore, there are two movement types when the player is being attacked

//1: free movement
//2: movement with gravity

//To win, the player must attack the boss until their health is 0
//The player attacks by clicking fight when it is their turn (when the among us is not drawn in the player zone)
//A white bar is prompted to move across the screen, and the player must click z to activate it as close to the center of the screen
//as possible. 

//if the player's health reaches 0, they lose, and they have the option to play again or exit the game


public class Sustale extends JPanel implements ActionListener, MouseListener, KeyListener {
	// window dimensions
	public final int WIDTH = 640;
	public final int HEIGHT = 416;
	boolean firstAttackClicked, inPlayerAttack;

	public Rectangle atkBar = new Rectangle (800, 140, 14, 128);

	// used for graphics
	public JFrame frame;
	public ImageIcon fight_background;
	public ImageIcon playerAtkBackground;
	Font eightBit, eightBit15, eightBit20;
	public JLabel playerHealth;
	public Rectangle healthBar;
	// reduces flickering
	Image offScreenImage;
	Graphics offScreenBuffer;

	// buttons for title screen + title screen background
	public JButton exit, play, aboutUs;
	public ImageIcon logoImage;
	public int menuChoice = 1;
	public JLabel aboutUsLabel;
	public ImageIcon aboutUsBG;
	public boolean inAboutUs;

	//Instruction labels
	public ImageIcon instructions, instructions2;
	public boolean displayInstructions = false;
	public JLabel instruct;
	public int instructPage = 0;

	//background music
	static Clip background;


	//buttons for level selection
	public JButton level1, level2;
	public JButton[] levelButtons = new JButton[2];
	public int levelChoice = 0;
	public JLabel levelLabel;

	// buttons for fight screen
	public JButton fightButton, itemButton, quitButton;
	public JButton[] fightScreenButtons = new JButton[3];
	public int buttonSelected = 0;
	public int attackNum = 0;
	public JButton fistBtn, spaceBtn, wallBtn, ballBtn, knifeBtn;

	// buttons for item menu
	public JButton fullHealthButton, partialHealthButton, shieldButton, speedButton;
	public JButton[] itemMenuButtons = new JButton[4];
	public int itemMenuButtonSelected = 0;
	public int shield;

	//buttons for end screen
	public JButton playAgainButton, endExitButton;
	public int endScreenChoice = 0;
	public JLabel endLabel;

	// variables for attacks
	public double atkMultiplier = 1;
	public int playerAtkDmg = 50;
	public int damageDone;

	//variables for enemy fist attack
	public int fistAtkX1 = -50, fistAtkX2 = -50, fistAtkX3 = -50; 
	public int shipX = -20;
	public boolean inFist = false, fistDropping = false;
	public ImageIcon fist1, fist2, fist3, fistWarning1, fistWarning2, fistWarning3;
	public int fistY;

	//variables for enemy knife attack 
	public boolean inKnives = false;

	public ImageIcon knife;
	public ImageIcon [] knives = new ImageIcon [20];
	int [] knivesY = new int [20];
	int [] knivesX = new int [20];

	//variables for enemy spaceship attack
	public boolean inSpaceship = false;

	public ImageIcon spaceship;
	public ImageIcon [] spaceshipArr = new ImageIcon [25];
	int [] spaceX = new int [25];
	int [] spaceY = new int [25];

	//variables for enemy wall attack
	public boolean inWalls = false;

	public int [] wallHeight = new int [15];
	public int [] wallX = new int [15];

	//variables for enemy ball attack
	public boolean inBall = false;

	public ImageIcon ball;
	public ImageIcon [] ballArr = new ImageIcon[20];
	public int [] ballX = new int [20];
	public int [] ballY = new int [20];


	// Images for bosses
	public ImageIcon boss1, boss2;
	public int bossHealth;
	public double maxBossHealth;

	// Entities
	Player player;
	Enemy enemy;

	// Various states
	public int screen = 0; // 0: title, 1: fight 2: level selection, 3: end screen
	public boolean playerTurn = false; // if false, enemy is currently attacking
	public int movementType = 0; // 0: free movement, 1: gravity
	public int gravityCount = 0;
	public boolean touchingGround = false; // 
	public int playerScreen = 0; // 0: blank, 1: fighting, 2: item menu

	/* additional info:
	 * 
	 * fight buttons topleft, botright coords
	 * 192, 404
	 * 261, 363
	 * 359, 405
	 * 429, 364
	 * 527, 405
	 * 
	 * healthbar dimensions: 150px width, 18px height
	 * 
	 * playerScreen topleft, botright
	 * 62, 186
	 * 572, 314
	 */

	public Sustale() {

		//initializing the frame
		frame = new JFrame("Testing");
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setResizable(false);
		frame.setFocusable(true);
		setFocusable(true);
		frame.add(this);
		frame.addMouseListener(this);
		frame.addKeyListener(this);
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("amongus.wav"));
			background = AudioSystem.getClip();
			background.open(sound);

		}
		catch (Exception e) {
		}
		//		background.setFramePosition(0);
		background.loop(Clip.LOOP_CONTINUOUSLY);
		background.start();

		this.setLayout(null);
		//terminates the program if the window is closed
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent we) {
				System.exit(0);
			}
		});

		//		background = Toolkit.getDefaultToolkit ().getImage ("fight_background.png");
		//		frame.getContentPane().add(background);

		fight_background = new ImageIcon("fight_background.png");
		playerAtkBackground = new ImageIcon("playerAttackGUI.gif");

		// player health counter
		playerHealth = new JLabel("100");
		playerHealth.setBounds(482, 302, 40, 20);
		playerHealth.setForeground(Color.WHITE);
		playerHealth.setFont(new Font("8bitoperator JVE", Font.PLAIN, 20));
		playerHealth.setVisible(false);
		this.add(playerHealth);

		// buttons for fight screen
		fightButton = new JButton("Fight");
		fightButton.setBounds(115, 333, 70, 41);
		fightButton.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 14));
		fightButton.setActionCommand("fight");
		fightButton.addActionListener(this);
		fightButton.setOpaque(false);
		fightButton.setContentAreaFilled(false);
		fightButton.setFocusPainted(false);
		fightButton.setBorderPainted(false);
		fightButton.setFocusable(false);
		fightButton.setForeground(Color.WHITE);

		itemButton = new JButton("Item");
		itemButton.setBounds(282, 333, 70, 41);
		itemButton.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 14));
		itemButton.setActionCommand("item");
		itemButton.addActionListener(this);
		itemButton.setContentAreaFilled(false);
		itemButton.setFocusPainted(false);
		itemButton.setBorderPainted(false);
		itemButton.setFocusable(false);
		itemButton.setForeground(Color.WHITE);

		quitButton = new JButton("Quit");
		quitButton.setBounds(449, 333, 70, 41);
		quitButton.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 14));
		quitButton.setActionCommand("quit");
		quitButton.addActionListener(this);
		quitButton.setContentAreaFilled(false);
		quitButton.setFocusPainted(false);
		quitButton.setBorderPainted(false);
		quitButton.setFocusable(false);
		quitButton.setForeground(Color.WHITE);

		fistBtn = new JButton();
		fistBtn.setActionCommand("fist");
		spaceBtn = new JButton();
		spaceBtn.setActionCommand("space");
		wallBtn = new JButton();
		wallBtn.setActionCommand("wall");
		ballBtn = new JButton();
		ballBtn.setActionCommand("ball");
		knifeBtn = new JButton();
		knifeBtn.setActionCommand("knife");

		add(fistBtn);
		add(spaceBtn);
		add(wallBtn);
		add(ballBtn);
		add(knifeBtn);

		fistBtn.setVisible(false);
		spaceBtn.setVisible(false);
		wallBtn.setVisible(false);
		ballBtn.setVisible(false);
		knifeBtn.setVisible(false);

		// buttons for death menu
		playAgainButton = new JButton ("Play Again");
		playAgainButton.setBounds(130, 250, 120, 65);
		playAgainButton.setBackground(Color.WHITE);
		playAgainButton.setForeground(Color.ORANGE);
		playAgainButton.setActionCommand("playAgainBtn");
		playAgainButton.addActionListener(this);
		playAgainButton.setOpaque(false);
		playAgainButton.setBorder(new LineBorder(Color.ORANGE, 4));
		playAgainButton.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 16));
		playAgainButton.setFocusable(false);


		endExitButton = new JButton("Exit");
		endExitButton.setBounds(375, 250, 120, 65);
		endExitButton.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 14));
		endExitButton.setActionCommand("endExitBtn");
		endExitButton.addActionListener(this);
		endExitButton.setOpaque(false);
		endExitButton.setBorder(new LineBorder(Color.WHITE, 4));
		endExitButton.setFocusable(false);
		endExitButton.setForeground(Color.WHITE);
		endExitButton.setBackground(Color.WHITE);


		endLabel = new JLabel ("", SwingConstants.CENTER);
		endLabel.setBounds(220,100,200,50);
		endLabel.setBackground(Color.BLACK);
		endLabel.setForeground(Color.WHITE);
		endLabel.setOpaque(false);
		endLabel.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 30));
		endLabel.setFocusable(false);

		add(playAgainButton);
		add(endExitButton);
		add(endLabel);
		playAgainButton.setVisible(false);
		endExitButton.setVisible(false);
		endLabel.setVisible(false);
		// buttons for item menu
		fullHealthButton = new JButton("Full Health");
		fullHealthButton.setActionCommand("full health");
		fullHealthButton.addActionListener(this);
		fullHealthButton.setContentAreaFilled(false);
		fullHealthButton.setFocusPainted(false);
		fullHealthButton.setBorderPainted(false);
		fullHealthButton.setFocusable(false);
		fullHealthButton.setFont(new Font("8bitoperator JVE", Font.PLAIN, 15));
		fullHealthButton.setForeground(Color.WHITE);

		partialHealthButton = new JButton("Partial Health");
		partialHealthButton.setActionCommand("partial health");
		partialHealthButton.addActionListener(this);
		partialHealthButton.setContentAreaFilled(false);
		partialHealthButton.setFocusPainted(false);
		partialHealthButton.setBorderPainted(false);
		partialHealthButton.setFocusable(false);
		partialHealthButton.setFont(new Font("8bitoperator JVE", Font.PLAIN, 15));
		partialHealthButton.setForeground(Color.WHITE);

		shieldButton = new JButton("Shield");
		shieldButton.setActionCommand("shield");
		shieldButton.addActionListener(this);
		shieldButton.setContentAreaFilled(false);
		shieldButton.setFocusPainted(false);
		shieldButton.setBorderPainted(false);
		shieldButton.setFocusable(false);
		shieldButton.setFont(new Font("8bitoperator JVE", Font.PLAIN, 15));
		shieldButton.setForeground(Color.WHITE);

		speedButton = new JButton("Speed Boost!");
		speedButton.setActionCommand("speed");
		speedButton.addActionListener(this);
		speedButton.setContentAreaFilled(false);
		speedButton.setFocusPainted(false);
		speedButton.setBorderPainted(false);
		speedButton.setFocusable(false);
		speedButton.setFont(new Font("8bitoperator JVE", Font.PLAIN, 15));
		speedButton.setForeground(Color.WHITE);


		//array of the items inside the menu
		itemMenuButtons[0] = fullHealthButton;
		itemMenuButtons[1] = partialHealthButton;
		itemMenuButtons[2] = shieldButton;
		itemMenuButtons[3] = speedButton;

		// set locations for buttons
		int ind = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				if (ind < itemMenuButtons.length) {
					itemMenuButtons[ind].setBounds(65+245*i, 160+40*j, 245, 40);
				} 
				ind++;
			}
		}

		for (JButton b : itemMenuButtons) {
			b.setVisible(false);
			this.add(b);
		}

		//title screen buttons
		play = new JButton("Play");
		play.setBounds(260, 125, 120, 65);
		play.setBackground(Color.WHITE);
		play.setForeground(Color.ORANGE);
		play.setActionCommand("playBtn");
		play.addActionListener(this);
		play.setOpaque(false);
		play.setBorder(new LineBorder(Color.ORANGE, 4));
		play.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 28));
		play.setFocusable(false);

		aboutUs = new JButton("About Us");
		aboutUs.setBounds (260, 215, 120, 65);
		aboutUs.setBackground(Color.WHITE);
		aboutUs.setForeground(Color.WHITE);
		aboutUs.setActionCommand("aboutUsBtn");
		aboutUs.addActionListener(this);
		aboutUs.setOpaque(false);
		aboutUs.setBorder(new LineBorder(Color.WHITE, 4));
		aboutUs.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 28));
		aboutUs.setFocusable(false);


		//Exit buttons and properties
		exit = new JButton("Exit");
		exit.setBounds (260, 305, 120, 65);
		exit.setBackground(Color.WHITE);
		exit.setForeground(Color.WHITE);
		exit.setActionCommand("exitBtn");
		exit.addActionListener(this);
		exit.setOpaque(false);
		exit.setBorder(new LineBorder(Color.WHITE, 4));
		exit.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 28));
		exit.setFocusable(false);

		setLayout(null);
		add(play);
		add(aboutUs);
		add(exit);


		// instruction labels
		instruct = new JLabel("PRESS I TO VIEW INSTRUCTIONS");
		instruct.setBounds(415, 190, 200, 100);
		instruct.setBackground(Color.WHITE);
		instruct.setForeground(Color.WHITE);
		instruct.setOpaque(false);
		instruct.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 15));
		instruct.setFocusable(false);
		
		add(instruct);

		// label that shows when about us button is clicked
		aboutUsLabel = new JLabel ("<html>Hi!!! Welcome to the about us screen of Sustale. This game is the product of a lot of hard work and learning for us. "
				+ "Us being Jason Deng and Robert Yin (In Ms. Wong's ICS3U class) This game is a spin off of undertale's unique fighting interface, combined with the "
				+ "popular themes of the hit game among us.  We really hope you enjoy playing this game!  p.s. Press X to head back to the title screen.</html>");
		aboutUsLabel.setBounds(160, 115, 280, 280);
		aboutUsLabel.setBackground(Color.BLACK);
		aboutUsLabel.setForeground(Color.BLACK);
		aboutUsLabel.setOpaque(false);
		aboutUsLabel.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 12));
		aboutUsLabel.setFocusable(false);

		// level selection screen buttons and label
		levelLabel = new JLabel("USE <- OR -> TO SWAP YOUR SELECTION");
		levelLabel.setBounds(220, 290, 220, 80);
		levelLabel.setBackground(Color.WHITE);
		levelLabel.setForeground(Color.WHITE);
		levelLabel.setOpaque(false);
		levelLabel.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 14));
		levelLabel.setFocusable(false);


		level1 = new JButton("Level 1");
		level1.setBounds(130, 250, 120, 65);
		level1.setBackground(Color.WHITE);
		level1.setForeground(Color.ORANGE);
		level1.setActionCommand("level1Btn");
		level1.addActionListener(this);
		level1.setOpaque(false);
		level1.setBorder(new LineBorder(Color.ORANGE, 4));
		level1.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 16));
		level1.setFocusable(false);

		level2 = new JButton("Level 2");
		level2.setBounds(375, 250, 120, 65);
		level2.setBackground(Color.WHITE);
		level2.setForeground(Color.WHITE);
		level2.setActionCommand("level2Btn");
		level2.addActionListener(this);
		level2.setOpaque(false);
		level2.setBorder(new LineBorder(Color.WHITE, 4));
		level2.setFont(new Font ("8bitoperator JVE", Font.PLAIN, 16));
		level2.setFocusable(false);

		add(level1);
		add(level2);
		add(levelLabel);
		add(aboutUsLabel);
		level1.setVisible(false);
		level2.setVisible(false);
		levelLabel.setVisible(false);
		aboutUsLabel.setVisible(false);

		//importing the font
		try {

			GraphicsEnvironment graphicsE = GraphicsEnvironment.getLocalGraphicsEnvironment();
			graphicsE.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("8bitoperator_jve.ttf")));
		} 
		catch (IOException|FontFormatException e) {    

		}

		add(fightButton);
		add(itemButton);
		add(quitButton);
		fightButton.setVisible(false);
		itemButton.setVisible(false);
		quitButton.setVisible(false);

		fightScreenButtons[0] = fightButton;
		fightScreenButtons[1] = itemButton;
		fightScreenButtons[2] = quitButton;

		//Initializing the arrays that store attack information
		for (int i = 0; i < 20; i++) {
			knives [i] = new ImageIcon ("knife.gif");
			knivesX [i] = (int) (Math.random() * -500 - 50);
			knivesY [i] = (int) (Math.random() * 115 + 155);

		}
		Arrays.sort(knivesX);

		for (int j = 0; j < 25; j++) {
			spaceshipArr [j] = new ImageIcon ("spaceship.png");
			spaceX [j] = (int) (Math.random() * 1000 + 750);
			spaceY [j] = (int) (Math.random() * 115 + 155);

		}
		Arrays.sort(spaceX);

		for (int i = 0; i < 15; i++) {
			wallHeight [i]= (int) (Math.random() * 20 + 15);
			wallX [i] = (i * -175) - 70;
		}
		Arrays.sort(wallX);

		for (int i = 0; i < 20; i++) {
			ballArr[i] = new ImageIcon("ball.png");
			ballX [i] = (int)(Math.random()* 460) + 54;
			ballY [i] = (int)(Math.random() * -700 - 50);
		}
		Arrays.sort(ballY);
		fistAtkX1 = ((int) (Math.random() * 260 + 54)); 
		//gets random x value from 54 to 574
		//uses rectangle as hit box for the fist
		fistAtkX2 = fistAtkX1 + 100;
		fistAtkX3 = fistAtkX2 + 100;

		// Makes a transparent image to set the cursor to
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		//set the cursor to the image
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		
		//set the frame's cursor as the blank cursor
		frame.getContentPane().setCursor(cursor);

		player = new Player("red_amogus_icon.png", "orange_amogus_icon.png", 300, 200, 16, 20, 2);
		logoImage = new ImageIcon ("title_background.gif");
		fistWarning1 = new ImageIcon ("fistWarning.png");
		fistWarning2 = new ImageIcon ("fistWarning.png");
		fistWarning3 = new ImageIcon ("fistWarning.png");
		fist1 = new ImageIcon ("fistAttack.png");
		fist2 = new ImageIcon ("fistAttack.png");
		fist3 = new ImageIcon ("fistAttack.png");
		boss1 = new ImageIcon ("boss1.png");
		boss2 = new ImageIcon ("boss2.png");
		aboutUsBG = new ImageIcon ("aboutUsBG.png");
		instructions = new ImageIcon ("instructions.png");
		instructions2 = new ImageIcon ("instructions2.png");

		frame.pack();
		frame.setVisible(true);

		playerAtk playerAtk = new playerAtk();
		playerAtk.start();
		fistAttack fistAtk = new fistAttack();
		fistAtk.start();
		knifeAttack knifeAtk = new knifeAttack();
		knifeAtk.start();
		spaceshipAttack spaceship = new spaceshipAttack();
		spaceship.start();
		wallAttack wallAtk = new wallAttack();
		wallAtk.start();
		ballAttack ballAtk = new ballAttack();
		ballAtk.start();
		game game = new game();
		game.start();
		immunityReset immunity = new immunityReset();
		immunity.start();
	}

	public void movePlayer(Graphics g) {


		// player movement
		int dx = 0, dy = 0;
		if (player.moveLeft && player.getX() > 52) dx -= player.speed;
		if (player.moveRight && player.getX() < 552) dx += player.speed;
		if (movementType == 0) {
			if (player.moveUp && player.getY() > 155) dy -= player.speed;
			if (player.moveDown && player.getY() < 270) dy += player.speed;
		} else if (movementType == 1) {
			if (player.getY() < 270) {
				gravityCount = (gravityCount + 1) % 5;
				if (gravityCount == 0) player.yVel += 1;
			}
			else {
				if (player.moveUp) player.yVel = -5;
				else player.yVel = 0;
			}
			dy += player.yVel;
		}
		player.move(dx, dy);

		// paint player on screen
		player.img1.paintIcon(this, offScreenBuffer, player.x, player.y);
		//		System.out.println("" + player.moveLeft + player.moveRight + player.moveDown + player.moveUp);
		//		System.out.println(player.x + ", " + player.y);
		//		g.drawImage(offScreenImage,0,0,this);

		try {
			Thread.sleep(5);
		}
		catch(InterruptedException e) {
		}

		repaint();
	}


	//	public void itemMenu(Graphics g) {
	//		playerScreen = 2;
	//	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Set up the offscreen buffer the first time paint() is called
		if (offScreenBuffer == null)
		{
			offScreenImage = createImage (this.getWidth (), this.getHeight ());
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());


		// draw background
		if (screen == 0 || screen == 2) { // main screen
			logoImage.paintIcon(this,  offScreenBuffer,  0, 0);
			
			//checks if the about us view should be drawn
			if (inAboutUs) {
				aboutUsBG.paintIcon(this, offScreenBuffer, 125, 125);
			}
			
			//checks if the instructions should be displayed
			else if (displayInstructions && instructPage == 0) {
				instructions.paintIcon(this, offScreenBuffer, 125, 125);
			}
			else if (displayInstructions && instructPage == 1) {
				instructions2.paintIcon(this,offScreenBuffer,125,125);
			}
		} else if (screen == 1) { // fight screen
			fight_background.paintIcon(this, offScreenBuffer, -10, -100);
			if (playerScreen == 1 && !playerTurn) {

				fight_background.paintIcon(this, offScreenBuffer, -10, -100);
				movePlayer(g);
			}
			else if (playerScreen == 3) {
				playerAtkBackground.paintIcon(this, offScreenBuffer, -10, -36);
			}
		}
		g.drawImage (offScreenImage, 0, 0, this);


		if (screen == 1) {
			if (levelChoice == 0) { // level 1, boss with shoes
				boss1.paintIcon (this, offScreenBuffer, 180, 15);
			}
			else if (levelChoice == 1) { // level two, wider among us
				boss2.paintIcon(this, offScreenBuffer, 40, 15);
			}

			//update health bars
			offScreenBuffer.setColor(Color.RED);
			offScreenBuffer.fillRect(240, 10, 130, 10);
			offScreenBuffer.setColor(Color.GREEN);
			offScreenBuffer.fillRect(240, 10, (int)(130*(bossHealth/maxBossHealth)), 10);
			playerHealth.setText("" + player.health);
			offScreenBuffer.fillRect(252, 302, (int)(150*(player.health/100.0)), 18);
			offScreenBuffer.setColor(Color.WHITE);

			if (playerScreen == 3) { // attacking boss


				if (!firstAttackClicked) {
					offScreenBuffer.clearRect(atkBar.x, 140, atkBar.width, atkBar.height);
					//					/fight_background.paintIcon(this, offScreenBuffer, -10, -100);
				}


				if (!firstAttackClicked) {
					g.setColor(Color.WHITE);
					g.fill3DRect(atkBar.x, atkBar.y, atkBar.width, atkBar.height, true);
					if (levelChoice == 0) {
						boss1.paintIcon (this, g, 180, 15);
					}
					//level two, wider among us
					else if (levelChoice == 1) {
						boss2.paintIcon(this, g, 40, 15);
					}
				}

			}
			else if (playerScreen == 1 && !playerTurn && player.health > 0 && bossHealth > 0) {

				//draws the attacks if the corresponding boolean flag is set to true by the game thread
				if (inFist) { // for fist attack
					if (!fistDropping) {
						offScreenBuffer.clearRect(fistAtkX1, 267, 59, 19);
						offScreenBuffer.clearRect(fistAtkX2, 267, 59, 19);
						offScreenBuffer.clearRect(fistAtkX3, 267, 59, 19);
						fistWarning1.paintIcon(this,  offScreenBuffer, fistAtkX1, 267);
						fistWarning2.paintIcon(this,  offScreenBuffer, fistAtkX2, 267);
						fistWarning3.paintIcon(this,  offScreenBuffer, fistAtkX3, 267);
						//g.drawImage(offScreenImage, 0, 0, this); 
						//paint fight background
					}
					else {
						fist1.paintIcon(this, offScreenBuffer, fistAtkX1, fistY);
						fist2.paintIcon(this, offScreenBuffer, fistAtkX2, fistY);
						fist3.paintIcon(this, offScreenBuffer, fistAtkX3, fistY);
						//						g.drawImage(offScreenImage, 0, 0, this); 
					}
				}
				else if (inKnives) { // for knife attack
					for (int i = 0; i < 20; i++) {
						knives[i].paintIcon(this, offScreenBuffer, knivesX[i], knivesY[i]);
					}
					//					g.drawImage(offScreenImage,0, 0, this);
				}
				else if (inSpaceship) { // for spaceship attack
					for (int i = 0; i < 25; i++) {
						if (spaceX[i] < 750 && spaceX [i] > 50) {
							spaceshipArr[i].paintIcon(this, offScreenBuffer, spaceX[i], spaceY[i]);
						}
					}
					//					g.drawImage(offScreenImage,0, 0, this);
				}
				else if (inWalls) { // for wall attack
					for (int i = 0; i < 15; i++) {

						if (wallX[i] > 50 && wallX [i] < 557) {
							offScreenBuffer.setColor(Color.WHITE);
							offScreenBuffer.fillRect(wallX[i], 287 - wallHeight [i], 10, wallHeight[i]);
						}
						//						g.drawImage(offScreenImage,0, 0, this);
					}
				}
				else if (inBall) { // for ball attack
					for (int i = 0; i < 20; i++) {
						if (ballY[i] < 264 && ballY[i] >150) {
							ballArr[i].paintIcon(this, offScreenBuffer, ballX[i], ballY[i]);
						}
					}
				}



			}
			//this draws the bosses directly to g, allowing it to be drawn while the player attack is occurring
			if (levelChoice == 0) {
				boss1.paintIcon (this, g, 180, 15);
			}
			//level two, wider among us
			else if (levelChoice == 1) {
				boss2.paintIcon(this, g, 40, 15);
			}
			g.drawImage(offScreenImage, 0, 0, this); 


		}
		if (screen == 3) {//End screen
			//sets all the end screen buttons to visible and sets the screen as black
			setItemButtonsVisible(false);
			playerHealth.setVisible(false);
			fightButton.setVisible(false);
			itemButton.setVisible(false);
			quitButton.setVisible(false);
			playAgainButton.setVisible(true);
			endExitButton.setVisible(true);
			offScreenBuffer.setColor(Color.BLACK);
			offScreenBuffer.fill3DRect(0, 0, WIDTH, HEIGHT, false);
			g.drawImage(offScreenImage, 0, 0, this);
			//if the player wins, draw "you win", if the players loses, draw "you lost"
			if(bossHealth == 0) {
				endLabel.setText("YOU WON!");
				endLabel.setBorder(new LineBorder(Color.GREEN, 4));
			}
			else if (player.health == 0) {
				endLabel.setText("YOU LOSE");
				endLabel.setBorder(new LineBorder(Color.RED, 4));
			}
			endLabel.setVisible(true);
		}


	}

	// helper functions

	/*
	 * Description: clearScreen clears the window completely
	 * Parameters: Graphics g
	 * return type: void
	 */
	public void clearScreen(Graphics g) {
		g.clearRect(0, 0, WIDTH, HEIGHT);
	}

	/*
	 * Description: clearPlayerArea clears the player area (a white bordered rectangle)
	 * Parameters: Graphics g
	 * return type: void
	 */
	public void clearPlayerArea(Graphics g) {
		g.clearRect(62, 186, 510, 128);
	}

	/*
	 * ENEMY ATTACKS GO HERE
	 */

	//Description: This attack has 3 fists coming down from the top of the screen
	//A warning image is drawn where the fists will drop, and if the
	//player touches the fists, they take damage
	public class fistAttack extends Thread 
	{
		public void run() {
			while (true) {
				while (true) {
					if (inFist && !playerTurn && screen == 1) {
						if (bossHealth == 0) { // checks if the enemy is still alive
							inFist = false;
							screen = 3;
							playerScreen = 0;
							break;
						}


						//resets the y location of the fists before it drops
						fistY = -100;

						//3 fists slam down
						try {
							Thread.sleep (1500);
							//						repaint();
						} 
						catch (InterruptedException e) {
						}
						fistDropping = true;
						while (fistY < 190) { //making the fists go down
							if (!inFist)
								break;
							fistY += 5;

							//This checks if the fist is colliding with the player WHILE it is falling
							if (player.rect.intersects(new Rectangle(fistAtkX1, fistY, 60,96))|| player.rect.intersects(new Rectangle(fistAtkX2, fistY, 60,96)) || player.rect.intersects(new Rectangle(fistAtkX3, fistY, 60,96))) {
								
								if (!player.immune && shield < 1) { // this makes the player take damage and gives them a damage timer of 250 ms
									player.damage(30);
									player.immune = true;
								}
								else if (shield > 0 && !player.immune){ //checks if the player has a shield
									shield--;
									player.immune = true;
								}
								if (player.health == 0) { // checks if the player is dead
									//reset variables
									inFist = false;
									fistDropping = false;
									playerScreen = 0;
									screen = 3;
									fistY = -100;
									fistAtkX1 = ((int) (Math.random() * 260 + 54)); 
									fistAtkX2 = fistAtkX1 + 100;
									fistAtkX3 = fistAtkX2 + 100;
									break;
								}
							}
							try {
								Thread.sleep(1);
							}
							catch(InterruptedException e) {
							}
						}

						//this loops when the fist is on the ground
						for (int i = 0; i < 150; i++) {
							if (!inFist)
								break;
							//This checks if the fist is colliding with the player when it is on the ground
							if (player.rect.intersects(new Rectangle(fistAtkX1, fistY, 60,96))|| player.rect.intersects(new Rectangle(fistAtkX2, fistY, 60,96)) || player.rect.intersects(new Rectangle(fistAtkX3, fistY, 60,96))) {
								if (!player.immune && shield < 1) {
									player.damage(30);
									player.immune = true;
								}
								else if (shield > 0 && !player.immune){ //checks if the player has a shield
									shield--;
									player.immune = true;
								}
								if (player.health == 0) { //checks if the player is still alive
									//reset variables
									inFist = false;
									fistDropping = false;
									screen = 3;
									playerScreen = 0;
									fistY = -100;
									fistAtkX1 = ((int) (Math.random() * 260 + 54)); 
									fistAtkX2 = fistAtkX1 + 100;
									fistAtkX3 = fistAtkX2 + 100;
									break;
								}
							}
							try {
								Thread.sleep (20);
							} 
							catch (InterruptedException e) {
							}
						}
						//resets all the variables so that paint component is drawing the right things
						playerTurn = true;
						fistDropping = false;
						inFist = false;

						attackNum++;
						//resetting the variable locations so that the attack can run again randomly
						fistAtkX1 = ((int) (Math.random() * 260 + 54)); 
						//gets random x value from 54 to 574
						//uses rectangle as hit box for the fist
						fistAtkX2 = fistAtkX1 + 100;
						fistAtkX3 = fistAtkX2 + 100;

						playerScreen = 0;
						//resetting movement booleans so that they are not preserved to the next attack
						player.moveDown = false;
						player.moveUp = false;
						player.moveRight = false;
						player.moveLeft = false;
						player.speed = 2;
					}
					else {


						//This is so that the loop doesn't run too many times, wasting resources
						try {
							sleep(3000);
						}
						catch(InterruptedException e) {
						}
					}
				}
			}
		}
	}

	//This attack has several knives moving from the left of the screen to the right of the screen
	//The knives are drawn at random locations and they are stored within an array
	//Inside this thread, the x location of the knives are incremented, and repaint is called, animating inside paint component
	public class knifeAttack extends Thread
	{
		public void run() {
			while (true) {
				while (true) {
					if (inKnives && !playerTurn && screen == 1) {
						if (bossHealth == 0) {
							inKnives = false;
							screen = 3;
							playerScreen = 0;
							break;
						}
						while (knivesX [0] < 700) {

							for (int i = 0; i < 20; i ++) {
								if (!inKnives)
									break;
								knivesX[i] += 8;
								if (player.rect.intersects(new Rectangle(knivesX[i], knivesY[i], 34, 9))) {
									
									if (!player.immune && shield < 1) {
										player.damage(10);
										player.immune = true;
									}
									else if (shield > 0 && !player.immune){
										shield--;
										player.immune = true;
									}
									if (player.health == 0) {
										
										inKnives = false;
										screen = 3;
										playerScreen = 0;
										break;
									}
								}
								try {
									sleep (1);
								}
								catch (InterruptedException e)
								{
								}
								//							repaint();
							}
						}
						inKnives = false;
						playerTurn = true;

						//resetting the arrays so that the attack can run again
						for (int i = 0; i < 20; i++) {
							knivesX [i] = (int) (Math.random() * -500 - 50);
							knivesY [i] = (int) (Math.random() * 115 + 155);
							Arrays.sort(knivesX);
						}
						attackNum++;

						playerScreen = 0;
						player.moveDown = false;
						player.moveUp = false;
						player.moveRight = false;
						player.moveLeft = false;
						player.speed = 2;
					}
					else {
						//This is so that the loop doesn't run too many times, wasting resources
						try {
							sleep (3000);
						}
						catch (InterruptedException e) 
						{
						}
					}
				}
			}
		}
	}


	//This Thread runs through an array of x values and increments them. When repaint is called through move, the spaceships
	//will be animated moving from the right of the screen to the left. 
	public class spaceshipAttack extends Thread{
		public void run() {
			while (true) {
				while (true) {
					if (inSpaceship && !playerTurn && screen == 1) {
						if (bossHealth == 0) {
							inSpaceship = false;
							screen = 3;
							playerScreen = 0;
							break;
						}
						while (spaceX [spaceX.length - 1] > 10) {

							for (int i = 0; i < 25; i ++) {
								spaceX[i] -= 8;
								if (!inSpaceship)
									break;
								if (player.rect.intersects(new Rectangle (spaceX[i], spaceY[i], 48, 16))) {
									
									if (!player.immune && shield < 1) {
										player.damage(20);
										player.immune = true;
									}
									else if (shield > 0 && !player.immune){
										shield--;
										player.immune = true;
									}
									if (player.health == 0) {
										
										inSpaceship = false;
										screen = 3;
										playerScreen = 0;
										break;
									}
								}
								//animation separation
								try {
									sleep (1);
								}
								catch (InterruptedException e)
								{
								}
							}
						}

						//resetting the arrays so that the attack can run again
						for (int i = 0; i < 25; i++) {
							spaceX [i] = (int) (Math.random() * 1000 + 750);
							spaceY [i] = (int) (Math.random() * 115 + 155);
							Arrays.sort(spaceX);
						}
						inSpaceship = false;
						playerTurn = true;
						attackNum++;
						playerScreen = 0;
						player.moveDown = false;
						player.moveUp = false;
						player.moveRight = false;
						player.moveLeft = false;
						player.speed = 2;
					}
					else {
						//This is so that the loop doesn't run too many times, wasting resources
						try {
							sleep (3000);
						}
						catch (InterruptedException e) 
						{
						}
					}
				}
			}
		}
	}

	//has walls moving in from the left side of the screen at a random height
	//hit boxes are tracked with a rectangle
	//if the player touches the walls, they take damage
	public class wallAttack extends Thread {
		public void run() {
			while (true) {
				while (true) {
					if (inWalls && !playerTurn && screen == 1) {
						if (bossHealth == 0) {
							inWalls = false;
							screen = 3;
							playerScreen = 0;
							break;
						}
						while (wallX[0] < 640) {

							for (int i = 0; i < 15; i++) {
								wallX[i] += 3;
								if (!inWalls) 
									break;
								//Checks whether the player touches the wall or not
								if (player.rect.intersects(new Rectangle(wallX[i], 287 - wallHeight [i], 10, wallHeight[i]))) {
									
									if (!player.immune && shield < 1) {
										player.damage(20);
										player.immune = true;
									}
									else if (shield > 0 && !player.immune){
										shield--;
										player.immune = true;
									}
									if (player.health == 0 || bossHealth == 0) {
										
										inWalls = false;
										screen = 3;
										playerScreen = 0;
										break;
									}

								}
								//							repaint();
								try {
									sleep(1);
								}
								catch (InterruptedException e){	
								}

							}
						}
						inWalls = false;

						playerTurn = true;

						//resetting the arrays so that the attack can run again
						for (int i = 0; i < 15; i++) {
							wallHeight [i]= (int) (Math.random() * 20 + 15);
							wallX [i] = (i * -175) - 70;
						}

						Arrays.sort(wallX);
						attackNum++;

						playerScreen = 0;
						player.moveDown = false;
						player.moveUp = false;
						player.moveRight = false;
						player.moveLeft = false;
						player.speed = 2;
					}
					else {
						//This is so that the loop doesn't run too many times, wasting resources
						try {
							sleep(3000);
						}
						catch (InterruptedException e){	
						}
					}
				}
			}
		}
	}

	//This attack has balls slowly moving from the top of the player area to the bottom of it.
	//if the player touches the projectiles, they take damage
	//The balls are stored in an array
	public class ballAttack extends Thread {
		public void run() {
			while (true) {
				while (true) {
					if (inBall && !playerTurn && screen == 1) {
						if (bossHealth == 0) {
							inBall = false;
							screen = 3;
							playerScreen = 0;
							break;
						}
						//this stops drawing the balls at a certain y point
						while (ballY[0] < 287) {

							for (int i = 0; i < 20; i++) {
								ballY[i] += 3;
								if (!inBall)
									break;
								//this checks if the player is colliding with the balls
								if (player.rect.intersects(new Rectangle(ballX[i], ballY[i], 23, 23))) {
									


									if (!player.immune && shield < 1) {
										player.damage(20);
										player.immune = true;
									}
									else if (shield > 0 && !player.immune){
										shield--;
										player.immune = true;
									}
									if (player.health == 0 || bossHealth == 0) {
										
										inBall = false;
										screen = 3;
										playerScreen = 0;
										break;
									}
								}
								try {
									sleep(1);
								}
								catch (InterruptedException e){	
								}
							}
						}
						inBall = false;
						playerTurn = true;
						attackNum++;
						//resetting the arrays so that the attack can run again
						for (int i = 0; i < 20; i++) {
							ballX [i] = (int)(Math.random()* 460) + 54;
							ballY [i] = (int)(Math.random() * -700 - 50);
						}
						//sorting the arrays so that loops run properly
						Arrays.sort(ballY);

						playerScreen = 0;
						player.moveDown = false;
						player.moveUp = false;
						player.moveRight = false;
						player.moveLeft = false;
						player.speed = 2;
					}
					else {


						try {
							sleep (3000);
						}
						catch (InterruptedException e) 
						{
						}
					}
				}
			}
		}
	}

	//The player attack thread runs based on a boolean flag, inPlayerAttack
	//When inPlayerAttack == true, the attack bar will be animated, moving across the screen from right to left
	//During this time, the user can click Z to confirm the position of their attack
	//The closer it is to the center of the screen, the more damage it does
	//if the player does not attack, the attack damage multiplier is set to the minimum
	public class playerAtk extends Thread{
		public void run() {
			while (true) {
				if (inPlayerAttack) {
					firstAttackClicked = false;
					while (atkBar.x > -20) {
						atkBar.x--;
						repaint();
						try {
							sleep(3);
						} 
						catch (InterruptedException e) 
						{
						}

					}
					if (!firstAttackClicked) {
						atkMultiplier = doDamage(0);
						damageDone = (int) (Math.round(Math.random() * 10) + playerAtkDmg * atkMultiplier);
						bossHealth = Math.max(0,  bossHealth - damageDone);


					}

					inPlayerAttack = false;
					playerScreen = 1;
					repaint();
					playerTurn = false;
				}
				else {
					atkBar.x = 800;
					try {
						sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	public class immunityReset extends Thread {
		public void run() {
			while (true) {
				if (player.immune) {
					try {
						sleep(250);
					} catch (InterruptedException e) {
					}
					player.immune = false;

				}
				else {
					try {

						sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	/*
	 * Description: this method takes the x value that is retrieved by the player attack bar, and converts it into damage. 
	 * The closer the bar is to the center, the more damage it will do
	 * Parameters: int x
	 * returns: 
	 */
	public double doDamage (int x) { // TODO rewrite to shorten
		double multiplier;
		if (x >= 285 && x <= 332) {
			multiplier = 2.5;
		}
		else if (x <= 394 && x > 332 || x < 285 && x >= 223) {
			multiplier = 1.5;
		}
		else if (x > 394 && x < 458 || x < 223 && x >= 157) {
			multiplier = 1;
		}
		else {
			multiplier = 0.8;
		}
		return multiplier;
	}

	public void setItemButtonsVisible(boolean flag) {
		for (JButton b : itemMenuButtons) {
			b.setVisible(flag);
		}
	}

	public void actionPerformed (ActionEvent event)
	{
		Graphics g = getGraphics();
		String eventName = event.getActionCommand ();
		if (screen == 0) { //buttons in title screen
			//activated by the play button in the title screen
			if (eventName.equals("playBtn")) {
				play.setVisible(false);
				aboutUs.setVisible(false);
				exit.setVisible(false);
				instruct.setVisible(false);
				level1.setVisible(true);
				level2.setVisible(true);
				levelLabel.setVisible(true);
				screen = 2;	
				repaint();
			}
			//activated by the about us button
			else if (eventName.equals("aboutUsBtn")) {
				inAboutUs = true;
				play.setVisible(false);
				aboutUs.setVisible(false);
				exit.setVisible(false);
				instruct.setVisible(false);
				aboutUsLabel.setVisible(true);
				repaint();

			}
			//exits the program
			else if (eventName.equals("exitBtn")) {
				frame.dispose();
				System.exit(0);
			}
		}
		else if (screen == 2) { //level display screen
			if (eventName.equals("level1Btn")) { //runs the first level
				level1.setVisible(false);
				level2.setVisible(false);
				levelLabel.setVisible(false);
				fightButton.setVisible(true);
				itemButton.setVisible(true);
				quitButton.setVisible(true);
				playerHealth.setVisible(true);
				inFist = false;
				inKnives = false;
				inSpaceship = false;
				inWalls = false;
				inBall = false;
				bossHealth = 800;
				maxBossHealth = 800.0;
				attackNum = 0;
				levelChoice = 0;
				screen = 1;	
				playerScreen = 1;
				
				clearScreen(g);
				repaint();
			}
			else if (eventName.equals("level2Btn")) { //runs the second level
				level1.setVisible(false);
				level2.setVisible(false);
				levelLabel.setVisible(false);
				fightButton.setVisible(true);
				itemButton.setVisible(true);
				quitButton.setVisible(true);
				playerHealth.setVisible(true);
				inFist = false;
				inKnives = false;
				inSpaceship = false;
				inWalls = false;
				inBall = false;
				bossHealth = 1200;
				maxBossHealth = 1200.0;
				attackNum = 0;
				levelChoice = 1;
				screen = 1;	
				playerScreen = 1;
				clearScreen(g);
				repaint();
			}
		}
		else if (screen == 1) { //in game buttons
			if (eventName.equals("quit") && playerTurn) { //quits the fight screen
				
				fightScreenButtons[buttonSelected].setForeground(Color.WHITE);
				inFist = false;
				inKnives = false;
				inSpaceship = false;
				inWalls = false;
				inBall = false;
				buttonSelected = 0;
				playerScreen = 0;
				screen = 0;
				setItemButtonsVisible(false);
				play.setVisible(true);
				aboutUs.setVisible(true);
				exit.setVisible(true);
				instruct.setVisible(true);
				fightButton.setVisible(false);
				itemButton.setVisible(false);
				quitButton.setVisible(false);
				playerHealth.setVisible(false);
				playerTurn = false;
				repaint();
				player.health = 100;
				bossHealth = (int) maxBossHealth;
				player.setDefaultPos();
			}

			if (playerScreen == 0) {
				if (eventName.equals("fight") && playerTurn) { // activates player fight
					
					fightScreenButtons[buttonSelected].setForeground(Color.WHITE);
					buttonSelected = 0;
					inPlayerAttack = true;
					playerScreen = 3;
					player.setDefaultPos();
				} else if (eventName.equals("item") && playerTurn) { // activates item menu
			
					fightScreenButtons[buttonSelected].setForeground(Color.WHITE);
					buttonSelected = 1;
					playerScreen = 2;
					for (JButton b : itemMenuButtons) {
						b.setVisible(true);
					}
					player.setDefaultPos();
				}
			} else if (playerScreen == 2)

				if (eventName.equals("full health")) { // full health potion: restores all health
		
					player.heal(100);
					playerHealth.setText("" + player.health);
					setItemButtonsVisible(false);
					playerScreen = 1;
					playerTurn = false;
					repaint();
				} else if (eventName.equals("partial health")) { // restores 30 health

					player.heal(30);
					playerHealth.setText("" + player.health);
					setItemButtonsVisible(false);
					playerScreen = 1;
					playerTurn = false;
					repaint();
				} else if (eventName.equals("shield")) { // shields the player from the next instance of damage

					setItemButtonsVisible(false);
					playerScreen = 1;
					shield = 1;
					playerTurn = false;
					repaint();
				} else if (eventName.equals("speed")) { // gives extra speed to the player

					player.speed = 4;
					setItemButtonsVisible(false);
					playerScreen = 1;
					playerTurn = false;
					repaint();
				}


		}
		else if (screen == 3) {
			if (eventName.equals("endExitBtn")) { // exits the program
				frame.dispose();
				System.exit(0);
			}
			else if (eventName.equals("playAgainBtn")) { // goes back to the title screen
				screen = 0;
				playerScreen = 0;
				playAgainButton.setVisible(false);
				endExitButton.setVisible(false);
				endLabel.setVisible(false);
				play.setVisible(true);
				aboutUs.setVisible(true);
				exit.setVisible(true);
				instruct.setVisible(true);
				attackNum = 0;
				player.health = 100;
				inFist = false;
				inKnives = false;
				inSpaceship = false;
				inWalls = false;
				inBall = false;
				buttonSelected = 2;
				playerScreen = 0;
				screen = 0;
				playerTurn = false;
				repaint();
			}
		}
	}

	// MouseListener methods
	public void mouseClicked (MouseEvent e) {
		int x, y;
		x = e.getX ();
		y = e.getY ();

		// deselects buttons if mouse clicked on area other than button
		fightScreenButtons[buttonSelected].setForeground(Color.WHITE);

	}

	public void mouseReleased (MouseEvent e) {
	}

	public void mouseEntered (MouseEvent e) {
	}

	public void mouseExited (MouseEvent e) {
	}

	public void mousePressed (MouseEvent e) {
	}

	// KeyListener methods
	public void keyPressed (KeyEvent kp) {
		int keypress = kp.getKeyCode();

		if (screen == 0) {
			//this cycles upwards through the main menu buttons
			if (keypress == KeyEvent.VK_UP) {
				if (menuChoice > 1) {
					menuChoice --;
				}
				else {
					menuChoice = 3;
				}

			}
			//this cycles downwards through the main menu buttons
			else if (keypress == KeyEvent.VK_DOWN) { 
				if (menuChoice < 3) {
					menuChoice ++;
				}
				else 
					menuChoice = 1;

			}
			//allows for I to display Instructions
			if(keypress == KeyEvent.VK_I && !displayInstructions) {
				displayInstructions = true;
				play.setVisible(false);
				aboutUs.setVisible(false);
				exit.setVisible(false);
				instruct.setVisible(false);
				repaint();
			}
			
			else if (keypress == KeyEvent.VK_Z && displayInstructions) {
				instructPage = 1;
			}
			//Acts as a back button from the instruction view
			else if (keypress == KeyEvent.VK_X && displayInstructions) {
				displayInstructions = false;
				play.setVisible(true);
				aboutUs.setVisible(true);
				exit.setVisible(true);
				instruct.setVisible(true);
				instructPage = 0;
				repaint();
			}

			//allows for z to be used as an activation key for the buttons
			else if(keypress == KeyEvent.VK_Z && screen == 0) {
				if (menuChoice == 1) {

					play.doClick();
				}
				else if (menuChoice == 2) {
					aboutUs.doClick();
				}
				else if (menuChoice == 3) {
					exit.doClick();
				}
				repaint();
			}

			//this allows the X key to be used as a back button from the about us screen
			if (inAboutUs && keypress == KeyEvent.VK_X) {
				inAboutUs = false;
				aboutUsLabel.setVisible(false);
				play.setVisible(true);
				aboutUs.setVisible(true);
				exit.setVisible(true);
				instruct.setVisible(true);
			}
			//the following lines change the color of the selected button
			if (menuChoice == 1) {
				play.setForeground(Color.ORANGE);
				aboutUs.setForeground(Color.WHITE);
				exit.setForeground(Color.WHITE);
				play.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
				aboutUs.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
				exit.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
			}
			else if (menuChoice == 2) {
				aboutUs.setForeground(Color.ORANGE);
				exit.setForeground(Color.WHITE);
				play.setForeground(Color.WHITE);
				play.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
				exit.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
				aboutUs.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
			}
			else if (menuChoice == 3) {
				exit.setForeground(Color.ORANGE);
				aboutUs.setForeground(Color.WHITE);
				play.setForeground(Color.WHITE);
				play.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
				aboutUs.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
				exit.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
			}
			repaint();
		}
		//draws the buttons a different color based on the selection
		//This can be toggled with left and right arrow keys
		else if (screen == 2) {
			if (keypress == KeyEvent.VK_LEFT || keypress == KeyEvent.VK_RIGHT) {
				if (levelChoice == 0) {
					level2.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
					level2.setForeground(Color.ORANGE);
					level1.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
					level1.setForeground(Color.WHITE);

					levelChoice = 1;
				}
				else if (levelChoice == 1){
					levelChoice = 0;
					level2.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
					level2.setForeground(Color.WHITE);
					level1.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
					level1.setForeground(Color.ORANGE);
				}

			}

			//within the level selection screen, if z is pressed, the level will be selected
			if(keypress ==KeyEvent.VK_Z) {
				if (levelChoice == 0) {

					level1.doClick();
				}
				
				else if (levelChoice == 1) {
					level2.doClick();
				}

				repaint();
			}

			// this allows x to be used as a back key in the level selection. If x is pressed here, it will return the user to the main menu
			else if (keypress == KeyEvent.VK_X) {
				screen = 0;
				level1.setVisible(false);
				level2.setVisible(false);
				levelLabel.setVisible(false);
				play.setVisible(true);
				aboutUs.setVisible(true);
				exit.setVisible(true);
				instruct.setVisible(true);
				repaint();
			}
		}
		// arrow keys will cycle through the selected button
		else if (screen == 1) {
			if (keypress == KeyEvent.VK_Z && inPlayerAttack) {
				if (!firstAttackClicked) {
					firstAttackClicked = true;
					if (atkBar.x > -20) {
						atkMultiplier = doDamage(atkBar.x);
						damageDone = (int) (Math.round(Math.random() * 10) + playerAtkDmg * atkMultiplier);
						bossHealth = Math.max(0,  bossHealth - damageDone);

						playerScreen = 1;
						playerTurn = false;
						repaint();
					}



				}
			}

			//this cycles through the button selection when fighting
			if (playerScreen == 0) { // button select
				if (keypress == KeyEvent.VK_LEFT) {
					fightScreenButtons[buttonSelected].setForeground(Color.WHITE);
					buttonSelected -= 1;
					if (buttonSelected < 0) buttonSelected = 2;
					fightScreenButtons[buttonSelected].setForeground(Color.YELLOW);
				} else if (keypress == KeyEvent.VK_RIGHT) {
					fightScreenButtons[buttonSelected].setForeground(Color.WHITE);
					buttonSelected = (buttonSelected + 1) % 3;
					fightScreenButtons[buttonSelected].setForeground(Color.YELLOW);
				} else if ((keypress == KeyEvent.VK_Z) && playerTurn) {
					fightScreenButtons[buttonSelected].doClick();
				}

			} else if (playerScreen == 1 && !playerTurn) { // currently fighting

				//this triggers the movement boolean flags, allowing the character to move
				if (keypress == KeyEvent.VK_LEFT) {
					player.moveLeft = true;

				} else if (keypress == KeyEvent.VK_RIGHT) {
					player.moveRight = true;
				} else if (keypress == KeyEvent.VK_UP) {
					player.moveUp = true;
				} else if (keypress == KeyEvent.VK_DOWN) {
					player.moveDown = true;
				}

			}else if (playerScreen == 2) { // item menu
				//The following lines allow for the toggle between the selection of item to use
				if (keypress == KeyEvent.VK_LEFT) {
					itemMenuButtons[itemMenuButtonSelected].setForeground(Color.WHITE);
					itemMenuButtonSelected -= 1;
					if (itemMenuButtonSelected < 0) itemMenuButtonSelected = 3;
					itemMenuButtons[itemMenuButtonSelected].setForeground(Color.YELLOW);
				} else if (keypress == KeyEvent.VK_RIGHT) {
					itemMenuButtons[itemMenuButtonSelected].setForeground(Color.WHITE);
					itemMenuButtonSelected = (itemMenuButtonSelected + 1) % itemMenuButtons.length;
					itemMenuButtons[itemMenuButtonSelected].setForeground(Color.YELLOW);
				} else if (keypress == KeyEvent.VK_ENTER || keypress == KeyEvent.VK_Z) {
					itemMenuButtons[itemMenuButtonSelected].doClick();
				} else if (keypress == KeyEvent.VK_X) {
					playerScreen = 0;
					setItemButtonsVisible(false);
				}
			}
		}
		else if (screen == 3) {
			if (keypress == KeyEvent.VK_LEFT || keypress == KeyEvent.VK_RIGHT) {
				if (endScreenChoice == 0) {
					endExitButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
					endExitButton.setForeground(Color.ORANGE);
					playAgainButton.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
					playAgainButton.setForeground(Color.WHITE);

					endScreenChoice = 1;
				}
				else if (endScreenChoice == 1){
					endScreenChoice = 0;
					endExitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE,4));
					endExitButton.setForeground(Color.WHITE);
					playAgainButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE,4));
					playAgainButton.setForeground(Color.ORANGE);
				}

			}if(keypress ==KeyEvent.VK_Z) {
				if (endScreenChoice == 0) {

					playAgainButton.doClick();
				}
				else if (endScreenChoice == 1) {
					endExitButton.doClick();
				}

				repaint();
			}
		}
	}

	public void keyReleased (KeyEvent e) {
		int keypress = e.getKeyCode();
		//sets the movement boolean flags to false when the movement keys are released
		if (playerScreen == 1) {
			if (keypress == KeyEvent.VK_LEFT) {
				player.moveLeft = false;
			} else if (keypress == KeyEvent.VK_RIGHT) {
				player.moveRight = false;
			} else if (keypress == KeyEvent.VK_UP) {
				player.moveUp = false;
			} else if (keypress == KeyEvent.VK_DOWN) {
				player.moveDown = false;
			}
		}
	} 


	//The primary purpose for this thread is to run the enemy attacks
	//It stores the enemy attack patterns, and sets an attack boolean to true based on the variable
	//attackNum, which stores the amount of attacks done
	public class game extends Thread {
		public void run () {
			while (true) {

				if (playerScreen == 1 && !playerTurn) {

					if (levelChoice == 0) {

						//Attack pattern for level 1 boss
						//calls enemy attacks in a specific order, with a variable that increments each time an attack is called
						try {
							sleep (500);
						} catch (InterruptedException e) {
						}
						if (attackNum % 5 == 0) {
							movementType = 1;
							inFist = true;
						}
						else if (attackNum % 5 == 1) {
							movementType = 0;
							inKnives = true;

						}

						else if (attackNum % 5 == 2) {
							movementType = 1;
							inWalls = true;

						}
						else if (attackNum % 5 == 3) {
							inFist = true;
						}
						else if (attackNum % 5 == 4) {
							inWalls = true;
						}

						repaint();


					}
					else if (levelChoice == 1) {
						//Attack pattern for level 2 boss

						try {
							sleep (500);
						} catch (InterruptedException e) {
						}
						if (attackNum % 7 == 0) {
							movementType = 0;
							inSpaceship = true;
						}
						else if (attackNum % 7 == 1) {
							movementType = 1;
							inFist = true;
						}
						else if (attackNum % 7 == 2) {
							movementType = 0;
							inBall = true;
						}
						else if (attackNum % 7 == 3) {
							movementType = 1;
							inWalls = true;
						}
						else if (attackNum % 7 == 4) {
							movementType = 0;
							inSpaceship = true;
						}
						else if (attackNum % 7 == 5) {
							movementType = 0;
							inKnives = true;
						}
						else if (attackNum % 7 == 6) {
							movementType = 0;
							inBall = true;
						}

						repaint();


					}
				}
				else {
					try {
						sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	public void keyTyped (KeyEvent e) {
	}
	public static void main(String[] args) {
		new Sustale();
	}
}