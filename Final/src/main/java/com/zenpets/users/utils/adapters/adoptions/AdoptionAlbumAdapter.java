package com.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zenpets.users.R;
import com.zenpets.users.utils.models.adoptions.AdoptionAlbumData;

import java.util.ArrayList;

public class AdoptionAlbumAdapter extends RecyclerView.Adapter<AdoptionAlbumAdapter.AlbumsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<AdoptionAlbumData> arrAlbums;

    public AdoptionAlbumAdapter(Activity activity, ArrayList<AdoptionAlbumData> arrAlbums) {

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrAlbums = arrAlbums;
    }

    @Override
    public int getItemCount() {
        return arrAlbums.size();
    }

    @Override
    public void onBindViewHolder(AlbumsVH holder, int position) {
        final AdoptionAlbumData td = arrAlbums.get(position);

        /** SET THE CLINIC IMAGE **/
        Bitmap bmpImage = td.getBmpAdoptionImage();
        if (bmpImage!= null)	{
            holder.imgvwAdoptionImage.setImageBitmap(bmpImage);
            holder.imgvwAdoptionImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        /** SET THE IMAGE NUMBER **/
        String strNumber = td.getTxtImageNumber();
        holder.txtImageNumber.setText(strNumber);
    }

    @Override
    public AlbumsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.adoption_album_item, parent, false);

        return new AlbumsVH(itemView);
    }

    class AlbumsVH extends RecyclerView.ViewHolder   {

        AppCompatImageView imgvwAdoptionImage;
        AppCompatTextView txtImageNumber;

        AlbumsVH(View v) {
            super(v);
            imgvwAdoptionImage = (AppCompatImageView) v.findViewById(R.id.imgvwAdoptionImage);
            txtImageNumber = (AppCompatTextView) v.findViewById(R.id.txtImageNumber);
            txtImageNumber.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Bold.ttf"));
        }
    }
}