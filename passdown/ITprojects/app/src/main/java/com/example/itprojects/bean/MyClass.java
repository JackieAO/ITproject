package com.example.itprojects.bean;

import com.google.firebase.database.Exclude;

public class MyClass {

    public enum Kind {W,V,U};

    private Double value;
    private Kind kind;

    /* Dummy Constructor */
    public MyClass() {}

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    // The Firebase data mapper will ignore this
    @Exclude
    public Kind getKindVal() {
        return kind;
    }

    public String getKind() {
        // Convert enum to string
        if (kind == null) {
            return null;
        } else {
            return kind.name();
        }
    }

    public void setKind(String kindString) {
        // Get enum from string
        if (kindString == null) {
            this.kind = null;
        } else {
            this.kind = Kind.valueOf(kindString);
        }
    }
}

