package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reading7.Dialogs.BuyProductDialog;
import com.reading7.MainActivity;
import com.reading7.Objects.Avatar;
import com.reading7.Objects.Product;
import com.reading7.Objects.User;
import com.reading7.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private List<Product> products;

    public StoreAdapter(Context mContext, Fragment mFragment, List<Product> products) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.store_item, viewGroup, false);
        return new StoreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Product product = products.get(position);
        Avatar.Item item = product.getItem();

        holder.productImage.setImageDrawable(item.findDisplayDrawable(mContext));

        if (isAlreadyPurchased(product)) {
            holder.priceLayout.setVisibility(View.GONE);
            holder.purchasedLayout.setVisibility(View.VISIBLE);
            holder.productImage.setOnClickListener(null);
            holder.background.setElevation(0);
        } else {
            holder.priceLayout.setVisibility(View.VISIBLE);
            holder.purchasedLayout.setVisibility(View.GONE);
            //holder.background.setBackgroundTintList(ColorStateList.valueOf(Utils.getColor(mContext,"white")));
            holder.background.setElevation(5);
            holder.productPrice.setText(Integer.toString(product.getPrice()));
            holder.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBuyProductDialog(product);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void showBuyProductDialog(Product product) {

        BuyProductDialog dialog = new BuyProductDialog(product);
        dialog.setTargetFragment(mFragment, 404);
        dialog.show(((MainActivity) mContext).getSupportFragmentManager(), "buy_product");

    }

    private boolean isAlreadyPurchased(Product product) {

        User user = ((MainActivity) mContext).getCurrentUser();
        return user.getAvatar().getUnlockedItems().contains(product.getItem());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productPrice;
        ImageView productImage;
        RelativeLayout background;
        RelativeLayout priceLayout;
        RelativeLayout purchasedLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            background = itemView.findViewById(R.id.background);
            priceLayout = itemView.findViewById(R.id.priceLayout);
            purchasedLayout = itemView.findViewById(R.id.purchasedLayout);
        }
    }

}
