package com.titan.foodrecipes.requests.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.titan.foodrecipes.models.Recipe;

public class RecipeResponse {

    @SerializedName("recipe")
    @Expose()
    private Recipe recipe;

    @SerializedName("error")
    @Expose()
    private String error;

    public Recipe getRecipe(){
        return recipe;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                ", error='" + error + '\'' +
                '}';
    }
}
