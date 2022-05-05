package com.edmi.controller.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.util.List;

public class TestClass {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = """
                {
                  "widgets" : [
                    {
                      "id" : "1",
                      "name" : "swtch1",
                      "description" : "descripcion",
                      "tite" : "TITLE",
                      "type" : "SWITCH",
                      "port" : 15200,
                      "ip" : "localhost",
                      "value" : 0.0,
                      "maxValue" : 100.0,
                      "minValue" : 0.0,
                      "positionX" : 0,
                      "positionY" : 1
                    },
                    {
                      "id" : "1",
                      "name" : "swtch1",
                      "description" : "descripcion",
                      "tite" : "TITLE",
                      "type" : "SWITCH",
                      "port" : 15200,
                      "ip" : "localhost",
                      "value" : 0.0,
                      "maxValue" : 100.0,
                      "minValue" : 0.0,
                      "positionX" : 0,
                      "positionY" : 4
                    }
                  ]
                }
                """;
        JsonConverter widgets = gson.fromJson(json, JsonConverter.class);
        System.out.println(widgets.getWidgets().get(1).getPositionY());

    }
}