
public class CustomFunctionInterpreter 
{
	public static Element interpret(String standardizedString, CustomFunction function)
	{
		// get the content of the standardized string
		String content = Interpreter.getContent(standardizedString);
		
		// analyze the content
		var contentAnalyze = new InterpreterContentAnalyzer(content);
		
		// get the operator
		Operator operator = Interpreter.getOperator(contentAnalyze);
		
		
		// ** if the operator is null, we can already deduce that the element is a value
		if(operator == null)
		{
			double value = 0;
			
			if(function.isVariable(content))
			{
				return function.getVariableValue(content);
			}
			else if(Constant.isConstant(content))
			{				
				return Constant.getConstant(content);
			}
			else if(Interpreter.isValue(content))
			{
				value = Double.valueOf(content);
			}
			else
			{
				// ERROR -- the content is neither a constant or a value, the constant is undefined
				System.out.println("ERROR -- THE CONTENT IS NEITHER A CONSTANT OR A VALUE, THE CONSTANT IS UNDEFINED OR YOUR INPUT IS INVALID");
				value = Double.NaN;
			}
			
			return new Value(value);
		}
		
		// get the sub-elements
		
		String[] subElementStandardizedStrings = contentAnalyze.getSubElementStandardizedStrings(operator);
		
		Element[] subElements = new Element[subElementStandardizedStrings.length];
		
		for(int i = 0; i < subElements.length; i++)
		{
			subElements[i] = interpret(subElementStandardizedStrings[i], function);
		}
		
		
		// create and return a expression
		
		Expression expression = new Expression(operator, subElements);
		
		return expression;
	}
}
