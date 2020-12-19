package com.apuri.sutoaa;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRewardsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    private RecyclerView my_rewards_recyclerView;

    private Dialog loadingDialog;
    public static MyRewardsAdapter myRewardsAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyRewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRewardsFragment newInstance(String param1, String param2) {
        MyRewardsFragment fragment = new MyRewardsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        //////////////////////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////////////////// loading dialog

        my_rewards_recyclerView = view.findViewById(R.id.my_rewards_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        my_rewards_recyclerView.setLayoutManager(linearLayoutManager);

        myRewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList, false);
        my_rewards_recyclerView.setAdapter(myRewardsAdapter);

        if (DBqueries.rewardModelList.size() == 0) {
            DBqueries.loadRewards(getContext(), loadingDialog, true);
        }
        else {
            loadingDialog.dismiss();
        }

        myRewardsAdapter.notifyDataSetChanged();

        return view;
    }
}