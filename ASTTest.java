package cop5556sp17;

import static cop5556sp17.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.*;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}
	
	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BooleanLitExpression.class, ast.getClass());
	}
	
	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(ConstantExpression.class, ast.getClass());
	}




	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
		
	@Test
	public void Dec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.dec();
		assertEquals(Dec.class, ast.getClass());
	}
	
	@Test
	public void ParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url abc, boolean abc, file a, image c, frame d";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
	}
	
	@Test
	public void Tuple() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(abc,bad)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
	}
	
	@Test
	public void ChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "gray(x,y)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(FilterOpChain.class, ast.getClass());
	}
	
	@Test
	public void ChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "scale(x,y)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(ImageOpChain.class, ast.getClass());
	}
	
	@Test
	public void ChainElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "move(x,y)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(FrameOpChain.class, ast.getClass());
	}
	
	@Test
	public void ChainElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(IdentChain.class, ast.getClass());
	}
	
	@Test
	public void Chain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc->de";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
	}
	
	@Test
	public void Chain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "scale(x,y)->show(a,b)->blur(c,d)->gray(e,f)|->convolve(g,h)->hide(i,j)|->move(k,l)->xloc(m,n)|->yloc(o,p)->width";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
	}
	
	@Test
	public void SleepStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep x<=y;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
	}
	
	@Test
	public void WhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(x<=y){integer abc d<-5;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
	}
	
	@Test
	public void IfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(x<=y){integer abc d<-5;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
	}
	
	@Test
	public void AssignmentStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "d<-5;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(AssignmentStatement.class, ast.getClass());
	}
	
	@Test
	public void Program() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc {integer abc}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass());
	}
	
	@Test
	public void Program1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc url d { integer a }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		assertEquals(Program.class, ast.getClass());
	}
	
	@Test
	public void StatementChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "scale(x,y)->show(a,b);";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(BinaryChain.class, ast.getClass());
	}
	
	


}
