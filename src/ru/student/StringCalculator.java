package ru.student;

import java.util.Scanner;

// основной класс
public class StringCalculator {
    private String[] string; // массив где хранятся 3 объекта: [0] - первая строка [1] - знак действия [2] - вторая строка или цифра
    private String output;   // результат

    // метод для запуска калькультора
    public void start() {
        InputReader reader = new InputReader();
        reader.read();

        calculation(string);

        OutputWriter writer = new OutputWriter();
        writer.write(output);
    }

    // метод для преобразования строк
    private void calculation(String[] string) {
        output = "";

        // выбор знака действия
        switch(string[1]) {
            case "+":
                output = string[0] + string[2];
                break;

            case "-":
                Scanner scanner = new Scanner(string[0]).useDelimiter(string[2]); // поиск в первой строке части похожей на вторую
                while(scanner.hasNext()) { output += scanner.next(); }            // пропуск необходимой части в первой строке
                scanner.close();
                break;

            case "*":
                int num = Integer.parseInt(string[2]); // парсим целое число
                for(int i = 0; i < num; i++) {         // складываем первую строку необходимое количество раз
                    output += string[0];
                }
                break;

            case "/":
                int numDivide = Integer.parseInt(string[2]); // парсим целое число
                int length = string[0].length();
                int outputLength = length / numDivide; // исходную длину делим на целое число

                for(int i = 0; i < (outputLength); i++) { // складываем n букв с начала
                    output += string[0].charAt(i);
                }
                break;
        }
    }

    // класс, который умеет читать и приводить к нужному виду входные данные из консоли
    private class InputReader {

        // метод для чтения и поиска ошибки во входных данных
        private void read() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите аргументы в формате: \n \"СТРОКА\" +- \"СТРОКА\" или \"СТРОКА\" */ ЧИСЛО");
            String input = scanner.nextLine();
            scanner.close();

            String[] splittedString = splitInput(input, "\"\\s"); // используем метод splitInput(String input, String pattern)
                                                                         // паттерн - символы между первой строкой и знаком
            try {
                if(!hasCorrectLength(splittedString[0], splittedString[2])) { // проверка длины строк
                    throw new MyException("Длина строки превышает 10 символов");
                }
            } catch (MyException e) {
                e.printStackTrace();
                System.exit(0);
            }

            //
            splittedString[0] = cutQuotes(splittedString[0]); // проверяем наличие кавычек у строк и убираем их
            if(splittedString[1].equals("+") || splittedString[1].equals("-")) {
                splittedString[2] = cutQuotes(splittedString[2]); // аналогично со второй строкой, если операция сложения или вычитания
            } else if(splittedString[1].equals("*") || splittedString[1].equals("/")) {

                try {
                    if(hasQuotes(splittedString[2])) { // проверяем, является ли второй аргумент строкой при умножении или  делении
                        throw new MyException("Нельзя делить и умножать на строку");
                    } else {

                        try {
                            Integer.parseInt(splittedString[2]); // проверяем, является ли целым числом второй аргумент
                        } catch (NumberFormatException e) {
                            throw new MyException("Неправильный формат входных данных");
                        }

                    }
                    if(Integer.parseInt(splittedString[2]) < 1         // проверяем диапазон числа
                            || Integer.parseInt(splittedString[2]) > 10) {
                        throw new MyException("Число должно быть не меньше 1 и не больше 10");
                    }
                } catch (MyException e) {
                    e.printStackTrace();
                    System.exit(0);
                }

            } else { // если знак не соответсвует +-*/, то выбрасываем исключение

                try {
                    throw new MyException("Выберите один из арифметических знаков: +-*/");
                } catch (MyException e) {
                    e.printStackTrace();
                    System.exit(0);
                }

            }

            string = splittedString;
        }

        // метод для "разбивания" входных данных на три объекта
        private String[] splitInput(String input, String pattern) {
            Scanner scanner;
            scanner = new Scanner(input);
            try { // проверяем, подходят ли данные под формат
                if(scanner.useDelimiter(pattern).next().equals(input)) { throw new MyException( // если сканнер не нашел паттерна, то бросаем исключение
                        "Неправильный формат входных данных"
                ); }
            } catch (MyException e) {
                e.printStackTrace();
                System.exit(0);
            }
            scanner.close(); // обязательно закрыть сканнер

            scanner = new Scanner(input);
            int signIndex = scanner.useDelimiter(pattern).next().length() + 2; // "двуангуляция" индекса знака XD
            scanner.close();        // после .useDelimiter(pattern).next() мы получим первую строку без последней кавычки и пробела, находим длину и прибавляем 2 = индекс знака

            StringBuilder builder = new StringBuilder(input);
            builder.setCharAt(signIndex - 1, '|'); // заменяем символы перед и после знака на |
            builder.setCharAt(signIndex + 1, '|'); // чтобы эффективно использовать метод split(String regex)
            String[] splittedString = builder.toString().split("\\|"); // если бы просто делили по пробелу, то строки не могли бы иметь больше одного слова
            return splittedString;                          // так как это ломало бы весь процесс
        }

        // метод для проверки - есть ли у строки кавычки
        private boolean hasQuotes(String s) {
            return s.charAt(0) == '"'
                    && s.charAt(s.length() - 1) == '"';
        }

        // убираем кавычки
        private String cutQuotes(String string) {

            try {
                if(!hasQuotes(string)) {
                    throw new MyException("Строка должна быть в кавычках");
                }
            } catch (MyException e) {
                e.printStackTrace();
                System.exit(0);
            }

            StringBuilder builder = new StringBuilder(string);
            string = builder.deleteCharAt(string.length() - 1).deleteCharAt(0).toString(); // удаляем первый и последний символ

            return string;
        }

        // проверяем длину строк
        private boolean hasCorrectLength(String first, String second) {
            return first.length() <= 12 && second.length() <= 12; // 12, потому что берем в рассчет кавычки
        }

    }

    // класс для выдачи результата
    private class OutputWriter {

        // выводим в консоль результат
        private void write(String output) {
            if(output.length() > 40) {
                output = output.substring(0, 39) + "..."; // если результат больше 40 символов, то режем с помощью .substring(int startIndex, int lastIndex)
            }
            System.out.println("\"" + output + "\"");
        }
    }

}
