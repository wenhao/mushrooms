package com.github.wenhao.mushrooms.stub.dataloader;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceReader {

    private static final Logger LOGGER = Logger.getLogger(ResourceReader.class.getName());

    public String readAsString(String path) {
        try {
            Path filePath = Paths.get(getClass().getResource(path).toURI());
            Stream<String> lines = Files.lines(filePath);
            String content = lines.collect(Collectors.joining("\n"));
            lines.close();
            return content;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, path + " file not found.", e);
            throw new FileSystemNotFoundException(path + " file not found.");
        }
    }
}
