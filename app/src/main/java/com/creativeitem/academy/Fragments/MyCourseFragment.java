package com.creativeitem.academy.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.creativeitem.academy.Activities.SignInActivity;
import com.creativeitem.academy.Adapters.MyCourseAdapter;
import com.creativeitem.academy.JSONSchemas.CourseSchema;
import com.creativeitem.academy.Models.MyCourse;
import com.creativeitem.academy.Models.Price;
import com.creativeitem.academy.Network.Api;
import com.creativeitem.academy.R;
import com.creativeitem.academy.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyCourseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MyCourseFragment";
    GridView myCoursesGridLayout;
    private ProgressBar progressBar;
    Button signInButton;
    RelativeLayout myCourseView;
    RelativeLayout signInPlaceholder;
    RelativeLayout mEmptyContentArea;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.my_course_fragment, container, false);
        init(view);
        initSwipeRefreshLayout(view);
        initProgressBar(view);
        if (isLoggedIn()){
            this.loggedInView();
        }else{
            this.loggedOutView();
        }
        return view;
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.googleAccentColor1,
                R.color.googleAccentColor2,
                R.color.googleAccentColor3,
                R.color.googleAccentColor4);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn()){
            this.loggedInView();
        }else{
            this.loggedOutView();
        }
    }

    private void init(View view) {
        myCoursesGridLayout = view.findViewById(R.id.myCoursesGridLayout);
        myCourseView = view.findViewById(R.id.myCourseView);
        signInPlaceholder = view.findViewById(R.id.signInPlaceHolder);
        signInButton = view.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        mEmptyContentArea = view.findViewById(R.id.emptyContentArea);
    }

    // check if the user is logged in or not
    private boolean isLoggedIn() {
        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        int userValidity = preferences.getInt("userValidity", 0);
        if (userValidity == 1) {
            return true;
        }else{
            return false;
        }
    }

    // it will show the looged in view
    private void loggedInView() {
        // fetching all of the my courses
        getMyCourses();
        signInPlaceholder.setVisibility(View.GONE);
        myCourseView.setVisibility(View.VISIBLE);
    }

    // it will show the looged out view
    private void loggedOutView() {
        signInPlaceholder.setVisibility(View.VISIBLE);
        myCourseView.setVisibility(View.GONE);
    }
    // Initialize the progress bar
    private void initProgressBar(View view) {
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }
    private  void getMyCourses() {
        progressBar.setVisibility(View.VISIBLE);
        // CourseSchema array of objects.
        final ArrayList<MyCourse> mMyCourse = new ArrayList<>();
        // Auth Token
        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", "");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        Call<List<CourseSchema>> call = api.getMyCourses(authToken);
        call.enqueue(new Callback<List<CourseSchema>>() {
            @Override
            public void onResponse(Call<List<CourseSchema>> call, Response<List<CourseSchema>> response) {
                Log.d("Size of response", response.toString());
                List<CourseSchema> myCourseSchema = response.body();
                for (CourseSchema m : myCourseSchema){
                    mMyCourse.add(new MyCourse(m.getId(), m.getTitle(), m.getThumbnail(), m.getPrice(), m.getInstructorName(), m.getRating(), m.getNumberOfRatings(), m.getTotalEnrollment(),m.getCourseCompletion(), m.getTotalNumberOfLessons(), m.getTotalNumberOfCompletedLessons(), m.getShareableLink(), m.getCourseOverviewProvider(), m.getCourseOverviewUrl()));
                }
                if (mMyCourse.size() > 0) {
                    initMyCourseGridView(mMyCourse);
                    mEmptyContentArea.setVisibility(View.GONE);
                }
                else{
                    mEmptyContentArea.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<CourseSchema>> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initMyCourseGridView(ArrayList<MyCourse> mMyCourse){
        MyCourseAdapter adapter = new MyCourseAdapter(getActivity(), mMyCourse);
        myCoursesGridLayout.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        if (isLoggedIn()){
            this.loggedInView();
        }else{
            this.loggedOutView();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
