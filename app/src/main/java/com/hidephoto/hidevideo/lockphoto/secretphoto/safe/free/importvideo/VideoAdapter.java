package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importvideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.Photo;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.PhotoAdapter;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{
    private Context mContext;
    private ArrayList<Video> mListVideo;
    private PhotoAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(PhotoAdapter.OnItemClickListener listener){
        mListener=listener;
    }


    public VideoAdapter(Context mContext, ArrayList<Video> mListVideo ) {
        this.mContext = mContext;
        this.mListVideo =mListVideo;
    }
    public void setData(ArrayList<Video> mListVideo){
        this.mListVideo =new ArrayList<>();
        this.mListVideo = mListVideo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_in_folder,parent,false);
        return new VideoViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {
        Video video = mListVideo.get(position);
        if (video==null){
            return;
        }
        holder.imgChecked.setVisibility(video.isChecked() ? View.VISIBLE : View.GONE);
//        holder.bind(mListPhoto.get(position));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        Glide.with(holder.imgVideo.getContext()).load(mListVideo.get(position).getPathVideo())
                .override(holder.imgVideo.getWidth()/3,holder.imgVideo.getHeight()/3)
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .apply(requestOptions)
                .into(holder.imgVideo);
//        Bitmap myBitmap = BitmapFactory.decodeFile(mListPhoto.get(position).getPathPhoto());
//        holder.imgPhoto.setImageBitmap(myBitmap);

    }

    @Override
    public int getItemCount() {
        if (mListVideo.size()>0){
            return mListVideo.size();
        }
        return 0;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView imgVideo, imgChecked;

        public VideoViewHolder(@NonNull View itemView,final PhotoAdapter.OnItemClickListener listener) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.imgVideo);
            imgChecked = itemView.findViewById(R.id.imgChecked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                        Video video = mListVideo.get(position);
                        video.setChecked(!video.isChecked());
                        imgChecked.setVisibility(video.isChecked() ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    }

    public ArrayList<Video> getAll(){
        return mListVideo;
    }

    public ArrayList<Video> getSelected(){
        ArrayList<Video> selected = new ArrayList<>();

        for(int i=0; i< mListVideo.size(); i++){
            if (mListVideo.get(i).isChecked()){
                selected.add(mListVideo.get(i));
            }
        }
        return selected;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
