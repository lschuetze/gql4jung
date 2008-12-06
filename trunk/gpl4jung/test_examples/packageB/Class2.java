package packageB;

import packageA.Class1;

public class Class2 {
	private int num;
	private Class1 obj1=new Class1();
	
	public void setValue(int x)
	{
		num=x;
		obj1.showText();
	}
}
