package project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class new_Main extends JFrame {
	private Image bufferImage;
	private Graphics screenGraphic;
	
	private Image background = new ImageIcon("src/images/HomeBackground.png").getImage();
	private Image bobber = new ImageIcon("src/images/bobber.png").getImage();
	private Image life = new ImageIcon("src/images/life.png").getImage();
	private int bobberX = 277, bobberY;
	private int bobberWidth = bobber.getWidth(null);
	private int bobberHeight = bobber.getHeight(null);

	private ArrayList<new_Fish> fishs = new ArrayList<new_Fish>();
	private int fish_cnt;
	private int life_cnt;
	private int score;
	private boolean catching;
	
	private boolean up, down, space;
	private int enter=0;
	
	public new_Main(){
		setTitle("Fishing Mania");
		setSize(530, 800);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((windowSize.width - frameSize.width) / 2,(windowSize.height - frameSize.height) / 2); //ȭ?? ?߾ӿ? ??????
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        
        
		addKeyListener(new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
					if (enter == 0) {
						enter = 1;
						background = new ImageIcon("src/images/GameBackground.png").getImage();
					}
					
					break;
				case KeyEvent.VK_UP:
					up = true;
					break;
				case KeyEvent.VK_DOWN:
					down = true;
					break;
				case KeyEvent.VK_SPACE:
					space = true;
					break;
				}
			}
			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					up = false;
					break;
				case KeyEvent.VK_DOWN:
					down = false;
					break;
				case KeyEvent.VK_SPACE:
					space = false;
					break;
				}
			}
		});
		
		init();	// ???? ?ʱ?ȭ
		
		while(true) {
			try {
				Thread.sleep(20);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			keyProcess();
			addFish();
			catchedCheck();
			scoreCheck();
			fishCntCheck();
			lifeCheck();
		}
        
	}
	
	public void init() {
		this.score = 0;
		this.life_cnt = 3;
		this.bobberY = 200;
		this.fish_cnt = 3;
		this.catching = false;
	}

	public void keyProcess() {
		if (up && bobberY - 3 > 130) bobberY-=3;
		if (down && bobberY + bobberHeight + 3 < 785) bobberY+=3;
		if (space && bobberY - 10 > 130) bobberY-=10;
	}
	
	public void addFish() {
		for (int i = fishs.size(); i<fish_cnt; i++) {
			fishs.add(new new_Fish());
		}
	}
	
	public void fishCntCheck() {
		if (score == 0) {
			fish_cnt = 3;
		}else {
			fish_cnt = 3 + (score / 50); 
		}
	}
	
	public void catchedCheck() {
		
		for (int i = 0; i<fishs.size(); i++) {
			new_Fish fish = fishs.get(i);
			
			if(fish.getCatched() == true) {
				fish.setX(bobberX+10);
				fish.setY(bobberY+10);
			}else{
				if(fish.crashCheck(bobberX, bobberY, bobberWidth, bobberHeight, catching)){
					this.catching = true;
				}
			}
		}
	}
	
	public void scoreCheck() {
		for(int i=0; i<fishs.size(); i++) {
			new_Fish fish = fishs.get(i);
			
			if(fish.getCheck()) {
				this.score = this.score + fish.getScore();
				if(fish.getScore()<0) {
					life_cnt-=1;
				}

				fishs.remove(i);
				--i;
				catching = false;
				
			}
		}
	}
	
	public void lifeCheck() {
		if(life_cnt == 0) {
			background = new ImageIcon("src/images/GameoverBackground.png").getImage();
			for (int i = 0; i<fishs.size(); i++) {
				fishs.get(i).stop();
			}
			fishs.clear();
			fish_cnt = 0;
		}
	}
	
	public void paint(Graphics g) {
		bufferImage = createImage(530, 800);
		screenGraphic = bufferImage.getGraphics();
		screenDraw(screenGraphic);
		g.drawImage(bufferImage, 0, 0, null);
	}
	public void screenDraw(Graphics g) {
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawImage(background, 0, 0, null);
		
		if (enter == 0) {
        	g.drawString("< PRESS ENTER >", 180, 400);
			
		}else if (enter==1){
			for(int i = 0; i<life_cnt; i++) {
				g.drawImage(life, 20+(40*i), 60, null);
			}
			for(int i = 0; i<fishs.size(); i++) {
				new_Fish afish = fishs.get(i);
				g.drawImage(afish.getImage(), afish.getX(), afish.getY(), null);
			}
			g.drawImage(bobber, bobberX, bobberY, null);
			g.setColor(Color.GRAY);
	        g.drawLine(287, 100, 287, bobberY);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("SCORE : " + score, 20, 55);
		}else if (enter == 2) {
			g.drawString("SCORE : " + score, 180, 400);
		}
		this.repaint();
	}
	
	
	public static void main(String[] args) {
        new new_Main();
    }
}

class new_Fish{
	Random random = new Random();
	private int x = 550, y = random.nextInt(400)+300;
	private int speed;
	private Image fish;
	private int score;
	private int width;
	private int height;
	private int num;
	private Thread move;
	private boolean catched = false;
	private boolean get_score = false;
	
	public new_Fish(){
		num = random.nextInt(8);
		
		ArrayList<Image> fishs = new ArrayList<Image>() {{
			add(new ImageIcon("src/images/10.png").getImage());
			add(new ImageIcon("src/images/15.png").getImage());
			add(new ImageIcon("src/images/20.png").getImage());
			add(new ImageIcon("src/images/25.png").getImage());
			add(new ImageIcon("src/images/30.png").getImage());
			add(new ImageIcon("src/images/35.png").getImage());
			add(new ImageIcon("src/images/-30.png").getImage());
			add(new ImageIcon("src/images/-50.png").getImage());
		}};
		int scores[] = {10, 15, 20, 25, 30, 35,-30, -50};
		int speeds[] = {13, 13, 11, 10, 6, 6, 11,  8 };
		
		this.speed = speeds[num];
		this.fish = fishs.get(num);
		this.score = scores[num];
		this.width = fish.getWidth(null);
		this.height = fish.getHeight(null);

		this.move = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(speed);
					}catch(InterruptedException e){
					}
					
					if(catched) {
						HeightCheck();
					}else {
						x--;
						
						if(x==-100) {
							x = 600;
							y = random.nextInt(400)+300;
							
							num = random.nextInt(8);
							
							ArrayList<Image> fishs = new ArrayList<Image>() {{
								add(new ImageIcon("src/images/10.png").getImage());
								add(new ImageIcon("src/images/15.png").getImage());
								add(new ImageIcon("src/images/20.png").getImage());
								add(new ImageIcon("src/images/25.png").getImage());
								add(new ImageIcon("src/images/30.png").getImage());
								add(new ImageIcon("src/images/35.png").getImage());
								add(new ImageIcon("src/images/-30.png").getImage());
								add(new ImageIcon("src/images/-50.png").getImage());
							}};
							int scores[] = {10, 15, 20, 25, 30, 35,-30, -50};
							int speeds[] = {15, 15, 13, 12, 8, 8, 13,  10 };
							
							speed = speeds[num];
							fish = fishs.get(num);
							score = scores[num];
							width = fish.getWidth(null);
							height = fish.getHeight(null);
						}
					}
					
				}
			}
		};
		this.move.start();
	}

	public boolean crashCheck(int bobberX, int bobberY, int bobberWidth, int bobberHeight, boolean catching) {
		if (num == 7) {
			if (bobberX + bobberWidth > x && x + width > bobberX && bobberY + bobberHeight > y && 100 < y) {
				get_score = true;	
			}
		}else {
			if (bobberX + bobberWidth > x && x + width > bobberX && bobberY + bobberHeight > y && y + height > bobberY && catched == false && catching == false) {
				this.catched = true;
				this.move.interrupt();
				return true;
			}
		}
		return catched;
	}
	
	public void HeightCheck() {
		if(y<150) {
			get_score = true;
		}
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
	public boolean getCheck() {
		return get_score;
	}
	public int getScore() {
		return score;
	}
	public Image getImage() {
		return fish;
	}
	
	public boolean getCatched() {
		return catched;
	}
	
	public void stop() {
		this.move.interrupt();
	}
}