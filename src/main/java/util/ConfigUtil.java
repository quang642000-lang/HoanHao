package util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        String val = properties.getProperty(key);
        if (val != null && val.startsWith("${") && val.endsWith("}")) {
            String inner = val.substring(2, val.length() - 1);
            String[] parts = inner.split(":", 2);
            String envKey = parts[0];
            String defaultVal = parts.length > 1 ? parts[1] : null;
            String envVal = System.getenv(envKey);
            return (envVal != null && !envVal.isEmpty()) ? envVal : defaultVal;
        }
        return val;
    }

    public static String getUploadDir() {
        String dir = getProperty("upload.dir");
        return (dir == null || dir.isEmpty()) ? System.getProperty("user.home") + "/tea_pos_images" : dir;
    }
}