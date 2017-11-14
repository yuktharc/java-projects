package org.learn.demo;

public class StringEqualsAndHashCodeDemo {
	public static void main(String[] args) {

		/**
		 * equals() and hadhCode() methods contract: If two objects are equal
		 * using equals() method then the hashcode of those two objects should
		 * be equal. But If two objects are NOT equal using equals() method then
		 * the hashcode of those two objects MAY or MAY NOT be equal.
		 */

		// String Object - same values comparision
		String x = "Hari";
		String y = "Hari";
		System.out.println(x.hashCode()); // same hash code as y.hashCode()
		System.out.println(y.hashCode()); // same hash code as x.hashCode()
		System.out.println(x.equals(y)); // true
		/**
		 * "=" operator compares object references. Since String objects
		 * internally maintain pool of objects and objects with same content
		 * will always point to same reference or memory location. So "x == y"
		 * in this case is true.
		 */
		System.out.println(x == y); // true
		System.out.println(x == y.intern()); // true

		// ===========================================================================
		// String Object - different values comparision
		x = "Hari";
		y = "Hello";
		// different hash code compared to y.hashCode()
		System.out.println(x.hashCode());
		// different hash code when compared to x.hashCode()
		System.out.println(y.hashCode());
		System.out.println(x.equals(y)); // false
		/**
		 * "=" operator compares object references. Since String objects
		 * internally maintain pool of objects and objects with different
		 * content will always point to difference reference or memory location.
		 * So "x == y" in this case is false.
		 */
		System.out.println(x == y); // false
		System.out.println(x == y.intern()); // false
		
		// ===========================================================================
		// String Object - different values comparision but same hash code
		x = "FB";
		y = "Ea";
		System.out.println(x.hashCode()); // same hash code as y.hashCode()
		System.out.println(y.hashCode()); // same hash code as x.hashCode()
		System.out.println(x.equals(y)); // false
		/**
		 * "=" operator compares object references. Since String objects
		 * internally maintain pool of objects and objects with different
		 * content will always point to difference reference or memory location.
		 * So "x == y" in this case is false.
		 */
		System.out.println(x == y); // false
		System.out.println(x == y.intern()); // false

		// ===========================================================================
		// String Object - different values comparision
		x = "Hari";
		/**
		 * Unless an explicit copy of original is needed, use of this
		 * constructor is unnecessary since Strings are immutable.
		 */
		y = new String("Hari");
		// different hash code compared to y.hashCode()
		System.out.println(x.hashCode());
		// different hash code when compared to x.hashCode()
		System.out.println(y.hashCode());
		System.out.println(x.equals(y)); // true
		/**
		 * "=" operator compares object references. Since String objects
		 * internally maintain pool of objects and objects with different
		 * content will always point to difference reference or memory location.
		 * So "x == y" in this case is false.
		 */
		System.out.println(x == y); // false
		/**
		 * A pool of strings, initially empty, is maintained privately by the
		 * class String. When the intern method is invoked, if the pool already
		 * contains a string equal to this {@code String} object as determined
		 * by the {@link #equals(Object)} method, then the string from the pool
		 * is returned. Otherwise, this {@code String} object is added to the
		 * pool and a reference to this {@code String} object is returned.
		 * 
		 * In this case the string from the pool is returned.
		 */
		System.out.println(x == y.intern()); // true

	}
}
