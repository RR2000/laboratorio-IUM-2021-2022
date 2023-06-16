package com.universita.laboratorioium.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.forEachIndexed
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.universita.laboratorioium.BuildConfig
import com.universita.laboratorioium.R
import com.universita.laboratorioium.databinding.FragmentMyBooksBinding
import com.universita.laboratorioium.ui.recycleAdapters.MyBooksAdminListRecycleAdapter
import com.universita.laboratorioium.ui.recycleAdapters.MyBooksUserListRecycleAdapter
import com.universita.laboratorioium.ui.support.ListableListViewModel
import com.universita.laboratorioium.utils.Booking


class MyBooksFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_books, container, false)
    }

    override fun onResume() {
        refreshBookings()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMyBooksBinding.bind(view)
        val model: ListableListViewModel by activityViewModels()
        binding.listableMyBooks.layoutManager = LinearLayoutManager(view.context)

        model.user.observe(viewLifecycleOwner) {
            when {
                it == null -> {//NOT LOGGED
                    binding.buttonDoneBook.visibility = View.GONE
                    binding.buttonCancelBook.visibility = View.GONE
                }
                it.isAdmin -> {
                    binding.buttonDoneBook.visibility = View.GONE
                    binding.buttonCancelBook.visibility = View.VISIBLE
                }
                else -> { //LOGGED NOT ADMIN
                    binding.buttonDoneBook.visibility = View.VISIBLE
                    binding.buttonCancelBook.visibility = View.VISIBLE
                }
            }
        }

        model.bookings.observe(viewLifecycleOwner) {
            val array = ArrayList<Booking>()
            it?.forEach { it1 ->
                array.add(it1!!)
            }

            val adapter =
                if (model.user.value?.isAdmin == true) MyBooksAdminListRecycleAdapter(array)
                else MyBooksUserListRecycleAdapter(array)

            binding.listableMyBooks.adapter = adapter
        }

        binding.refreshMyBooks.setOnRefreshListener {
            refreshBookings()
            binding.refreshMyBooks.isRefreshing = false
        }

        binding.buttonCancelBook.setOnClickListener {
            binding.listableMyBooks.forEachIndexed { index, view ->
                val isChecked = view.findViewById<CheckBox>(R.id.isCheckedBook).isChecked
                if (isChecked) {
                    sendCancel(model.bookings.value?.get(index)!!)
                }
            }
        }

        binding.buttonDoneBook.setOnClickListener {
            binding.listableMyBooks.forEachIndexed { index, view ->
                val isChecked = view.findViewById<CheckBox>(R.id.isCheckedBook).isChecked
                if (isChecked) {
                    sendDone(model.bookings.value?.get(index)!!)
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = MyBooksFragment()
    }

    private fun refreshBookings() {
        val model: ListableListViewModel by activityViewModels()
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.GET, BuildConfig.BASE_URL + "/api/get?type=bookings",
            Response.Listener { response ->
                Log.w("Response", response)
                val listType = object : TypeToken<ArrayList<Booking?>?>() {}.type
                model.bookings.postValue(Gson().fromJson(response, listType) ?: ArrayList())
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso scaricare le prenotazioni",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(
                        requireView(),
                        "Errore del server",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile scaricare prenotazioni",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {}

        mRequestQueue.add(strRequest)
    }

    private fun changeStatus(bookingId: String, status: String) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/set?type=updateStatus",
            Response.Listener { response ->
                Log.w("Response", response)
                val text = when (status) {
                    "done" -> "La prenotazione è stata segnata come effettuata"
                    "canceled" -> "La prenotazione è stata annullata"
                    else -> "Sei riuscito ad inviare uno status non valido, complimenti!"
                }
                Snackbar.make(
                    requireView(),
                    text,
                    Snackbar.LENGTH_SHORT
                ).show()
                refreshBookings()
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso cambiare stato prenotazione",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    400 -> Snackbar.make(
                        requireView(),
                        "Richiesta cambio stato prenotazione non valida",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(
                        requireView(),
                        "Errore del server",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile cambiare stato prenotazione",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["id"] = bookingId
                map["status"] = status
                return map
            }
        }

        mRequestQueue.add(strRequest)
    }

    private fun sendCancel(booking: Booking) {
        changeStatus(booking.id.toString(), "canceled")
    }

    private fun sendDone(booking: Booking) {
        changeStatus(booking.id.toString(), "done")
    }
}