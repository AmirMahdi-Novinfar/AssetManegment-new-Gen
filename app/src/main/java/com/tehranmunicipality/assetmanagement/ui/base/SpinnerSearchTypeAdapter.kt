package com.tehranmunicipality.assetmanagement.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tehranmunicipality.assetmanagement.R
import org.w3c.dom.Text


class SpinnerSearchTypeAdapter(context: Context, list : Array<String>)
    : ArrayAdapter<String>(context, 0 , list){

    private var layoutInflater = LayoutInflater.from(context)
   var context2: Context =context
   var list2 : Array<String> = list


    public fun spinnerSearchTypeAdapter(context: Context, list : Array<String>){

        list2=list
        context2=context
    }
    override fun getCount(): Int {
        return list2.size // تعداد آیتم‌ها را برمی‌گرداند
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val view: View = layoutInflater.inflate(R.layout.spinner_layout, null, true)
        return view(view, position)

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        var cv = convertView
        if(cv == null) {
            cv = layoutInflater.inflate(R.layout.spinner_layout, parent, false)

        }
        return view(cv!!, position)

    }

    private fun view(view: View, position: Int): View
    {
        val textView :TextView=view.findViewById(R.id.text_spinner_one)
        textView.setText(list2.get(position))


        val line:View=view.findViewById(R.id.line_spiiner)

        if (position==1){
            line.visibility=View.GONE
        }
        return view
    }


    }


