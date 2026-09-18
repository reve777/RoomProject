package com.booking.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 自動讀取專案根目錄的 .env 檔案並注入至 Spring 環境變數中 (支援 UTF-8 編碼與 Gmail 密碼自動去空白)
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Enforce UTF-8 in JVM
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.stdout.encoding", "UTF-8");
        System.setProperty("sun.stderr.encoding", "UTF-8");

        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File(System.getProperty("user.dir"), ".env");
        }

        if (!envFile.exists() || !envFile.isFile()) {
            return;
        }

        Map<String, Object> envProperties = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int eqIdx = line.indexOf('=');
                if (eqIdx > 0) {
                    String key = line.substring(0, eqIdx).trim();
                    String value = line.substring(eqIdx + 1).trim();

                    // Strip surrounding quotes if present
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    // For MAIL_PASSWORD, if it is a 16-char Gmail app password with spaces, strip whitespace
                    if ("MAIL_PASSWORD".equalsIgnoreCase(key) && value.length() == 19 && value.chars().filter(c -> c == ' ').count() == 3) {
                        value = value.replace(" ", "");
                    }

                    envProperties.put(key, value);
                    // Also populate into System properties for early library loading
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, value);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("無法讀取 .env 檔案: " + e.getMessage());
        }

        if (!envProperties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envProperties));
        }
    }
}
