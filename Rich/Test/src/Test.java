enum Conditions {NORMAL, STOP, DOOM, LUCKY};

class data
{
	Conditions c = Conditions.NORMAL;
	boolean x = false;
	int m = 20;
	String s = "hahaha";
}

public class Test 
{
	data d;
	static Conditions c = Conditions.NORMAL;
	static boolean x = false;
	static int m = 20;
	public static void main (String args[])
	{
		{
		String s = "!" + m + "!" + x + "!" + c;
		System.out.println(s);
		}
	}

  
}  