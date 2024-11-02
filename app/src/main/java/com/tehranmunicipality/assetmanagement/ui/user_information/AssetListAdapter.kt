package com.tehranmunicipality.assetmanagement.ui.user_information

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.util.englishToPersian

class AssetListAdapter(
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<AssetListAdapter.DataViewHolder>() {

    private var assetList: MutableList<AssetListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_asset: AssetListItem) {

            var name = "-"
            var barcode = "-"
            var assetCode = "-"
            var assetTag = "-"
            var asset = "-"
            var assetMainGroup = "-"
            var costCenter = "-"
            var subCostCenter = "-"
            var assetLocation = "-"
            var assetStatus = "-"
            var assetPhysicalStatus = "-"
            var item12 = "-"
            var item13 = "-"
            var item14 = "-"
            var item15 = "-"
            var item16 = "-"
            var item17 = "-"
            var item18 = "-"
            var item19 = "-"
            var item20 = "-"
            var item21 = "-"
            var item22 = "-"
            var item23 = "-"
            var item24 = "-"
            var item25 = "-"
            var item26 = "-"
            var item27 = "-"
            var item28 = "-"
            var item29 = "-"
            var item30 = "-"
            var item31 = "-"
            var item32 = "-"
            var item33 = "-"

            if (_asset.actorName != null) {
                name = "نام:${_asset.actorName.toString()}"
            }

            if (_asset.barCode != null) {
                barcode = "بارکد:${_asset.barCode.toString()}"
            }

            if (_asset.goodsCode != null) {
                assetCode = "کداموال:${_asset.goodsCode.toString()}"
            }

            if (_asset.assetTag != null) {
                assetTag = "برچسب:${_asset.assetTag.toString()}"
            }

            if (_asset.productName != null && _asset.assetCode != null) {
                asset = "${_asset.assetCode}-${_asset.productName}"
            }

            if (_asset.articlePatternCode != null && _asset.articlePatternName != null) {
                assetMainGroup =
                    "${_asset.articlePatternCode}-${_asset.articlePatternName.toString()}"
            }

            if (_asset.costCenterCode != null && _asset.costCenterName != null) {
                costCenter = "${_asset.costCenterCode}-${_asset.costCenterName.toString()}"
            }

            if (_asset.subCostCenterCode != null && _asset.subCostCenterName != null) {
                subCostCenter = "${_asset.subCostCenterCode}-${_asset.subCostCenterName.toString()}"
            }

            if (_asset.assetLocationID != null && _asset.assetLocationName != null) {
                assetLocation = "${_asset.assetLocationID}-${_asset.assetLocationName.toString()}"
            }

            if (_asset.assetStateName != null) {
                assetStatus = "وضعیت:${_asset.assetStateName.toString()}"
            }

            if (_asset.assetStatusName != null) {
                assetPhysicalStatus = "وضعیت فیزیکی:${_asset.assetStateName.toString()}"
            }

            itemView.findViewById<TextView>(R.id.tvName).text =
                "${englishToPersian(name)}"
            itemView.findViewById<TextView>(R.id.tvBarcode).text =
                "${englishToPersian(barcode)}"
            //itemView.findViewById<TextView>(R.id.tvAssetCode).text =
                "${englishToPersian(assetCode)}"
            itemView.findViewById<TextView>(R.id.tvAssetTag).text =
                "${englishToPersian(assetTag)}"
            //itemView.findViewById<TextView>(R.id.tvAsset).text =
                "${englishToPersian(asset)}"
            //itemView.findViewById<TextView>(R.id.tvAssetMainGroup).text =
                "${englishToPersian(assetMainGroup)}"
            itemView.findViewById<TextView>(R.id.tvCostCenter).text =
                "${englishToPersian(costCenter)}"
            itemView.findViewById<TextView>(R.id.tvSubCostCenter).text =
                "${englishToPersian(subCostCenter)}"
            //itemView.findViewById<TextView>(R.id.tvAssetLocation).text =
                "${englishToPersian(assetLocation)}"
            itemView.findViewById<TextView>(R.id.tvAssetStatus).text =
                "${englishToPersian(assetStatus)}"
            //itemView.findViewById<TextView>(R.id.tvAssetPhysicalStatus).text =
                "${englishToPersian(assetPhysicalStatus)}"
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
        holder.bind(assetList[position])
        holder.itemView.findViewById<TextView>(R.id.tvRowNumber).text =
            englishToPersian((position).toString())
        holder.itemView.findViewById<AppCompatButton>(R.id.btnAssetEdit).setOnClickListener {
            itemClickListener.itemClicked(assetList[position])

            Log.i("AssetListAdapter1positi",position.toString())
        }
    }

    override fun getItemCount(): Int {
        return assetList.size
    }

    fun addData(_assetList: List<AssetListItem>) {
        assetList = _assetList as MutableList<AssetListItem>
        notifyDataSetChanged()
    }

    fun addData(asset: AssetListItem) {
        assetList.add(asset)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        assetList.clear()
        notifyDataSetChanged()
    }
}