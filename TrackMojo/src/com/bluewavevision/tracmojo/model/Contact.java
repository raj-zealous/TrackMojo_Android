package com.bluewavevision.tracmojo.model;

import java.io.Serializable;

public class Contact implements Comparable<Contact> ,Serializable{
	String name;
	String phone;
	String email;
	String id;


    boolean isSelected;
	boolean  hasEmail,hasPhone;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isHasEmail() {
		return hasEmail;
	}
	public void setHasEmail(boolean hasEmail) {
		this.hasEmail = hasEmail;
	}
	public boolean isHasPhone() {
		return hasPhone;
	}
	public void setHasPhone(boolean hasPhone) {
		this.hasPhone = hasPhone;
	}
	@Override
	public int compareTo(Contact another) {
		// TODO Auto-generated method stub
		return this.getName().compareToIgnoreCase(another.getName());
	}

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
