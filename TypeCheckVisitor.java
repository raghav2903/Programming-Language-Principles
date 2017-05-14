package cop5556sp17;
import java.util.*;
import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

   @SuppressWarnings("serial")
   public static class TypeCheckException extends Exception {
      TypeCheckException(String message) {
         super(message);
      }
   }

   SymbolTable symtab = new SymbolTable();

   @Override
   public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
      Chain e0 = binaryChain.getE0();
      ChainElem e1 = binaryChain.getE1();
      Token op = binaryChain.getArrow();
      e0.visit(this, arg);
      e1.visit(this, arg);
      if(op.isKind(ARROW))
      {
         if((e0.getTypeName().equals(URL)) && (e1.getTypeName().equals(IMAGE)))
         {
            binaryChain.setTypeName(IMAGE);
         }
         else if((e0.getTypeName().equals(FILE)) && (e1.getTypeName().equals(IMAGE)))
         {
            binaryChain.setTypeName(IMAGE);
         }
         else if((e0.getTypeName().equals(FRAME)) && (e1 instanceof FrameOpChain) && ((e1.getFirstToken().isKind(KW_XLOC)) || (e1.getFirstToken().isKind(KW_YLOC))))
         {
            binaryChain.setTypeName(INTEGER);
         }
         else if((e0.getTypeName().equals(FRAME)) && (e1 instanceof FrameOpChain) && ((e1.getFirstToken().isKind(KW_SHOW)) || ((e1.getFirstToken().isKind(KW_HIDE)) || (e1.getFirstToken().isKind(KW_MOVE)))))
         {
            binaryChain.setTypeName(FRAME);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1 instanceof ImageOpChain) && ((e1.getFirstToken().isKind(OP_WIDTH)) || ((e1.getFirstToken().isKind(OP_HEIGHT)))))
         {
            binaryChain.setTypeName(INTEGER);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1.getTypeName().equals(FRAME)))
         {
            binaryChain.setTypeName(FRAME);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1.getTypeName().equals(FILE)))
         {
            binaryChain.setTypeName(NONE);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1 instanceof ImageOpChain) && ((e1.getFirstToken().isKind(KW_SCALE))))
         {
            binaryChain.setTypeName(IMAGE);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1 instanceof IdentChain) && e1.getTypeName() == IMAGE)
         {
            binaryChain.setTypeName(IMAGE);
         }
         else if((e0.getTypeName().equals(INTEGER)) && (e1 instanceof IdentChain) && e1.getTypeName() == INTEGER)
         {
            binaryChain.setTypeName(INTEGER);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1 instanceof FilterOpChain) && ((e1.getFirstToken().isKind(OP_GRAY)) || (e1.getFirstToken().isKind(OP_BLUR)) || (e1.getFirstToken().isKind(OP_CONVOLVE))))
         {
            binaryChain.setTypeName(IMAGE);
         }
         else
         {
            throw new TypeCheckException("Error in visitBinaryChain ARROW");
         }
      }
      else if(op.isKind(BARARROW))
      {
         if((e0.getTypeName().equals(IMAGE)) && (e1 instanceof FilterOpChain) && ((e1.getFirstToken().isKind(OP_GRAY)) || (e1.getFirstToken().isKind(OP_BLUR)) || (e1.getFirstToken().isKind(OP_CONVOLVE))))
         {
            binaryChain.setTypeName(IMAGE);
         }
         else
         {
            throw new TypeCheckException("Error in visitBinaryChain BARARROW");
         }

      }
      else
      {
         throw new TypeCheckException("Error in visitBinaryChain");
      }

      return null;
   }

   @Override
   public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      Expression e0 = binaryExpression.getE0();
      Expression e1 = binaryExpression.getE1();
      Token op = binaryExpression.getOp();
      e0.visit(this, arg);
      e1.visit(this, arg);
      if(op.isKind(MINUS) || op.isKind(PLUS))
      {
         if((e0.getTypeName().equals(INTEGER)) && (e1.getTypeName().equals(INTEGER)))
         {
            binaryExpression.setTypeName(INTEGER);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1.getTypeName().equals(IMAGE)))
         {
            binaryExpression.setTypeName(IMAGE);
         }
         else
         {
            throw new TypeCheckException("Error in visitBinaryExpression PLUS/MINUS");
         }

      }
      else if(op.isKind(TIMES) || op.isKind(DIV) || op.isKind(MOD))
      {
    	  if((e0.getTypeName().equals(INTEGER)) && (e1.getTypeName().equals(INTEGER)))
          {
             binaryExpression.setTypeName(INTEGER);
          }
    	  else if((e0.getTypeName().equals(INTEGER)) && (e1.getTypeName().equals(IMAGE)))
         {
            binaryExpression.setTypeName(IMAGE);
         }
         else if((e0.getTypeName().equals(IMAGE)) && (e1.getTypeName().equals(INTEGER)))
         {
            binaryExpression.setTypeName(IMAGE);
         }
         else
         {
            throw new TypeCheckException("Error in visitBinaryExpression TIMES, DIV, MOD");
         }
      }
      else if(op.isKind(LT) || op.isKind(GT) || op.isKind(LE) || op.isKind(GE))
      {
         if((e0.getTypeName().equals(INTEGER)) && (e1.getTypeName().equals(INTEGER)))
         {
            binaryExpression.setTypeName(BOOLEAN);
         }
         else if((e0.getTypeName().equals(BOOLEAN)) && (e1.getTypeName().equals(BOOLEAN)))
         {
            binaryExpression.setTypeName(BOOLEAN);
         }
         else
         {
            throw new TypeCheckException("Error in visitBinaryExpression LT/GT/LE/GE");
         }
      }
      else if(op.isKind(AND) || op.isKind(OR))
      {
    	  if((e0.getTypeName().equals(BOOLEAN)) && (e1.getTypeName().equals(BOOLEAN)))
          {
             binaryExpression.setTypeName(BOOLEAN);
          }
          else
          {
             throw new TypeCheckException("Error in visitBinaryExpression AND/OR");
          }

      }
      else if(op.isKind(EQUAL) || op.isKind(NOTEQUAL))
      {
         if((e0.getTypeName()).equals(e1.getTypeName()))
         {
            binaryExpression.setTypeName(BOOLEAN);
         }
         else
         {
            throw new TypeCheckException("Error in visitBinaryExpression EQUAL/NOTEQUAL");
         }
      }
      else
      {
         throw new TypeCheckException("Error in visitBinaryExpression");
      }
      return null;
   }

   @Override
   public Object visitBlock(Block block, Object arg) throws Exception {
      symtab.enterScope();
      ArrayList<Dec> decList = new ArrayList<Dec>();
      ArrayList<Statement> statementList = new ArrayList<Statement>();
      decList = block.getDecs();
      statementList = block.getStatements();

      for(int i = 0; i<= decList.size()-1;i++)
      {
         decList.get(i).visit(this, arg);
      }

      for(int i = 0; i<= statementList.size()-1;i++)
      {
         statementList.get(i).visit(this, arg);
      }
      symtab.leaveScope();

      return null;
   }

   @Override
   public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
      booleanLitExpression.setTypeName(BOOLEAN);
      return null;
   }

   @Override
   public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
      Tuple tuple = filterOpChain.getArg();
      tuple.visit(this, arg);
      if((tuple.getExprList().size() == 0))
      {
         filterOpChain.setTypeName(IMAGE);
      }
      else
      {
         throw new TypeCheckException("Error in visitFilterOpChain");
      }
      return null;
   }

   @Override
   public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
      Tuple tuple = frameOpChain.getArg();
      tuple.visit(this, arg);
      if(frameOpChain.getFirstToken().isKind(KW_SHOW) || frameOpChain.getFirstToken().isKind(KW_HIDE))
      {
         if((tuple.getExprList().size() == 0))
         {
            frameOpChain.setTypeName(NONE);
         }
         else
         {
        	 throw new TypeCheckException("Error in visitFrameOpChain SHOW/HIDE");
         }

      }
      else if(frameOpChain.getFirstToken().isKind(KW_XLOC) || frameOpChain.getFirstToken().isKind(KW_YLOC))
      {
         if((tuple.getExprList().size() == 0))
         {
            frameOpChain.setTypeName(INTEGER);
         }
         else
         {
        	 throw new TypeCheckException("Error in visitFrameOpChain XLOC/YLOC");
         }

      }
      else if(frameOpChain.getFirstToken().isKind(KW_MOVE))
      {
         if((tuple.getExprList().size() == 2))
         {
            frameOpChain.setTypeName(NONE);
         }
         else
         {
        	 throw new TypeCheckException("Error in visitFrameOpChain MOVE");
         }

      }
      else
      {
         throw new TypeCheckException("Error in visitFrameOpChain");
      }
      return null;
   }

   @Override
   public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
      Dec d = symtab.lookup(identChain.getFirstToken().getText());
      identChain.setDec(d);
      if( d == null)
      {
         throw new TypeCheckException("Error in visitIdentChain");
      }
      else
      {
         identChain.setTypeName(d.getTypeName());
      }
      return null;
   }

   @Override
   public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
      Dec d = symtab.lookup(identExpression.getFirstToken().getText());
      if( d == null)
      {
         throw new TypeCheckException("Error in visitIdentExpression");
      }
      else
      {
         identExpression.setTypeName(d.getTypeName());
         identExpression.setDec(d);
      }
      return null;
   }

   @Override
   public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
      Expression exp = ifStatement.getE();
      exp.visit(this, arg);
      if((exp.getTypeName().equals(TypeName.BOOLEAN)))
      {
         Block b = ifStatement.getB();
         b.visit(this, arg);
      }
      else
      {
         throw new TypeCheckException("Error in visitIfStatement");
      }
      return null;
   }

   @Override
   public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
      intLitExpression.setTypeName(INTEGER);
      return null;
   }

   @Override
   public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
      Expression exp = sleepStatement.getE();
      exp.visit(this, arg);
      if(!(exp.getTypeName().equals(TypeName.INTEGER)))
      {
         throw new TypeCheckException("Error in visitSleepStatement");
      }

      return null;
   }

   @Override
   public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
      Expression exp = whileStatement.getE();
      exp.visit(this, arg);
      if((exp.getTypeName().equals(TypeName.BOOLEAN)))
      {
         Block b = whileStatement.getB();
         b.visit(this, arg);
      }
      else
      {
         throw new TypeCheckException("Error in visitWhileStatement");
      }

      return null;
   }

   @Override
   public Object visitDec(Dec declaration, Object arg) throws Exception {
      boolean insert_indicator = symtab.insert(declaration.getIdent().getText(), declaration);
      if(!insert_indicator)
      {
    	  throw new TypeCheckException("Error in visitDec");
      }
      switch (declaration.firstToken.kind) {
         case KW_BOOLEAN:
            declaration.setTypeName(BOOLEAN);
            break;
         case KW_INTEGER:
            declaration.setTypeName(INTEGER);
            break;
         case KW_IMAGE:
            declaration.setTypeName(IMAGE);
            break;
         case KW_FRAME:
            declaration.setTypeName(FRAME);
            break;
      }
      return null;
   }

   @Override
   public Object visitProgram(Program program, Object arg) throws Exception {
      ArrayList<ParamDec> params = program.getParams();
      for(int i = 0;i<=params.size()-1;i++)
      {
         params.get(i).visit(this, arg);
      }
      Block block = program.getB();
      block.visit(this, arg);

      return null;
   }

   @Override
   public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
      Expression exp = assignStatement.getE();
      exp.visit(this, arg);
      IdentLValue ident_l_value = assignStatement.getVar();
      ident_l_value.visit(this, arg);
      if(ident_l_value.getTypeName() != exp.getTypeName())
      {
         throw new TypeCheckException("Error in visitAssignmentStatement");
      }
      return null;
   }

   @Override
   public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
      Dec d = symtab.lookup(identX.getFirstToken().getText());
      if( d == null)
      {
         throw new TypeCheckException("Error in visitIdentLvalue");
      }
      else
      {
         identX.setDec(d);
         identX.setTypeName(d.getTypeName());
      }
      return null;
   }

   @Override
   public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
      boolean insert_indicator = symtab.insert(paramDec.getIdent().getText(), paramDec);
      if(!insert_indicator)
      {
        	  throw new TypeCheckException("Error in visitParamDec");
      }
      switch (paramDec.firstToken.kind) {
      case KW_BOOLEAN:
    	  paramDec.setTypeName(BOOLEAN);
         break;
      case KW_URL:
    	  paramDec.setTypeName(URL);
         break;
      case KW_FILE:
    	  paramDec.setTypeName(FILE);
         break;
      case KW_INTEGER:
    	  paramDec.setTypeName(INTEGER);
         break;
   }
      return null;
   }

   @Override
   public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
      constantExpression.setTypeName(INTEGER);
      return null;
   }

   @Override
   public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
      Tuple tuple = imageOpChain.getArg();
      tuple.visit(this, arg);
      if(imageOpChain.getFirstToken().isKind(OP_WIDTH) || imageOpChain.getFirstToken().isKind(OP_HEIGHT))
      {
         if((tuple.getExprList().size() == 0))
         {
            imageOpChain.setTypeName(INTEGER);
         }
         else
         {
            throw new TypeCheckException("Error in visitImageOpChain OP_WIDTH/OP_HEIGHT");
         }

      }
      else if(imageOpChain.getFirstToken().isKind(KW_SCALE))
      {
         if((tuple.getExprList().size() == 1))
         {
            imageOpChain.setTypeName(IMAGE);
         }
         else
         {
            throw new TypeCheckException("Error in visitImageOpChain KW_SCALE");
         }

      }
      else
      {
         throw new TypeCheckException("Error in visitImageOpChain");
      }

      return null;
   }

   @Override
   public Object visitTuple(Tuple tuple, Object arg) throws Exception {
      List<Expression> tup_list = tuple.getExprList();
      for(Expression exp: tup_list)
      {
         exp.visit(this, arg);
         if(!(exp).getTypeName().equals(TypeName.INTEGER))
         {
            throw new TypeCheckException("Error in visitTuple");
         }
      }
      return null;
   }


}