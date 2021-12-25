package com.example.trabajomaster.ui.home

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttkotlinsample.MQTTClient
import com.example.trabajomaster.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class UserAdapter(val c: HomeFragment, val userList:ArrayList<UserData>):RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{

    // Describes an item view and its place within the RecyclerView
    inner class UserViewHolder(val v:View):RecyclerView.ViewHolder(v){
        var name:TextView
        var mbNum:TextView
        var mMenus: ImageView
        private lateinit var mqttClient : MQTTClient

        init {
            name = v.findViewById(R.id.mTitle)
            mbNum = v.findViewById(R.id.mSubTitle)
            mMenus = v.findViewById(R.id.mMenus)
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(v:View) {
            val position = userList[adapterPosition]
            val popupMenus = PopupMenu(c.activity,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val bottomSheetDialog = c.activity?.let { BottomSheetDialog(it, R.style.BottomSheetDialogTheme) }

                        val inflter = LayoutInflater.from(c.activity)
                        val v = inflter.inflate(R.layout.bottom_sheet,null)
                        /**set view*/
                        val name = v.findViewById<EditText>(R.id.username)
                        val number = v.findViewById<EditText>(R.id.userno)
                        val publishButton = v.findViewById<Button>(R.id.publish)

                        publishButton.setOnClickListener{
                            position.userName = name.text.toString()
                            position.userMb = number.text.toString()
                            notifyDataSetChanged()
                            Toast.makeText(c.activity,"Adding User Information Success",Toast.LENGTH_SHORT).show()
                            bottomSheetDialog?.dismiss()
                        }

                        bottomSheetDialog?.setContentView(v)
                        bottomSheetDialog?.show()

                        true
                    }
                    R.id.delete->{
                        /**set delete*/
                        AlertDialog.Builder(c.activity)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure delete this Information")
                            .setPositiveButton("Yes"){
                                    dialog,_->
                                userList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                Toast.makeText(c.activity,"Deleted this Information",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item,parent,false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = userList[position]
        holder.name.text = newList.userName
        holder.mbNum.text = newList.userMb
    }

    override fun getItemCount(): Int {
        return  userList.size
    }
}