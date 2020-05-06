package com.example.itprojects.bean;

import java.util.Comparator;

public class SortByDate implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        Update artifact1=(Update) o1;
        Update artifact2=(Update) o2;

        return artifact1.getUpload().compareTo(artifact2.getUpload());
    }
}
