package com.titan.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.titan.foodrecipes.models.Recipe;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private MutableLiveData<List<Recipe>> mRecipes = new MutableLiveData<>();

    public RecipeListViewModel() {

    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }
}
