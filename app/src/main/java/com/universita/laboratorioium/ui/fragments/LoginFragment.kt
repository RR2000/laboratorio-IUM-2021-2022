package com.universita.laboratorioium.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import com.android.volley.Network
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.universita.laboratorioium.BuildConfig
import com.universita.laboratorioium.R
import com.universita.laboratorioium.databinding.FragmentLoginBinding
import com.universita.laboratorioium.ui.main.MainActivity
import com.universita.laboratorioium.ui.support.ListableListViewModel
import com.universita.laboratorioium.utils.*

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        binding.editTextUsername.setText(
            requireContext().getSharedPreferences(
                "LOGIN",
                Context.MODE_PRIVATE
            ).getString("username", "")
        )
        binding.editTextPassword.setText(
            requireContext().getSharedPreferences(
                "LOGIN",
                Context.MODE_PRIVATE
            ).getString("password", "")
        )

        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (username == "" || password == "") {
                Snackbar.make(
                    requireView(),
                    "Username e/o password mancanti",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                sendLogin(username, password)
                requireContext().getSharedPreferences("LOGIN", Context.MODE_PRIVATE).edit {
                    putString("username", binding.editTextUsername.text.toString())
                    putString("password", binding.editTextPassword.text.toString())
                }
            }

            MainActivity.closeKeyboard(requireContext())
        }

        binding.buttonWithoutLogin.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(
                R.id.nav_host_fragment,
                BookTutoringFragment.newInstance()
            ).commit()
            MainActivity.closeKeyboard(requireContext())
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {

        val model: ListableListViewModel by activityViewModels()
        val sharedPreferences = requireContext().getSharedPreferences("LOGIN", Context.MODE_PRIVATE)

        if (model.user.value != null) {//logout
            sendLogout()
            sharedPreferences.edit {
                putString("username", "")
                putString("password", "")
            }
        } else {//login
            val username = sharedPreferences.getString("username", "")!!
            val password = sharedPreferences.getString("password", "")!!
            if (username == "" || password == "")
                Snackbar.make(
                    requireView(),
                    "Username e/o password mancanti",
                    Snackbar.LENGTH_SHORT
                ).show()
            else
                sendLogin(username, password)
        }
        super.onResume()
    }

    private fun sendLogout() {
        val mRequestQueue = Volley.newRequestQueue(context)
        val model: ListableListViewModel by activityViewModels()

        val strRequest: StringRequest = object : StringRequest(
            Method.GET, BuildConfig.BASE_URL + "/session?logout",
            Response.Listener { response ->
                model.bookings.value = null
                model.courses.value = null
                model.teachers.value = null
                model.teaches.value = null
                model.teachesOfDay0.value = null
                model.teachesOfDay1.value = null
                model.teachesOfDay2.value = null
                model.teachesOfDay3.value = null
                model.teachesOfDay4.value = null
                model.user.value = null
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireContext(), "Impossibile sloggarsi", Toast.LENGTH_SHORT).show()
            }) {}
        mRequestQueue.add(strRequest)
    }

    private fun sendLogin(username: String, password: String) {

        val mRequestQueue = Volley.newRequestQueue(context)
        val model: ListableListViewModel by activityViewModels()
        val strRequest: StringRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/session",
            Response.Listener { response ->
                model.user.postValue(Gson().fromJson(response, User::class.java))
                parentFragmentManager.beginTransaction().replace(
                    R.id.nav_host_fragment,
                    BookTutoringFragment.newInstance()
                ).commit()
                Snackbar.make(
                    requireView(),
                    "Loggato come $username",
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Username e/o password errata",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile loggarsi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = username
                params["password"] = password
                return params
            }
        }

        mRequestQueue.add(strRequest)
    }

    companion object {
        fun newInstance(): Fragment {
            return LoginFragment()
        }
    }
}