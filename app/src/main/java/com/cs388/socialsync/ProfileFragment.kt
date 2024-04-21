package com.cs388.socialsync

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.facebook.drawee.view.SimpleDraweeView
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    lateinit var tvDisplayName: TextView
    lateinit var ivProfile: SimpleDraweeView
    lateinit var etDisplayName: EditText
    lateinit var btnChangeDisplayName: Button
    lateinit var logout: Button
    lateinit var change_display_photo: Button
    lateinit var switchNotifications: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvDisplayName = view.findViewById(R.id.tvDisplayName)
        ivProfile = view.findViewById(R.id.ivProfile)

        etDisplayName = view.findViewById(R.id.etDisplayName)
        btnChangeDisplayName = view.findViewById(R.id.btnChangeDisplayName)
        change_display_photo = view.findViewById(R.id.change_display_photo)
        logout = view.findViewById(R.id.logout)
        switchNotifications = view.findViewById(R.id.switch1)

        tvDisplayName.text = Obj.user.displayName
        ivProfile.setImageURI(Obj.user.image)
        getUserData()


        val displayName: TextView = view.findViewById(R.id.displayName)



        logout.setOnClickListener{
            // logging out the user :)
            Obj.auth.signOut()

            val loginIntent = Intent(context, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

//        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
//            Obj.user.notificationsEnabled = isChecked
//            Obj.uploadUserData(Obj.user)
//        }



        btnChangeDisplayName.setOnClickListener {
            val newName = etDisplayName.text.toString()
            if (newName.isEmpty()) {
                etDisplayName.setError("Please enter name here.")
            } else {
                Obj.user.displayName = newName
                Obj.uploadUserData(Obj.user).let {
                    getUserData()
                }
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    ivProfile.setImageURI(uri)
                    Glide.with(this)
                        .asBitmap()
                        .load(uri)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                val baos = ByteArrayOutputStream()
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                val bytes = baos.toByteArray()
                                val encodedString: String =
                                    Base64.encodeToString(bytes, Base64.DEFAULT)
                                // for base 64 encoding, we need to add the data:image/png for some reason
                                Obj.user.image = "data:image/png;base64," + encodedString
                                Obj.uploadUserData(Obj.user)
                                getUserData()

                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }

                        })

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        change_display_photo.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        return view
    }


    fun getUserData() {
        Obj.getUserData(object : Obj.UserDataListener {
            override fun onUserDataLoad(user: Obj.User) {
                Obj.user = user
                tvDisplayName.text = user.displayName
                ivProfile.setImageURI(user.image)
            }
        })
    }


    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}