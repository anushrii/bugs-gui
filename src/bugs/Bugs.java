package bugs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.management.RuntimeErrorException;

import tree.Tree;
import bugs.Token.Type;

/**
 * Interpreter for Bugs-Language. 
 * 
 * @author Anushree Singh
 * @version March 2015
 */
public class Bugs extends Thread{
    static Random rand = new Random();
    private int myBugNumber; // An ID, for printing purposes
    private boolean blocked;    // If true, this worker cannot work
    private Interpreter interpreter;
	
    View view;
	
	double x,y,angle;
	double returnValue = 0;
	Color bugColor;
	String bugName; 
	public HashMap<String,Double> variables;
	public HashMap<String, Tree<Token>> functions;
//	Interpreter i;
	boolean stepOn = false;
	boolean flag = true;
	
	Tree<Token> tree;
	Tree<Token> BlockTree ;
	
	private Stack<Boolean> loopStack = new Stack<>(); 
	Stack<HashMap<String, Double>> scopes = new Stack<HashMap<String, Double>>();
	 /**
     * Constructs a Bug.
     */
	public Bugs(){
		
		x =0;
		y=0;
		angle=0;
		bugColor = Color.black;
		variables = new HashMap<>();
		functions = new HashMap<String,Tree<Token>>();
		
	//	this.I  = I
	}
	
	public Bugs(int bugNumber,Interpreter i,Tree<Token> tree,View view){
		this.view =view;
		myBugNumber = bugNumber;
		this.tree= tree;
		this.interpreter = i;
		x =0;
		y=0;
		angle=0;
		bugColor = Color.black;
		variables = new HashMap<>();
		functions = new HashMap<String,Tree<Token>>();
		BlockTree = this.tree.getChild(3);
		scopes.push(variables);
	//	this.I  = I
	}
	
    public void setBlocked(boolean b) { blocked = b; }
    
    public boolean isBlocked() { return blocked; }

    public int getBugNumber() { return myBugNumber; }
    
    private int getNumberofCommands(){
    	return this.BlockTree.getNumberOfChildren();
    }
    /** Repeatedly: Get permission to work; work; signal completion */
    @Override
    public void run() {
    	view.addBug(this);
    	 
        interpret(this.tree);   
        interpreter.terminateWorker(this);
        // Thread dies upon exit
    }

    /** Pause for a random amount of time */
    private void pause() {
        try { sleep(rand.nextInt(100)); }
        catch (InterruptedException e) {}
    }
    
    
	public double distance (String bugname){
		Bugs b = interpreter.nameBug.get(bugname);
		double distance = Math.sqrt(((this.x - b.x)*(this.x - b.x)) +
				((this.y  - b.y)*(this.y  - b.y)));
		return distance;
		
	}
	
	public double direction (String bugname){
		Bugs b = interpreter.nameBug.get(bugname);
		double directionAngle = this.angle - b.angle;
		return directionAngle;
		
	}
	 /**
     * Stores the value specified into the desired variable in the hashMap
     * @param variable 
     *  * @param value 
     */
	public void store(String variable, double value){
	    if(variable.equals("x")){
			x = value;
			return;
		}
		else if(variable.equals("y")){
			y = value;
			return;
		}
		else if(variable.equals("angle")){
			angle = value;
			if (angle >= 360) angle = angle%360;
			return;
		}
		for (int i = scopes.size()-1; i>=0; i--){
			if ( scopes.get(i).containsKey(variable)) {
				scopes.get(i).put(variable,value);
				return;
			}
		}
		if( interpreter.variables.containsKey(variable)){
			 interpreter.variables.put(variable,value);
		}
		else throw new  RuntimeException();
	}
	
	 /**
     * Fetches the value of the specified  variable, that needs to be 
     * there in the hashMap
     * A <code>RunTimeException</code> will be thrown if the variable(key) 
     * is not in the map.
     * @param  variable
     * @return <code>true</code> value of the the variable.
     */
	public double fetch(String variable) {
		if(variable.equals("x")){
			return x;
		}
		if(variable.equals("y")){
			return y;		
		}
		if(variable.equals("angle")){
			return angle;
		}
		for (int i = scopes.size()-1 ; i>=0 ; i--){
		if ( scopes.get(i).containsKey(variable)) {
			return scopes.get(i).get(variable);
		}
		}
		if( interpreter.variables.containsKey(variable)){
			return interpreter.variables.get(variable);
		}
		else throw new  RuntimeException();
	}
	
	
	
	public double getOther(String otherbugname, String variable) {
	
		if(variable.equals("x")){
			return interpreter.nameBug.get(otherbugname).x;
		}
		if(variable.equals("y")){
			return interpreter.nameBug.get(otherbugname).y;	
		}
		if(variable.equals("angle")){
			return interpreter.nameBug.get(otherbugname).angle;
		}
		
		if ( interpreter.nameBug.get(otherbugname).scopes.get(0).containsKey(variable)) {
			return interpreter.nameBug.get(otherbugname).scopes.get(0).get(variable);
		}
		else throw new  RuntimeException();
	}
	
	 /**
     * Evaluates the tree input
     * @param tree to be evaluated
     * @return <code>true</code> evaluated value.
     */
	public double evaluate(Tree<Token> tree){
		Token s = tree.getValue();
		//System.out.println("Evaluating "+s.value);
		if (tree.getNumberOfChildren() == 0) {
			if (s.type== Type.NUMBER){
				return Double.parseDouble(s.toStringHelper()) ;
			}
			else {
				return fetch(s.toStringHelper());
			}
		}
		
		
		if ("call".equalsIgnoreCase(s.value)){ 
			HashMap<String, Double> funcVar = new HashMap<String,Double>();
			scopes.push(funcVar);
			 //really means evaluate?
			Tree<Token>  getFunction  = functions.get(tree.getChild(0).getValue().toStringHelper());
			int numFormalParams = getFunction.getChild(1).getNumberOfChildren();
			for(int i = 0 ; i<numFormalParams ; i++){
				double  evalVar = evaluate(tree.getChild(1).getChild(i));
				String varName =  getFunction.getChild(1).getChild(i).getValue().toStringHelper();
				funcVar.put(varName,evalVar);
			}
			interpret(getFunction.getChild(2));
			return returnValue;
		}
		
//		if ("do".equals(s.value)){ 
//			HashMap<String, Double> funcVar = new HashMap<String,Double>();
//			scopes.push(funcVar);
//			 //really means evaluate?
//			Tree<Token>  getFunction  = functions.get(tree.getChild(1).getValue().toStringHelper());
//			int numFormalParams = getFunction.getChild(1).getNumberOfChildren();
//			for(int i = 0 ; i<numFormalParams ; i++){
//				double  evalVar = evaluate(tree.getChild(1).getChild(i));
//				String varName =  getFunction.getChild(1).getChild(i).getValue().toStringHelper();
//				funcVar.put(varName,evalVar);
//			}
//			interpret(getFunction.getChild(2));
//			return returnValue;
//		}
		
		if ("+".equals(s.value)){          
			if (tree.getNumberOfChildren() == 1){
				
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					return Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
				    return -(fetch(tree.getChild(0).getValue().toStringHelper()));
				else
					return -(evaluate(tree.getChild(0)));
//				return fetch(tree.getChild(0).getValue().toStringHelper());
			}
			else if (tree.getNumberOfChildren() == 2){
				double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				//double c = a + b ;
				return a + b;
				
			}
		}
		
		if ("-".equals(s.value)){            
			if (tree.getNumberOfChildren() == 1){
				
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					return -1*(Double.parseDouble(tree.getChild(0).getValue().toStringHelper()));
				else if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
				    return -(fetch(tree.getChild(0).getValue().toStringHelper()));
				else
					return -(evaluate(tree.getChild(0)));
			//	return -1*(fetch(tree.getChild(0).getValue().toStringHelper()));
			}
			else if (tree.getNumberOfChildren() == 2){
				double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				//double c = a + b ;
				return a - b;
				
			}
		}
		
		
		if ("*".equals(s.value)){            
			 if (tree.getNumberOfChildren() == 2){
					double a;
					double b;
					if(tree.getChild(0).getValue().type == Type.NUMBER)
						a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
					else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
						   try{
							a = fetch(tree.getChild(0).getValue().toStringHelper());
						   }catch(Exception e){
							a = evaluate(tree.getChild(0));
						   }
						}
					
					if(tree.getChild(1).getValue().type == Type.NUMBER)
						b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
					else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
						   try{
							b = fetch(tree.getChild(1).getValue().toStringHelper());
						   }catch(Exception e){
							b = evaluate(tree.getChild(1));
						   }
						}
				
				//double c = a + b ;
				return a*b;
				
			}
		}
		
		if ("/".equals(s.value)){            
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (b== 0) throw new RuntimeException("Division by zero isnt valid ");
				//double c = a + b ;
				return a/b;
				
			}
		}
		
		if (">".equals(s.value)){           
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (a>b) return 1;
				else return 0;
			}
		}
		
		if ("<".equals(s.value)){           
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
				   try{
					a = fetch(tree.getChild(0).getValue().toStringHelper());
				   }catch(Exception e){
					a = evaluate(tree.getChild(0));
				   }
				}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (a<b) return 1;
				else return 0;
				
			}
		}
		
		if ("=".equals(s.value)){            
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (a==b) return 1;
				else return 0;
				
				
			}
		}
		
		if ("!=".equals(s.value)){            
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (a==b) return 0;
				else return 1;
				
				
			}
		}
		
		if (">=".equals(s.value)){          
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (a>=b) return 1;
				else return 0;
				
				
			}
		}
		if ("<=".equals(s.value)){            
		     if (tree.getNumberOfChildren() == 2){
		 		double a;
				double b;
				if(tree.getChild(0).getValue().type == Type.NUMBER)
					a = Double.parseDouble(tree.getChild(0).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						a = fetch(tree.getChild(0).getValue().toStringHelper());
					   }catch(Exception e){
						a = evaluate(tree.getChild(0));
					   }
					}
				
				if(tree.getChild(1).getValue().type == Type.NUMBER)
					b = Double.parseDouble(tree.getChild(1).getValue().toStringHelper());
				else {//if(variables.containsKey(tree.getChild(0).getValue().toStringHelper()))
					   try{
						b = fetch(tree.getChild(1).getValue().toStringHelper());
					   }catch(Exception e){
						b = evaluate(tree.getChild(1));
					   }
					}
				
				if (a==b || a<b) return 1;
				else return 0;
				
				
			}
		}
		
		if (".".equals(s.value)){            
		     if (tree.getNumberOfChildren() == 2){
		    	 
		    String other =	 tree.getChild(0).getValue().toStringHelper();
		    if (interpreter.nameBug.containsKey(other))
		    	 return   interpreter.nameBug.get(other).fetch(tree.getChild(1).getValue().toStringHelper()); 
		    else  throw new RuntimeException();
		     }
		    else  throw new RuntimeException();
		 
		}
		
		if ("case".equals(s.value)){            
		     if (tree.getNumberOfChildren() == 2){

			double exp = evaluate(tree.getChild(0));
			if (!(exp == 0)){
			interpret(tree.getChild(1)) ;
			}
			return exp;

			}
		}
		return 0;
		
		
	}
	
	 /**
     * Interprets the tree input.
     * @param tree to be interpreted.
     */
	public void interpret(Tree<Token> tree){
		Token s = tree.getValue();
//		System.out.println("Interpreting "+s.value);
		// return
		if ("call".equals(s.value)){
			evaluate( tree);
		}
		if ("return".equals(s.value)){ 
			returnValue = 	evaluate(tree.getChild(0));
		    scopes.pop();
		}
		
		
		// move
		if ("move".equals(s.value)){ 
			if(stepOn){
				while(flag){
					pause();
				}
				interpreter.getBugPermit(this);
		         
				Command command = new Command();
				double distance = 	evaluate(tree.getChild(0));
				command.x1 = x;
				command.y1 = y;
				x = x + distance*Math.cos(Math.toRadians(angle));
			
				y = y - distance*Math.sin(Math.toRadians(angle));
			
				command.x2 = x;
				command.y2 = y;
				command.color = this.bugColor;
				view.addCommand(command);
				interpreter.completeCurrentTask(this);
				
			}else{
				interpreter.getBugPermit(this);
		         
				Command command = new Command();
				double distance = 	evaluate(tree.getChild(0));
				command.x1 = x;
				command.y1 = y;
				x = x + distance*Math.cos(Math.toRadians(angle));
			
				y = y - distance*Math.sin(Math.toRadians(angle));
			
				command.x2 = x;
				command.y2 = y;
				command.color = this.bugColor;
				view.addCommand(command);
				interpreter.completeCurrentTask(this);
			}
			
          
			//System.out.println("move " + "x" + x + "y" + y);
		
			
		}
		
		// move to
		if ("moveto".equals(s.value)){ 
			if(stepOn){
				while(flag){
					pause();
				}
				interpreter.getBugPermit(this);
		         
				Command command = new Command();
				command.x1 = x;
				command.y1 = y;
				double a = 	evaluate(tree.getChild(0));
				double b = 	evaluate(tree.getChild(1));
				x = a;
				y = b;
				command.x2 = x;
				command.y2 = y;
				command.color = this.bugColor;
				view.addCommand(command);
				interpreter.completeCurrentTask(this);
				
			}else{
				interpreter.getBugPermit(this);
		         
				Command command = new Command();
				command.x1 = x;
				command.y1 = y;
				double a = 	evaluate(tree.getChild(0));
				double b = 	evaluate(tree.getChild(1));
				x = a;
				y = b;
				command.x2 = x;
				command.y2 = y;
				command.color = this.bugColor;
				view.addCommand(command);
				interpreter.completeCurrentTask(this);
			}
			
	
		}
		
		//line
		if ("line".equals(s.value)){ 
			interpreter.getBugPermit(this);
	          pause();
			Command command = new Command();
			double a = 	evaluate(tree.getChild(0));
			double b = 	evaluate(tree.getChild(1));
			double c = 	evaluate(tree.getChild(2));
			double d = 	evaluate(tree.getChild(3));
			command.x1 = a;
			command.y1 = b;
			command.x2 = c;
			command.y2 = d;

			command.color = this.bugColor;
			view.addCommand(command);
			interpreter.completeCurrentTask(this);
			if(stepOn){
				while(flag){
					pause();
				}
			}
		}
		
		
		
		
		// turn		
		if ("turn".equals(s.value)){ 
			if(stepOn){
				while(flag){
					pause();
				}
				interpreter.getBugPermit(this);
		          
				double a = 	evaluate(tree.getChild(0));
				angle = angle + a;
				
			 angle = angle%360;
				System.out.println(Math.toRadians(angle) + "angle" + angle);
				interpreter.completeCurrentTask(this);
				
			}else{
			interpreter.getBugPermit(this);
	          pause();
			double a = 	evaluate(tree.getChild(0));
			angle = angle + a;
			
		 angle = angle%360;
			System.out.println(Math.toRadians(angle) + "angle" + angle);
			interpreter.completeCurrentTask(this);
			}
		}
		
		// turn to		
		if ("turnto".equals(s.value)){ 
			
		  if(stepOn){
			  while(flag){
					pause();
				}
			  interpreter.getBugPermit(this);
	          pause();
			double a = 	evaluate(tree.getChild(0));
		    angle = a;
		  angle = angle%360;
		  interpreter.completeCurrentTask(this);
				
			}else{
				interpreter.getBugPermit(this);
		          pause();
				double a = 	evaluate(tree.getChild(0));
			    angle = a;
			  angle = angle%360;
			  interpreter.completeCurrentTask(this);
			}
		}
		
		// switch
		
		if ("switch".equals(s.value)){
			int number = tree.getNumberOfChildren();
			if(number!=0){
			for(int i=0; i < number ; i++){
				double a = 	evaluate(tree.getChild(i));
				if (a == 1 ) break;
				else continue;
			}
			}
		}
		
		// color detection		
		if ("color".equals(s.value)){ 
			String a = 	tree.getChild(0).getValue().toStringHelper();
			if(a.equals("black")){
			bugColor = Color.black;
			}
			else if(a.equals("blue")){
			bugColor = Color.blue;	
		    }
			else if(a.equals("cyan")){
			bugColor = Color.cyan;
		}
			else if(a.equals("darkGray")){
			bugColor = Color.darkGray;
		}
			else if(a.equals("gray")){
			bugColor = Color.gray;
		}
			else if(a.equals("green")){
			bugColor = Color.green;
		}
			else if(a.equals("yellow")){
			bugColor = Color.yellow;
		}
			else if(a.equals("lightGray")){
			bugColor = Color.lightGray;
		}
			else if(a.equals("magenta")){
			bugColor = Color.magenta;
		}
			else if(a.equals("orange")){
			bugColor = Color.orange;
		}
			else if(a.equals("pink")){
			bugColor = Color.pink;
		}
			else if(a.equals("red")){
			bugColor = Color.red;		
		}
			else if(a.equals("white")){
			bugColor = Color.white;
		}
			else if(a.equals("brown")){
			
			bugColor = new Color(165,42,42);
		}
			else if(a.equals("purple")){
			bugColor = new Color(128,0,128);
		}
			else if(a.equals("none")){
			bugColor = null;
		}
			else throw new  RuntimeException();
				
		}
		
		if("function".equals(s.value)){
			functions.put(tree.getChild(0).getValue().toStringHelper(), tree);
		}
		
		if ("Bug".equals(s.value)){ 
		String name = tree.getChild(0).getValue().toStringHelper();
		bugName  = name;
		
		interpret(tree.getChild(1));
		interpret(tree.getChild(2));
		interpret(tree.getChild(4));
	//	interpret(tree.getChild(3));
		for (int j= 0 ; j< getNumberofCommands(); j ++){
            // Get permission to work
//            interpreter.getBugPermit(this);
//            pause();
            interpret(this.tree.getChild(3).getChild(j));
            // Do the actual work, NOT inside a synchronized block
           
      
            // Indicate that this work has been completed
//            interpreter.completeCurrentTask(this);
            
            }
	
	
		}

		if ("list".equals(s.value)){ 
			int number = tree.getNumberOfChildren();
			if(number!=0){
			for(int i=0; i < number ; i++){
				interpret(tree.getChild(i));
			}
			}
		}

		if ("initially".equals(s.value)){ 
			int number = tree.getNumberOfChildren();
			if(number!=0){
			interpret(tree.getChild(0));
			}
		}

		if ("var".equals(s.value)){ 
			
			int number = tree.getNumberOfChildren();
			if(number!=0){
			for(int i=0; i < number ; i++){
			String name =	(tree.getChild(i).getValue().toStringHelper());
			variables.put(name, 0.0);
			}
		}
		}

		if ("assign".equals(s.value)){ 
			String a = 	tree.getChild(0).getValue().toStringHelper();
		//	if ( !variables.containsKey(a))  throw new  RuntimeException();
			double val = evaluate(tree.getChild(1));
			store(a, val);
		}

		if ("exit".equals(s.value)){ 
			double condition = evaluate(tree.getChild(0));
			if (condition == 1){
			loopStack.pop();
			loopStack.push(false);
			}
		
		}

		if ("block".equals(s.value)){ 
			int number = tree.getNumberOfChildren();
			if(number!=0){
			for(int i=0; i < number ; i++){
				interpret(tree.getChild(i));
			}
		}
		}

		if ("loop".equals(s.value)){ 
			loopStack.push(true);
			
				while (loopStack.peek()) {
				interpret(tree.getChild(0))	;
			//	System.out.println (y);
				
				}
				loopStack.pop();
		}

		
		
	}
	
	
}
