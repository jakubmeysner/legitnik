package com.jakubmeysner.legitnik.data.notifications

interface ParkingLotCacheInterface {
    val id: String
    val symbol: String
    val freePlaces: Int
    val previousFreePlaces: Int
}
