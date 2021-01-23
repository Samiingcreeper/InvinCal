
public class Value extends Element
{
	private double value = Double.NaN;
	
	public double getValue()
	{
		return value;
	}
	
	public void assign(double value)
	{
		this.value = value;
	}
	
	/**
	 * Used by custom functions!
	 */
	public Value()
	{
		
	}
	
	public Value (double value)
	{
		this.value = value;
	}
}
