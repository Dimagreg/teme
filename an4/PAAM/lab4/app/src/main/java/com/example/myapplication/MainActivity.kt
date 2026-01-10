package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.ItemAdapter
import com.example.myapplication.data.Item
import com.example.myapplication.databinding.ActivityMainBinding
import kotlin.collections.mutableListOf;

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemAdapter
    private val itemList = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ItemAdapter(itemList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Add a new item when FAB is clicked
        binding.fabAdd.setOnClickListener {
            val newItem = Item(
                title = "New Item ${itemList.size + 1}",
                description = "This is a hardcoded item added to the list."
            )
            adapter.addItem(newItem)
        }
    }
}
