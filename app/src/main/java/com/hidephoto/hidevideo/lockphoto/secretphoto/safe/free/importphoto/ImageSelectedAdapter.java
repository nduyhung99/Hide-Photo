package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.importphoto;

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

import java.util.ArrayList;
import java.util.List;

public class ImageSelectedAdapter extends RecyclerView.Adapter<ImageSelectedAdapter.ImageSelectedViewHolder>{
    private Context mContext;
    private ArrayList<ImageSelected> mListImageSelected;
    private PhotoAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(PhotoAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    public ImageSelectedAdapter(Context mContext, ArrayList<ImageSelected> mListImageSelected) {
        this.mContext = mContext;
        this.mListImageSelected = mListImageSelected;
    }

    public void setData(ArrayList<ImageSelected> list){
        this.mListImageSelected = new ArrayList<>();
        this.mListImageSelected = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageSelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_selected,parent,false);
        return new ImageSelectedViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSelectedAdapter.ImageSelectedViewHolder holder, int position) {
        ImageSelected imageSelected = mListImageSelected.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop());
        Glide.with(holder.imageSelected.getContext()).load(imageSelected.getPathImageSelected())
                .override(200,200)
                .placeholder(R.drawable.ic_outline_image)
                .error(R.drawable.ic_outline_image)
                .apply(requestOptions)
                .into(holder.imageSelected);

    }

    @Override
    public int getItemCount() {
        if (mListImageSelected != null){
            return mListImageSelected.size();
        }
        return 0;
    }

    public class ImageSelectedViewHolder extends RecyclerView.ViewHolder{
        ImageView imageSelected, btnCancel;

        public ImageSelectedViewHolder(@NonNull View itemView,final PhotoAdapter.OnItemClickListener listener) {
            super(itemView);
            imageSelected=itemView.findViewById(R.id.imageSelected);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
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
