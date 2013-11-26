package ru.nordmine.crystalmoney.category;

import java.io.Serializable;

public class CategoryItem implements Serializable {
	
	private int id;
	private int categoryType;
	private String name;
	
	public CategoryItem(int id, int categoryType, String name) {
		this.id = id;
		this.categoryType = categoryType;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(int categoryType) {
		this.categoryType = categoryType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
