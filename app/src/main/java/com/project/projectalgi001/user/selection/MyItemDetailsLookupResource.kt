package com.project.projectalgi001.user.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.project.projectalgi001.user.adapter.ResourceSpkAdapter
import com.project.projectalgi001.user.model.ResourceModel

class MyItemDetailsLookupResource(private val recyclerViewResource: RecyclerView) : ItemDetailsLookup<ResourceModel>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<ResourceModel>? {
        val view = recyclerViewResource.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerViewResource.getChildViewHolder(view) as ResourceSpkAdapter.ResourceSpkViewHolder)
                .getItemResourceDetails()
        }

        return null
    }
}