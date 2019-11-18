package com.titan.foodrecipes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.titan.foodrecipes.models.Recipe;
import com.titan.foodrecipes.repository.RecipeRepository;
import com.titan.foodrecipes.util.Resource;

import java.util.List;

import timber.log.Timber;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public static final String QUERY_EXHAUSTED = "No more results";
    public enum ViewState {CATEGORIES, RECIPES}

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();
    private RecipeRepository recipeRepository;

    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;

    private long requestStartTime;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);

        recipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init(){
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewState() {
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return recipes;
    }


    public int getPageNumber() {
        return pageNumber;
    }


    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }


    public void searchRecipesApi(String query, int pageNumber){

        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeSearch();
        }
    }


    public void searchNextPage(){

        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeSearch();
        }
    }


    private void executeSearch(){

        requestStartTime = System.currentTimeMillis();

        cancelRequest = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);

        Timber.d("Search recipes: %s on page %d", query, pageNumber);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);

        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {

                if(!cancelRequest) {
                    if (listResource != null) {
                        recipes.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {

                            Timber.d("onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds");

                            isPerformingQuery = false;

                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Timber.d("onChanged: query is exhausted");
                                    recipes.setValue(new Resource<List<Recipe>>(Resource.Status.ERROR, listResource.data, QUERY_EXHAUSTED));
                                }
                            }
                            recipes.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {

                            Timber.e("onChanged error: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds");


                            isPerformingQuery = false;
                            recipes.removeSource(repositorySource);
                        }
                    } else {
                        recipes.removeSource(repositorySource);
                    }
                }
                else{
                    recipes.removeSource(repositorySource);
                }
            }
        });
    }

    public void cancelSearchRequest(){

        if(isPerformingQuery){
            Timber.d("cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }

    }
}
