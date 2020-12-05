import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.text.AbstractDocument.Content;

/**
 * Interpreter turns a standardized string into an element
 * @author samiingcreeper
 *
 */
public class Interpreter 
{
	public static void main(String[] args)
	{
		String ss = "((7)*((0)+(negative((1))))*(2))";
		// ( (7) + ( negative((3) ) )
		
		// ( (7) + ( negative((3)) ) )
		
		Element e = interpret(ss);
		
		System.out.println(e.getValue());
	}

	
	public static Element interpret(String standardizedString)
	{
		// get the content of the standardized string
		
		String content = getContent(standardizedString);
		
		
		// analyze the content
		
		var contentAnalyze = new InterpreterContentAnalyzer(content);
		
		
		// get the operator
		
		Operator operator = getOperator(contentAnalyze);
		
		
		// ** if the operator is null, we can already deduce that the element is a value
		
		if(operator == null)
		{
			double value = Double.valueOf(content);
			
			return new Value(value);
		}
		
		// get the sub-elements
		
		String[] subElementStandardizedStrings = contentAnalyze.getSubElementStandardizedStrings(operator);
		
		Element[] subElements = new Element[subElementStandardizedStrings.length];
		
		for(int i = 0; i < subElements.length; i++)
		{
			subElements[i] = interpret(subElementStandardizedStrings[i]);
		}
		
		
		// create and return a formula
		
		Formula formula = new Formula(operator, subElements);
		
		return formula;
	}
	
	/**
	 * Get the content inside the enclosing brackets of a standardized string.
	 * @param string
	 * @return
	 */
	private static String getContent(String string)
	{
//			System.out.println("getContent - string: " + string);
		
		// if the standardized string doesn't start with "(" or end with ")"
		if(!string.startsWith("(") || !string.endsWith(")"))
		{
			System.out.println("ERROR: STANDARDIZED STRING DOESN'T START WITH \"(\" AND END WTIH \")\"");
			
			return null;
		}
		
		return string.substring(1, string.length() - 1);
	}
	
	/**
	 * getOperator interpret the content to get the operator of the element. 
	 * There are only 4 forms of operator : Summation, Product, Bracket, and Function
	 * @param contentAnalyze
	 * @return
	 */
	private static Operator getOperator(InterpreterContentAnalyzer contentAnalyze)
	{
		if(contentAnalyze.isOperatorBracket())
		{
			return Operator.BRACKET;
		}
		
		Operator operator;
		
		operator = contentAnalyze.getFirstSummationProcutAt0();
		
		// if the operator is either summation or product
		
		if(operator != null)
		{
			return operator;
		}
		
		// if the operator is a function operator
		
		operator = contentAnalyze.getFunctionOperator();
		
		if(operator != null)
		{
			return operator;
		}
		
		return null;
	}
	
	
	
	
	/**
	 * StringAnalyzer analyzes a string
	 * @author samiingcreeper
	 *
	 */
	private static class InterpreterContentAnalyzer 
	{
		private final String content;
		
		/**
		 * Operation level at each location
		 */
		private int[] operationLevel;
		
		public InterpreterContentAnalyzer(String string)
		{
			this.content = string;
			operationLevel = initializeOperationLevel(this.content);
			
		}
		
		public String getString()
		{
			return content;
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
		 * Get the first summation or product when the operation level at the location is zero
		 */
		public Operator getFirstSummationProcutAt0()
		{
			for(int i = 0; i < operationLevel.length; i++)
			{
				if(operationLevel[i] == 0)
				{
					if(content.substring(i, i+1).equals("+"))
					{
						return Operator.SUMMATION;
					}
					else if (content.substring(i, i+1).equals("*"))
					{
						return Operator.PRODUCT;
					}
				}
			}
			
			return null;
		}
		
		public Operator getFunctionOperator()
		{
			// get the name of the function
			
			int openBracketPos = content.indexOf("(");
			
			// if openBracketPos == -1, we know that there's no function operator
			
			if(openBracketPos == -1)
			{
				return null;
			}
			
			String name = content.substring(0, openBracketPos);
			
				//System.out.println("getFunctionOperator: name of the function: " + name);
			
			// if the function exist, we return the corresponding operator. Otherwise, null is returned
			
			if(Operator.operatorList.containsKey(name))
			{
				return Operator.operatorList.get(name);
			}
			else
			{
				return null;
			}
				
		}
		
		public boolean isOperatorBracket()
		{
			// if the content starts with "(" and ends with ")", and operation level only reaches 0 at the end, we know that the operator is bracket.
			
			// if it only either starts with "(" or ends with ")", there's an error.
			
			if(content.startsWith("(") && content.endsWith(")")) 
			{
				int operationLevel0Count = 0;
				
				for(int i : operationLevel)
				{
					if(i == 0)
					{
						operationLevel0Count++;
					}
				}
				
				if(operationLevel0Count == 1)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
					
		}
		
		/**
		 * ----The current method can only get a sub-element from an element with function operator with one variable!!!
		 * @param the operator of the element
		 * @return
		 */
		public String[] getSubElementStandardizedStrings(Operator operator)
		{
			if(operator instanceof Operator.Function)
			{
				return getSubElementStandardizedStrings_Function();
			}
			
			// (((A) + (B)) - (B) + (C)) -> the sub-elements are ((A) + (B)), (B), (C)
			
			// the start of a sub element string is when operationLevel[i] == 1 && content.charAt(i) == '('
			// the end of a sub element string is when operationLevel[i] == 0 && content.charAt(i) == '('

			ArrayList<String> subElementStandardizedStrings = new ArrayList<String>();
			
			// -1 represents the position of the current sub-element string is not found 
			
			int startPos = -1;
			int endPos = -1;
			
			for(int i = 0; i < content.length(); i++)
			{
				if(operationLevel[i] == 1 && content.charAt(i) == '(')
				{
					startPos = i;
				}
				else if (operationLevel[i] == 0 && content.charAt(i) == ')')
				{
					endPos = i;
				}
				
				// if both startPos and endPos are found, we already know the content (and the brackets) of a sub-element
				
				if(startPos != -1 && endPos != -1)
				{
					// ** the string has to be processed by standardizer (implemented later)
					
					String subElementString = content.substring(startPos, endPos + 1 );
					
					subElementStandardizedStrings.add(subElementString);
					
					startPos = -1;
					endPos = -1;
				}
			}
			
			String[] s = new String[1];
			s = subElementStandardizedStrings.toArray(s);
			
			return s;
		}
		
		/**
		 * This is the specific method for getting sub-elements from a formula with function operator
		 * @return
		 */
		public String[] getSubElementStandardizedStrings_Function()
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
			
			for(int i = 0; i < content.length(); i++)
			{
				if (content.charAt(i) == ',' && operationLevel[i] == 1)
				{
					_operandNum++;
				}
			}
			
			final int operandNum = _operandNum;
			
			
			// the process of getting the sub-elements strings beings
			
			int currOperandNum = 1;
			
			for(int i = 0; i < content.length(); i++)
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
						startPos = content.indexOf("(");
					}

					if(endPos == -1)
					{
						if(operandNum != 1)
						{
							if(operationLevel[i] == 1 && content.charAt(i) == ',')
							{
								endPos = i;
							}
						}
						else if(operandNum == 1)
						{
							endPos = content.lastIndexOf(')');
						}

					}

					
				}
				else if(currOperandNum > 1 && currOperandNum < operandNum)
				{
					// the startPos have been set at the last iteration!
					
					if(endPos == -1)
					{
						if(operationLevel[i] == 1 && content.charAt(i) == ',')
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
						endPos = content.lastIndexOf(')');
					}
				}
				
					//System.out.printf("statPos: %d, endPos: %d\n", startPos, endPos);
				
				if(startPos != -1 && endPos != -1)
				{
					String subElementString = content.substring(startPos + 1, endPos);
					
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
		
		
		
	}
	
}