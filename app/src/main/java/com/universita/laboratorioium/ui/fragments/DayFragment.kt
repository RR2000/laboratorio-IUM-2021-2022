package com.universita.laboratorioium.ui.fragments

import android.content.Context
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
import com.google.android.material.tabs.TabLayout
import com.universita.laboratorioium.R
import com.universita.laboratorioium.databinding.FragmentDayBinding
import com.universita.laboratorioium.ui.recycleAdapters.BookTutoringListRecycleAdapter
import com.universita.laboratorioium.ui.support.ListableListViewModel
import android.os.Handler
import com.universita.laboratorioium.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DayFragment : Fragment() {
    private var dayNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dayNum = it.getInt("DAY_NUM")
        }
    }

    override fun onResume() {
        BookTutoringFragment.refreshAvailableOfDay(
            requireView().rootView.findViewById<TabLayout>(R.id.tabs).selectedTabPosition,
            this
        )
        observeIfDayListFilled(this, dayNum)
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentDayBinding.bind(view)
        val model: ListableListViewModel by activityViewModels()
        val registeredAndNotAdmin =
            (model.user.value != null).let { it && !model.user.value?.isAdmin!! }
        var adapter = BookTutoringListRecycleAdapter(ArrayList(), registeredAndNotAdmin)

        binding.bookableList.layoutManager = LinearLayoutManager(view.context)
        binding.bookableList.adapter = adapter

        Log.w("Oggi è: ", dayNum.toString())

        val teachesOfDay = when (dayNum) {
            0 -> model.teachesOfDay0
            1 -> model.teachesOfDay1
            2 -> model.teachesOfDay2
            3 -> model.teachesOfDay3
            4 -> model.teachesOfDay4
            else -> model.teachesOfDay0
        }

        val numToDay = arrayOf("Lun", "Mar", "Mer", "Gio", "Ven")
        teachesOfDay.observe(viewLifecycleOwner) {
            val array = ArrayList<HashMap<String, String>>()
            it?.forEach { it1 ->
                array.add(it1!!)
            }
            adapter = BookTutoringListRecycleAdapter(array, registeredAndNotAdmin)
            binding.bookableList.adapter = adapter
        }

        model.user.observe(viewLifecycleOwner) {
            when {
                it == null -> {//NOT LOGGED
                    binding.bookButton.visibility = View.GONE
                }
                it.isAdmin -> {
                    binding.bookButton.visibility = View.GONE
                }
                else -> { //LOGGED NOT ADMIN
                    binding.bookButton.visibility = View.VISIBLE
                }
            }
        }

        binding.bookButton.setOnClickListener {

            val selectedTeaches: ArrayList<HashMap<String, String>?> = ArrayList()

            binding.bookableList.forEachIndexed { index, view ->
                val isChecked = view.findViewById<CheckBox>(R.id.isCheckedBookTutoring).isChecked

                if (isChecked) {
                    selectedTeaches.add(teachesOfDay.value?.get(index)!!)
                }

            }

            CoroutineScope(Dispatchers.IO).launch {
                selectedTeaches.forEach {
                    if (it != null) {
                        sendBook(it)
                    }
                    delay(250)
                }
                Snackbar.make(
                    requireView(),
                    "Apri \"Le mie prenotazioni\" per vedere le ripetizioni prenotate",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(dayNum: Int): DayFragment {
            return DayFragment().apply {
                arguments = Bundle().apply {
                    putInt("DAY_NUM", dayNum)
                }
            }
        }

        fun observeIfDayListFilled(fragment: Fragment, day: Int) {
            val model: ListableListViewModel by fragment.activityViewModels()
            val numToDay = arrayOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì")
            val teachesOfDay = when (day) {
                0 -> model.teachesOfDay0
                1 -> model.teachesOfDay1
                2 -> model.teachesOfDay2
                3 -> model.teachesOfDay3
                4 -> model.teachesOfDay4
                else -> model.teachesOfDay0
            }

            teachesOfDay.observe(fragment.viewLifecycleOwner) {
                if (it?.size == 0)
                    Snackbar.make(
                        fragment.requireView(),
                        "Non ci sono insegnamenti disponibili ${numToDay[day]}",
                        Snackbar.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun sendBook(book: HashMap<String, String>) {
        val mRequestQueue = Volley.newRequestQueue(context)
        val strRequest = object : StringRequest(
            Method.POST, BuildConfig.BASE_URL + "/api/set?type=book",
            Response.Listener { response ->
                Log.w("Response", response)
                BookTutoringFragment.refreshAvailableOfDay(
                    requireView().rootView.findViewById<TabLayout>(R.id.tabs).selectedTabPosition,
                    this
                )
            },
            Response.ErrorListener { error ->
                when (error.networkResponse?.statusCode) {
                    403 -> Snackbar.make(
                        requireView(),
                        "Non ti è permmesso prenotare",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    409 -> Snackbar.make(
                        requireView(),
                        "Professore e/o utente già impegnato",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    500 -> Snackbar.make(requireView(), "Errore del server", Snackbar.LENGTH_SHORT)
                        .show()
                    else -> Toast.makeText(
                        requireContext(),
                        "Impossibile prenotare",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> {
                return book
            }
        }

        mRequestQueue.add(strRequest)
    }

}