package bugs;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;

public class View extends JPanel {

	
	View(){
		super();//new FlowLayout(FlowLayout.CENTER));
		
		setBackground(Color.WHITE);
		repaint();
	}
	CopyOnWriteArrayList<Bugs> bugList = new CopyOnWriteArrayList<Bugs>();
	CopyOnWriteArrayList<Command> commands = new CopyOnWriteArrayList<Command>();
	
	synchronized void addBug(Bugs b){
		bugList.add(b);
	}
	
	synchronized void addCommand(Command c){
		commands.add(c);
	}
	
	/**
	 * Paints a triangle to represent this Bug.
	 * 
	 * @param g Where to paint this Bug.
	 */
	public void paintBug(Graphics g, Bugs bug) {
		
//		 int x1 = (int) (scaleX(50) + computeDeltaX(12, 0));
//		    int x2 = (int) (scaleX(50) + computeDeltaX(6, 0 - 135));
//		    int x3 = (int) (scaleX(50) + computeDeltaX(6, 0 + 135));
//		    
//		    int y1 = (int) (scaleY(50) + computeDeltaY(12,0));
//		    int y2 = (int) (scaleY(50) + computeDeltaY(6,0 - 135));
//		    int y3 = (int) (scaleY(50) + computeDeltaY(6, 0 + 135));
//		    g.fillPolygon(new int[] {100+ x1,100+ x2,100+ x3 }, new int[] { 100+y1, 100+y2, 100+y3 }, 3);
		if(bug  == null)
			return;
		    
	    if (bug.bugColor == null) return;
	    g.setColor(bug.bugColor);
	    
	    int x1 = (int) (scaleX(bug.x) + computeDeltaX(12, (int)bug.angle));
	    int x2 = (int) (scaleX(bug.x) + computeDeltaX(6, (int)bug.angle - 135));
	    int x3 = (int) (scaleX(bug.x) + computeDeltaX(6, (int)bug.angle + 135));
	    
	    int y1 = (int) (scaleY(bug.y) + computeDeltaY(12, (int)bug.angle));
	    int y2 = (int) (scaleY(bug.y) + computeDeltaY(6, (int)bug.angle - 135));
	    int y3 = (int) (scaleY(bug.y) + computeDeltaY(6, (int)bug.angle + 135));
	    
	    x1 += super.getWidth()/4;
	    x2 +=super.getWidth()/4;
	    x3 +=super.getWidth()/4;
	    y1 += super.getHeight()/4;
	    y2 += super.getHeight()/4;
	    y3 +=super.getHeight()/4;
	    g.fillPolygon(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, 3);
	}
	
	public void paintCommand(Graphics g,Command c){
		g.setColor(c.color);
		g.drawLine((int)scaleX(c.x1)+super.getWidth()/4,(int)scaleY(c.y1)+super.getHeight()/4,(int) scaleX(c.x2)+super.getWidth()/4 ,(int) scaleY(c.y2)+super.getHeight()/4);
	}

	
	public double scaleX(double x){
		return (x*super.getWidth()*1.0)/200.0;
		
	}
	
	public double scaleY(double y){
		return (y*super.getHeight()*1.0)/200.0;
	}
	
	/**
	 * Computes how much to move to add to this Bug's x-coordinate,
	 * in order to displace the Bug by "distance" pixels in 
	 * direction "degrees".
	 * 
	 * @param distance The distance to move.
	 * @param degrees The direction in which to move.
	 * @return The amount to be added to the x-coordinate.
	 */
	private static double computeDeltaX(int distance, int degrees) {
	    double radians = Math.toRadians(degrees);
	    return distance * Math.cos(radians);
	}

	/**
	 * Computes how much to move to add to this Bug's y-coordinate,
	 * in order to displace the Bug by "distance" pixels in 
	 * direction "degrees.
	 * 
	 * @param distance The distance to move.
	 * @param degrees The direction in which to move.
	 * @return The amount to be added to the y-coordinate.
	 */
	private static double computeDeltaY(int distance, int degrees) {
	    double radians = Math.toRadians(degrees);
	    return distance * Math.sin(-radians);
	}
	
	protected static int random(int min, int max) {

        return (int)Math.round(Math.random() * (max - min)) + min;

    }
	@Override
	public void paint(Graphics g){
		super.paint(g);
	//	System.out.println("asdf");
		 
//		g.setColor(new Color(random(0, 255), random(0, 255), random(0, 255)));

		
		for(Bugs b:bugList){
	//		System.out.println("Drawing bug "+b.bugName+" x "+b.x+" y "+b.y+" angle "+b.angle);
			paintBug(g, b);
		}
		
		for(Command c:commands){
			paintCommand(g,c);
		//	System.out.println("Drawing a command " + super.getHeight() + "height" + super.getWidth() + "width");
			
		}
		
		
	}
	
	public void update(){
		
		super.repaint();
	}
	
}
