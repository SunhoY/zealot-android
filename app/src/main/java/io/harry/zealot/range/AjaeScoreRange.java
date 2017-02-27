package io.harry.zealot.range;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import io.harry.zealot.state.AjaePower;

public class AjaeScoreRange {
    private static final int LOWER_NONE = 0;
    private static final int UPPER_NONE = 30;
    private static final int LOWER_RARE = 30;
    private static final int UPPER_RARE = 50;
    private static final int LOWER_MEDIUM = 50;
    private static final int UPPER_MEDIUM = 70;
    private static final int LOWER_WELL_DONE = 70;
    private static final int UPPER_WELL_DONE = 90;
    private static final int LOWER_BURNT = 90;
    private static final int UPPER_BURNT = 100;

    private final RangeMap<Integer, AjaePower> ajaeScoreRangeMap;

    public AjaeScoreRange() {
        this.ajaeScoreRangeMap = TreeRangeMap.create();

        this.ajaeScoreRangeMap.put(Range.closedOpen(LOWER_NONE, UPPER_NONE), AjaePower.NONE);
        this.ajaeScoreRangeMap.put(Range.closedOpen(LOWER_RARE, UPPER_RARE), AjaePower.RARE);
        this.ajaeScoreRangeMap.put(Range.closedOpen(LOWER_MEDIUM, UPPER_MEDIUM), AjaePower.MEDIUM);
        this.ajaeScoreRangeMap.put(Range.closedOpen(LOWER_WELL_DONE, UPPER_WELL_DONE), AjaePower.WELL_DONE);
        this.ajaeScoreRangeMap.put(Range.closed(LOWER_BURNT, UPPER_BURNT), AjaePower.BURNT);
    }

    public AjaePower getRange(int value) {
        return this.ajaeScoreRangeMap.get(value);
    }
}
