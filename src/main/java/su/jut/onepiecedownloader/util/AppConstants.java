package su.jut.onepiecedownloader.util;

import lombok.experimental.UtilityClass;

import java.nio.file.Paths;

@UtilityClass
public final class AppConstants {

    public static final String BASE_EPISODE_URL = "https://jut.su/oneepiece/episode-%d.html";
    public static final String REFERER = "https://jut.su/";
    public static final String OUTPUT_FILE_PATTERN = "onepiece_episode%d_%sp.%%(ext)s";

    public static String getDownloadPathFor(String outputFileName) {
        return Paths.get(System.getProperty("user.home"), "Downloads", outputFileName).toString();
    }
}
