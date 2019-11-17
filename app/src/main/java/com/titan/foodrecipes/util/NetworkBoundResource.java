package com.titan.foodrecipes.util;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.titan.foodrecipes.AppExecutors;
import com.titan.foodrecipes.requests.responses.ApiResponse;

import timber.log.Timber;

/**
 *
 * @param <CacheObject> Type for the Resource data. (database cache)
 * @param <RequestObject> Type for the API response. (network request)
 */
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkBoundResource";
    private AppExecutors appExecutors;
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();


    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){

        Timber.d("init...");

        //update livedata for loading status
        results.setValue((Resource<CacheObject>) Resource.loading(null));

        // observer livedata source from local db
        final LiveData<CacheObject> dbSource = loadFromDb();

        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {

                results.removeSource(dbSource);

                if(shouldFetch(cacheObject)){
                    //get data from the network
                    fetchFromNetwork(dbSource);
                }
                else{
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(CacheObject cacheObject) {

                            Timber.d("Did not fetch...");

                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });
    }


    /**
     * 1) observe local db
     * 2) if <condition/> query network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){

        Timber.d("fetchFromNetwork: called");


        //update liveData for loading status
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject));
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();

        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(final ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(dbSource);
                results.removeSource(apiResponse);

                /*
                    3 cases:
                        1)ApiSuccessResponse
                        2)ApiErrorResponse
                        2)ApiEmptyResponse
                 */

                if(requestObjectApiResponse instanceof  ApiResponse.ApiSuccessResponse){
                    Timber.d("onChanged: ApiSuccessResponse");

                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //save the  response to local db
                            saveCallResult((RequestObject) processResponse(((ApiResponse.ApiSuccessResponse) requestObjectApiResponse)));

                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                        @Override
                                        public void onChanged(@Nullable CacheObject cacheObject) {
                                            setValue(Resource.success(cacheObject));
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else if(requestObjectApiResponse instanceof  ApiResponse.ApiEmptyResponse){
                    Timber.d("onChanged: ApiEmptyResponse");

                    appExecutors.mainThread().execute((new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(@Nullable CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject));
                                }
                            });
                        }
                    }));
                }
                else if(requestObjectApiResponse instanceof  ApiResponse.ApiErrorResponse){
                    Timber.d("onChanged: ApiErrorResponse: " + ((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage());

                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(Resource.error (
                                    ((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(), cacheObject));
                        }
                    });
                }

            }
        });
    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response){
        return (CacheObject) response.getBody();
    }

    private void setValue(Resource<CacheObject> newValue){
        Timber.d("setValue: " + newValue);
        if(results.getValue() != newValue){
            results.setValue(newValue);
            Timber.d("setValue: value set");
        }
    }


    /**
     * Called to save the result of the API response into the database
     * @param item
     */
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);


    /**
     * Called with the data in the database to decide whether to fetch potentially updated data from the network.
     * @param data
     * @return
     */
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);


    /**
     * Called to get the cached data from the database.
     * @return
     */
    @NonNull
    @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();


    /**
     * Called to create the API call.
     * @return
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();


    /**
     * Returns a LiveData object that represents the resource that's implemented in the base class.
     * @return
     */
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results;
    }
}