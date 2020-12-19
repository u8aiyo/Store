package com.apuri.sutoaa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private int position;
    private TextView title;
    private TextView price;
    private TextView quantity;
    private ImageView productImage;

    private ImageView orderedIndicator;
    private ImageView packedIndicator;
    private ImageView shippedIndicator;
    private ImageView deliveredIndicator;

    private ProgressBar o_p_progress;
    private ProgressBar p_s_progress;
    private ProgressBar s_d_progress;

    private TextView orderedTitle;
    private TextView packedTitle;
    private TextView shippedTitle;
    private TextView deliveredTitle;
    private TextView orderedDate;
    private TextView packedDate;
    private TextView shippedDate;
    private TextView deliveredDate;
    private TextView orderedBody;
    private TextView packedBody;
    private TextView shippedBody;
    private TextView deliveredBody;

    private LinearLayout rateNowContainer;
    private int rating;

    private TextView fullName;
    private TextView address;
    private TextView pinecode;

    private TextView totalItems;
    private TextView totalItemsPrice;
    private TextView deliveryPrice;
    private TextView totalAmount;
    private TextView savedAmount;

    private Dialog loadingDialog;
    private SimpleDateFormat simpleDateFormat;
    private Button cancelOrderButton;
    private Dialog cancelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //////////////////////// loading dialog
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////////////////// loading dialog

        //////////////////////// cancel dialog
        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(true);
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        //cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////////////////// loading dialog

        position = getIntent().getIntExtra("Position", -1);
        final MyOrderItemModel model = DBqueries.myOrderItemModelList.get(position);

        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        quantity = findViewById(R.id.product_quantity);
        productImage = findViewById(R.id.product_image);
        cancelOrderButton = findViewById(R.id.cancel_button);

        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipping_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        o_p_progress = findViewById(R.id.ordered_packed_progressBar);
        p_s_progress = findViewById(R.id.packed_shipped_progressBar);
        s_d_progress = findViewById(R.id.shipped_delivered_progressBar);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipping_title);
        deliveredTitle = findViewById(R.id.delivered_title);

        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipping_date);
        deliveredDate = findViewById(R.id.delivered_date);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipping_body);
        deliveredBody = findViewById(R.id.delivered_body);

        rateNowContainer = findViewById(R.id.linearLayout10);

        fullName = findViewById(R.id.full_name);
        address = findViewById(R.id.address);
        pinecode = findViewById(R.id.pin_code);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        totalAmount = findViewById(R.id.total_amount);
        savedAmount = findViewById(R.id.saved_amount);

        title.setText(model.getProductTitle());
        if (!model.getDiscountedPrice().equals("")) {
            price.setText("$" + model.getDiscountedPrice() + "/-");
        }
        else {
            price.setText("$" + model.getProductPrice() + "/-");
        }

        quantity.setText("Quantity: " + String.valueOf(model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);
        simpleDateFormat = new SimpleDateFormat(" EEE, dd MMM YYYY - hh:mm aa");

        switch (model.getOrdersStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                o_p_progress.setVisibility(View.GONE);
                p_s_progress.setVisibility(View.GONE);
                s_d_progress.setVisibility(View.GONE);

                orderedTitle.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                orderedDate.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                orderedBody.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                break;
            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setVisibility(View.GONE);
                s_d_progress.setVisibility(View.GONE);

                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setProgress(100);
                s_d_progress.setVisibility(View.GONE);

                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setProgress(100);
                s_d_progress.setProgress(100);

                deliveredTitle.setText("Out of Delivery");
                deliveredBody.setText("Your order is out for delivery.");
                break;
            case "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setProgress(100);
                s_d_progress.setProgress(100);
                break;
            case "Cancelled":
                if (model.getPackedDate().after(model.getOrderedDate())) {

                    if (model.getShippedDate().after(model.getPackedDate())) {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));

                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled.");

                        o_p_progress.setProgress(100);
                        p_s_progress.setProgress(100);
                        s_d_progress.setProgress(100);
                    }
                    else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));

                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled.");

                        o_p_progress.setProgress(100);
                        p_s_progress.setProgress(100);
                        s_d_progress.setVisibility(View.GONE);

                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                    }
                }
                else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));

                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled.");

                    o_p_progress.setProgress(100);
                    p_s_progress.setVisibility(View.GONE);
                    s_d_progress.setVisibility(View.GONE);

                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedIndicator.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }
                break;
        }

        //////////////////////// rating layout
        rating = model.getRating();
        setRating(rating);

        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int startPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingDialog.show();
                    setRating(startPosition);
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCT")
                            .document(model.getProductId());

                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                            if (rating != 0) {
                                Long increase = documentSnapshot.getLong(startPosition + 1 + "_star") + 1;
                                Long decrease = documentSnapshot.getLong(rating + 1 + "_star") - 1;
                                transaction.update(documentReference, startPosition + 1 + "_star", increase);
                                transaction.update(documentReference, rating + 1 + "_star", decrease);
                            }
                            else {
                                Long increase = documentSnapshot.getLong(startPosition + 1 + "_star") + 1;
                                transaction.update(documentReference, startPosition + 1 + "_star", increase);
                            }
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        @Override
                        public void onSuccess(Object o) {

                            Map<String, Object> myRating = new HashMap<>();

                            if (DBqueries.myRatedIds.contains(model.getProductId())) {
                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(model.getProductId()), (long) startPosition + 1);
                            } else {
                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), model.getProductId());
                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) startPosition + 1);
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                    .collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        DBqueries.myOrderItemModelList.get(position).setRating(startPosition);

                                        if (DBqueries.myRatedIds.contains(model.getProductId())) {
                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(model.getProductId()), Long.parseLong(String.valueOf(startPosition + 1)));
                                        }
                                        else {
                                            DBqueries.myRatedIds.add(model.getProductId());
                                            DBqueries.myRating.add(Long.parseLong(String.valueOf(startPosition + 1)));
                                        }
                                    }
                                    else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        }
        //////////////////////// rating layout

        if (model.isCancellationRequested()) {
            cancelOrderButton.setVisibility(View.VISIBLE);
            cancelOrderButton.setEnabled(false);
            cancelOrderButton.setText("Cancellation in process.");
            cancelOrderButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            cancelOrderButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
        else {
            if (model.getOrdersStatus().equals("Ordered") || model.getOrdersStatus().equals("Packed")) {
                cancelOrderButton.setVisibility(View.VISIBLE);
                cancelOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.show();
                        cancelDialog.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();

                                Map<String, Object> map = new HashMap<>();
                                map.put("Order Id", model.getOrderID());
                                map.put("product Id", model.getOrderID());
                                map.put("Order Cancelled", false);

                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document()
                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderID())
                                                    .collection("OrderItems").document(model.getProductId())
                                                    .update("Cancellation requested", true)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                model.setCancellationRequested(true);
                                                                cancelOrderButton.setEnabled(false);
                                                                cancelOrderButton.setText("Cancellation in process.");
                                                                cancelOrderButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                                cancelOrderButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                                                            }
                                                            else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }
                                        else {
                                            loadingDialog.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                        cancelDialog.show();
                    }
                });
            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pinecode.setText(model.getPinecode());

        totalItems.setText("Price (" + model.getProductQuantity() + " items)");

        Long totalItemsPriceValue;

        if (model.getDiscountedPrice().equals("")) {
            totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getProductPrice());
            totalItemsPrice.setText("$" + totalItemsPriceValue + "/-");
        }
        else {
            totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getDiscountedPrice());
            totalItemsPrice.setText("$" + totalItemsPriceValue + "/-");
        }

        if (model.getDeliveryPrice().equals("FREE")) {
            deliveryPrice.setText(model.getDeliveryPrice());
            totalAmount.setText(totalItemsPrice.getText());
        }
        else {
            deliveryPrice.setText("$" + model.getDeliveryPrice() + "/-");
            totalAmount.setText("$" + (totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice())) + "/-");
        }

        if (!model.getCuttedPrice().equals("")) {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved $" + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order");
            }
            else {
                savedAmount.setText("You saved $" + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getProductPrice())) + "/- on this order");
            }
        }
        else {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved $" + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getDeliveryPrice())) + "/- on this order");
            }
            else {
                savedAmount.setText("You saved $0/- on this order");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRating(int startPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView startBtn = (ImageView) rateNowContainer.getChildAt(x);
            startBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#BEBEBE")));

            if (x <= startPosition) {
                startBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFBB00")));
            }
        }
    }
}