package com.example.mobile.model;

import com.google.gson.annotations.SerializedName;

public class ExpenseDetail {
    @SerializedName("id")
    int id;
    @SerializedName("paymentStatus")
    Status status;
    @SerializedName("user")
    User user;
    @SerializedName("cost")
    Double cost;

    public ExpenseDetail(int id, Status status, User user, Double cost) {
        this.id = id;
        this.status = status;
        this.user = user;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}