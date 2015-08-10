package bugs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tree.Tree;

public class Interpreter extends Thread {

	Tree<Token> t;
	private int speed = 1;
	public static List<Bugs> bugs = new ArrayList<Bugs>();
	int numberOfBugs;
    // Control the amount of output produced
    private boolean verbose = false;
    ConcurrentHashMap<String,Bugs> nameBug = new ConcurrentHashMap<String,Bugs>();
	ConcurrentHashMap<String, Double> variables = new ConcurrentHashMap<String,Double>();
	ConcurrentHashMap<String, Tree<Token>> functions = new ConcurrentHashMap<String,Tree<Token>>();
	View view;
	
	boolean step = false;
	// main
	// Jfilechooser compulsory in main method 
	
	// Every GUI program needs a starting point that has
	// to be in the main program
	// Create a new interpreter object in this main method
	
	//map for variables
	//map 
	
	Interpreter(String text,View view){
		this.view =view;
	//	Recognizer r = new Recognizer(text).isProgram();
		Parser p = new Parser(text);
		p.isProgram();
		t = p.stack.peek();
		numberOfBugs = t.getChild(1).getNumberOfChildren();

		
	}

	public void runAllbugs() {
		
		intrepretAllbugs(t);
	}
	

	public void run() {
		
		  Tree<Token> bugTree = t.getChild(1);
	        for(int i =0;i<numberOfBugs;i++){
				Bugs b = new Bugs(i,this,bugTree.getChild(i),this.view);
				b.bugName = bugTree.getChild(i).getChild(0).getValue().toStringHelper();
				nameBug.put(b.bugName,b);
				bugs.add(b);
				b.setBlocked(true);
				//b.interpret(t.getChild(i));
			}
	        // Start worker threads (still blocked)
	        for (int j = 0; j<bugs.size(); j++) {
	            bugs.get(j).start();
	        }
	       
	        // When all worker threads are blocked (waiting), unblock them
	        // If no worker threads remain, exit loop
	        while (bugs.size() > 0) {
	            unblockAllBugs();
	        }
	        System.out.println("Master thread dies.");
	    }
	
	
	
	void pause(){
		for(Bugs bug : bugs){
			bug.stepOn = true;
			bug.flag = true;
		}
	}
	
	void resumeBugs(){
		for(Bugs bug : bugs){
			bug.stepOn = false;
			bug.flag = false;
		}
	}
	
	void step(){
		for(Bugs bug : bugs){
			bug.stepOn = true;
			bug.flag = false;
		}
		for(Bugs bug : bugs){
			
			bug.flag = true;
		}
	}
	
	public void intrepretAllbugs(Tree<Token> programTree){
			Tree<Token> varTree = programTree.getChild(0).getChild(0);
			if (!(varTree.getNumberOfChildren()==0)){
				int number = varTree.getChild(0).getNumberOfChildren();
				if(number!=0){
					for(int j=0; j < number ; j++){
						String name =	(varTree.getChild(0).getChild(j).getValue().toStringHelper());
						variables.put(name,0.0);
					}
				}
				
			}
			
			Tree<Token> funcsTree = programTree.getChild(0).getChild(1);
			if (!(funcsTree.getNumberOfChildren()==0)){
			int numberOfFuncs = funcsTree.getNumberOfChildren();
			if(numberOfFuncs!=0){
				for(int j=0; j < numberOfFuncs ; j++){
					functions.put(funcsTree.getChild(j).getChild(0).getValue().toStringHelper(), funcsTree.getChild(j));
				}
			}
			}
	}
	
	 /** Called by a Worker to try to get permission to work */
    synchronized void getBugPermit(Bugs b) {
    	try {
			sleep(speed);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        int bugNumber = b.getBugNumber();
        verbosePrint("    Bug " + bugNumber + 
                     " is trying to get a work permit.");
        while (b.isBlocked()) {
            try {
                verbosePrint("   Bug " + bugNumber +
                             " is waiting.");
                wait();
            }
            catch (InterruptedException e) {
                verbosePrint("   Bug " + bugNumber +
                             " has been interrupted.");
            }
        }
        verbosePrint("Bug " + bugNumber + " got a work permit.");
    }
    
    /** Called by a Bug to indicate completion of work */
    synchronized void completeCurrentTask(Bugs b) {
        b.setBlocked(true);
        verbosePrint("  Bug" + b.getBugNumber() +
                     " has done work and is now blocked.");
        notifyAll();
    }

    /** Called by this TaskManager to allow all Workers to work */
    synchronized void unblockAllBugs() {
      //  verbosePrint("    Master is trying to reset all.");
        while (countBlockedBugs() < bugs.size()) {
            try {
                verbosePrint("    Master is waiting for all workers" +
                             " to be blocked.");
                wait();
            }
            catch (InterruptedException e) {
                verbosePrint("    Master has been interrupted.");
            }
        }
        printResultsSoFar();
        for (Bugs b : bugs) {
            b.setBlocked(false);
        };
        verbosePrint("\nMaster has unblocked all workers.");
        notifyAll();  
    }
    
    /** Counts the number of currently blocked Workers; since this is
     *  called from a synchronized method, it is effectively synchronized */
    private int countBlockedBugs() {
        int count = 0;
        for (Bugs b : bugs) {
            if (b.isBlocked()) {
                count++;
            }
        }
        return count;
    }
    
    /** Called by a Worker to die; synchronized because it modifies the
     * ArrayList of workers, which is used by other synchronized methods. */
    synchronized void terminateWorker(Bugs b) {
        bugs.remove(b);
        System.out.println("* Bug " + b.getBugNumber() +
                           " has terminated.");
    }
    
    synchronized void reset(){
    	bugs.clear();
    }

    private void printResultsSoFar() {
        for (Bugs b : bugs) {
            System.out.print("bugs " + b.getBugNumber() );
        }
        System.out.println();
    }
    private void verbosePrint(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
    
}

