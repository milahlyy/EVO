package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import model.CashPayment;
import model.EWalletPayment;
import model.Payment;
import model.TransferPayment;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentStorage extends JsonStorage {
    private static final String PAYMENT_FILE = "data/payments.json";

    private final Gson paymentGson;

    public PaymentStorage() {
        super();

        JsonDeserializer<Payment> deserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("receivedBy")) {
                return context.deserialize(json, CashPayment.class);
            } else if (jsonObject.has("bankName")) {
                return context.deserialize(json, TransferPayment.class);
            } else if (jsonObject.has("walletProvider")) {
                return context.deserialize(json, EWalletPayment.class);
            }

            throw new JsonParseException("Tipe Payment tidak dikenali di dalam file JSON");
        };

        this.paymentGson = new GsonBuilder()
                .registerTypeAdapter(Payment.class, deserializer)
                .setPrettyPrinting()
                .create();
    }

    public List<Payment> loadPayments() {
        ensureFileExists(PAYMENT_FILE);

        try (FileReader reader = new FileReader(PAYMENT_FILE)) {
            Payment[] payments = paymentGson.fromJson(reader, Payment[].class);

            if (payments == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(Arrays.asList(payments));
        } catch (IOException e) {
            System.out.println("Gagal membaca file JSON Payment: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void savePayments(List<Payment> payments) {
        ensureFileExists(PAYMENT_FILE);

        try (FileWriter writer = new FileWriter(PAYMENT_FILE)) {
            paymentGson.toJson(payments, writer);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file JSON Payment: " + e.getMessage());
        }
    }
}