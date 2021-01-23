import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.event.ListSelectionEvent;

/**
 * Standardizer standardizes a source expression string into standardized form which can be interpreted by the interpreter
 * @author samiingcreeper
 *
 */
public class Standardizer 
{
	public static void main(String[] args)
	{
		String s = standardize("combination()");		// -pi
		
		System.out.println(s);
	}
	
	public static String standardize(String string)
	{
		string = _standardize(string);
		
		string = symbolTransform(string);
		
		return string;
	}
	
	private static String _standardize(String string)
	{
//		System.out.println("STANDARDIZE: INPUT- " + string);
		
		string = EliminateSpaces(string);
		//string = preliminaryTransformation_NoImplicitSignInverse(string);
		string = preliminaryTransformation_NoStartingPlusMinus(string);
		string = preliminaryTransformation_AddingMultiplicationSymbol(string);
		
		string = _bracketStandardize(string);
		
		string = boxing(string);
		
		return string;
	}
	
	/**
	 * Turn - into negative() and / into reciprocal()
	 * @param string
	 * @return
	 */
	private static String symbolTransform(String string)
	{
		String transformedString = "";
		
		var stringAnalyze = new StandardizerStringAnalyzer(string);
		
		int i = 0;
		
		while(i < string.length())
		{
			if(string.charAt(i) == '-')
			{
				int elementStartPos = i + 1;
				int elementEndPos = stringAnalyze.getNextLocaitonWithSameOperationLevel(i);
				
				transformedString += "+(negative(" + string.substring(elementStartPos,elementEndPos + 1) + "))";
				
				i = elementEndPos + 1;
			}
			else if(string.charAt(i) == '/')
			{
				int elementStartPos = i + 1;
				int elementEndPos = stringAnalyze.getNextLocaitonWithSameOperationLevel(i);
				
				transformedString += "*(reciprocal(" + string.substring(elementStartPos,elementEndPos + 1)  + "))";
				
				i = elementEndPos + 1;
			}
			else
			{
				transformedString += string.substring(i,i+1);
				
				i++;
			}
				
		}
		
		// for cases like 9/-3, the method has to be executed 2 times to fully transform the symbols
		// thus, this method is executed repeatedly until the string has no '-' && '/'
		if(transformedString.indexOf("-") != -1 || transformedString.indexOf("/") != -1)
		{
			transformedString = symbolTransform(transformedString);
		}
		
		return transformedString;
	}
	
	/**
	 * The final step of standardize, boxing the element string with a pair of brackets (if it doesn't have one)
	 * @return
	 */
	private static String boxing(String string)
	{
//		System.out.println("BOXING: INPUT- " + string);
		
		var s = new StandardizerStringAnalyzer(string);
		
		if(!s.ifEnclosedByBrackets())
		{
//			System.out.println("BOXING: NOT ENCLOSED BY BRACKETS");
			
			String boxed = "(" + string + ")";
			
//			System.out.println("BOIXNG: BOXED- " + boxed);
			
			return boxed;
		}
		else
		{
			return string;
		}
	}
	
	static private String EliminateSpaces(String s)
	{
		return s.replaceAll(" ", "");
	}
	
	/**
	 * Turn -(-1) into (-1)*(-1), Turn 3/-(3) 
	 */
	static private String preliminaryTransformation_NoImplicitSignInverse(String string)
	{
		String result = "";
		
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == '-')
			{
				if(i < string.length() - 1 && string.charAt(i + 1) == '(')
				{
					if(i > 0 && string.charAt(i - 1) == '/')
					{
						result += "(-1)/";
					}
					else
					{
						result += "(-1)*";
					}

				}
				else
				{
					result += string.substring(i, i+1);
				}
			}
			else
			{
				result += string.substring(i, i+1);
			}
			
		}
		
		return result;
	}
	
	static private String preliminaryTransformation_NoStartingPlusMinus(String string)
	{
		if(string.startsWith("-") || string.startsWith("+"))
		{
			return "0" + string;
		}
		else
		{
			return string;
		}
	}
	
	
	/**
	 * This function adds * symbol to locations where are multiplication but the * symbols are implicit.
	 * For "(", the cases can be : 1. (7)(9)  2. 7(9) --- make sure the former string is not a function name
	 * For ")", the cases can not be : (9)-0, (9)+0, (9)/1, ...(9)), ...(9) --- the end of the string 
	 * @return
	 */
	static private String preliminaryTransformation_AddingMultiplicationSymbol(String string)
	{
		String transformedString = "";
		
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == '(')
			{
				if(i > 0)
				{
					
					// if these symbols appears, we don't need to add '*'
					List<Character> prohibitedSymbols = Arrays.asList('-','+','/','*','(',',');
					
					if(string.charAt(i - 1) == ')')
					{
						transformedString += "*";
					}
					else if(!prohibitedSymbols.contains(string.charAt(i - 1)) && __formerStringIsNotAFucntionName(string,i) )
					{
						transformedString += "*";
					}
				}
			}
			
			transformedString += string.charAt(i);
			
			if(string.charAt(i) == ')')
			{
				// if these symbols appears, we don't need to add '*'
				List<Character> prohibitedSymbols = Arrays.asList('-','+','/','*',')','(',',');		// For '(', the '*' is added in the next iteration
				
				if(i + 1 < string.length())
				{
					if(!prohibitedSymbols.contains(string.charAt(i + 1)))
					{
						transformedString += "*";
					}
				}
			}
		}
		
		
		return transformedString;
	}
	
	/**
	 * This function is a sub function for preliminaryTransformation_AddingMultiplicationSymbol.
	 * The end point is the position, the start point is the start of the string or symbols (+ - * / ( ) )
	 * @param string -- no spaces
	 * @param position -- the index
	 * @return
	 */
	static private boolean __formerStringIsNotAFucntionName(String string, int position)
	{
		int endPoint = position;
		
		// finding the starting point
		
		int i = position - 1;
		
		int startPoint = -1;
		
		Character[] _symbolList = {'+','-','*','/','(',')',','};
		
		List<Character> symbolList = Arrays.asList(_symbolList);
		
		do
		{
			if(symbolList.contains(string.charAt(i)))
			{
				// ** i is the position of the symbol
				
				startPoint = i + 1;
			}
			
			// if the startPoint is still not found when i == 0, we know the startPoint is the start of the string
			
			if(startPoint == -1 && i == 0)
			{
				startPoint = 0;
			}
			
			i--;
			
		}
		while(startPoint == -1 && i >= 0);
		
		String functionName = string.substring(startPoint,endPoint);
		
//		System.out.println("FORMERSTRINGISNOTAFUNCTIONNAME: FUNCTIONNAME- " + functionName);
		
		return !Function.getFunctionList().containsKey(functionName);
		
	}
	
	/**
	 * Standardize the expression string so that the brackets are correct (the enclosing brackets are not added in this method). All of the sub-elements are standardized along the way.
	 * This is done by splitting the string into sub element strings by the operands and standardize them individually.
	 * The process of standardizing the sub-elements are done by "boxing" and "symbolsTransform".
	 * @param string
	 * @return
	 */
	private static String _bracketStandardize(String string)
	{
//		System.out.println("BRACKETSTANDARDIZE!");
		
		
		var stringAnalyze = new StandardizerStringAnalyzer(string);
		
		String bracketStandardizedString = "";
		
		// the sub-element strings to be standardized. If string is already a value, the method ignores it
		String[] subElementStrings;
		
		if(stringAnalyze.ifValidAdditionSubtractionAt0())
		{
			String[] symbols;
			
			subElementStrings = stringAnalyze.getSubElementStringsSplittedByValidAdditionSubtraction();
			symbols = stringAnalyze.getValidPlusMinusAt0Symbols();
			
			// standardize the sub element strings
			String[] standardizedSubElementStrings = subElementStrings.clone();
			
			for(int i = 0; i < standardizedSubElementStrings.length; i++)
			{
				standardizedSubElementStrings[i] = _standardize(standardizedSubElementStrings[i]);
			}
			
			// combining the standardized stirng and the symbols
			
			for(int i = 0; i < standardizedSubElementStrings.length; i++)
			{
				bracketStandardizedString += standardizedSubElementStrings[i];
				
				if(i < standardizedSubElementStrings.length - 1)
				{
					bracketStandardizedString += symbols[i];
				}
			}
			
			return bracketStandardizedString;
		}
		else if(stringAnalyze.ifMultiplicationDivisionAt0())
		{
			String[] symbols;
			
			subElementStrings = stringAnalyze.getSubElementStringsSplittedByMultiplicationDivision();
			symbols = stringAnalyze.getMuliplicationDivisionAt0Symbols();
			
			// standardize the sub element strings
			String[] standardizedSubElementStrings = subElementStrings.clone();
			
			for(int i = 0; i < standardizedSubElementStrings.length; i++)
			{
				standardizedSubElementStrings[i] = _standardize(standardizedSubElementStrings[i]);
			}
			
			// combining the standardized stirng and the symbols
			
			for(int i = 0; i < standardizedSubElementStrings.length; i++)
			{
				bracketStandardizedString += standardizedSubElementStrings[i];
				
				if(i < standardizedSubElementStrings.length - 1)
				{
					bracketStandardizedString += symbols[i];
				}
			}
			
			return bracketStandardizedString;
		}
		else if(stringAnalyze.ifFunctionAt0())
		{
			subElementStrings = stringAnalyze.getSubElementStringsInFunction();
			String functionName = stringAnalyze.getFunctionName();
			
			bracketStandardizedString += functionName;
			
			for(int i = 0; i < subElementStrings.length; i++)
			{
				if(i == 0)
				{
					bracketStandardizedString += "(";
				}
				
				bracketStandardizedString += _standardize(subElementStrings[i]);
				
				if(i < subElementStrings.length - 1)
				{
					bracketStandardizedString += ",";
				}
				
				if(i == subElementStrings.length - 1)
				{
					bracketStandardizedString += ")";
				}
				
			}
			
			return bracketStandardizedString;
			
		}
		else if(stringAnalyze.ifEnclosedByBrackets())
		{
			String subElementString = stringAnalyze.getElementStringInBracket();
			
			subElementStrings = new String[1];
			subElementStrings[0] = subElementString;
			
			return "(" + _bracketStandardize(subElementStrings[0]) + ")";
		}
		else if(string.startsWith("-"))
		{
			// if the string has negative sign at start , it can be - 1 /-(6 - 4), calling _bracketStandardize would cause infinite recursion
			// thus, this situation is dealt by changing it into (0)-(1) directly
			
			string = "(0)-(" +  _bracketStandardize(string.substring(1)) + ")";
			
			return string;
		}
		else if(string.startsWith("+"))
		{
			// if the string has negative sign at start , it can be +1 / -(6 - 4), calling _bracketStandardize would cause infinite recursion
			// thus, this situation is dealt by changing it into (0)+(1) directly
			
			string = "(0)+(" +  _bracketStandardize(string.substring(1)) + ")";
			
			return string;
		}
		else
		{

			
			//System.out.println("VALUE! STRING- " + string);
			
			// the string is a value
			
			// --- ERROR: if the string contains ',', it's probable that the user typed in the wrong function

			
			return string;
		}
		
	}
	
	private static class StandardizerStringAnalyzer
	{
		private String string;
		
		/**
		 * Operation level at each location
		 */
		private int[] operationLevel;
		
		
		public StandardizerStringAnalyzer(String string)
		{
			this.string = string;
			
			this.operationLevel = initializeOperationLevel(this.string); 
		}
		
		public String getString()
		{
			return string;
		}
		
		public int getOperationLevelAt(int index)
		{
			return operationLevel[index];
		}
		
		public int[] getOperationLevel()
		{
			return operationLevel.clone();
		}
		
		/**
		 * Traverse through the input string to get the operation level at each location
		 * @param string
		 * @return
		 */
		private int[] initializeOperationLevel(String string)
		{
			int currOperationLevel = 0;
			
			int[] operationLevel = new int[string.length()];
			
			for(int i = 0; i < string.length(); i++)
			{
				String s = string.substring(i,i+1);
				
				if(s.equals("("))
				{
					currOperationLevel++;
				}
				else if(s.equals(")"))
				{
					currOperationLevel--;
				}
				
				operationLevel[i] = currOperationLevel;
			}
			
			return operationLevel;
		}
		
		/**
		 * there are some special cases for +/- : string = "9*-4" , string = "-9", ifValidAdditionSubtractionAt0() == true is undesired. 
		 * @return
		 */
		public boolean ifValidAdditionSubtractionAt0()
		{
			for(int i = 0; i < operationLevel.length; i++)
			{
				if(operationLevel[i] == 0 && (string.charAt(i) == '+' || string.charAt(i) == '-') )
				{
					if(i > 0 && (string.charAt(i-1) != '*' && string.charAt(i-1) != '/'))
					{
						return true;
					}

				}
			}
			
			return false;
			
		}
		
		/**
		 * get all the locations of valid +- locations at positions where operation level == 0
		 * @return
		 */
		private int[] getValidAdditionSubtractionAt0Locations()
		{
			ArrayList<Integer> locations = new ArrayList<Integer>();
			
			for(int i = 0; i < operationLevel.length; i++)
			{
				if(operationLevel[i] == 0 && (string.charAt(i) == '+' || string.charAt(i) == '-') )
				{
					if(i > 0 && (string.charAt(i-1) != '*' && string.charAt(i-1) != '/'))
					{
						locations.add(i);
					}

				}
			}
			
			Integer[] x = new Integer[1];
			x = locations.toArray(x);
			int[] result = new int[x.length];
			
			for(int i = 0; i < result.length; i++)
			{
				result[i] = x[i];
			}

			return result;
			
		}
		
		private int[] getMultiplicationDivisionAt0Locations()
		{
			ArrayList<Integer> locations = new ArrayList<Integer>();
			
			for(int i = 0; i < operationLevel.length; i++)
			{
				if(operationLevel[i] == 0 && (string.charAt(i) == '*' || string.charAt(i) == '/') )
				{

					locations.add(i);
				}
			}
			
			Integer[] x = new Integer[1];
			x = locations.toArray(x);
			int[] result = new int[x.length];
			
			for(int i = 0; i < result.length; i++)
			{
				result[i] = x[i];
			}

			return result;
			
		}
		
		public boolean ifMultiplicationDivisionAt0()
		{
			for(int i = 0; i < operationLevel.length; i++)
			{
				if(operationLevel[i] == 0 && (string.charAt(i) == '*' || string.charAt(i) == '/') )
				{
					return true;
				}
			}
			
			return false;
			
		}
		
		public boolean ifFunctionAt0()
		{
			Set<String> functionList = Function.getFunctionList().keySet();
			
			for(String functionName : functionList)
			{
				String charAt0 = "";
				
				for(int i = 0; i < string.length(); i++)
				{
					if(operationLevel[i] == 0)
					{
						charAt0 += string.charAt(i);
					}
				}
				
				if(charAt0.contains(functionName))
				{
					return true;
				}
			}
			
			return false;
		}
		
		public boolean ifEnclosedByBrackets()
		{
			// being enclosed by brackets, operation level only reaches 0 once and at last
			
			int operationLevel0count = 0;
			
			for(int o : operationLevel)
			{
				operationLevel0count += o == 0 ? 1 : 0;
			}
			
			return string.startsWith("(") && string.endsWith(")") && operationLevel0count == 1;
		}
		
		public boolean ifElementStringValue()
		{
			try
			{
				double test = Double.parseDouble(string);
			}
			catch(Exception e)
			{
				return false;
			}
			
			return true;
		}
		
		private String[] _getSubElemetsStringsSplittedBy(ArrayList<Integer> locations)
		{

			// the start locations and end locations of "string" are added
			
			locations.add(0, 0);
			locations.add(string.length());
			
			
			String[] result = new String[locations.size() + 1 -2];
			
			for(int i = 0; i < locations.size() - 1; i++)
			{
				
				//System.out.println(string.substring(locations[i] + 1,locations[i+1]));
				
				if(i == 0)
				{
					result[i] = string.substring(locations.get(i),locations.get(i + 1));
				}
				else
				{
					result[i] = string.substring(locations.get(i) + 1,locations.get(i + 1));
				}

			}
			
			return result;
		}
		
		public String getFunctionName()
		{
			return string.substring(0, string.indexOf("("));
		}
		
		public String[] getValidPlusMinusAt0Symbols()
		{
			return _getSymbols(getValidAdditionSubtractionAt0Locations());
		}
		
		public String[] getMuliplicationDivisionAt0Symbols()
		{
			return _getSymbols(getMultiplicationDivisionAt0Locations());
		}
		
		private String[] _getSymbols(int[] locations)
		{
			String[] symbols = new String[locations.length];
			
			for(int i = 0; i < symbols.length; i++)
			{
				symbols[i] = String.valueOf(string.charAt(locations[i]));
			}
			
			return symbols;
		}
		
		public String[] getSubElementStringsSplittedByMultiplicationDivision()
		{
			return  _getSubElemetsStringsSplittedBy((ArrayList<Integer>) Arrays.stream( getMultiplicationDivisionAt0Locations() ).boxed().collect( Collectors.toList() ));
		}
		
		public String[] getSubElementStringsSplittedByValidAdditionSubtraction()
		{
			return _getSubElemetsStringsSplittedBy((ArrayList<Integer>) Arrays.stream( getValidAdditionSubtractionAt0Locations() ).boxed().collect( Collectors.toList() ));
		}
		
		public String getElementStringInBracket()
		{
			return string.substring(1,string.length() - 1);
		}
		
		public String[] getSubElementStringsInFunction()
		{
			// if the function only accepts 1 variable, it looks like f(x), (startTarget == "(" && endTarget == ")")
			
			// if the function accepts 2 variables, it looks like f(x,y),
			// (startTarget == "(" && endTarget == ",") || (startTarget == "," && endTarget == ")")
			
			// if the function accepts 3 and more variables, it looks like f(x,y,...,n),
			// (startTarget == "(" && endTarget == ",") || (startTarget == "," && endTarget == ")") || (startTarget == "," && endTarget == ",")
		
			
			ArrayList<String> subElementStandardizedStrings = new ArrayList<String>();
			
			int startPos = -1;
			int endPos = -1;
			
			
			// for a standardized string, the number of variables the function accepts == no. of "," + 1
			
			int _operandNum = 1;
			
			for(int i = 0; i < string.length(); i++)
			{
				if (string.charAt(i) == ',' && operationLevel[i] == 1)
				{
					_operandNum++;
				}
			}
			
			final int operandNum = _operandNum;
			
			
			// the process of getting the sub-elements strings beings
			
			int currOperandNum = 1;
			
			for(int i = 0; i < string.length(); i++)
			{
				
				// if we have found all sub-element strings, we can leave this loop
				if(currOperandNum > operandNum)
				{
					break;
				}
				
				
				// ** the ',' we are looking for are those whose operationLevel is 1, sub-element strings can have ',' as well!
				
				if(currOperandNum == 1)
				{
					if(startPos == -1)
					{
						startPos = string.indexOf("(");
					}

					if(endPos == -1)
					{
						if(operandNum != 1)
						{
							if(operationLevel[i] == 1 && string.charAt(i) == ',')
							{
								endPos = i;
							}
						}
						else if(operandNum == 1)
						{
							endPos = string.lastIndexOf(')');
						}

					}

					
				}
				else if(currOperandNum > 1 && currOperandNum < operandNum)
				{
					// the startPos have been set at the last iteration!
					
					if(endPos == -1)
					{
						if(operationLevel[i] == 1 && string.charAt(i) == ',')
						{
							endPos = i;
						}
					}
				}
				else
				{
					// the startPos have been set at the last iteration!
					
					if(endPos == -1)
					{
						endPos = string.lastIndexOf(')');
					}
				}
				
					//System.out.printf("statPos: %d, endPos: %d\n", startPos, endPos);
				
				if(startPos != -1 && endPos != -1)
				{
					String subElementString = string.substring(startPos + 1, endPos);
					
					subElementStandardizedStrings.add(subElementString);
					
					startPos = -1;
					endPos = -1;
					
					//increment currOperandNum
					
					currOperandNum++;
					
					// the new startPos should be here (the current value of i)

					startPos = i;
					
				}
			}
			
			String[] s = new String[1];
			s = subElementStandardizedStrings.toArray(s);
			
			return s;
		}
		
		/**
		 * if not found, -1 is returned (this should not be possible)
		 * @param location
		 * @return
		 */
		public int getNextLocaitonWithSameOperationLevel(int location)
		{
			int currOperationLevel = operationLevel[location];
			
			for(int i = location + 1; i < string.length(); i++)
			{
				if(operationLevel[i] == currOperationLevel)
				{
					return i;
				}
			}
			
			return -1;
		}
	}
}
