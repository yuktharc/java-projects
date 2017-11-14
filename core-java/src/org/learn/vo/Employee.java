package org.learn.vo;


public class Employee implements Cloneable {

	private int age;

	public Employee(int age) {
		super();
		this.age = age;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (age != other.age)
			return false;
		return true;
	}


	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
