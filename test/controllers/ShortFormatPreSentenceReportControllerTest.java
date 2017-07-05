package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.val;
import org.junit.Test;
import play.Application;
import play.filters.csrf.*;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class ShortFormatPreSentenceReportControllerTest extends WithApplication implements PdfGenerator, DocumentStore {

  @Test
  public void getSampleReportOK() {

    val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, request);

    assertEquals(OK, result.status());
  }

  @Test
  public void getSampleReportConsumesDtoQueryStrings() {

    val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?name=Alan%20Smith&foobar=xyz987");

    val content = Helpers.contentAsString(route(app, request));

    assertTrue(content.contains("Alan Smith"));
    assertFalse(content.contains("xyz987"));
    }

    @Test
    public void getSampleReportWithDocumentIdDecyptsAndRetrievesFromStore() {

        try {

            val secretKey = "ThisIsASecretKey";
            val clearDocumentId = "12345";
            val clearUserName = "John Smith";

            val documentId = URLEncoder.encode(Encryption.encrypt(clearDocumentId, secretKey), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(Encryption.encrypt(clearUserName, secretKey), "UTF-8");

            val request = new RequestBuilder().method(GET).
                    uri("/report/shortFormatPreSentenceReport?documentId=" + documentId + "&onBehalfOfUser=" + onBehalfOfUser);

            val content = Helpers.contentAsString(route(app, request));

            assertTrue(content.contains(clearDocumentId));          // Decrypted documentId is returned by Mock retrieveOriginalData
            assertTrue(content.contains(clearUserName));            // Decrypted onBehalfOfUser is returned by Mock retrieveOriginalData
            assertTrue(content.contains("Retrieved From Store"));   // Returned from Mock retrieveOriginalData

        } catch (Exception ex) {

            fail(ex.getMessage());
        }
  }

  @Test
  public void postSampleReportPage1TitleOnlyReturnsBadRequest() {

    val formData = ImmutableMap.of(
        "name", "",
        "pageNumber", "1"
    );
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage1AllFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("pageNumber", "1");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage2SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("pageNumber", "2");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage2AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("pageNumber", "2");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage3SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("otherInformationSource", "true");
        put("pageNumber", "3");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage3WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "This texxt has speeling mistakes");
        put("pageNumber", "3");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage3WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "This texxt has speeling mistakes");
        put("ignoreOtherInformationDetailsSpelling", "true");
        put("pageNumber", "3");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage3AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("pageNumber", "3");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage4WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence spelld wrng");
        put("offenceSummary", "Some offence summary spelld wrng");
        put("offenceAnalysis", "Some offence analysis spelld wrng");
        put("pageNumber", "4");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage4WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence spelld wrng");
        put("ignoreMainOffenceSpelling", "true");
        put("offenceSummary", "Some offence summary spelld wrng");
        put("ignoreOffenceSummarySpelling", "true");
        put("offenceAnalysis", "Some offence analysis spelld wrng");
        put("ignoreOffenceAnalysisSpelling", "true");
        put("pageNumber", "4");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage4SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("pageNumber", "4");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage4AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("pageNumber", "4");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage5WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment speld wrng");
        put("pageNumber", "5");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage5WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment speld wrng");
        put("ignoreOffenderAssessmentSpelling", "true");
        put("pageNumber", "5");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage5SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("pageNumber", "5");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage5AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("pageNumber", "5");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage6WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending speld wrng");
        put("pageNumber", "6");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage6WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending speld wrng");
        put("ignorePatternOfOffendingSpelling", "true");
        put("pageNumber", "6");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage6AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("pageNumber", "6");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage7WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response speld wrng");
        put("likelihoodOfReOffending", "Some likelihood of re-offending speld wrng");
        put("pageNumber", "7");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage7WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response speld wrng");
        put("ignorePreviousSupervisionResponseSpelling", "true");
        put("likelihoodOfReOffending", "Some likelihood of re-offending speld wrng");
        put("ignoreLikelihoodOfReOffendingSpelling", "true");
        put("pageNumber", "7");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage7SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("pageNumber", "7");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage7AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("pageNumber", "7");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage8WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment speld wrng");
        put("pageNumber", "8");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage8WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment speld wrng");
        put("ignoreRiskOfSeriousHarmSpelling", "true");
        put("pageNumber", "8");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage8SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("pageNumber", "8");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage8AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("pageNumber", "8");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage9WithSpellingMistakeReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("proposal", "An example of a proposalspeld wring");
        put("pageNumber", "9");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage9WithSpellingMistakeAndOverrideReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("ignoreRiskOfSeriousHarmSpelling", "true");
        put("proposal", "An example of a proposal speld wring");
        put("ignoreProposalSpelling", "true");
        put("pageNumber", "9");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage9SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("pageNumber", "9");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage9AllRequiredFieldsReturnsOK() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("ignoreOtherInformationDetailsSpelling", "false");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some pattern of offending");
        put("previousSupervisionResponse", "Some previous supervision response");
        put("likelihoodOfReOffending", "Some likelihood of re-offending");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("proposal", "An example of a proposal");
        put("pageNumber", "9");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
  }

  @Test
  public void postSampleReportPage10SomeFieldsMissingReturnsBadRequest() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some assessment");
        put("previousSupervisionResponse", "Some assessment");
        put("likelihoodOfReOffending", "Some assessment");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("proposal", "An example of a proposal");
        put("reportAuthor", "Robert Jenkins");
        put("pageNumber", "10");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

    val result = route(app, addCsrfToken(request));

    assertEquals(BAD_REQUEST, result.status());
  }

  @Test
  public void postSampleReportPage10AllRequiredFieldsReturnsOKAndPdfGenerated() {

    val formData = new HashMap<String, String>() {
      {
        put("name", "John Smith");
        put("dateOfBirth", "06/02/1976");
        put("age", "41");
        put("address", "10 High Street");
        put("crn", "B56789");
        put("pcn", "98793030");
        put("court", "Manchester and Salford Magistrates Court");
        put("dateOfHearing", "01/02/2017");
        put("localJusticeArea", "Greater Manchester");
        put("interviewInformationSource", "true");
        put("serviceRecordsInformationSource", "true");
        put("cpsSummaryInformationSource", "true");
        put("oasysAssessmentsInformationSource", "true");
        put("previousConvictionsInformationSource", "true");
        put("victimStatementInformationSource", "true");
        put("childrenServicesInformationSource", "true");
        put("policeInformationSource", "true");
        put("otherInformationSource", "true");
        put("otherInformationDetails", "These notes are spelled correctly");
        put("mainOffence", "Some offence");
        put("offenceSummary", "Some offence summary");
        put("offenceAnalysis", "Some offence analysis");
        put("offenderAssessment", "Some assessment");
        put("patternOfOffending", "Some assessment");
        put("previousSupervisionResponse", "Some assessment");
        put("likelihoodOfReOffending", "Some assessment");
        put("riskOfSeriousHarm", "An example of a risk assessment");
        put("proposal", "An example of a proposal");
        put("reportAuthor", "Robert Jenkins");
        put("office", "Manchester and Salford Magistrates Court");
        put("pageNumber", "10");
      }
    };
    val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");
    pdfGenerated = false;

    val result = route(app, addCsrfToken(request));

    assertEquals(OK, result.status());
//        assertEquals("application/pdf", result.contentType().orElse(""));
    assertTrue(pdfGenerated);
  }

  private boolean pdfGenerated;

  @Override
  public <T> CompletionStage<Byte[]> generate(String templateName, T values) {

    pdfGenerated = true;

    return CompletableFuture.supplyAsync(() -> new Byte[0]);    // Mocked PdfGenerator returns empty Byte array
    }

    @Override
    public CompletionStage<Map> uploadNewPdf(Byte[] document, String filename, String originalData, String onBehalfOfUser, String crn, Integer entityId) {
        return null;
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        return CompletableFuture.supplyAsync(() -> "{ \"name\": \"" + onBehalfOfUser + "\", \"address\": \"" + documentId + "\", \"court\": \"Retrieved From Store\" }");
  }

  @Override
  protected Application provideApplication() {

    return new GuiceApplicationBuilder().
                overrides(
                        bind(PdfGenerator.class).toInstance(this),  // Mock out PdfGenerator to this Test Class
                        bind(DocumentStore.class).toInstance(this)  // Mock out DocumentStore to this Test Class
                ).
        build();
  }

  private RequestBuilder addCsrfToken(RequestBuilder requestBuilder) {
    final CSRFFilter csrfFilter = app.injector().instanceOf(CSRFFilter.class);
    final CSRFConfig csrfConfig = app.injector().instanceOf(CSRFConfigProvider.class).get();
    final String token = csrfFilter.tokenProvider().generateToken();

    requestBuilder.tag(CSRF.Token$.MODULE$.NameRequestTag(), csrfConfig.tokenName());
    requestBuilder.tag(CSRF.Token$.MODULE$.RequestTag(), token);
    requestBuilder.header(csrfConfig.headerName(), token);

    return requestBuilder;
  }
}
