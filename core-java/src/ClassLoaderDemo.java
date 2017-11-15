
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ClassLoaderDemo {

	// Note: Run this program from command prompt. The directory "." assumes C.class in the current directory.
	public static void main(String[] argv) throws Exception {

		String code = "public class C implements Base {\n" + "   public void run() {\n"
				+ "      System.out.println(\"++++++++++\");\n" + "   }}";

		createClassFile(code); // Implemented in the next slide
		Class classB = Class.forName("C");
		Base b = (Base) classB.newInstance();
		b.run();
	}

	private static void createClassFile(String code) throws Exception {
		OutputStream os = new FileOutputStream(new File("C.java"));
		os.write(code.getBytes());
		os.flush();
		os.close();
		Process p = Runtime.getRuntime().exec("javac -classpath . C.java");
		p.waitFor();
	}
}

interface Base {
	public void run();
}
