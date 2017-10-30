package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import com.google.common.collect.ImmutableMap;
import data.nationalSearch.Request;
import helpers.JsonHelper;
import javax.inject.Inject;
import java.io.IOException;
import lombok.val;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import play.libs.streams.ActorFlow;
import play.mvc.*;
import views.html.nationalSearch;

public class NationalSearchController extends Controller {

    private final nationalSearch template;
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    @Inject
    public NationalSearchController(nationalSearch template, ActorSystem actorSystem, Materializer materializer) {

        this.template = template;
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    public Result index() {

        return ok(template.render());
    }

    public Result postSpellcheck() {

        return spellcheck(request().body().asText());
    }

    public Result spellcheck(String text) {

        val spellChecker = new JLanguageTool(new BritishEnglish());

        try {
            return JsonHelper.okJson(
                    spellChecker.check(text).stream().map(mistake -> ImmutableMap.of(
                            "mistake", text.substring(mistake.getFromPos(), mistake.getToPos()),
                            "suggestions", mistake.getSuggestedReplacements()
                            )
                    )
            );
        } catch (IOException ex) {

            return badRequest(ex.getMessage());
        }
    }

    public WebSocket socket() {

        return WebSocket.json(Request.class).accept(requestHeader ->

                ActorFlow.actorRef(NationalSearchActor::props, actorSystem, materializer)
        );
    }
}
