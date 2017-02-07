package io.harry.zealot.fragment;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.harry.zealot.BuildConfig;
import io.harry.zealot.R;
import io.harry.zealot.TestZealotApplication;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GagFragmentTest {
    private GagFragment subject;

    @BindView(R.id.gag_image)
    ImageView gagImage;

    @Inject
    Picasso mockPicasso;

    @Mock
    RequestCreator mockRequestCreator;

    @Test
    public void gagImage_loadsImageUriIntoGagImageView() throws Exception {
        MockitoAnnotations.initMocks(this);
        ((TestZealotApplication)RuntimeEnvironment.application).getZealotComponent().inject(this);
        Uri gag1Uri = Uri.parse("http://gag1.png");

        when(mockPicasso.load(any(Uri.class))).thenReturn(mockRequestCreator);

        subject = GagFragment.newInstance(gag1Uri);
        SupportFragmentTestUtil.startFragment(subject);

        ButterKnife.bind(this, subject.getView());

        verify(mockPicasso).load(Uri.parse("http://gag1.png"));
        verify(mockRequestCreator).into(gagImage);
    }
}