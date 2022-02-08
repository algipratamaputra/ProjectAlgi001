package com.project.projectalgi001.admin.selection

import androidx.recyclerview.selection.ItemKeyProvider
import com.project.projectalgi001.admin.adapter.AddCarsAdapter
import com.project.projectalgi001.admin.model.CarsModel

class MyItemKeyProvider(private val adapter: AddCarsAdapter) : ItemKeyProvider<CarsModel>(SCOPE_CACHED) {
    override fun getKey(position: Int): CarsModel {
        return adapter.getItem(position)
    }

    override fun getPosition(key: CarsModel): Int {
        return adapter.getPosition(key.image)
    }
}