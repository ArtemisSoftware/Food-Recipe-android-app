package com.titan.foodrecipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.titan.foodrecipes.adapters.OnRecipeListener;
import com.titan.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.util.Resource;
import com.titan.foodrecipes.util.Testing;
import com.titan.foodrecipes.util.VerticalSpacingItemDecorator;
import com.titan.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

import timber.log.Timber;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";
    
    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        subscribeObservers();
        initSearchView();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        subscribeObservers();
    }

    private void subscribeObservers(){

        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if(listResource.data != null){

                        switch (listResource.status){

                            case LOADING:{

                                if(mRecipeListViewModel.getPageNumber() > 1){
                                    mAdapter.displayLoading();
                                }
                                else{
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case ERROR:{

                                Timber.e("onChange: cannot refresh the cache");
                                Timber.e("onChange: ERROR message: " + listResource.message);
                                Timber.e("onChange: status: ERROR, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                                if(listResource.message.equals(RecipeListViewModel.QUERY_EXHAUSTED)){
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            }
                            case SUCCESS:{

                                Timber.d("onChange: cache has been refreshed.");
                                Timber.d("onChange: status: SUCCESS, # Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                break;
                            }

                        }


                    }
                }
            }
        });

        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(RecipeListViewModel.ViewState viewState) {

                if(viewState != null){

                    switch (viewState){

                        case RECIPES:{

                            break;
                        }

                        case CATEGORIES:{

                            displaySearchCategories();
                            break;
                        }
                    }
                }
            }
        });
    }

    private RequestManager initGlide(){

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private void searchRecipesApi(String query){

        Timber.d("Search recipes: %s", query);
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesApi(query, 1);
        mSearchView.clearFocus();
    }

    private void initRecyclerView(){

        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();

        mAdapter = new RecipeRecyclerAdapter(this, initGlide(), viewPreloader);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(6);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(Glide.with(this), mAdapter, viewPreloader, 30/*numero de resultados em cada pagina*/);

        mRecyclerView.addOnScrollListener(preloader);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!mRecyclerView.canScrollVertically(1)
                        && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPES){

                    mRecipeListViewModel.searchNextPage();
                }
            }
        });

    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchRecipesApi(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipesApi(category);
    }

    private void displaySearchCategories(){
        mAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if(mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            super.onBackPressed();
        }
        else{
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }
    }
}
