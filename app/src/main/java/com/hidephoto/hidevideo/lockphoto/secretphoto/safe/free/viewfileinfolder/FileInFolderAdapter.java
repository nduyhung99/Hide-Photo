package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.Photo;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.PhotoAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FileInFolderAdapter extends RecyclerView.Adapter<FileInFolderAdapter.FileInFolderViewHolder>{
    private Context mContext;
    private ArrayList<FileInFolder> listFileInFolder;
    private FileInFolderAdapter.OnItemClickListener mListener;
    private FileInFolderAdapter.OnItemClickListener_1 mListener_1;
    private FileInFolderAdapter.OnItemClickListener_2 mListener_2;
    private boolean checkSelection = false;

    public void setOnItemClickListener(FileInFolderAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public void setOnItemClickListener_1(FileInFolderAdapter.OnItemClickListener_1 listener_1){
        mListener_1 = listener_1;
    }

    public void setOnItemClickListener_2(FileInFolderAdapter.OnItemClickListener_2 listener_2){
        mListener_2 = listener_2;
    }

    public FileInFolderAdapter(Context mContext, ArrayList<FileInFolder> listFileInFolder) {
        this.mContext = mContext;
        this.listFileInFolder = listFileInFolder;
    }

    public void setData(ArrayList<FileInFolder> list){
        this.listFileInFolder = new ArrayList<>();
        this.listFileInFolder = list;
        notifyDataSetChanged();
    }

    public void selectFile(FileInFolder fileInFolder){
        fileInFolder.setFileChecked(true);
        notifyDataSetChanged();
    }

    public ArrayList<FileInFolder> getSelected(){
        ArrayList<FileInFolder> selected = new ArrayList<>();

        for(int i=0; i< listFileInFolder.size(); i++){
            if (listFileInFolder.get(i).isFileChecked()){
                selected.add(listFileInFolder.get(i));
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public FileInFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_in_folder,parent,false);
        return new FileInFolderViewHolder(view, mListener, mListener_1, mListener_2);
    }

    @Override
    public void onBindViewHolder(@NonNull FileInFolderAdapter.FileInFolderViewHolder holder, int position) {
        FileInFolder fileInFolder = listFileInFolder.get(position);
        String nameFileImage = fileInFolder.getPathFileInFolder();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        Glide.with(holder.imageFile.getContext()).load(nameFileImage)
                .override(200,200)
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .apply(requestOptions)
                .into(holder.imageFile);
        if (checkSelection == false){
            holder.layoutSelection.setVisibility(View.GONE);
            enableDisableView(holder.imageFile,true);
        }else {
            holder.layoutSelection.setVisibility(View.VISIBLE);
            enableDisableView(holder.imageFile,false);
        }
        holder.ic_select.setVisibility(fileInFolder.isFileChecked() ? View.VISIBLE : View.GONE);
        holder.ic_unselect.setVisibility(fileInFolder.isFileChecked() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if (listFileInFolder.size()>0){
            return listFileInFolder.size();
        }
        return 0;
    }

    public class FileInFolderViewHolder extends RecyclerView.ViewHolder{
        ImageView imageFile, ic_select, ic_unselect;
        RelativeLayout layoutSelection;

        public FileInFolderViewHolder(@NonNull View itemView,final FileInFolderAdapter.OnItemClickListener listener,
                                      final FileInFolderAdapter.OnItemClickListener_1 listener_1,
                                      final FileInFolderAdapter.OnItemClickListener_2 listener_2) {
            super(itemView);
            imageFile = itemView.findViewById(R.id.imageFile);
            ic_select=itemView.findViewById(R.id.ic_select);
            ic_unselect=itemView.findViewById(R.id.ic_unselect);
            layoutSelection=itemView.findViewById(R.id.layoutSelection);

            imageFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            try {
                                listener.onItemClick(position);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            imageFile.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener_2!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            try {
                                listener_2.onItemClick_2(position);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return false;
                }
            });

            layoutSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener_1!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            try {
                                listener_1.onItemClick_1(position);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            FileInFolder fileInFolder = listFileInFolder.get(position);
                            fileInFolder.setFileChecked(!fileInFolder.isFileChecked());

                            ic_select.setVisibility(fileInFolder.isFileChecked() ? View.VISIBLE : View.GONE);
                            ic_unselect.setVisibility(fileInFolder.isFileChecked() ? View.GONE : View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    public void openSelection(){
        checkSelection=true;
        notifyDataSetChanged();
    }
    public void closeSelection(){
        checkSelection=false;
        notifyDataSetChanged();
    }



    public void selectAll(){
        for (int i=0; i<listFileInFolder.size(); i++){
            listFileInFolder.get(i).setFileChecked(true);
        }
        notifyDataSetChanged();
    }


    public void unSelectAll(){
        for (int i=0; i<listFileInFolder.size(); i++){
            listFileInFolder.get(i).setFileChecked(false);
        }
        notifyDataSetChanged();
    }

    public ArrayList<FileInFolder> getAll(){
        return listFileInFolder;
    }

    public interface OnItemClickListener{
        void onItemClick(int position) throws FileNotFoundException;
    }

    public interface OnItemClickListener_1{
        void onItemClick_1(int position) throws FileNotFoundException;
    }

    public interface OnItemClickListener_2{
        void onItemClick_2(int position) throws FileNotFoundException;
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }
}
