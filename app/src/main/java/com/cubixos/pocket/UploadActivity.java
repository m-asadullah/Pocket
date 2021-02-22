package com.cubixos.pocket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cubixos.pocket.accessories.misc.AppPermissionManager;
import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.accessories.misc.TimeSet;
import com.cubixos.pocket.accessories.misc.FileMetaData;
import com.cubixos.pocket.accessories.notifications.NotificationSuccessUpload;
import com.cubixos.pocket.models.Pocket;
import com.cubixos.pocket.models.Category;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    private final int PICK_FILE_REQUEST = 81;
    //
    Activity activity;
    Context context;
    //
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DocumentReference documentReference;
    CollectionReference collectionReference;
    //
    String stringCurrentUserID;
    String stringUserPhoneNumber;
    //
    String stringFilename;
    String stringFileExtenstion;
    String stringFileMIMEType;
    long longFileSize;
    long longFilePagesDuration;
    //
    Uri uriFilePathPicked;
    //No Uri for Text
    Uri uriFilePathIntentFile;
    Uri uriFilePathThumbnailPDF;
    String stringTimeDate;
    String stringTimeStamp;
    //
    TextView textViewSubject;
    TextView textViewExtension;
    TextView textViewMIMEType;
    TextView textViewSize;
    TextView textViewPagesResolutionTitle;
    TextView textViewPagesResolution;
    TextView textViewFolder;
    TextView textViewFileName;
    //
    ImageButton imageButtonAttachFile;
    //
    ImageView imageViewThumbnail;
    ImageView imageViewFileTypeIconThumbnail;
    ImageView imageViewFileTypeIconAttach;
    //
    private Spinner spinner;
    //
    EditText editTextTitle;
    EditText editTextDescription;
    //
    Button buttonReset;
    Button buttonUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        //
        activity = UploadActivity.this;
        context = getApplicationContext();
        //
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            stringCurrentUserID = firebaseAuth.getCurrentUser().getUid();
            stringUserPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        }
        //
        spinner = findViewById(R.id.spinner_subjects);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Category.categoryFront));
        //
        textViewSubject = findViewById(R.id.text_view_subject_upload);
        textViewExtension = findViewById(R.id.text_view_extension_file);
        textViewMIMEType = findViewById(R.id.text_view_mime_type_file);
        textViewSize = findViewById(R.id.text_view_size_file_upload);
        textViewPagesResolutionTitle = findViewById(R.id.text_view_pages_resolution_title);
        textViewPagesResolution = findViewById(R.id.text_view_pages_resolution_file_upload);
        textViewFolder = findViewById(R.id.text_view_folder_file_upload);
        textViewFileName = findViewById(R.id.text_view_filenameext_file_upload);
        //
        imageButtonAttachFile = findViewById(R.id.image_button_attach_file_upload);
        //
        imageViewThumbnail = findViewById(R.id.image_view_thumbnail_file_upload);
        imageViewFileTypeIconThumbnail = findViewById(R.id.image_view_file_type_icon_thumbnail);
        //
        editTextTitle = findViewById(R.id.edit_text_title_upload);
        editTextDescription = findViewById(R.id.edit_text_description_upload);
        //
        textViewFileName = findViewById(R.id.text_view_filenameext_file_upload);
        //
        imageViewFileTypeIconAttach = findViewById(R.id.image_view_file_upload);
        textViewSize = findViewById(R.id.text_view_size_file_upload);
        //
        buttonReset = findViewById(R.id.button_reset);
        buttonUpload= findViewById(R.id.button_upload);
        //
        String stringSubj = Category.categoryFront[spinner.getSelectedItemPosition()];
        textViewSubject.setText(stringSubj);
        //
        imageButtonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose file
                chooseFile();
            }
        });
        imageViewFileTypeIconAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Choose file
                chooseFile();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Clear all filling
                resetData();
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPermissionManager.permissionStorage(activity, context);
                //Upload file to FireStorage - retrieve getDownloadUrl
                //Upload retrieve-getDownloadUrl + title - description
                if (CheckInternet.isNetwork(getApplicationContext())){
                    if (uriFilePathPicked != null){
                        uploadData(uriFilePathPicked);
                    }else if (uriFilePathIntentFile != null){
                        uploadData(uriFilePathIntentFile);
                    }else {
                        Toast.makeText(context,"Error: No file detected",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"Error: Check your Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //
        AppPermissionManager.permissionStorage(activity, context);
        //
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            AppPermissionManager.permissionStorage(activity, context);
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("video/")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("audio/")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("application/pdf")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("application/vnd.ms-excel")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("application/vnd.ms-powerpoint")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("application/msword")) {
                handleSendFile(intent); // Handle single image being sent
            } else if (type.startsWith("application/rtf")) {
                handleSendFile(intent); // Handle single image being sent
            } else {
                Toast.makeText(activity, "File Not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle other intents, such as being started from the home screen
            Toast.makeText(activity, "Only single file supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            editTextTitle.setText(sharedText);
        } else {
            Toast.makeText(activity, "Only Text not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSendFile(Intent intent) {
        Uri uriFilePathIntent = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uriFilePathIntent != null) {
            // Update UI to reflect image being shared
            onResultProceed(uriFilePathIntent);
            //Uri
            uriFilePathIntentFile = uriFilePathIntent;
        }else{
            Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    //Reset Clear all Data
    private void resetData() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        textViewFileName.setText("");
        textViewSize.setText("");
    }

    //File Choose
    private void chooseFile() {
        AppPermissionManager.permissionStorage(activity, context);
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentData) {
        AppPermissionManager.permissionStorage(activity, context);
        super.onActivityResult(requestCode, resultCode, intentData);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && intentData != null && intentData.getData() != null) {
            Uri uriFilePathPick = intentData.getData();
            onResultProceed(uriFilePathPick);
            //Uri
            uriFilePathPicked = uriFilePathPick;
        }
    }
    // After file picked / received to app
    private void onResultProceed(Uri uriFilePathIntra) {
        //uriFileIN = uriFilePath;
        stringTimeDate = TimeSet.timeStampDate();
        stringTimeStamp = TimeSet.timeStampString() ;
        Toast.makeText(context, uriFilePathIntra.toString() +"\n"+ uriFilePathIntra.getPath() + "\n" + FileMetaData.stringFilePathB(context, uriFilePathIntra) , Toast.LENGTH_SHORT).show();
        //
        stringFilename = FileMetaData.pickedFileName(context, uriFilePathIntra);
        stringFileExtenstion = FileMetaData.pickedFileExtension(context, uriFilePathIntra);
        stringFileMIMEType = FileMetaData.pickedFileMimeType(context, uriFilePathIntra);
        //
        longFileSize = FileMetaData.pickedFileSize(context, uriFilePathIntra);
        //extra line below
        editTextDescription.setText(FileMetaData.pickedFileSize(context, uriFilePathIntra) +"\n"+ FileMetaData.pickedFileMimeType(context, uriFilePathIntra));
        //for Filename.ext set and edit as
        String stringFilenameForEdit = FileMetaData.pickedFileName(context, uriFilePathIntra);
        //stringFilenameEdit.replace("[^a-zA-Z0-9]", " ");
        String stringFilenameEdited = stringFilenameForEdit.replaceAll("[-+._^:,]", " ");
        //to Show in front
        editTextTitle.setText(stringFilenameEdited.trim());
        //file info
        textViewFileName.setText(FileMetaData.pickedFileName(context, uriFilePathIntra));
        textViewSize.setText(FileMetaData.humanReadableByteCountSI(FileMetaData.pickedFileSize(context, uriFilePathIntra)));
        textViewExtension.setText(FileMetaData.pickedFileExtension(context, uriFilePathIntra));
        textViewMIMEType.setText(FileMetaData.pickedFileMimeType(context, uriFilePathIntra));
        textViewFolder.setText(FileMetaData.pickedFileFolder(context, uriFilePathIntra));
        //
        if(stringFileExtenstion.equals("jpg")||stringFileExtenstion.equals("JPG")||stringFileExtenstion.equals("png")||stringFileExtenstion.equals("PNG")||stringFileExtenstion.equals("jpeg")||stringFileExtenstion.equals("JPEG")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_twotone_image_24);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_twotone_image_24);
            textViewPagesResolutionTitle.setText(getString(R.string.resolution));
            textViewPagesResolution.setText(FileMetaData.pickedFileImageResolution(context, uriFilePathIntra));
            Picasso.get()
                    .load(uriFilePathIntra)
                    .placeholder(R.drawable.empty_profile_pic)
                    .error(R.drawable.ic_warning_sign_board)
                    .into(imageViewThumbnail);
        }else if (stringFileExtenstion.equals("pdf")||stringFileExtenstion.equals("PDF")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_google_pdf);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_google_pdf);
            textViewPagesResolutionTitle.setText(getString(R.string.pages));
            try {
                longFilePagesDuration = FileMetaData.pickedFilePDFPages(uriFilePathIntra);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textViewPagesResolution.setText(String.valueOf(longFilePagesDuration));//page numbers
            uriFilePathThumbnailPDF = FileMetaData.imageUri(context, FileMetaData.generateImageFromPdf(activity, context, uriFilePathIntra));
            Picasso.get()
                    .load(uriFilePathThumbnailPDF)
                    .placeholder(R.drawable.empty_profile_pic)
                    .error(R.drawable.ic_warning_sign_board)
                    .into(imageViewThumbnail);
        }else if (stringFileExtenstion.equals("doc")||stringFileExtenstion.equals("DOC")||stringFileExtenstion.equals("docx")||stringFileExtenstion.equals("DOCX")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_google_docs_logo);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_google_docs_logo);
            textViewPagesResolutionTitle.setText(getString(R.string.pages));
            textViewPagesResolution.setText(String.valueOf(longFilePagesDuration));//page numbers
        }else if (stringFileExtenstion.equals("xls")||stringFileExtenstion.equals("XLS")||stringFileExtenstion.equals("xlsx")||stringFileExtenstion.equals("XLSX")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_google_sheets_logo);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_google_sheets_logo);
            textViewPagesResolutionTitle.setText(getString(R.string.pages));
            textViewPagesResolution.setText("0");//page numbers
        }else if (stringFileExtenstion.equals("ppt")||stringFileExtenstion.equals("PPT")||stringFileExtenstion.equals("pptx")||stringFileExtenstion.equals("PPTX")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_google_sheets_logo);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_google_sheets_logo);
            textViewPagesResolutionTitle.setText(getString(R.string.pages));
            textViewPagesResolution.setText("0");//page numbers
        }else if (stringFileExtenstion.equals("txt")||stringFileExtenstion.equals("TXT")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_twotone_file_24);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_twotone_file_24);
            textViewPagesResolutionTitle.setVisibility(View.GONE);
            textViewPagesResolution.setVisibility(View.GONE);
        }else if (stringFileExtenstion.equals("mp4")||stringFileExtenstion.equals("MP4")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_twotone_file_24);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_twotone_file_24);
            textViewPagesResolutionTitle.setText(getString(R.string.duration));
            longFilePagesDuration = FileMetaData.longDurationAudioVideo(context ,uriFilePathIntra);
            textViewPagesResolution.setText(FileMetaData.formatMilliSecond(longFilePagesDuration));
        }else if (stringFileExtenstion.equals("mp3")||stringFileExtenstion.equals("MP3")){
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_twotone_file_24);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_twotone_file_24);
            textViewPagesResolutionTitle.setText(getString(R.string.duration));
            longFilePagesDuration = FileMetaData.longDurationAudioVideo(context ,uriFilePathIntra);
            textViewPagesResolution.setText(FileMetaData.formatMilliSecond(longFilePagesDuration));
        }else {
            imageViewFileTypeIconAttach.setImageResource(R.drawable.ic_twotone_file_24);
            imageViewFileTypeIconThumbnail.setImageResource(R.drawable.ic_twotone_file_24);
            textViewPagesResolutionTitle.setText(getString(R.string.duration));
            Toast.makeText(activity, "Only valid file proceed further", Toast.LENGTH_SHORT).show();
        }
        //end file info
    }

    private void uploadData(Uri uriUploadFile) {
        String subjectnameFront = Category.categoryFront[spinner.getSelectedItemPosition()];//
        String subjectname = Category.categoryBack[spinner.getSelectedItemPosition()];
        String subjectdescription = editTextDescription.getText().toString().trim();
        String subjecttitle = editTextTitle.getText().toString().trim();
        if (subjectname.isEmpty()||(subjectname.equals("NotSelected"))){
            Toast.makeText(context,"Select subject", Toast.LENGTH_SHORT).show();
            return;
        }
        if (subjecttitle.isEmpty()||(subjecttitle.equals("")||(subjecttitle.equals(" ")))){
            editTextTitle.setError("File Name without extension");
            editTextTitle.requestFocus();
            return;
        }
        //
        if (uriUploadFile != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            //
            documentReference = firebaseFirestore.collection(subjectname).document(stringTimeDate);
            storageReference = firebaseStorage.getReference("Users/").child(stringCurrentUserID);
            final StorageReference ref = storageReference.child(stringTimeDate + "." + stringFileExtenstion);
            //
            //UploadTask uploadTask = storageReference.putFile(uriUploadFile);
            storageReference.putFile(uriUploadFile).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setProgress((int) progress);
                    progressDialog.setMessage("Verifying " + (int) progress + "%");
                    //Log.d(TAG, "Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(activity, "Upload is paused", Toast.LENGTH_SHORT).show();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(activity, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(activity, "Failed\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            long longFilePgsDur = 0;
                            String stringFileThumbType = null;
                            if (stringFileExtenstion.equals("jpg") || stringFileExtenstion.equals("JPG") || stringFileExtenstion.equals("png") || stringFileExtenstion.equals("PNG") || stringFileExtenstion.equals("jpeg") || stringFileExtenstion.equals("JPEG")) {
                                stringFileThumbType = uri.toString();
                                longFilePgsDur = 0;
                            } else if (stringFileExtenstion.equals("pdf") || stringFileExtenstion.equals("PDF")) {
                                //PDF Thumbnail in png upload and getUri link
                                stringFileThumbType = null;
                                longFilePgsDur = longFilePagesDuration;
                                //stringFileThumbType = uri.toString();
                            } else {
                                stringFileThumbType = null;
                            }
                            //
                            documentReference.set(new Pocket("allow","","","","",""));
                            NotificationSuccessUpload.notificationFileUpload(activity, context, subjecttitle, subjectdescription, stringFilename, FileMetaData.humanReadableByteCountSI(FileMetaData.pickedFileSize(context, uriUploadFile)));
                            if (firebaseUser.getEmail().equals("asadknaam@gmail.com") || firebaseUser.getEmail().equals("cubixos.com@gmail.com")) {
                                Toast.makeText(context, stringTimeStamp + "\n" + stringTimeDate + "\n" + subjecttitle + "\n" + subjectdescription + "\n" + FileMetaData.humanReadableByteCountSI(FileMetaData.pickedFileSize(context, uriUploadFile)) + "\n" + stringFilename + "\n" + stringFileExtenstion + "\n" + uri.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                            editTextTitle.setText("");
                            editTextDescription.setText("");
                            textViewSize.setText("Uploaded Successfully");
                            upThumbnail();
                        }
                    });
                }
            });
            //
        } else {
            Toast.makeText(context, stringTimeDate+"\n"+subjecttitle+"\n"+subjectdescription+"\n"+"Data will not uploaded without file",Toast.LENGTH_LONG).show();
        }
    }

    private void upThumbnail(){
        if (uriFilePathThumbnailPDF != null){
            final ProgressDialog progressDialog2 = new ProgressDialog(context);
            progressDialog2.setMessage("Verifying...");
            progressDialog2.setCancelable(false);
            progressDialog2.show();
            final StorageReference ref2 = storageReference.child("Thumbnails").child(FileMetaData.pickedFileName(context, uriFilePathThumbnailPDF));
            //UploadTask uploadTask2 = ref2.putFile(uriFilePathThumbnailPDF);
            ref2.putFile(uriFilePathThumbnailPDF).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri2) {
                            documentReference.update("fileThumbnailUrl", uri2.toString());
                        }
                    });
                    progressDialog2.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog2.setMessage("Verifying " + (int) progress + "%");
                }
            });
        }
    }
}