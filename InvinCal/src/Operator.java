import java.util.function.*;
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
	public static final Function RECIPROCAL = new Function("reciprocal",1, operands ->
			{
				return 1 / operands[0].getValue();
			});
	public static final Function NEGATIVE = new Function("negative",1, operands ->
			{
				return - operands[0].getValue();
			});
	public static final Function Pow10 = new Function("pow10",1,operands ->
			{
				return Math.pow(10, operands[0].getValue());
			});
	public static final Function Pow = new Function("pow",2,operands ->
	{
		return Math.pow(operands[0].getValue(), operands[1].getValue());
	});
	public static final Function FACTORIAL = new Function("factorial",1,operands ->
			{
				int value = (int)operands[0].getValue();
				
				// if the following is true, we know that the value of operand[0] is not an integer
				if(value != operands[0].getValue())
				{
					System.out.println("ERROR: FACTORIAL - OPERAND IS NOT AN INTEGER, OPERAND VALUE: " + operands[0].getValue());
					
					return Double.NaN;
				}
				
				int fractorial = 1;
				
				for(int i = value; i >= 1; i--)
				{
					fractorial *= i;
				}
				
				return fractorial;
			});
	public static final Function COMBINATION = new Function("combination",2,operands ->
			{
				int n = (int)operands[0].getValue();
				
				//System.out.println("COMBINATION: n- " + n);
				
				if(n != operands[0].getValue())
				{
					System.out.println("ERROR: COMBINATION - N IS NOT AN INTEGER, N VALUE: " + operands[0].getValue());
					
					return Double.NaN;
				}
				
				int r = (int)operands[1].getValue();
				
				if(r != operands[1].getValue())
				{
					System.out.println("ERROR: COMBINATION - R IS NOT AN INTEGER, R VALUE: " + operands[1].getValue());
					
					return Double.NaN;
				}
				
				if( r > n)
				{
					System.out.println("ERROR: COMBINATION - R IS LARGER THAN N, N VALUE: " + operands[0].getValue()
							+ ", R VALUE: " + operands[1].getValue());
					
					return Double.NaN;
				}
				
				int nF = (int)FACTORIAL.evaluate(new Element[] { new Value(n) });				
				int rF = (int)FACTORIAL.evaluate(new Element[] { new Value(r) });
				int nrF = (int)FACTORIAL.evaluate(new Element[] { new Value(n-r) });
				
				//System.out.printf("nF: %d, rF: %d, nrF: %d\n",nF,rF,nrF);
				
				return nF/rF/nrF;
			});
	

	public static final HashMap<String,Operator> operatorList = initializeOperatorList();
	private static HashMap<String,Operator> initializeOperatorList()
	{		
		HashMap<String,Operator> operatorList = new HashMap<String,Operator>();
		operatorList.put(RECIPROCAL.getName(), RECIPROCAL);
		operatorList.put(NEGATIVE.getName(), NEGATIVE);
		operatorList.put(FACTORIAL.getName(), FACTORIAL);
		operatorList.put(COMBINATION.getName(), COMBINATION);
		operatorList.put(Pow10.getName(), Pow10);
		operatorList.put(Pow.getName(), Pow);
		
		return operatorList;	
	}
	
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
	
	
	
	/**
	 * A function is a kind of operator, but it has a name. It also has limited operators.
	 * A function is represented as name(x1,x2,...,xn).
	 * 
	 * @author samiingcreeper
	 *
	 */
	static class Function extends Operator
	{
		private String name;
		
		/**
		 * the function only proceed if the number of operands is equal to oeprandNum.
		 * operandNum == Integer.MaxValue means the function can accept infinite number of operands. (Not implemented) 
		 */
		private int operandNum;
		
		private ToDoubleFunction<Element[]> operation;
		
		public String getName()
		{
			return name;
		}
		
		public Function(String name, int operandNum, ToDoubleFunction<Element[]> operation)
		{
			this.name = name;
			this.operandNum = operandNum;
			this.operation = operation;
		}
		
		/**
		 * If the number of operands is not equal to operandNum, an error occurs
		 */
		public double evaluate(Element[] operands)
		{
			if(operands.length != operandNum)
			{
				System.out.printf("ERROR: WRONG NUMBER OF OPERANDS INPUTTED. FUNCTION NAME: \"%s\","
						+ " NUMBER OF OPERAND NEEDED: %d, NUMBER OF OPERANDS INPUTTED: %d \n", name, operandNum, operands.length);
				
				return Double.NaN;
			}
				
			
			return operation.applyAsDouble(operands);
		}

		
	}
	
	public static void main(String[] args)
	{
		Value v1 = new Value(5);
		Value v2 = new Value(3);
		
		Operator operator = Operator.COMBINATION;
		
		Formula f = new Formula(operator, v1, v2);
		
		System.out.println(f.getValue());
	}
}
