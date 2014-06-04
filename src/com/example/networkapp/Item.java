package com.example.networkapp;

// This class represents a single entry (post) in the XML feed.
// It includes the data members "title," "link," and "summary."
public class Item {
	private int id;
    private String title;
    private String description;
    private float price;
    private int categoryId;
    private String imageUrl; 
    private String image;
    private int quantity = 1;
    private int position;

    //generic values to configs items
    private String key;
    private String value;
	

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getDescription() {
		return description;
	}
	public String getDescription(int length) {
		
			if (description.length() > length){
				description = description.substring(0, length);
				description +="...";
			}
		
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public float getPrice() {
		return price;
	}



	public void setPrice(float price) {
		this.price = price;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getCategoryId() {
		return categoryId;
	}



	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}



	public String getImageUrl() {
		return imageUrl;
	}



	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}



	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
	}



	public int getQuantity() {
		return quantity;
	}



	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}



	public int getPosition() {
		return position;
	}



	public void setPosition(int position) {
		this.position = position;
	}
}
