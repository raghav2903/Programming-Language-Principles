package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testelem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x * 5";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void testelem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x * (x/10)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.elem();
	}
	
	@Test
	public void expression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(x >= y)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.expression();
	}
	
	
	@Test
	public void dec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer x boolean y image z frame a";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.dec();
	}
	
	@Test
	public void paramDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url x file y integer z boolean a";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.paramDec();
	}
	
	@Test
	public void chainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x blur(x) gray(z) convolve(a) show(b) hide(c) move(d) xloc(e) yloc(f) width(g) height(h) scale(i)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.chainElem();
	}
	
	@Test
	public void term() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x + 5 - true | false + screenwidth - screenheight + (5-x)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.term();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(3,)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}
	
	@Test
	public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.arg();
	}
	
	@Test
	public void testArg2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(x,y,z,a)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x |-> y -> z";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.chain();
	}
	
	@Test
	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(x<=y){}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(x == y){ integer a }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x <- y;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep x!=y ;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void block1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{ boolean x }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.block();
	}
	
	@Test
	public void block2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{ sleep x<=y; while(x>y){} if(x==y){} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.block();
	}
	
	@Test
	public void program1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x {} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}
	
	@Test
	public void program2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x boolean y {} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}
	
	@Test
	public void program3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "x boolean y,integer z,file a,url b {} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}

}
