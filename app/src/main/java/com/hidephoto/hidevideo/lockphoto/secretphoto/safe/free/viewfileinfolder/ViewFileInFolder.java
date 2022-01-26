package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.FileUltils;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.LoadingDialog;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.MainActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.ImportPhotos;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.PhotoAdapter;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importvideo.ImportVideos;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.introduce.IntrodutionActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.dialogfolder.FolderToMove;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.dialogfolder.FolderToMoveAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ViewFileInFolder extends AppCompatActivity {

    private TextView nameFolder, btnAddFile;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAdd;
    private RelativeLayout layoutParent;
    private ImageView iconSelection, selectAll, closeSelect;

    private RelativeLayout layoutViewImage;
    private LinearLayout btnUnlock, btnShare, btnDelete, btnInfo;
    private LinearLayout imageOptions_1, btnUnlock_1, btnShare_1, btnDelete_1, btnMove;
    //    private ArrayList<FileInFolder> listFileInFolderSelected = new ArrayList<FileInFolder>();
//    private ImageView viewImage;
    private ArrayList<FileInFolder> listImageSelected = new ArrayList<FileInFolder>();
    private ArrayList<FolderToMove> listFolderToMove = new ArrayList<FolderToMove>();
    private PhotoView viewImage;
    String name;
    boolean type;

    private RecyclerView rcvFile;
    private FileInFolderAdapter fileInFolderAdapter;
    private ArrayList<FileInFolder> listFileInFolder = new ArrayList<FileInFolder>();
    protected boolean _active = true;
    protected int _splashTime = 10;

    int statusApp = 0, positionPlayVideo;
    private String pathAsyncTask;

    LoadingDialog loadingDialog = new LoadingDialog(ViewFileInFolder.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this, R.color.button), true, false, ViewFileInFolder.this);
        setContentView(R.layout.activity_view_file_in_folder);

        addControls();
        addEvents();

    }

    private void addEvents() {
        fileInFolderAdapter.setOnItemClickListener(new FileInFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws FileNotFoundException {
                if (!type) {
                    positionPlayVideo = position;
                    MyTask2 myTask2 = new MyTask2();
                    myTask2.execute();
                } else {
                    layoutParent.setVisibility(View.GONE);
                    layoutViewImage.setVisibility(View.VISIBLE);
                    iconSelection.setVisibility(View.GONE);

                    Bitmap bitmap = BitmapFactory.decodeFile(listFileInFolder.get(position).getPathFileInFolder());
//                    viewImage.setImageBitmap(bitmap);
                    viewImage.setImageBitmap(FileUltils.modifyOrientation(ViewFileInFolder.this, bitmap, listFileInFolder.get(position).getPathFileInFolder()));

//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    Uri uri = getImageContentUri(ViewFileInFolder.this, new File(listFileInFolder.get(position).getPathFileInFolder()));
//                    BitmapFactory.decodeStream(ViewFileInFolder.this.getContentResolver().openInputStream(uri), null, options);
//                    int h = options.outHeight;
//                    int w = options.outWidth;

                    btnUnlock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File fileIn = new File(listFileInFolder.get(position).getPathFileInFolder());
                            String imagePath = fileIn.getName().replaceAll("&", "/").substring(0, fileIn.getName().lastIndexOf("&"));
                            String imageName = fileIn.getName().substring(fileIn.getName().lastIndexOf("&") + 1, fileIn.getName().lastIndexOf("."));
                            File fileOut = new File(imagePath, imageName);
                            try {
                                FileInputStream fileInputStream = new FileInputStream(fileIn);
                                FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
                                copyStream(fileInputStream, fileOutputStream);
                                fileOutputStream.close();
                                fileInputStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fileIn.delete();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileIn)));
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileOut)));
                            Toast.makeText(ViewFileInFolder.this, R.string.unlockSuccessfully, Toast.LENGTH_SHORT).show();
                            layoutViewImage.setVisibility(View.GONE);
                            fileInFolderAdapter.setData(getAllFile());
                            checkListFile();
                            layoutParent.setVisibility(View.VISIBLE);
                        }
                    });

                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                        share_bitMap_to_Apps(listFileInFolder.get(position).getPathFileInFolder());
//                            shareImage(listFileInFolder.get(position).getPathFileInFolder());
                            pathAsyncTask = listFileInFolder.get(position).getPathFileInFolder();
                            MyTask myTask = new MyTask();
                            myTask.execute();
                        }
                    });

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewFileInFolder.this);
                            builder.setTitle(R.string.delete)
                                    .setMessage(R.string.deleteFile)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            File file = new File(listFileInFolder.get(position).getPathFileInFolder());
                                            file.delete();
                                            layoutViewImage.setVisibility(View.GONE);
                                            fileInFolderAdapter.setData(getAllFile());
                                            checkListFile();
                                            layoutParent.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
                        }
                    });

                    btnInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
//                            Uri uri = getImageContentUri(ViewFileInFolder.this, new File(listFileInFolder.get(position).getPathFileInFolder()));
//                            Uri uri1 = Uri.parse(listFileInFolder.get(position).getPathFileInFolder());
//                            BitmapFactory.decodeStream(ViewFileInFolder.this.getContentResolver().openInputStream(uri1), null, options);
                            BitmapFactory.decodeFile(listFileInFolder.get(position).getPathFileInFolder(), options);
                            int h = options.outHeight;
                            int w = options.outWidth;
                            openBottomSheetDialog(listFileInFolder.get(position).getPathFileInFolder(), h, w);
                        }
                    });
                }
            }
        });

        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type) {
                    statusApp = 1;
                    Intent intent1 = new Intent(ViewFileInFolder.this, ImportPhotos.class);
                    intent1.putExtra("FOLDER_NAME_1", name);
                    startActivity(intent1);
                } else {
                    statusApp = 1;
                    Intent intent2 = new Intent(ViewFileInFolder.this, ImportVideos.class);
                    intent2.putExtra("FOLDER_NAME_2", name);
                    startActivity(intent2);
                }
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type) {
                    statusApp = 1;
                    Intent intent1 = new Intent(ViewFileInFolder.this, ImportPhotos.class);
                    intent1.putExtra("FOLDER_NAME_1", name);
                    startActivity(intent1);
                } else {
                    statusApp = 1;
                    Intent intent2 = new Intent(ViewFileInFolder.this, ImportVideos.class);
                    intent2.putExtra("FOLDER_NAME_2", name);
                    startActivity(intent2);
                }
            }
        });

        fileInFolderAdapter.setOnItemClickListener_1(new FileInFolderAdapter.OnItemClickListener_1() {
            @Override
            public void onItemClick_1(int position) throws FileNotFoundException {

                listImageSelected = fileInFolderAdapter.getSelected();
                if (!listFileInFolder.get(position).isFileChecked()) {
                    listImageSelected.add(listFileInFolder.get(position));
                } else {
                    for (int i = 0; i < listImageSelected.size(); i++) {
                        if (listImageSelected.get(i).getPathFileInFolder().equalsIgnoreCase(listFileInFolder.get(position).getPathFileInFolder()))
                            listImageSelected.remove(i);
                    }
                }
                nameFolder.setText(listImageSelected.size() + " " + getString(R.string.selected));
                fileInFolderAdapter.setData(listFileInFolder);
                if (listImageSelected.size() <= 1) {
                    btnShare_1.setVisibility(View.VISIBLE);
                } else {
                    btnShare_1.setVisibility(View.GONE);
                }
            }
        });

        fileInFolderAdapter.setOnItemClickListener_2(new FileInFolderAdapter.OnItemClickListener_2() {
            @Override
            public void onItemClick_2(int position) throws FileNotFoundException {
//                nameFolder.setText("0 selected");
                fileInFolderAdapter.openSelection();
                iconSelection.setVisibility(View.GONE);
                selectAll.setVisibility(View.VISIBLE);
                closeSelect.setVisibility(View.VISIBLE);
                imageOptions_1.setVisibility(View.VISIBLE);
                fabAdd.setVisibility(View.GONE);
                fileInFolderAdapter.selectFile(listFileInFolder.get(position));
                fileInFolderAdapter.setData(listFileInFolder);
                listImageSelected = fileInFolderAdapter.getSelected();
                nameFolder.setText(listImageSelected.size() + " " + getString(R.string.selected));
            }
        });

        btnShare_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileInFolderAdapter.getSelected().size() == 0) {
                    if (!type) {
                        Toast.makeText(ViewFileInFolder.this, R.string.noVideoSelected, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(ViewFileInFolder.this, R.string.noImageSelected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
//                    shareImage(fileInFolderAdapter.getSelected().get(0).getPathFileInFolder());
                    pathAsyncTask = fileInFolderAdapter.getSelected().get(0).getPathFileInFolder();
                    MyTask myTask = new MyTask();
                    myTask.execute();
                }
            }
        });

        btnDelete_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FileInFolder> list = fileInFolderAdapter.getSelected();
                if (list.size() == 0) {
                    if (!type) {
                        Toast.makeText(ViewFileInFolder.this, R.string.noVideoSelected, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(ViewFileInFolder.this, R.string.noImageSelected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewFileInFolder.this);
                builder.setTitle(R.string.delete)
                        .setMessage(R.string.deleteFile)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (list.size() == 0) {
                                    Toast.makeText(ViewFileInFolder.this, R.string.noImageSelected, Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    for (int i = 0; i < list.size(); i++) {
                                        File file = new File(list.get(i).getPathFileInFolder());
                                        file.delete();
                                    }
                                }
                                fileInFolderAdapter.setData(getAllFile());
                                fileInFolderAdapter.unSelectAll();
                                nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
                                fileInFolderAdapter.closeSelection();
                                iconSelection.setVisibility(View.VISIBLE);
                                selectAll.setVisibility(View.GONE);
                                closeSelect.setVisibility(View.GONE);
                                imageOptions_1.setVisibility(View.GONE);
                                checkListFile();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03A9F4"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03A9F4"));
            }
        });

        btnUnlock_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FileInFolder> list = fileInFolderAdapter.getSelected();
                if (list.size() == 0) {
                    if (!type) {
                        Toast.makeText(ViewFileInFolder.this, R.string.noVideoSelected, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(ViewFileInFolder.this, R.string.noImageSelected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    File fileIn = new File(list.get(i).getPathFileInFolder());
                    String imagePath = fileIn.getName().replaceAll("&", "/").substring(0, fileIn.getName().lastIndexOf("&"));
                    String imageName = fileIn.getName().substring(fileIn.getName().lastIndexOf("&") + 1, fileIn.getName().lastIndexOf("."));
                    File fileOut = new File(imagePath, imageName);
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileIn);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
                        copyStream(fileInputStream, fileOutputStream);
                        fileOutputStream.close();
                        fileInputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileIn.delete();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileIn)));
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileOut)));
                }
                Toast.makeText(ViewFileInFolder.this, R.string.unlockSuccessfully, Toast.LENGTH_SHORT).show();
                fileInFolderAdapter.setData(getAllFile());
                fileInFolderAdapter.unSelectAll();
                nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
                fileInFolderAdapter.closeSelection();
                iconSelection.setVisibility(View.VISIBLE);
                selectAll.setVisibility(View.GONE);
                closeSelect.setVisibility(View.GONE);
                imageOptions_1.setVisibility(View.GONE);
                checkListFile();
            }
        });


        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FileInFolder> list = fileInFolderAdapter.getSelected();
                if (list.size() == 0) {
                    if (!type) {
                        Toast.makeText(ViewFileInFolder.this, R.string.noVideoSelected, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(ViewFileInFolder.this, R.string.noImageSelected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                openMoveBottomSheetDialog();
            }
        });
    }

    private void playVideo(int position) {
        FileInFolder fileInFolder = listFileInFolder.get(position);
        File file = new File(fileInFolder.getPathFileInFolder());
        String videoName = file.getName().substring(file.getName().lastIndexOf("&") + 1, file.getName().lastIndexOf("."));
        File fileOut = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Temporary", videoName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
            copyStream(fileInputStream, fileOutputStream);
            fileOutputStream.close();
            fileInputStream.close();
            statusApp = 1;
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setType("video/*");
            Uri path = FileProvider.getUriForFile(ViewFileInFolder.this, "lol.contentprovider", fileOut);
//            intent.setData(path);
//            startActivity(intent);
            Intent intent = ShareCompat.IntentBuilder.from(this)
                    .setStream(path) // uri from FileProvider
                    .setType("text/html")
                    .getIntent()
                    .setAction(Intent.ACTION_VIEW) //Change if needed
                    .setDataAndType(path, "video/*")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMoveBottomSheetDialog() {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_move, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();

        TextView haveNoFolder = viewDialog.findViewById(R.id.haveNoFolder);

        RecyclerView rcvMoveFolder = viewDialog.findViewById(R.id.rcvMoveFolder);
        FolderToMoveAdapter folderToMoveAdapter = new FolderToMoveAdapter(this, listFolderToMove);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewFileInFolder.this, RecyclerView.VERTICAL, false);
        rcvMoveFolder.setLayoutManager(linearLayoutManager);
        folderToMoveAdapter.setData(getListFolder());
        rcvMoveFolder.setAdapter(folderToMoveAdapter);

        if (listFolderToMove.isEmpty()) {
            haveNoFolder.setVisibility(View.VISIBLE);
            rcvMoveFolder.setVisibility(View.GONE);
        }

        folderToMoveAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < fileInFolderAdapter.getSelected().size(); i++) {
                    File fileIn = new File(fileInFolderAdapter.getSelected().get(i).getPathFileInFolder());
                    String nameFile = fileIn.getName();
                    File fileOut = new File(listFolderToMove.get(position).getPathFolder(), nameFile);
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileIn);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
                        copyStream(fileInputStream, fileOutputStream);
                        fileOutputStream.close();
                        fileInputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileIn.delete();
                }
                bottomSheetDialog.dismiss();
                fileInFolderAdapter.setData(getAllFile());
                fileInFolderAdapter.unSelectAll();
                nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
                fileInFolderAdapter.closeSelection();
                iconSelection.setVisibility(View.VISIBLE);
                selectAll.setVisibility(View.GONE);
                closeSelect.setVisibility(View.GONE);
                imageOptions_1.setVisibility(View.GONE);
                checkListFile();
            }
        });

    }

    private ArrayList<FolderToMove> getListFolder() {
        File file = null;
        if (type) {
            file = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Photo");
        } else {
            file = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Video");
        }
        ArrayList<FolderToMove> list1 = new ArrayList<>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (!listFile[i].getName().equals(name)) {
                    list1.add(new FolderToMove(listFile[i].getAbsolutePath()));
                }
            }
            listFolderToMove = list1;
        }
        return listFolderToMove;
    }

    private void shareImage(String pathFileInFolder) {
        File fileIn = new File(pathFileInFolder);
        String imageName = fileIn.getName().substring(fileIn.getName().lastIndexOf("&") + 1, fileIn.getName().lastIndexOf("."));
        File fileOut = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Temporary", imageName);
        try {
            FileInputStream fileInputStream = new FileInputStream(fileIn);
            FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
            copyStream(fileInputStream, fileOutputStream);
            fileOutputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        share_bitMap_to_Apps(fileOut.getAbsolutePath(),imageName);
        shareOnePhoto(fileOut);
//        shareFileImage(pathFileInFolder);
    }

    private void shareOnePhoto(File fileOut) {
        Uri path = FileProvider.getUriForFile(this, "lol.contentprovider", fileOut);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (!type) {
            shareIntent.setType("video/*");
        } else {
            shareIntent.setType("image/*");
        }
        statusApp = 1;
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadingDialog.dismissDialog();
//            }
//        },4000);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    private void shareFileImage(String pathFileInFolder) {
        Bitmap b = BitmapFactory.decodeFile(pathFileInFolder);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ViewFileInFolder.this.getContentResolver(),
                b, "Title", null);
        Uri imageUri = Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Share via"));
    }

    public void openSelection(View view) {
        if (listFileInFolder.isEmpty()) {
            if (type) {
                Toast.makeText(ViewFileInFolder.this, R.string.haveNoImage, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(ViewFileInFolder.this, R.string.haveNoVideo, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        nameFolder.setText("0 selected");
        fileInFolderAdapter.openSelection();
        iconSelection.setVisibility(View.GONE);
        selectAll.setVisibility(View.VISIBLE);
        closeSelect.setVisibility(View.VISIBLE);
        imageOptions_1.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.GONE);
    }

    public void closeSelect(View view) {
        nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
        fileInFolderAdapter.closeSelection();
        fileInFolderAdapter.unSelectAll();
        iconSelection.setVisibility(View.VISIBLE);
        selectAll.setVisibility(View.GONE);
        closeSelect.setVisibility(View.GONE);
        imageOptions_1.setVisibility(View.GONE);
        checkListFile();
    }

    public void selectAll(View view) {
//        fileInFolderAdapter.setData(listFileInFolder);
        if (listImageSelected == null) {
            fileInFolderAdapter.selectAll();
            listImageSelected = fileInFolderAdapter.getSelected();
            nameFolder.setText(listImageSelected.size() + " " + getString(R.string.selected));
            if (listImageSelected.size() <= 1) {
                btnShare_1.setVisibility(View.VISIBLE);
            } else {
                btnShare_1.setVisibility(View.GONE);
            }
        } else if (listImageSelected.size() < listFileInFolder.size()) {
            fileInFolderAdapter.selectAll();
            listImageSelected = fileInFolderAdapter.getSelected();
            nameFolder.setText(listImageSelected.size() + " " + getString(R.string.selected));
            if (listImageSelected.size() <= 1) {
                btnShare_1.setVisibility(View.VISIBLE);
            } else {
                btnShare_1.setVisibility(View.GONE);
            }
        } else {
            fileInFolderAdapter.unSelectAll();
            listImageSelected = fileInFolderAdapter.getSelected();
            nameFolder.setText("0 " + getString(R.string.selected));
        }

    }

    private void openBottomSheetDialog(String pathFileInFolder, int h, int w) {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_info, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();

        TextView txtTitle = viewDialog.findViewById(R.id.txtTitle);
        TextView txtPath = viewDialog.findViewById(R.id.txtPath);
        TextView txtRatio = viewDialog.findViewById(R.id.txtRatio);
        TextView txtSize = viewDialog.findViewById(R.id.txtSize);

        File file = new File(pathFileInFolder);
        txtTitle.setText(file.getName().substring(file.getName().lastIndexOf("&") + 1, file.getName().lastIndexOf(".")));
        txtPath.setText(file.getName().replaceAll("&", "/").substring(0, file.getName().lastIndexOf(".")));
        if (file.length() / 1000 > 1000) {
            txtSize.setText(String.valueOf(file.length() / 1000000) + "MB");
        } else {
            txtSize.setText(String.valueOf(file.length() / 1000) + "kB");
        }
        txtRatio.setText(String.valueOf(w) + "x" + String.valueOf(h));
    }

    private void addControls() {
        nameFolder = findViewById(R.id.nameFolder);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnAddFile = findViewById(R.id.btnAddFile);
        fabAdd = findViewById(R.id.fabAdd);
        layoutParent = findViewById(R.id.layoutParent);
        iconSelection = findViewById(R.id.iconSelection);
        selectAll = findViewById(R.id.selectAll);
        closeSelect = findViewById(R.id.closeSelect);

        imageOptions_1 = findViewById(R.id.imageOptions_1);
        btnDelete_1 = findViewById(R.id.btnDelete_1);
        btnShare_1 = findViewById(R.id.btnShare_1);
        btnUnlock_1 = findViewById(R.id.btnUnlock_1);
        btnMove = findViewById(R.id.btnMove);

        layoutViewImage = findViewById(R.id.layoutViewImage);
        btnUnlock = findViewById(R.id.btnUnlock);
        btnShare = findViewById(R.id.btnShare);
        btnDelete = findViewById(R.id.btnDelete);
        btnInfo = findViewById(R.id.btnInfo);
        viewImage = findViewById(R.id.viewImage);

        Intent intent = getIntent();
        name = intent.getStringExtra("FOLDER NAME");
        type = intent.getBooleanExtra("TYPE", false);
        nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));

        rcvFile = findViewById(R.id.rcvFile);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rcvFile.setLayoutManager(gridLayoutManager);
        fileInFolderAdapter = new FileInFolderAdapter(this, listFileInFolder);
        fileInFolderAdapter.setData(getAllFile());
        rcvFile.setAdapter(fileInFolderAdapter);

        checkListFile();
    }

    private void checkListFile() {
        if (getAllFile().size() > 0) {
            layoutEmpty.setVisibility(View.GONE);

            fabAdd.setVisibility(View.VISIBLE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);

            fabAdd.setVisibility(View.GONE);
        }

    }

    private ArrayList<FileInFolder> getAllFile() {
        File file = null;
        if (type) {
            file = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Photo", name);
        } else {
            file = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Video", name);
        }
        ArrayList<FileInFolder> list = new ArrayList<>();
        if (file.isDirectory()) {
            File[] mFile = file.listFiles();
            for (int i = 0; i < mFile.length; i++) {
                list.add(new FileInFolder(mFile[i].getAbsolutePath(), false));
            }
        }
        listFileInFolder = list;
        return listFileInFolder;
    }

    public void clickBack(View view) {
        if (layoutViewImage.getVisibility() == View.VISIBLE || imageOptions_1.getVisibility() == View.VISIBLE) {
            layoutViewImage.setVisibility(View.GONE);
            fileInFolderAdapter.setData(getAllFile());
            fileInFolderAdapter.unSelectAll();
            nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
            fileInFolderAdapter.closeSelection();
            iconSelection.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.GONE);
            closeSelect.setVisibility(View.GONE);
            imageOptions_1.setVisibility(View.GONE);
            layoutParent.setVisibility(View.VISIBLE);
            listImageSelected = null;
            checkListFile();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutViewImage.getVisibility() == View.GONE) {
            deleteFileInTemporary();
            fileInFolderAdapter.setData(getAllFile());
            fileInFolderAdapter.unSelectAll();
            nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
            fileInFolderAdapter.closeSelection();
            iconSelection.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.GONE);
            closeSelect.setVisibility(View.GONE);
            imageOptions_1.setVisibility(View.GONE);
            layoutParent.setVisibility(View.VISIBLE);
            listImageSelected = null;
            checkListFile();
            statusApp = 0;
        } else if (layoutViewImage.getVisibility() == View.VISIBLE) {
            layoutParent.setVisibility(View.GONE);
            layoutViewImage.setVisibility(View.VISIBLE);
            iconSelection.setVisibility(View.GONE);
            statusApp = 0;
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (statusApp == 0) {
            restartApp();
        }
    }

    private void deleteFileInTemporary() {
        File file = new File(MainActivity.getStore(ViewFileInFolder.this) + "/.Hidephoto/Temporary");
        String fileChildren[];
        if (file.isDirectory()) {
            fileChildren = file.list();
            for (int i = 0; i < fileChildren.length; i++) {
                File myFile = new File(file, fileChildren[i]);
                myFile.delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(myFile)));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutViewImage.getVisibility() == View.VISIBLE || imageOptions_1.getVisibility() == View.VISIBLE) {
            layoutViewImage.setVisibility(View.GONE);
            fileInFolderAdapter.setData(getAllFile());
            fileInFolderAdapter.unSelectAll();
            nameFolder.setText(String.valueOf(name.charAt(0)).toUpperCase()+""+name.substring(1, name.length()));
            fileInFolderAdapter.closeSelection();
            iconSelection.setVisibility(View.VISIBLE);
            selectAll.setVisibility(View.GONE);
            closeSelect.setVisibility(View.GONE);
            imageOptions_1.setVisibility(View.GONE);
            layoutParent.setVisibility(View.VISIBLE);
            listImageSelected = null;
            checkListFile();
        } else {
            super.onBackPressed();
        }
    }

    public void share_bitMap_to_Apps(String pathFileInFolder, String imageName) {

        Intent i = new Intent(Intent.ACTION_SEND);

        Bitmap bitmap = BitmapFactory.decodeFile(pathFileInFolder);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, saveImageshare(bitmap, imageName));
        try {
            statusApp = 1;
            startActivity(Intent.createChooser(i, getString(R.string.sharePhotoAndView)));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = context.getContentResolver();
                    Uri picCollection = MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    ContentValues picDetail = new ContentValues();
                    picDetail.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
                    picDetail.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    picDetail.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + UUID.randomUUID().toString());
                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 1);
                    Uri finaluri = resolver.insert(picCollection, picDetail);
                    picDetail.clear();
                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(picCollection, picDetail, null, null);
                    return finaluri;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }

            } else {
                return null;
            }
        }
    }

    public static Uri getVideoContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID},
                MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = context.getContentResolver();
                    Uri picCollection = MediaStore.Video.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    ContentValues picDetail = new ContentValues();
                    picDetail.put(MediaStore.Video.Media.DISPLAY_NAME, imageFile.getName());
                    picDetail.put(MediaStore.Video.Media.MIME_TYPE, "image/jpg");
                    picDetail.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/" + UUID.randomUUID().toString());
                    picDetail.put(MediaStore.Video.Media.IS_PENDING, 1);
                    Uri finaluri = resolver.insert(picCollection, picDetail);
                    picDetail.clear();
                    picDetail.put(MediaStore.Video.Media.IS_PENDING, 0);
                    resolver.update(picCollection, picDetail, null, null);
                    return finaluri;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                }

            } else {
                return null;
            }
        }
    }

    public void restartApp() {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(9);
                        if (_active) {
                            waited += 9;
                        }
                    }
                } catch (InterruptedException e) {
                    e.toString();
                } finally {
                    Intent intent = new Intent(getApplicationContext(),
                            IntrodutionActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        splashTread.start();
    }

    private void shareOnePhoto1(String pathImage) {
        Uri path = FileProvider.getUriForFile(this, "com.restart.sharingdata", new File(pathImage));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is one image I'm sharing.");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    private Uri saveImageshare(Bitmap image, String imageName) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), getPackageName()); // (get file cach + name folder cache"

        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, imageName);
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(ViewFileInFolder.this, "lol.contentprovider", file);
        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ViewFileInFolder.this, R.style.AppCompatAlertDialogStyle);
            pd.setMessage(getString(R.string.loading));
            pd.setContentView(R.layout.progress_dialog);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            shareImage(pathAsyncTask);
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
        }
    }

    class MyTask2 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ViewFileInFolder.this, R.style.AppCompatAlertDialogStyle);
            pd.setMessage(getString(R.string.loading));
            pd.setContentView(R.layout.progress_dialog);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do your request
            playVideo(positionPlayVideo);
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
        }
    }

}