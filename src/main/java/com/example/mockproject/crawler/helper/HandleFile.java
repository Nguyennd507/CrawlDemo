package com.example.mockproject.crawler.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandleFile {
    private final static Logger LOGGER = LoggerFactory.getLogger(HandleFile.class);
    private static int result;

    public static Set<String> readFile(String pathFile) {
        Set<String> links = new HashSet<>();
        Path path = Paths.get(pathFile);
        LOGGER.info("Start read file saved previous Links" + path);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String str;
            while ((str = reader.readLine()) != null) {
                links.add(str);
            }
        } catch (IOException e) {
            LOGGER.error("ERROR reading File " + e.getMessage());
        }
        LOGGER.info("List previous Links" + links);
        return links;
    }

    public static int readFileTotal(String fileName) {
        Path path = Paths.get(fileName);
        try {
            List<String> list = Files.readAllLines(path, StandardCharsets.UTF_8);
            result = Integer.parseInt(list.get(0));

        } catch (IOException e) {
            LOGGER.error("ERROR reading File " + e.getMessage());
        }
        return result;
    }

    public static void writeFile(String pathFile, Set<String> links) {
        Path path = Paths.get(pathFile);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String link : links) {
                writer.append(link);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.error("ERROR write File " + e.getMessage());
        }
    }

    public static void writeFileTotal(String pathFile, int total) {
        Path path = Paths.get(pathFile);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.append(String.valueOf(total));
        } catch (IOException e) {
            LOGGER.error("ERROR write File " + e.getMessage());
        }

    }


}
