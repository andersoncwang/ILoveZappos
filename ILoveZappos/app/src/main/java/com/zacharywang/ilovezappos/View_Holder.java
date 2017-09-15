package com.zacharywang.ilovezappos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class View_Holder extends RecyclerView.ViewHolder {

    CardView cv;
    TextView description;
    TextView description2;
    ImageView imageView;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        description = (TextView) itemView.findViewById(R.id.description);
        description2 = (TextView) itemView.findViewById(R.id.description2);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
    }
}