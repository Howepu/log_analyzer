package backend.academy.log_analyzer;

import java.io.BufferedReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogFileReader {

    public List<String> loadLogs(List<String> paths) throws Exception {
        for (String path : paths) {
            if (path.startsWith("https://") || path.startsWith("http://")) {
                // Чтение логов через HTTP
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path))
                    .GET()
                    .build();

                HttpResponse<Stream<String>> response = client.send(request, HttpResponse.BodyHandlers.ofLines());

                // Сбор строк в список
                return response.body().collect(Collectors.toList());
            } else {
                // Локальное чтение логов (осталось без изменений)
                Path filePath = Paths.get(path);
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                    return lines;
                }
            }
        }
        return paths;
    }
}
