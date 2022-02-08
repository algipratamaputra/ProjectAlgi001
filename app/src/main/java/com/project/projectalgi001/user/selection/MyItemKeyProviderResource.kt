package com.project.projectalgi001.user.selection

import androidx.recyclerview.selection.ItemKeyProvider
import com.project.projectalgi001.user.adapter.ResourceSpkAdapter
import com.project.projectalgi001.user.model.ResourceModel

class MyItemKeyProviderResource(private val adapterResource: ResourceSpkAdapter) : ItemKeyProvider<ResourceModel>(SCOPE_CACHED) {
    override fun getKey(position: Int): ResourceModel {
        return adapterResource.getItemResource(position)
    }

    override fun getPosition(key: ResourceModel): Int {
        return adapterResource.getPositionResource(key.imageResource)
    }
}