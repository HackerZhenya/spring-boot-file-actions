package com.hackerzhenya.fileactions.core;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Utils {
    private static final DecimalFormat df = new DecimalFormat("0.##");
    private static final double sizeKb = 1 << 10;
    private static final double sizeMb = sizeKb * sizeKb;
    private static final double sizeGb = sizeMb * sizeKb;
    private static final double sizeTb = sizeGb * sizeKb;

    public static String formatFileSize(Long size) {
        if (size < sizeKb)
            return df.format(size) + " B";

        if (size < sizeMb)
            return df.format(size / sizeKb) + " KB";

        if (size < sizeGb)
            return df.format(size / sizeMb) + " MB";

        if (size < sizeTb)
            return df.format(size / sizeGb) + " GB";

        return df.format(size / sizeTb) + " TB";
    }

    public static String getFileExtension(File file) {
        var name = file.getName();
        var lastIndexOf = name.lastIndexOf(".");

        return lastIndexOf >= 0
                ? name.substring(Math.min(lastIndexOf + 1, name.length()))
                : "";
    }

    public static Collection<FileProcessorInfo> getModulesFileProcessors(Collection<Module> modules) {
        return modules.stream()
                      .flatMap(module -> Utils.getModuleFileProcessors(module).stream())
                      .collect(Collectors.toList());
    }

    public static Collection<FileProcessorInfo> getModuleFileProcessors(Module module) {
        return Arrays.stream(module.getClass().getDeclaredMethods())
                     .filter(method -> method.isAnnotationPresent(FileProcessor.class))
                     .map(method -> new FileProcessorInfo(module, method))
                     .collect(Collectors.toList());
    }
}
