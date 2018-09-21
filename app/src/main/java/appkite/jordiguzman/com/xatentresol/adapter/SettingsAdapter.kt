package appkite.jordiguzman.com.xatentresol.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.model.ItemSettings


class SettingsAdapter(private val context: Context, private val settingsArrayList: ArrayList<ItemSettings> ): BaseAdapter(){



    override fun getCount(): Int {
        return settingsArrayList.size
    }

    override fun getItem(position: Int): Any {
        return settingsArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }


    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
         var mConvertView = convertView
        val holder: ViewHolder

        if (mConvertView == null){
            holder = ViewHolder()
            val inflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
            mConvertView = inflater.inflate(R.layout.item_settings, null, false)

            holder.iv_logo = mConvertView!!.findViewById(R.id.iv_item_settings) as ImageView
            holder.tv_title = mConvertView.findViewById(R.id.tv_item_settings) as TextView
            mConvertView.tag = holder
        }else{
            holder = mConvertView.tag as ViewHolder
        }
        holder.iv_logo!!.setImageResource(settingsArrayList[position].getLogo())
        holder.tv_title!!.text = settingsArrayList[position].getTitles()


        return mConvertView
    }



private inner class ViewHolder{
    internal var iv_logo: ImageView? = null
    var tv_title: TextView? = null
}




}