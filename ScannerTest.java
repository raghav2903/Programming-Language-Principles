package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;

import static cop5556sp17.Scanner.Kind.COMMA;
import static cop5556sp17.Scanner.Kind.KW_INTEGER;
import static cop5556sp17.Scanner.Kind.KW_BOOLEAN;
import static cop5556sp17.Scanner.Kind.KW_IMAGE;
import static cop5556sp17.Scanner.Kind.KW_URL;
import static cop5556sp17.Scanner.Kind.KW_FILE;
import static cop5556sp17.Scanner.Kind.KW_FRAME;
import static cop5556sp17.Scanner.Kind.KW_WHILE;
import static cop5556sp17.Scanner.Kind.KW_IF;
import static cop5556sp17.Scanner.Kind.KW_TRUE;
import static cop5556sp17.Scanner.Kind.KW_FALSE;
import static cop5556sp17.Scanner.Kind.LPAREN;
import static cop5556sp17.Scanner.Kind.RPAREN;
import static cop5556sp17.Scanner.Kind.LBRACE;
import static cop5556sp17.Scanner.Kind.RBRACE;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.BARARROW;
import static cop5556sp17.Scanner.Kind.OR;
import static cop5556sp17.Scanner.Kind.AND;
import static cop5556sp17.Scanner.Kind.EQUAL;
import static cop5556sp17.Scanner.Kind.NOTEQUAL;
import static cop5556sp17.Scanner.Kind.LT;
import static cop5556sp17.Scanner.Kind.GT;
import static cop5556sp17.Scanner.Kind.LE;
import static cop5556sp17.Scanner.Kind.GE;
import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.MINUS;
import static cop5556sp17.Scanner.Kind.TIMES;
import static cop5556sp17.Scanner.Kind.DIV;
import static cop5556sp17.Scanner.Kind.MOD;
import static cop5556sp17.Scanner.Kind.NOT;
import static cop5556sp17.Scanner.Kind.ASSIGN;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.INT_LIT;
import static cop5556sp17.Scanner.Kind.IDENT;
import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	@Test
	public void testCommaSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";,;,";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(COMMA, token1.kind);
		assertEquals(1, token1.pos);
		String text1 = COMMA.getText();
		assertEquals(text1.length(), token.length);
		assertEquals(text1, token1.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(COMMA, token3.kind);
		assertEquals(3, token3.pos);
		assertEquals(text1.length(), token3.length);
		assertEquals(text1, token3.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token4.kind);
	}
	
	@Test
	public void testAllSeparators() throws IllegalCharException, IllegalNumberException {
		String input = ",(){}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(COMMA, token.kind);
		assertEquals(0, token.pos);
		String text = COMMA.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(LPAREN, token1.kind);
		assertEquals(1, token1.pos); 
		String text1 = LPAREN.getText();
		assertEquals(text1.length(), token.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(RPAREN, token2.kind);
		String text2 = RPAREN.getText();
		assertEquals(2, token2.pos);
		assertEquals(text2.length(), token2.length);
		assertEquals(text2, token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(LBRACE, token3.kind);
		String text3 = LBRACE.getText();
		assertEquals(3, token3.pos);
		assertEquals(text3.length(), token3.length);
		assertEquals(text3, token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(RBRACE, token4.kind);
		String text4 = RBRACE.getText();
		assertEquals(4, token4.pos);
		assertEquals(text4.length(), token4.length);
		assertEquals(text4, token4.getText());
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token5.kind);
	}
	
    @Test
	public void testOneSingleOperators() throws IllegalCharException, IllegalNumberException {
		String input = "|&<>!";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(OR, token.kind);
		assertEquals(0, token.pos);
		String text = OR.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(AND, token1.kind);
		assertEquals(1, token1.pos); 
		String text1 = AND.getText();
		assertEquals(text1.length(), token.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(LT, token2.kind);
		String text2 = LT.getText();
		assertEquals(2, token2.pos);
		assertEquals(text2.length(), token2.length);
		assertEquals(text2, token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(GT, token3.kind);
		String text3 = GT.getText();
		assertEquals(3, token3.pos);
		assertEquals(text3.length(), token3.length);
		assertEquals(text3, token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(NOT, token4.kind);
		String text4 = NOT.getText();
		assertEquals(4, token4.pos);
		assertEquals(text4.length(), token4.length);
		assertEquals(text4, token4.getText());
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token5.kind);
	}
    
    @Test
	public void testTwoSingleOperators() throws IllegalCharException, IllegalNumberException {
		String input = "+-*/%";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(PLUS, token.kind);
		assertEquals(0, token.pos);
		String text = PLUS.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(MINUS, token1.kind);
		assertEquals(1, token1.pos); 
		String text1 = MINUS.getText();
		assertEquals(text1.length(), token.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(TIMES, token2.kind);
		String text2 = TIMES.getText();
		assertEquals(2, token2.pos);
		assertEquals(text2.length(), token2.length);
		assertEquals(text2, token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(DIV, token3.kind);
		String text3 = DIV.getText();
		assertEquals(3, token3.pos);
		assertEquals(text3.length(), token3.length);
		assertEquals(text3, token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(MOD, token4.kind);
		String text4 = MOD.getText();
		assertEquals(4, token4.pos);
		assertEquals(text4.length(), token4.length);
		assertEquals(text4, token4.getText());
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token5.kind);
	}
	
    @Test
	public void testDoubleOperators() throws IllegalCharException, IllegalNumberException {
		String input = "==!=<=>=-><-";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(EQUAL, token.kind);
		assertEquals(0, token.pos);
		String text = EQUAL.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(NOTEQUAL, token1.kind);
		assertEquals(2, token1.pos); 
		String text1 = NOTEQUAL.getText();
		assertEquals(text1.length(), token.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(LE, token2.kind);
		String text2 = LE.getText();
		assertEquals(4, token2.pos);
		assertEquals(text2.length(), token2.length);
		assertEquals(text2, token2.getText());
        Scanner.Token token3 = scanner.nextToken();
		assertEquals(GE, token3.kind);
		String text3 = GE.getText();
		assertEquals(6, token3.pos);
		assertEquals(text3.length(), token3.length);
		assertEquals(text3, token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(ARROW, token4.kind);
		String text4 = ARROW.getText();
		assertEquals(8, token4.pos);
		assertEquals(text4.length(), token4.length);
		assertEquals(text4, token4.getText());
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(ASSIGN, token5.kind);
		assertEquals(10, token5.pos);
		String text5 = ASSIGN.getText();
		assertEquals(text5.length(), token5.length);
		assertEquals(text5, token5.getText());
	    Scanner.Token token6 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token6.kind);
	}
    
    @Test
    public void Zero() throws IllegalCharException, IllegalNumberException {
		String input = "0";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		String text = "0";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token1.kind);
    }
    
    @Test
    public void Boolean() throws IllegalCharException, IllegalNumberException {
		String input = "true false";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_TRUE, token.kind);
		assertEquals(0, token.pos);
		String text = KW_TRUE.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(KW_FALSE, token1.kind);
		assertEquals(5, token1.pos);
		String text1 = KW_FALSE.getText();
		assertEquals(text1.length(), token1.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
    }
    
    @Test
   	public void testFrameKeywords() throws IllegalCharException, IllegalNumberException {
   		String input = "xloc yloc hide show move";

   		Scanner scanner = new Scanner(input);
   		scanner.scan();
   		Scanner.Token token = scanner.nextToken();
   		assertEquals(KW_XLOC, token.kind);
   		assertEquals(0, token.pos);
   		String text = KW_XLOC.getText();
   		assertEquals(text.length(), token.length);
   		assertEquals(text, token.getText());
   		Scanner.Token token1 = scanner.nextToken();
   		assertEquals(KW_YLOC, token1.kind);
   		assertEquals(5, token1.pos); 
   		String text1 = KW_YLOC.getText();
   		assertEquals(text1.length(), token.length);
   		assertEquals(text1, token1.getText());
   		Scanner.Token token2 = scanner.nextToken();
   		assertEquals(KW_HIDE, token2.kind);
   		String text2 = KW_HIDE.getText();
   		assertEquals(10, token2.pos);
   		assertEquals(text2.length(), token2.length);
   		assertEquals(text2, token2.getText());
        Scanner.Token token3 = scanner.nextToken();
   		assertEquals(KW_SHOW, token3.kind);
   		String text3 = KW_SHOW.getText();
   		assertEquals(15, token3.pos);
   		assertEquals(text3.length(), token3.length);
   		assertEquals(text3, token3.getText());
   		Scanner.Token token4 = scanner.nextToken();
   		assertEquals(KW_MOVE, token4.kind);
   		String text4 = KW_MOVE.getText();
   		assertEquals(20, token4.pos);
   		assertEquals(text4.length(), token4.length);
   		assertEquals(text4, token4.getText());
   		Scanner.Token token5 = scanner.nextToken();
   		assertEquals(Scanner.Kind.EOF,token5.kind);
   	}
    
    @Test
   	public void testImageKeywords() throws IllegalCharException, IllegalNumberException {
   		String input = "width height";

   		Scanner scanner = new Scanner(input);
   		scanner.scan();
   		Scanner.Token token = scanner.nextToken();
   		assertEquals(OP_WIDTH, token.kind);
   		assertEquals(0, token.pos);
   		String text = OP_WIDTH.getText();
   		assertEquals(text.length(), token.length);
   		assertEquals(text, token.getText());
   		Scanner.Token token1 = scanner.nextToken();
   		assertEquals(OP_HEIGHT, token1.kind);
   		assertEquals(6, token1.pos); 
   		String text1 = OP_HEIGHT.getText();
   		assertEquals(text1.length(), token1.length);
   		assertEquals(text1, token1.getText());
   		Scanner.Token token5 = scanner.nextToken();
   		assertEquals(Scanner.Kind.EOF,token5.kind);
    
    }
    
    @Test
   	public void testFilterKeywords() throws IllegalCharException, IllegalNumberException {
   		String input = "gray convolve blur scale";

   		Scanner scanner = new Scanner(input);
   		scanner.scan();
   		Scanner.Token token = scanner.nextToken();
   		assertEquals(OP_GRAY, token.kind);
   		assertEquals(0, token.pos);
   		String text = OP_GRAY.getText();
   		assertEquals(text.length(), token.length);
   		assertEquals(text, token.getText());
   		Scanner.Token token1 = scanner.nextToken();
   		assertEquals(OP_CONVOLVE, token1.kind);
   		assertEquals(5, token1.pos); 
   		String text1 = OP_CONVOLVE.getText();
   		assertEquals(text1.length(), token1.length);
   		assertEquals(text1, token1.getText());
   		Scanner.Token token2 = scanner.nextToken();
   		assertEquals(OP_BLUR, token2.kind);
   		String text2 = OP_BLUR.getText();
   		assertEquals(14, token2.pos);
   		assertEquals(text2.length(), token2.length);
   		assertEquals(text2, token2.getText());
        Scanner.Token token3 = scanner.nextToken();
   		assertEquals(KW_SCALE, token3.kind);
   		String text3 = KW_SCALE.getText();
   		assertEquals(19, token3.pos);
   		assertEquals(text3.length(), token3.length);
   		assertEquals(text3, token3.getText());
   		Scanner.Token token4 = scanner.nextToken();
   		assertEquals(Scanner.Kind.EOF,token4.kind);
   	}
    
    @Test
   	public void testKeyword() throws IllegalCharException, IllegalNumberException {
   		String input = "integer boolean image url file frame while if sleep screenwidth screenheight";

   		Scanner scanner = new Scanner(input);
   		scanner.scan();
   		Scanner.Token token = scanner.nextToken();
   		assertEquals(KW_INTEGER, token.kind);
   		assertEquals(0, token.pos);
   		String text = KW_INTEGER.getText();
   		assertEquals(text.length(), token.length);
   		assertEquals(text, token.getText());
   		Scanner.Token token1 = scanner.nextToken();
   		assertEquals(KW_BOOLEAN, token1.kind);
   		assertEquals(8, token1.pos); 
   		String text1 = KW_BOOLEAN.getText();
   		assertEquals(text1.length(), token.length);
   		assertEquals(text1, token1.getText());
   		Scanner.Token token2 = scanner.nextToken();
   		assertEquals(KW_IMAGE, token2.kind);
   		String text2 = KW_IMAGE.getText();
   		assertEquals(16, token2.pos);
   		assertEquals(text2.length(), token2.length);
   		assertEquals(text2, token2.getText());
        Scanner.Token token3 = scanner.nextToken();
   		assertEquals(KW_URL, token3.kind);
   		String text3 = KW_URL.getText();
   		assertEquals(22, token3.pos);
   		assertEquals(text3.length(), token3.length);
   		assertEquals(text3, token3.getText());
   		Scanner.Token token4 = scanner.nextToken();
   		assertEquals(KW_FILE, token4.kind);
   		String text4 = KW_FILE.getText();
   		assertEquals(26, token4.pos);
   		assertEquals(text4.length(), token4.length);
   		assertEquals(text4, token4.getText());
   		Scanner.Token token5 = scanner.nextToken();
		assertEquals(KW_FRAME, token5.kind);
		assertEquals(31, token5.pos);
		String text5 = KW_FRAME.getText();
		assertEquals(text5.length(), token5.length);
		assertEquals(text5, token5.getText());
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(KW_WHILE, token6.kind);
		assertEquals(37, token6.pos);
		String text6 = KW_WHILE.getText();
		assertEquals(text6.length(), token6.length);
		assertEquals(text6, token6.getText());
		Scanner.Token token7 = scanner.nextToken();
		assertEquals(KW_IF, token7.kind);
		assertEquals(43, token7.pos);
		String text7 = KW_IF.getText();
		assertEquals(text7.length(), token7.length);
		assertEquals(text7, token7.getText());
        Scanner.Token token8 = scanner.nextToken();
		assertEquals(OP_SLEEP, token8.kind);
		assertEquals(46, token8.pos);
		String text8 = OP_SLEEP.getText();
		assertEquals(text8.length(), token8.length);
		assertEquals(text8, token8.getText());
		Scanner.Token token9 = scanner.nextToken();
		assertEquals(KW_SCREENWIDTH, token9.kind);
		assertEquals(52, token9.pos);
		String text9 = KW_SCREENWIDTH.getText();
		assertEquals(text9.length(), token9.length);
		assertEquals(text9, token9.getText());
        Scanner.Token token10 = scanner.nextToken();
		assertEquals(KW_SCREENHEIGHT, token10.kind);
		assertEquals(64, token10.pos);
		String text10 = KW_SCREENHEIGHT.getText();
		assertEquals(text10.length(), token10.length);
		assertEquals(text10, token10.getText());
   		Scanner.Token token11 = scanner.nextToken();
   		assertEquals(Scanner.Kind.EOF,token11.kind);
   	}
    
    @Test
    public void Integer() throws IllegalCharException, IllegalNumberException {
		String input = "1007856 3476";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		String text = "1007856";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(INT_LIT, token1.kind);
		assertEquals(8, token1.pos); 
		String text1 = "3476";
		assertEquals(text1.length(), token1.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
    }
    
    @Test
    public void Ident() throws IllegalCharException, IllegalNumberException {
		String input = "var_iable consta1234nt nAM$e";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(0, token.pos);
		String text = "var_iable";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(IDENT, token1.kind);
		assertEquals(10, token1.pos); 
		String text1 = "consta1234nt";
		assertEquals(text1.length(), token1.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(IDENT, token2.kind);
		String text2 = "nAM$e";
		assertEquals(23, token2.pos);
		assertEquals(text2.length(), token2.length);
		assertEquals(text2, token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
    }
    
    @Test
    public void Comment() throws IllegalCharException, IllegalNumberException {
		String input = "1234/*   *a/    */     /";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(0, token.pos);
		String text = "1234";
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(DIV, token1.kind);
		assertEquals(23, token1.pos);
		String text1 = DIV.getText();
		assertEquals(text1.length(), token1.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
    }
    
    @Test
    public void Comment1() throws IllegalCharException, IllegalNumberException {
		String input = "width/* bkdcsb ****    */    5674";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(OP_WIDTH, token.kind);
		assertEquals(0, token.pos);
		String text = OP_WIDTH.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(INT_LIT, token1.kind);
		assertEquals(29, token1.pos);
		String text1 = "5674";
		assertEquals(text1.length(), token1.length);
		assertEquals(text1, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
    }
    
    @Test
	public void IllegCharacter() throws IllegalCharException, IllegalNumberException{
		String input = "^";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();		
	}
    
    @Test
    public void LineComment1() throws IllegalCharException, IllegalNumberException {
		String input = "\n";
		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(1,token.getLinePos().line);
		assertEquals(Scanner.Kind.EOF,token.kind);
		assertEquals(0,token.getLinePos().posInLine);

    }
    
    @Test
    public void LineComment2() throws IllegalCharException, IllegalNumberException {
		String input = "\n /* 1234 */ ";
		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(1,token.getLinePos().line);
		assertEquals(Scanner.Kind.EOF,token.kind);
		assertEquals(12,token.getLinePos().posInLine);

    }
    
    @Test
    public void Line3() throws IllegalCharException, IllegalNumberException {
		String input = "integer \n +/**/ ";
		
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_INTEGER,token.kind);
		assertEquals(0,token.pos);
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(1,token1.getLinePos().line);
		assertEquals(10,token1.pos);
		assertEquals(1,token1.getLinePos().posInLine);
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);		
    }
   
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}

//TODO  more tests
	
}
