package com.example.chattingproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.chattingproject.databinding.ActivityChatBinding
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding : ActivityChatBinding

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = firebaseDatabase.reference

    private var CHAT_NAME = ""
    private var USER_NAME = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        initData()

        openChat(CHAT_NAME)

        chatAction()
    }

    private fun initData() {
        CHAT_NAME = intent.getStringExtra("chatName") ?:""
        USER_NAME = intent.getStringExtra("userName") ?:""
    }

    private fun chatAction() {
        binding.chatSent.setOnClickListener {
            if(binding.chatEdit.text.isNullOrEmpty()){
                return@setOnClickListener
            }

            val chat = ChatDTO()
            chat.setUserName(USER_NAME)
            chat.setMessage(binding.chatEdit.text.toString())

            databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat)
            binding.chatEdit.setText("")

        }
    }

    private fun addMessage(
        dataSnapshot: DataSnapshot,
        adapter: ArrayAdapter<String>
    ) {
        val chatDTO: ChatDTO? = dataSnapshot.getValue(ChatDTO::class.java)
        adapter.add(chatDTO?.getUserName() + " : " + chatDTO?.getMessage())
    }

    private fun removeMessage(
        dataSnapshot: DataSnapshot,
        adapter: ArrayAdapter<String>
    ) {
        val chatDTO: ChatDTO? = dataSnapshot.getValue(ChatDTO::class.java)
        adapter.remove(chatDTO?.getUserName() + " : " + chatDTO?.getMessage())
    }

    private fun openChat(chatName: String) {
        // 리스트 어댑터 생성 및 세팅
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1
        )
        binding.chatView.adapter = adapter

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").child(chatName)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    addMessage(snapshot, adapter)

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    removeMessage(snapshot, adapter)
                }
            })
    }
}