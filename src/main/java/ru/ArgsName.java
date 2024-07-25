package ru;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {

    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        String value = values.get(key);
        if (value == null) {
            throw new IllegalArgumentException("This key: '" + key + "' is missing");
        }
        return value;
    }
    private void validate(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        for (String arg : args) {
            if (!arg.contains("=")) {

                throw new IllegalArgumentException("Error: This argument '" + arg + "' does not contain an equal sign");
            }
            if (arg.substring(1, arg.indexOf("=")).isEmpty()) {

                throw new IllegalArgumentException("Error: This argument '" + arg + "' does not contain a key");
            }
            if (!arg.startsWith("-")) {

                throw new IllegalArgumentException("Error: This argument '" + arg + "' does not start with a '-' character");
            }
            if (arg.endsWith("=") && arg.indexOf("=") == arg.length() - 1) {
                throw new IllegalArgumentException("Error: This argument '" + arg + "' does not contain a value");
            }
        }
    }

    private void parse(String[] args) {
        String[] keys;
        for (String arg : args) {
            keys = arg.split("=", 2);
            values.put(keys[0].substring(1), keys[1]);
        }
    }

    public static ArgsName of(String[] args) {
        ArgsName names = new ArgsName();
        names.validate(args);
        names.parse(args);
        return names;
    }

    public static void main(String[] args) {
        ArgsName jvm = ArgsName.of(new String[] {"-Xmx=512", "-encoding=UTF-8"});
        System.out.println(jvm.get("Xmx"));

        ArgsName zip = ArgsName.of(new String[] {"-out=project.zip", "-encoding=UTF-8"});
        System.out.println(zip.get("out"));
    }
}

