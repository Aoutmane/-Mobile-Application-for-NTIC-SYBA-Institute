package com.el_aouthmanie.nticapp.modules.intities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// Data class to represent an announcement
class Announcement : RealmObject{
    @PrimaryKey
    var id : ObjectId = ObjectId()

    var sender: String = "unknown"
    var message: String = "empty "
    var time: String = "not specified"
}