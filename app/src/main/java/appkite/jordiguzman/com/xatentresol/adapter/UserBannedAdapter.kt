package appkite.jordiguzman.com.xatentresol.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.glide.GlideApp
import appkite.jordiguzman.com.xatentresol.model.User
import appkite.jordiguzman.com.xatentresol.util.StorageUtil
import appkite.jordiguzman.com.xatentresol.util.XatUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.custom_dialog_banned_users.view.*

class UserBannedAdapter(private val userBanned: ArrayList<User>, val context: Context) :
        RecyclerView.Adapter<UserBannedAdapter.AdapterUserBannedViewHolder>() {

    private var cardViewClicked = false
    private var reportEmailBody = ""
    private var comment = ""
    private var emailUserBanned = ""
    private val messageToBannedUser = context.getString(R.string.message_to_banned_user)
    private var isBannedUser = false


    companion object {
        var userBannedEmail = ""


    }


    override fun onBindViewHolder(holder: AdapterUserBannedViewHolder, position: Int) {
        holder.tvBannedUsers.text = userBanned[position].name

        if (userBanned[position].profilePicturePath != null) {
            GlideApp.with(context)
                    .load(StorageUtil.pathToReference(userBanned[position].profilePicturePath!!))
                    .into(holder.photoBannedUsers)
        } else {
            holder.photoBannedUsers.background = ContextCompat.getDrawable(context, R.drawable.ic_person)
        }



        holder.cardViewBannedUser.setOnClickListener {
            if (cardViewClicked) {
                holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
                cardViewClicked = false
                return@setOnClickListener
            }

            alertDialog(holder, position, context)
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary_text))

            //Pruebas
            //setDataUserBanned(position)

            // Leer la database que se ha creado arriba.
            /**
             * AL PEGISTRARSE O INICIAR SESION COMPROBAR DATABASE CUENTAS SUSPENDIDAS
             *
             * EL USUARIO SUSPENDIDO TIENE QUE DESAPARECER DEL RECYCLERVIEW
             *
             * PRIMERO MIRAR LOS USUARIOS POR SI HAY ALGUNO BANEADO. EN TAL CASO GUARDARLO EN USERBANNED
             * SI NO HA SIDO BANEADO ENVIAR UN CORREO DE AVISO AL USUARIO Y OTRO AL ADMIN.
             * SI YA HA SIDO BANEADO ENVIAR UN CORREO AL USUARIO DICIENDOLE QUE SU CUENTA ESTA SUSPENDIDA Y OTRO AL ADMIN. GUARDAR A DATABASE DE CUENTAS SUSPENDIDAS.
             *
             * DOS OPCIONES:
             * BORRAR POR COMPLETO AL USUARIO DE LA DATABASE, DE AUTH Y DE STORAGE. PROBLEMAS: ¿QUE PASA CON LOS CHAT ABIERTOS?¿DARA ERROR?
             * NO BORRAR EL USUARIO DE LA DATABASE PERO SÍ DEL RECYCLERVIEW DE USUARIOS. ¿QUE PASA CON LOS CHAT ABIERTOS?¿DARA ERROR?
             * COMPROBAR AMBAS OPCIONES.
             */
            //setDataUserBanned(position)
            //updateBannedUser(position)
            cardViewClicked = true
        }
    }

    private fun setDataUserBanned(position: Int) {
        XatUtil.userBanned["email"] = userBanned[position].emailUser
        XatUtil.userBanned["name"] = userBanned[position].name
        XatUtil.userBanned["uid"] = userBanned[position].uidUser
    }

    private fun updateBannedUser(position: Int) {
        if (userBanned[position].isBanned) {
            Log.d("BannedUser", userBanned[position].isBanned.toString() )
            Log.d("BannedUser", userBanned[position].name )
            //sendMessageToAdminDeleteUserBanned()
            return
        }else{
            Log.d("BannedUser", userBanned[position].isBanned.toString() )
            //setEmailToUserBannedFirst(position)
        }




    }

    private fun setEmailToUserBannedFirst(position: Int) {
        reportEmailBody = userBanned[position].name
        emailUserBanned = userBanned[position].emailUser
        userBanned[position].isBanned = true
        isBannedUser = userBanned[position].isBanned
        XatUtil.sendMessageToUserBannedFirst(messageToBannedUser, emailUserBanned, context)
    }


    private fun sendMessageToAdminDeleteUserBanned(){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUserBannedViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_banned_users, parent, false)
        return AdapterUserBannedViewHolder(v)
    }

    override fun getItemCount() = userBanned.size

    @SuppressLint("InflateParams")
    private fun alertDialog(holder: AdapterUserBannedViewHolder, position: Int, context: Context) {
        val dialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_banned_users, null)
        val builder = AlertDialog.Builder(context)
                .setView(dialog)
                .setTitle(R.string.report_user_message)
        val alertDialog = builder.show()
        alertDialog.show()


        dialog.btn_report.setOnClickListener {
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
            cardViewClicked = false
            setupEmail(dialog, position, context)


            alertDialog.dismiss()
        }
        dialog.btn_close.setOnClickListener {
            holder.cardViewBannedUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons))
            cardViewClicked = false
            alertDialog.dismiss()

        }
    }

    private fun setupEmail(dialog: View, position: Int, context: Context) {
        comment = dialog.et_user_report.text.toString()
        XatUtil.getUserBanned()
        reportEmailBody = userBanned[position].name
        emailUserBanned = userBanned[position].emailUser
        XatUtil.sendMessageToUserBannedFirst(messageToBannedUser, emailUserBanned, context)

        /*sendMessageToUserBannedFirst()
        userBannedEmail = userBanned[position].emailUser
        val isUserBanned = userBanned[position].isBanned
        if (isUserBanned) {
            XatUtil.createBannedDatabase()
            XatUtil.deleteUserBanned()
        } else {
            XatUtil.getUserBanned()
        }*/
    }



    private fun sendMessageToUserBannedFinal(){

    }




    class AdapterUserBannedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val photoBannedUsers = view.findViewById(R.id.photo_banned_users) as CircleImageView
        val tvBannedUsers = view.findViewById(R.id.tv_banned_users) as TextView
        val cardViewBannedUser = view.findViewById(R.id.cardView_banned_user) as CardView
    }

}
