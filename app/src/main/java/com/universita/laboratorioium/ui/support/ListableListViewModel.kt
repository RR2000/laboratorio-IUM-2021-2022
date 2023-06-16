package com.universita.laboratorioium.ui.support

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.universita.laboratorioium.utils.*

class ListableListViewModel : ViewModel() {
    val teachers: MutableLiveData<ArrayList<Teacher?>?> by lazy {
        MutableLiveData<ArrayList<Teacher?>?>()
    }
    val courses: MutableLiveData<ArrayList<Course?>?> by lazy {
        MutableLiveData<ArrayList<Course?>?>()
    }
    val teaches: MutableLiveData<ArrayList<Teach?>?> by lazy {
        MutableLiveData<ArrayList<Teach?>?>()
    }
    val user: MutableLiveData<User?> by lazy {
        MutableLiveData<User?>()
    }
    val bookings: MutableLiveData<ArrayList<Booking?>?> by lazy {
        MutableLiveData<ArrayList<Booking?>?>()
    }
    val teachesOfDay0: MutableLiveData<ArrayList<HashMap<String, String>?>?> by lazy {
        MutableLiveData<ArrayList<HashMap<String, String>?>?>()
    }

    val teachesOfDay1: MutableLiveData<ArrayList<HashMap<String, String>?>?> by lazy {
        MutableLiveData<ArrayList<HashMap<String, String>?>?>()
    }

    val teachesOfDay2: MutableLiveData<ArrayList<HashMap<String, String>?>?> by lazy {
        MutableLiveData<ArrayList<HashMap<String, String>?>?>()
    }

    val teachesOfDay3: MutableLiveData<ArrayList<HashMap<String, String>?>?> by lazy {
        MutableLiveData<ArrayList<HashMap<String, String>?>?>()
    }

    val teachesOfDay4: MutableLiveData<ArrayList<HashMap<String, String>?>?> by lazy {
        MutableLiveData<ArrayList<HashMap<String, String>?>?>()
    }
}