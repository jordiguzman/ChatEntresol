package appkite.jordiguzman.com.xatentresol.model


data class User(val name: String,
                val bio: String,
                val profilePicturePath: String?,
                val registrationTokens: MutableList<String>,
                val emailUser: String,
                var isBanned: Boolean,
                var uidUser: String) {
    constructor(): this("", "", null, mutableListOf(), "", false, "")


}

data class UserBanned(var name: String,
                      var email: String,
                      var uid: String){
    constructor(): this("", "", "")
}