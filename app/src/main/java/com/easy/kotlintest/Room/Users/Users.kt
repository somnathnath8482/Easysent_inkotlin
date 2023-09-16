package com.easy.kotlintest.Room.Users

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(indices = [Index(value = ["email"], unique = true)])
class Users {
    @PrimaryKey(autoGenerate = true)
    var rid = 0

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("profile_pic")
    var profilePic: String? = null

    @ColumnInfo(name = "user_id")
    var id: String? = null

    @SerializedName("fstatus")
    var fstatus: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("about")
    var about: String? = null

    @SerializedName("is_verifyed")
    var Verifyed: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("ip")
    var ip: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("cash_pic")
    var cash_pic: String? = null

    constructor() {}
    constructor(
        phone: String?,
        name: String?,
        profilePic: String?,
        id: String?,
        fstatus: String?,
        gender: String?,
        about: String?,
        isVerifyed: String?,
        email: String?,
        token: String?
    ) {
        this.phone = phone
        this.name = name
        this.profilePic = profilePic
        this.id = id
        this.fstatus = fstatus
        this.gender = gender
        this.about = about
        this.Verifyed = isVerifyed
        this.email = email
        this.token = token
    }
}
