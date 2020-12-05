
public class Formula extends Element
{
	private Element[] subElements;
	
	private Operator operator;
	
	public double getValue()
	{
		return operator.evaluate(subElements);
	}
	
	public Formula(Operator operator, Element... subElements)
	{
		this.operator = operator;
		this.subElements = subElements;
		
		String name = "";
		
//		if(operator == Operator.BRACKET)
//		{
//			name = "BRACKET";
//		}
//		else if (operator == Operator.SUMMATION)
//		{
//			name = "SUMMATION";
//		}
//		else if (operator == Operator.PRODUCT)
//		{
//			name = "PRODUCT";
//		}
//		else if (operator == Operator.RECIPROCAL)
//		{
//			name = "RECIPROCAL";
//		}
//		else if (operator == Operator.NEGATIVE)
//		{
//			name = "NEGATIVE";
//		}
//		else if (operator == Operator.FACTORIAL)
//		{
//			name = "FRACOTRIAL";
//		}
//		else if (operator == Operator.COMBINATION)
//		{
//			name = "COMBINATION";
//		}
//		
//		System.out.println("A Formula is created, opertor:" + name + ", number of operands: " + subElements.length);
	}
}
