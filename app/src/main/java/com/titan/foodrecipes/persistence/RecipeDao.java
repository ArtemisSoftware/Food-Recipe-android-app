package com.titan.foodrecipes.persistence;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.titan.foodrecipes.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long [] insertRecipes(Recipe... recipe);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Query("UPDATE recipes " +
            "SET title = :title, publisher = :publisher, image_url =:image_url, social_rank =:social_rank " +
            "WHERE recipe_id =:recipe_id")
    void updateRecipe(String recipe_id, String title, String publisher, String image_url, float social_rank);

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' ORDER BY social_rank DESC LIMIT (:pageNUmber * 30)")
    LiveData<List<Recipe>> searchRecipes(String query, int pageNUmber);

    @Query("SELECT * FROM recipes WHERE recipe_id =:recipe_id")
    LiveData<Recipe> getRecipe(String recipe_id);
}
