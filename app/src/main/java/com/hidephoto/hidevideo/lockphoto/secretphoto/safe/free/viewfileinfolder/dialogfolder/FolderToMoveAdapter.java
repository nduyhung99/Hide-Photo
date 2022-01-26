package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.dialogfolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.Folder;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.Photo;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.PhotoAdapter;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.viewfileinfolder.FileInFolder;

import java.io.File;
import java.util.ArrayList;

public class FolderToMoveAdapter extends RecyclerView.Adapter<FolderToMoveAdapter.FolderToMoveViewHolder>{
    private Context mContext;
    private ArrayList<FolderToMove> listFolderToMove;
    private PhotoAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(PhotoAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public FolderToMoveAdapter(Context mContext, ArrayList<FolderToMove> listFolderToMove) {
        this.mContext = mContext;
        this.listFolderToMove = listFolderToMove;
    }

    public void setData(ArrayList<FolderToMove> list){
        this.listFolderToMove = new ArrayList<>();
        this.listFolderToMove = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FolderToMoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_move_folder,parent,false);
        return new FolderToMoveViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderToMoveAdapter.FolderToMoveViewHolder holder, int position) {
        FolderToMove folderToMove = listFolderToMove.get(position);
        File file = new File(folderToMove.getPathFolder());
        holder.nameFolderMove.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        if (listFolderToMove.size()>0){
            return listFolderToMove.size();
        }
        return 0;
    }

    public class FolderToMoveViewHolder extends RecyclerView.ViewHolder{
        TextView nameFolderMove,haveNoFolder;
        LinearLayout layoutMoveToFolder;
        public FolderToMoveViewHolder(@NonNull View itemView,final PhotoAdapter.OnItemClickListener listener) {
            super(itemView);
            nameFolderMove = itemView.findViewById(R.id.nameFolderMove);
            layoutMoveToFolder = itemView.findViewById(R.id.layoutMoveToFolder);
            haveNoFolder=itemView.findViewById(R.id.haveNoFolder);

            layoutMoveToFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
