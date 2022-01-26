package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private Context mContext;
    private List<Folder> mListFolder;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public FolderAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Folder> list) {
        this.mListFolder = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.FolderViewHolder holder, int position) {
        Folder folder = mListFolder.get(position);
        if (folder == null) {
            return;
        }
        if (folder.getFirstPath().isEmpty()) {
            holder.imageFolder.setImageResource(folder.getImage());
        } else {
//            holder.imageFolder.setImageBitmap(BitmapFactory.decodeFile(folder.getFirstPath()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop());
            Glide.with(holder.imageFolder.getContext()).load(folder.getFirstPath())
                    .override(holder.imageFolder.getWidth(), holder.imageFolder.getHeight())
                    .placeholder(R.drawable.ic_outline_image)
                    .error(R.drawable.ic_outline_image)
                    .apply(requestOptions)
                    .into(holder.imageFolder);
            holder.imageFolder.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
//        holder.nameFolder.setText(folder.getNameFolder().substring(folder.getNameFolder().indexOf(".")+1));
        holder.nameFolder.setText(String.valueOf(folder.getNameFolder().charAt(0)).toUpperCase() + "" + folder.getNameFolder().substring(1, folder.getNameFolder().length()));
        holder.countItem.setText(folder.getCountItem() + " " + mContext.getString(R.string.items));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    View viewDialog = LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_sheet_delete, null);
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
                    bottomSheetDialog.setContentView(viewDialog);
                    bottomSheetDialog.show();

                    Button btnCancel = viewDialog.findViewById(R.id.btnCancel);
                    Button btnDelete = viewDialog.findViewById(R.id.btnDelete);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(folder.getPathFolder());
                            if (file.getName().equals("Document") || file.getName().equals("Private Photo") || file.getName().equals("Security Card") || file.getName().equals("Default")) {
                                Toast.makeText(mContext, R.string.deleteDefaultFolder, Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            } else {
                                file.delete();
                                mListFolder.remove(position);
                                Toast.makeText(mContext, R.string.deleteFolder, Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListFolder != null) {
            return mListFolder.size();
        }
        return 0;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private ImageView imageFolder;
        private TextView nameFolder, countItem;
        private CardView layoutParent;

        private ItemClickListener itemClickListener;

        public FolderViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageFolder = itemView.findViewById(R.id.imageFolder);
            nameFolder = itemView.findViewById(R.id.nameFolder);
            countItem = itemView.findViewById(R.id.countItem);
            layoutParent = itemView.findViewById(R.id.layoutParent);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


}
