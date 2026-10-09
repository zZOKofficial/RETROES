package app;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {
    private static final String APP_DIR_NAME = "RETROES";
    private static Path installDir;
    private static Path dataDir;

    private AppPaths() {
    }

    public static synchronized Path installDir() {
        if (installDir == null) {
            installDir = resolveInstallDir();
        }
        return installDir;
    }

    public static synchronized Path dataDir() {
        if (dataDir == null) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isEmpty()) {
                dataDir = Paths.get(appData, APP_DIR_NAME);
            } else {
                dataDir = installDir().resolve("data-local");
            }
            try {
                java.nio.file.Files.createDirectories(dataDir);
            } catch (Exception e) {
                System.err.println("Could not create data dir: " + dataDir);
            }
        }
        return dataDir;
    }

    public static Path asset(String relative) {
        return installDir().resolve(relative.replace('/', File.separatorChar));
    }

    public static Path userFile(String name) {
        return dataDir().resolve(name);
    }

    public static String assetString(String relative) {
        return asset(relative).toString();
    }

    private static Path resolveInstallDir() {
        try {
            Path codeSource = Paths.get(AppPaths.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path candidate = codeSource.getParent();
            if (candidate != null && java.nio.file.Files.isDirectory(candidate)) {
                return candidate;
            }
        } catch (URISyntaxException | SecurityException | NullPointerException ignored) {
        }
        return Paths.get(System.getProperty("user.dir", "."));
    }
}
