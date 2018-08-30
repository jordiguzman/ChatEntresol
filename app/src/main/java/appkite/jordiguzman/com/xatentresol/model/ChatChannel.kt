package appkite.jordiguzman.com.xatentresol.model



class ChatChannel(val userIds: MutableList<String>) {
    constructor(): this(mutableListOf())
}