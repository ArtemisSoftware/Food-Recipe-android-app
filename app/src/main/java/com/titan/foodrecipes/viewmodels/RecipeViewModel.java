package com.titan.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.repository.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;

    public RecipeViewModel() {
        this.mRecipeRepository = RecipeRepository.getInstance();
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeRepository.getRecipe();
    }

    public void searchRecipeById(String recipeId){
        mRecipeRepository.searchRecipeById(recipeId);
    }
}
