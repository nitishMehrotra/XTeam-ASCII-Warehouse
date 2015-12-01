package com.sample.xteamtest.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sample.xteamtest.R;
import com.sample.xteamtest.rest.model.Face;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<Face> mItems;

    public GridAdapter(List<Face> list) {
        super();
        mItems = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Face face = mItems.get(i);
        if(face == null) {
            return;
        }
        viewHolder.price.setText("$" + face.getPrice() / 100);
        viewHolder.faceIcon.setText(face.getFace());
        if (face.getStock() > 1) {
            viewHolder.buyButton.setBackgroundColor(
                    viewHolder.buyButton.getContext().getResources().getColor(R.color.grey_600));
        } else if (face.getStock() == 1) {
            viewHolder.buyButton.setBackgroundColor(
                    viewHolder.buyButton.getContext().getResources().getColor(R.color.colorAccent));
        } else if (face.getStock() < 1) {
            viewHolder.buyButton.setBackgroundColor(
                    viewHolder.buyButton.getContext().getResources().getColor(R.color.grey_400));
            viewHolder.buyButton.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView faceIcon;
        public TextView price;
        public Button buyButton;

        public ViewHolder(View itemView) {
            super(itemView);
            faceIcon = (TextView) itemView.findViewById(R.id.face);
            price = (TextView) itemView.findViewById(R.id.price);
            buyButton = (Button) itemView.findViewById(R.id.buyButton);
        }
    }

}
