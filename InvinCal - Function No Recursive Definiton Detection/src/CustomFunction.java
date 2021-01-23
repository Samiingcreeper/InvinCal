import java.util.Arrays;

public class CustomFunction extends Function
{
	
	private Element implementation;
	private Variable[] variables;
	private String representation;
	
	public String getRepresentation() { return representation; }
	
	private CustomFunction(String name, int operandNum, String implementation, String... variables) 
	{
		super(name, operandNum);
		
		this.variables = new Variable[variables.length];
		for(int i = 0; i < variables.length; i++)
		{
			this.variables[i] = new Variable(variables[i]);
		}
		setRepresentation();
		
		// create implementation
		this.implementation = CustomFunction.compileCustomFunction(implementation, this);
	}
	
	private void updateFunction(int operandNum, String implementation, String...variables)
	{
		super.setOperandNum(operandNum);
		this.variables = new Variable[variables.length];
		for(int i = 0; i < variables.length; i++)
		{
			this.variables[i] = new Variable(variables[i]);
		}
		setRepresentation();
		
		// create implementation
		this.implementation = CustomFunction.compileCustomFunction(implementation, this);
	}
	
	/**
	 * Return true for successful creation
	 * @param name
	 * @param operandNum
	 * @param implementation
	 * @param variables
	 * @return
	 */
	public static boolean createCustomFunction(String name, String implementation, String... vars)
	{
		CustomFunction func;
		
		if(!SystemController.isNameUsed(name) && SystemController.validName(name))
		{
			//System.out.println("CreateCustomFunction");
			
			func = new CustomFunction(name, vars.length, implementation, vars);
			Function.addToCustomFunctionList(func.getName(), func);
			return true;
			
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Return true for successful deletion
	 * @param name
	 * @return
	 */
	public static boolean deleteCustomFunction(String name)
	{
		return Function.deleteFromCustomFunctionList(name);
	}
	
	public static boolean modifyCustomFunction(String name, String implementation, String... vars)
	{
		if(Function.isCustomFunction(name))
		{
			CustomFunction f = Function.getCustomFunction(name);
			if(f != null)
			{
				f.updateFunction(vars.length, implementation, vars);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isVariable(String s)
	{
		for(Variable v : variables)
		{
			if(v.getName().equals(s)) return true;
		}
		
		return false;
	}
	
	private void setRepresentation()
	{
		String r = this.getName() + "(";
		for(int i = 0 ; i < variables.length; i++)
		{
			r = r + variables[i].getName();
			if(i + 1 == variables.length)
			{
				r = r + ")";
			}
			else
			{
				r = r + ",";
			}
		}
		representation = r;
	}

	@Override
	public double evaluate(Element[] operands) 
	{
		if(operands.length != this.getOperandNum())
		{
			System.out.printf("ERROR: WRONG NUMBER OF OPERANDS INPUTTED. FUNCTION NAME: \"%s\","
					+ " NUMBER OF OPERAND NEEDED: %d, NUMBER OF OPERANDS INPUTTED: %d \n", this.getName(), this.getOperandNum(), operands.length);
			
			return Double.NaN;
		}
		
		for(int i = 0; i < variables.length; i++)
		{
			variables[i].assignValue(operands[i].getValue());
		}
		return implementation.getValue();
	}
	
	
	
	public Value getVariableValue(String name)
	{
		for(Variable v : variables)
		{
			if(v.getName().equals(name)) return v.getElement();
		}
		
		return null;
	}
	
	private static Element compileCustomFunction(String input, CustomFunction f)
	{
		return CustomFunctionInterpreter.interpret(Standardizer.standardize(input), f);
		
	}

	public static void main(String[] args)
	{
		CustomFunction c = new CustomFunction("doublePow", 3, "pow(x,pow(y,z))", "x", "y","z");
		
//		Value x = c.getVariableValue("x");
//		Value y = c.getVariableValue("y");
//		Expression e = new Expression(Operator.SUMMATION, x, y);
//		c.implementation = e;
//		
		System.out.println(c.evaluate( new Element[]{new Value(10), new Value(2), new Value(2)}) );
		
	}
	
	private static class Variable
	{
		private String name;
		private Value value;
		
		public String getName() { return name; }
		public double getValue() { return value.getValue(); }
		public Value getElement() { return value; }
		public void assignValue(double v) { value.assign(v);}
		
		public Variable(String name)
		{
			this.name = name;
			value = new Value();
		}
	}
}
