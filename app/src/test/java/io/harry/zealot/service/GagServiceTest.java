package io.harry.zealot.service;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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
    private static final int ANY_COUNT = 9;
    private static final boolean ANY_VERIFIED = true;
    private static final String ANY_FILE_NAME = "no matter";
    private static final String ANY_KEY = "no_matter";

    @Inject
    FirebaseHelper mockFirebaseHelper;

    @Mock
    DatabaseReference mockDatabaseReference;
    @Mock
    DatabaseReference mockVerifiedFieldReference;
    @Mock
    DatabaseReference mockChildReference;
    @Mock
    StorageReference mockStorageReference;
    @Mock
    StorageReference mockImageReference;
    @Mock
    ServiceCallback<List<Gag>> mockGagListServiceCallback;
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
    Query mockQuery;
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
    @Captor
    ArgumentCaptor<List<Gag>> gagListServiceCallbackCaptor;

    private GagService subject;

    @Before
    public void setUp() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(MILLIS_2017_06_19);

        ((TestZealotApplication) RuntimeEnvironment.application).getZealotComponent().inject(this);
        MockitoAnnotations.initMocks(this);

        subject = new GagService(RuntimeEnvironment.application);

        when(mockFirebaseHelper.getDatabaseReference(anyString())).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.orderByChild(anyString())).thenReturn(mockQuery);
        when(mockQuery.equalTo(anyBoolean())).thenReturn(mockQuery);
        when(mockQuery.limitToLast(anyInt())).thenReturn(mockQuery);
    }

    @Test
    public void getGags_getsGagsReferenceFromFirebaseHelper() throws Exception {
        subject.getGags(ANY_COUNT, ANY_VERIFIED, mockGagListServiceCallback);

        verify(mockFirebaseHelper).getDatabaseReference("gags");
    }

    @Test
    public void getGags_queriesOrderingByVerifiedField() throws Exception {
        subject.getGags(ANY_COUNT, ANY_VERIFIED, mockGagListServiceCallback);

        verify(mockDatabaseReference).orderByChild("verified");
    }

    @Test
    public void getGags_queriesVerifiedValueIsTrue() throws Exception {
        subject.getGags(ANY_COUNT, true, mockGagListServiceCallback);

        verify(mockQuery).equalTo(true);
    }

    @Test
    public void getGags_queriesVerifiedValueIsFalse() throws Exception {
        subject.getGags(ANY_COUNT, false, mockGagListServiceCallback);

        verify(mockQuery).equalTo(false);
    }

    @Test
    public void getGags_queriesSampleSize100ItemsFromDatabaseReference() throws Exception {
        subject.getGags(10, ANY_VERIFIED, mockGagListServiceCallback);

        verify(mockQuery).limitToLast(100);
    }

    @Test
    public void getGags_addSingleValueEventListenerToQuery() throws Exception {
        when(mockFirebaseHelper.getDatabaseReference("gags")).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.limitToLast(anyInt())).thenReturn(mockQuery);

        subject.getGags(ANY_COUNT, ANY_VERIFIED, mockGagListServiceCallback);

        verify(mockQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    @Ignore
    public void getGags_shufflesItemsFromFirebase() throws Exception {
        fail("power mock test should be here");
    }

    @Test
    public void getGags_extractsFileNamesFromDataSnapshot_andPassRequestedNumberOfItemsToServiceCallback() throws Exception {
        DataSnapshot mockDataSnapshot = createMockDataSnapshotForJPGImages(3);

        subject.getGags(2, ANY_VERIFIED, mockGagListServiceCallback);

        verify(mockQuery).addListenerForSingleValueEvent(valueEventListenerCaptor.capture());

        valueEventListenerCaptor.getValue().onDataChange(mockDataSnapshot);

        verify(mockGagListServiceCallback).onSuccess(gagListServiceCallbackCaptor.capture());

        assertThat(gagListServiceCallbackCaptor.getValue().size()).isEqualTo(2);
    }

    @Test
    public void getGags_retrievesLowerNumberOfGagsAmongFullListAndRequestedList() throws Exception {
        DataSnapshot mockDataSnapshot = createMockDataSnapshotForJPGImages(3);

        subject.getGags(10, ANY_VERIFIED, mockGagListServiceCallback);

        verify(mockQuery).addListenerForSingleValueEvent(valueEventListenerCaptor.capture());

        valueEventListenerCaptor.getValue().onDataChange(mockDataSnapshot);

        verify(mockGagListServiceCallback).onSuccess(gagListServiceCallbackCaptor.capture());

        assertThat(gagListServiceCallbackCaptor.getValue().size()).isEqualTo(3);
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

    private void setFirebaseMocksForUploadToStorage(String referenceName, String childName) {
        when(mockFirebaseHelper.getStorageReference(NO_MATTER.equals(referenceName) ? anyString() : referenceName))
                .thenReturn(mockStorageReference);
        when(mockStorageReference.child(NO_MATTER.equals(childName) ? anyString() : childName))
                .thenReturn(mockImageReference);

        when(mockImageReference.putBytes(any(byte[].class))).thenReturn(mockUploadTask);
    }

    @Test
    public void uploadGag_getsStorageInstanceFromFirebaseHelper() throws Exception {
        setFirebaseMocksForUploadToStorage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockFirebaseHelper).getStorageReference("gags");
    }

    @Test
    public void uploadGag_createsImageReferenceWithTimestamp() throws Exception {
        setFirebaseMocksForUploadToStorage(NO_MATTER, "1497873600000.jpg");

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockStorageReference).child("1497873600000.jpg");
    }

    @Test
    public void uploadGag_compressBitmapIntoByteArrayOutputStream() throws Exception {
        setFirebaseMocksForUploadToStorage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockBitmap).compress(
                eq(Bitmap.CompressFormat.JPEG), eq(100), any(ByteArrayOutputStream.class));
    }

    @Test
    public void uploadGag_putImageByteArrayToImageReference() throws Exception {
        setFirebaseMocksForUploadToStorage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockBitmap).compress(any(Bitmap.CompressFormat.class), anyInt(), byteArrayOutputStreamCaptor.capture());
        verify(mockImageReference).putBytes(byteArrayOutputStreamCaptor.getValue().toByteArray());
    }

    @Test
    public void uploadGag_addOnSuccessListenerToUploadTask() throws Exception {
        setFirebaseMocksForUploadToStorage(NO_MATTER, NO_MATTER);

        subject.uploadGag(mockBitmap, mockVoidServiceCallback);

        verify(mockUploadTask).addOnSuccessListener(Matchers.<OnSuccessListener<UploadTask.TaskSnapshot>>any());
    }

    private void assumeImageHasBeenUploaded(ArgumentCaptor<OnSuccessListener<UploadTask.TaskSnapshot>> onSuccessListenerUploadCaptor) {
        setFirebaseMocksForUploadToStorage(NO_MATTER, NO_MATTER);

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

        verify(mockChildReference).setValue(ImmutableMap.of("fileName", "1497873600000.jpg", "verified", false));
    }

    @Test
    public void uploadGag_runsSuccessCallback_afterAllTheProcedureIsCleared() throws Exception {
        assumeImageHasBeenUploaded(onSuccessListenerUploadCaptor);

        setFirebaseMocksForPuttingFileToDatabase(NO_MATTER);

        verify(mockVoidServiceCallback, never()).onSuccess(null);

        onSuccessListenerUploadCaptor.getValue().onSuccess(mock(UploadTask.TaskSnapshot.class));

        verify(mockVoidServiceCallback).onSuccess(null);
    }

    private void setMockFirebaseDatabase(String key) {
        when(mockFirebaseHelper.getDatabaseReference(anyString())).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.child(key)).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.child("verified")).thenReturn(mockVerifiedFieldReference);
    }

    @Test
    public void verifyGag_getsGagReferenceFromFirebaseHelper() throws Exception {
        setMockFirebaseDatabase(ANY_KEY);

        subject.verifyGag(createGag(ANY_KEY, ANY_FILE_NAME));

        verify(mockFirebaseHelper).getDatabaseReference("gags");
    }

    @Test
    public void verifyGag_getsChildByKey() throws Exception {
        setMockFirebaseDatabase("this_key");

        subject.verifyGag(createGag("this_key", ANY_FILE_NAME));

        verify(mockDatabaseReference).child("this_key");
    }

    @Test
    public void verifyGag_getsVerifiedFieldByChild() throws Exception {
        setMockFirebaseDatabase(ANY_KEY);

        subject.verifyGag(createGag(ANY_KEY, ANY_FILE_NAME));

        verify(mockDatabaseReference).child("verified");
    }

    @Test
    public void verifyGag_setsValueTrueOnVerifiedField() throws Exception {
        setMockFirebaseDatabase(ANY_KEY);

        subject.verifyGag(createGag(ANY_KEY, ANY_FILE_NAME));

        verify(mockVerifiedFieldReference).setValue(true);
    }

    private void setMockFirebaseStorage(String fileName) {
        when(mockFirebaseHelper.getStorageReference(anyString())).thenReturn(mockStorageReference);
        when(mockStorageReference.child(fileName)).thenReturn(mockStorageReference);
    }

    @Test
    public void rejectGag_getsStorageReferenceFromFirebaseHelper() throws Exception {
        setMockFirebaseStorage(ANY_FILE_NAME);
        setMockFirebaseDatabase(ANY_KEY);

        subject.rejectGag(createGag(ANY_KEY, ANY_FILE_NAME));

        verify(mockFirebaseHelper).getStorageReference("gags");
    }

    @Test
    public void rejectGag_getsChildWithFileName() throws Exception {
        setMockFirebaseStorage("delete_this.jpg");
        setMockFirebaseDatabase(ANY_KEY);


        subject.rejectGag(createGag(ANY_KEY, "delete_this.jpg"));

        verify(mockStorageReference).child("delete_this.jpg");
    }

    @Test
    public void rejectGag_deletesFileFromStorage() throws Exception {
        setMockFirebaseStorage(ANY_FILE_NAME);
        setMockFirebaseDatabase(ANY_KEY);

        subject.rejectGag(createGag(ANY_KEY, ANY_FILE_NAME));

        verify(mockStorageReference).delete();
    }

    @Test
    public void rejectGag_getsGagDatabaseReferenceFromFirebase() throws Exception {
        setMockFirebaseDatabase("delete_this");
        setMockFirebaseStorage(ANY_FILE_NAME);

        subject.rejectGag(createGag("delete_this", ANY_FILE_NAME));

        verify(mockFirebaseHelper).getDatabaseReference("gags");
    }

    @Test
    public void rejectGag_getsChildByKeyFromDatabaseReference() throws Exception {
        setMockFirebaseDatabase("delete_this");
        setMockFirebaseStorage(ANY_FILE_NAME);

        subject.rejectGag(createGag("delete_this", ANY_FILE_NAME));

        verify(mockDatabaseReference).child("delete_this");

    }

    @Test
    public void rejectGag_deletesGagFromDatabase() throws Exception {
        setMockFirebaseDatabase(ANY_KEY);
        setMockFirebaseStorage(ANY_FILE_NAME);

        subject.rejectGag(createGag(ANY_KEY, ANY_FILE_NAME));

        verify(mockDatabaseReference).removeValue();
    }

    @NonNull
    private DataSnapshot createMockDataSnapshotForJPGImages(int size) {
        DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);
        List<DataSnapshot> children = new ArrayList<>();

        for(int i = 0; i < size; ++i) {
            DataSnapshot mockChild = mock(DataSnapshot.class);
            when(mockChild.getKey()).thenReturn("key_" + String.valueOf(i));

            Gag gag = new Gag();
            gag.fileName = "filename_" + i + ".jpg";

            when(mockChild.getValue(Gag.class)).thenReturn(gag);

            children.add(mockChild);
        }

        when(mockDataSnapshot.getChildren()).thenReturn(children);

        return mockDataSnapshot;
    }

    private Gag createGag(String key, String fileName) {
        Gag gag = new Gag();
        gag.key = key;
        gag.fileName = fileName;

        return gag;
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

    private List<Gag> createGagList(String... fileNames) {
        List<Gag> result = new ArrayList<>();
        int i = 0;
        for(String fileName : fileNames) {
            Gag gag = new Gag();
            gag.key = "key_" + String.valueOf(i++);
            gag.fileName = fileName;
            result.add(gag);
        }

        return result;
    }
}