package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto.PhotoAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageSecurityAdapter extends RecyclerView.Adapter<ImageSecurityAdapter.ImageSecurityViewHolder>{
    private Context mContext;
    private ArrayList<ImageSecurity> mListImageSecurity;
    private ImageSecurityAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(ImageSecurityAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public ImageSecurityAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(ArrayList<ImageSecurity> list){
        this.mListImageSecurity = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageSecurityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_security_image,parent,false);
        return new ImageSecurityViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSecurityAdapter.ImageSecurityViewHolder holder, int position) {
        ImageSecurity imageSecurity = mListImageSecurity.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        Glide.with(holder.imageSecurity.getContext()).load(imageSecurity.getPath())
                .override(200,200)
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .apply(requestOptions)
                .into(holder.imageSecurity);
        holder.dateAndTimeOfImage.setText(imageSecurity.getDateAndTime());
        holder.passwordWrong.setText(imageSecurity.getPasswordWrong());
    }

    @Override
    public int getItemCount() {
        if (mListImageSecurity.size()>0){
            return mListImageSecurity.size();
        }
        return 0;
    }

    public class ImageSecurityViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageSecurity;
        private TextView dateAndTimeOfImage,passwordWrong;
        private LinearLayout layoutImageSecurity;

        public ImageSecurityViewHolder(@NonNull View itemView,final ImageSecurityAdapter.OnItemClickListener listener) {
            super(itemView);
            imageSecurity = itemView.findViewById(R.id.imageSecurity);
            dateAndTimeOfImage = itemView.findViewById(R.id.dateAndTimeOfImage);
            passwordWrong = itemView.findViewById(R.id.passwordWrong);
            layoutImageSecurity = itemView.findViewById(R.id.layoutImageSecurity);

            layoutImageSecurity.setOnClickListener(new View.OnClickListener() {
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
