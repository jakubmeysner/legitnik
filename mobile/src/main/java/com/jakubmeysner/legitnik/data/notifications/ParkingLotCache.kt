package com.jakubmeysner.legitnik.data.notifications


data class ParkingLotCache(
    override val id: String,
    override val symbol: String,
    override val freePlaces: Int,
    override val previousFreePlaces: Int,
) : ParkingLotCacheInterface
