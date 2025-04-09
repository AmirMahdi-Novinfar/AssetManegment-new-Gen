package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.util.SearchType
import com.tehranmunicipality.assetmanagement.util.englishToPersian

class AssetListAdapter(
    private val context: Context,
    private val itemClickListener: AssetItemClickListener
) : RecyclerView.Adapter<AssetListAdapter.DataViewHolder>() {

    private var assetList: MutableList<AssetListItem> = mutableListOf()
    private var filteredAssetList: MutableList<AssetListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_asset: AssetListItem) {

            var item1 = "-"
            var item2 = "-"
            var item3 = "-"
            var item4 = "-"
            var item5 = "-"
            var item6 = "-"
            var item7 = "-"
            var item8 = "-"
            var item9 = "-"
            var item10 = "-"
            var item11 = "-"
            var item12 = "-"
            var item13 = "-"
            var item14 = "-"
            var item15 = "-"
            var item16 = "-"
            var item17 = "-"
            var item18 = "-"

            if (_asset.regionCode != null) {
                item1 = "کد واحد اجرایی : ${_asset.regionCode.toString()}"
            }

            if (_asset.actorName != null) {
                item2 = "نام : ${_asset.actorName.toString()}"
            }

            if (_asset.regionName != null) {
                item3 = "واحد اجرایی : ${_asset.regionName.toString()}"
            }

            if (_asset.costCenterCode != null) {
                item4 = "کد مرکز هزینه اصلی : ${_asset.costCenterCode.toString()}"
            }

            if (_asset.costCenterName != null) {
                item5 = "نام مرکز هزینه اصلی : ${_asset.costCenterName.toString()}"
            }

            if (_asset.subCostCenterCode != null) {
                item6 = "کد مرکز هزینه فرعی : ${_asset.subCostCenterCode.toString()}"
            }

            if (_asset.subCostCenterName != null) {
                item7 = "نام مرکز هزینه فرعی : ${_asset.subCostCenterName.toString()}"
            }

            if (_asset.goodsCode != null) {
                item8 = "کد کالا : ${_asset.goodsCode.toString()}"
            }

            if (_asset.productName != null) {
                item9 = "نام کالا : ${_asset.productName.toString()}"
            }

            if (_asset.assetCode != null) {
                item10 = "کد اموال : ${_asset.assetCode.toString()}"
            }

            if (_asset.assetTag != null) {
                item11 = "برچسب کالا : ${_asset.assetTag.toString()}"
            }

            if (_asset.barCode != null) {
                item12 = "بارکد : ${_asset.barCode.toString()}"
            }

            if (_asset.articlePatternCode != null) {
                item13 = "کد گروه کالا : ${_asset.articlePatternCode.toString()}"
            }

            if (_asset.articlePatternName != null) {
                item14 = "گروه کالا : ${_asset.articlePatternName.toString()}"
            }

            if (_asset.assetHistoryDate != null) {
                item15 = "تاریخ تاریخچه اموال : ${_asset.assetHistoryDate.toString()}"
            }

            if (_asset.assetLocationName != null) {
                item16 = "محل استقرار : ${_asset.assetLocationName.toString()}"
            }

            if (_asset.assetStateName != null) {
                item17 = "وضعیت اموال : ${_asset.assetStateName.toString()}"
            }

            if (_asset.assetStatusName != null) {
                item18 = "وضعیت فیزیکی اموال : ${_asset.assetStatusName.toString()}"
            }

            itemView.findViewById<TextView>(R.id.tvItem1).text =
                "${englishToPersian(item1)}"
            itemView.findViewById<TextView>(R.id.tvItem2).text =
                "${englishToPersian(item2)}"
            itemView.findViewById<TextView>(R.id.tvItem3).text =
                "${englishToPersian(item3)}"
            itemView.findViewById<TextView>(R.id.tvItem4).text =
                "${englishToPersian(item4)}"
            itemView.findViewById<TextView>(R.id.tvItem5).text =
                "${englishToPersian(item5)}"
            itemView.findViewById<TextView>(R.id.tvItem6).text =
                "${englishToPersian(item6)}"
            itemView.findViewById<TextView>(R.id.tvItem7).text =
                "${englishToPersian(item7)}"
            itemView.findViewById<TextView>(R.id.tvItem8).text =
                "${englishToPersian(item8)}"
            itemView.findViewById<TextView>(R.id.tvItem9).text =
                "${englishToPersian(item9)}"
            itemView.findViewById<TextView>(R.id.tvItem10).text =
                "${englishToPersian(item10)}"
            itemView.findViewById<TextView>(R.id.tvItem11).text =
                "${englishToPersian(item11)}"
            itemView.findViewById<TextView>(R.id.tvItem12).text =
                "${englishToPersian(item12)}"
            itemView.findViewById<TextView>(R.id.tvItem13).text =
                "${englishToPersian(item13)}"
            itemView.findViewById<TextView>(R.id.tvItem14).text =
                "${englishToPersian(item14)}"
            itemView.findViewById<TextView>(R.id.tvItem15).text =
                "${englishToPersian(item15)}"
            itemView.findViewById<TextView>(R.id.tvItem16).text =
                "${englishToPersian(item16)}"
            itemView.findViewById<TextView>(R.id.tvItem17).text =
                "${englishToPersian(item17)}"
            itemView.findViewById<TextView>(R.id.tvItem18).text =
                "${englishToPersian(item18)}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_asset_info, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(filteredAssetList[position])

        holder.itemView.findViewById<TextView>(R.id.tvRowNumber).text =
            englishToPersian((position).toString())

        holder.itemView.findViewById<AppCompatButton>(R.id.btnAssetEdit).setOnClickListener {
            itemClickListener.editItemClicked(filteredAssetList[position])



        }

        holder.itemView.findViewById<AppCompatButton>(R.id.btnSetBarcode).setOnClickListener {
            itemClickListener.setBarcodeClicked(filteredAssetList[position])
        }
        holder.itemView.findViewById<AppCompatButton>(R.id.sabtmoreasset).setOnClickListener {
            itemClickListener.setAssetMoreClicked(filteredAssetList[position])
        }
        holder.itemView.findViewById<AppCompatButton>(R.id.sabtmoreasset2).setOnClickListener {
            itemClickListener.setAssetMoreClicked(filteredAssetList[position])
        }
        holder.itemView.findViewById<AppCompatButton>(R.id.sabtmoreasset3).setOnClickListener {
            itemClickListener.setAssetMoreClicked(filteredAssetList[position])
        }
        holder.itemView.findViewById<AppCompatButton>(R.id.sabtmoreasset4).setOnClickListener {
            itemClickListener.setAssetMoreClicked(filteredAssetList[position])
        }
    }

    override fun getItemCount(): Int {
        return filteredAssetList.size
    }

    fun addData(_assetList: List<AssetListItem>) {
        assetList = _assetList as MutableList<AssetListItem>
        filteredAssetList = assetList
        notifyDataSetChanged()
    }

    fun addData(asset: AssetListItem) {
        assetList.add(asset)
        filteredAssetList.add(asset)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        assetList.clear()
        notifyDataSetChanged()
    }

    fun search(searchType: SearchType,searchText: String){
        filteredAssetList = assetList
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
                    SearchType.NationalCode -> {
                        if (row.identityNo?.contains(searchText) == true) {
                            resultList.add(row)
                        }
                    }
                    SearchType.Username -> {
                        if (row.loginName?.contains(searchText) == true) {
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
            notifyDataSetChanged()
        }
    }
}