package com.universita.laboratorioium.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.universita.laboratorioium.databinding.FragmentAddThingsBinding
import com.universita.laboratorioium.ui.support.ListableListViewModel
import com.universita.laboratorioium.utils.Course
import com.universita.laboratorioium.utils.Teacher
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.universita.laboratorioium.BuildConfig
import com.universita.laboratorioium.ui.main.MainActivity


class AddThingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_things, container, false)
    }

    override fun onResume() {
        refreshCourses()
        refreshTeachers()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAddThingsBinding.bind(view)
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
            }
        }

        binding.buttonAddCourse.setOnClickListener {
            sendAddCourse(
                binding.editTextCourseName.text.toString(),
                binding.editTextCourseDescription.text.toString()
            )
            MainActivity.closeKeyboard(requireContext())
        }

        binding.buttonAddTeacher.setOnClickListener {
            sendAddTeacher(
                binding.editTextTeacherName.text.toString(),
                binding.editTextTeacherSurname.text.toString()
            )
            MainActivity.closeKeyboard(requireContext())
        }

        binding.buttonAddTeach.setOnClickListener {
            val indexTeacher = binding.spinnerTeachers.selectedItemPosition
            val indexCourse = binding.spinnerCourses.selectedItemPosition

            if (indexCourse >= 0 && indexTeacher >= 0) {
                val teacherId = model.teachers.value?.get(indexTeacher)?.id
                val courseId = model.courses.value?.get(indexCourse)?.id
                if (teacherId != null && courseId != null) {
                    sendAddTeach(teacherId, courseId)
                } else {
                    Snackbar.make(
                        requireView(),
                        "Professore e/o corso non selezionato",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    "Professore e/o corso non selezionato",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendAddTeach(teacherId: Int, courseId: Int) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/set?type=teach",
            Response.Listener { response ->
                Log.w("Response", response)
                refreshTeachers()
                refreshCourses()
                Snackbar.make(
                    requireView(),
                    "Insegnamento aggiunto con successo!",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso aggiungere insegnamenti",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    409 -> Snackbar.make(
                        requireView(),
                        "Associazione professore - corso già presente",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile aggiungere insegnamento",
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

    private fun sendAddCourse(name: String, description: String) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/set?type=course",
            Response.Listener { response ->
                Log.w("Response", response)
                refreshCourses()
                Snackbar.make(requireView(), "Corso aggiunto con successo!", Snackbar.LENGTH_SHORT)
                    .show()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    400 -> Snackbar.make(
                        requireView(),
                        "Nome corso non compilato",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso aggiungere corsi",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile aggiungere corso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["name"] = name
                params["description"] = description
                return params
            }
        }
        mRequestQueue.add(strRequest)
    }

    private fun sendAddTeacher(name: String, surname: String) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/set?type=teacher",
            Response.Listener { response ->
                Log.w("Response", response)
                Snackbar.make(
                    requireView(),
                    "Professore aggiunto con successo!",
                    Snackbar.LENGTH_SHORT
                ).show()
                refreshTeachers()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    400 -> Snackbar.make(
                        requireView(),
                        "Nome e/o cognome insegnante non compilato",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso aggiungere professori",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile aggiungere professore",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["name"] = name
                params["surname"] = surname
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
                when (error.networkResponse.statusCode) {
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Snackbar.make(
                        requireView(),
                        "Impossibile scaricare professori",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }) {}

        mRequestQueue.add(strRequest)
    }

    companion object {
        fun newInstance() = AddThingsFragment()
    }
}