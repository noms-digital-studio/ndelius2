package services;

import data.offendersearch.Address;
import data.offendersearch.Alias;
import data.offendersearch.OffenderSummary;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import play.Environment;
import play.Mode;
import services.search.ElasticSearch;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ElasticSearchTest {

    @Test
    public void returnsSearchResults() {

        val environment = new Environment(null, this.getClass().getClassLoader(), Mode.TEST);
        val elasticSearch = new ElasticSearch(environment);
        val results = elasticSearch.search("smith");

        assertThat(results.join().getOffenders().size()).isEqualTo(3);
        assertThat(results.join().getOffenders()).contains(anOffenderSummary());
    }

    private OffenderSummary anOffenderSummary() {
        return OffenderSummary.builder()
            .offenderId("2500020000")
            .firstName("John")
            .surname("Smith")
            .dateOfBirth("1955-10-07")
            .gender("Male Only")
            .crn("X012261")
            .currentOffender(true)
            .previousSurname("Not Yet Implemented")
            .risk("Not Yet Implemented")
            .aliases(aListOfAliases())
            .addresses(aListOfAddresses())
            .build();
    }

    private List<Address> aListOfAddresses() {
        val addresses = new ArrayList<Address>();
        addresses.add(anAddress());
        addresses.add(anOtherAddress());
        return addresses;

    }

    private Address anAddress() {
        return Address.builder()
            .addressNumber("2")
            .buildingName("Fair View")
            .streetName("South Street")
            .town("Sheffield")
            .county("South Yorkshire")
            .postcode("S1 5ZX")
            .build();
    }

    private Address anOtherAddress() {
        return Address.builder()
            .addressNumber("91")
            .buildingName("Dale Farm")
            .streetName("Dalton Street")
            .town("Leeds")
            .county("West Yorkshire")
            .postcode("LS5 5OP")
            .build();
    }

    private List<Alias> aListOfAliases() {
        val aliases = new ArrayList<Alias>();
        aliases.add(anAlias());
        aliases.add(anOtherAlias());
        return aliases;
    }

    private Alias anAlias() {
        return Alias.builder()
            .dateOfBirth("1970-01-01")
            .firstName("Fred")
            .surname("Blogs")
            .build();
    }

    private Alias anOtherAlias() {
        return Alias.builder()
            .dateOfBirth("1972-12-12")
            .firstName("Dave")
            .surname("Johnson")
            .build();
    }
}