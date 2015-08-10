package bugs;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
//import java.util.Timer;


import javax.management.RuntimeErrorException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Timer;

/**
 * GUI for Bugs language.
 * @author Dave Matuszek
 * @version 2015
 */
public class BugsGui extends JFrame {
    private static final long serialVersionUID = 1L;
    JPanel display;
    View view;
    JSlider speedControl;
    int speed;
    private boolean first = true;
    JButton newButton;
    JButton stepButton;
    JButton runButton;
    JButton pauseButton;
    JButton resetButton;
    String readFile;
    Interpreter interpreter;
    
    /**
     * GUI constructor.
     */
    public BugsGui() {
        super();
        first = true;
        setSize(600, 600);
        setLayout(new BorderLayout());
        createAndInstallMenus();
        createDisplayPanel();
        createControlPanel();
        initializeButtons();
        setVisible(true);
    }

    private void createAndInstallMenus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        JMenuItem LoadMenuItem = new JMenuItem("Load");
        JMenuItem helpMenuItem = new JMenuItem("Help");
        
        menuBar.add(fileMenu);
        fileMenu.add(quitMenuItem);
        fileMenu.add(LoadMenuItem);
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                quit();
            }});
        
        LoadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	
                chooseFile();
                
            }});
        
        menuBar.add(helpMenu);
        helpMenu.add(helpMenuItem);
        helpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                help();
            }});
        
        this.setJMenuBar(menuBar);
    }

    private void createDisplayPanel() {
//        display = new JPanel();
//        add(display, BorderLayout.CENTER);
    	view = new View();
    	add(view,BorderLayout.CENTER);
    	
    }


    private int _timeSlice = 40;  // update every 50 milliseconds
    private Timer _timer = new Timer(_timeSlice, new ActionListener() {

        public void actionPerformed (ActionEvent e) {
            view.update();
        }
    });
    
    
    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        
        addSpeedLabel(controlPanel);       
        addSpeedControl(controlPanel);
        addNewButton(controlPanel);
        addStepButton(controlPanel);
        addRunButton(controlPanel);
        addPauseButton(controlPanel);
        addResetButton(controlPanel);
        
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void addSpeedLabel(JPanel controlPanel) {
        controlPanel.add(new JLabel("Speed:"));
    }

    private void addSpeedControl(JPanel controlPanel) {
        speedControl = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50);
        speed = 50;
        speedControl.setMajorTickSpacing(10);
        speedControl.setMinorTickSpacing(5);
        speedControl.setPaintTicks(true);
        speedControl.setPaintLabels(true);
        speedControl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
            	int x = speedControl.getValue();
            	
            	int z = (int) ((-5999.0/10.0)*x) + 60000;
//            	_timer.setDelay(z);
//            	System.out.println("speed" + z );
//                resetSpeed(z);
            	speed = z;
            }
        });
        controlPanel.add(speedControl);
    }

    private void addNewButton(JPanel controlPanel) {
        newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newAnimation();
            }
        });
       // controlPanel.add(newButton);
    }

    private void addStepButton(JPanel controlPanel) {
        stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	if(first)
            	{
//            		interpreter.stepInit();
            		interpreter.start();
            		first = false;
            		
            	}
            	interpreter.step();
//            	interpreter.resumeBugs();
//            	
//            	interpreter.pause();
//            	if(first){
//                	interpreter.start();
//                	first = false;
//                }
//                else{
//                	interpreter.unblockAllBugs();
//                }
//            	for (int j = 0; j<interpreter.bugs.size(); j++) {
//                	interpreter.bugs.get(j).setBlocked(true);
//    	        }
                stepAnimation();
            }
        });
        controlPanel.add(stepButton);
    }

    private void addRunButton(JPanel controlPanel) {
        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	interpreter.setSpeed(speed);
            	interpreter.resumeBugs();
                runAnimation();
                if(first){
                	interpreter.start();
                	first = false;
                }
            }
        });

        controlPanel.add(runButton);
    }

    private void addPauseButton(JPanel controlPanel) {
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseAnimation();
//                for (int j = 0; j<interpreter.bugs.size(); j++) {
//                	interpreter.bugs.get(j).setBlocked(true);
//    	        }
                interpreter.pause();
            }
        });
        controlPanel.add(pauseButton);
    }

    private void addResetButton(JPanel controlPanel) {
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetAnimation();
                view.commands.clear();
                view.bugList.clear();
                interpreter.reset();
                interpreter = null;
            }
        });
        controlPanel.add(resetButton);
    }
    
    private void initializeButtons() {
        newButton.setEnabled(true);
        stepButton.setEnabled(false);
        runButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
  //     timer = new Timer(40, taskPerformer);
    }

    private void resetSpeed(int value) {
        //_timeSlice = value;;
    }

    protected void newAnimation() {

        
        newButton.setEnabled(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
    }
    
    protected void stepAnimation() {
//    	_timer.start();
//        _timer.stop();
//        runButton.setEnabled(true);
//        model.setLimits(view.getWidth(), view.getHeight());
//        model.makeOneStep();
//        paint(g);
        newButton.setEnabled(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
    }
    
    protected void runAnimation() {
     //   timer.start();
//    	_timer.start();
//    	super.validate();
//        super.repaint();

        
        newButton.setEnabled(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);
        
    }
    
    protected void pauseAnimation() {
//        _timer.stop();
        newButton.setEnabled(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
    }
    
    protected void resetAnimation() {
//        timer.stop();
//        model.reset();
        newButton.setEnabled(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
//        paint(g);
    }

    protected void help() {
        // TODO Auto-generated method stub
    }

    protected void quit() {
        System.exit(0);
    }

    protected void chooseFile() {
    	first = true;
    	final JFileChooser fc = new JFileChooser();
    	int returnVal = fc.showOpenDialog(this.display);
    	
    	File filePath = fc.getSelectedFile(); 
    	byte[] encoded =null;
    	try {
			 encoded = Files.readAllBytes(Paths.get(filePath.getCanonicalPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        readFile = new String(encoded, StandardCharsets.US_ASCII);
        readFile = readFile+"\n";
//    	readFile = "Allbugs {\n" + 
//        		"    var abc\n" + 
//        		"   \n" + 
//        		"    define forward using n {\n" + 
//        		"        move n  \n" + 
//        		"        return -n\n" + 
//        		"    }\n" + 
//        		"    define abc123 {\n" + 
//        		"        abc = 123\n" + 
//        		"    }\n" + 
//        		"}\n" + 
//        		"\n" + 
//        		"Bug Sally {\n" + 
//        		"    var a, b, c\n" + 
//        		"    var x, y\n" + 
//        		"    \n" + 
//        		"    initially {\n" + 
//        		"        x = -50\n" +
//        		"		 c = 1\n" + 
//        		"        color red\n" + 
//        		"        line 0, 0, 25.3, 100/3\n" + 
//        		"    }\n" + 
//        		"    \n" + 
//        		"    y = 2 + 3 * a - b / c\n" + 
//        		"    y = ((2+3)*a)-(b/c)\n" + 
//        		"    loop{\n" + 
//        		"        y = y / 2.0\n" + 
//        		"        exit if y<=0.5\n" + 
//        		"    }\n" + 
//        		"    switch {\n" + 
//        		"    }\n" + 
//        		"    switch {\n" + 
//        		"        case x < y\n" + 
//        		"            moveto 3, x+y\n" + 
//        		"            turn x-y\n" + 
//        		"        case a <= x < y = z !=a >= b > c\n" + 
//        		"            turnto -abc123() + forward(x)\n" + 
//        		"    }\n" + 
//        		"    do forward(a)\n" + 
//        		"}\n" + 
//        		"Bug henry {\n" + 
//        		"    x = Sally.x\n" + 
//        		"    y = -Sally.y + 100\n" + 
//        		"}\n"; 
    	// System.out.println(readFile);
    	 checkParserError();
    	interpreter = new Interpreter(readFile,view);
    	
    	interpreter.runAllbugs();
    	
        
//    	view.bugs = interpreter.bugs;
//    	view.validate();
//    	view.repaint();
//    	Graphics g = view.getGraphics();
//    	paint(g);
    }
    
    private void checkParserError(){
    	try{
    		if(!(new bugs.Recognizer(readFile).isProgram()))
    			throw new RuntimeException("Invalid program");
    		stepButton.setEnabled(true);
            runButton.setEnabled(true);
    		
    	}
    	catch(Exception e){
//    		throw new RuntimeException();
    	
//    		System.out.println("errrrrrorrrr");
    		JOptionPane.showMessageDialog(this.display, e.getMessage());
    	}
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
     BugsGui b = new BugsGui();
     b._timer.start();
//     b.add(new View(b.interpreter.bugs));
        //In response to a button click:
   //     int returnVal = fc.showOpenDialog(aComponent);
    }

    
}
