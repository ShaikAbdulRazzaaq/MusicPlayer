package com.projects.musicplayer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.projects.musicplayer.databinding.MusicListItemBinding
import com.projects.musicplayer.ui.Songs

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolderClass>() {
    class ViewHolderClass(val musicListItemBinding: MusicListItemBinding) :
        RecyclerView.ViewHolder(musicListItemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Songs>() {
        override fun areItemsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem.songID == newItem.songID
        }

        override fun areContentsTheSame(oldItem: Songs, newItem: Songs): Boolean {
            return oldItem == newItem
        }
    }
     val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            MusicListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val songs = differ.currentList[position]
        holder.musicListItemBinding.apply {
            title.text = songs.songTitle
            artist.text = songs.artist
            tvDateAdded.text = songs.dateAdded.toString()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}