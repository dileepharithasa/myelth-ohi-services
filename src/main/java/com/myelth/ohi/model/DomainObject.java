package com.myelth.ohi.model;

public class DomainObject<T> {
    private T object;
    // You can add more properties or methods if needed

    public DomainObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }
    
    public void setObject(T object) {
        this.object = object;
    }
}
