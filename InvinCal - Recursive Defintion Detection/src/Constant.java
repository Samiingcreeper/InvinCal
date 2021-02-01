import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Constant can be expressed by either a value or an expression.
 * Constants include system defined constants, user defined constants, result storing locations etc, only the system defined ones are not allow to redefine.
 * User-defined constants can also be deleted.
 * 
 * Naming of Constants : No same name as already existed constants and functions, No symbols
 * 
 * -- Later on, when custom function is introduced, functions may be unusable because the functions they refers to are deleted, causing constants that are expressed by 
 *    those functions to be unusable as well. In this case, the value of the constants should be NaN.
 * @author samiingcreeper
 *
 */
public class Constant extends Element implements Interrelated
{
	
	private String name;
	
	/**
	 * For constant expressed by a value, the object would be a "Value", 
	 * For constant expressed by an expression, the object would be an "Expression".
	 */
	private Element expression;
	
	/**
	 * Whether the constant can be redefined.
	 */
	private boolean changeable = true;
	
	/**
	 * Whether the constant can be deleted.
	 */
	private boolean deletable = true;
	
	/**
	 * If this constant is deleted, when getValue is called, NaN is returned
	 */
	private boolean isDeleted = false;
	
	public boolean getChangeable() {
		return changeable;
	}
	
	public double getValue()
	{
		if(isDeleted)
		{
			return Double.NaN;
		}
		
		return expression.getValue();
	}
	
	private boolean modify(Element e, String expression)
	{
		if(Interrelated.isRecurisve(this, expression)) return false;
		
		this.expression = e;
		this.referenceTo = InterrelatedExtractor.extract(expression,this);
		return true;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * This construction is mainly for adding the name to the usedNames list
	 * @param name
	 */
	private Constant(String name)
	{
		this.name = name;
		SystemController.addUsedName(name);
	}
	
	private Constant(String name, double value, boolean changeable, boolean deletable)
	{
		this(name);
		this.expression = new Value(value);
		this.changeable = changeable;
		this.deletable = deletable;
	}
	
	private Constant(String name, Element e, boolean changeable, boolean deletable)
	{
		this(name);
		this.expression = e;
		this.changeable = changeable;
		this.deletable = deletable;
	}
	
	private static ArrayList<Constant> storageLocations = new ArrayList<Constant>();
	static
	{
		storageLocations.add(new Constant("A", 0, true, false));
		storageLocations.add(new Constant("B", 0, true, false));
		storageLocations.add(new Constant("C", 0, true, false));
		storageLocations.add(new Constant("D", 0, true, false));
		storageLocations.add(new Constant("E", 0, true, false));
		storageLocations.add(new Constant("F", 0, true, false));
		storageLocations.add(new Constant("G", 0, true, false));
		storageLocations.add(new Constant("ANS", 0, true, false));	// Answer

	}
	
	private static ArrayList<Constant> systemDefinedConstant = new ArrayList<Constant>();
	static
	{
		systemDefinedConstant.add(new Constant("PI", 3.1415926535897931, false, false));
	}
	
	private static ArrayList<Constant> userDefinedConstants = new ArrayList<Constant>();
	
	public static boolean isConstant(String name)
	{
		for(Constant c : storageLocations)
		{
			if(c.getName().equals(name)) return true;
		}
		
		for(Constant c : systemDefinedConstant)
		{
			if(c.getName().equals(name)) return true;
		}
		
		for(Constant c : userDefinedConstants)
		{
			if(c.getName().equals(name)) return true;
		}
		
		return false;
	}
	
	/**
	 * return the value of the constant. If not found, returns NaN.
	 * @param name
	 * @return
	 */
	public static double getValue(String name)
	{	
		for(Constant c : storageLocations)
		{
			if(c.getName().equals(name)) return c.getValue();
		}
		
		for(Constant c : systemDefinedConstant)
		{
			if(c.getName().equals(name)) return c.getValue();
		}
		
		for(Constant c : userDefinedConstants)
		{
			if(c.getName().equals(name)) return c.getValue();
		}
		
		return Double.NaN;
	}
	
	public static Constant getConstant(String name)
	{
		for(Constant c : storageLocations)
		{
			if(c.getName().equals(name)) return c;
		}
		
		for(Constant c : systemDefinedConstant)
		{
			if(c.getName().equals(name)) return c;
		}
		
		for(Constant c : userDefinedConstants)
		{
			if(c.getName().equals(name)) return c;
		}
		
		return null;
	}
	
	/**
	 * Create a constant expressed by an expression, if the constant already exits, false is returned.
	 * @param name
	 * @param e
	 * @return
	 */
	private static boolean createExpressionConstant(String name, Element e, String expression)
	{
		//System.out.println("CREATE");
		
		// Only constant with unused name and valid name can be created.
		if(!SystemController.isNameUsed(name) && SystemController.validName(name))
		{
			Constant c = new Constant(name, e, true, true);
			userDefinedConstants.add(c);
			c.referenceTo = InterrelatedExtractor.extract(expression,c);
			return true;
		}
		else
		{
			return false;
		}

	}
	
	public static boolean createConstant(String name, String expression)
	{
		return createExpressionConstant(name, Interpreter.interpret(Standardizer.standardize(expression)), expression);
	}
	
	public static boolean createConstant(String name, double value)
	{	
		return createExpressionConstant(name, new Value(value), Double.toString(value));
	}
	
	/**
	 * True is returned for successful modification
	 * @param name
	 * @param e
	 * @return
	 */
	private static boolean modifyConstant(String name, Element e, String expression)
	{		
		//System.out.println("MODIFY");
		
		Constant c = getConstant(name);
		
		if(c != null && c.getChangeable())
		{
			return c.modify(e,expression);
			
		}
		else
		{
			return false;
		}
	}
	
	public static boolean modifyConstant(String name, String expression)
	{
		return modifyConstant(name, Interpreter.interpret(Standardizer.standardize(expression)),expression);
	}
	
	public static boolean modifyConstant(String name, double value)
	{
		return modifyConstant(name, new Value(value),Double.toString(value));
	}
	
	/**
	 * Returns true for successful deletion
	 * @param name
	 * @return
	 */
	public static boolean deleteConstant(String name)
	{
		// only user-defined constants can be deleted
		
		for(Constant c : userDefinedConstants)
		{
			if(c.getName().equals(name))
			{
				userDefinedConstants.remove(c);
				c.isDeleted = true;
				return true;
			}
		}
		
		return false;
	}

	public static void main(String[] args)
	{
		System.out.println(createConstant("PI",3));
		System.out.println(userDefinedConstants.isEmpty());
		System.out.printf("%f\n",3.1415161718);
		System.out.println(getConstant("A").getChangeable());
	}

	
	private Set<Interrelated> referenceTo = new HashSet<>();
	@Override
	public Set<Interrelated> getReferenceTo() 
	{
		return referenceTo;
	}
	@Override
	public void setReferenceTo(Set<Interrelated> s)
	{
		this.referenceTo = s;
	}

}
