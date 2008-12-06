package packageA;

import packageB.Class2;

public class Class3 {
	private int x;
	private Class2 obj= new Class2();
	
	public void setValue(int y)
	{
		x=y;
	}
	
	public int getValue()
	{
		return x;
	}

	public void setObjValue(int n)
	{
		obj.setValue(555);		//link to class2 in packageB
	}
}
