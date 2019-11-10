package com.titan.foodrecipes.util;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.titan.foodrecipes.AppExecutors;
import com.titan.foodrecipes.requests.responses.ApiResponse;

/**
 *
 * @param <CacheObject> Type for the Resource data. (database cache)
 * @param <RequestObject> Type for the API response. (network request)
 */
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private AppExecutors appExecutors;
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();


    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    private void init(){

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


                }
                else{
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });
    }

    private void setValue(Resource<CacheObject> newValue){
        if(results.getValue() != newValue){
            results.setValue(newValue);
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