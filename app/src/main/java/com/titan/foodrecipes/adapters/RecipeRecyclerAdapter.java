package com.titan.foodrecipes.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.titan.foodrecipes.R;
import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int RECIPE_TYPE = 1;
    public static final int LOADING_TYPE = 2;
    public static final int CATEGORY_TYPE = 3;
    public static final int EXHAUSTED_TYPE = 4;

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;
    private RequestManager requestManager;

    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener, RequestManager requestManager) {
        this.mOnRecipeListener = mOnRecipeListener;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType){

            case LOADING_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            }

            case CATEGORY_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener, requestManager);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted, parent, false);
                return new SearchExhaustedViewHolder(view);
            }

            default:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener, requestManager);
            }
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);

        if(itemViewType == RECIPE_TYPE) {

            ((RecipeViewHolder) holder).onBind(mRecipes.get(position));
        }
        else if(itemViewType == CATEGORY_TYPE) {

            // set the image

            ((CategoryViewHolder) holder).onBind(mRecipes.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else{
            return RECIPE_TYPE;
        }
    }

    // display loading during search request
    public void displayOnlyLoading(){
        clearRecipesList();
        Recipe recipe = new Recipe();
        recipe.setTitle("LOADING...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    private void clearRecipesList(){
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        else{
            mRecipes.clear();
        }

        notifyDataSetChanged();
    }

    //pagination
    public void displayLoading(){

        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        if(!isLoading()){

            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            mRecipes.add(recipe);
            notifyDataSetChanged();
        }
    }

    public void setQueryExhausted(){

        hideLoading();
        Recipe recipe = new Recipe();
        recipe.setTitle("EXHAUSTED...");
        mRecipes.clear(); //provisorio
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()){

            if(mRecipes.get(0).getTitle().equals("LOADING...")){
                mRecipes.remove(0);
            }
            else if(mRecipes.get(mRecipes.size() - 1).equals("LOADING...")){
                mRecipes.remove(mRecipes.size() - 1);
            }

            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){

        if(mRecipes != null) {
            if (mRecipes.size() > 0) {
                if (mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING...")) {
                    return true;
                }
            }
        }
        return false;
    }


    public void displaySearchCategories(){
        List<Recipe> categories = new ArrayList<>();

        for(int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }

        mRecipes = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if(mRecipes != null) {
            return mRecipes.size();
        }

        return 0;
    }

    public void setRecipes(List<Recipe> recipes){
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position){
        if(mRecipes != null){
            if(mRecipes.size() > 0){
                return mRecipes.get(position);
            }
        }
        return null;
    }
}
