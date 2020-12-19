package com.apuri.sutoaa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;*/
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
/*import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    public static boolean ordered = false;  //my new code
    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRecyclerView;
    public static CartAdapter cartAdapter;
    private Button changeOrAddNewAddressButton;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private TextView fullname;
    private TextView fulladdress;
    private TextView pincode;
    private Button continueButton;
    public static Dialog loadingDialog;

    private boolean successRespones = false;
    public static boolean fromCart;
    private String order_id;
    public static boolean codOrderConfirmed = false;

    private FirebaseFirestore firebaseFirestore;
    //public static boolean allProductsAvailable;
    public static boolean getQtyIDs = true;

    private Dialog paymentMethodDialog;
    private TextView codeTitle;
    private View divider;
    private ImageButton paytm, cod;
    private String paymentMethod = "PayPal";
    private String name, mobileNo;

    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingButton;
    private TextView orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        //////////////////////// loading dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////////////////// loading dialog

        //////////////////////// paymentMethod dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        codeTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.payment_divider);
        //////////////////////// paymentMethod dialog

        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;
        //allProductsAvailable = true;

        order_id = UUID.randomUUID().toString().substring(0, 28);

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerView);
        changeOrAddNewAddressButton = findViewById(R.id.change_or_add_address_button);
        totalAmount = findViewById(R.id.total_cart_amount);

        fullname = findViewById(R.id.full_name);
        fulladdress = findViewById(R.id.address);
        pincode = findViewById(R.id.pin_code);
        continueButton = findViewById(R.id.cart_continue_button);

        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingButton = findViewById(R.id.continue_shopping_button);
        orderId = findViewById(R.id.order_id);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressButton.setVisibility(View.VISIBLE);
        changeOrAddNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQtyIDs = false;
                Intent intent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                intent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(intent);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allProductsAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductsAvailable = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codeTitle.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codeTitle.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductsAvailable) {
                    paymentMethodDialog.show();
                }
                //orderId.setText("ORDER ID " + bundle.getString);

                /*if (MainActivity.mainActivity != null) {
                    MainActivity.mainActivity.finish();
                    MainActivity.mainActivity = null;
                    MainActivity.showCart = false;
                }

                if (ProductDetailsActivity.productDetailsActivity != null) {
                    ProductDetailsActivity.productDetailsActivity.finish();
                    ProductDetailsActivity.productDetailsActivity = null;
                }*/

                /*orderConfirmationLayout.setVisibility(View.VISIBLE);
                continueShoppingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });*/

                //////////// IMPORTANT
                /*successRespones = true;
                if (fromCart) {
                    loadingDialog.show();
                    Map<String, Object> updateCartList = new HashMap<>();
                    long cartListSize = 0;
                    final List<Integer> indexList = new ArrayList<>();
                    for (int x = 0; x < DBqueries.cartList.size(); x++) {
                        if (!cartItemModelList.get(x).isInStock()) {
                            updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                            cartListSize++;
                        }
                        else {
                            indexList.add(x);
                        }
                    }
                    updateCartList.put("list_size", cartListSize);
                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA").document("MY_CART").set(updateCartList)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        for (int x = 0; x < indexList.size(); x++) {
                                            DBqueries.cartList.remove(indexList.get(x).intValue());
                                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                            DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                }*/

            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod = "COD";
                placeOrderDetails();
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeliveryActivity.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(DeliveryActivity.this, PaypalActivity.class));
                //paymentMethodDialog.dismiss();
                //loadingDialog.show();
                paymentMethod = "PayPal";
                placeOrderDetails();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Accessing quantity
        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                            .collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {
                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID())
                                                    .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                    .limit(cartItemModelList.get(finalX).getProductQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<>();
                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }
                                                                long availableQty = 0;
                                                                boolean notLongerAvailableQty = true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                    cartItemModelList.get(finalX).setQtyError(false);
                                                                    if (!serverQuantity.contains(qtyId)) {
                                                                        if (notLongerAvailableQty) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        }
                                                                        else {
                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry! All products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        //allProductsAvailable = false;
                                                                    }
                                                                    else {
                                                                        availableQty++;
                                                                        notLongerAvailableQty = false;
                                                                    }

                                                                }
                                                                cartAdapter.notifyDataSetChanged();
                                                            }
                                                            else {
                                                                //// error
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }

                                    }
                                    else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        }
        else {
            getQtyIDs = true;
        }
        //Accessing quantity

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullname.setText(name + " - " + mobileNo);
        }
        else {
            fullname.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getStateSpinner();

        if (landmark.equals("")) {
            fulladdress.setText(flatNo + " " + locality + " " + city + " " + state);
        }
        else {
            fulladdress.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPinCode());

        if (codOrderConfirmed) {
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                if (!successRespones) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                                .collection("QUANTITY").document(qtyID)
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                    cartItemModelList.get(finalX).getQtyIDs().clear();
                                }
                            }
                        });
                    }
                }
                else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successRespones) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout() {
        successRespones = true;
        codOrderConfirmed = false;
        getQtyIDs = false;

        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                        .collection("QUANTITY").document(qtyID)
                        .update("user_ID", FirebaseAuth.getInstance().getUid());
            }
        }

        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showCart = false;
        }
        else {
            MainActivity.resetMainActivity = true;
        }

        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }

        //////////////// Send confirmation SMS
        /*String SMS_API = "";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("MID", M_id);
                paramMap.put("ORDER_ID", order_id);
                paramMap.put("CUST_ID", customer_id);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                paramMap.put("CALLBACK_URL", callBackUrl);
                return paramMap;
            }
        };*/
        //////////////// Send confirmation SMS

        //RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        //requestQueue.add(stringRequest);

        if (fromCart) {
            loadingDialog.show();

            Map<String, Object> updateCartList = new HashMap<>();

            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();

            for (int x = 0;  x < DBqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                }
                else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        for (int x = 0; x < indexList.size(); x++) {
                            DBqueries.cartList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                        }
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }

        continueButton.setEnabled(false);
        changeOrAddNewAddressButton.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order ID " + order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void placeOrderDetails() {
        String userID = FirebaseAuth.getInstance().getUid();
        loadingDialog.show();

        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("Product Id", cartItemModel.getProductID());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                orderDetails.put("User Id", userID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());

                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                }
                else {
                    orderDetails.put("Cutted Price", "");
                }

                orderDetails.put("Product Price", cartItemModel.getProductPrice());

                if (cartItemModel.getSelectedCouponId() != null) {
                    orderDetails.put("Coupon Id", cartItemModel.getSelectedCouponId());
                }
                else {
                    orderDetails.put("Coupon Id", "");
                }

                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                }
                else {
                    orderDetails.put("Discounted Price", "");
                }

                orderDetails.put("Ordered date", FieldValue.serverTimestamp());
                orderDetails.put("Packed date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date", FieldValue.serverTimestamp());
                orderDetails.put("Order Status", "Ordered");

                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", fulladdress.getText());
                orderDetails.put("FullName", fullname.getText());
                orderDetails.put("Pincode", pincode.getText());

                orderDetails.put("Free Coupons", cartItemModel.getFreeCoupons());
                orderDetails.put("Delivery Price", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("Cancellation requested", false);

                firebaseFirestore.collection("ORDERS").document(order_id)
                        .collection("OrderItems").document(cartItemModel.getProductID())
                .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount", cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status", "not Paid");
                orderDetails.put("Order Status", "Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (paymentMethod.equals("PayPal")) {
                                paytm();
                            }
                            else {
                                code();
                            }
                        }
                        else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

     private void paytm(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        //loadingDialog.show();

        /*if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        final String M_id = "YlutwW84809962366406";
        final String customer_id = FirebaseAuth.getInstance().getUid();

        final String url = "https://mymalll.000webhostapp.com/paytm/generateChecksum.php";
        ////////////////////////////////////////
        final String order_id = UUID.randomUUID().toString().substring(0, 28);
        ////////////////////////////////////////
        final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        final RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("CHECKSUMHASH")) {
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
                        PaytmPGService paytmPGService = PaytmPGService.getStagingService(url);
                        HashMap<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("MID", M_id);
                        paramMap.put("ORDER_ID", order_id);
                        paramMap.put("CUST_ID", customer_id);
                        paramMap.put("CHANNEL_ID", "WAP");
                        paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
                        paramMap.put("WEBSITE", "WEBSTAGING");
                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                        paramMap.put("CALLBACK_URL", callBackUrl);
                        paramMap.put("CHECKSUMHASH", CHECKSUMHASH);

                        PaytmOrder order = new PaytmOrder(paramMap);
                        paytmPGService.initialize(order, null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle bundle) {
                                //Toast.makeText(getApplicationContext(), "Payment Transaction response " + bundle.toString(), Toast.LENGTH_LONG).show();
                                if (bundle.getString("STATES").equals("TXN_SUCCESS") {
                                    if (x == cartItemModelList.size() - 2) {

                                        Map<String, Object> updateStatus = new HashMap<>();
                                        updateStatus.put("Payment Status", "Paid");
                                        updateStatus.put("Order Status", "Ordered");
                                        firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showConfirmationLayout()
                                                    }
                                                    else {
                                                        //Toast.makeText(DeliveryActivity.this, "Order CANCELLED!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                     }
                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String s) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: server error" + s, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String s) {
                                Toast.makeText(getApplicationContext(), "UI error " + s, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int i, String s, String s1) {
                                Toast.makeText(getApplicationContext(), "Unable to load web page  " + s.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled! ", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionCancel(String s, Bundle bundle) {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled " + bundle.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("MID", M_id);
                paramMap.put("ORDER_ID", order_id);
                paramMap.put("CUST_ID", customer_id);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                paramMap.put("CALLBACK_URL", callBackUrl);
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);*/
    }

    private void code() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this, OTPverificationActivity.class);
        otpIntent.putExtra("mobileNo", mobileNo.substring(0, 10));
        otpIntent.putExtra("OrderID", order_id);
        startActivity(otpIntent);
    }
}