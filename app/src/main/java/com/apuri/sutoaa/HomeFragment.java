package com.apuri.sutoaa;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.apuri.sutoaa.DBqueries.categoryModelList;
import static com.apuri.sutoaa.DBqueries.firebaseFirestore;
import static com.apuri.sutoaa.DBqueries.lists;
import static com.apuri.sutoaa.DBqueries.loadCategories;
import static com.apuri.sutoaa.DBqueries.loadFragmentData;
import static com.apuri.sutoaa.DBqueries.loadedCategoriesNames;
//import static com.apuri.sutoaa.DBqueries.setFragmentData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryRecyclerView;
    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter adapter;
    private ImageView noInternetConnection;
    private Button retryButton;
    //private List<CategoryModel> categoryModelList;
    //private FirebaseFirestore firebaseFirestore;

    /*
    /////////////////////////// Banner Slider
    private ViewPager bannerSliderViewPager;
    private List<SliderModel> sliderModelList;
    private int currentPage = 2;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    /////////////////////////// Banner Slider

    /////////////////////////// Strip Ad
    private ImageView stripAdImage;
    private ConstraintLayout stripAdContainer;
    /////////////////////////// Strip Ad

    /////////////////////////// Horizontal Product Layout
    private TextView horizontalLayoutTitle;
    private Button horizontalLayoutViewAllButton;
    private RecyclerView horizontalRecyclerView;
    /////////////////////////// Horizontal Product Layout
     */

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        categoryRecyclerView = view.findViewById(R.id.category_recyclerView);
        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerview);
        retryButton = view.findViewById(R.id.retry_button);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary), getContext().getResources().getColor(R.color.colorPrimary), getContext().getResources().getColor(R.color.colorPrimary));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);

        /////////////////////////// categories fake list
        categoryModelFakeList.add(new CategoryModel("null", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        /////////////////////////// categories fake list

        /////////////////////////// home page fake list
        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null", "#DFDFDF"));
        sliderModelFakeList.add(new SliderModel("null", "#DFDFDF"));
        sliderModelFakeList.add(new SliderModel("null", "#DFDFDF"));
        sliderModelFakeList.add(new SliderModel("null", "#DFDFDF"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));

        homePageModelFakeList.add(new HomePageModel(0, sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1, "", "#DFDFDF"));
        homePageModelFakeList.add(new HomePageModel(2, "", "#DFDFDF", horizontalProductScrollModelFakeList, new ArrayList<WishListModel>()));
        homePageModelFakeList.add(new HomePageModel(3, "", "#DFDFDF", horizontalProductScrollModelFakeList));
        /////////////////////////// home page fake list

        categoryAdapter = new CategoryAdapter(categoryModelFakeList);
        adapter = new HomePageAdapter(homePageModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            if (categoryModelList.size() == 0) {
                loadCategories(categoryRecyclerView, getContext());
            }
            else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);

            if (lists.size() == 0) {
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(homePageRecyclerView, getContext(), 0, "Home");
            }
            else {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);

        }
        else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.internet_offine).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        }

        /////////////////////////// Refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });
        /////////////////////////// Refresh layout

        /////////////////////////// Banner Slider
        //bannerSliderViewPager = view.findViewById(R.id.banner_slider_viewPager);
        /*List<SliderModel> sliderModelList = new ArrayList<SliderModel>();

        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));
        sliderModelList.add(new SliderModel(R.drawable.banner, "#D63362"));*/
        /*
        SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList);
        bannerSliderViewPager.setAdapter(sliderAdapter);
        bannerSliderViewPager.setClipToPadding(false);
        bannerSliderViewPager.setPageMargin(20);

        bannerSliderViewPager.setCurrentItem(currentPage);

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    pageLooper();
                }
            }
        };
        bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

        startBannerSlideShow();

        bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pageLooper();
                stopBannerSlideShow();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startBannerSlideShow();
                }
                return false;
            }
        });
         */
        /////////////////////////// Banner Slider
        /*
        /////////////////////////// Strip Ad
        stripAdImage = view.findViewById(R.id.strip_ad_image);
        stripAdContainer = view.findViewById(R.id.strip_ad_container);
        stripAdImage.setImageResource(R.drawable.banner_7);
        stripAdContainer.setBackgroundColor(Color.parseColor("#A80D24"));
        /////////////////////////// Strip Ad
         */
        /////////////////////////// Horizontal Product Layout
        //horizontalLayoutTitle = view.findViewById(R.id.horizontal_scroll_layout_title);
        //horizontalLayoutViewAllButton = view.findViewById(R.id.horizontal_scroll_view_all_button);
        //horizontalRecyclerView = view.findViewById(R.id.horizontal_scroll_RecyclerView);
        /*List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.image2, "Iphone 8", "2.39 GHz hexa-core 64-bit", "$449.99"));

        HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);*/
        /*horizontalRecyclerView.setLayoutManager(linearLayoutManager);
        horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
        horizontalProductScrollAdapter.notifyDataSetChanged();*/
        /////////////////////////// Horizontal Product Layout

        /////////////////////////// Grid Product Layout
        /*TextView gridLayoutTitle = view.findViewById(R.id.grid_product_layout_title);
        Button gridLayoutViewAllButton = view.findViewById(R.id.grid_product_layout_viewAll_button);
        GridView gridView = view.findViewById(R.id.grid_product_layout_gridView);

        gridView.setAdapter(new GridProductLayoutAdapter(horizontalProductScrollModelList));*/
        /////////////////////////// Grid Product Layout

        /////////////////////////// Testing Recycler Layout

        /////////////////////////// Testing Recycler Layout

        return view;
    }

    /*
    /////////////////////////// Banner Slider
    private void pageLooper() {
        if (currentPage == sliderModelList.size() - 2) {
            currentPage = 2;
            bannerSliderViewPager.setCurrentItem(currentPage, false);
        }
        if (currentPage == 1) {
            currentPage = sliderModelList.size() - 3;
            bannerSliderViewPager.setCurrentItem(currentPage, false);
        }
    }

    private void startBannerSlideShow() {
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage >= sliderModelList.size()) {
                    currentPage = 1;
                }
                bannerSliderViewPager.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    private void stopBannerSlideShow() {
        timer.cancel();
    }
    /////////////////////////// Banner Slider
     */

    private void reloadPage() {
        noInternetConnection.setVisibility(View.GONE);

        networkInfo = connectivityManager.getActiveNetworkInfo();

        //categoryModelList.clear();
        //lists.clear();
        //loadedCategoriesNames.clear();
        DBqueries.clearData();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            noInternetConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            adapter = new HomePageAdapter(homePageModelFakeList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homePageRecyclerView.setAdapter(adapter);

            loadCategories(categoryRecyclerView, getContext());

            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePageRecyclerView, getContext(), 0, "Home");
        }
        else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);

            Glide.with(getContext()).load(R.drawable.internet_offine).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}