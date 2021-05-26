package com.hackerzhenya.fileactions.modules;

import com.hackerzhenya.fileactions.core.FileProcessor;
import com.hackerzhenya.fileactions.core.Module;
import com.hackerzhenya.fileactions.core.Utils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class TextModule implements Module {
    @Override
    public boolean canProcessFile(File file) {
        var path = file.toPath();

        return Files.isRegularFile(path) &&
                Files.isReadable(path) &&
                Utils.getFileExtension(file).equals("txt");
    }

    @FileProcessor(description = "Подсчет и вывод количества строк")
    void rowCount(File file) throws IOException {
        System.out.printf("Rows in file: %d\n\n", Files.lines(file.toPath()).count());
    }

    @FileProcessor(description = "Подсчет и вывод количества слов")
    void wordCount(File file) throws IOException {
        System.out.printf("Words in file: %d\n\n", Files.lines(file.toPath())
                                                      .flatMap(string -> Arrays.stream(string.split(" ")))
                                                      .count());
    }

    @FileProcessor(description = "Вывод частоты вхождения каждого символа")
    void symbolFrequency(File file) throws IOException {
        Map<Character, Long> frequencies = new HashMap<>();

        Files.lines(file.toPath())
             .flatMapToInt(String::chars)
             .forEach(i -> {
                 var ch = (char) i;

                 frequencies.put(ch, frequencies.containsKey(ch)
                         ? frequencies.get(ch) + 1
                         : 1L);
             });

        frequencies.entrySet()
                   .stream()
                   .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                   .forEach((entry) -> System.out.printf("\"%c\" — %d times\n", entry.getKey(), entry.getValue()));

        System.out.println();
    }
}
