package com.github.tacowasa059.snakeplayer.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class Config {
    private static final Properties PROPERTIES = new Properties();
    private static final List<Value<?>> VALUES = new ArrayList<>();
    private static Path configPath;
    private static boolean initialized;

    public static final DoubleValue CX = new DoubleValue("cx", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static final DoubleValue CZ = new DoubleValue("cz", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE);
    public static final DoubleValue L = new DoubleValue("L", 50.0, 0.1, 10000.0);
    public static final DoubleValue R = new DoubleValue("r", 5.0, 0.1, 100.0);
    public static final IntValue expValue = new IntValue("expValue", 10, 0, 10000);
    public static final BooleanValue enableSpread = new BooleanValue("enable_spread", false);
    public static final BooleanValue DEFAULT_IS_SNAKE = new BooleanValue("default_is_snake", true);
    public static final DoubleValue DEFAULT_HEAD_SIZE = new DoubleValue("default_head_size", 1.0, 0.001, 100.0);
    public static final DoubleValue DEFAULT_BODY_SEGMENT_SIZE = new DoubleValue("default_body_segment_size", 1.0, 0.001, 100.0);
    public static final DoubleValue DEFAULT_DAMAGE = new DoubleValue("default_damage", 1000.0, 0.0, Float.MAX_VALUE);
    public static final DoubleValue DEFAULT_SPEED = new DoubleValue("default_speed", 0.3, 0.0, 1.5);
    public static final IntValue SPAWN_BLOCK_VIEW_DISTANCE = new IntValue("spawn_block_view_distance", 8, 0, 128);
    public static final IntValue SPAWN_BLOCK_VIEW_HALF_WIDTH = new IntValue("spawn_block_view_half_width", 2, 0, 64);

    private Config() {
    }

    public static synchronized void init(Path path) {
        if (initialized) {
            return;
        }
        configPath = path;
        load();
        initialized = true;
    }

    public static synchronized void reload() {
        if (configPath == null) {
            return;
        }
        PROPERTIES.clear();
        load();
    }

    private static void load() {
        try {
            Files.createDirectories(configPath.getParent());

            boolean changed = false;
            if (Files.exists(configPath)) {
                loadToml(configPath);
            } else {
                Path legacyPath = getLegacyPropertiesPath(configPath);
                if (Files.exists(legacyPath)) {
                    loadProperties(legacyPath);
                    changed = true;
                }
            }

            for (Value<?> value : VALUES) {
                changed |= value.ensurePresent();
            }
            if (changed || !Files.exists(configPath)) {
                save();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load config: " + configPath, exception);
        }
    }

    private static Path getLegacyPropertiesPath(Path tomlPath) {
        String fileName = tomlPath.getFileName().toString();
        if (fileName.endsWith(".toml")) {
            fileName = fileName.substring(0, fileName.length() - 5) + ".properties";
        } else {
            fileName = fileName + ".properties";
        }
        return tomlPath.resolveSibling(fileName);
    }

    private static void loadProperties(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            PROPERTIES.load(input);
        }
    }

    private static void loadToml(Path path) throws IOException {
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            int separator = trimmed.indexOf('=');
            if (separator < 0) {
                continue;
            }

            String key = trimmed.substring(0, separator).trim();
            String value = trimmed.substring(separator + 1).trim();
            int commentIndex = value.indexOf(" #");
            if (commentIndex >= 0) {
                value = value.substring(0, commentIndex).trim();
            }
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                value = value.substring(1, value.length() - 1);
            }
            PROPERTIES.setProperty(key, value);
        }
    }

    private static synchronized void save() {
        if (configPath == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("# SnakePlayer config\n\n");
        Set<String> writtenKeys = new HashSet<>();
        for (Value<?> value : VALUES) {
            value.appendToml(builder);
            writtenKeys.add(value.key);
        }
        for (String key : PROPERTIES.stringPropertyNames()) {
            if (writtenKeys.contains(key)) {
                continue;
            }
            builder.append(key).append(" = ").append(PROPERTIES.getProperty(key)).append('\n');
        }

        try {
            Files.writeString(configPath, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to save config: " + configPath, exception);
        }
    }

    public abstract static class Value<T> {
        private final String key;
        private final T defaultValue;

        protected Value(String key, T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
            VALUES.add(this);
        }

        protected String getRaw() {
            return PROPERTIES.getProperty(key, String.valueOf(defaultValue));
        }

        protected void setRaw(String value) {
            PROPERTIES.setProperty(key, value);
            save();
        }

        private boolean ensurePresent() {
            if (PROPERTIES.containsKey(key)) {
                return false;
            }
            PROPERTIES.setProperty(key, String.valueOf(defaultValue));
            return true;
        }

        private void appendToml(StringBuilder builder) {
            builder.append(key).append(" = ").append(getRaw()).append('\n');
        }
    }

    public static final class BooleanValue extends Value<Boolean> {
        public BooleanValue(String key, boolean defaultValue) {
            super(key, defaultValue);
        }

        public boolean get() {
            return Boolean.parseBoolean(getRaw());
        }

        public void set(boolean value) {
            setRaw(Boolean.toString(value));
        }
    }

    public static final class IntValue extends Value<Integer> {
        private final int minValue;
        private final int maxValue;

        public IntValue(String key, int defaultValue, int minValue, int maxValue) {
            super(key, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public int get() {
            int parsed = Integer.parseInt(getRaw());
            return Math.max(minValue, Math.min(maxValue, parsed));
        }

        public void set(int value) {
            setRaw(Integer.toString(Math.max(minValue, Math.min(maxValue, value))));
        }
    }

    public static final class DoubleValue extends Value<Double> {
        private final double minValue;
        private final double maxValue;

        public DoubleValue(String key, double defaultValue, double minValue, double maxValue) {
            super(key, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public double get() {
            double parsed = Double.parseDouble(getRaw());
            return Math.max(minValue, Math.min(maxValue, parsed));
        }

        public void set(double value) {
            setRaw(Double.toString(Math.max(minValue, Math.min(maxValue, value))));
        }
    }
}
