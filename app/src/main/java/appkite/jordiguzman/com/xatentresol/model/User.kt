package appkite.jordiguzman.com.xatentresol.model


data class User(val name: String,
                val bio: String,
                val profilePicturePath: String?,
                val registrationTokens: MutableList<String>,
                val emailUser: String,
                var isBanned: Boolean) {
    constructor(): this("", "", null, mutableListOf(), "", false)


}
