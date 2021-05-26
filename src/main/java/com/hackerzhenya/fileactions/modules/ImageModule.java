package com.hackerzhenya.fileactions.modules;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Tag;
import com.hackerzhenya.fileactions.core.FileProcessor;
import com.hackerzhenya.fileactions.core.Module;
import com.hackerzhenya.fileactions.core.Utils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ImageModule implements Module {
    @Override
    public boolean canProcessFile(File file) {
        var path = file.toPath();

        return Files.isRegularFile(path) &&
                Files.isReadable(path) &&
                List.of("jpg", "png")
                    .contains(Utils.getFileExtension(file));
    }

    @FileProcessor(description = "Вывод размера изображения")
    void imageResolution(File file) throws IOException {
        var image = ImageIO.read(file);
        System.out.printf("Image resolution: %dx%dpx\n\n", image.getWidth(), image.getHeight());
    }

    @FileProcessor(description = "Вывод информации EXIF")
    void extractExif(File file) throws IOException, ImageProcessingException {
        var metadata = ImageMetadataReader.readMetadata(file);

        System.out.printf("EXIF information:\n\t%s\n\n",
                StreamSupport.stream(metadata.getDirectories().spliterator(), false)
                             .flatMap(d -> d.getTags().stream())
                             .map(Tag::toString)
                             .collect(Collectors.joining("\n\t")));
    }

    @FileProcessor(description = "Вычислить ориентацию изображения")
    void calculateOrientation(File file) throws IOException {
        var image = ImageIO.read(file);
        int width = image.getWidth(), height = image.getHeight();

        if (width == height){
            System.out.println("Square");
        } else if (width > height) {
            System.out.println("Landscape");
        } else {
            System.out.println("Portrait");
        }
    }
}
