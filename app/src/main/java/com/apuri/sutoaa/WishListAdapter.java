package com.apuri.sutoaa;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private boolean fromSearch;
    private List<WishListModel> wishlistModelList;
    private Boolean wishList;
    private int lastPosition = -1;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishListAdapter(List<WishListModel> wishlistModelList, boolean wishList) {
        this.wishlistModelList = wishlistModelList;
        this.wishList = wishList;
    }

    public List<WishListModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishListModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wish_list_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.ViewHolder viewHolder, int position) {
        String productId = wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        long freeCouponNo = wishlistModelList.get(position).getFreeCoupon();
        String rating = wishlistModelList.get(position).getRating();
        long totalRatings = wishlistModelList.get(position).getTotalRatings();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String cuttedPrice = wishlistModelList.get(position).getCuttedPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();

        viewHolder.setData(productId, resource, title, freeCouponNo, rating, totalRatings, productPrice, cuttedPrice, paymentMethod, position, inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
        //return wishlistModelList == null ? 0 : wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupons;
        private ImageView couponIcon;
        private TextView rating;
        private TextView totalRatings;
        //private View priceCutView;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupons = itemView.findViewById(R.id.free_coupon);
            couponIcon = itemView.findViewById(R.id.coupon_icon);
            rating = itemView.findViewById(R.id.product_rating_mainview_textView);
            totalRatings = itemView.findViewById(R.id.totalRatings);
            //priceCutView = itemView.findViewById(R.id.view);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            deleteButton = itemView.findViewById(R.id.delete_button);

        }

        private void setData(final String productId, String resource, String title, long freeCouponNo, String averageRate, long totalRatingsNo, String price, String cuttedPriceValue, boolean COD, final int index, boolean inStock) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.loading_13)).into(productImage);
            productTitle.setText(title);
            if (freeCouponNo != 0 && inStock) {
                couponIcon.setVisibility(View.VISIBLE);
                if (freeCouponNo == 1) {
                    freeCoupons.setText("free " + freeCouponNo + " coupon");
                }
                else {
                    freeCoupons.setText("free " + freeCouponNo + " coupons");
                }
            }
            else {
                couponIcon.setVisibility(View.INVISIBLE);
                freeCoupons.setVisibility(View.INVISIBLE);
            }

            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (inStock) {
                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);

                linearLayout.setVisibility(View.INVISIBLE);

                rating.setText(averageRate);
                totalRatings.setText("(" + totalRatingsNo + ")" + " (ratings)");
                productPrice.setText("$" + price + "/-");
                cuttedPrice.setText("$" + cuttedPriceValue + "/-");

                if (COD) {
                    paymentMethod.setVisibility(View.VISIBLE);
                }
                else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }
            else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getColor(R.color.colorPrimary));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }

            if (wishList) {
                deleteButton.setVisibility(View.VISIBLE);
            }
            else {
                deleteButton.setVisibility(View.GONE);
            }

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ProductDetailsActivity.running_wish_list_query) {
                        ProductDetailsActivity.running_wish_list_query = true;
                        DBqueries.removeFromWishlist(index, itemView.getContext());
                    }
                    //Toast.makeText(itemView.getContext(), "delete", Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fromSearch) {
                        ProductDetailsActivity.fromSearch = true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productId);
                    itemView.getContext().startActivity(productDetailsIntent);

                }
            });
        }
    }
}
