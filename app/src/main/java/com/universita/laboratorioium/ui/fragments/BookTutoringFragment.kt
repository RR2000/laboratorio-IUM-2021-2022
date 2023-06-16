package com.universita.laboratorioium.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.universita.laboratorioium.BuildConfig
import com.universita.laboratorioium.R
import com.universita.laboratorioium.databinding.FragmentBookTutoringBinding
import com.universita.laboratorioium.ui.stateAdapters.DaysStateAdapter
import com.universita.laboratorioium.ui.support.ListableListViewModel
import com.universita.laboratorioium.utils.Teach


class BookTutoringFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_tutoring, container, false)
    }

    override fun onResume() {
        val position = requireView().findViewById<TabLayout>(R.id.tabs).selectedTabPosition
        val model: ListableListViewModel by activityViewModels()
        model.teachesOfDay0.removeObservers(viewLifecycleOwner)
        val teachesOfDay = when (position) {
            0 -> model.teachesOfDay0
            1 -> model.teachesOfDay1
            2 -> model.teachesOfDay2
            3 -> model.teachesOfDay3
            4 -> model.teachesOfDay4
            else -> model.teachesOfDay0
        }
        teachesOfDay.value = null

        refreshAvailableOfDay(position, this)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentBookTutoringBinding.bind(view)

        binding.refreshBookableList.setOnRefreshListener {
            refreshAvailableOfDay(view.findViewById<TabLayout>(R.id.tabs).selectedTabPosition, this)
            binding.refreshBookableList.isRefreshing = false
        }


        val daysStateAdapter = DaysStateAdapter(requireActivity())
        binding.viewPager.adapter = daysStateAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Lun"
                1 -> "Mar"
                2 -> "Mer"
                3 -> "Gio"
                4 -> "Ven"
                else -> "None"
            }
        }.attach()

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = BookTutoringFragment()

        fun refreshAvailableOfDays(fragment: Fragment) {
            for (i in 0..4) {
                refreshAvailableOfDay(i, fragment)
            }
        }

        fun refreshAvailableOfDay(dayNum: Int, fragment: Fragment) {
            Log.w("FUNCTION", "refreshDay")
            val model: ListableListViewModel by fragment.activityViewModels()
            val mRequestQueue = Volley.newRequestQueue(fragment.context)
            val strRequest = object : StringRequest(
                Method.GET, BuildConfig.BASE_URL + "/api/get?type=teachesOfDay&day=$dayNum",
                Response.Listener { response ->
                    val res: ArrayList<HashMap<String, String>?>?
                    res = ArrayList()

                    val day = JsonParser.parseString(response).asJsonObject
                    val letterToHour =
                        mapOf("a" to "15-16", "b" to "16-17", "c" to "17-18", "d" to "18-19")
                    day.keySet().forEach { hourLetter ->
                        day[hourLetter].asJsonArray.forEach { teach ->
                            val convertedTeach = Gson().fromJson(teach, Teach::class.java)
                            val temp = HashMap<String, String>()
                            temp["time"] = letterToHour[hourLetter].toString()
                            temp["hour"] = hourLetter
                            temp["day"] = dayNum.toString()
                            temp["courseName"] = convertedTeach.course.name
                            temp["teacherSurnameName"] =
                                convertedTeach.teacher.surname + " " + convertedTeach.teacher.name
                            temp["teacherId"] = convertedTeach.teacher.id.toString()
                            temp["courseId"] = convertedTeach.course.id.toString()
                            res.add(temp)
                        }
                    }

                    val teachesOfDay = when (dayNum) {
                        0 -> model.teachesOfDay0
                        1 -> model.teachesOfDay1
                        2 -> model.teachesOfDay2
                        3 -> model.teachesOfDay3
                        4 -> model.teachesOfDay4
                        else -> model.teachesOfDay0
                    }

                    teachesOfDay.postValue(res)
                },
                Response.ErrorListener { error ->
                    when (error.networkResponse?.statusCode) {
                        403 -> Snackbar.make(
                            fragment.requireView(),
                            "Non ti è permmesso scaricare insegnamenti",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        500 -> Snackbar.make(
                            fragment.requireView(),
                            "Errore del server",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        else -> Toast.makeText(
                            fragment.requireContext(),
                            "Impossibile scaricare insegnamenti",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {}

            mRequestQueue.add(strRequest)
        }
    }

}