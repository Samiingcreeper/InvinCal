import java.util.HashMap;
import java.util.Map.Entry;

public abstract class Function extends Operator
{
	private String name;
	
	/**
	 * the ProgrammedFunction only proceed if the number of operands is equal to oeprandNum.
	 * operandNum == Integer.MaxValue means the ProgrammedFunction can accept infinite number of operands. (Not implemented) 
	 */
	private int operandNum;
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * This function is automatically called when a function is constructed.
	 * This constructor is for adding the name of the function to usedNames.
	 */
	protected Function(String name, int operandNum)
	{
		this.name = name;
		this.operandNum = operandNum;
		SystemController.addUsedName(name);
	}
	
	public int getOperandNum() { return operandNum; }
	
	
	public static boolean isFunction(String name)
	{
		return programmedFunctionList.containsKey(name) || customFunctionList.containsKey(name);
	}
	
	public static boolean isCustomFunction(String name)
	{
		return customFunctionList.containsKey(name);
	}
	
	/**
	* Get a copy of the function list.
	* @return
	*/
	public static HashMap<String,Operator> getFunctionList()
	{
		HashMap<String,Operator> functionlist = new HashMap<String,Operator>();
		functionlist.putAll(programmedFunctionList);
		functionlist.putAll(customFunctionList);
		
		//System.out.println(functionlist.toString());
		
		return functionlist;
	}
	
	
	public static final ProgrammedFunction RECIPROCAL = new ProgrammedFunction("reciprocal",1, operands ->
	{
		return 1 / operands[0].getValue();
	});
	public static final ProgrammedFunction NEGATIVE = new ProgrammedFunction("negative",1, operands ->
	{
		return - operands[0].getValue();
	});
	public static final ProgrammedFunction Pow10 = new ProgrammedFunction("pow10",1,operands ->
	{
		return Math.pow(10, operands[0].getValue());
	});
	public static final ProgrammedFunction Pow = new ProgrammedFunction("pow",2,operands ->
	{
		return Math.pow(operands[0].getValue(), operands[1].getValue());
	});
	public static final ProgrammedFunction FACTORIAL = new ProgrammedFunction("!",1,operands ->
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
	public static final ProgrammedFunction COMBINATION = new ProgrammedFunction("combination",2,operands ->
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
//	public static final ProgrammedFunction IFELSE0 = new ProgrammedFunction("ifelse0",3, operands ->
//	{
//		return operands[0].getValue() == 0 ? operands[1].getValue() : operands[2].getValue();
//	});
	
	private static HashMap<String,Operator> customFunctionList = new HashMap<String,Operator>();
	
	public static boolean isCustomFunctionOperator(Operator o)
	{
		for(Entry<String, Operator> et : customFunctionList.entrySet())
		{
			if(et.getValue() == o)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static void addToCustomFunctionList(String name, CustomFunction func)
	{
		//System.out.println("AddToCutomFunctionList");
		customFunctionList.put(name, func);
	}
	
	/**
	 * Return true for successful deletion
	 * @param name
	 * @return
	 */
	public static boolean deleteFromCustomFunctionList(String name)
	{
		if(customFunctionList.containsKey(name))
		{
			customFunctionList.remove(name);
			SystemController.deleteUsedName(name);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Return true for successful modification
	 * @param name
	 * @param modf
	 * @return
	 */
	public static boolean modifyCustomFunctionList(String name, CustomFunction modf)
	{
		if(customFunctionList.containsKey(name))
		{
			customFunctionList.replace(name, modf);
			return true;
		}
		else return false;
	}
	
	public static CustomFunction getCustomFunction(String name)
	{
		return (CustomFunction) customFunctionList.get(name);
	}
	
	private static final HashMap<String,Operator> programmedFunctionList = initializeOperatorList();
	private static HashMap<String,Operator> initializeOperatorList()
	{		
		HashMap<String,Operator> operatorList = new HashMap<String,Operator>();
		operatorList.put(RECIPROCAL.getName(), RECIPROCAL);
		operatorList.put(NEGATIVE.getName(), NEGATIVE);
		operatorList.put(FACTORIAL.getName(), FACTORIAL);////
		operatorList.put(COMBINATION.getName(), COMBINATION);
		operatorList.put(Pow10.getName(), Pow10);
		operatorList.put(Pow.getName(), Pow);
		//operatorList.put(IFELSE0.getName(), IFELSE0);
		
		return operatorList;	
	}
	
	protected void setOperandNum(int o)
	{
		this.operandNum = o;
	}

}