package com.universita.laboratorioium.ui.recycleAdapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.universita.laboratorioium.R
import com.universita.laboratorioium.databinding.FragmentMyBooksBinding
import com.universita.laboratorioium.databinding.MyBooksAdminRowBinding
import com.universita.laboratorioium.databinding.MyBooksUserRowBinding
import com.universita.laboratorioium.utils.Booking
import kotlin.collections.ArrayList


class MyBooksUserListRecycleAdapter(
    private val dataSet: ArrayList<Booking>
) :
    RecyclerView.Adapter<MyBooksUserListRecycleAdapter.ViewHolder>() {

    private lateinit var view: View

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowCourseName: TextView = MyBooksUserRowBinding.bind(view).textCourseName
        val rowTeacherNameSurname: TextView = MyBooksUserRowBinding.bind(view).textTeacherSurname
        val rowDay: TextView = MyBooksUserRowBinding.bind(view).textDay
        val rowHour: TextView = MyBooksUserRowBinding.bind(view).textHour
        val checkBoxBook: CheckBox = MyBooksUserRowBinding.bind(view).isCheckedBook

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.my_books_user_row, viewGroup, false)
        val viewHolder = ViewHolder(view);

        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val numToDay = arrayOf("Lun", "Mar", "Mer", "Gio", "Ven")
        val letterToHour = mapOf("a" to "15-16", "b" to "16-17", "c" to "17-18", "d" to "18-19")

        when (dataSet[position].status) {
            "booked" -> {
                viewHolder.itemView.setBackgroundColor(Color.WHITE)
            }
            "canceled" -> {
                viewHolder.itemView.setBackgroundColor(Color.RED)
                viewHolder.checkBoxBook.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        compoundButton.isChecked = false
                        Snackbar.make(view, "Non puoi cambiare stato ad una prenotazione già annullata!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            "done" -> {
                viewHolder.itemView.setBackgroundColor(Color.rgb(0x1D, 0xB9, 0x54))//GREEN
                viewHolder.checkBoxBook.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        compoundButton.isChecked = false
                        Snackbar.make(view, "Non puoi cambiare stato ad una prenotazione già fatta!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewHolder.rowDay.text = numToDay[dataSet[position].day]
        viewHolder.rowHour.text = letterToHour[dataSet[position].hour]
        viewHolder.rowCourseName.text = dataSet[position].course.name
        viewHolder.rowTeacherNameSurname.text =
            dataSet[position].teacher.surname + " " + dataSet[position].teacher.name

        //viewHolder.rowDay.text = numToDay[dataSet[position].day]
        //viewHolder.rowHour.text = letterToHour[dataSet[position].hour]
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = dataSet.size

    fun addItem(booking: Booking) {
        dataSet.add(booking)
        notifyItemInserted(dataSet.size - 1)
    }

    fun clear() {
        val size = dataSet.size
        dataSet.clear()
        notifyItemRangeRemoved(0, size)
    }

}