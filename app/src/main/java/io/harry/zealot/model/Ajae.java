package io.harry.zealot.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import io.harry.zealot.R;
import io.harry.zealot.state.AjaePower;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Ajae {
    private AjaePower ajaePower;
    @ColorRes
    @Getter
    private final int color;
    @StringRes
    @Getter
    private final int message;
    @DrawableRes
    @Getter
    private final int image;
    @StringRes
    @Getter
    private final int taunt;

    public Ajae(AjaePower ajaePower) {
        this.ajaePower = ajaePower;

        switch (ajaePower) {
            case BURNT:
                color = R.color.burnt_ajae;
                message = R.string.burnt_ajae_message;
                image = R.drawable.burnt_ajae;
                taunt = R.string.burnt_taunt;
                break;
            case WELL_DONE:
                color = R.color.well_done_ajae;
                message = R.string.well_done_ajae_message;
                image = R.drawable.well_done_ajae;
                taunt = R.string.well_taunt;
                break;
            case MEDIUM:
                color = R.color.medium_ajae;
                message = R.string.medium_ajae_message;
                image = R.drawable.medium_ajae;
                taunt = R.string.medium_taunt;
                break;
            case RARE:
                color = R.color.rare_ajae;
                message = R.string.rare_ajae_message;
                image = R.drawable.rare_ajae;
                taunt = R.string.rare_taunt;
                break;
            case NONE:
                color = R.color.none_ajae;
                message = R.string.none_ajae_message;
                image = R.drawable.none_ajae;
                taunt = R.string.none_taunt;
                break;

            default:
                color = R.color.none_ajae;
                message = R.string.none_ajae_message;
                image = R.drawable.none_ajae;
                taunt = R.string.none_taunt;
        }
    }
}