package appkite.jordiguzman.com.xatentresol.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object StorageUtil {

    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
                .child(FirebaseAuth.getInstance().currentUser?.uid
                        ?: throw NullPointerException("UID is null."))


    fun getIdOfBannedUser(): String {

        return currentUserRef.path

    }


    fun uploadProfilePhoto(imageBytes: ByteArray,
                           onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
                .addOnSuccessListener {

                    //TODO guardar ref.path para futuras acciones
                    onSuccess(ref.path)
                }

    }

    fun uploadMessageImage(imageBytes: ByteArray,
                           onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child("messages/${UUID.nameUUIDFromBytes(imageBytes)}")


        ref.putBytes(imageBytes)
                .addOnSuccessListener {
                    //TODO guardar ref.path para futuras acciones

                    onSuccess(ref.path)
                    Log.d("Path", ref.path )
                }
    }


    fun pathToReference(path: String) = storageInstance.getReference(path)


}


