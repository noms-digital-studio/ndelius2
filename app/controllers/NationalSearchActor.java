package controllers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import data.nationalSearch.Request;
import data.nationalSearch.Response;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.val;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;

@Value
@EqualsAndHashCode(callSuper = true)
public class NationalSearchActor extends AbstractActor {

    public static Props props(ActorRef out) {
        return Props.create(NationalSearchActor.class, out);
    }

    private final ActorRef out;

    @Override
    public Receive createReceive() {

        val spellChecker = new JLanguageTool(new BritishEnglish());

        return receiveBuilder().match(Request.class, request -> out.tell(
                spellChecker.check(request.getSearch()).stream().map(mistake -> new Response(

                        request.getSearch().substring(mistake.getFromPos(), mistake.getToPos()),
                        mistake.getSuggestedReplacements()
                )),
                self()
        )).build();
    }
}
