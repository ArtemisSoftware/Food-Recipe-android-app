package com.titan.foodrecipes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;

import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity {

    private RecipeListViewModel mRecipeListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {

            }
        });
    }
}
