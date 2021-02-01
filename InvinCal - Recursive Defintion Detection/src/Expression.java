import java.util.*;

public class Expression extends Element
{
	private Element[] subElements;
	
	private Operator operator;
	
	public double getValue()
	{
		return operator.evaluate(subElements);
	}
	
	public Expression(Operator operator, Element... subElements)
	{
		this.operator = operator;
		this.subElements = subElements;
	}
	
	public static Element compileExpression(String input)
	{
		return Interpreter.interpret(Standardizer.standardize(input));
	}
	
	public Operator getOperator() { return operator; }

}
