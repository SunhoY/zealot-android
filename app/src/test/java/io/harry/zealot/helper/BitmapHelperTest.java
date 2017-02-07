package io.harry.zealot.helper;

import android.content.ContentResolver;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.annotation.Config;

import io.harry.zealot.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@Config(constants = BuildConfig.class)
@PrepareForTest({BitmapFactory.class, Bitmap.class})
public class BitmapHelperTest {
    private static final String NO_MATTER = "no matter";
    private BitmapHelper subject;

    @Mock
    ContentResolver mockContentResolver;
    @Mock
    Uri mockURI;
    @Mock
    MatrixCursor mockCursor;
    @Mock
    Bitmap mockBitmap;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(BitmapFactory.class);
        mockStatic(Bitmap.class);

        subject = new BitmapHelper(mockContentResolver);
    }

    @Test
    public void getBitmapByURI_queriesImageViaContentResolver() throws Exception {
        mockContentResolverWithMockCursor(NO_MATTER, mockCursor);

        subject.getBitmapByURI(mockURI);

        verify(mockContentResolver).query(mockURI, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
    }

    @Test
    public void getBitmapByURI_getStringBinaryFromCursor() throws Exception {
        mockContentResolverWithMockCursor("awesome image binary", mockCursor);

        expect(BitmapFactory.decodeFile("awesome image binary")).andReturn(mockBitmap);

        replay(BitmapFactory.class);

        Bitmap result = subject.getBitmapByURI(mockURI);

        PowerMock.verify(BitmapFactory.class);

        assertThat(result).isEqualTo(mockBitmap);
    }

    @Test
    public void getBitmapByURI_makeSureCursorIsClosed() throws Exception {
        mockContentResolverWithMockCursor(NO_MATTER, mockCursor);

        subject.getBitmapByURI(mockURI);

        verify(mockCursor).close();
    }

    @Test
    public void scaleBitmap_returnsScaledBitmap() throws Exception {
        Bitmap mockResult = mock(Bitmap.class);
        expect(Bitmap.createScaledBitmap(mockBitmap, 720, 1280, false)).andReturn(mockResult);

        replay(Bitmap.class);

        Bitmap result = subject.scaleBitmap(mockBitmap, 720, 1280);

        PowerMock.verify(Bitmap.class);

        assertThat(result).isEqualTo(mockResult);
    }

    private void mockContentResolverWithMockCursor(String firstRowValue, MatrixCursor mockCursor) {
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.getString(0)).thenReturn(firstRowValue);
        when(mockContentResolver.query(any(Uri.class), any(String[].class), anyString(), any(String[].class), anyString()))
                .thenReturn(mockCursor);
    }
}