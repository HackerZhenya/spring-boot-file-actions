package com.hackerzhenya.fileactions.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class FileProcessorInfo {
    private final String command;
    private final String description;
    private final Module module;
    private final Method method;

    public FileProcessorInfo(Module module, Method method) {
        if (!method.isAnnotationPresent(FileProcessor.class)) {
            throw new IllegalArgumentException("Method must be annotated with @FileProcessor annotation");
        }

        if (!Arrays.equals(method.getParameterTypes(), new Class[]{File.class})
                || method.getReturnType() != void.class) {
            throw new IllegalArgumentException("Illegal method signature");
        }

        method.setAccessible(true);

        var annotation = method.getAnnotation(FileProcessor.class);

        this.module = module;
        this.method = method;
        this.command = method.getName();
        this.description = annotation.description();
    }

    public String getModuleName() {
        return module.getClass().getSimpleName();
    }

    public String getCommand() {
        return command;
    }

    public void invoke(File file) throws InvocationTargetException, IllegalAccessException {
        method.invoke(module, file);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] — %s", command, module.getClass().getSimpleName(), description);
    }
}
