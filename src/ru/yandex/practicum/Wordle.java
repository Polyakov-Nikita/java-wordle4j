package ru.yandex.practicum;

import ru.yandex.practicum.dictionary.WordleDictionary;
import ru.yandex.practicum.dictionary.exceptions.WordIsAbsentException;
import ru.yandex.practicum.game.WordleGame;
import ru.yandex.practicum.game.exceptions.IncorrectWordException;
import ru.yandex.practicum.game.exceptions.WordRepeatingException;
import ru.yandex.practicum.log.FileLogger;
import ru.yandex.practicum.log.Logger;

import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {
    private static final String LOG_NAME = "wordle-log";
    private static final String WORDS_FILENAME = "words_ru.txt";

    private static WordleGame game;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            initialize();
            startGame();
            gameCycle();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    private static void initialize() {
        Logger logger = createLogger();
        WordleDictionaryLoader loader = createLoader(logger);
        WordleDictionary wordleDictionary = loader.load();
        game = new WordleGame(wordleDictionary);
        game.addLogger(logger);
        scanner = new Scanner(System.in);
    }

    private static Logger createLogger() {
        FileLogger fileLogger = new FileLogger(LOG_NAME);
        fileLogger.create();
        return fileLogger;
    }

    private static WordleDictionaryLoader createLoader(Logger fileLogger) {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(WORDS_FILENAME);
        loader.addLogger(fileLogger);
        return loader;
    }

    private static void startGame() {
        System.out.println("Игра началась");
        game.start();
        printTurn(game.getSteps());
    }

    private static void printTurn(int turn) {
        System.out.printf("Ход %d%n", turn);
    }

    private static void gameCycle() {
        while (true) {
            if (game.isGameOver()) {
                System.out.println("Попытки закончились. Вы проиграли.");
                break;
            }
            String input = takeInput();
            if (input.isEmpty()) {
                input = showHint();
            }
            if (game.isAnswer(input)) {
                System.out.println("Победа!");
                break;
            }
            analyseInput(input);
        }
    }

    private static String takeInput() {
        System.out.print("Введите слово из пяти букв(или Enter, если хотите подсказку): ");
        return scanner.nextLine();
    }

    private static String showHint() {
        String hint = game.giveHint();
        System.out.printf("Подсказка: %s%n", hint);
        return hint;
    }

    private static void analyseInput(String input) {
        String analysis;
        try {
            analysis = game.analyseWord(input);
            System.out.println("Ответ: " + analysis);
            printTurn(game.getSteps());
        } catch (IncorrectWordException e) {
            System.out.println("Некорректный ввод.");
        } catch (WordIsAbsentException e) {
            System.out.println("Такого слова я не знаю.");
        } catch (WordRepeatingException e) {
            System.out.println("Такое слово уже было!");
        }
    }
}
