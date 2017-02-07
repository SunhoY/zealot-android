package io.harry.zealot.range;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import io.harry.zealot.state.AjaePower;

public class AjaeScoreRange {
    private static final int UPPER_NO_POWER = 70;
    private static final int LOWER_NO_POWER = 0;
    private static final int LOWER_MEDIUM_POWER = 70;
    private static final int UPPER_MEDIUM_POWER = 90;
    private static final int LOWER_FULL_POWER = 90;
    private static final int UPPER_FULL_POWER = 100;

    private final RangeMap<Integer, AjaePower> ajaeScoreRangeMap;

    public AjaeScoreRange() {
        this.ajaeScoreRangeMap = TreeRangeMap.create();

        this.ajaeScoreRangeMap.put(Range.closedOpen(LOWER_NO_POWER, UPPER_NO_POWER), AjaePower.NO);
        this.ajaeScoreRangeMap.put(Range.closedOpen(LOWER_MEDIUM_POWER, UPPER_MEDIUM_POWER), AjaePower.MEDIUM);
        this.ajaeScoreRangeMap.put(Range.closed(LOWER_FULL_POWER, UPPER_FULL_POWER), AjaePower.FULL);
    }

    public AjaePower getRange(int value) {
        return this.ajaeScoreRangeMap.get(value);
    }
}
