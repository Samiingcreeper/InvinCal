import java.util.*;

public interface Interrelated 
{	
	Set<Interrelated> getReferenceTo();
	
	void setReferenceTo(Set<Interrelated> s);
	
	static boolean isRecurisve(Interrelated ir, String implementation)
	{
		Set<Interrelated> referenceTo = new HashSet<>();
		if(ir instanceof Constant)
			referenceTo = InterrelatedExtractor.extract(implementation, (Constant)ir);
		else
			referenceTo = InterrelatedExtractor.extract(implementation, (CustomFunction)ir);
		
		Set<Interrelated> set = new HashSet<>(/*referenceTo*/);
	/*	boolean recursive =  !*/set.add(ir);
	//	if(recursive)
	//		return true;
		
		for(Interrelated iir : referenceTo)
		{
			return traverse(iir, set);
		}
		
		return false;
		
	}
	
	private static boolean traverse(Interrelated ir, Set<Interrelated> s)
	{
//		System.out.println();
//		System.out.println("ir: " + ir + " reference to: " + ir.getReferenceTo().toString() + " set: " + s.toString() + "\n\n");
		
		Set<Interrelated> set = new HashSet<>(s);
		
		Set<Interrelated> referenceTo = ir.getReferenceTo();
		boolean recursive = !set.add(ir);
		if(recursive) return true;
		
		for(Interrelated iir : referenceTo)
		{
			return traverse(iir, set);
		}
		
		return false;
	}
}
