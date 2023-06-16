package com.universita.laboratorioium.ui.recycleAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.universita.laboratorioium.R
import kotlin.collections.ArrayList


class BookTutoringListRecycleAdapter(
    private val dataSet: ArrayList<HashMap<String, String>>,
    private val isRegisteredAndNotAdmin: Boolean
) :
    RecyclerView.Adapter<BookTutoringListRecycleAdapter.ViewHolder>() {

    private lateinit var view: View

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowCourseName: TextView = view.findViewById(R.id.textCourseName)
        val rowTeacherSurname: TextView = view.findViewById(R.id.textTeacherSurname)
        val rowHour: TextView = view.findViewById(R.id.textHour)
        val checkBoxBook: CheckBox = view.findViewById(R.id.isCheckedBookTutoring)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.book_tutoring_row, viewGroup, false)
        val viewHolder = ViewHolder(view);

        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //viewHolder.setIsRecyclable(false);

        val numToDay = arrayOf("Lun", "Mar", "Mer", "Gio", "Ven")

        if (!isRegisteredAndNotAdmin) {
            viewHolder.checkBoxBook.setOnCheckedChangeListener { compoundButton, b ->
                viewHolder.checkBoxBook.isChecked = false
                Snackbar.make(view, "Non sei abilitato a prenotare", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewHolder.rowHour.text = dataSet[position]["time"]
        viewHolder.rowCourseName.text = dataSet[position]["courseName"]
        viewHolder.rowTeacherSurname.text = dataSet[position]["teacherSurnameName"]

        //viewHolder.rowDay.text = numToDay[dataSet[position].day]
        //viewHolder.rowHour.text = letterToHour[dataSet[position].hour]
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = dataSet.size


}