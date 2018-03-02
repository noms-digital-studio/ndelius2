package helpers;

import org.apache.commons.compress.utils.IOUtils;
import play.Environment;
import play.Logger;

import java.io.IOException;

public class StaticImage {
    public static byte[] noPhotoImage(Environment environment) {
        try {
            return IOUtils.toByteArray(environment.resourceAsStream("/public/images/NoPhoto@2x.png"));
        } catch (IOException e) {
            Logger.error("Unable to read NoPhoto@2x.png ", e);
            return null;
        }
    }

}
