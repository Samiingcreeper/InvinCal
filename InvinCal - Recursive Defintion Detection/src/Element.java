/**
 *  Element is the representation of a expression.
 *  It can be a value or a expression.
 *  An element usually consists of sub-elements (i.e. sub-expression) with an operator combining them.
 *  
 *  An element can be evaluated to get a value.
 */
public abstract class Element 
{
	public abstract double getValue();
}

