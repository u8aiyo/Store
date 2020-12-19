package com.apuri.sutoaa;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.ViewHolder> {

    private List<RewardModel> rewardModelList;
    private Boolean useMiniLayout = false;
    private RecyclerView couponsRecyclerView;
    private LinearLayout selectedCoupon;
    private String productOriginalPrice;
    private TextView selectedCouponTitle;
    private TextView selectedCouponExpiryDate;
    private TextView selectedCouponBody;
    private TextView discountedPrice;
    private int cartItemPosition = -1;
    private List<CartItemModel> cartItemModelList;

    public MyRewardsAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }

    public MyRewardsAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponsRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView couponTitle, TextView couponExpiryDate, TextView couponBody, TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponsRecyclerView = couponsRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = couponTitle;
        this.selectedCouponExpiryDate = couponExpiryDate;
        this.selectedCouponBody = couponBody;
        this.discountedPrice = discountedPrice;
    }

    public MyRewardsAdapter(int cartItemPosition, List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponsRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView couponTitle, TextView couponExpiryDate, TextView couponBody, TextView discountedPrice, List<CartItemModel> cartItemModelList) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponsRecyclerView = couponsRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = couponTitle;
        this.selectedCouponExpiryDate = couponExpiryDate;
        this.selectedCouponBody = couponBody;
        this.discountedPrice = discountedPrice;
        this.cartItemPosition = cartItemPosition;
        this.cartItemModelList = cartItemModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (useMiniLayout) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mini_rewards_item_layout, viewGroup, false);
        }
        else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rewards_item_layout, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String couponId = rewardModelList.get(position).getCouponId();
        String type = rewardModelList.get(position).getType();
        Date validity = rewardModelList.get(position).getTimestamp();
        String body = rewardModelList.get(position).getCouponBody();

        String lowerLimit = rewardModelList.get(position).getLowerLimit();
        String upperLimit = rewardModelList.get(position).getUpperLimit();
        String discountOrAmount = rewardModelList.get(position).getDiscountOrAmount();
        Boolean alreadyUsed = rewardModelList.get(position).isAlreadyUsed();

        viewHolder.setData(couponId, type, validity, body, upperLimit, lowerLimit, discountOrAmount, alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView couponTitle;
        private TextView couponExpiryDate;
        private TextView couponBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponExpiryDate = itemView.findViewById(R.id.coupon_validity);
            couponBody = itemView.findViewById(R.id.coupon_body);
        }

        private void setData(final String couponId, final String type, final Date validity, final String body, final String lowerLimit, final String upperLimit, final String discountOrAmount, final boolean alreadyUsed) {

            if (type.equals("Discount")) {
                couponTitle.setText(type);
            }
            else {
                couponTitle.setText("Mobile $" + discountOrAmount + " OFF");
            }

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
            if (alreadyUsed) {
                couponExpiryDate.setText("Already used");
                couponExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                couponBody.setTextColor(Color.parseColor("#50FFFFFF"));
                couponTitle.setTextColor(Color.parseColor("#50FFFFFF"));
            }
            else {
                couponExpiryDate.setText("till " + simpleDateFormat.format(validity));
                couponExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPurple));
                couponBody.setTextColor(Color.parseColor("#FFFFFF"));
                couponTitle.setTextColor(Color.parseColor("#FFFFFF"));
            }
            couponBody.setText(body);

            if (useMiniLayout) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!alreadyUsed) {

                            selectedCouponTitle.setText(type);
                            selectedCouponExpiryDate.setText(simpleDateFormat.format(validity));
                            selectedCouponBody.setText(body);

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit)) {
                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discountOrAmount) / 100;
                                    discountedPrice.setText("$" + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                } else {
                                    discountedPrice.setText("$" + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discountOrAmount) + "/-"));
                                }
                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(couponId);
                                }
                            } else {
                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(null);
                                }
                                discountedPrice.setText("Invalid");
                                Toast.makeText(itemView.getContext(), "Sorry! Product does't matches the coupon terms.", Toast.LENGTH_SHORT).show();
                            }

                            if (couponsRecyclerView.getVisibility() == View.GONE) {
                                couponsRecyclerView.setVisibility(View.VISIBLE);
                                selectedCoupon.setVisibility(View.GONE);
                            } else {
                                couponsRecyclerView.setVisibility(View.GONE);
                                selectedCoupon.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }
    }
}
