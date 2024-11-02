package com.tehranmunicipality.assetmanagement.ui.person_list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.PersonInfo
import com.tehranmunicipality.assetmanagement.data.model.PersonListItem
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import com.tehranmunicipality.assetmanagement.util.setFormattedText

class PersonListAdapter(
    private val personItemClickListener: IPersonItemClickListener
) : RecyclerView.Adapter<PersonListAdapter.DataViewHolder>() {

    private var personList: MutableList<PersonListItem?>? = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_personItem: PersonListItem?) {

            var item1 = "-"
            var item2 = "-"
            var item3 = "-"

            if (_personItem?.firstName != null && _personItem.lastName != null) {
                item1 =
                    "${_personItem.firstName.toString()} ${_personItem.lastName}"
            }

            if (_personItem?.identityNo != null) {
                item2 = "${_personItem.identityNo}"
            }

            item3 = "سازمان فناوری اطلاعات و ارتباطات شهرداری تهران"

            val name = englishToPersian(item1)
            val identityNo = englishToPersian(item2)
            val location = englishToPersian(item3)

            setFormattedText(itemView.findViewById(R.id.tvName), "نام : ", name)
            setFormattedText(itemView.findViewById(R.id.tvIdentity), "کد ملی : ", identityNo)
            setFormattedText(
                itemView.findViewById(R.id.tvLocation),
                "محل استقرار : ",
                location
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_person_info, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(personList?.get(position))


        holder.itemView.setOnClickListener {
            personItemClickListener.itemClicked(personList?.get(position)?.lastName.toString())
        }
    }

    override fun getItemCount(): Int {
        return personList!!.size
    }

    fun addData(_personList: List<PersonListItem?>) {
        personList = _personList as MutableList<PersonListItem?>
        notifyDataSetChanged()
    }

    fun addData(_personItem: PersonListItem) {
        personList?.add(_personItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        personList?.clear()
        notifyDataSetChanged()
    }

}