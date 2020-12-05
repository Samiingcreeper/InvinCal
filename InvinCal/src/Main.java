import java.util.*;

public class Main {

	public static void main(String[] args)
	{
		System.out.println("===============================================================");
		System.out.println("                        InvinCal 超級計算機                       ");
		System.out.println("===============================================================");                                                         
		System.out.println("  Avaliable Functions 內置數學函數:                                ");
		System.out.println("      1. reciprocal(x) 倒數       2. negative(x) 負數             ");
		System.out.println("      3. factorial(x) 階乖        4. combination(n,r) 組合        ");
		System.out.println("      5. pow(x,y) 幕次            6. pow10(x) 十的幕次              ");
		System.out.println("===============================================================");
		
		
		String string = "";
		
		while(!string.equals("exit"))
		{
			Scanner s = new Scanner(System.in);
			
			System.out.print("Input: ");
			
			string = s.nextLine();
			
			if(!string.equals("exit"))
			{
				Element e = Interpreter.interpret(Standardizer.standardize(string));
				
				System.out.println("Answer: " + e.getValue());
			}
			
			System.out.print("\n");
		}
			
	}
}
