package com.tehranmunicipality.assetmanagement.ui.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.ArticlePatternListItem
import com.tehranmunicipality.assetmanagement.ui.asset_information.ArticlePatternItemClickListener
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import java.util.*

class ArticlePatternListAdapter(
    private val itemClickListener: ArticlePatternItemClickListener
) : RecyclerView.Adapter<ArticlePatternListAdapter.DataViewHolder>() {

    private var articlePatternList: MutableList<ArticlePatternListItem> = mutableListOf()
    private var filteredArticlePatternList: MutableList<ArticlePatternListItem> = mutableListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(_item: ArticlePatternListItem) {

            var item = "-"

            if (_item.articlePatternCode != null) {
                Log.i("DEBUG","articlePatternName=${_item.articlePatternCode}")
                item = "${_item.articlePatternCode}"
                itemView.findViewById<TextView>(R.id.tvItem).text =
                    "${englishToPersian(item)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_searchable_spinner, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(filteredArticlePatternList[position])
        holder.itemView.findViewById<TextView>(R.id.tvItem).setOnClickListener {
            itemClickListener.itemClicked(filteredArticlePatternList[position])
        }

    }

    override fun getItemCount(): Int {
        return filteredArticlePatternList.size
    }

    fun addData1(_articlePatternListItem: List<ArticlePatternListItem>) {
        articlePatternList = _articlePatternListItem as MutableList<ArticlePatternListItem>
        notifyDataSetChanged()
    }

    fun addData(_articlePatternListItem: ArticlePatternListItem) {
        articlePatternList.add(_articlePatternListItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        articlePatternList.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(_articlePatternList: List<ArticlePatternListItem?>) {
        articlePatternList = _articlePatternList as MutableList<ArticlePatternListItem>
        filteredArticlePatternList = _articlePatternList as MutableList<ArticlePatternListItem>
        Log.i("DEBUG","ArticlePatternListAdapter addData=$_articlePatternList")
        notifyDataSetChanged()
    }

    fun search(searchText: String){
        filteredArticlePatternList = articlePatternList
        if (searchText.isEmpty()){
        }else{
            val resultList = mutableListOf<ArticlePatternListItem>()
            for (row in filteredArticlePatternList){
                if (row.articlePatternName?.contains(searchText) == true){
                    resultList.add(row)
                }
            }
            filteredArticlePatternList = resultList
            notifyDataSetChanged()
        }
    }

}



