package com.hackerzhenya.fileactions;

import com.hackerzhenya.fileactions.core.FileProcessorInfo;
import com.hackerzhenya.fileactions.core.Module;
import com.hackerzhenya.fileactions.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
public class FileActionsApplication {
    private final Logger logger;
    private final ApplicationArguments applicationArguments;
    private final Collection<Module> modules;

    @Autowired
    public FileActionsApplication(ApplicationArguments applicationArguments, Collection<Module> modules) {
        this.modules = modules;
        this.applicationArguments = applicationArguments;
        this.logger = LoggerFactory.getLogger(FileActionsApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(FileActionsApplication.class, args);
    }

    private File resolveFile() {
        var args = applicationArguments.getSourceArgs();

        if (args.length != 1) {
            throw new IllegalArgumentException("Expected path to file as first argument");
        }

        var path = Paths.get(args[0]);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found");
        }

        return path.toFile();
    }

    @PostConstruct
    public void run() {
        var file = resolveFile();

        var availableModules = modules.stream()
                                      .filter(x -> x.canProcessFile(file))
                                      .collect(Collectors.toList());

        logger.info("Got file: {}", file.getAbsolutePath());

        logger.info("Loaded modules: {}", modules.stream()
                                                 .map(module -> module.getClass().getSimpleName())
                                                 .collect(Collectors.joining(", ")));

        logger.info("Available modules: {}", availableModules.stream()
                                                             .map(module -> module.getClass().getSimpleName())
                                                             .collect(Collectors.joining(", ")));

        var loadedProcessors = Utils.getModulesFileProcessors(modules);
        var processors = loadedProcessors
                .stream()
                .filter(proc -> availableModules.stream()
                                                .map(module -> module.getClass().getSimpleName())
                                                .anyMatch(name -> proc.getModuleName().equals(name)))
                .collect(Collectors.toList());

        if (processors.stream()
                      .map(FileProcessorInfo::getCommand)
                      .distinct()
                      .count() != processors.size()) {
            throw new IllegalStateException("Multiple commands with same name");
        }

        var stdin = new Scanner(System.in);
        while (true) {
            String input = stdin.nextLine()
                                .trim();

            if (input.length() == 0) {
                continue;
            }

            if (input.equals("exit")) {
                return;
            }

            if (input.equals("?")) {
                showHelp(processors);
                continue;
            }

            if (input.equals("??")) {
                showHelp(loadedProcessors);
                continue;
            }

            var processor = processors.stream()
                                      .filter(x -> x.getCommand().equals(input))
                                      .findFirst()
                                      .orElse(null);

            if (processor == null) {
                System.err.println("Unknown command\n");
                showHelp(processors);
                continue;
            }

            try {
                processor.invoke(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showHelp(Collection<FileProcessorInfo> processors) {
        System.out.println("Available commands:");

        for (FileProcessorInfo processor : processors) {
            System.out.printf("\t%s\n", processor.toString());
        }

        System.out.println();
    }
}
