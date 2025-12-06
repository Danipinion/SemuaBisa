package com.netown.semuabisa.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class DriverFavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DriverFavAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvDriverFavorite)

        val sampleData = listOf(
            DriverFavorite("Danipinion", 4.7, 1000, R.drawable.avatar_profile, true),
            DriverFavorite("Danipinion", 4.7, 1000, R.drawable.avatar_profile, true),
            DriverFavorite("Danipinion", 4.7, 1000, R.drawable.avatar_profile, true),
            DriverFavorite("Danipinion", 4.7, 1000, R.drawable.avatar_profile, true),
            DriverFavorite("Danipinion", 4.7, 1000, R.drawable.avatar_profile, true),
        )

        adapter = DriverFavAdapter(sampleData)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}

//data class DriverFavorite(
//    val name: String,
//    val rating: Double,
//    val reviews: Int,
//    val image: Int,
//    val isFavorite: Boolean
//)
