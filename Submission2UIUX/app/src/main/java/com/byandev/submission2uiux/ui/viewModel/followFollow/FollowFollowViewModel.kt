package com.byandev.submission2uiux.ui.viewModel.followFollow

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.byandev.submission2uiux.SearchApplication
import com.byandev.submission2uiux.data.model.FollowersSource
import com.byandev.submission2uiux.data.model.FollowingSource
import com.byandev.submission2uiux.data.repo.FollowFollowListRepository
import com.byandev.submission2uiux.utils.Constants
import com.byandev.submission2uiux.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class FollowFollowViewModel(
    app: Application,
    val followFollowListRepository: FollowFollowListRepository
) : AndroidViewModel(app) {

    val userFollowers: MutableLiveData<Resource<FollowersSource>> get() = MutableLiveData<Resource<FollowersSource>>()
    var usersFollowersPage = Constants.QUERY_PAGE_SIZE
    var userFollowersResponse: FollowersSource? = null

    val userFollowings: MutableLiveData<Resource<FollowingSource>> = MutableLiveData()
    var userFollowingPage = Constants.QUERY_PAGE_SIZE
    var userFollowingResponse: FollowingSource? = null

    fun followingFetch(userName: String) = viewModelScope.launch {
        safeFollowingsCall(userName)
    }

    fun followersFetch(userName: String) = viewModelScope.launch {
        safeFollowersCall(userName)
    }

    private fun handleFollowersResponse(response: Response<FollowersSource>) : Resource<FollowersSource> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                usersFollowersPage++
                if(userFollowersResponse == null) {
                    userFollowersResponse = resultResponse
                } else {
                    val oldUsers = userFollowersResponse
                    oldUsers?.login
                }
                return Resource.Success(userFollowersResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleFollowingResponse(response: Response<FollowingSource>) : Resource<FollowingSource> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                userFollowingPage++
                if(userFollowingResponse == null) {
                    userFollowingResponse = resultResponse
                } else {
                    val oldUsers = userFollowingResponse
                    oldUsers?.login
                }
                return Resource.Success(userFollowingResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeFollowersCall(userName: String) {
        userFollowers.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = followFollowListRepository.userFollowers(userName, usersFollowersPage)
//                userFollowers.postValue(handleFollowersResponse(response))
                userFollowers.value = handleFollowersResponse(response)
            } else {
//                userFollowers.postValue(Resource.Error("No internet connection"))
                userFollowers.value = Resource.Error("No internet connection")
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> userFollowers.value = Resource.Error("Network Failure")
//                    userFollowers.postValue(Resource.Error("Network Failure"))
                else -> userFollowers.value = Resource.Error("Conversion Error")
//                    userFollowers.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeFollowingsCall(userName: String) {
        userFollowings.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = followFollowListRepository.userFollowing(userName, userFollowingPage)
                userFollowings.postValue(handleFollowingResponse(response))
            } else {
                userFollowings.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable) {
            when(t) {
                is IOException -> userFollowings.postValue(Resource.Error("Network Failure"))
                else -> userFollowings.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<SearchApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}