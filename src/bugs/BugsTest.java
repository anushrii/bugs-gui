package bugs;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import tree.Tree;

import org.junit.Test;

public class BugsTest {
	 Bugs bug = new Bugs();
	 private static final double delta = 0.001;

	@Before
	public void setUp() throws Exception { // something more?
		// double delta = 0.0001;
	    }

	
	@Test
	public void testIsEvaluate() {
		//use("a + b") ;
		tree("+", "a","b");
		//testAddition();
	}

	// tests for fetch and store
	@Test
	public void testFetchStore() {
		
		bug.store("x",3);
		assertEquals(bug.fetch("x"),3,delta);
		bug.store("y",4);
		assertEquals(bug.fetch("x"),3,delta);
		bug.x = 0;
		bug.y = 0;
		
		bug.store("angle",390);
		double expected = 30;
		assertEquals(bug.fetch("angle"),expected,delta);
		
	}
	

	
	@Test
	public void testAddition() {
		
		bug.store("x",3);
		bug.store("y",4);
		
		double actual = bug.evaluate(tree("+", "x","y"));
		double expected = 7;
		assertEquals(expected, actual,delta);
		
		
		actual = bug.evaluate(tree("+", "x","4"));
		expected = 7;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("+", "12.8","4.9"));
	    expected = 17.7;
		assertEquals(expected, actual,delta);
	}
	
	@Test
	public void testSubtraction() {
		
		bug.store("x",21);
		bug.store("y",32);
		
		double actual = bug.evaluate(tree("-", "x","y"));
		double expected = -11;
		assertEquals(expected, actual,delta);
		
		
		actual = bug.evaluate(tree("-", "4","x"));
		expected = -17;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("-", "12.8","4.9"));
	    expected = 7.9;
		assertEquals(expected, actual,delta);
	}
	
	@Test
	public void testMultiplication() {
		
		bug.store("x",11);
		bug.store("y",2);
		
		double actual = bug.evaluate(tree("*", "x","y"));
		double expected = 22;
		assertEquals(expected, actual,delta);
		
		
		actual = bug.evaluate(tree("*", "4","x"));
		expected = 44;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("*", "12.0","4.0"));
	    expected = 48;
		assertEquals(expected, actual,delta);
	}
	
	@Test
	public void testDivision() {
		
		bug.store("x",11);
		bug.store("y",22);
		
		double actual = bug.evaluate(tree("/", "y","x"));
		double expected = 2;
		assertEquals(expected, actual,delta);
		
		
		actual = bug.evaluate(tree("/", "x","4"));
		expected = 2.75;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("/", "4.0","12.0"));
	    expected = 0.333;
		assertEquals(expected, actual,delta);
	}
	
	@Test
	public void testComparators() {
		
		bug.store("x",11.012);
		bug.store("y",22.001);
		
		double actual = bug.evaluate(tree("=", "y","x"));
		double expected = 0;
		assertEquals(expected, actual,delta);
		
		
		actual = bug.evaluate(tree("=", "x","11.000"));
		expected = 0;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("!=", "4.0","12.0"));
	    expected = 1;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree(">=", "x","11.000"));
		expected = 1;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("<=", "4.0","12.0"));
	    expected = 1;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree(">", "x","11.000"));
		expected = 1;
		assertEquals(expected, actual,delta);
		
		actual = bug.evaluate(tree("<", "4.0","12.0"));
	    expected = 1;
		assertEquals(expected, actual,delta);
		
	}
	
	
		@Test
		public void testCase() {
			
			
			
		    bug.evaluate(tree("case", tree("!=", "x","12.0"), tree("block", tree("moveto" , "12", "12"))));
			double expectedX = 12;
			double expectedY = 12;
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta);
			
		
			bug.angle  =  Math.toDegrees((Math.PI)/2);
		
			bug.evaluate(tree("case", tree("!=", "4.0","12.0"),  tree("block",tree("move" , "12"))));
			 expectedX = 12;
			 expectedY = 24;
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta);
			
			bug.x =  5;
			bug.y = 5;
			bug.angle =  0;
			bug.evaluate(tree("case", tree("=", "4.0","12.0"),  tree("block",tree("move" , "12"))));
			 expectedX = 5;
			 expectedY = 5;
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta);
			
			bug.x =  0;
			bug.y =  0;
			bug.angle = 360 +  180;
			
			bug.evaluate(tree("case", tree(">=", "12.01","12.0"), tree("block", tree("move" , "12"))));
			 expectedX = -12;
			 expectedY =  0;
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta);
			
			bug.evaluate(tree("case", tree(">=", "12.01","12.0"),  tree("block",tree("turn" , "90"))));
			 double expectedAngle = 270;
			assertEquals(expectedAngle, bug.angle ,delta);
			
			
			bug.evaluate(tree("case", tree(">=", "12.01","12.0"),  tree("block",tree("turnto" , "360"))));
			expectedAngle = 0;
			assertEquals(expectedAngle, bug.angle ,delta);
	}
	

		@Test
		public void testSwitch() {
			bug.store("alpha",11.01);
			bug.x = 0;
			bug.y = 0;
			bug.angle = 0;
			bug.interpret(tree("switch",
					    		tree("case", tree("=", "alpha","11.000"), tree("block",tree("turnto" , "0"))),
									tree("case",tree(">=", "12.01","12.0"), tree("block",tree("move", "1.0"))),
									tree("case", tree(">=", "12.01","12.0"),  tree("block",tree("move" , "90")))));
	   	    double expectedX = 1;
			double expectedY = 0;
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta); 
			
			bug.interpret(tree("switch",
							tree("case", tree(">=", "alpha","11.000"), tree("block",tree("moveto" , "0", "0"))),
								tree("case",tree(">=", "12.01","12.0"), tree("block",tree("move", "1.0"))),
									tree("case", tree(">=", "12.01","12.0"),  tree("block",tree("move" , "90")))));
		    expectedX = 0;
			expectedY = 0;
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta); 
		}

		
		@Test
		public void testColor() {
			bug.bugColor = Color.yellow;
			bug.interpret(tree("color", "red"));
			Color expectedColor = Color.red;
			assertEquals( expectedColor, bug.bugColor);
			
			bug.interpret(tree("color", "cyan"));
		    expectedColor = Color.cyan;
			assertEquals( expectedColor, bug.bugColor);
			
			bug.interpret(tree("color", "none"));
			expectedColor = null;
			assertEquals( expectedColor, bug.bugColor);
			
			
			
			try {
				bug.interpret(tree("color", "belu"));
				expectedColor = Color.blue;
				assertEquals( expectedColor, bug.bugColor);
	            fail();
	        }
	        catch (RuntimeException e) {
	        }
		}
		
		@Test
		public void testBlock() {
			bug.store("alpha",11.01);
			bug.x = 0;
			bug.y = 0;
			bug.angle = 0;
			bug.interpret(tree("block",
					    	 tree("turnto" , "0"),
								tree("move", "1.0"),
								  tree("turnto" , "90"),
								    tree("move" , "3")));
			double expectedX = 1;
			double	expectedY = 3;
			double	expectedAngle = 90;
			assertEquals(expectedAngle, bug.angle ,delta);
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta); 
			
			// test for statements
		
		}
		
		@Test
		public void testLoop() { /// test more
			bug.store("alpha",11.01);
			bug.x = 0;
			bug.y = 0;
			bug.angle = 0;
			bug.interpret(tree("loop", 
					tree("block",
			    	 tree("turnto" , "0"),
			    	 tree("loop",
			          tree("block",
					    tree("move", "1.0"),
					    tree("turn" , "0"),
					        tree("move" , "5.0"), tree("exit", tree(">=", "x","5")))),
					        tree("exit", tree(">", "x","100")))));
		
		double expectedX = 102;
		double expectedY =  0;
		double	expectedAngle = 0;
		
		assertEquals(expectedAngle, bug.angle ,delta);
		assertEquals(expectedX, bug.x ,delta);
		assertEquals(expectedY, bug.y ,delta); 
		}
		
		
		@Test
		public void testAssign() {
			bug.store("foo", 0);
			bug.store("x", 0);
			bug.interpret(tree("assign", "foo", "30"));
			double expected = 30;
			
			assertEquals( expected, bug.fetch("foo"),delta);
			
			
			try{
			bug.interpret(tree("assign", "x", "30"));
			 fail();
			}
			catch (RuntimeException e){
			}
			
			try {
				bug.store("buz", 0);
				bug.interpret(tree("assign", "biz", "30"));
	            fail();
	        }
	        catch (RuntimeException e) {
	        }
		}
		
		@Test
		public void testVar() {
		
			bug.interpret(tree("var", "foo", "bar", "baz"));
			double expected = 0;
			
			assertEquals( expected, bug.fetch("foo"),delta);
			assertEquals( expected, bug.fetch("bar"),delta);
			assertEquals( expected, bug.fetch("baz"),delta);
			
			try {
				assertEquals( expected, bug.fetch("buz"),delta);
	            fail();
	        }
	        catch (RuntimeException e) {
	        }
			
		}
		
		@Test
		public void testInitially() {
		
			bug.interpret(tree("initially",
					        tree("block", 
					        	tree("var", "foo", "bar", "baz"))));
			double expected = 0;
			
			assertEquals( expected, bug.fetch("foo"),delta);
			assertEquals( expected, bug.fetch("bar"),delta);
			assertEquals( expected, bug.fetch("baz"),delta);
		}
		
		@Test
		public void testList() {
		
			bug.interpret(tree("list", 
					        	tree("var", "foo", "bar", "baz")));
			double expected = 0;
			
			assertEquals( expected, bug.fetch("foo"),delta);
			assertEquals( expected, bug.fetch("bar"),delta);
			assertEquals( expected, bug.fetch("baz"),delta);
		}
		
		@Test
		public void testFunctio() {
		
			bug.interpret(tree("function", "foo", tree("var","a","b","c"), "block"));
			Tree<Token> expectedTree = bug.functions.get("foo");
			assertTrue( expectedTree.equals(tree("function", "foo", tree("var","a","b","c"), "block")) );
		
		}
		
		
		@Test
		public void testBug() {
			bug.x = 0;
			bug.y = 0;
			bug.angle = 0;
			bug.interpret(tree("Bug","beebu",
							tree("list",
								tree("var","beebui", "bar", "baz")), "initially",
							    	tree("block", tree("moveto","8.0","7.0")),"list"));
			double expected = 0;
			double expectedX = 8;
			double expectedY =  7;
			double	expectedAngle = 0;
			
			assertEquals(expectedAngle, bug.angle ,delta);
			assertEquals(expectedX, bug.x ,delta);
			assertEquals(expectedY, bug.y ,delta);
			assertEquals( expected, bug.fetch("beebui"),delta);
			assertEquals( expected, bug.fetch("bar"),delta);
			assertEquals( expected, bug.fetch("baz"),delta);
		}
		


//----- "Helper" methods


private String typeName(int type) {
    switch(type) {
        case StreamTokenizer.TT_EOF: return "EOF";
        case StreamTokenizer.TT_EOL: return "EOL";
        case StreamTokenizer.TT_WORD: return "WORD";
        case StreamTokenizer.TT_NUMBER: return "NUMBER";
        default: return "'" + (char)type + "'";
    }
}

/**
 * Returns a Tree node consisting of a single leaf; the
 * node will contain a Token with a String as its value. <br>
 * Given a Tree, return the same Tree.<br>
 * Given a Token, return a Tree with the Token as its value.<br>
 * Given a String, make it into a Token, return a Tree
 * with the Token as its value.
 * 
 * @param value A Tree, Token, or String from which to
          construct the Tree node.
 * @return A Tree leaf node containing a Token whose value
 *         is the parameter.
 */
private Tree<Token> createNode(Object value) {
    if (value instanceof Tree) {
        return (Tree) value;
    }
    if (value instanceof Token) {
        return new Tree<Token>((Token) value);
    }
    else if (value instanceof String) {
        return new Tree<Token>(new Token((String) value));
    }
    assert false: "Illegal argument: tree(" + value + ")";
    return null; 
}

/**
 * Builds a Tree that can be compared with the one the
 * Parser produces. Any String or Token arguments will be
 * converted to Tree nodes containing Tokens.
 * 
 * @param op The String value to use in the Token in the root.
 * @param children The objects to be made into children.
 * @return The resultant Tree.
 */
private Tree<Token> tree(String op, Object... children) {
    Tree<Token> tree = new Tree<Token>(new Token(op));
    for (int i = 0; i < children.length; i++) {
        tree.addChild(createNode(children[i]));
    }
    return tree;
}

private Tree<Token> makeTreeOfTokens(Tree<String> treeOfStrings) {
    Tree<Token> treeOfTokens;
    
    String valueInRoot = treeOfStrings.getValue();
    treeOfTokens = new Tree<Token>(new Token(valueInRoot));
    Iterator<Tree<String>> iter = treeOfStrings.iterator();
    while (iter.hasNext()) {
        treeOfTokens.addChild(makeTreeOfTokens(iter.next()));
    }
    return treeOfTokens;
}

}
