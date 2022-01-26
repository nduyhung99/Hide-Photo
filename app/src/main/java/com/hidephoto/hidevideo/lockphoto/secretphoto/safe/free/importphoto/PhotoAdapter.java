package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{
    private Context mContext;
    private ArrayList<Photo> mListPhoto;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }


    public PhotoAdapter(Context mContext, ArrayList<Photo> mListPhoto ) {
        this.mContext = mContext;
        this.mListPhoto =mListPhoto;
    }
    public void setData(ArrayList<Photo> mListPhoto){
        this.mListPhoto =new ArrayList<>();
        this.mListPhoto = mListPhoto;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_in_folder,parent,false);
        return new PhotoViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.PhotoViewHolder holder, int position) {
        Photo photo = mListPhoto.get(position);
        if (photo==null){
            return;
        }
        holder.imgChecked.setVisibility(photo.isChecked() ? View.VISIBLE : View.GONE);
//        holder.bind(mListPhoto.get(position));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        Glide.with(holder.imgPhoto.getContext()).load(mListPhoto.get(position).getPathPhoto())
                .override(holder.imgPhoto.getWidth()/3,holder.imgPhoto.getHeight()/3)
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .apply(requestOptions)
                .into(holder.imgPhoto);
//        Bitmap myBitmap = BitmapFactory.decodeFile(mListPhoto.get(position).getPathPhoto());
//        holder.imgPhoto.setImageBitmap(myBitmap);
    }

    @Override
    public int getItemCount() {
        if (mListPhoto!=null){
            return mListPhoto.size();
        }
        return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPhoto;
        ImageView imgChecked;
        RelativeLayout itemPhoto;

        public PhotoViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            imgPhoto=itemView.findViewById(R.id.imgPhoto);
            imgChecked=itemView.findViewById(R.id.imgChecked);
            itemPhoto=itemView.findViewById(R.id.itemPhoto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                        Photo photo = mListPhoto.get(position);
                        photo.setChecked(!photo.isChecked());
                        imgChecked.setVisibility(photo.isChecked() ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }

        void bind(final Photo photo){
//            Bitmap myBitmap = BitmapFactory.decodeFile(photo.getPathPhoto());
//            imgPhoto.setImageBitmap(myBitmap);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photo.setChecked(!photo.isChecked());
                    imgChecked.setVisibility(photo.isChecked() ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    public ArrayList<Photo> getAll(){
        return mListPhoto;
    }

    public ArrayList<Photo> getSelected(){
        ArrayList<Photo> selected = new ArrayList<>();

        for(int i=0; i< mListPhoto.size(); i++){
            if (mListPhoto.get(i).isChecked()){
                selected.add(mListPhoto.get(i));
            }
        }
        return selected;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
