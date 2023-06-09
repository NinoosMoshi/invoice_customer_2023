package com.ninos.enumeration;

public enum verificationType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");

    private final String type;

    verificationType(String type) {
        this.type = type;
    }


    public String getType(){
        return this.type.toLowerCase();
    }

}
