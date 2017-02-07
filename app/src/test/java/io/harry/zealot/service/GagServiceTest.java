package io.harry.zealot.service;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.harry.zealot.BuildConfig;
import io.harry.zealot.TestZealotApplication;
import io.harry.zealot.helper.FirebaseHelper;
import io.harry.zealot.model.Gag;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GagServiceTest {
    private static final long MILLIS_2017_06_19 = 1497873600000L;
    private static final String NO_MATTER = "no matter";

    @Inject
    FirebaseHelper mockFirebaseHelper;

    @Mock
    DatabaseReference mockDatabaseReference;
    @Mock
    DatabaseReference mockChildReference;
    @Mock
    StorageReference mockStorageReference;
    @Mock
    StorageReference mockImageReference;
    @Mock
    ServiceCallback<List<String>> mockStringListServiceCallback;
    @Mock
    ServiceCallback<List<Uri>> mockUriListServiceCallback;
    @Mock
    ServiceCallback<Void> mockVoidServiceCallback;
    @Mock
    StorageReference firstFile;
    @Mock
    StorageReference secondFile;
    @Mock
    Task<StorageMetadata> mockFirstTask;
    @Mock
    Task<StorageMetadata> mockSecondTask;
    @Mock
    UploadTask mockUploadTask;
    @Mock
    Bitmap mockBitmap;

    @Captor
    ArgumentCaptor<ValueEventListener> valueEventListenerCaptor;
    @Captor
    ArgumentCaptor<OnSuccessListener<StorageMetadata>> onSuccessListenerMetadataCaptor;
    @Captor
    ArgumentCaptor<OnSuccessListener<UploadTask.TaskSnapshot>> onSuccessListenerUploadCaptor;
    @Captor
    ArgumentCaptor<ByteArrayOutputStream> byteArrayOutputStreamCaptor;

    private GagService subject;

    @Before
    public void setUp() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(MILLIS_2017_06_19);

        ((TestZealotApplication) RuntimeEnvironment.application).getZealotComponent().inject(this);
        MockitoAnnotations.initMocks(this);

        subject = new GagService(RuntimeEnvironment.application);

        when(mockFirebaseHelper.getDatabaseReference(anyString())).thenReturn(mockDatabaseReference);
    }

    @Test
    public void getGagImageFileNames_getGagsReferenceFromFirebaseHelper() throws Exception {
        subject.getGagImageFileNames(mockStringListServiceCallback);

        verify(mockFirebaseHelper).getDatabaseReference("gags");
    }

    @Test
    public void getGagImageFileNames_addSingleValueEventListenerToReference() throws Exception {
        when(mockFirebaseHelper.getDatabaseReference("gags")).thenReturn(mockDatabaseReference);

        subject.getGagImageFileNames(mockStringListServiceCallback);

        verify(mockDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    public void getGagImageFileNames_extractsFileNamesFromDataSnapshot() throws Exception {
        DataSnapshot mockDataSnapshot = createMockDataSnapshotForJPGImages(3);

        subject.getGagImageFileNames(mockStringListServiceCallback);

        verify(mockDatabaseReference).addListenerForSingleValueEvent(valueEventListenerCaptor.capture());

        valueEventListenerCaptor.getValue().onDataChange(mockDataSnapshot);

        verify(mockStringListServiceCallback).onSuccess(Arrays.asList("0.jpg", "1.jpg", "2.jpg"));
    }

    @Test
    public void getGagImageUris_getsStorageReferenceFromFirebaseHelper() throws Exception {
        subject.getGagImageUris(new ArrayList<String>(), mockUriListServiceCallback);

        verify(mockFirebaseHelper).getStorageReference("gags");
    }

    @Test
    public void getGagImageURLs_getFilesFromStorageReference() throws Exception {
        createMockStorageReferenceForTwoFiles("0.jpg", "1.jpg");

        subject.getGagImageUris(Arrays.asList("0.jpg", "1.jpg"), mockUriListServiceCallback);

        verify(mockStorageReference).child("0.jpg");
        verify(mockStorageReference).child("1.jpg");
    }

    @Test
    public void getGagImageURLs_getsMetadataFromFile() throws Exception {
        createMockStorageReferenceForTwoFiles("0.jpg", "1.jpg");

        subject.getGagImageUris(Arrays.asList("0.jpg", "1.jpg"), mockUriListServiceCallback);

        verify(firstFile).getMetadata();
        verify(secondFile).getMetadata();
    }

    @Test
    public void getGagImageURLs_addSuccessListenerOnFileMetaData() throws Exception {
        createMockStorageReferenceForTwoFiles("0.jpg", "1.jpg");

        subject.getGagImageUris(Arrays.asList("0.jpg", "1.jpg"), mockUriListServiceCallback);

        verify(mockFirstTask).addOnSuccessListener(Matchers.<OnSuccessListener<StorageMetadata>>any());
        verify(mockSecondTask).addOnSuccessListener(Matchers.<OnSuccessListener<StorageMetadata>>any());
    }

    @Test
    public void getGagImageURLs_successListenerRunsServiceCallbackWithImageURL() throws Exception {
        createMockStorageReferenceForTwoFiles("0.jpg", "1.jpg");

        subject.getGagImageUris(Arrays.asList("0.jpg", "1.jpg"), mockUriListServiceCallback);

        verify(mockFirstTask).addOnSuccessListener(onSuccessListenerMetadataCaptor.capture());
        verify(mockSecondTask).addOnSuccessListener(onSuccessListenerMetadataCaptor.capture());

        StorageMetadata mockStorageMetaData0 = createMockStorageMetaData("http://myhost/0.jpg");
        onSuccessListenerMetadataCaptor.getAllValues().get(0).onSuccess(mockStorageMetaData0);

        verify(mockUriListServiceCallback, never()).onSuccess(anyListOf(Uri.class));

        StorageMetadata mockStorageMetaData1 = createMockStorageMetaData("http://myhost/1.jpg");
        onSuccessListenerMetadataCaptor.getAllValues().get(1).onSuccess(mockStorageMetaData1);

        verify(mockUriListServiceCallback).onSuccess(
                Arrays.asList(
                        Uri.parse("http://myhost/0.jpg"),
                        Uri.parse("http://myhost/1.jpg")));
    }

    private void setFirebaseMocksForUploadToStroage(String referenceName, String childName) {
        when(mockFirebaseHelper.getStorageReference(NO_MATTER.equals(referenceName) ? anyString() : referenceName))
                .thenReturn(mockStorageReference);
        when(mockStorageReference.child(NO_MATTER.equals(childName) ? anyString() : childName))
                .thenReturn(mockImageReference);

        when(mockImageReference.putBytes(any(byte[].class))).thenReturn(mockUploadTask);
    }

    @Test
    public void uploadGag_getsStorageInstanceFromFirebaseHelper() throws Exception {
        setFirebaseMocksForUploadToStroage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockFirebaseHelper).getStorageReference("gags");
    }

    @Test
    public void uploadGag_createsImageReferenceWithTimestamp() throws Exception {
        setFirebaseMocksForUploadToStroage(NO_MATTER, "1497873600000.jpg");

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockStorageReference).child("1497873600000.jpg");
    }

    @Test
    public void uploadGag_compressBitmapIntoByteArrayOutputStream() throws Exception {
        setFirebaseMocksForUploadToStroage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockBitmap).compress(
                eq(Bitmap.CompressFormat.JPEG), eq(100), any(ByteArrayOutputStream.class));
    }

    @Test
    public void uploadGag_putImageByteArrayToImageReference() throws Exception {
        setFirebaseMocksForUploadToStroage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockBitmap).compress(any(Bitmap.CompressFormat.class), anyInt(), byteArrayOutputStreamCaptor.capture());
        verify(mockImageReference).putBytes(byteArrayOutputStreamCaptor.getValue().toByteArray());
    }

    @Test
    public void uploadGag_addOnSuccessListenerToUploadTask() throws Exception {
        setFirebaseMocksForUploadToStroage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockUploadTask).addOnSuccessListener(Matchers.<OnSuccessListener<UploadTask.TaskSnapshot>>any());
    }

    private void assumeImageHasBeenUploaded(ArgumentCaptor<OnSuccessListener<UploadTask.TaskSnapshot>> onSuccessListenerUploadCaptor) {
        setFirebaseMocksForUploadToStroage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockUploadTask).addOnSuccessListener(onSuccessListenerUploadCaptor.capture());
    }

    private void setFirebaseMocksForPuttingFileToDatabase(String referenceName) {
        when(mockFirebaseHelper.getDatabaseReference(NO_MATTER.equals(referenceName) ? anyString() : referenceName))
                .thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.push()).thenReturn(mockChildReference);
    }

    @Test
    public void uploadGag_getsDatabaseReference_afterSuccessfulUpload() throws Exception {
        assumeImageHasBeenUploaded(onSuccessListenerUploadCaptor);

        setFirebaseMocksForPuttingFileToDatabase(NO_MATTER);

        onSuccessListenerUploadCaptor.getValue().onSuccess(mock(UploadTask.TaskSnapshot.class));

        verify(mockFirebaseHelper).getDatabaseReference("gags");
    }

    @Test
    public void uploadGag_pushAndGetKeyOfChild_afterSuccessfulUpload() throws Exception {
        assumeImageHasBeenUploaded(onSuccessListenerUploadCaptor);

        setFirebaseMocksForPuttingFileToDatabase(NO_MATTER);

        onSuccessListenerUploadCaptor.getValue().onSuccess(mock(UploadTask.TaskSnapshot.class));

        verify(mockDatabaseReference).push();
    }

    @Test
    public void uploadGag_putChildWithNewlyCreatedKey_afterSuccessfulUpload() throws Exception {
        assumeImageHasBeenUploaded(onSuccessListenerUploadCaptor);

        setFirebaseMocksForPuttingFileToDatabase(NO_MATTER);

        onSuccessListenerUploadCaptor.getValue().onSuccess(mock(UploadTask.TaskSnapshot.class));

        verify(mockChildReference).setValue(ImmutableMap.of("fileName", "1497873600000.jpg"));
    }

    @Test
    public void uploadGag_runsSuccessCallback_afterAllTheProcedureIsCleared() throws Exception {
        assumeImageHasBeenUploaded(onSuccessListenerUploadCaptor);

        setFirebaseMocksForPuttingFileToDatabase(NO_MATTER);

        verify(mockVoidServiceCallback, never()).onSuccess(null);

        onSuccessListenerUploadCaptor.getValue().onSuccess(mock(UploadTask.TaskSnapshot.class));

        verify(mockVoidServiceCallback).onSuccess(null);
    }

    @NonNull
    private DataSnapshot createMockDataSnapshotForJPGImages(int size) {
        DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);
        List<DataSnapshot> children = new ArrayList<>();

        for(int i = 0; i < size; ++i) {
            DataSnapshot mockChild = mock(DataSnapshot.class);

            Gag gag = new Gag();
            gag.fileName = i + ".jpg";

            when(mockChild.getValue(Gag.class)).thenReturn(gag);

            children.add(mockChild);
        }

        when(mockDataSnapshot.getChildren()).thenReturn(children);

        return mockDataSnapshot;
    }

    private void createMockStorageReferenceForTwoFiles(String firstFileName, String secondFileName) {
        when(mockFirebaseHelper.getStorageReference(anyString())).thenReturn(mockStorageReference);

        when(mockStorageReference.child(firstFileName)).thenReturn(firstFile);
        when(mockStorageReference.child(secondFileName)).thenReturn(secondFile);

        when(firstFile.getMetadata()).thenReturn(mockFirstTask);
        when(secondFile.getMetadata()).thenReturn(mockSecondTask);
    }

    private StorageMetadata createMockStorageMetaData(String url){
        StorageMetadata mockStorageMetaData = mock(StorageMetadata.class);
        Uri mockUri = Uri.parse(url);
        when(mockStorageMetaData.getDownloadUrl()).thenReturn(mockUri);

        return mockStorageMetaData;
    }
}