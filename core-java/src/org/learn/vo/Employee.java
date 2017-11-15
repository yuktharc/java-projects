package org.learn.vo;


public class Employee implements Cloneable {

	private int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Employee(int age) {
		super();
		this.age = age;
	}
}
