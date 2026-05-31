package com.example.romajijp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.romajijp.activity.LyricsDisplay
import com.example.romajijp.databinding.SongItemBinding
import com.example.romajijp.model.Song

class SongAdapter(
    private var songs: List<Song> = emptyList(),
    private val onSongClicked: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(val binding: SongItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = SongItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.song = song

        holder.binding.root.setOnClickListener {
            onSongClicked(song)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs : List<Song>){
        songs = newSongs
        notifyDataSetChanged()
    }
}