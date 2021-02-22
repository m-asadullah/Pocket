package com.cubixos.pocket.adapters;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.cubixos.pocket.accessories.misc.AppPermissionManager;
import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.misc.FileExist;
import com.cubixos.pocket.accessories.misc.FileLoad;
import com.cubixos.pocket.accessories.misc.FileMetaData;
import com.cubixos.pocket.accessories.misc.TimeSet;
import com.cubixos.pocket.models.Botany;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotanyAdapter extends FirestoreRecyclerAdapter<Botany, BotanyAdapter.MathHolder> {

    public BotanyAdapter(@NonNull FirestoreRecyclerOptions<Botany> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MathHolder holder, int position, @NonNull Botany subjectmodel) {
        Context context = holder.itemView.getContext();
        Context appcontext = holder.itemView.getContext().getApplicationContext();
        holder.textViewDate.setText(TimeSet.timeDateFormat(subjectmodel.getTimeStamp(), "dd MMM, YYYY"));
        holder.textViewTime.setText(TimeSet.timeDateFormat(subjectmodel.getTimeStamp(), "h:mm a"));
        holder.textViewFilename.setText(subjectmodel.getFileName());
        holder.textViewTitle.setText(subjectmodel.getTitle());
        holder.textViewSize.setText(FileMetaData.humanReadableByteCountSI(subjectmodel.getFileSize()));
        holder.textViewType.setText(subjectmodel.getFileExtension());
        //OnClick item
        holder.relativeLayoutSubject.setOnClickListener(v -> {
            if (FileExist.fileExists(context, subjectmodel.getFileName())) {
                //
                holder.relativeLayoutDownload.setVisibility(View.GONE);
                holder.imageViewDownload.setVisibility(View.GONE);
                //file type icon
                switch (subjectmodel.getFileExtension()) {
                    case "jpg":
                    case "JPG":
                    case "png":
                    case "PNG":
                    case "jpeg":
                    case "JPEG":
                        holder.relativeLayoutPagesDuration.setVisibility(View.GONE);
                        holder.textViewPagesDuration.setVisibility(View.GONE);
                        if (FileExist.fileExists(context, subjectmodel.getFileName())) {
                            FileLoad.loadLocally(context, subjectmodel.getFileName(), holder.imageViewThumbnail);
                        } else if (subjectmodel.getFileThumbnailUrl() == null) {
                            Picasso.get()
                                    .load("http://bit.ly/32hX6xX-blank-image")
                                    .placeholder(R.drawable.empty_profile_pic)
                                    .error(R.drawable.ic_warning_sign_board)
                                    .into(holder.imageViewThumbnail);
                        } else if (subjectmodel.getFileThumbnailUrl().equals("")) {
                            Picasso.get()
                                    .load("http://bit.ly/32hX6xX-blank-image")
                                    .placeholder(R.drawable.empty_profile_pic)
                                    .error(R.drawable.ic_warning_sign_board)
                                    .into(holder.imageViewThumbnail);
                        } else {
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(holder.imageViewThumbnail);
                            } else {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(holder.imageViewThumbnail);
                            }
                        }
                        break;
                    case "mp4":
                    case "MP4":
                        if (subjectmodel.getFilePagesDuration() != 0) {
                            holder.textViewPagesDuration.setText(FileMetaData.formatMilliSecond(subjectmodel.getFilePagesDuration()));
                        } else {
                            holder.textViewPagesDuration.setVisibility(View.GONE);
                        }
                        if (FileExist.fileExists(context, subjectmodel.getFileName())) {
                            FileLoad.loadLocally(context, subjectmodel.getFileName(), holder.imageViewThumbnail);
                        } else if (subjectmodel.getFileThumbnailUrl() == null) {
                            Picasso.get()
                                    .load("http://bit.ly/32hX6xX-blank-image")
                                    .placeholder(R.drawable.empty_profile_pic)
                                    .error(R.drawable.ic_warning_sign_board)
                                    .into(holder.imageViewThumbnail);
                        } else if (subjectmodel.getFileThumbnailUrl().equals("")) {
                            Picasso.get()
                                    .load("http://bit.ly/32hX6xX-blank-image")
                                    .placeholder(R.drawable.empty_profile_pic)
                                    .error(R.drawable.ic_warning_sign_board)
                                    .into(holder.imageViewThumbnail);
                        } else {
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(holder.imageViewThumbnail);
                            } else {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(holder.imageViewThumbnail);
                            }
                        }
                        //PDF
                        break;
                    case "pdf":
                    case "PDF":
                        if (subjectmodel.getFilePagesDuration() != 0) {
                            String stringPages = String.valueOf(subjectmodel.getFilePagesDuration());
                            holder.textViewPagesDuration.setText("Pages: " + stringPages);
                        } else {
                            holder.textViewPagesDuration.setVisibility(View.GONE);
                        }
                        if (subjectmodel.getFileThumbnailUrl() == null) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_pdf);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else if (subjectmodel.getFileThumbnailUrl().equals("")) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_pdf);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else {
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(holder.imageViewThumbnail);
                            } else {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(holder.imageViewThumbnail);
                            }
                        }
                        //Document Word
                        break;
                    case "doc":
                    case "DOC":
                    case "docx":
                    case "DOCX":
                        if (subjectmodel.getFilePagesDuration() != 0) {
                            String stringPages = String.valueOf(subjectmodel.getFilePagesDuration());
                            holder.textViewPagesDuration.setText("Pages: " + stringPages);
                        } else {
                            holder.textViewPagesDuration.setVisibility(View.GONE);
                        }
                        if (subjectmodel.getFileThumbnailUrl() == null) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_docs_logo);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else if (subjectmodel.getFileThumbnailUrl().equals("")) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_docs_logo);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else {
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(holder.imageViewThumbnail);
                            } else {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(holder.imageViewThumbnail);
                            }
                        }
                        //Document Excel
                        break;
                    case "xls":
                    case "XLS":
                    case "xlsx":
                    case "XLXS":
                        if (subjectmodel.getFilePagesDuration() != 0) {
                            String stringPages = String.valueOf(subjectmodel.getFilePagesDuration());
                            holder.textViewPagesDuration.setText("Pages: " + stringPages);
                        } else {
                            holder.textViewPagesDuration.setVisibility(View.GONE);
                        }
                        if (subjectmodel.getFileThumbnailUrl() == null) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_sheets_logo);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else if (subjectmodel.getFileThumbnailUrl().equals("")) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_sheets_logo);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else {
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(holder.imageViewThumbnail);
                            } else {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(holder.imageViewThumbnail);
                            }
                        }
                        //Document Powerpoint
                        break;
                    case "ppt":
                    case "PPT":
                    case "pptx":
                    case "PPTX":
                        if (subjectmodel.getFilePagesDuration() != 0) {
                            String stringPages = String.valueOf(subjectmodel.getFilePagesDuration());
                            holder.textViewPagesDuration.setText("Pages: " + stringPages);
                        } else {
                            holder.textViewPagesDuration.setVisibility(View.GONE);
                        }
                        if (subjectmodel.getFileThumbnailUrl() == null) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_slides_logo);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else if (subjectmodel.getFileThumbnailUrl().equals("")) {
                            holder.imageViewTypeIcon.setImageResource(R.drawable.ic_google_slides_logo);
                            holder.imageViewTypeIcon.setVisibility(View.VISIBLE);
                        } else {
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(holder.imageViewThumbnail);
                            } else {
                                Picasso.get()
                                        .load(subjectmodel.getFileThumbnailUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(holder.imageViewThumbnail);
                            }
                        }
                        break;
                }
                //
                String filepath = Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name)+"/" + subjectmodel.getFileName();
                File file = new File(filepath);
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (subjectmodel.getFileMIMEType() != null) {
                    intent.setDataAndType(uri, subjectmodel.getFileMIMEType());
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intentChooser = Intent.createChooser(intent, "Open with");
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intentChooser);
                }else {
                    Toast.makeText(context,"Install App to view these type of files.",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(appcontext,"Download it before open.",Toast.LENGTH_SHORT).show();
                holder.relativeLayoutDownload.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
                holder.imageViewDownload.setVisibility(View.VISIBLE);
                //
                holder.imageViewDownload.setOnClickListener(v1 -> {
                    Context context12 = holder.itemView.getContext();
                    if (CheckInternet.isNetwork(context12)) {
                        holder.imageViewDownload.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.VISIBLE); // if now crash then hide this line
                        //
                        DownloadManager manager = (DownloadManager) context12.getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(subjectmodel.getFileUrl()));
                        request.setTitle(subjectmodel.getTitle());
                        request.setDescription(subjectmodel.getFileName());
                        //request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory()+File.separator+"Press/"+pressmodel.getBookName(), pressmodel.getFileName());
                        request.setDestinationInExternalPublicDir(File.separator + context12.getString(R.string.app_name) + "/", subjectmodel.getFileName());
                        //request.getFileDir(File.separator+"Press/", pressmodel.getFileName());
                        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filesmodel.getFileName());
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setVisibleInDownloadsUi(true);
                        manager.enqueue(request);
                        //
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext())
                                .setIcon(R.drawable.ic_twotone_perm_scan_wifi_24)
                                .setTitle("Network Error")
                                .setMessage("Internet Connection is not available. Check your Data connection or WiFi")
                                .setCancelable(true)
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    Context context1 = holder.itemView.getContext();
                                    WifiManager wifiManager = (WifiManager) context1.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                    wifiManager.setWifiEnabled(true);
                                    final Intent intentWifi = new Intent(Intent.ACTION_MAIN, null);
                                    intentWifi.addCategory(Intent.CATEGORY_LAUNCHER);
                                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                                    intentWifi.setComponent(cn);
                                    intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context1.startActivity(intentWifi);
                                })
                                //set negative button
                                .setNegativeButton("Cancel", (dialogInterface, i) -> Toast.makeText(holder.itemView.getContext(),"Canceled",Toast.LENGTH_LONG).show())
                                .show();
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public MathHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_botany, parent, false);
        return new MathHolder(v);
    }

    static class MathHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewFilename;
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewSize;
        TextView textViewType;
        TextView textViewPagesDuration;

        ImageView imageViewThumbnail;
        ImageView imageViewTypeIcon;
        RelativeLayout relativeLayoutSubject;
        RelativeLayout relativeLayoutDownload;
        RelativeLayout relativeLayoutPagesDuration;
        ProgressBar progressBar;
        ImageView imageViewDownload;

        public MathHolder(View itemView) {
            super(itemView);
            relativeLayoutSubject = itemView.findViewById(R.id.container_item_subject);
            relativeLayoutDownload = itemView.findViewById(R.id.relative_layout_download_item_subject);
            imageViewDownload = itemView.findViewById(R.id.image_view_download_item_subject);
            progressBar = itemView.findViewById(R.id.progress_bar_item_subject);
            imageViewThumbnail = itemView.findViewById(R.id.image_view_thumbnail_subject_item);
            imageViewTypeIcon = itemView.findViewById(R.id.image_view_typeicon_subject_item);
            textViewFilename = itemView.findViewById(R.id.text_view_filenameext_item_subject);
            textViewTitle = itemView.findViewById(R.id.text_view_title_item_subject);
            textViewDate = itemView.findViewById(R.id.text_view_date_item_subject);
            textViewTime = itemView.findViewById(R.id.text_view_time_item_subject);
            textViewSize = itemView.findViewById(R.id.text_view_size_item_subject);
            textViewType = itemView.findViewById(R.id.text_view_type_item_subject);
            textViewPagesDuration = itemView.findViewById(R.id.text_view_pages_duration_item_subject);
            relativeLayoutPagesDuration = itemView.findViewById(R.id.relative_layout_pages_duration_item_subject);
        }
    }
}
