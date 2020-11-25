package com.example.requip;

public class saman {
    String id;
    String title;
    String username;
    String price;
    String type;
    String description;
    String phone;
    String image;

    public saman(){
        // empty cuonstructor needed for firebse.
    }

    public saman(String id, String title, String username, String price, String type, String description, String phone, String image) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.price = price;
        this.type = type;
        this.description = description;
        this.phone = phone;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }
}
