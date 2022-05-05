package com.edmi.controller;

public class BaseController {
    protected final ViewFactory viewFactory;
    private final String fxmlPath;

    public BaseController(ViewFactory viewFactory, String fxmlPath){
        this.viewFactory = viewFactory;
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath(){
        return fxmlPath;
    }
}