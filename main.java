import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.CharStreams;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException{

	// we expect exactly one argument: the name of the input file
	if (args.length!=1) {
	    System.err.println("\n");
	    System.err.println("Simple calculator\n");
	    System.err.println("=================\n\n");
	    System.err.println("Please give as input argument a filename\n");
	    System.exit(-1);
	}
	String filename=args[0];

	// open the input file
	CharStream input = CharStreams.fromFileName(filename);
	    //new ANTLRFileStream (filename); // depricated
	
	// create a lexer/scanner
	simpleCalcLexer lex = new simpleCalcLexer(input);
	
	// get the stream of tokens from the scanner
	CommonTokenStream tokens = new CommonTokenStream(lex);
	
	// create a parser
	simpleCalcParser parser = new simpleCalcParser(tokens);
	
	// and parse anything from the grammar for "start"
	ParseTree parseTree = parser.start();

	// Construct an interpreter and run it on the parse tree
	Interpreter interpreter = new Interpreter();
	Double result=interpreter.visit(parseTree);
	System.out.println("The result is: "+result);

	// Construct an abstract syntax three
	ASTmaker asTmaker = new ASTmaker();
	Expr re=asTmaker.visit(parseTree);
	System.out.println("my new result is: "+ re.eval());
	
    }
}

// We write an interpreter that implements interface
// "simpleCalcVisitor<T>" that is automatically generated by ANTLR
// This is parameterized over a return type "<T>" which is in our case
// simply a Double.

class Interpreter extends AbstractParseTreeVisitor<Double> implements simpleCalcVisitor<Double> {

    public Double visitStart(simpleCalcParser.StartContext ctx){
	Double v=visit(ctx.e);
	return v;
    };
    //    public Double visitExpr(simpleCalcParser.ExprContext ctx){return null;};
    public Double visitParenthesis(simpleCalcParser.ParenthesisContext ctx){
	return visit(ctx.e);
    };
    public Double visitMultiplication(simpleCalcParser.MultiplicationContext ctx){
	if (ctx.op.getText().equals("*"))
	    return visit(ctx.e1) * visit(ctx.e2);
	else
	    return visit(ctx.e1) / visit(ctx.e2);
    };
    public Double visitAddition(simpleCalcParser.AdditionContext ctx){	
	if (ctx.op.getText().equals("+"))
	    return visit(ctx.e1) + visit(ctx.e2);
	else
	    return visit(ctx.e1) - visit(ctx.e2);
    };
    public Double visitConstant(simpleCalcParser.ConstantContext ctx){
	return Double.parseDouble(ctx.c.getText());
    };

    public Double visitMinus(simpleCalcParser.MinusContext ctx){
	if (ctx.op.getText().equals("+"))
	    return visit(ctx.e);
	else
	    return -visit(ctx.e);
    };

}



//abstract syntax three
class ASTmaker extends AbstractParseTreeVisitor<Expr> implements simpleCalcVisitor<Expr> {

	@Override
	public Expr visitStart(simpleCalcParser.StartContext ctx) {
		return visit(ctx.e);
	}

	@Override
	public Expr visitParenthesis(simpleCalcParser.ParenthesisContext ctx) {
		return visit(ctx.e);
	}

	@Override
	public Expr visitMultiplication(simpleCalcParser.MultiplicationContext ctx) {
		if (ctx.op.getText().equals("*"))
			return new Multiplication(visit(ctx.e1),visit(ctx.e2));

		else
			return new Division(visit(ctx.e1),visit(ctx.e2));
	}

	@Override
	public Expr visitAddition(simpleCalcParser.AdditionContext ctx) {
		if (ctx.op.getText().equals("+"))
			return new Addition(visit(ctx.e1),visit(ctx.e2));

		else
			return new Subtraction(visit(ctx.e1),visit(ctx.e2));
	}

	@Override
	public Expr visitConstant(simpleCalcParser.ConstantContext ctx) {
		return new Const(Double.parseDouble(ctx.c.getText()));
	}

	@Override
	public Expr visitMinus(simpleCalcParser.MinusContext ctx) {
		if (ctx.op.getText().equals("+"))
			return visit(ctx.e);
		else
			return new Subtraction(new Const(Double.valueOf(0)),visit(ctx.e));
	}
}



abstract class Expr{
	abstract public Double eval();
};

//variable
class Var extends Expr{
	public String name;
	//constructor
	public Var(String name) {
		this.name=name;
	}

	@Override
	public Double eval() {
		return null;
	}
};


class Const extends Expr{
	public Double v;

	//constructor
	public Const(Double v) {
		this.v = v;
	}
	@Override
	public Double eval() {
		return v;
	}


};


class Multiplication extends Expr{
	public Expr e1;
	public Expr e2;

	//constructor
	public Multiplication(Expr e1, Expr e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	public Double eval() {
		Double v1=e1.eval();
		Double v2=e2.eval();
		return v1*v2;
	}
};

class Addition extends Expr{
	public Expr e1;
	public Expr e2;

	//constructor
	public Addition(Expr e1, Expr e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	public Double eval() {
		Double v1=e1.eval();
		Double v2=e2.eval();
		return v1+v2;
	}
};


class Subtraction extends Expr{
	public Expr e1;
	public Expr e2;

	//constructor
	public Subtraction(Expr e1, Expr e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	public Double eval() {
		Double v1=e1.eval();
		Double v2=e2.eval();
		return v1-v2;
	}
};



class Division extends Expr{
	public Expr e1;
	public Expr e2;

	//constructor
	public Division(Expr e1, Expr e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	@Override
	public Double eval() {
		Double v1=e1.eval();
		Double v2=e2.eval();
		return v1/v2;
	}
};

