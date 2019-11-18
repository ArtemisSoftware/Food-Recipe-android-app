package com.titan.foodrecipes.requests.responses;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse <T> {


    public ApiResponse<T> create(Throwable error){
        return new ApiErrorResponse<T>(!error.getMessage().equals("") ? error.getMessage() : "Unknow error\nCheck network connection");
    }

    public ApiResponse<T> create(Response<T> response) {

        if (response.isSuccessful()) {
            T body = response.body();

            if(body instanceof RecipeSearchResponse){
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse) body)){
                    return new ApiErrorResponse<>("Api key is invalid or expired");
                }
            }

            if(body instanceof RecipeResponse){
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeResponse) body)){
                    return new ApiErrorResponse<>("Api key is invalid or expired");
                }
            }

            if (body == null || response.code() == 204) {//204 is empty response code
                return new ApiEmptyResponse<T>();
            } else {
                return new ApiSuccessResponse<T>(body);
            }
        }
        else {
            String errorMsg = "";
            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {


                e.printStackTrace();
                errorMsg = response.message();
            }

            return new ApiErrorResponse<T>(errorMsg);
        }
    }




    public class ApiSuccessResponse<T> extends  ApiResponse<T>{

        private T body;

        public ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }
    }

    public class ApiErrorResponse<T> extends  ApiResponse<T>{

        private String errorMessage;

        public ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public class ApiEmptyResponse<T> extends  ApiResponse<T>{}

}
