package com.apuri.sutoaa;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteButton;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteButton) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteButton = showDeleteButton;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup, false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_total_amount_layout, viewGroup, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freeCoupons = cartItemModelList.get(position).getFreeCoupons();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                Long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean COD = cartItemModelList.get(position).isCOD();
                ((CartItemViewHolder) viewHolder).setItemDetails(productID, resource, title, freeCoupons, productPrice, cuttedPrice, offersApplied, position, inStock, String.valueOf(productQuantity), maxQuantity, qtyError, qtyIds, stockQty, COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;

                for (int x = 0; x < cartItemModelList.size(); x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice()) * quantity;
                        }
                        else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()) * quantity;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())) {
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        }
                        else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        }
                    }
                }

                if (totalItemPrice > 100) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                }
                else {
                    deliveryPrice = "10";
                    totalAmount = totalItemPrice + 10;
                }

                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemPrice);
                cartItemModelList.get(position).setDiscountedPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewHolder) viewHolder).setTotalAmount(totalItems, totalItemPrice, deliveryPrice, totalAmount, savedAmount);
                break;
            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView freeCouponIcon;
        private TextView productTitle;
        private TextView freeCoupons;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView productQuantity;
        private TextView offersApplied;
        private TextView couponsApplied;
        private LinearLayout couponRedemptionLayout;
        private TextView couponRedemptionBody;

        private LinearLayout deleteButton;
        private Button redeemButton;
        private ImageView codeIndicator;

        //////////////////////// coupon dialog
        private TextView couponTitle;
        private TextView couponExpiryDate;
        private TextView couponBody;
        private RecyclerView couponsRecyclerView;
        private LinearLayout selectedCoupon;
        private TextView discountedPrice;
        private TextView originalPrice;
        private LinearLayout applyOrRemoveButtonContainer;
        private TextView footerText;
        private Button removeCouponButton;
        private Button applyCouponButton;
        private String productOriginalPrice;
        //////////////////////// coupon dialog

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            freeCouponIcon = itemView.findViewById(R.id.free_coupon_icon);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupons = itemView.findViewById(R.id.free_coupon_textView);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            offersApplied = itemView.findViewById(R.id.offers_applied);
            couponsApplied = itemView.findViewById(R.id.coupons_applied);
            couponRedemptionLayout = itemView.findViewById(R.id.coupon_redemption_layout);
            couponRedemptionBody = itemView.findViewById(R.id.coupon_redemption_textView);

            codeIndicator = itemView.findViewById(R.id.code_indicator);

            deleteButton = itemView.findViewById(R.id.remove_item_button);
            redeemButton = itemView.findViewById(R.id.coupon_redemption_button);
        }

        private void setItemDetails(final String productID, String resource, String title, Long freeCouponsNo, final String productPriceText, String cuttedPriceText, Long offerAppliedNo, final int position, boolean inStock, final String quantity, final Long maxQuantity, boolean qtyError, final List<String> qtyIds, final long stockQty, boolean COD) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.loading_1)).into(productImage);
            productTitle.setText(title);

            final Dialog checkCouponPriceDialog = new Dialog(itemView.getContext());
            checkCouponPriceDialog.setContentView(R.layout.coupon_redeem_dialog);
            checkCouponPriceDialog.setCancelable(false);
            checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (COD) {
                codeIndicator.setVisibility(View.VISIBLE);
            }
            else {
                codeIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock) {

                if (freeCouponsNo > 0) {
                    freeCouponIcon.setVisibility(View.VISIBLE);
                    freeCoupons.setVisibility(View.VISIBLE);
                    if (freeCouponsNo == 1) {
                        freeCoupons.setText("free " + freeCouponsNo + " Coupon");
                    }
                    else {
                        freeCoupons.setText("free " + freeCouponsNo + " Coupons");
                    }
                }
                else {
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                    freeCoupons.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("$" + productPriceText + "/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("$" + cuttedPriceText + "/-");
                couponRedemptionLayout.setVisibility(View.VISIBLE);

                //////////////////////// coupon dialog
                ImageView openCouponRecyclerView = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerView);
                couponsRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerView);
                selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);

                couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
                couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
                couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);

                applyOrRemoveButtonContainer = checkCouponPriceDialog.findViewById(R.id.apply_remove_button_container);
                footerText = checkCouponPriceDialog.findViewById(R.id.footer_text);
                removeCouponButton = checkCouponPriceDialog.findViewById(R.id.remove_button);
                applyCouponButton = checkCouponPriceDialog.findViewById(R.id.apply_button);

                applyOrRemoveButtonContainer.setVisibility(View.VISIBLE);
                footerText.setVisibility(View.GONE);

                originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
                discountedPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);


                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                couponsRecyclerView.setLayoutManager(linearLayoutManager);

                //////////////////////// for coupon dialog
                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productPriceText;

                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(position, DBqueries.rewardModelList, true, couponsRecyclerView, selectedCoupon, productOriginalPrice, couponTitle, couponExpiryDate, couponBody, discountedPrice, cartItemModelList);
                couponsRecyclerView.setAdapter(myRewardsAdapter);
                myRewardsAdapter.notifyDataSetChanged();
                //////////////////////// for coupon dialog

                applyCouponButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                                    couponRedemptionBody.setText(rewardModel.getCouponBody());
                                    redeemButton.setText("Coupon");
                                }
                            }
                            couponsApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmount = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2)));
                            couponsApplied.setText("Coupon applied -" + offerDiscountedAmount);
                            notifyItemChanged(cartItemModelList.size() - 1);
                            checkCouponPriceDialog.dismiss();
                        }

                    }
                });

                removeCouponButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        couponTitle.setText("Coupon");
                        couponExpiryDate.setText("validity");
                        couponBody.setText("Tape the icon on the top right center to select your coupon.");
                        couponsApplied.setVisibility(View.INVISIBLE);
                        couponRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorCoupon));
                        couponRedemptionBody.setText("Apply your coupon here");
                        redeemButton.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCouponId(null);
                        productPrice.setText("$" + productPriceText + "/-");
                        notifyItemChanged(cartItemModelList.size() - 1);
                        checkCouponPriceDialog.dismiss();
                    }
                });

                openCouponRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showDialogRecyclerView();

                    }
                });
                //////////////////////// coupon dialog

                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                            couponRedemptionBody.setText(rewardModel.getCouponBody());
                            redeemButton.setText("Coupon");
                            couponBody.setText(rewardModel.getCouponBody());
                            if (rewardModel.getType().equals("Discount")) {
                                couponTitle.setText(rewardModel.getType());
                            }
                            else {
                                couponTitle.setText("Mobile $" + rewardModel.getDiscountOrAmount() + " OFF");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                            couponExpiryDate.setText("till " + simpleDateFormat.format(rewardModel.getTimestamp()));
                        }
                    }
                    discountedPrice.setText("$" + cartItemModelList.get(position).getDiscountedPrice() + "/-");
                    couponsApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("$" + cartItemModelList.get(position).getDiscountedPrice() + "/-");
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cartItemModelList.get(position).getProductPrice()));
                    couponsApplied.setText("Coupon applied -$" + offerDiscountedAmount + "/-");
                }
                else {
                    couponsApplied.setVisibility(View.INVISIBLE);
                    couponRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorCoupon));
                    couponRedemptionBody.setText("Apply your coupon here");
                    redeemButton.setText("Redeem");
                }

                productQuantity.setText("Qty: " + quantity);

                if (!showDeleteButton) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelButton = quantityDialog.findViewById(R.id.cancel_button);
                        Button okButton = quantityDialog.findViewById(R.id.ok_button);
                        quantityNo.setHint("Max " + String.valueOf(maxQuantity));

                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {

                                        if (itemView.getContext() instanceof MainActivity) {
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        }
                                        else {

                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() - 1);

                                        if (!showDeleteButton) {
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(quantity);
                                            final int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQty > initialQty) {
                                                for (int y = 0; y < finalQty - initialQty; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                                                    Map<String, Object> timestamp = new HashMap<>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());
                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    qtyIds.add(quantityDocumentName);

                                                                    if (finalY + 1 == finalQty - initialQty) {
                                                                        firebaseFirestore.collection("PRODUCTS").document(productID)
                                                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                                                .limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            List<String> serverQuantity = new ArrayList<>();
                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }
                                                                                            long availableQty = 0;
                                                                                            for (String qtyId : qtyIds) {
                                                                                                if (!serverQuantity.contains(qtyId)) {
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry! All products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                                else {
                                                                                                    availableQty++;
                                                                                                }

                                                                                            }
                                                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                                                        } else {
                                                                                            //// error
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                            else if (initialQty > finalQty) {
                                                for (int x = 0; x < initialQty - finalQty; x++) {
                                                    final String qtyId = qtyIds.get(qtyIds.size() - 1 - x);
                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(qtyId).delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            qtyIds.remove(qtyId);
                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                            if (finalX + 1 == initialQty - finalQty) {
                                                                DeliveryActivity.loadingDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                    }
                                    else {
                                        Toast.makeText(itemView.getContext(), "Max quantity " + maxQuantity.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });

                        quantityDialog.show();
                    }
                });

                if (offerAppliedNo > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(cuttedPriceText) - Long.valueOf(productPriceText));
                    offersApplied.setText("Offer applied -$" + offerDiscountedAmount + "/-");
                }
                else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            }
            else {
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setText("");
                couponRedemptionLayout.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                /*productQuantity.setText("Qty: " + 0);
                productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
                productQuantity.setTextColor(Color.parseColor("#70000000"));
                productQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));*/
                couponsApplied.setVisibility(View.GONE);
                offersApplied.setVisibility(View.GONE);
                freeCouponIcon.setVisibility(View.INVISIBLE);
            }

            if (showDeleteButton) {
                deleteButton.setVisibility(View.VISIBLE);
            }
            else {
                deleteButton.setVisibility(View.GONE);
            }



            redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCouponPriceDialog.show();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }
                        if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }

        private void showDialogRecyclerView() {
            if (couponsRecyclerView.getVisibility() == View.GONE) {
                couponsRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupon.setVisibility(View.GONE);
            }
            else {
                couponsRecyclerView.setVisibility(View.GONE);
                selectedCoupon.setVisibility(View.VISIBLE);
            }
        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalItems;
        private TextView totalItemPrice;
        private TextView totalAmount;
        private TextView deliveryPrice;
        private TextView savedAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            totalAmount = itemView.findViewById(R.id.total_amount);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount (int totalItemsText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price (" + totalItemsText + " items)");
            totalItemPrice.setText("$" + totalItemPriceText + "/-");
            totalAmount.setText("$" + totalAmountText + "/-");
            cartTotalAmount.setText("$" + totalAmountText + "/-");
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            }
            else {
                deliveryPrice.setText("$" + deliveryPriceText + "/-");
            }
            savedAmount.setText("You saved $" + savedAmountText + "/- on this order.");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteButton) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }
            else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
