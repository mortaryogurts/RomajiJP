package com.example.romajijp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.romajijp.activity.LyricsDisplay
import com.example.romajijp.databinding.SongItemBinding
import com.example.romajijp.model.Song

class SongAdapter(private var songs : List<Song> = emptyList()): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(val binding : SongItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

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

        //click Listner
        p0.binding.root.setOnClickListener {
            val context = it.context
            val intent = Intent(context, LyricsDisplay::class.java).apply {
                putExtra("song_title", song.title)
                putExtra("song_artist", song.artist)
                putExtra("song_album", song.album)
                putExtra("song_lyrics", song.lyrics)
            }
            context.startActivity(intent)
        }
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