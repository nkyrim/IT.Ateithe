package com.nkyrim.itapp.domain;

import java.io.Serializable;

public final class Staff implements Serializable {
	private static final long serialVersionUID = 4273900482197127576L;

	private final String detailId;
	private final String firstName;
	private final String lastName;
	private String officeTel;
	private String email;
	private String webpage;
	private String role;
	private String classes;

	public Staff(String detailId, String firstName, String lastName) {
		if(lastName == null) throw new IllegalArgumentException("lastName cannot be null");
		if(firstName == null) throw new IllegalArgumentException("firstName cannot be null");
		if(detailId == null) throw new IllegalArgumentException("detailId cannot be null");

		this.lastName = lastName;
		this.firstName = firstName;
		this.detailId = detailId;
	}

	public Staff(String detailId, String firstName, String lastName, String officeTel,
				 String email, String webpage, String role, String classes) {
		if(detailId == null) throw new IllegalArgumentException("detailId cannot be null");
		if(firstName == null) throw new IllegalArgumentException("firstName cannot be null");
		if(lastName == null) throw new IllegalArgumentException("lastName cannot be null");

		this.detailId = detailId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.officeTel = officeTel;
		this.email = email;
		this.webpage = webpage;
		this.role = role;
		this.classes = classes;
	}

	public String getClasses() {
		return classes;
	}

	public String getDetailId() {
		return detailId;
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

	public String getFullName() {
		return lastName + " " + firstName;
	}

	public String getRole() {
		return role;
	}

	public String getTel() {
		return officeTel;
	}

	public String getWebpage() {
		return webpage;
	}

	@Override
	public String toString() {
		return "Staff{" +
				"lastName='" + lastName + '\'' +
				", firstName='" + firstName + '\'' +
				", role='" + role + '\'' +
				", officeTel='" + officeTel + '\'' +
				", email='" + email + '\'' +
				", classes='" + classes + '\'' +
				", webpage='" + webpage + '\'' +
				", detailId='" + detailId + '\'' +
				'}';
	}
}
