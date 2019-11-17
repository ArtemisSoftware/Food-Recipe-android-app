package com.titan.foodrecipes.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.titan.foodrecipes.AppExecutors;
import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.persistence.RecipeDao;
import com.titan.foodrecipes.persistence.RecipeDatabase;
import com.titan.foodrecipes.requests.RecipeApiClient;
import com.titan.foodrecipes.requests.ServiceGenerator;
import com.titan.foodrecipes.requests.responses.ApiResponse;
import com.titan.foodrecipes.requests.responses.RecipeSearchResponse;
import com.titan.foodrecipes.util.Constants;
import com.titan.foodrecipes.util.NetworkBoundResource;
import com.titan.foodrecipes.util.Resource;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    /*
    private RecipeApiClient mRecipeApiClient;

    private String mQuery;
    private int mPageNumber;

    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();
*/
    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context){
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber));
            }
        }.getAsLiveData();
    }

    /*
    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimeOut();
    }



    private void initMediators(){
        LiveData<List<Recipe>> recipeListApiSource = mRecipeApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if(recipes != null){
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                }
                else{
                    //search db cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<Recipe> list){

        if(list != null){
            if(list.size() % 30 != 0){
                mIsQueryExhausted.setValue(true);
            }
        }
        else{
            mIsQueryExhausted.setValue(true);
        }
    }

    public LiveData<Boolean> getmIsQueryExhausted() {
        return mIsQueryExhausted;
    }


    public LiveData<Recipe> getRecipe(){
        return mRecipeApiClient.getRecipe();
    }


    public void searchRecipesApi(String query, int pageNumber){
        if(pageNumber == 0){
            pageNumber = 1;
        }

        mQuery = query;
        mPageNumber = pageNumber;
        mIsQueryExhausted.setValue(false);
        mRecipeApiClient.searchRecipesApi(query, pageNumber);
    }

    public void searchRecipeById(String recipeId){
        mRecipeApiClient.searchRecipeById(recipeId);
    }


    public void searchNextPage(){
        searchRecipesApi(mQuery, mPageNumber + 1);
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }

*/

}
