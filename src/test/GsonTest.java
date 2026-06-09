package test;

import com.google.gson.Gson;

public class GsonTest {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = gson.toJson("Hello EVO");

        System.out.println(json);
    }
}
