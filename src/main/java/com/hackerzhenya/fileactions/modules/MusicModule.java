package com.hackerzhenya.fileactions.modules;

import com.hackerzhenya.fileactions.core.FileProcessor;
import com.hackerzhenya.fileactions.core.Module;
import com.hackerzhenya.fileactions.core.Utils;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MusicModule implements Module {
    @Override
    public boolean canProcessFile(File file) {
        var path = file.toPath();

        return Files.isRegularFile(path) &&
                Files.isReadable(path) &&
                Utils.getFileExtension(file).equals("mp3");
    }

    @FileProcessor(description = "Вывод длительности в секундах")
    void trackDuration(File file) throws IOException, UnsupportedAudioFileException {
        System.out.printf("Duration of the song: %d seconds\n\n", ((long) AudioSystem.getAudioFileFormat(file)
                                                                                     .properties()
                                                                                     .get("duration") / (1_000_000)));
    }

    @FileProcessor(description = "Вывод названия трека")
    void trackName(File file) throws IOException, UnsupportedAudioFileException {
        System.out.printf("Track name: %s\n\n", AudioSystem.getAudioFileFormat(file)
                                                           .properties()
                                                           .get("title"));
    }

    @FileProcessor(description = "Вывод полной информации о треке")
    void trackInfo(File file) throws IOException, UnsupportedAudioFileException {
        System.out.printf("Track info:\n\t%s\n\n",
                AudioSystem.getAudioFileFormat(file)
                           .properties()
                           .entrySet()
                           .stream()
                           .sorted(Map.Entry.comparingByKey())
                           .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                           .collect(Collectors.joining("\n\t")));
    }
}
