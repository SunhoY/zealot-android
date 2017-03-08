package io.harry.zealot.range;

import org.junit.Before;
import org.junit.Test;

import static io.harry.zealot.state.AjaePower.BURNT;
import static io.harry.zealot.state.AjaePower.MEDIUM;
import static io.harry.zealot.state.AjaePower.NONE;
import static io.harry.zealot.state.AjaePower.RARE;
import static io.harry.zealot.state.AjaePower.WELL_DONE;
import static org.assertj.core.api.Assertions.assertThat;

public class AjaeScoreRangeTest {

    private AjaeScoreRange subject;

    @Before
    public void setUp() throws Exception {
        subject = new AjaeScoreRange();
    }

    @Test
    public void scoreRange_returnsValue_accordingToRange() throws Exception {
        assertThat(subject.getAjaePower(-1)).isEqualTo(NONE);
        assertThat(subject.getAjaePower(0)).isEqualTo(NONE);
        assertThat(subject.getAjaePower(29)).isEqualTo(NONE);
        assertThat(subject.getAjaePower(30)).isEqualTo(RARE);
        assertThat(subject.getAjaePower(49)).isEqualTo(RARE);
        assertThat(subject.getAjaePower(50)).isEqualTo(MEDIUM);
        assertThat(subject.getAjaePower(69)).isEqualTo(MEDIUM);
        assertThat(subject.getAjaePower(70)).isEqualTo(WELL_DONE);
        assertThat(subject.getAjaePower(89)).isEqualTo(WELL_DONE);
        assertThat(subject.getAjaePower(90)).isEqualTo(BURNT);
        assertThat(subject.getAjaePower(100)).isEqualTo(BURNT);
        assertThat(subject.getAjaePower(101)).isEqualTo(BURNT);
    }
}