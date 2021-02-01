import java.util.*;

public class InterrelatedExtractor 
{
	public static Set<Interrelated> extract(String s, Constant c)	// s -> standardized string
	{
		List<String> wordList = new ArrayList<>();
		String word = "";
		for(int i = 0; i < s.length(); i++)
		{	
			String character = s.substring(i,i+1);
			if(character.matches("^[a-zA-Z]+$"))
			{
				word += character;
			}
			else
			{
				if(!word.isEmpty() && SystemController.isInterrelated(word)) wordList.add(word);
				word = "";
			}
		}
		if(!word.isEmpty() && SystemController.isInterrelated(word)) wordList.add(word);
		
		Set<Interrelated> referenceTo = new HashSet<>();
		for(String ss : wordList)
		{
			referenceTo.add(SystemController.getInterrelated(ss));
		}
		
		return referenceTo;
	}
	
	public static Set<Interrelated> extract(String s, CustomFunction f)	// s -> standardized string
	{
		List<String> wordList = new ArrayList<>();
		String word = "";
		for(int i = 0; i < s.length(); i++)
		{	
			String character = s.substring(i,i+1);
			if(character.matches("^[a-zA-Z]+$"))
			{
				word += character;
			}
			else
			{
				if(!word.isEmpty() && SystemController.isInterrelated(word)) 
					if(!f.isVariable(word))
						wordList.add(word);
				word = "";
			}
		}
		if(!word.isEmpty() && SystemController.isInterrelated(word)) wordList.add(word);
		
		Set<Interrelated> referenceTo = new HashSet<>();
		for(String ss : wordList)
		{
			referenceTo.add(SystemController.getInterrelated(ss));
		}
		
		return referenceTo;
	}
	
	private static Set<String> getExtractWordList(String s)	// s -> standardized string
	{
		//System.out.println(s);
		
		List<String> wordList = new ArrayList<>();
		String word = "";
		for(int i = 0; i < s.length(); i++)
		{	
			String character = s.substring(i,i+1);
			
			//System.out.println("character: " + character);
			
			if(character.matches("^[a-zA-Z]+$"))
			{
				//System.out.println("match");
				
				word += character;
			}
			else
			{
				if(!word.isEmpty() && SystemController.isInterrelated(word)) wordList.add(word);
				word = "";
			}
		}
		if(!word.isEmpty() && SystemController.isInterrelated(word)) wordList.add(word);
		
		return new HashSet(wordList);
	}
	
	public static void main(String[] args)
	{		
		Main.setConstant("a->--expression 1", true);
		Main.setConstant("b->--expression a", true);
		Main.setConstant("c->--expression b", true);
		//Main.setConstant("a->--expression c", true);
		Main.setConstant("d->--expression b", true);
		Main.setConstant("e->--expression d", true);
//		Main.setConstant("d->--expression 1", true);
//		Main.setFunction("c(x) --> x + d");
//		Main.setConstant("b ->--expression c(1)",true);
//		Main.setFunction("a(x) --> c(x) + b");
		//System.out.println("x: " + Constant.getConstant("g").getReferenceTo());
	//	System.out.println("a: " + Function.getCustomFunction("a")  + " referenceTo: " + Function.getCustomFunction("a").getReferenceTo());
		
		System.out.println(Interrelated.isRecurisve(Function.getCustomFunction("e"), "d"));
		
		Main.setConstant("a->--expression e", true);
		//System.out.println(getExtractWordList("A + B"));
	}
}
