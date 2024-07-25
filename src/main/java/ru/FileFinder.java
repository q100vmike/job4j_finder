package ru;

import java.io.*;
import java.nio.charset.Charset;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FileFinder {

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            throw new IllegalArgumentException("Program needs 4 arguments!");
        }
        List<String> files = new ArrayList<>();
        ArgsName argsName = ArgsName.of(args);
        checkArguments(argsName);
        Path start = Paths.get(argsName.get("d"));
        Predicate<Path> filter = getFilter(argsName.get("t"), argsName.get("n"));

        search(start, filter).forEach(f -> files.add(String.valueOf(f)));
        String out = argsName.get("o");
        if ("stdout".equals(out)) {
            files.forEach(file -> System.out.println(file));
        } else {
            writeDataInFile(out, files);
        }
    }

    public static Predicate<Path> getFilter(String type, String pattern) {
        if ("regex".equals(type)) {
            String finalPattern = pattern;
            return path -> path.toFile().getName().matches(finalPattern);
        } else if ("mask".equals(type)) {
            String finalPattern1 = pattern.replace(".", "\\.")
                    .replace("*", ".+")
                    .replace("?",".{1}") + "$";
            return path -> path.toFile().getName().matches(finalPattern1);
        } else {
            String finalPattern2 = pattern;
            return path -> finalPattern2.equals(path.toFile().getName());
        }
    }

    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    public static void writeDataInFile(String path, List<String> data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path, StandardCharsets.UTF_8, true))) {
            data.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkArguments(ArgsName argsName) {
        File file = new File(argsName.get("d"));
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("-d argument are wrong!");
        }
        String arg = argsName.get("t");
        if (!"mask".equals(arg) && !"name".equals(arg) && !"regex".equals(arg)) {
            throw new IllegalArgumentException("-t' argument are wrong!");
        }
        if ("regex".equals(arg)) {
            Pattern.compile(argsName.get("n"));
        }
    }
}
