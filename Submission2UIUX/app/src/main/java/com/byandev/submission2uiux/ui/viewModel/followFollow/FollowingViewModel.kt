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
import com.byandev.submission2uiux.data.model.FollowingSource
import com.byandev.submission2uiux.data.repo.FollowFollowListRepository
import com.byandev.submission2uiux.utils.Constants
import com.byandev.submission2uiux.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

@Suppress("DEPRECATION")
class FollowingViewModel(
    app: Application,
    private val followFollowListRepository: FollowFollowListRepository
) : AndroidViewModel(app) {

    val userFollowing: MutableLiveData<Resource<FollowingSource>> = MutableLiveData()
    private var userFollowingResponse: FollowingSource? = null
    var pagination = Constants.QUERY_PAGE_SIZE

    fun followingFetch(userName: String) = viewModelScope.launch {
        safeFollowingCall(userName)
    }

    private fun handleFollowingResponse(response: Response<FollowingSource>) : Resource<FollowingSource>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pagination++
                if (userFollowingResponse == null) {
                    userFollowingResponse = resultResponse
                }else {

                    for (i in 0 until resultResponse.size) {
                        val foll = userFollowingResponse
                        val follow = resultResponse[i]
                        foll?.contains(follow)
                    }
                    userFollowing.postValue(Resource.Loading())
                }
                return Resource.Success(userFollowingResponse ?: resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

    private suspend fun safeFollowingCall(userName: String) {
        userFollowing.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    followFollowListRepository.userFollowing(userName, pagination)
                userFollowing.postValue(handleFollowingResponse(response))
            } else {
                userFollowing.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> userFollowing.postValue(Resource.Error("Network Failure"))
                else -> userFollowing.postValue(Resource.Error("Conversion Error"))
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