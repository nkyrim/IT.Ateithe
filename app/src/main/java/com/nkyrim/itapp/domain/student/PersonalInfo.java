package com.nkyrim.itapp.domain.student;

import java.io.Serializable;

public final class PersonalInfo implements Serializable {
	private final static long serialVersionUID = -6670229860083046603L;

	// general information
	private final String lastName;
	private final String firstName;
	private final String am;
	private final String department;
	private final String semester;
	private final String program;
	private final String rate;
	// registration info
	private final String year;
	private final String period;
	private final String regSemester;
	private final String regMode;
	// address permanent
	private final String p_address;
	private final String p_zip;
	private final String p_city;
	private final String p_country;
	// address current
	private final String c_address;
	private final String c_zip;
	private final String c_city;
	private final String c_country;
	// contact info
	private final String phone1;
	private final String phone2;
	private final String email;

	public PersonalInfo(String lastName, String firstName, String am, String department, String semester, String program,
						String rate, String year, String period, String regSemester, String regMode, String p_address, String p_zip,
						String p_city, String p_country, String c_address, String c_zip, String c_city, String c_country, String phone1,
						String phone2, String email) {

		if(lastName == null) throw new IllegalArgumentException("lastName cannot be null");
		if(firstName == null) throw new IllegalArgumentException("firstName cannot be null");
		if(am == null) throw new IllegalArgumentException("am cannot be null");
		if(semester == null) throw new IllegalArgumentException("semester cannot be null");

		this.lastName = lastName;
		this.firstName = firstName;
		this.am = am;
		this.department = department;
		this.semester = semester;
		this.program = program;
		this.rate = rate;
		this.year = year;
		this.period = period;
		this.regSemester = regSemester;
		this.regMode = regMode;
		this.p_address = p_address;
		this.p_zip = p_zip;
		this.p_city = p_city;
		this.p_country = p_country;
		this.c_address = c_address;
		this.c_zip = c_zip;
		this.c_city = c_city;
		this.c_country = c_country;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.email = email;
	}

	public String getYear() {
		return year;
	}

	public String getAm() {
		return am;
	}

	public String getC_address() {
		return c_address;
	}

	public String getC_city() {
		return c_city;
	}

	public String getC_country() {
		return c_country;
	}

	public String getC_zip() {
		return c_zip;
	}

	public String getDepartment() {
		return department;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getP_address() {
		return p_address;
	}

	public String getP_city() {
		return p_city;
	}

	public String getP_country() {
		return p_country;
	}

	public String getP_zip() {
		return p_zip;
	}

	public String getPeriod() {
		return period;
	}

	public String getPhone1() {
		return phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public String getProgram() {
		return program;
	}

	public String getRegMode() {
		return regMode;
	}

	public String getRegSemester() {
		return regSemester;
	}

	public String getSemester() {
		return semester;
	}

	public String getRate() {
		return rate;
	}

	@Override
	public String toString() {
		return "PersonalInfo{" +
				"lastName='" + lastName + '\'' +
				", firstName='" + firstName + '\'' +
				", am='" + am + '\'' +
				", department='" + department + '\'' +
				", semester='" + semester + '\'' +
				", program='" + program + '\'' +
				", rate='" + rate + '\'' +
				", year='" + year + '\'' +
				", period='" + period + '\'' +
				", regSemester='" + regSemester + '\'' +
				", regMode='" + regMode + '\'' +
				", p_address='" + p_address + '\'' +
				", p_zip='" + p_zip + '\'' +
				", p_city='" + p_city + '\'' +
				", p_country='" + p_country + '\'' +
				", c_address='" + c_address + '\'' +
				", c_zip='" + c_zip + '\'' +
				", c_city='" + c_city + '\'' +
				", c_country='" + c_country + '\'' +
				", phone1='" + phone1 + '\'' +
				", phone2='" + phone2 + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
