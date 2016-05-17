package com.example.merna.mallowner.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Merna on 4/28/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Shop implements Serializable {

    private String shopName;
    private String shopEmail;
    private String password;
    private String phone;
    private String fbContact;
    private String twitterContact;
    private String logo;
    private String Type;
    private String status;


    public Shop() {
    }

    public Shop(String shopEmail) {
        this.shopEmail = shopEmail;
        Type="Shop";
    }

    public Shop(String shopName, String shopEmail,String password, String Logo, String phone, String fbContact, String twitterContact) {
        this.shopName = shopName;
        this.shopEmail = shopEmail;
        this.password=password;
        this.logo = Logo;
        this.phone = phone;
        this.fbContact = fbContact;
        this.twitterContact = twitterContact;
        this.status="false";
    }

    public String getShopEmail() {
        return shopEmail;
    }
    public String getPassword() {
        return password;
    }
    public String getShopName() {
        return shopName;
    }
    public String getType() {
        return Type;
    }
    public String getPhone() {
        return phone;
    }
    public String getFbContact() {
        return fbContact;
    }
    public String getLogo() {
        return logo;
    }
    public String getTwitterContact() {
        return twitterContact;
    }
    public String getStatus() {
        return status;
    }

}


