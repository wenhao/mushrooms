package com.github.wenhao.mushrooms.stub.dataloader;

import java.nio.charset.Charset;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceReader {

    private final static Logger logger = Logger.getLogger(ResourceReader.class.getName());

    public String readAsString(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(getClass().getResource("classpath:" + path).toURI())), Charset.forName("UTF8"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, path + " file not found.", e);
            throw new FileSystemNotFoundException(path + " file not found.");
        }
    }
}
