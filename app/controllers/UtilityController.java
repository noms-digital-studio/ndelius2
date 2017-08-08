package controllers;

import com.google.common.collect.ImmutableMap;
import play.libs.Json;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import lombok.val;
import play.mvc.Controller;
import play.mvc.Result;

public class UtilityController extends Controller {

    public Result healthcheck() {

        String localHost;
        val runtime = Runtime.getRuntime();
        val roots = Arrays.stream(File.listRoots()).map(root -> ImmutableMap.of(
                "filePath", root.getAbsolutePath(),
                "totalSpace", root.getTotalSpace(),
                "freeSpace", root.getFreeSpace(),
                "usableSpace", root.getUsableSpace()
        ));

        try {
            localHost = InetAddress.getLocalHost().toString();

        } catch (UnknownHostException ex) {

            localHost = "unknown";
        }

        return ok(Json.toJson(ImmutableMap.of(
                "status", "OK",
                "runtime", ImmutableMap.of(
                        "processors", runtime.availableProcessors(),
                        "freeMemory", runtime.freeMemory(),
                        "totalMemory", runtime.totalMemory(),
                        "maxMemory", runtime.maxMemory()
                ),
                "fileSystems", roots,
                "localHost", localHost
        )));
    }
}
