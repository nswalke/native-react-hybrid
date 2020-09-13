package com.nswapps.react_native_hybrid.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nswapps.react_native_hybrid.R
import com.nswapps.react_native_hybrid.ui.hybrid.HybridActivity

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        root.findViewById<View>(R.id.btn_bike).setOnClickListener {
            startActivity(Intent(requireContext(), HybridActivity::class.java)
                .apply {
                    putExtra("flow", "purchase")
                    putExtra("product", "bike")
                })
        }
        root.findViewById<View>(R.id.btn_car).setOnClickListener {
            startActivity(Intent(requireContext(), HybridActivity::class.java)
                .apply {
                    putExtra("flow", "purchase")
                    putExtra("product", "car")
                })
        }
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
}
