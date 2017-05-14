package cop5556sp17;
import cop5556sp17.AST.*;
import  cop5556sp17.*;
import  cop5556sp17.AST.*;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;
public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	int slotCount = 1;
	Stack<Integer> stack = new Stack<Integer>();

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 2);
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params)
			dec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		mv.visitLocalVariable("Counter", "I", null, constructorStart, constructorEnd, 2);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method


		cw.visitEnd();//end of class

		//generate classfile and return it
		return cw.toByteArray();
	}

	String getTypeFromToken(Token t) throws SyntaxException
	{
		String s = null;
		switch(t.kind)
		{
		case KW_INTEGER:
			s = "I";
			break;

		case KW_BOOLEAN:
			s = "Z";
			break;

		case KW_URL:
			s = Type.getTypeName(t).getJVMTypeDesc();
			break;

		case KW_FILE:
			s = Type.getTypeName(t).getJVMTypeDesc();
			break;

		case KW_IMAGE:
			s = Type.getTypeName(t).getJVMTypeDesc();
			break;

		case KW_FRAME:
			s = Type.getTypeName(t).getJVMTypeDesc();
			break;
		}
		return  s;
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
        Chain left = binaryChain.getE0();
        Chain right = binaryChain.getE1();
        left.visit(this, true);
        right.visit(this, false);
		return arg;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
        Expression e0 = binaryExpression.getE0();
        Expression e1 = binaryExpression.getE1();
        Token op = binaryExpression.getOp();
        e0.visit(this, arg);
        e1.visit(this, arg);

        switch(op.kind)
        {
           case TIMES:
        	   if(e0.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE && e1.getTypeName() == cop5556sp17.AST.Type.TypeName.INTEGER){
        		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
        	   }
        	   else if(e0.getTypeName() == cop5556sp17.AST.Type.TypeName.INTEGER && e1.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE){
                   mv.visitInsn(SWAP);
                   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig,false);
        	   }
        	   else{
        	   mv.visitInsn(IMUL);
        	   }
        	   break;

           case DIV:
        	   if(e0.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE && e1.getTypeName() == cop5556sp17.AST.Type.TypeName.INTEGER){
        		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig,false);
        	   }
        	   else if(e0.getTypeName() == cop5556sp17.AST.Type.TypeName.INTEGER && e1.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE){
        		   mv.visitInsn(SWAP);
        		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig,false);
        	   }
        	   else{
        	   mv.visitInsn(IDIV);
        	   }
        	   break;

           case AND:
        	   mv.visitInsn(IAND);
        	   break;

           case MOD:
         	   if(e0.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE && e1.getTypeName() == cop5556sp17.AST.Type.TypeName.INTEGER){
         		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig,false);
         	   }
         	   else if(e0.getTypeName() == cop5556sp17.AST.Type.TypeName.INTEGER && e1.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE){
         		   mv.visitInsn(SWAP);
         		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig,false);
         	   }
         	   else{
        	   mv.visitInsn(IREM);
         	   }
        	   break;

           case PLUS:
        	   if(e0.getTypeName() ==  cop5556sp17.AST.Type.TypeName.IMAGE || e1.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE){
                   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig,false);
        	   }
        	   else{
        		   mv.visitInsn(IADD);
        	   }
        	   break;

           case MINUS:
        	   if(e0.getTypeName() ==  cop5556sp17.AST.Type.TypeName.IMAGE || e1.getTypeName() == cop5556sp17.AST.Type.TypeName.IMAGE){
                   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig,false);
        	   }
        	   else{
        		   mv.visitInsn(ISUB);
        	   }
        	   break;

           case OR:
        	   mv.visitInsn(IOR);
        	   break;

           case LT:
        	   Label l0 = new Label();
   			   Label l1 = new Label();
   			   mv.visitJumpInsn(IF_ICMPGE, l1);
   			   mv.visitInsn(ICONST_1);
   			   mv.visitJumpInsn(GOTO, l0);
   			   mv.visitLabel(l1);
   			   mv.visitInsn(ICONST_0);
   			   mv.visitLabel(l0);
   			   break;

           case LE:
        	   Label l2 = new Label();
   			   Label l3 = new Label();
   			   mv.visitJumpInsn(IF_ICMPGT, l3);
   			   mv.visitInsn(ICONST_1);
   			   mv.visitJumpInsn(GOTO, l2);
   			   mv.visitLabel(l3);
   			   mv.visitInsn(ICONST_0);
   			   mv.visitLabel(l2);
   			   break;

           case GT:
        	   Label l4 = new Label();
   			   Label l5 = new Label();
   			   mv.visitJumpInsn(IF_ICMPLE, l5);
   			   mv.visitInsn(ICONST_1);
   			   mv.visitJumpInsn(GOTO, l4);
   			   mv.visitLabel(l5);
   			   mv.visitInsn(ICONST_0);
   			   mv.visitLabel(l4);
   			   break;

           case GE:
        	   Label l6 = new Label();
   			   Label l7 = new Label();
   			   mv.visitJumpInsn(IF_ICMPLT, l7);
   			   mv.visitInsn(ICONST_1);
   			   mv.visitJumpInsn(GOTO, l6);
   			   mv.visitLabel(l7);
   			   mv.visitInsn(ICONST_0);
   			   mv.visitLabel(l6);
   			   break;

           case EQUAL:
        	   Label l8 = new Label();
   			   Label l9 = new Label();
   			   mv.visitJumpInsn(IF_ICMPNE, l9);
   			   mv.visitInsn(ICONST_1);
   			   mv.visitJumpInsn(GOTO, l8);
   			   mv.visitLabel(l9);
   			   mv.visitInsn(ICONST_0);
   			   mv.visitLabel(l8);
   			   break;

           case NOTEQUAL:
        	   Label l10 = new Label();
   			   Label l11 = new Label();
   			   mv.visitJumpInsn(IF_ICMPEQ, l11);
   			   mv.visitInsn(ICONST_1);
   			   mv.visitJumpInsn(GOTO, l10);
   			   mv.visitLabel(l11);
   			   mv.visitInsn(ICONST_0);
   			   mv.visitLabel(l10);
   			   break;

           case NOT:
        	   mv.visitInsn(INEG);
        	   break;
        }
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		Label startBlock = new Label();
		mv.visitLabel(startBlock);
		stack.push(slotCount);

		ArrayList<Dec> getDecs = new ArrayList<Dec>();
		getDecs = block.getDecs();
		for(Dec dec: getDecs)
		{
			dec.visit(this, arg);
		}
		ArrayList<Statement> getStatements = new ArrayList<Statement>();
		getStatements = block.getStatements();
		for(Statement statement: getStatements)
		{
			statement.visit(this, arg);
			if(statement instanceof BinaryChain){
				mv.visitInsn(POP);
			}
		}

		Label endBlock = new Label();
		mv.visitLabel(endBlock);

		for(Dec dec: getDecs)
		{
			Kind k = dec.firstToken.kind;

			if(k.equals(KW_INTEGER))
			{
			    	 mv.visitLocalVariable(dec.getIdent().getText(), "I", null, startBlock, endBlock, dec.getSlotNumber());
			}
			else if(k.equals(KW_BOOLEAN))
			{
			    	 mv.visitLocalVariable(dec.getIdent().getText(), "Z", null, startBlock, endBlock, dec.getSlotNumber());
			}
			else if(k.equals(KW_IMAGE))
			{
			    	 mv.visitLocalVariable(dec.getIdent().getText(), getTypeFromToken(dec.firstToken), null, startBlock, endBlock, dec.getSlotNumber());
			}
			if(k.equals(KW_FRAME))
			{
			    	 mv.visitLocalVariable(dec.getIdent().getText(), getTypeFromToken(dec.firstToken), null, startBlock, endBlock, dec.getSlotNumber());
			}
		}

        slotCount = stack.pop();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {

		if(constantExpression.getFirstToken().isKind(KW_SCREENWIDTH))
		{
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);

		}
		else if(constantExpression.getFirstToken().isKind(KW_SCREENHEIGHT))
		{
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		return arg;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {

		if(declaration.firstToken.isKind(KW_INTEGER))
		{
			declaration.setSlotNumber(slotCount);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, slotCount++);
		}
		else if(declaration.firstToken.isKind(KW_BOOLEAN))
		{
			declaration.setSlotNumber(slotCount);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, slotCount++);
		}
		else if(declaration.firstToken.isKind(KW_FRAME))
		{
			declaration.setSlotNumber(slotCount);
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, slotCount++);
		}
		else if(declaration.firstToken.isKind(KW_IMAGE))
		{
			declaration.setSlotNumber(slotCount);
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, slotCount++);
		}
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
        String filterMethName = "";
        Tuple tuple = filterOpChain.getArg();
        tuple.visit(this, mv);
        if(filterOpChain.firstToken.isKind(OP_BLUR))
        {
        	filterMethName = "blurOp";
        }
        else if(filterOpChain.firstToken.isKind(OP_GRAY))
        {
        	filterMethName = "grayOp";
        }
        else if(filterOpChain.firstToken.isKind(OP_CONVOLVE))
        {
        	filterMethName = "convolveOp";
        }
        mv.visitInsn(DUP);
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, filterMethName, PLPRuntimeFilterOps.opSig,false);
		return arg;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		String frameMethName = "";
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, mv);

		Token t = frameOpChain.firstToken;

		if(t.isKind(KW_SHOW))
		{
			frameMethName = "showImage";
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, frameMethName, PLPRuntimeFrame.showImageDesc, false);
		}
		else if(t.isKind(KW_MOVE))
		{
			frameMethName = "moveFrame";
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, frameMethName, PLPRuntimeFrame.moveFrameDesc,false);
		}
		else if(t.isKind(KW_XLOC))
		{
			frameMethName = "getXVal";
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, frameMethName, PLPRuntimeFrame.getXValDesc,false);
		}
		else if(t.isKind(KW_YLOC))
		{
			frameMethName = "getYVal";
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, frameMethName, PLPRuntimeFrame.getYValDesc,false);
		}
		else if(t.isKind(KW_HIDE))
		{
			frameMethName = "hideImage";
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, frameMethName, PLPRuntimeFrame.hideImageDesc,false);
		}

		return arg;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {


		boolean left = (boolean)arg;
        Dec dec = identChain.getDec();
        if(left){
        	if(dec instanceof ParamDec){
        		mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(),getTypeFromToken(dec.firstToken));
        	}
        	else {
        		mv.visitVarInsn(ALOAD, dec.getSlotNumber());
        	}
        	switch(identChain.getTypeName()){

        	case FILE:
        		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
        		break;

        	case URL:
  				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className,"readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
  				break;
  				}
        }
        else{
        	   switch(identChain.getTypeName()){

        	   case FILE:
        		   if(dec instanceof ParamDec){
        			   mv.visitVarInsn(ALOAD, 0);
        			   mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
        		   }
        		   else {
        			   mv.visitVarInsn(ALOAD, dec.getSlotNumber());
        		   }
        		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc,false);
        		   mv.visitInsn(ICONST_0);
        		   break;


        	   case IMAGE:
        		   mv.visitInsn(DUP);
        		   if(dec instanceof ParamDec){
        			   mv.visitVarInsn(ALOAD, 0);
        			   mv.visitInsn(SWAP);
        			   mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
        		   }
        		   else {
        			   mv.visitVarInsn(ASTORE, dec.getSlotNumber());
        		   }
        		   break;

        	   case FRAME:
        		   if(dec instanceof ParamDec){
                         mv.visitVarInsn(ALOAD, 0);
                         mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
        		   }
        		   else {
        			   mv.visitVarInsn(ALOAD, dec.getSlotNumber());
        		   }
        		   mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig,false);
                   mv.visitInsn(DUP);
                   if(dec instanceof ParamDec){
                	   mv.visitVarInsn(ALOAD, 0);
                	   mv.visitInsn(SWAP);
                	   mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(),getTypeFromToken(dec.firstToken));
                   }
                   else
                   {
                	   mv.visitVarInsn(ASTORE, dec.getSlotNumber());
                   }
                   break;

        	   case INTEGER:
        		   mv.visitInsn(DUP);
        		   if(dec instanceof ParamDec){
        			   mv.visitVarInsn(ALOAD, 0);
        			   mv.visitInsn(SWAP);
        			   mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(),getTypeFromToken(dec.firstToken));
        		   }
        		   else
        		   {
        			   mv.visitVarInsn(ISTORE, dec.getSlotNumber());
        		   }
        		   break;

        	}
        }
        return arg;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {

		if(identExpression.getDec() instanceof ParamDec )
		{
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getDec().getIdent().getText(),getTypeFromToken(identExpression.getDec().firstToken));
		}
		else
		{
			if(identExpression.getDec().firstToken.isKind(KW_IMAGE) || identExpression.getDec().firstToken.isKind(KW_FRAME))
			{
				mv.visitVarInsn(ALOAD, identExpression.getDec().getSlotNumber());
			}
			else
			{
			mv.visitVarInsn(ILOAD, identExpression.getDec().getSlotNumber());
			}
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {

		if(identX.getDec().firstToken.isKind(KW_IMAGE))
		{
		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig,false);
		}

		if(identX.getDec() instanceof ParamDec )
		{
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, identX.getDec().getIdent().getText(),getTypeFromToken(identX.getDec().firstToken));
		}
		else
		{
			if(identX.getDec().firstToken.isKind(KW_IMAGE) || identX.getDec().firstToken.isKind(KW_FRAME) || identX.getDec().firstToken.isKind(KW_URL))
			{
				mv.visitVarInsn(ASTORE, identX.getDec().getSlotNumber());
			}
			else
			{
			mv.visitVarInsn(ISTORE, identX.getDec().getSlotNumber());
			}

		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression e = ifStatement.getE();
		Block b = ifStatement.getB();
		e.visit(this, arg);
		Label after = new Label();
		mv.visitInsn(ICONST_1);
		mv.visitJumpInsn(IF_ICMPNE, after);
		b.visit(this, arg);
		mv.visitLabel(after);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
        String imageMethName = "";
        Tuple tuple = imageOpChain.getArg();
        tuple.visit(this, mv);
        if(imageOpChain.firstToken.isKind(KW_SCALE))
        {
        	imageMethName = "scale";
        	mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, imageMethName, PLPRuntimeImageOps.scaleSig,false);
        }
        else if(imageOpChain.firstToken.isKind(OP_HEIGHT))
        {
        	imageMethName = "getHeight";
        	mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName,imageMethName, PLPRuntimeImageOps.getHeightSig,false);
        }
        else if(imageOpChain.firstToken.isKind(OP_WIDTH))
        {
        	imageMethName = "getWidth";
        	mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, imageMethName, PLPRuntimeImageOps.getWidthSig,false);
        }

		return arg;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		MethodVisitor mv = (MethodVisitor)arg;
		intializeDecObject(mv,paramDec);
		return arg;
	}


	public void intializeDecObject(MethodVisitor mv, Dec dec) throws Exception{
		cw.visitField(ACC_PUBLIC, dec.getIdent().getText(), getTypeFromToken(dec.firstToken), null, false).visitEnd();

		if(dec.firstToken.isKind(KW_INTEGER)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
            mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
		}
		else if(dec.firstToken.isKind(KW_BOOLEAN)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
            mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
		}
		else if(dec.firstToken.isKind(KW_FILE)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
		}
		else if(dec.firstToken.isKind(KW_URL)){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD,1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "getURL", "([Ljava/lang/String;I)Ljava/net/URL;", false);
			mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), getTypeFromToken(dec.firstToken));
		}

		mv.visitIincInsn(2, 1);
	}



	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression e = sleepStatement.getE();
		e.visit(this, arg);
		mv.visitInsn(I2L);
		Label startTry = new Label();
		Label startCatch = new Label();
		mv.visitLabel(startTry);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		Label endTry = new Label();
		mv.visitLabel(endTry);
		Label endCatch = new Label();
		mv.visitJumpInsn(GOTO, endCatch);
		mv.visitLabel(startCatch);
		mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/InterruptedException"});
		mv.visitLabel(endCatch);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {

		List<Expression> exprlist = tuple.getExprList();
		for(Expression e: exprlist)
		{
			e.visit(this, mv);
		}
		return arg;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        Expression e = whileStatement.getE();
        Block b = whileStatement.getB();
        Label body = new Label();
        Label guard = new Label();
        mv.visitJumpInsn(GOTO, guard);
        mv.visitLabel(body);
        b.visit(this, arg);
        mv.visitLabel(guard);
        e.visit(this, arg);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPEQ, body);
		return null;
	}

}
