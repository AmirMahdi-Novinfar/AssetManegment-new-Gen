package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.util.SearchType
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import com.tehranmunicipality.assetmanagement.util.getPersianDate
import com.tehranmunicipality.assetmanagement.util.setFormattedText

/**
 * Created by david - dchoopani@yahoo.com on 12, November, 2023
 */

class AssetExpandableListAdapter2(
    private val context: Context,
    private val itemClickListener: AssetItemClickListener
) : RecyclerView.Adapter<AssetExpandableListAdapter2.RowViewHolder>() {

    private var assetList: List<AssetListItem> = mutableListOf()
    private var filteredAssetList: List<AssetListItem> = mutableListOf()
    private var expandedStates: MutableList<Boolean> = mutableListOf()
    private lateinit var assetListItem: AssetListItem
    private lateinit var layoutInflater: LayoutInflater

    init {
        expandedStates = MutableList(assetList.size) { false }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val rowView: View =
            layoutInflater.inflate(R.layout.row_asset_info_expandable, parent, false)
        val holder = RowViewHolder(rowView)

        // Set the initial visibility state
//        holder.rlRowChildRoot.findViewById<RelativeLayout>(R.id.rlRowChildRoot).visibility =
//            View.GONE
//        holder.ivDropDownAsset.setImageResource(R.drawable.ic_down)

        return holder
    }

    override fun getItemCount(): Int = filteredAssetList.size

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {

        assetListItem = filteredAssetList[position]

        holder.itemView.findViewById<TextView>(R.id.assetNumber).text =
            englishToPersian((position + 1).toString())

        //group items
        val rlRowGroupRoot = holder.rlRowGroupRoot.findViewById<RelativeLayout>(R.id.rlRowGroupRoot)
        val tvAssetName = holder.tvAssetName.findViewById<TextView>(R.id.tvAssetName)
        val ivDropDownAsset = holder.ivDropDownAsset.findViewById<ImageView>(R.id.ivDropDownAsset)
        val rlBarcode = holder.rlBarcode.findViewById<RelativeLayout>(R.id.rlBarcode)
        val tvBarcode = holder.tvBarcode.findViewById<TextView>(R.id.tvBarcode)


//        if (assetListItem.barCode != -1 && assetListItem.assetHistoryDate?.equals(getPersianDate())==true){
//            rlBarcode.background =
//                ContextCompat.getDrawable(context, R.drawable.background_blue_borders_round)
//        }else{
      var validtime= assetListItem.assetHistoryDate?.split(" ")?.get(0)
        if (validtime.equals(getPersianDate())) {
            rlBarcode.background =
                ContextCompat.getDrawable(context, R.drawable.background_yellow_borders_round)
        } else if (assetListItem.barCode != "") {
            rlBarcode.background =
                ContextCompat.getDrawable(context, R.drawable.background_green_borders_round)
        } else if (assetListItem.barCode == "") {
            rlBarcode.background =
                ContextCompat.getDrawable(context, R.color.white)
        }


        if (assetListItem.barCode!=""){
            tvBarcode.text = englishToPersian(assetListItem.barCode.toString())
        }else{
            tvBarcode.text = "فاقد بارکد"
        }

        //child items
        val rlRowChildRoot = holder.rlRowChildRoot.findViewById<RelativeLayout>(R.id.rlRowChildRoot)
        rlRowChildRoot.setBackgroundColor(Color.TRANSPARENT)
        val tvItem1 = holder.tvItem1.findViewById<TextView>(R.id.tvItem1)
        val tvItem2 = holder.tvItem2.findViewById<TextView>(R.id.tvItem2)
        val tvItem3 = holder.tvItem3.findViewById<TextView>(R.id.tvItem3)
        val tvItem4 = holder.tvItem4.findViewById<TextView>(R.id.tvItem4)
        val tvItem5 = holder.tvItem5.findViewById<TextView>(R.id.tvItem5)
        val tvItem7 = holder.tvItem7.findViewById<TextView>(R.id.tvItem7)
        val tvItem9 = holder.tvItem9.findViewById<TextView>(R.id.tvItem9)
        val tvItem11 = holder.tvItem11.findViewById<TextView>(R.id.tvItem11)


        tvAssetName.text = englishToPersian(assetListItem.productName.toString())

        //initializing
        val costCenterInfo =
            englishToPersian(assetListItem.costCenterCode.toString() + " - " + assetListItem.costCenterName.toString())
        val subCostCenterInfo =
            englishToPersian(assetListItem.subCostCenterCode.toString() + " - " + assetListItem.subCostCenterName.toString())

        setFormattedText(tvItem1, "کد ملی : ", assetListItem.identityNo.toString())
        setFormattedText(tvItem2, "تحویل گیرنده : ", assetListItem.actorName.toString() )
        setFormattedText(tvItem3, "محل استقرار : ",assetListItem.assetLocationName.toString() )
        setFormattedText(tvItem4, "برچسب اموال : ",assetListItem.assetTag.toString() )
        setFormattedText(
            tvItem5, "تاریخ آخرین ویرایش : ",
            englishToPersian(assetListItem.assetHistoryDate.toString())
        )
        setFormattedText(
            tvItem7, "نام مرکز هزینه اصلی : ",
            englishToPersian(costCenterInfo)
        )

        setFormattedText(
            tvItem9, "نام مرکز هزینه فرعی : ",
            englishToPersian(subCostCenterInfo)
        )

        if (assetListItem.barCode!="") {
            setFormattedText(
                tvItem11, "بارکد اموال : ",
                englishToPersian(assetListItem.barCode.toString())
            )
        }else{
            setFormattedText(
                tvItem11, "بارکد اموال : ",
                englishToPersian("فاقد بارکد")
            )        }


        holder.btnAssetEdit.findViewById<Button>(R.id.btnAssetEdit)

        holder.btnAssetEdit.setOnClickListener {
            assetListItem = filteredAssetList[position]
            itemClickListener.editItemClicked(assetListItem)
        }

        holder.btnSetBarcode.findViewById<Button>(R.id.btnSetBarcode).setOnClickListener {
            assetListItem = filteredAssetList[position]
            itemClickListener.setBarcodeClicked(assetListItem)
        }
        holder.btnmoreasset.findViewById<Button>(R.id.btnAddAssetTag).setOnClickListener {
            assetListItem = filteredAssetList[position]
            itemClickListener.setAssetMoreClicked(assetListItem)
        }

//        if (assetListItem.barCode.toString().equals("")) {
//            val linearLayoutParams =
//                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
//            linearLayoutParams.weight = 5F
//            linearLayoutParams.setMargins(16, 16, 16, 16)
//            holder.btnAssetEdit.findViewById<Button>(R.id.btnAssetEdit).layoutParams =
//                linearLayoutParams
//            holder.btnSetBarcode.findViewById<Button>(R.id.btnSetBarcode).visibility = View.VISIBLE
//            holder.btnSetBarcode.findViewById<Button>(R.id.btnSetBarcode).layoutParams =
//                linearLayoutParams
//            holder.btnSetBarcode.findViewById<Button>(R.id.btnSetBarcode)
//                .setText("تخصیص بارکد اموال")
//        } else {
//            val linearLayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT
//            )
//            linearLayoutParams.setMargins(16, 16, 16, 16)
//            holder.btnAssetEdit.findViewById<Button>(R.id.btnAssetEdit).layoutParams =
//                linearLayoutParams
//            holder.btnSetBarcode.findViewById<Button>(R.id.btnSetBarcode).visibility = View.GONE
//        }

        rlRowGroupRoot.setOnClickListener {
            Log.i("DEBUG", "rlRowGroupRoot clicked.position=$position")

            if (expandedStates[position]) {
                rlRowChildRoot.findViewById<RelativeLayout>(R.id.rlRowChildRoot).visibility =
                    View.GONE
                ivDropDownAsset.setImageResource(R.drawable.ic_down)
            } else {
                rlRowChildRoot.findViewById<RelativeLayout>(R.id.rlRowChildRoot).visibility =
                    View.VISIBLE
                ivDropDownAsset.setImageResource(R.drawable.ic_up)
            }

            // Toggle the expanded state
            expandedStates[position] = !expandedStates[position]
        }
    }

    fun addData(_assetList: List<AssetListItem>) {
        assetList = _assetList
        filteredAssetList = _assetList
        expandedStates = MutableList(_assetList.size) { false }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class RowViewHolder(row: View) : RecyclerView.ViewHolder(row) {

        val rlRowGroupRoot = row.findViewById<RelativeLayout>(R.id.rlRowGroupRoot)
        val rlBarcode = row.findViewById<RelativeLayout>(R.id.rlBarcode)
        val tvBarcode = row.findViewById<TextView>(R.id.tvBarcode)
        val tvAssetName = row.findViewById<TextView>(R.id.tvAssetName)
        val tvAssetTag = row.findViewById<TextView>(R.id.tvAssetTag)
        val ivDropDownAsset = row.findViewById<ImageView>(R.id.ivDropDownAsset)

        val rlRowChildRoot = row.findViewById<RelativeLayout>(R.id.rlRowChildRoot)
        val tvItem1 = row.findViewById<TextView>(R.id.tvItem1)
        val tvItem2 = row.findViewById<TextView>(R.id.tvItem2)
        val tvItem3 = row.findViewById<TextView>(R.id.tvItem3)
        val tvItem4 = row.findViewById<TextView>(R.id.tvItem4)
        val tvItem5 = row.findViewById<TextView>(R.id.tvItem5)
        val tvItem6 = row.findViewById<TextView>(R.id.tvItem6)
        val tvItem7 = row.findViewById<TextView>(R.id.tvItem7)
        val tvItem8 = row.findViewById<TextView>(R.id.tvItem8)
        val tvItem9 = row.findViewById<TextView>(R.id.tvItem9)
        val tvItem10 = row.findViewById<TextView>(R.id.tvItem10)
        val tvItem11 = row.findViewById<TextView>(R.id.tvItem11)
        val tvItem12 = row.findViewById<TextView>(R.id.tvItem12)
        val tvItem13 = row.findViewById<TextView>(R.id.tvItem13)
        val btnAssetEdit = row.findViewById<Button>(R.id.btnAssetEdit)
        val btnSetBarcode = row.findViewById<Button>(R.id.btnSetBarcode)
        val btnmoreasset = row.findViewById<Button>(R.id.btnAddAssetTag)

    }

    fun search(searchType: SearchType, searchText: String) {
        Log.i("DEBUG", "inside search. searchText=$searchText")
        Log.i("DEBUG", "inside search. searchType=${searchType}")
        filteredAssetList = assetList
        if (searchText.isEmpty()) {
        } else {
            val resultList = mutableListOf<AssetListItem>()
            for (row in filteredAssetList) {
                when (searchType) {

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
                    else -> {

                    }
                }
            }
            filteredAssetList = resultList
            notifyDataSetChanged()
        }
    }
}

