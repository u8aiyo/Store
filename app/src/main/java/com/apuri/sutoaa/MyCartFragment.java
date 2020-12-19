package com.apuri.sutoaa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Button continueButton;
    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    private TextView totalAmount;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCartFragment newInstance(String param1, String param2) {
        MyCartFragment fragment = new MyCartFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        //////////////////////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////////////////// loading dialog

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerView);
        totalAmount = view.findViewById(R.id.total_cart_amount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);


        continueButton = view.findViewById(R.id.cart_continue_button);

        cartAdapter = new CartAdapter(DBqueries.cartItemModelList, totalAmount, true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;
                for (int x = 0; x < DBqueries.cartItemModelList.size(); x++) {
                    CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()) {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();

                if (DBqueries.addressesModelList.size() == 0) {
                    DBqueries.loadAddresses(getContext(), loadingDialog, true);
                }
                else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();

        if (DBqueries.rewardModelList.size() == 0) {
            loadingDialog.show();
            DBqueries.loadRewards(getContext(), loadingDialog, false);
        }

        if (DBqueries.cartItemModelList.size() == 0) {
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        }
        else {
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT) {
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (CartItemModel cartItemModel : DBqueries.cartItemModelList) {
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCouponId())) {
                for (RewardModel rewardModel : DBqueries.rewardModelList) {
                    if (rewardModel.getCouponId().equals(cartItemModel.getSelectedCouponId())) {
                        rewardModel.setAlreadyUsed(false);
                    }
                }
                cartItemModel.setSelectedCouponId(null);
                if (MyRewardsFragment.myRewardsAdapter != null) {
                    MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}