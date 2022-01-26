package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.FolderAdapter;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;

import java.io.File;
import java.util.List;

public class ButketAdapter extends RecyclerView.Adapter<ButketAdapter.ButketViewHolder>{
    private Context mContext;
    private List<Butket> mListButket;
    private FolderAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(FolderAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public ButketAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Butket> list){
        this.mListButket=list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ButketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bucket,parent,false);
        return new ButketViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ButketAdapter.ButketViewHolder holder, int position) {
        Butket butket= mListButket.get(position);
        if (mListButket==null){
            return;
        }
        holder.path.setText(butket.getFirstImageContainedPath());
        holder.fileName.setText(butket.getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        Glide.with(holder.imageFile.getContext()).load(butket.getFirstImageContainedPath())
                .override(200,200)
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .apply(requestOptions)
                .into(holder.imageFile);
//        File imgFile = new  File(butket.getFirstImageContainedPath());
//        if(imgFile.exists()){
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
//            holder.imageFile.setImageBitmap(myBitmap);
//
//        }

    }

    @Override
    public int getItemCount() {
        if (mListButket!=null){
            return mListButket.size();
        }
        return 0;
    }

    public class ButketViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageFile;
        private TextView fileName, path;
        public ButketViewHolder(@NonNull View itemView,final FolderAdapter.OnItemClickListener listener) {
            super(itemView);
            imageFile=itemView.findViewById(R.id.imageFile);
            fileName=itemView.findViewById(R.id.fileName);
            path=itemView.findViewById(R.id.path);

            itemView.setOnClickListener(new View.OnClickListener() {
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
