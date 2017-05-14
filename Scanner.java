package cop5556sp17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.*;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;

public class Scanner {
	/**
	 * Kind enum
	 */

	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"),
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"),
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"),
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"),
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"),
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="),
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"),
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"),
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"),
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"),
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}


	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;

		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}




	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;

		//returns the text of this Token
		public String getText() {
			if(this.kind == Kind.EOF)
			{
				return Kind.EOF.getText();
			}
			else
			{
			return chars.substring(this.pos,this.pos+this.length);
			}
		}
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			LinePos lipo;
			int ip = Arrays.binarySearch(new_line_charac.toArray(),this.pos);

			if(ip>=-2)
			{
				lipo = new LinePos(0,pos);
			}
			else
			{
				ip = -(ip+1);
				lipo = new LinePos(ip-1,pos-new_line_charac.get(ip-1)-1);
			}

			return lipo;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/**
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 *
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			return Integer.parseInt(chars.substring(this.pos,this.pos+this.length));
		}

		public boolean isKind(Kind k)
		{
		if(this.kind == k)
			return true;
		else
			return false;
		}

	}

	 public static enum State{Start, AFTER_EQ, AFTER_NOT, AFTER_LT, AFTER_GT, AFTER_MINUS, AFTER_OR, AFTER_DIV,IN_IDENT,IN_DIGIT,INSIDE_COMMENT,END_COMMENT };
	 Map<Kind, String> data = new HashMap<Kind, String>();
	 {
	        data.put(Kind.KW_INTEGER, Kind.KW_INTEGER.getText());
	        data.put(Kind.KW_BOOLEAN, Kind.KW_BOOLEAN.getText());
	        data.put(Kind.KW_IMAGE, Kind.KW_IMAGE.getText());
	        data.put(Kind.KW_URL, Kind.KW_URL.getText());
	        data.put(Kind.KW_FILE, Kind.KW_FILE.getText());
	        data.put(Kind.KW_FRAME, Kind.KW_FRAME.getText());
	        data.put(Kind.KW_WHILE, Kind.KW_WHILE.getText());
	        data.put(Kind.KW_IF, Kind.KW_IF.getText());
	        data.put(Kind.KW_TRUE, Kind.KW_TRUE.getText());
	        data.put(Kind.KW_FALSE, Kind.KW_FALSE.getText());
	        data.put(Kind.OP_BLUR, Kind.OP_BLUR.getText());
	        data.put(Kind.OP_GRAY, Kind.OP_GRAY.getText());
	        data.put(Kind.OP_CONVOLVE, Kind.OP_CONVOLVE.getText());
	        data.put(Kind.KW_SCREENHEIGHT, Kind.KW_SCREENHEIGHT.getText());
	        data.put(Kind.KW_SCREENWIDTH, Kind.KW_SCREENWIDTH.getText());
	        data.put(Kind.OP_WIDTH, Kind.OP_WIDTH.getText());
	        data.put(Kind.OP_HEIGHT, Kind.OP_HEIGHT.getText());
	        data.put(Kind.KW_XLOC, Kind.KW_XLOC.getText());
	        data.put(Kind.KW_YLOC, Kind.KW_YLOC.getText());
	        data.put(Kind.KW_HIDE, Kind.KW_HIDE.getText());
	        data.put(Kind.KW_SHOW, Kind.KW_SHOW.getText());
	        data.put(Kind.KW_MOVE, Kind.KW_MOVE.getText());
	        data.put(Kind.OP_SLEEP, Kind.OP_SLEEP.getText());
	        data.put(Kind.KW_SCALE, Kind.KW_SCALE.getText());
	        data.put(Kind.EOF, Kind.EOF.getText());
	 }

	 List<Integer> new_line_charac = new ArrayList<Integer>();


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		}


	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 *
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException,IllegalNumberException//
	{
		int pos = 0;
		final int strlen = chars.length();
		State s = State.Start;
		int ch;
		int startPos=0;
		new_line_charac.add(0);

		while(pos<=strlen)
		{
			ch = pos<strlen?chars.charAt(pos):-1;

			switch(s)
			{

			case Start:
				{

                    while(Character.isWhitespace(ch) && ch !='\n')
					{
						pos++;
						ch = pos<strlen?chars.charAt(pos):-1;
					}

					startPos=pos;

					switch(ch)
					{

					case -1: //EOF
						tokens.add(new Token(Kind.EOF,startPos,0));
						pos++;//??
						break;

					case ',':
						tokens.add(new Token(Kind.COMMA,startPos,1));
						pos++;
						break;

					case ';':
						tokens.add(new Token(Kind.SEMI,startPos,1));
						pos++;
						break;

					case ')':
						tokens.add(new Token(Kind.RPAREN,startPos,1));
						pos++;
						break;

					case '&':
						tokens.add(new Token(Kind.AND,startPos,1));
						pos++;
						break;

					case '(':
						tokens.add(new Token(Kind.LPAREN,startPos,1));
						pos++;
						break;

					case '+':
						tokens.add(new Token(Kind.PLUS,startPos,1));
						pos++;
						break;

					case '{':
						tokens.add(new Token(Kind.LBRACE,startPos,1));
						pos++;
						break;

					case '}':
						tokens.add(new Token(Kind.RBRACE,startPos,1));
						pos++;
						break;

					case '*':
						tokens.add(new Token(Kind.TIMES,startPos,1));
						pos++;
						break;

					case '%':
						tokens.add(new Token(Kind.MOD,startPos,1));
						pos++;
						break;

					case '0':
						tokens.add(new Token(Kind.INT_LIT,startPos,1));
						pos++;
						break;

					case '=':
						s=State.AFTER_EQ;
						pos++;
						break;

					case '!':
						s=State.AFTER_NOT;
						pos++;
						break;

					case '|':
						s=State.AFTER_OR;
						pos++;
						break;

					case '<':
						s=State.AFTER_LT;
						pos++;
						break;

					case '>':
						s=State.AFTER_GT;
						pos++;
						break;

					case '-':
						s=State.AFTER_MINUS;
						pos++;
						break;

					case '/':
						s=State.AFTER_DIV;
						pos++;
						break;

					case '\n':
						new_line_charac.add(pos++);
						break;

					default: {
			            if (Character.isDigit(ch))
			            {
			            	s = State.IN_DIGIT;
			            	pos++;
			            }
			            else if (Character.isJavaIdentifierStart(ch))
			            {
			                 s = State.IN_IDENT;
			                 pos++;
			            }
			            else
			            {
			            	throw new IllegalCharException("Illegal char " +ch+" at pos "+pos);
			            }
			            break;
			          }
			    }
			}
			break;


			case AFTER_EQ:

				if(ch == '=')
				{
					tokens.add(new Token(Kind.EQUAL,startPos,2));
					pos++;
					s=State.Start;
				}
				else
				{
					throw new IllegalCharException("Illegal char starting at pos"+startPos);
				}

				break;

			case AFTER_NOT:

				if(ch == '=')
				{
					tokens.add(new Token(Kind.NOTEQUAL,startPos,2));
					pos++;
					s=State.Start;
				}
				else
				{
					tokens.add(new Token(Kind.NOT,startPos,1));
					s=State.Start;
				}

				break;

			case AFTER_LT:

				if(ch == '=')
				{
					tokens.add(new Token(Kind.LE,startPos,2));
					pos++;
					s=State.Start;
				}
				else if(ch == '-')
				{
					tokens.add(new Token(Kind.ASSIGN,startPos,2));
					pos++;
					s=State.Start;
				}
				else
				{
					tokens.add(new Token(Kind.LT,startPos,1));
					s=State.Start;
				}

				break;

			case AFTER_GT:

				if(ch == '=')
				{
					tokens.add(new Token(Kind.GE,startPos,2));
					pos++;
					s=State.Start;
				}
				else
				{
					tokens.add(new Token(Kind.GT,startPos,1));
					s=State.Start;
				}

				break;

			case AFTER_MINUS:

				if(ch == '>')
				{
					tokens.add(new Token(Kind.ARROW,startPos,2));
					pos++;
					s=State.Start;
				}
				else
				{
					tokens.add(new Token(Kind.MINUS,startPos,1));
					s=State.Start;
				}

				break;

			case AFTER_OR:

				if(ch == '-')
				{
					if( pos<strlen-1 && (ch=chars.charAt(pos+1)) == '>' )
					{
						tokens.add(new Token(Kind.BARARROW,startPos,3));
						pos+=2;
						s=State.Start;
					}
					else
					{
						tokens.add(new Token(Kind.OR,startPos,1));
						tokens.add(new Token(Kind.MINUS, (startPos+1) ,1));
						pos++;
						s=State.Start;
					}
				}
				else
				{
					tokens.add(new Token(Kind.OR,startPos,1));
					s=State.Start;
				}

				break;

			case AFTER_DIV:

				if(ch == '*')
				{
					pos++;
					s = State.INSIDE_COMMENT;
				}
				else
				{
					tokens.add(new Token(Kind.DIV,startPos,1));
					s=State.Start;
				}

				break;

			case INSIDE_COMMENT:

				while(pos < strlen-1 && chars.charAt(pos) != '*')
				{
					if(chars.charAt(pos) == '\n')
						new_line_charac.add(pos);
					pos++;
				}

			    if(pos >= (strlen-1))
			    {
			    	if(chars.charAt(pos) == '\n')
			    		new_line_charac.add(pos);

			    	if(pos == strlen-1)
			    		pos++;

			    	s = State.Start;
			    }
			    else
			    {
			    	pos++;
			    	s = State.END_COMMENT;
			    }

				break;

			case END_COMMENT:

				if(chars.charAt(pos) == '/')
				{
					if(chars.charAt(pos) == '\n')
						new_line_charac.add(pos);
					pos++;
					s = State.Start;
				}
				else
				{
					s = State.INSIDE_COMMENT;
					if(chars.charAt(pos) == '\n')
						new_line_charac.add(pos);
				}

				break;

			case IN_IDENT:

			      if (Character.isJavaIdentifierPart(ch))
			      {
			          pos++;
			      }
			      else
			      {
			    	  boolean flag = false;
			    	  for(Kind k: data.keySet())
			    	  {
			    	    if((chars.substring(startPos, pos)).contentEquals(data.get(k)))
			    	    {
			    	    	flag = true;
			    	    	tokens.add(new Token(k, startPos, pos - startPos));
				            s = State.Start;
			    	    }

			    	  }
			    	  if(!flag)
			    	  {
			    	  tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
				      s = State.Start;
			    	  }
			      }

			      break;

			case IN_DIGIT:

				if (Character.isDigit(ch))
				{
		           pos++;
		        }
				else
				{
				   try
				   {
				      if(Integer.parseInt(chars.substring(startPos, pos)) <= Integer.MAX_VALUE)
				       {
		                 tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
		                 s = State.Start;
				       }
				   }
				   catch(NumberFormatException e)
				   {
					   throw new IllegalNumberException("Illegal Number starting at pos"+" "+pos);
				   }
		        }

			    break;

			default:
				//throw new IllegalCharException("Illegal Character at pos"+ " "+pos);

				break;

			}//state switch

		}//while


		//tokens.add(new Token(Kind.EOF,pos,0));
		return this;
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}

	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the
	 * given token.
	 *
	 * Line numbers start counting at 0
	 *
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return t.getLinePos();
	}

}

