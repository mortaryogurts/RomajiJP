package com.example.romajijp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.romajijp.databinding.RecentSearchesItemBinding

class SearchHistoryAdapter(
    private var history: List<String> = emptyList(),
    private val onQueryClick: (String) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: RecentSearchesItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = RecentSearchesItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val query = history[position]
        holder.binding.query = query
        
        holder.itemView.setOnClickListener {
            onQueryClick(query)
        }
        
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = history.size

    fun updateHistory(newHistory: List<String>) {
        this.history = newHistory
        notifyDataSetChanged()
    }
}