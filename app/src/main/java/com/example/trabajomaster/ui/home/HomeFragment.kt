package com.example.trabajomaster.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttkotlinsample.*
import com.example.trabajomaster.R
import com.example.trabajomaster.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import org.eclipse.paho.client.mqttv3.*

class HomeFragment : Fragment() {


    private lateinit var addsBtn: ExtendedFloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<UserData>
    private lateinit var userAdapter: UserAdapter

    private lateinit var mqttClient : MQTTClient

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        /**set List*/
        userList = ArrayList()
        /**set find Id*/
        addsBtn = root.findViewById(R.id.addRoom)
        recv = root.findViewById(R.id.recycler)
        /**set Adapter*/
        userAdapter = UserAdapter(this, userList)
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(context)
        recv.adapter = userAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addInfo() }

        return root
    }


    private fun addInfo() {


        val bottomSheetDialog = context?.let { BottomSheetDialog(it, R.style.BottomSheetDialogTheme) }

        val inflter = LayoutInflater.from(context)
        val v = inflter.inflate(R.layout.bottom_sheet,null)
        /**set view*/
        val userName = v.findViewById<EditText>(R.id.username)
        val userNo = v.findViewById<EditText>(R.id.userno)
        val subscribeButton = v.findViewById<Button>(R.id.subscribe)
        val publishButton = v.findViewById<Button>(R.id.publish)

        val serverURI   = "tcp://broker.hivemq.com:1883"
        val clientId    = ""
        val username    = ""
        val pwd    = ""

        mqttClient = MQTTClient(context, serverURI, clientId)


        // Connect and login to MQTT Broker
        mqttClient.connect(username,
            pwd,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Connection success")

                    Toast.makeText(context, "MQTT Connection success", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")

                    Toast.makeText(context, "MQTT Connection fails: ${exception.toString()}", Toast.LENGTH_SHORT).show()

                    // Come back to Connect Fragment
                    //findNavController().navigate(R.id.action_ClientFragment_to_ConnectFragment)
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d(this.javaClass.name, msg)

                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, "Delivery complete")
                }
            })

        subscribeButton.setOnClickListener {
            val topic = userName.text.toString()

            if (mqttClient.isConnected()) {
                mqttClient.subscribe(topic,
                    1,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg = "Subscribed to: $topic"
                            Log.d(this.javaClass.name, msg)

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Log.d(this.javaClass.name, "Failed to subscribe: $topic")
                        }
                    })
            } else {
                Toast.makeText(
                    context,
                    "Impossible to subscribe, no server connected",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(this.javaClass.name, "Impossible to subscribe, no server connected")
            }
        }

        publishButton.setOnClickListener {
            val topic = userName.text.toString()
            val number = userNo.text.toString()

            if (mqttClient.isConnected()) {
                mqttClient.publish(topic,
                    number,
                    1,
                    false,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg ="Publish message: $number to topic: $topic"
                            Log.d(this.javaClass.name, msg)

                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(this.javaClass.name, "Failed to publish message to topic")
                        }
                    })
            } else {
                Log.d(this.javaClass.name, "Impossible to publish, no server connected")
            }

            userList.add(UserData(topic, number))
            userAdapter.notifyDataSetChanged()
            //Toast.makeText(context,"Adding User Information Success",Toast.LENGTH_SHORT).show()
            bottomSheetDialog?.dismiss()
        }


        bottomSheetDialog?.setContentView(v)
        bottomSheetDialog?.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}