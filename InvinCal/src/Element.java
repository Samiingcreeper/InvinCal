/**
 *  Element is the representation of a formula.
 *  It can be a value or a formula.
 *  An element usually consists of sub-elements (i.e. sub-formula) with an operator combining them.
 *  
 *  An element can be evaluated to get a value.
 */
public abstract class Element 
{
	public abstract double getValue();
}
