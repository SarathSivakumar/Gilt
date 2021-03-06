package com.streak.gilt;

public class User {
    public  int id;
    public String name;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String role;

    public User(int id,String name,String role){
        this.id=id;
        this.name=name;
        this.role=role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
