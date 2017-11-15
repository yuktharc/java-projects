package org.learn.demo;

import org.learn.vo.Employee;
import org.learn.vo.Person;

public class EmployeeEqualsAndHashCodeDemo {

	public static void main(String a[]) {
		Employee e1 = new Employee(12);
		Employee e2 = new Employee(12);

		System.out.println(e1); // org.learn.vo.Employee@15db9742
		System.out.println(e2); // org.learn.vo.Employee@6d06d69c
		System.out.println(e1.hashCode()); // different from e2.hashCode()
		System.out.println(e2.hashCode()); // different from e1.hashCode()
		/**
		 * In this case equals() and hashCode() are not overridden. So default
		 * implementation of equals() from Object class which compares object
		 * references will be used. Since the references of these two objects
		 * are always different default implementation of equals() method will
		 * return false.
		 */
		System.out.println(e1.equals(e2)); // false

		Person p1 = new Person(12);
		Person p2 = new Person(12);
		System.out.println(p1); // org.learn.vo.Person@2b
		System.out.println(p2); // org.learn.vo.Person@2b
		System.out.println(p1.hashCode()); // same as e2.hashCode()
		System.out.println(p2.hashCode()); // same as e1.hashCode()
		/**
		 * In Person class, we have overridden equals() along with hashCode()
		 * method to maintain contract between those two methods and to truly
		 * verify the equality of two Person objects.
		 * 
		 * If we do not override hadhCode() and if it returns two different
		 * hashCode for two Persons objects which are equal with equals()
		 * method, then we would be storing those two objects in the same bucket
		 * while storing them in the buckets as part hashing data structures
		 * like HashTable. This would affect the performance in fetching the
		 * objects.
		 */
		System.out.println(p1.equals(p2)); // true
	}

}
