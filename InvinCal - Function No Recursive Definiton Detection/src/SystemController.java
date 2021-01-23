import java.util.LinkedList;

/**
 * SystemController stores important informations about the program
 * @author samiingcreeper
 *
 */
public class SystemController 
{
	/**
	 * Stores the names that already exits in the program. The names include function names and constant names.
	 */
	private static LinkedList<String> usedNames = new LinkedList<String>();
	
	/**
	 * Return true for successful addition.
	 * @param name
	 * @return
	 */
	public static boolean addUsedName(String name)
	{
		if(!isNameUsed(name))
		{
			usedNames.add(name);
			//System.out.println(usedNames.toString());
			return true;
		}
		return false;
	}
	
	/**
	 * Return true for successful deletion.
	 * @param name
	 * @return
	 */
	public static boolean deleteUsedName(String name)
	{
		if(isNameUsed(name))
		{
			usedNames.removeIf(string -> string.equals(name));
			return true;
		}
		return false;
	}
	
	public static boolean isNameUsed(String name)
	{
		for(String s : usedNames)
		{
			if(s.equals(name)) return true;
		}
		
		return false;
	}
	
	/**
	 * Definition of valid name:
	 * 1. no symbols
	 * 2. contain at least 1 alphabet
	 * @param name
	 * @return
	 */
	public static boolean validName(String name)
	{
		boolean noSymbols = name.matches("^[a-zA-Z0-9]+$");
		
		String onlyAlphabets = name.replaceAll("[0-9]*", "");
		boolean atLeast1Alphabet = onlyAlphabets.matches("^[a-zA-Z]+$");
		return noSymbols && atLeast1Alphabet;
	}
	
	public static void main(String[] args)
	{
		System.out.println(validName("aaaa1"));
	}
}
