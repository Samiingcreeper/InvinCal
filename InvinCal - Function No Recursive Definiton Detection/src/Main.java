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
		else if(input.contains("->"))
		{
			setConstant(input);
		}
		else
		{
			calculateExpression(input);
		}
	}
	
	/**
	 * setConstant command : "X1 -> 9 + combination(5,2)"
	 * If constant doesn't exist, create on.
	 * If constant exists, change its value.
	 * @param input
	 */
	private static void setConstant(String input)
	{
		String name = "";
		String expression = "";
		boolean successful = true;
		
		// eliminate all the spaces
		input = input.replaceAll(" ", "");
		
		if(!input.startsWith("->")){
			name = input.substring(0, input.indexOf("->"));
		}else{
			successful = false;
		}
		if(!input.endsWith("->")) {
			expression = input.substring(input.indexOf("->") + 2);
		}else {
			successful = false;
		}
		
		Element solution = Interpreter.interpret(Standardizer.standardize(expression));
		
		if(Constant.isConstant(name))
		{
			successful = Constant.modifyConstant(name, solution);
		}else 
		{
			successful = Constant.createConstant(name, solution);
			if(!successful) {
				
				if(!SystemController.validName(name))
				{
					System.out.printf("UNSUCCESSFUL CREATION, THE NAME \"%s\" IS NOT A VALID NAME!\n", name);
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
			System.out.printf("UNSUCCESSFUL MODIFICATION, CONSTANT \"%s\" IS UNCHANGEABLE!", name);
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
	private static void setFunction(String input)
	{
		input = input.replaceAll(" ", "");
		// functionRepersentation
		String funcRep = input.substring(0, input.indexOf("-->"));
		String name = funcRep.substring(0, funcRep.indexOf("("));
		String funcVar = funcRep.substring(funcRep.indexOf("(") + 1, funcRep.indexOf(")"));
		String[] variables = funcVar.split(",");
		String implementation = input.substring(input.indexOf("-->") + 3);
		
		//System.out.println("name: " + name + "\nvariabels: " + Arrays.toString(variables) + "\nimplementation: " + implementation);
		
		if(!SystemController.validName(name))
		{
			System.out.printf("UNSUCCESSFUL CREATION, THE NAME \"%s\" IS NOT A VALID NAME!\n", name);
			return;
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
			CustomFunction.modifyCustomFunction(name, implementation, variables);
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
	
	private static void modifyFunction(String input)
	{
		
	}
	
	private static void calculateExpression(String input)
	{
		Element solution = Expression.compileExpression(input);
		
		System.out.println("Answer: " + solution.getValue());
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
