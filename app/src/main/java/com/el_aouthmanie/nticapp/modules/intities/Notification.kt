package com.el_aouthmanie.nticapp.modules.intities

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class Notification : RealmObject{
    @PrimaryKey
    var id : ObjectId = ObjectId()

    var title : String = "No title"
    var sender: String = "unknown"
    var body: String = "empty "
    var createdAt: String = "not specified"

    var type : String = "N/A"
    var priority : String = "NORMAL"

    var logoName : String = "Notification"
    var expireDate : String = "PERMANENT"

    var isRead : Boolean = false
}