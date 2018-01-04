package data;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ShortFormatPreSentenceReportDataTest {
    private ShortFormatPreSentenceReportData data;

    @Before
    public void before() {
        data = new ShortFormatPreSentenceReportData();
    }

    @Test
    public void addressLinesSplitByNewline() {
        data.setAddress("Green Gardens\n" +
                "123 Fake St\n" +
                "Hunters Bar\n" +
                "Sheffield\n" +
                "South Yorkshire\n" +
                "S11 2XZ\n");

        assertThat(data.addressLines()).contains(
                "Green Gardens",
                "123 Fake St",
                "Hunters Bar",
                "Sheffield",
                "South Yorkshire",
                "S11 2XZ"
        ).hasSize(6);
    }

    @Test
    public void addressLinesBlanksRemoved() {
        data.setAddress("Green Gardens\n" +
                "123 Fake St\n" +
                "\n" +
                "Sheffield\n" +
                "South Yorkshire\n" +
                "S11 2XZ\n");

        assertThat(data.addressLines()).contains(
                "Green Gardens",
                "123 Fake St",
                "Sheffield",
                "South Yorkshire",
                "S11 2XZ"
        ).hasSize(5);
    }

    @Test
    public void singleAddressLineCreatesSingleValue() {
        data.setAddress("Green Gardens\n");

        assertThat(data.addressLines()).contains(
                "Green Gardens").hasSize(1);
    }
    @Test
    public void singleAddressLineWithNoNewlineCreatesSingleValue() {
        data.setAddress("Green Gardens");

        assertThat(data.addressLines()).contains(
                "Green Gardens").hasSize(1);
    }
    @Test
    public void nullAddressHasEmptyAddressLineList() {
        data.setAddress(null);

        assertThat(data.addressLines()).isEmpty();
    }
    @Test
    public void blankAddressHasEmptyAddressLineList() {
        data.setAddress("");

        assertThat(data.addressLines()).isEmpty();
    }
}