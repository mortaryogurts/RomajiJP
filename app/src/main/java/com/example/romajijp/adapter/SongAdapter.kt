package com.example.romajijp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.romajijp.databinding.SongItemBinding
import com.example.romajijp.model.Song

class SongAdapter(private var songs : List<Song> = emptyList()): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(val binding : SongItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongViewHolder {
        val binding = SongItemBinding.inflate(
            LayoutInflater.from(p0.context),
            p0,
            false
        )

        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(p0: SongViewHolder, p1: Int) {
        val song = songs[p1]
        p0.binding.song = song
        p0.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun updateSongs(newSongs : List<Song>){
        songs = newSongs
        notifyDataSetChanged()
    }
}