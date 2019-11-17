package com.titan.foodrecipes.requests;

import androidx.lifecycle.LiveData;

import com.titan.foodrecipes.requests.responses.ApiResponse;
import com.titan.foodrecipes.requests.responses.RecipeResponse;
import com.titan.foodrecipes.requests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    // SEARCH
    @GET("api/search")
    LiveData<ApiResponse<RecipeSearchResponse>> searchRecipe(@Query("key") String key, @Query("q") String query, @Query("page") String page);

    // GET SPECIFIC RECIPE
    @GET("api/get")
    LiveData<ApiResponse<RecipeResponse>> getRecipe(@Query("key") String key, @Query("rId") String recipe_id);
}
