package com.titan.foodrecipes.util;

import androidx.lifecycle.LiveData;

import com.titan.foodrecipes.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    /**
     * This method performs a number of checks and then returns the response type for retrofit requests
     * @bodyType is the ResponseType. It can be RecipeResponse or RecipeSearchResponse
     *
     * CHECK #1) returnType returns LiveData
     * CHECK #2) type LiveData<T> us of ApiResponse.class
     * CHECK #3) Make sure ApiResponse is parameterized. AKA ApiResponse<T> exists.
     *
     *
     */


    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        //CHECK #1 - Make sure the CallAdapter is returning a type of LiveData

        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        //CHECK #2 - Type that LiveData is wrapping
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);

        //Check if its of type ApiResponse
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);

        if(rawObservableType != ApiResponse.class){
            throw  new IllegalArgumentException("Type must be a defined resource");
        }

        //CHECK #3 - Check if ApiResponse is parameterized. AKA: Does ApiResponse<T> exists? (must wrap around T)
        //FYI: T is either recipeResponse or T will be a RecipeSearchResponse

        if(!(observableType instanceof  ParameterizedType)){
            throw new IllegalArgumentException("resource must be parameterized");
        }

        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<Type>(bodyType);
    }
}
