package com.hackerzhenya.fileactions.modules;

import com.hackerzhenya.fileactions.core.FileProcessor;
import com.hackerzhenya.fileactions.core.Module;
import com.hackerzhenya.fileactions.core.Utils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Component
public class DirectoryModule implements Module {
    @Override
    public boolean canProcessFile(File file) {
        return file.isDirectory();
    }

    private String getIconByType(File file) {
        return getIconByType(file, " ", "+");
    }

    private String getCanonicalIconByType(File file) {
        return getIconByType(file, "-", "d");
    }

    private String getIconByType(File file, String fileIcon, String dirIcon) {
        if (file.isFile()) {
            return fileIcon;
        }

        if (file.isDirectory()) {
            return dirIcon;
        }

        return "?";
    }

    private Stream<File> getEntry(File directory) throws IOException {
        return Files.list(directory.toPath())
                    .map(Path::toFile)
                    .sorted((a, b) -> a.isDirectory() && b.isDirectory() || a.isFile() && b.isFile()
                            ? a.compareTo(b)
                            : Boolean.compare(a.isFile(), b.isFile()));
    }

    @FileProcessor(description = "Вывод списка файлов в каталоге")
    void listFiles(File directory) throws IOException {
        getEntry(directory)
                .forEach(file -> System.out.printf("[ %s ] %s\n", getIconByType(file), file.getName()));

        System.out.println();
    }

    @FileProcessor(description = "Подсчет размера всех файлов в каталоге")
    void showFileSizes(File directory) throws IOException {
        var total = getEntry(directory)
                .map(file -> {
                    var size = file.length();
                    System.out.printf("[ %s ] %s — %s\n",
                            getIconByType(file),
                            file.getName(),
                            Utils.formatFileSize(size));

                    return size;
                })
                .reduce(0L, Long::sum);

        System.out.printf("\nTotal: %s\n\n", Utils.formatFileSize(total));
    }

    @FileProcessor(description = "Вывод прав доступа к файлам")
    void showFileAccess(File directory) throws IOException {
        getEntry(directory)
                .forEach(file -> System.out.printf("%s%s%s%s %s\n",
                        getCanonicalIconByType(file),
                        file.canRead() ? "r" : "-",
                        file.canWrite() ? "w" : "-",
                        file.canExecute() ? "x" : "-",
                        file.getName()));
    }
}
