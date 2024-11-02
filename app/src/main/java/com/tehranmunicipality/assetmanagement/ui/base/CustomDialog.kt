package com.tehranmunicipality.assetmanagement.ui.base

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.ui.asset_information.ArticlePatternItemClickListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.CostCenterItemClickListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.GoodListItemClickListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.ObjectType

class CustomDialog(
    context: Context,
    private val title: String,
    private val dataList: List<Any>,objectType: ObjectType,
    private val itemClickListener: ItemClickListener
) {

    init {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_searchable_list)
        dialog.setCancelable(false)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val etSearchText = dialog.findViewById<EditText>(R.id.etSearchText)
        val rvInformation = dialog.findViewById<RecyclerView>(R.id.rvInformation)
        val btnClose = dialog.findViewById<AppCompatButton>(R.id.acbClose)
        val ivClearSearch = dialog.findViewById<ImageView>(R.id.ivClearSearch)

        tvTitle.setText(title)

        when(objectType){

            ObjectType.ARTICLE_PATTERN_LIST -> {

                var customAdapter : ArticlePatternListAdapter = ArticlePatternListAdapter(object :
                    ArticlePatternItemClickListener {
                    override fun itemClicked(_articlePatternListItem: ArticlePatternListItem) {
                        itemClickListener.articlePatternItemClicked(_articlePatternListItem)
                        dialog.dismiss()
                    }

                })

                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<ArticlePatternListItem>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        customAdapter.search(s.toString())
                        Log.i("DEBUG","searchText=$s")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }

            ObjectType.GOOD_LIST -> {

                var customAdapter : GoodListAdapter = GoodListAdapter(object :
                    GoodListItemClickListener {
                    override fun itemClicked(_goodListItem: GoodList) {
                        itemClickListener.goodListItemClicked(_goodListItem)
                        dialog.dismiss()
                    }
                })

                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<GoodList>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        customAdapter.search(s.toString())
                        Log.i("DEBUG","searchText=$s")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })

            }

            //

            ObjectType.LOCATION_LIST -> {
                var customAdapter : LocationListAdapter = LocationListAdapter(object : ItemClickListener {
                    override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun goodListItemClicked(goodListItem: GoodList) {
                        TODO("Not yet implemented")
                    }

                    override fun locationItemClicked(_locationListItem: LocationListItem) {
                        itemClickListener.locationItemClicked(_locationListItem)
                        dialog.dismiss()
                    }
                    override fun costCenterItemClicked(_costCenterListItem: CostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun subCostCenterItemClicked(_subCostCenterListItem: SubCostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun personListItemClicked(_personListItem: PersonListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun assetStatusItemClicked(_assetStatusListItem: AssetStatusListItem) {
                        TODO("Not yet implemented")
                    }
                })
                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<LocationListItem>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        customAdapter.search(s.toString())
                        Log.i("DEBUG","searchText=$s")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }

            ObjectType.ASSET_STATUS -> {
                var customAdapter : AssetStatusListAdapter = AssetStatusListAdapter(object : ItemClickListener {
                    override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun goodListItemClicked(goodListItem: GoodList) {
                        TODO("Not yet implemented")
                    }

                    override fun locationItemClicked(_locationListItem: LocationListItem) {
                        TODO("Not yet implemented")
                    }
                    override fun costCenterItemClicked(_costCenterListItem: CostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun subCostCenterItemClicked(_subCostCenterListItem: SubCostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun personListItemClicked(_personListItem: PersonListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun assetStatusItemClicked(_assetStatusListItem: AssetStatusListItem) {
                        itemClickListener.assetStatusItemClicked(_assetStatusListItem)
                        dialog.dismiss()
                    }
                })
                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<AssetStatusListItem>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        customAdapter.search(s.toString())
                        Log.i("DEBUG","searchText=$s")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }

            ObjectType.COST_CENTER_LIST -> {
                var customAdapter = CostCenterListAdapter(object : CostCenterItemClickListener {
                    override fun itemClicked(costCenterListItem: CostCenterListItem) {
                        itemClickListener.costCenterItemClicked(costCenterListItem)
                        dialog.dismiss()
                    }
                })

                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<CostCenterListItem>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        customAdapter.search(s.toString())
                        Log.i("DEBUG","searchText=$s")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }

            ObjectType.SUB_COST_CENTER_LIST -> {
                var customAdapter = SubCostCenterListAdapter(object : ItemClickListener {
                    override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun goodListItemClicked(goodListItem: GoodList) {
                        TODO("Not yet implemented")
                    }

                    override fun locationItemClicked(locationListItem: LocationListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun costCenterItemClicked(costCenterListItem: CostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun subCostCenterItemClicked(_subCostCenterListItem: SubCostCenterListItem) {
                        itemClickListener.subCostCenterItemClicked(_subCostCenterListItem)
                        dialog.dismiss()
                    }

                    override fun personListItemClicked(personListItem: PersonListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun assetStatusItemClicked(assetStatusListItem: AssetStatusListItem) {
                        TODO("Not yet implemented")
                    }

                })

                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<SubCostCenterListItem>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        customAdapter.search(s.toString())
                        Log.i("DEBUG","searchText=$s")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }

            ObjectType.PERSON_LIST -> {
                var customAdapter = PersonListAdapter(object : ItemClickListener {
                    override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun goodListItemClicked(goodListItem: GoodList) {
                        TODO("Not yet implemented")
                    }

                    override fun locationItemClicked(locationListItem: LocationListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun costCenterItemClicked(costCenterListItem: CostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun subCostCenterItemClicked(_subCostCenterListItem: SubCostCenterListItem) {
                        TODO("Not yet implemented")
                    }

                    override fun personListItemClicked(_personListItem: PersonListItem) {
                        itemClickListener.personListItemClicked(_personListItem)
                        dialog.dismiss()
                    }

                    override fun assetStatusItemClicked(assetStatusListItem: AssetStatusListItem) {
                        TODO("Not yet implemented")
                    }

                })

                Log.i("DEBUG","CustomDialog dataList=$dataList")
                customAdapter.addData(dataList as List<PersonListItem>)
                rvInformation.adapter = customAdapter

                // Add a TextWatcher to the search EditText
                etSearchText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        var searchText = s.toString()
                        if (searchText.endsWith("ی")){
                            searchText = searchText.dropLast(1)
                            searchText += "ي"
                        }
                        customAdapter.search(searchText)
                        Log.i("DEBUG","searchText=$searchText")
                        if (s?.isEmpty() == true){
                            ivClearSearch.visibility = View.GONE
                        }else{
                            ivClearSearch.visibility = View.VISIBLE
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }

        }

        rvInformation.layoutManager = LinearLayoutManager(context)

        ivClearSearch.setOnClickListener {
            etSearchText.setText("")
        }

        btnClose.setOnClickListener {
            Log.i("DEBUG","CustomDialog btnClose clicked")
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }
}
