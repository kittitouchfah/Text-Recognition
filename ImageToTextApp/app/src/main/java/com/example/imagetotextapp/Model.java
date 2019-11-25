package com.example.imagetotextapp;

public class Model {
    String id,text,user,date,filename;
     public Model(){

     }
     public Model(String id,String text,String user,String date,String filename){
         this.id = id;
         this.text = text;
         this.user = user;
         this.date = date;
         this.filename = filename;
     }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public String getFilename() {
        return filename;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
