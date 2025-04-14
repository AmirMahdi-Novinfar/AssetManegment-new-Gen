package com.tehranmunicipality.assetmanagement.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogListClickListener

/**
 * Created by AmirMahdi Novinfar - amirnovin80@gmail.com on 28,October,2023
 */


class SpinnerAdapter(context: Context, items: Array<String>,_dialogListItemClickListener: IDialogListClickListener) :
    BaseAdapter() {
    private val context: Context
    private val items: Array<String>
    private val dialogListItemClickListener: IDialogListClickListener = _dialogListItemClickListener

    init {
        this.context = context
        this.items = items
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.row_search_type, parent, false)
        }
        val itemTextView = convertView!!.findViewById<TextView>(R.id.tvSearchType)
        val tvSeparator = convertView!!.findViewById<View>(R.id.vwSeparator)
        itemTextView.text = items[position]

        if (position == items.size-1){
            tvSeparator.visibility = View.GONE
        }else{
            tvSeparator.visibility = View.VISIBLE
        }

        convertView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                dialogListItemClickListener.onClick(p0,items[position])
            }
        })


        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.row_search_type, parent, false)
        }
        val itemTextView = convertView!!.findViewById<TextView>(R.id.tvSearchType)
        val tvSeparator = convertView!!.findViewById<View>(R.id.vwSeparator)
        itemTextView.text = items[position]

        if (position == items.size -1){
            tvSeparator.visibility = View.GONE
        }else{
            tvSeparator.visibility = View.VISIBLE
        }

        return convertView
    }
}