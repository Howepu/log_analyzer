package backend.academy.log_analyzer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAnalyzerApp {

    private LogAnalyzerApp() {
        throw new AssertionError("Не удается создать экземпляр служебного класса");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("Ошибка: не указаны аргументы.");
            System.exit(1);
        }

        Arguments arguments = parseArguments(args);
        if (arguments == null) {
            System.exit(1);
        }

        try {
            processLog(arguments);
        } catch (Exception e) {
            log.error("Ошибка при анализе логов: {}", e.getMessage());
        }
    }

    private static Arguments parseArguments(String[] args) {
        List<String> paths = new ArrayList<>();
        LocalDate from = null;
        LocalDate to = null;
        String outputFormat = null;
        String filterField = null;
        String filterValue = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int i = 1;
        while (i < args.length) {
            String currentArg = args[i];
            switch (currentArg) {
                case "--path":
                    paths.add(args[++i]);
                    break;
                case "--from":
                    from = LocalDate.parse(args[++i], dateFormatter);
                    break;
                case "--to":
                    to = LocalDate.parse(args[++i], dateFormatter);
                    break;
                case "--format":
                    outputFormat = args[++i];
                    break;
                case "--filter-field":
                    filterField = args[++i];
                    break;
                case "--filter-value":
                    filterValue = args[++i];
                    break;
                default:
                    log.error("Ошибка: неизвестный аргумент {}", currentArg);
                    System.exit(1);
            }
            i++;
        }

        if (paths.isEmpty()) {
            log.error("Ошибка: путь к лог-файлам обязателен.");
            return null;
        }

        return new Arguments(paths, from, to, outputFormat, filterField, filterValue);
    }

    private static void processLog(Arguments arguments) throws Exception {
        // Создаём LogAnalyzer с данными всех файлов
        LogAnalyzer logAnalyzer = new LogAnalyzer(arguments.paths());

        // Применение фильтров
        if (arguments.from() != null || arguments.to() != null) {
            logAnalyzer.filterByDateRange(
                arguments.from() != null ? arguments.from().atStartOfDay() : null,
                arguments.to() != null ? arguments.to().atStartOfDay() : null
            );
        }

        if (arguments.filterField() != null && arguments.filterValue() != null) {
            logAnalyzer.filterByField(arguments.filterField(), arguments.filterValue());
        }

        // Генерация отчёта по всем данным
        LogReport logReport = new LogReport(logAnalyzer, arguments.outputFormat());
        String report = logReport.generateReport(
            arguments.paths().toArray(new String[0]),
            arguments.from() != null ? arguments.from().atStartOfDay() : null,
            arguments.to() != null ? arguments.to().atStartOfDay() : null
        );

        log.info("\n{}", report);
    }
}
