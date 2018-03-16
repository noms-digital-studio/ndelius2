package views;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import play.Environment;
import play.Mode;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.WithBrowser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class WithChromeBrowser extends WithBrowser {
    @Override
    protected TestBrowser provideBrowser(int port) {
        System.setProperty("webdriver.chrome.driver", prepareChromeDriver());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        return Helpers.testBrowser(new ChromeDriver(options), port);
    }

    private static boolean isMac() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        return ((OS.contains("mac")) || (OS.contains("darwin")));
    }

    private static String prepareChromeDriver()  {
        final URL driverPath = new Environment(Mode.TEST).resource(String.format("webdriver/%s/chromedriver", isMac() ? "mac64" : "linux64"));
        fixExecutablePermissions(driverPath);
        return driverPath.getPath();
    }

    private static void fixExecutablePermissions(URL driverPath) {
        //fix permission when running inside linux since executable permission is being lost
        //when copying resource to target directory

        //using PosixFilePermission to set file permissions 777
        Set<PosixFilePermission> perms = new HashSet<>();
        //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        //add others permissions
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        try {
            Files.setPosixFilePermissions(Paths.get(driverPath.getPath()), perms);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
