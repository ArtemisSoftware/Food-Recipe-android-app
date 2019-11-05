package com.titan.foodrecipes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.util.Testing;
import com.titan.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";
    
    private RecipeListViewModel mRecipeListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        findViewById(R.id.test).setOnClickListener(testRetrofitRequest);

        subscribeObservers();
    }

    private void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {

                if(recipes != null) {

                    Testing.printRecipes("recipes test", recipes);
                }
            }
        });
    }


    private void searchRecipesApi(String query, int pageNumber){
        mRecipeListViewModel.searchRecipesApi(query, pageNumber);
    }


    View.OnClickListener testRetrofitRequest = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchRecipesApi("Chicken breast", 1);
        }
    };

}
