package com.titan.foodrecipes.requests;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.titan.foodrecipes.AppExecutors;
import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.requests.responses.RecipeResponse;
import com.titan.foodrecipes.requests.responses.RecipeSearchResponse;
import com.titan.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private MutableLiveData<Recipe> mRecipe;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private MutableLiveData<Boolean> mRecipeRequestTimeOut = new MutableLiveData<>();

    public static RecipeApiClient getInstance(){
        if(instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }

    private RecipeApiClient(){
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }

    public LiveData<Recipe> getRecipe(){
        return mRecipe;
    }

    public LiveData<Boolean> isRecipeRequestTimeOut() {
        return mRecipeRequestTimeOut;
    }

    public void searchRecipesApi(String query, int pageNumber){

        /*
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable = null;
        }

        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //let user know its timed out
                handler.cancel(true);
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
        */
    }


    public void searchRecipeById(String recipeId){

        /*
        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable = null;
        }

        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeRunnable);

        mRecipeRequestTimeOut.setValue(false);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                mRecipeRequestTimeOut.postValue(true);
                handler.cancel(true);
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
        */
    }



    private class RetrieveRecipesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {

            try {
                Response response = getRecipes(query, pageNumber).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse) response.body()).getRecipes());
                    if(pageNumber == 1){
                        mRecipes.postValue(list);
                    }
                    else{
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(list);
                        mRecipes.postValue(currentRecipes);
                    }
                }
                else{

                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }

        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber){
            return null;//ServiceGenerator.getRecipeApi().searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber));
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }


    private class RetrieveRecipeRunnable implements Runnable{

        private String recipeId;
        boolean cancelRequest;

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            cancelRequest = false;
        }

        @Override
        public void run() {

            try {
                Response response = getRecipe(recipeId).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){
                    Recipe recipe = ((RecipeResponse) response.body()).getRecipe();
                    mRecipe.postValue(recipe);

                }
                else{

                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }

        }

        private Call<RecipeResponse> getRecipe(String recipeId){
            return null;//ServiceGenerator.getRecipeApi().getRecipe(Constants.API_KEY, recipeId);
        }

        private void cancelRequest(){
            Log.d(TAG, "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }



    public void cancelRequest(){
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable.cancelRequest();
        }

        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable.cancelRequest();
        }
    }
}
