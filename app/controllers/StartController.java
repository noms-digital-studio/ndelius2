package controllers;

import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import lombok.val;
import play.mvc.*;

public class StartController extends Controller {

    public Result startReport() {

        val reportData = new HashMap<String, String>() {
            {
                put("name", "Alan Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "1 Albert Square, Manchester, Greater Manchester. M60 2LA");
                put("crn", "B56789");
                put("pnc", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", new SimpleDateFormat("dd MMMM yyy").format(new Date()));
                put("localJusticeArea", "Greater Manchester");
            }
        };

        return ok(views.html.startReport.render(reportData));
    }
}
