package appkite.jordiguzman.com.xatentresol.model


class ItemSettings {

    var mTitle: String? = null
    var mLogo: Int = 0


    fun getTitles(): String{
        return mTitle.toString()
    }
    fun setTitles(title: String){
        this.mTitle = title
    }
    fun getLogo(): Int{
        return mLogo
    }
    fun setLogo(logo: Int){
        this.mLogo = logo
    }

}