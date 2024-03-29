package com.titan.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.util.Resource;
import com.titan.foodrecipes.viewmodels.RecipeViewModel;

import timber.log.Timber;

public class RecipeActivity extends BaseActivity {

    private static final String TAG = "RecipeActivity";
    
    private AppCompatImageView mRecipeImage;
    private TextView mRecipeTitle, mRecipeRank;
    private LinearLayout mRecipeIngredientsContainer;
    private ScrollView mScrollView;

    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mRecipeImage = findViewById(R.id.recipe_image);
        mRecipeTitle = findViewById(R.id.recipe_title);
        mRecipeRank = findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
        mScrollView = findViewById(R.id.parent);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        showProgressBar(true);

        getIncomingIntent();


    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("recipe")){
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            subscribeObservers(recipe.getRecipe_id());
        }
    }

    private void subscribeObservers(final String recipeId){

        mRecipeViewModel.searchRecipeApi(recipeId).observe(this, new Observer<Resource<Recipe>>() {
            @Override
            public void onChanged(Resource<Recipe> recipeResource) {

                if(recipeResource != null){
                    if(recipeResource.data != null){

                        switch (recipeResource.status){

                            case LOADING:{

                                showProgressBar(true);
                                break;
                            }

                            case ERROR:{

                                Timber.d("onChanged: status: ERROR, Recipe: "+ recipeResource.data.getTitle());
                                Timber.d("onChanged: status: ERROR message: "+ recipeResource.message);
                                showParent();
                                showProgressBar(false);
                                setRecipeProperties(recipeResource.data);
                                break;
                            }

                            case SUCCESS:{

                                Timber.d("onChanged: cache has been refreshed.");
                                Timber.d("onChanged: status: SUCCESS, Recipe: "+ recipeResource.data.getTitle());
                                showParent();
                                showProgressBar(false);
                                setRecipeProperties(recipeResource.data);
                                break;
                            }
                        }

                    }
                }
            }
        });

        /*
        mRecipeViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && !mRecipeViewModel.didRetrieveRecipe()){
                    Log.d(TAG, "onChanged: timed out");
                    displayErrorScreen("Error retrieving data. Check network connection");
                }
            }
        });
        */
    }

    private void showParent(){
        mScrollView.setVisibility(View.VISIBLE);
    }



    private void setRecipeProperties(Recipe recipe){

        if(recipe != null){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.white_background);

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(recipe.getImage_url())
                    .into(mRecipeImage);

            mRecipeTitle.setText(recipe.getTitle());
            mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

            mRecipeIngredientsContainer.removeAllViews();
            if(recipe.getIngredients() != null) {
                for (String ingredient : recipe.getIngredients()) {
                    TextView textView = new TextView(this);
                    textView.setText(ingredient);
                    textView.setTextSize(15);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    mRecipeIngredientsContainer.addView(textView);
                }
            }
            else{
                TextView textView = new TextView(this);
                textView.setText("Error retrieving ingredients.\nCheck network connection.");
                textView.setTextSize(15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mRecipeIngredientsContainer.addView(textView);
            }
        }
    }

/*
    private void displayErrorScreen(String errorMessage){
        mRecipeTitle.setText("Error retrieveing recipe...");
        mRecipeRank.setText("");
        TextView textView = new TextView(this);

        if(errorMessage.equals("")){
            textView.setText(errorMessage);
        }
        else{
            textView.setText("Error");
        }

        textView.setTextSize(15);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRecipeIngredientsContainer.addView(textView);

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.ic_launcher_background)
                .into(mRecipeImage);

        showParent();
        showProgressBar(false);
        mRecipeViewModel.setRetrievedRecipe(true);
    }
    */
}
