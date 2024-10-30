package backend.academy.log_analyzer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("checkstyle:UncommentedMain")
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
        } catch (IOException e) {
            log.error("Ошибка при анализе логов: {}", e.getMessage());
        }
    }

    private static Arguments parseArguments(String[] args) {
        String path = null;
        LocalDate from = null;
        LocalDate to = null;
        String outputFormat = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int i = 1;
        while (i < args.length) {
            String currentArg = args[i];
            switch (currentArg) {
                case "--path":
                    if (i + 1 < args.length) {
                        path = args[i + 1];
                        i += 2;  // Пропускаем следующий аргумент
                    } else {
                        log.error("Ошибка: не указан путь к лог-файлам.");
                        System.exit(1);
                    }
                    break;
                case "--from":
                    if (i + 1 < args.length) {
                        from = LocalDate.parse(args[i + 1], dateFormatter);
                        i += 2;  // Пропускаем следующий аргумент
                    } else {
                        log.error("Ошибка: не указана начальная дата.");
                        System.exit(1);
                    }
                    break;
                case "--to":
                    if (i + 1 < args.length) {
                        to = LocalDate.parse(args[i + 1], dateFormatter);
                        i += 2;  // Пропускаем следующий аргумент
                    } else {
                        log.error("Ошибка: не указана конечная дата.");
                        System.exit(1);
                    }
                    break;
                case "--format":
                    if (i + 1 < args.length) {
                        outputFormat = args[i + 1];
                        i += 2;  // Пропускаем следующий аргумент
                    } else {
                        log.error("Ошибка: не указан формат вывода.");
                        System.exit(1);
                    }
                    break;
                default:
                    log.error("Ошибка: неизвестный аргумент {}", currentArg);
                    System.exit(1);
            }
        }

        if (path == null) {
            log.error("Ошибка: путь к лог-файлам обязателен.");
            return null;
        }

        return new Arguments(path, from, to, outputFormat);
    }


    private static void processLog(Arguments arguments) throws IOException {
        LogAnalyzer logAnalyzer = new LogAnalyzer(arguments.path());

        if (arguments.from() != null || arguments.to() != null) {
            logAnalyzer.filterByDateRange(
                arguments.from() != null ? arguments.from().atStartOfDay() : null,
                arguments.to() != null ? arguments.to().atStartOfDay() : null
            );
        }

        LogReport logReport = new LogReport(logAnalyzer, arguments.outputFormat());
        String report = logReport.generateReport(
            new String[] {arguments.path()},
            arguments.from() != null ? arguments.from().atStartOfDay() : null,
            arguments.to() != null ? arguments.to().atStartOfDay() : null
        );

        log.info("\n {}", report);
    }
}
