package com.khtn.freebies.helper

object FireStoreCollection {
    const val PLANT_TYPE = "plant_type"
    const val SPECIES = "species"
    const val USER = "user"
    const val ACCOUNT_SETTING = "user_account_setting"
}

object FireDatabase {
    const val PLANT = "plant"
}

object FireStoreDocumentField {
    const val DATE = "date"
    const val USER_ID = "user_id"
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "user_session"
    const val ACCOUNT_SETTING_SESSION = "account_setting_session"
    const val LOGIN_INFO = "login_info"
}

object FirebaseStorageConstants {
    const val ROOT_DIRECTORY = "app"
    const val NOTE_IMAGES = "note"
}

enum class HomeTabs(val index: Int, val key: String) {
    NOTES(0, "notes"),
    TASKS(1, "tasks"),
}