package com.name.social_helper_r_p.user.edit;

public class Thing {
    private String title = "";
    private String description = "";
    private int quantity = 1;
    private String category = "";
    private String secondCategory = "";
    private int get=0;


    public Thing(){

    }

    public Thing(String title, String description, int quantity, String category, String secondCategory, int get){
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.secondCategory = secondCategory;
        this.get = get;
    }

    public int getGet() {
        return get;
    }

    public void setGet(int get) {
        this.get = get;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }
}
