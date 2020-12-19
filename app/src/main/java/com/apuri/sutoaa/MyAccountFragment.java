package com.apuri.sutoaa;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton settingsButton;
    private Button viewAllAddressButton;
    private Button signOutButton;
    public static final int MANAGE_ADDRESS = 1;
    private CircleImageView profileView;
    private CircleImageView currentOrderImage;
    private TextView fullname;
    private TextView email;
    private TextView currentOrderStatusTextView;
    private ImageView verified;
    private ImageView orderedIndicator;
    private ImageView packedIndicator;
    private ImageView shippingIndicator;
    private ImageView deliveredIndicator;
    private ProgressBar o_p_progressBar;
    private ProgressBar p_s_progressBar;
    private ProgressBar s_d_progressBar;
    private LinearLayout layoutContainer;

    private LinearLayout recentOrdersContainer;
    private TextView yourRecentOrderTitle;

    private TextView addressname;
    private TextView address;
    private TextView pincode;

    private Dialog loadingDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        //////////////////////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////////////////// loading dialog

        profileView = view.findViewById(R.id.profile_circleImageView);
        fullname = view.findViewById(R.id.username);
        email = view.findViewById(R.id.user_email);
        verified = view.findViewById(R.id.verified_icon);
        currentOrderImage = view.findViewById(R.id.current_order_circleImageView);
        currentOrderStatusTextView = view.findViewById(R.id.current_order_status_textView);

        orderedIndicator = view.findViewById(R.id.ordered_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippingIndicator = view.findViewById(R.id.shipping_indicator);
        deliveredIndicator = view.findViewById(R.id.delivered_indicator);

        o_p_progressBar = view.findViewById(R.id.ordered_packed_progressBar);
        p_s_progressBar = view.findViewById(R.id.packed_shipped_progressBar);
        s_d_progressBar = view.findViewById(R.id.shipped_delivered_progressBar);

        recentOrdersContainer = view.findViewById(R.id.recent_orders_container);
        yourRecentOrderTitle = view.findViewById(R.id.your_recent_order_title);

        addressname = view.findViewById(R.id.address_fullName);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.address_pinCode);

        layoutContainer = view.findViewById(R.id.layout_container);

        Glide.with(getContext()).load(DBqueries.verified).apply(new RequestOptions().placeholder(R.drawable.verified_account)).into(verified);

        layoutContainer.getChildAt(1).setVisibility(View.GONE);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                for (MyOrderItemModel orderItemModel : DBqueries.myOrderItemModelList) {
                    if (!orderItemModel.isCancellationRequested()) {
                        if (!orderItemModel.getOrdersStatus().equals("Delivered") && !orderItemModel.getOrdersStatus().equals("Cancelled")) {

                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(currentOrderImage);
                            currentOrderStatusTextView.setText(orderItemModel.getOrdersStatus());

                            switch (orderItemModel.getOrdersStatus()) {
                                case "Ordered":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    break;
                                case "Packed":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    o_p_progressBar.setProgress(100);
                                    break;
                                case "Shipped":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    shippingIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    o_p_progressBar.setProgress(100);
                                    p_s_progressBar.setProgress(100);
                                    break;
                                case "Out for Delivery":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    shippingIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
                                    o_p_progressBar.setProgress(100);
                                    p_s_progressBar.setProgress(100);
                                    s_d_progressBar.setProgress(100);
                                    break;
                            }
                        }
                    }
                }

                int i = 0;
                for (MyOrderItemModel myOrderItemModel : DBqueries.myOrderItemModelList) {
                    if (i < 4) {
                        if (myOrderItemModel.getOrdersStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                            i++;
                        }
                    }
                    else {
                        break;
                    }
                }

                if (i == 0) {
                    yourRecentOrderTitle.setText("No recent Orders.");
                }

                if (i < 3) {
                    for (int x = i; x < 4; x++) {
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }

                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DBqueries.addressesModelList.size() == 0) {
                            addressname.setText("No Address");
                            address.setText("-");
                            pincode.setText("-");
                        }
                        else {
                            setAddress();
                        }
                    }
                });

                DBqueries.loadAddresses(getContext(), loadingDialog, false);
            }
        });

        DBqueries.loadOrders(getContext(), null, loadingDialog);
        viewAllAddressButton = view.findViewById(R.id.viewAll_address_button);
        viewAllAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyAddressesActivity.class);
                intent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(intent);
            }
        });

        signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                Intent registerIntent = new Intent(getContext(), RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });

        settingsButton = view.findViewById(R.id.settings_floatingActionButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name", fullname.getText());
                updateUserInfo.putExtra("Email", email.getText());
                updateUserInfo.putExtra("Photo", DBqueries.profile);
                startActivity(updateUserInfo);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fullname.setText(DBqueries.fullname);
        email.setText(DBqueries.email);
        if (!DBqueries.profile.equals("")) {
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_placeholder)).into(profileView);
        }
        else {
            profileView.setImageResource(R.drawable.profile_placeholder);
        }

        if (!loadingDialog.isShowing()) {
            if (DBqueries.addressesModelList.size() == 0) {
                addressname.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            }
            else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String nametext, mobileNo;
        nametext = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            addressname.setText(nametext + " - " + mobileNo);
        }
        else {
            addressname.setText(nametext + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getStateSpinner();

        if (landmark.equals("")) {
            address.setText(flatNo + " " + locality + " " + city + " " + state);
        }
        else {
            address.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPinCode());
    }
}