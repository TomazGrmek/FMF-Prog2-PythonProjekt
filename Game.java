package prog2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import prog2.Game.Food;
import prog2.Game.Snake;

public class Game {
	private final static int WIDTH = 800;
	private final static int HEIGHT = 800;
	private static final int WIDTH_GRID = WIDTH/20-1;
	private static final int HEIGHT_GRID = HEIGHT/20-1;
	private GUI GUI;
	static boolean paused = false;
	static int score = 0;

	public static void main(String[] args) {
		Snake snake = new Snake();
		Food food = new Food(snake);
		GUI frame = new GUI(snake, food, score);
		boolean eat = false;
		boolean eat2 = false;
		int check;
		
		frame.pack();
		frame.setVisible(true);

		
		while(true) {
			
			if(paused) {
				try {
					frame.addKeyListener(new KeyAdapter() {

						@Override
						public void keyPressed(KeyEvent e) {
							int key = e.getKeyCode();
							
				            if (key == KeyEvent.VK_SPACE) {
				            	paused = false;
				            }
				           
				            
						}
					});
					if(paused) {
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
			}
			else {
			
			
			frame.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					
					if ((key == KeyEvent.VK_LEFT) && (snake.getDirection() != 0 )) {
						snake.setDirection(2);
		            }

		            if ((key == KeyEvent.VK_RIGHT) && (snake.getDirection() != 2 )) {
		            	snake.setDirection(0);
		            }

		            if ((key == KeyEvent.VK_UP) && (snake.getDirection() != 1)) {
		            	snake.setDirection(3);
		            }

		            if ((key == KeyEvent.VK_DOWN) && (snake.getDirection() != 3)) {
		            	snake.setDirection(1);
		            }
		            if ( key == KeyEvent.VK_SPACE ) {
		            	paused = true;
		            }
		            
				}

			      
			});
			check = snake.checkCollision(food);
			if(check == 1) {
				break;
			}
			else if (check == 2) {
				eat = true;
				eat2 = false;
				score += 1;
			}
			else if (check == 3) {
				eat2 = true;
				eat = false;
				score += 2;
			}
			else {
				eat = false;
				eat2 = false;
			}
			if(food.numElements() == 0) {
				food.addElements(snake);
			}
			if(food.numElementsDouble() == 0) {
				food.addElementsDouble(snake);
			}
			frame.top.setScore(score);
			snake.move(eat,eat2);
			frame.repaint();
			
			
			
			
			try {
				Thread.sleep(frame.getSpeed());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			}

	}
	
	
	

	public static class Snake{
		
		protected List<Point> coordinates;
		private Point[] directions = {new Point(1,0), new Point(0,1), new Point(-1,0), new Point(0,-1)};
		private int direction = 0;
	
		public Snake() {
			this.coordinates = new ArrayList<Point>();
			int head_x = 3 + (int)(Math.random() * (WIDTH_GRID-10));
			int head_y = (int)(Math.random() * (HEIGHT_GRID));
			this.coordinates.add(new Point(head_x,head_y));
			for (int i= 0; i < 3; i++) {
				Point last_el = this.coordinates.get(this.coordinates.size()-1);
				this.coordinates.add(new Point(last_el.getX() + (-1)*directions[direction].getX(),last_el.getY() + (-1)*directions[direction].getY()));
			}
			


		}
		public void move(boolean eat, boolean eat2) {
			Point last_el;
			Point last_el2;
			if(eat) {
				last_el = this.coordinates.get(this.coordinates.size()-1);
				for (int i = this.coordinates.size()-1; i > 0; i--) {
					Point el = this.coordinates.get(i-1);
					this.coordinates.set(i, new Point(el.getX() , el.getY()));
					}
				this.coordinates.add(last_el);
			}
			else if(eat2) {
				last_el = this.coordinates.get(this.coordinates.size()-1);
				last_el2 = this.coordinates.get(this.coordinates.size()-2);
				for (int i = this.coordinates.size()-1; i > 0; i--) {
					Point el = this.coordinates.get(i-1);
					this.coordinates.set(i, new Point(el.getX() , el.getY()));
					}
				this.coordinates.add(last_el2);
				this.coordinates.add(last_el);
				
			}
			else {
				for (int i = this.coordinates.size()-1; i > 0; i--) {
					Point el = this.coordinates.get(i-1);
					this.coordinates.set(i, new Point(el.getX() , el.getY()));
					}
			}
		
			Point el = this.coordinates.get(0);
			this.coordinates.set(0, new Point(el.getX() + directions[direction].getX(), el.getY() + directions[direction].getY()));
		}
		public void setDirection(int i) {
			if(i>=0 && i<=3) {
				this.direction = i;
			}
		}
		public int getDirection() {
			return direction;
		}
		public int checkCollision(Food food) {
			//0 - ni trkov
			//1 - trk s steno ali s sabo
			//2 - trk s hrano
			//3 - trk s hrano za 2
			Point head = this.coordinates.get(0);
			if(head.getX() > WIDTH_GRID || head.getX() < 0) {
				return 1;
			}
			if(head.getY() > HEIGHT_GRID || head.getY() < 0) {
				return 1;
			}
			for (int i = 1; i < this.coordinates.size()-1;i++) {
				if(head.equals(this.coordinates.get(i))) {
					return 1;
				}
			}
			for(int i = 0; i< food.coordinates.size();i++) {
				if(food.coordinates.get(i).getX() == head.getX() && food.coordinates.get(i).getY() == head.getY()) {
					food.coordinates.remove(i);
					return 2;
				}
			}
			for(int i = 0; i< food.coordinates_double.size();i++) {
				if(food.coordinates_double.get(i).getX() == head.getX() && food.coordinates_double.get(i).getY() == head.getY()) {
					food.coordinates_double.remove(i);
					return 3;
				}
			}
			return 0;
		}
		
		
	}
	
public static class Food{
	protected List<Point> coordinates;
	protected List<Point> coordinates_double;
	
	public Food(Snake snake) {
		this.coordinates = new ArrayList<Point>();
		this.coordinates_double = new ArrayList<Point>();
		
		while(this.coordinates.size()<5) {
			Point new_point = new Point((int)(Math.random() * (WIDTH_GRID)), (int)(Math.random() * (HEIGHT_GRID)));
			if(!snake.coordinates.contains(new_point)) {
				this.coordinates.add(new_point);
				//System.out.println(new_point.getX()+" "+new_point.getY());
			}
		}
		while(this.coordinates_double.size()<2) {
			Point new_point = new Point((int)(Math.random() * (WIDTH_GRID)), (int)(Math.random() * (HEIGHT_GRID)));
			if(!snake.coordinates.contains(new_point) && !this.coordinates.contains(new_point)) {
				this.coordinates_double.add(new_point);
			}
		}
	}
	public int numElements(){
		return this.coordinates.size();
	}
	public int numElementsDouble(){
		return this.coordinates_double.size();
	}
	public void addElements(Snake snake){
		while(this.coordinates.size()<5) {
			Point new_point = new Point((int)(Math.random() * (WIDTH_GRID)), (int)(Math.random() * (HEIGHT_GRID)));
			if(!snake.coordinates.contains(new_point) && !this.coordinates_double.contains(new_point)) {
				this.coordinates.add(new_point);

			}
		}
	}
	public void addElementsDouble(Snake snake){
		while(this.coordinates_double.size()<2) {
			Point new_point = new Point((int)(Math.random() * (WIDTH_GRID)), (int)(Math.random() * (HEIGHT_GRID)));
			if(!snake.coordinates.contains(new_point) && !this.coordinates.contains(new_point)) {
				this.coordinates_double.add(new_point);

			}
		}
	}
	
	}

}




class GUI extends JFrame {
	  
	  
	  private static final long serialVersionUID = 1L;
	  TopPanel top;
	  JMenuBar menuBar;
	  private int speed = 170;
	  

	  public GUI(Snake snake, Food food, int score) {
	    super();
	    
	    
	    setTitle("Snake");
	    setPreferredSize(new Dimension(800, 800));
	    setMinimumSize(new Dimension(800, 800));
	    setLayout(new BorderLayout());
	    setResizable(false);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    top = new TopPanel(score);
	    add(new Panel(snake, food), BorderLayout.CENTER);
	    add(top , BorderLayout.NORTH);
	    
	    menuBar = new JMenuBar();
	    
	    JMenu menu = new JMenu("Zahtevnost");
	    ButtonGroup group = new ButtonGroup();
	    JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Lahka");
	    rbMenuItem.addActionListener(new ActionListener() {
	        
	        @Override
	        public void actionPerformed(ActionEvent e) {
	          speed = 250;
	        }
	        
	    });
	    group.add(rbMenuItem);
	    menu.add(rbMenuItem);
	    rbMenuItem = new JRadioButtonMenuItem("Normalna");
	    rbMenuItem.setSelected(true);
	    rbMenuItem.addActionListener(new ActionListener() {
	        
	        @Override
	        public void actionPerformed(ActionEvent e) {
	          speed = 170;
	        }
	        
	    });
	    group.add(rbMenuItem);
	    menu.add(rbMenuItem);
	    rbMenuItem = new JRadioButtonMenuItem("Težka");
	    rbMenuItem.addActionListener(new ActionListener() {
	        
	        @Override
	        public void actionPerformed(ActionEvent e) {
	          speed = 110;
	        }
	        
	    });
	    group.add(rbMenuItem);
	    menu.add(rbMenuItem);
	    menuBar.add(menu);
	    
	    setJMenuBar(menuBar);
	    

	}
	
	  public int getSpeed() {
		  return speed;
	  }

}

class Panel extends JPanel{
	private static final long serialVersionUID = 1L;
	private Snake snake;
	private Food food;
	
	public Panel(Snake snake,Food food) {
		super();
		this.snake = snake;
		this.food = food;
//		setPreferredSize(new Dimension(800, 800));
//	    setMinimumSize(new Dimension(800, 800));
	}
	
	@Override
	public void paint(Graphics g) {
		double w = getWidth() / 40.0;
		double h = getHeight() / 40.0;
		
		super.paint(g);
		Graphics2D graphics = (Graphics2D)g;
		for (int i = 0; i < snake.coordinates.size()-1; i++) {
			Point el = snake.coordinates.get(i);
			if(i == 0) {
				graphics.setColor(Color.GREEN);
			}
			else {
				graphics.setColor(Color.RED);
			}
			
			graphics.fillRect((int)Math.round(el.getX()*w)  , (int)Math.round(el.getY()*h), (int)Math.round(w), (int)Math.round(h));
			graphics.setColor(Color.BLACK);
			graphics.fillRect((int)Math.round(el.getX()*w) + 2  , (int)Math.round(el.getY()*h) + 2, (int)Math.round(w)-5, (int)Math.round(h)-5);
		}
		for (int i = 0; i < food.coordinates.size(); i++) {
			Point el = food.coordinates.get(i);
			graphics.setColor(Color.BLUE);
			graphics.fillRect((int)Math.round(el.getX()*w)  , (int)Math.round(el.getY()*h), (int)Math.round(w), (int)Math.round(h));
		}
		for (int i = 0; i < food.coordinates_double.size(); i++) {
			Point el = food.coordinates_double.get(i);
			graphics.setColor(Color.ORANGE);
			graphics.fillRect((int)Math.round(el.getX()*w)  , (int)Math.round(el.getY()*h), (int)Math.round(w), (int)Math.round(h));
		}
		
	}
}

class TopPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private int score;
	
	public TopPanel(int score) {
		super();
		this.score = score;
		setBackground(Color.BLACK);
	}
	public void setScore(int score) {
		this.score = score;
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800,40);
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int w = getWidth() / 2;
		int h = getHeight() / 2;
		g.setColor(Color.WHITE);
		g.setFont(new Font("Calibri", Font.BOLD, 25));
		g.drawString(""+ score, w, h+5);
	}
}
	  
class Point{
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean equals(Point p) {
		return this.x == p.getX() && this.y == p.getY();
	}

	
}


