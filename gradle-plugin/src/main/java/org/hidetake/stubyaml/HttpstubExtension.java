package org.hidetake.stubyaml;

import lombok.Data;
import org.hidetake.stubyaml.util.MapUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class HttpstubExtension {

    //app settings
    private String serverPort = "8080";
    private String appLogLevel = "INFO";
    private String adminPrefix = "/admin";
    private String stubData = "src/test/resources/stubs";
    private String watchInterval = "5";
    private String watchEnabled = "true";
    private String loggingFile;
    //plugin settings
    private String awaitedLine = "OK";
    private Long taskTimeout = 10L;

    public List<String> toArgsList() {
        Map<String, String> map = MapUtil.of(
            "SERVER_PORT", serverPort
            ,"APP_LOG_LEVEL", appLogLevel
            ,"ADMIN_PREFIX", adminPrefix
            ,"STUB_DATA", stubData
            ,"WATCH_INTERVAL", watchInterval
            ,"WATCH_ENABLED", watchEnabled
            ,"logging.file", loggingFile
        );

        return map.entrySet().stream()
            .filter(entry -> isNotEmpty(entry.getValue()))
            .map(entry -> String.format("--%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

}
