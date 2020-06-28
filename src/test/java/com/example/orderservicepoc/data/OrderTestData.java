package com.example.orderservicepoc.data;

import com.example.orderservicepoc.model.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;

public class OrderTestData {

    public static final String ORDER_DATA_FILE_PATH = "src/test/resources/data/order.json";

    public static List<Order> mockOrders() throws Exception {
        try (FileReader fileReader = new FileReader(ORDER_DATA_FILE_PATH)) {
            return newArrayList(gson().fromJson(fileReader, Order[].class));
        }
    }

    private static Gson gson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }
}
