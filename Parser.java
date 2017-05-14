package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}
	
	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	public ASTNode parse() throws SyntaxException {
		ASTNode e = null;
	
		e = program();
		matchEOF();
		
		return e;
	}

	public Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;

		 e0 = term();
		
		while(t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL))
		{
			Token op = t;
			consume();
			e1 = term();
			e0 = new BinaryExpression(firstToken,e0,op,e1);
		}
		return e0;
		//throw new SyntaxException("In expression");
	}

	public Expression term() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		
		e0 = elem();
		
		while(t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR))
		{
			Token op = t;
			consume();
			e1 = elem();
			e0 = new BinaryExpression(firstToken,e0,op,e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	public Expression elem() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		
		e0 = factor();
		
		while(t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND)|| t.isKind(MOD))
		{
			Token op = t;
			consume();
			e1 = factor();
			e0 = new BinaryExpression(firstToken,e0,op,e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	public Expression factor() throws SyntaxException {
		Token firstToken = t;
		Expression e = null;
		Kind kind = t.kind;
		switch (kind) 
		{
		
		case IDENT: 
		{
			e = new IdentExpression(firstToken);
			consume();
		}
			break;
			
		case INT_LIT: 
		{
			e = new IntLitExpression(firstToken);
			consume();
		}
			break;
			
		case KW_TRUE:
		case KW_FALSE: 
		{
			e = new BooleanLitExpression(firstToken);
			consume();
		}
			break;
			
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: 
		{
			e = new ConstantExpression(firstToken);
			consume();
		}
			break;
			
		case LPAREN: 
		{
			consume();
			e = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		return e;
	}

	public Block block() throws SyntaxException {
		Token firstToken = t;
		Block e = null;
		ArrayList<Dec> decs = new ArrayList<Dec>();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		
		
		if(t.isKind(LBRACE))
		{
		    match(LBRACE);
			
			while((!t.isKind(RBRACE)))
			{
				
			  if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME))
	           {
	        	  decs.add(dec());
	           }
			
			  else
			  {
				  statements.add(statement());
	          }
		
			}
		match(RBRACE);
		
		e = new Block(firstToken,decs,statements);
		return e;
		
		}
		else
		{
			throw new SyntaxException("In block");
		}
		
	}

	public Program program() throws SyntaxException {
		Token firstToken = t;
		ArrayList<ParamDec>paramList = new ArrayList<ParamDec>();
		Program e0 = null;
		Block b = null;
		
		if(t.isKind(IDENT))
		{
			consume();
			
			if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN))
			{
				paramList.add(paramDec());
			
				while(t.isKind(COMMA))
			     {
				   consume();
				   paramList.add(paramDec());
			     }
			
				b = block();
		    }
			else
			{
				b = block();
			}
		}
		else
		{
		   throw new SyntaxException("In pogram");
		}
		
		e0 = new Program(firstToken,paramList,b);
		return e0;
	}

	public ParamDec paramDec() throws SyntaxException {
		Token firstToken = t;
		ParamDec e = null;
		
        if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN))
        {
        	e = new ParamDec(consume(),match(IDENT));
        	return e;
        }
        else
        {
        	throw new SyntaxException("In paramdec");
        }
	}

	public Dec dec() throws SyntaxException {
		Token firstToken = t;
		Dec e = null;
		
		if(t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME))
		{
			e = new Dec(consume(),match(IDENT));
        	return e;
        }
		else
        {
        	throw new SyntaxException("In dec");
        }
	}

	public Statement statement() throws SyntaxException {
		Token firstToken = t;
		IdentLValue var = null;
		Statement e0 = null;
		Block b = null;
		Expression e = null;
		
		
		if(t.isKind(OP_SLEEP))
		{
			consume();
			e = expression();
			e0 = new SleepStatement(firstToken,e);
			match(SEMI);
		}
		else if(t.isKind(KW_WHILE))
		{
			consume();
			match(LPAREN);
			e = expression();
			match(RPAREN);
			b = block();
			e0 = new WhileStatement(firstToken,e,b);
		}
		else if(t.isKind(KW_IF))
		{
			consume();
			match(LPAREN);
			e = expression();
			match(RPAREN);
			b = block();
			e0 = new IfStatement(firstToken,e,b);
		}
		 else if(t.isKind(IDENT) && scanner.peek().isKind(ASSIGN))
		 {
			//if(scanner.peek().isKind(ASSIGN))
			//{
			var = new IdentLValue(t);
			consume();
			match(ASSIGN);
			e = expression();
			match(SEMI);
			e0 = new AssignmentStatement(firstToken,var,e);
			/*}
			else
			{
				chain();
				match(SEMI);
			}*/
		 }
		 else if(t.isKind(IDENT)||t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE) || t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC) || t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE))
		 {
			 e0 = chain();
			 match(SEMI);
			 
		 }
	     else
		 {
			 throw new SyntaxException("In statement");
		 }
		
		return e0;
	
	}

	public Chain chain() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Chain e0 = null;
		ChainElem e1 = null;
		
		e0 = chainElem();
		
		if(t.isKind(ARROW) || t.isKind(BARARROW))
		{
			op  = t;
			consume();
		}
		else
		{
			throw new SyntaxException("In chain");
		}
	
		e1 = chainElem();
		e0 = new BinaryChain(firstToken,e0,op,e1);
		
		while(t.isKind(ARROW) || t.isKind(BARARROW))
		{
			op = t;
			consume();
			e1 = chainElem();
			e0 = new BinaryChain(firstToken,e0,op,e1);
		}
		
	 return e0;

	}

	public ChainElem chainElem() throws SyntaxException {
		Token firstToken = t;
		ChainElem e = null;
	
		if(t.isKind(IDENT))
		{
			e = new IdentChain(firstToken);
			consume();
			return e;
		}
		else if(t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE))
		{
			consume();
			Tuple arg = arg();
			e = new FilterOpChain(firstToken,arg);
			return e;
		}
		else if(t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC))
		{
			consume();
			Tuple arg = arg();
			e = new FrameOpChain(firstToken,arg);
			return e;
		}
		else if(t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE))
		{
			consume();
			Tuple arg = arg();
			e = new ImageOpChain(firstToken,arg);
			return e;
		}
		else
        {
        	throw new SyntaxException("In chainElem");
        }
	}

	public Tuple arg() throws SyntaxException {
		Tuple e = null;
		Token firstToken = t;
		ArrayList<Expression>argList = new ArrayList<Expression>();
		
		if(t.isKind(LPAREN)) 
		{
			consume();
			argList.add(expression());
		 
			while(t.isKind(COMMA))
		   {
			  consume();
			  argList.add(expression());
		   }
		
			match(RPAREN);
			e = new Tuple(firstToken,argList);
			return e;
		
		}
		else
		{
			e = new Tuple(firstToken,argList);
			return e ;
		}
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
