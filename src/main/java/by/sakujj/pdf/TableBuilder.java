package by.sakujj.pdf;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class TableBuilder {
    private List<Entry<String, String>> table = new ArrayList<>();
    boolean isBuilt = false;

    public <K, V> TableBuilder addRow(K key, V value) {
        if (isBuilt) {
            throw new IllegalStateException("Table has already been built");
        }
        table.add(Map.entry(key.toString(), value.toString()));
        return this;
    }

    public <K, V> TableBuilder addRowAt(int rowNumber, K key, V value) {
        if (isBuilt) {
            throw new IllegalStateException("Table has already been built");
        }
        table.add(rowNumber, Map.entry(key.toString(), value.toString()));
        return this;
    }

    public List<Entry<String, String>> build() {
        isBuilt = true;
        return table;
    }

    public static List<Entry<String, String>> fromClientResponse(ClientResponse clientResponse) {
        return new TableBuilder()
                .addRow("ID", clientResponse.getId())
                .addRow("Имя клиента", clientResponse.getUsername())
                .addRow("Email",clientResponse.getEmail())
                .addRow("Возраст", clientResponse.getAge())
                .build();
    }

    public static List<Entry<String, String>> fromClientRequest(ClientRequest clientRequest) {
        return new TableBuilder()
                .addRow("Имя клиента", clientRequest.getUsername())
                .addRow("Email",clientRequest.getEmail())
                .addRow("Возраст", clientRequest.getAge())
                .addRow("Пароль", clientRequest.getNotHashedPassword())
                .build();
    }
}
