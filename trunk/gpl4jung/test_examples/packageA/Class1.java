package packageA;

public class Class1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Class3 test = new Class3();		//referenecing obj from Class3 in the same package
		test.setValue(8);
		System.out.println("Value of x:" + test.getValue());
		}
	public void showText()
	{
		System.out.println("HELLO WORLD");
	}
}
