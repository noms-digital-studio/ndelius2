package views;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.JwtHelperTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import views.pages.SearchFeedbackPage;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

@RunWith(MockitoJUnitRunner.class)
public class SearchFeedbackWebTest extends WithPartialMockedApplicationBrowser {
    private SearchFeedbackPage searchFeedbackPage;

    @Before
    public void before() {
        searchFeedbackPage = new SearchFeedbackPage(browser);
        when(pdfGenerator.generate(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
    }

    @Test
    public void rendersFeedbackOnPage() {
        when(analyticsStore.nationalSearchFeedback()).thenReturn(CompletableFuture.supplyAsync(() -> ImmutableList.of(feedbackContext())));

        searchFeedbackPage.navigateHere().isAt();

        assertThat(searchFeedbackPage.getSubmittedDate()).isNotEmpty();
        assertThat(searchFeedbackPage.getUsernameAndEmail()).isEqualTo("fake.user\nfoo@bar.com");
        assertThat(searchFeedbackPage.getRoleProviderRegion()).isEqualTo("Probation Officer\nNPS\nNorth East");
        assertThat(searchFeedbackPage.getRating()).isEqualTo("Satisfied");
        assertThat(searchFeedbackPage.getAdditionalComments()).isEqualTo("Some text");
    }

    private ImmutableMap<String, Object> feedbackContext() {
        return ImmutableMap.of(
            "username", "cn=fake.user,cn=Users,dc=moj,dc=com",
            "dateTime", new Date(),
            "feedback", userFeedback());
    }

    private ImmutableMap<String, Object> userFeedback() {
        return ImmutableMap.<String, Object>builder()
            .put("email", "foo@bar.com")
            .put("feedback", "Some text")
            .put("rating", "Satisfied")
            .put("role", "Probation Officer")
            .put("provider", "NPS")
            .put("region", "North East")
            .build();
    }

}
