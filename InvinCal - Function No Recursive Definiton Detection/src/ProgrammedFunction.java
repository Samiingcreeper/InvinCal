import java.util.function.ToDoubleFunction;

/**
	 * A ProgrammedFunction is a kind of operator, but it has a name. It also has limited operators.
	 * ProgrammedFunction means the functions that are programmed in the program, not those that the user can customize.
	 * A ProgrammedFunction is represented as name(x1,x2,...,xn).
	 * 
	 * @author samiingcreeper
	 *
	 */
public class ProgrammedFunction extends Function
{
	private ToDoubleFunction<Element[]> implementation;
	
	public ProgrammedFunction(String name, int operandNum, ToDoubleFunction<Element[]> implementation)
	{
		super(name, operandNum);
		this.implementation = implementation;
	}
	
	/**
	 * If the number of operands is not equal to operandNum, an error occurs
	 */
	public double evaluate(Element[] operands)
	{
		if(operands.length != this.getOperandNum())
		{
			System.out.printf("ERROR: WRONG NUMBER OF OPERANDS INPUTTED. FUNCTION NAME: \"%s\","
					+ " NUMBER OF OPERAND NEEDED: %d, NUMBER OF OPERANDS INPUTTED: %d \n", this.getName(), this.getOperandNum(), operands.length);
			
			return Double.NaN;
		}
			
		
		return implementation.applyAsDouble(operands);
	}
	
}