package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.util.SearchType
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import com.tehranmunicipality.assetmanagement.util.setFormattedText


class AssetExpandableListAdapter internal constructor(
    private val context: Context,
    private val itemClickListener: AssetItemClickListener
) : BaseExpandableListAdapter() {

    private var assetNamesList: List<String> = mutableListOf()
    private var assetList: List<AssetListItem> = mutableListOf()

    private var filteredAssetNamesList: List<String> = mutableListOf()
    private var filteredAssetList: List<AssetListItem> = mutableListOf()

    private lateinit var layoutInflater: LayoutInflater

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        Log.i("DEBUG","getChild listPosition=$listPosition")
        Log.i("DEBUG","getChild expandedListPosition=$expandedListPosition")
        return this.filteredAssetList[listPosition]
    }
    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val assetListItem = getChild(listPosition, expandedListPosition) as AssetListItem
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_asset_info_child, null)
        }
        val tvItem1 = convertView!!.findViewById<TextView>(R.id.tvItem1)
        val tvItem2 = convertView!!.findViewById<TextView>(R.id.tvItem2)
        val tvItem3 = convertView!!.findViewById<TextView>(R.id.tvItem3)
        val tvItem4 = convertView!!.findViewById<TextView>(R.id.tvItem4)
        val tvItem5 = convertView!!.findViewById<TextView>(R.id.tvItem5)
        val tvItem6 = convertView!!.findViewById<TextView>(R.id.tvItem6)
        val tvItem7 = convertView!!.findViewById<TextView>(R.id.tvItem7)
        val tvItem8 = convertView!!.findViewById<TextView>(R.id.tvItem8)
        val tvItem9 = convertView!!.findViewById<TextView>(R.id.tvItem9)
        val tvItem10 = convertView!!.findViewById<TextView>(R.id.tvItem10)
        val tvItem11 = convertView!!.findViewById<TextView>(R.id.tvItem11)
        val tvItem12 = convertView!!.findViewById<TextView>(R.id.tvItem12)
        val tvItem13 = convertView!!.findViewById<TextView>(R.id.tvItem13)

        val btnAssetEdit = convertView!!.findViewById<Button>(R.id.btnAssetEdit)
        val btnSetBarcode = convertView!!.findViewById<Button>(R.id.btnSetBarcode)

        //initializing
        val costCenterInfo = englishToPersian(assetListItem.costCenterCode.toString() + " - " + assetListItem.costCenterName.toString())
        val subCostCenterInfo = englishToPersian(assetListItem.subCostCenterCode.toString() + " - " + assetListItem.subCostCenterName.toString())
        val regionInfo = englishToPersian(assetListItem.regionCode.toString() + " - " + assetListItem.regionName)
        val articlePatternInfo = englishToPersian(assetListItem.articlePatternCode.toString()) + " - " + assetListItem.articlePatternName
        setFormattedText(tvItem1,"واحد اجرایی : ",regionInfo)
        setFormattedText(tvItem2,"مرکز هزینه اصلی : ",costCenterInfo)
        setFormattedText(tvItem3,"مرکز هزینه فرعی : ",subCostCenterInfo)
        setFormattedText(tvItem4,"گروه کالا : ",articlePatternInfo)
        setFormattedText(tvItem5,"تحویل گیرنده : ",englishToPersian(assetListItem.actorName.toString()))
        setFormattedText(tvItem6,"کد کالا : ",englishToPersian(assetListItem.productID.toString()))
        setFormattedText(tvItem7,"کد اموال : ",englishToPersian(assetListItem.assetCode.toString()))
        setFormattedText(tvItem8,"شماره برچسب : ",englishToPersian(assetListItem.assetTag.toString()))
        if (assetListItem.barCode!=-1){
            setFormattedText(tvItem9,"بارکد اموال : ",englishToPersian(assetListItem.barCode.toString()))

        }else{
            setFormattedText(tvItem9,"بارکد اموال : ","فاقد اموال")

        }

        setFormattedText(tvItem10,"تاریخ اموال : ",englishToPersian(assetListItem.assetHistoryDate.toString()))
        setFormattedText(tvItem11,"وضعیت فیزیکی : ",englishToPersian(assetListItem.assetStateName.toString()))
        setFormattedText(tvItem12,"وضعیت اموال : ",englishToPersian(assetListItem.assetStatusName.toString()))
        setFormattedText(tvItem13,"محل استقرار : ",englishToPersian(assetListItem.assetLocationName.toString()))

        btnAssetEdit.setOnClickListener {
            itemClickListener.editItemClicked(filteredAssetList[listPosition])
        }

        btnSetBarcode.setOnClickListener {
            itemClickListener.setBarcodeClicked(filteredAssetList[listPosition])
        }

        if (assetListItem.barCode.toString().equals("-1")){
            val linearLayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            linearLayoutParams.weight = 5F
            linearLayoutParams.setMargins(0,12,0,12)
            btnAssetEdit.layoutParams = linearLayoutParams
            btnSetBarcode.visibility = View.VISIBLE
            btnSetBarcode.setText("ثبت بارکد")
        }else{
            val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            linearLayoutParams.setMargins(0,12,0,12)
            btnAssetEdit.layoutParams = linearLayoutParams
            btnSetBarcode.visibility = View.GONE
        }

        return convertView
    }
    override fun getChildrenCount(listPosition: Int): Int {
        return 1
    }
    override fun getGroup(listPosition: Int): Any {
        return this.filteredAssetNamesList[listPosition]
    }
    override fun getGroupCount(): Int {
        return this.filteredAssetNamesList.size
    }
    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var _convertView = convertView
        val assetName = getGroup(listPosition) as String

        if (convertView == null) {

            val viewHolder = ViewHolder()

            layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            _convertView = layoutInflater.inflate(R.layout.row_asset_info_group, null)

            viewHolder.addView(_convertView.rootView)
            _convertView.tag = viewHolder
        }

        val viewHolder = _convertView?.tag as com.tehranmunicipality.assetmanagement.ui.asset_information.ViewHolder

        viewHolder.getView(R.id.rlRowGroupRoot)

        val tvAssetName = viewHolder.getView(R.id.rlRowGroupRoot).findViewById<TextView>(R.id.tvAssetName)
        val ivDropDownAsset = viewHolder.getView(R.id.rlRowGroupRoot).findViewById<ImageView>(R.id.ivDropDownAsset)
        tvAssetName.setTypeface(null, Typeface.BOLD)
        tvAssetName.text = englishToPersian(assetName)

        return _convertView
    }
    override fun hasStableIds(): Boolean {
        return false
    }
    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    fun addData(_assetNamesList: List<String>,_assetList: List<AssetListItem>){
        assetNamesList = _assetNamesList
        assetList = _assetList
        filteredAssetNamesList = _assetNamesList
        filteredAssetList = _assetList
    }

    fun search(searchType: SearchType, searchText: String){
        Log.i("DEBUG","inside search. searchText=$searchText")
        Log.i("DEBUG","inside search. searchType=${searchType}")
        filteredAssetList = assetList
        filteredAssetNamesList = assetNamesList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<AssetListItem>()
            for (row in filteredAssetList){
                when(searchType) {

                    SearchType.TagText -> {
                        if (row.assetTag?.contains(searchText) == true) {
                            resultList.add(row)
                        }
                    }
                    SearchType.Barcode -> {
                        if (row.barCode?.toString()?.contains(searchText) == true) {
                            resultList.add(row)
                        }
                    }
                    SearchType.Location -> {
                        if (row.assetLocationName?.contains(searchText) == true) {
                            resultList.add(row)
                        }
                    }
                    SearchType.AssetName -> {
                        if (row.productName?.contains(searchText) == true) {
                            resultList.add(row)
                        }
                    }
                }
            }
            filteredAssetList = resultList
            val assetNamesListTemp = mutableListOf<String>()
            for (item in filteredAssetList){
                assetNamesListTemp.add(item.productName.toString())
            }
            filteredAssetNamesList = assetNamesListTemp
            notifyDataSetChanged()
        }
    }
}