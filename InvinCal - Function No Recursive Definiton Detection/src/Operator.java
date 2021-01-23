import java.util.function.ToDoubleFunction;
import java.util.*;

/**
 * An operator evaluates a set of operands and returns a value
 * 
 * This class holds the most fundamental operators to be used.
 */
public abstract class Operator 
{
	public static final Operator SUMMATION = new Summation();
	public static final Operator PRODUCT = new Product();
	public static final Operator BRACKET = new Bracket();
	
	/**
	 * Get the result of operating the operands with this operator
	 * @param operands - the elements to be evaluated
	 * @return result
	 */
	public abstract double evaluate(Element[] operands);
	
	/**
	 * Summation operates the operands by adding them together.
	 * @author samiingcreeper
	 *
	 */
	private static class Summation extends Operator
	{
		public double evaluate(Element[] operands)
		{
			double result = 0;
			
			for(Element e : operands)
			{
				result += e.getValue();
			}
			
			return result;
		}
	}
	
	/**
	 * Product operates the operands by multiplying them together.
	 * @author samiingcreeper
	 *
	 */
	private static class Product extends Operator
	{
		public double evaluate(Element[] operands)
		{
			double result = 1;
			
			for(Element e : operands)
			{
				result *= e.getValue();
			}
			
			return result;
				
		}
	}
	
	/**
	 * Bracket returns the value of the sub-element enclosed by brackets.
	 * @author samiingcreeper
	 *
	 */
	private static class Bracket extends Operator
	{
		public double evaluate(Element[] operands)
		{
			if(operands.length != 1)
			{
				System.out.println("ERROR: MORE THAN ONE OPERANDS INPUTTED TO BRACKET OPERATOR");
				
				return Double.NaN;
			}
			
			return operands[0].getValue();
		}
	}
	
	public static void main(String[] args)
	{

	}
}
