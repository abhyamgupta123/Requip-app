package com.example.requip;

public class saman {
    String id;
    String title;
    String price;
    String type;
    String image;

    public saman(){
        // empty cuonstructor needed for firebse.
    }

    public saman(String id, String title, String price, String type, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.type = type;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }
}
