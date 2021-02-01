import java.util.*;

public class Main {

	public static void main(String[] args)
	{	
		//createFunction("doublePow(x,y,z) --> pow(x,pow(y,z))");
		
		welcomingPage();
		
		while(true)
		{
			String input = getInput();
			decideAction(input);
			switchPage();
		}
			
	}
	
	private static void welcomingPage()
	{
		System.out.println("===============================================================");
		System.out.println("                          InvinCal                             ");
		System.out.println("===============================================================");                                                         
		System.out.println("  Avaliable Functions :                                        ");
		System.out.println("      1. reciprocal(x)          2. negative(x)                 ");
		System.out.println("      3. factorial(x)           4. combination(n,r)            ");
		System.out.println("      5. pow(x,y)               6. pow10(x)                    ");
		System.out.println("===============================================================");
	}
	
	private static String getInput()
	{
		String input = "";
		Scanner s = new Scanner(System.in);
		System.out.print("Input: ");
		input = s.nextLine();
		
		return input;
	}
	
	private static void decideAction(String input)
	{
		input = input.replaceAll(" ", "");
		
		if(input.equals("--exit"))
		{
			programExit();
			return;
		}
		if(input.contains("-->--delete"))
		{
			deleteFunction(input);
		}
		else if(input.contains("-->"))
		{
			setFunction(input);
		}
		else if(input.contains("->--delete"))
		{
			deleteConstant(input);
		}
		else if(input.contains("->--expression"))
		{
			setConstant(input, true);
		}
		else if(input.contains("->"))
		{
			setConstant(input,false);
		}
		else
		{
			calculateExpression(input);
		}
	}
	
	/**
	 * setConstant command : "X1 -> --expression 9 + combination(5,2)" (Expression)
	 * "X1 -> 9 + A" (Value)
	 * If constant doesn't exist, create on.
	 * If constant exists, change its value.
	 * @param input
	 */
	public static void setConstant(String input, boolean inExpression)
	{
		String name = "";
		String expression = "";
		boolean successful = true;
		
		// eliminate all the spaces
		input = input.replaceAll(" ", "");
		
		String commandOp = inExpression ? "->--expression" : "->";
		
		if(!input.startsWith(commandOp)){
			name = input.substring(0, input.indexOf(commandOp));
		}else{
			System.out.printf("UNSUCCESSFUL CREATION, THE NAME OF THE CONSTANT IS NOT DEFINED!\n");
			return;
		}
		if(!input.endsWith(commandOp)) {
			expression = input.substring(input.indexOf(commandOp) + commandOp.length());
		}else {
			System.out.printf("UNSUCCESSFUL CREATION, THE CONSTANT HAS NO DEFINITION!\n");
			return;
		}
		String solutionString;
		if(inExpression)
		{
			solutionString = expression;
		}
		else
		{
			Element e = Interpreter.interpret(Standardizer.standardize(expression));
			Value v = new Value(e.getValue());
			solutionString = Double.toString(v.getValue());
			
			//System.out.println("soultionString: " + solutionString);
		}
		
		if(Constant.isConstant(name))
		{
			successful = Constant.modifyConstant(name, solutionString);
		}else 
		{
			successful = Constant.createConstant(name, solutionString);
			if(!successful) {
				
				if(!SystemController.validName(name))
				{
					System.out.printf("UNSUCCESSFUL CREATION, THE CONSTANT NAME \"%s\" IS NOT A VALID NAME!\n", name);
					return;
				}
				
				System.out.printf("UNSUCCESSFUL CREATION, THE NAME \"%s\" HAS BEEN USED!\n", name);
				return;
			}
		}
		
		// OUTPUT
		if(successful)
		{
			System.out.printf("CONSTANT \"%s\" HAS BEEN SET, THE CURRENT VALUE IS: %f\n", name, Constant.getValue(name));
		}
		else
		{
			System.out.printf("UNSUCCESSFUL MODIFICATION, CONSTANT \"%s\" IS UNCHANGEABLE OR THE NEW DEFINITION IS RECURSIVE AND GET REJECTED!\n", name);
		}
	}
	
	/**
	 * deleteConstant command : "X1 -> --delete"
	 * @param input
	 */
	private static void deleteConstant(String input)
	{
		String name = "";
		
		input = input.replaceAll(" ", "");
		name = input.substring(0, input.indexOf("->--delete"));
		
		if(Constant.deleteConstant(name) == false)
		{
			// --ERROR
			System.out.printf("UNSUCCESSFUL DELETION, CONSTANT \"%s\" DOES NOT EXIST OR IS UNDELETEABLE!\n", name);
			return;
		}
		else
		{
			System.out.printf("CONSTANT \"%s\" HAS BEEN SUCCEESSFULLY DELETED\n", name);
			return;
		}
	}
	
	/**
	 * createFunction command : "f(x,y,z) --> x^2 + y^2 + z^2"
	 * @param input
	 */
	public static void setFunction(String input)
	{
		input = input.replaceAll(" ", "");
		// functionRepersentation
		String funcRep = "";
		if(!input.startsWith("-->"))
		{
			funcRep = input.substring(0, input.indexOf("-->"));
		}
		else
		{
			System.out.printf("UNSUCCESSFUL CREATION / MODIFICATION, THE NAME AND THE VARIABLES OF THE FUNCTION ARE NOT DEFINED!\n");
			return;
		}
		String name = "";
		if(funcRep.contains("(") && funcRep.contains(")"))
		{
			name = funcRep.substring(0, funcRep.indexOf("("));
		}
		else
		{
			System.out.println("UNSUCCESSFUL CREATEION / MODIFICATION, THE VARIABLES OF THE FUNCTION ARE NOT DEFINED!");
			return;
		}
		String funcVar = funcRep.substring(funcRep.indexOf("(") + 1, funcRep.indexOf(")"));
		String[] variables = funcVar.split(",");
		
		//System.out.println("Variabels: " + Arrays.toString(variables) + "Length: " + variables.length);
		
		// The function must has at least 1 variable
		if(variables.length == 0 || variables[0].equals(""))
		{
			System.out.println("UNSUCCESSFUL CREATEION / MODIFICATION, THE VARIABLES OF THE FUNCTION ARE NOT DEFINED!");
			return;
		}
		
		String implementation = "";
		if(!input.endsWith("-->"))
		{
			implementation = input.substring(input.indexOf("-->") + 3);
		}
		else
		{
			System.out.printf("UNSUCCESSFUL CREATION, THE FUNCTION HAS NO DEFINITION!\n");
			return;
		}
		
		//System.out.println("name: " + name + "\nvariabels: " + Arrays.toString(variables) + "\nimplementation: " + implementation);
		
		if(!SystemController.validName(name))
		{
			System.out.printf("UNSUCCESSFUL CREATION, THE FUNCTION NAME \"%s\" IS NOT A VALID NAME!\n", name);
			return;
		}
		
		// variable names has to be valid as well
		for(String s : variables)
		{
			if(!SystemController.validName(s))
			{
				System.out.printf("UNSUCCESSFUL CREATION, THE VARIABLE NAME \"%s\" IS NOT A VALID NAME!\n", s);
				return;
			}
		}
		
		if(!SystemController.isNameUsed(name))
		{
			//Create Function
			CustomFunction.createCustomFunction(name, implementation, variables);
			System.out.printf("FUNCTION \"%s\" HAS BEEN SUCCESSFULLY CREATED\n",name);
			return;
		}
		else
		{
			//Modify Function
			boolean successful = CustomFunction.modifyCustomFunction(name, implementation, variables);
			if(!successful)
			{
				System.out.printf("UNSUCCESSFUL MODIFICAITON, MODIFICATION REJECTED BECAUSE THE NEW DEFINITION OF FUNCTION \"%s\" IS RECURSIVE\n", name);
				return;
			}
			System.out.printf("FUNCTION \"%s\" HAS BEEN SUCCESSFULLY MODIFIED\n", name);
			return;
		}
	}
	
	/**
	 * Command : " a --> --delete"
	 * @param input
	 */
	public static void deleteFunction(String input)
	{
		input = input.replaceAll(" ", "");
		String name = input.substring(0, input.indexOf("-->--delete"));
		
		if(!Function.isFunction(name))
		{
			System.out.printf("UNSUCCESSFUL DELETION, FUNCTION WITH NAME \"%s\" DOES NOT EXIST!\n", name);
			return;
		}
		
		CustomFunction.deleteCustomFunction(name);
		System.out.printf("FUNCTION \"%s\" HAS BEEN SUCCESSFULLY DELETED\n", name);
		return;
	}
	
	private static void calculateExpression(String input)
	{
		Element solution = Expression.compileExpression(input);
		
		System.out.println("Answer: " + solution.getValue());
		
		// Set constant ANS
		Constant.modifyConstant("ANS", solution.getValue());
	}
	
	private static void switchPage()
	{
		System.out.print("\n");
	}
	
	private static void programExit()
	{
		System.exit(0);
	}
}
