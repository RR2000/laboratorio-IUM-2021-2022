package com.universita.laboratorioium.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.universita.laboratorioium.R
import com.universita.laboratorioium.databinding.FragmentRemoveThingsBinding
import com.universita.laboratorioium.ui.support.ListableListViewModel
import com.universita.laboratorioium.utils.Course
import com.universita.laboratorioium.utils.Teach
import com.universita.laboratorioium.utils.Teacher
import android.content.Context.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import com.universita.laboratorioium.BuildConfig


class RemoveThingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_remove_things, container, false)
    }

    override fun onResume() {
        refreshCourses()
        refreshTeachers()
        refreshTeaches()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentRemoveThingsBinding.bind(view)
        val model: ListableListViewModel by activityViewModels()

        model.courses.observe(viewLifecycleOwner) { courses ->
            val coursesNames = ArrayList<String>()
            if (courses != null) {
                for (course in courses)
                    if (course != null) {
                        coursesNames.add(course.name)
                    }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    coursesNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCourses.adapter = adapter

                binding.buttonRemoveCourse.setOnClickListener {
                    val indexCourse = binding.spinnerCourses.selectedItemPosition
                    if (indexCourse >= 0) {
                        val id = courses[indexCourse]?.id
                        if (id != null) {
                            sendDeleteCourse(id)
                        } else {
                            Snackbar.make(
                                requireView(),
                                "Corso non selezionato",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Corso non selezionato",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        model.teachers.observe(viewLifecycleOwner) { teachers ->
            val teachersNames = ArrayList<String>()
            if (teachers != null) {
                for (course in teachers)
                    if (course != null) {
                        teachersNames.add(course.name)
                    }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    teachersNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTeachers.adapter = adapter

                binding.buttonRemoveTeacher.setOnClickListener {
                    val indexTeacher = binding.spinnerTeachers.selectedItemPosition
                    if (indexTeacher >= 0) {
                        val id = teachers[indexTeacher]?.id
                        if (id != null) {
                            sendDeleteTeacher(id)
                        } else {
                            Snackbar.make(
                                requireView(),
                                "Professore non selezionato",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Professore non selezionato",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        model.teaches.observe(viewLifecycleOwner) { teaches ->
            val teachesPair = ArrayList<String>()
            if (teaches != null) {
                for (teach in teaches)
                    if (teach != null) {
                        teachesPair.add(teach.teacher.surname + " - " + teach.course.name)
                    }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    teachesPair
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerTeaches.adapter = adapter

                binding.buttonRemoveTeach.setOnClickListener {
                    val indexTeach = binding.spinnerTeaches.selectedItemPosition

                    if (indexTeach >= 0) {
                        val teacherId = teaches[indexTeach]?.teacher?.id
                        val courseId = teaches[indexTeach]?.course?.id
                        if (teacherId != null && courseId != null) {
                            sendDeleteTeach(teacherId, courseId)
                        } else {
                            Snackbar.make(
                                requireView(),
                                "Insegnamento non selezionato",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Insegnamento non selezionato",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendDeleteTeach(teacherId: Int, courseId: Int) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/delete?type=teach",
            Response.Listener { response ->
                Log.w("Response", response)
                Snackbar.make(
                    requireView(),
                    "Insegnamento rimosso con successo",
                    Snackbar.LENGTH_SHORT
                ).show()
                refreshTeaches()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso rimuovere insegnamenti",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile rimuovere insegnamento",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["courseId"] = courseId.toString()
                params["teacherId"] = teacherId.toString()
                return params
            }
        }
        mRequestQueue.add(strRequest)
    }

    private fun sendDeleteCourse(id: Int) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/delete?type=course",
            Response.Listener { response ->
                Log.w("Response", response)
                Snackbar.make(requireView(), "Corso rimosso con successo", Snackbar.LENGTH_SHORT)
                    .show()
                refreshCourses()
                refreshTeaches()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso rimuovere corsi",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile rimuovere corso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = id.toString()
                return params
            }
        }
        mRequestQueue.add(strRequest)
    }

    private fun sendDeleteTeacher(id: Int) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/delete?type=teacher",
            Response.Listener { response ->
                Log.w("Response", response)
                Snackbar.make(
                    requireView(),
                    "Professore rimosso con successo",
                    Snackbar.LENGTH_SHORT
                ).show()
                refreshTeachers()
                refreshTeaches()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso rimuovere professori",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile rimuovere professore",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = id.toString()
                return params
            }
        }

        mRequestQueue.add(strRequest)
    }

    private fun refreshTeachers() {
        val model: ListableListViewModel by activityViewModels()
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.GET, BuildConfig.BASE_URL + "/api/get?type=teachers",
            Response.Listener { response ->
                val listType = object : TypeToken<ArrayList<Teacher?>?>() {}.type
                val teachers: ArrayList<Teacher?>? = Gson().fromJson(response, listType)
                model.teachers.postValue(teachers)
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile scaricare professori",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {}

        mRequestQueue.add(strRequest)
    }

    private fun refreshCourses() {
        val model: ListableListViewModel by activityViewModels()
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.GET, BuildConfig.BASE_URL + "/api/get?type=courses",
            Response.Listener { response ->
                val listType = object : TypeToken<ArrayList<Course?>?>() {}.type
                val teachers: ArrayList<Course?>? = Gson().fromJson(response, listType)
                model.courses.postValue(teachers)
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    500 -> Snackbar.make(
                        requireView(),
                        "Errore del server",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile scaricare corsi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {}

        mRequestQueue.add(strRequest)
    }

    private fun refreshTeaches() {
        val model: ListableListViewModel by activityViewModels()
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.GET, BuildConfig.BASE_URL + "/api/get?type=teaches",
            Response.Listener { response ->
                val listType = object : TypeToken<ArrayList<Teach?>?>() {}.type
                val teaches: ArrayList<Teach?>? = Gson().fromJson(response, listType)
                model.teaches.postValue(teaches)
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile scaricare insegnamenti",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {}

        mRequestQueue.add(strRequest)
    }

    companion object {
        fun newInstance() = RemoveThingsFragment()
    }
}