package com.example.requip;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

//public class saman_adapter {
//}
public class saman_adapter extends RecyclerView.Adapter<saman_adapter.ViewHolder>{

    private List<saman> samanList;
    Context context;



    public saman_adapter(List<saman> _samanList, Context contextm) {
        this.context = contextm;
        this.samanList = _samanList;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView saman_image;
        public MaterialCardView constraintLayout;
        public TextView samanPrice;
        public TextView samanTitle;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super( itemView );

            saman_image = (ImageView) itemView.findViewById( R.id.saman_image );
            samanPrice  = (TextView)  itemView.findViewById( R.id.saman_price );
            samanTitle  = (TextView)  itemView.findViewById( R.id.saman_title );
            constraintLayout = (MaterialCardView) itemView.findViewById( R.id.cardview );
        }

    }

    @Override
    public saman_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from( context );

        // Inflate the custom layout
        View sysview = inflater.inflate( R.layout.saman_post_cardlayout, parent, false );

        // Return a new holder instance
        saman_adapter.ViewHolder viewHolder = new saman_adapter.ViewHolder( sysview );
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final saman_adapter.ViewHolder holder, final int position) {
        // Get the data model based on position
        final saman saman_post = samanList.get( position );

        TextView TVprice = holder.samanPrice;
        TVprice.setText("₹ " + saman_post.getPrice());
        TextView TVtitle = holder.samanTitle;
        TVtitle.setText(saman_post.getTitle());

        String base_url_saman_image = context.getResources().getString(R.string.base_url_samanImage);
        String url = base_url_saman_image + saman_post.getImage();
        ImageView IVimage = holder.saman_image;
        Picasso.get().load( url ).placeholder( R.drawable.iitj )
                .error( R.drawable.iitj )
                .into( IVimage );



        holder.constraintLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "saman id " + saman_post.getId(), Toast.LENGTH_SHORT).show();
            }
        } );
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return samanList.size();
    }

//    private void loadimage (String Url){
//        Picasso.get().load( Url ).placeholder( R.mipmap.ic_launcher )
//                .error( R.mipmap.ic_launcher )
//                .into(  );
//    }
}
