package org.v8LogScanner.commonly;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

final public class Constants {

    public static final String libDir = System.getProperty("user.dir") + "\\lib\\";

    public static final String osType = System.getProperty("os.name");

    // Text charset using whiting 1C techn log
    public final static Charset logsCharset = StandardCharsets.UTF_8;

    // using to limit event portion for one iteration pass in RgxReader
    public final static int logEventsCount = 10000;

    /**
     *  if true system logs will be collected in the program catalog
     */
    public static boolean ALLOW_LOGGING = false;

    public static List<String> V8_Dirs() {
        List<String> dirs = new ArrayList<>();
        if (osType.matches("(?i).*windows.*")) {
            dirs.add("C:\\Program Files (x86)\\1cv8");
            dirs.add("C:\\Program Files\\1cv8");
        } else if (osType.matches("(?i).*linux.*")) {
            dirs.add("/opt/1C/v8.3/x86_64");
            dirs.add(" /opt/1C/v8.3/i386");
        }
        return dirs;
    }

    // for lan log scanning
    public final static int serverPort = 48151;

}
