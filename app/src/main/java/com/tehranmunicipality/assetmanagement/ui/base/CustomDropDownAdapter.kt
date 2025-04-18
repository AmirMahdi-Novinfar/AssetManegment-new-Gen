package com.tehranmunicipality.assetmanagement.ui.base

/**
 * Created by AmirMahdi Novinfar - amirnovin80@gmail.com on 07,November,2023
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tehranmunicipality.assetmanagement.R

class CustomDropDownAdapter(val context: Context, var dataSource: List<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner_item, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.itemName.text = dataSource.get(position)

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(row: View?) {
        val itemName: TextView
        init {
            itemName = row?.findViewById(R.id.tvItem) as TextView
        }
    }

}