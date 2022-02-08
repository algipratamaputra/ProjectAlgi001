package com.project.projectalgi001.admin.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.project.projectalgi001.admin.adapter.AddCarsAdapter
import com.project.projectalgi001.admin.model.CarsModel

class MyItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<CarsModel>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<CarsModel>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as AddCarsAdapter.AddCarsViewHolder)
                .getItemDetails()
        }

        return null
    }
}