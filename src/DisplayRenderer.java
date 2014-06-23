import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class DisplayRenderer extends JFrame implements Runnable {

	public static int imageWidth;
	public static int imageHeight;

	BufferedImage pixels;

	boolean action = false;
	boolean drawPixels = false;
	boolean fullScreen = false;
	//TEST
	//TEST

	private int circleWidth;
	private int circleHeight;

	private double centerX;
	private double centerY;

	private int topX;
	private int topY;
	//Test

	private ArrayList<Point> inscribedPixels;

	private Thread t;

	private Image offScreenImage;
	private Graphics offScreenBuffer;
	
	JFrame jFrame;
	GraphicsDevice gd;
	
	public DisplayRenderer() {
		super();
		
		addKeyListener(new myKeyListener());
		circleWidth = 76;
		circleHeight = 76;
		topX = 0;
		topY = 0;
		centerX = (double) circleWidth / 2 + topX;
		centerY = (double) circleHeight / 2 + topY;
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(imageWidth, imageHeight);
		
		imageWidth = size.width;
		imageHeight = size.height;

		pixels = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_ARGB);

		inscribedPixels = new ArrayList<Point>();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (isMacOSX()) {
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "Full Screen Demo");
        }
		if (isMacOSX()) {
            enableFullScreenMode(this);
        }
		
		
		setVisible(true); 

		start();

	}
	public static void enableFullScreenMode(Window window) {
        String className = "com.apple.eawt.FullScreenUtilities";
        String methodName = "setWindowCanFullScreen";
 
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, new Class<?>[] {
                    Window.class, boolean.class });
            method.invoke(null, window, true);
        } catch (Throwable t) {
            System.err.println("Full screen mode is not supported");
            t.printStackTrace();
        }
    }
	private static boolean isMacOSX() {
        return System.getProperty("os.name").indexOf("Mac OS X") >= 0;
    }

	public void start() {
		t = new Thread(this);
		t.start();
	}

	public void run() {
		while (true) {
			try {

				Thread.sleep(19);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}

	}
	

	public void paint(Graphics g) {

		if (offScreenImage == null) {

			offScreenImage = createImage(imageWidth, imageHeight);
		}
		g.drawImage(offScreenImage, 0, 0, this);

		// Draws the onscreen oval
		g.drawOval(topX, topY, circleWidth, circleHeight);
		g.setColor(Color.GREEN);
		g.fillOval(topX, topY, circleWidth, circleHeight);

		offScreenBuffer = offScreenImage.getGraphics();
		offScreenBuffer.setColor(Color.white);

		update(g);

	}

	@Override
	public void update(Graphics g) {

		centerX = (double) circleWidth / 2 + topX;
		centerY = (double) circleHeight / 2 + topY;

//		g.drawImage(offScreenImage, 0, 0, this);
//		g.drawOval(topX, topY, circleWidth, circleHeight);
//		g.setColor(Color.GREEN);
//		g.fillOval(topX, topY, circleWidth, circleHeight);
		
	}

	public void getCoveredPixels() {

		inscribedPixels.clear();

		double radius = circleWidth / 2;
		double dx, dy;
		double distanceSq;

		for (int x = topX; x <= circleWidth + topX; x++) {
			for (int y = topY; y <= circleHeight + topY; y++) {
				dx = x - centerX;
				dy = y - centerY;
				distanceSq = Math.pow(dx, 2) + Math.pow(dy, 2);

				if (distanceSq <= Math.pow(radius, 2)) {
					inscribedPixels.add(new Point(x, y));

					pixels.setRGB(x, y, 6535423);
				}

			}
		}
		int count = 1;

		System.out.println("\nNumber of inscribed pixels: "
				+ inscribedPixels.size());

		for (Point p : inscribedPixels) {
			System.out.println(count + "\t" + p.toString());
			count++;
		}
		System.out.println("Area of circle...ish: "
				+ (Math.PI * Math.pow(radius, 2)));

		System.out.println();
		
		Graphics g=offScreenImage.getGraphics();
		
		for (Point p : inscribedPixels) {
			if(p.x <= (circleWidth/2 + topX)){
				g.setColor(Color.RED);
				g.drawLine(p.x, p.y, p.x, p.y);
			}else if (p.x > (circleWidth/2 + topX)){
				g.setColor(Color.BLUE);
				g.drawLine(p.x, p.y, p.x, p.y);
			}
			
		}

	}

	private class myKeyListener extends KeyAdapter {

		public myKeyListener() {

		}

		@Override
		public void keyPressed(KeyEvent e) {

			switch (e.getKeyCode()) {

			case KeyEvent.VK_M:
				// m key
				if (!drawPixels) {
					drawPixels = true;
				} else {
					drawPixels = false;
				}

				getCoveredPixels();

				break;

			case KeyEvent.VK_E:
				// e key
				circleHeight = circleWidth;

				break;
			case KeyEvent.VK_N:
				// n key
				if(action){
					circleHeight++;
					circleWidth++;
				}

				break;
			case KeyEvent.VK_F:
				// f key
				if(!fullScreen){
					fullScreen = true;
				}else{
					fullScreen = false;
				}
				
				break;


			case KeyEvent.VK_P:
				// p key
				System.out.println("X-coordinate: " + centerX
						+ "\nY-coordinate: " + centerY + "\nHeight: "
						+ circleHeight + "\nWidth: " + circleWidth);

				break;
			case KeyEvent.VK_B:
				// b key
				circleHeight++;
				circleWidth++;

				break;

			case KeyEvent.VK_S:
				// s key
				if (!action) {
					action = true;
				} else {
					action = false;
				}

				break;
			case KeyEvent.VK_I:
				// i key
				if (!action) {
					topY -= 10;
				} else {
					circleHeight -= 10;
				}

				break;

			case KeyEvent.VK_J:
				// j key
				if (!action) {
					topX -= 10;
				} else {
					circleWidth -= 10;
				}

				break;
			case KeyEvent.VK_K:
				// k key
				if (!action) {
					topY += 10;
				} else {
					circleHeight += 10;
				}

				break;

			case KeyEvent.VK_L:
				// l key
				if (!action) {
					topX += 10;
				} else {
					circleWidth += 10;
				}

				break;
			case 37:
				// left arrow
				if (!action) {
					topX--;
				} else {
					circleWidth--;
				}

				break;
			case 38:
				// up arrow
				if (!action) {
					topY--;
				} else {
					circleHeight--;
				}

				break;
			case 39:
				// right arrow
				if (!action) {
					topX++;
				} else {
					circleWidth++;
				}

				break;
			case 40:
				// down arrow
				if (!action) {
					topY++;
				} else {
					circleHeight++;
				}

				break;
			}

		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
	
	private class MyPanel extends JPanel{
		private final Dimension PREF_SIZE = new Dimension(imageWidth, imageHeight);
		
		public Dimension getPreferedSize(){
			return PREF_SIZE;
		}
		
		public JMenuBar methodThatReturnsJMenuBar(){
			JMenu menu = new JMenu("Menu");
			JMenuBar menuBar = new JMenuBar();
			menuBar.add(menu);
			return menuBar;
		}
	}

}
