package com.example.MyBookShopApp.data;

public enum TagCategoryEnum {
    XS (0.25), SM(0.5), MD(0.75), LG(1.0);

    private Double percent;

    TagCategoryEnum(Double percent) {
        this.percent = percent;
    }

    public Double getPercent() {
        return percent;
    }
}