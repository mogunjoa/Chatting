package com.example.chattingproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chattingproject.databinding.ActivityStartBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = firebaseDatabase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)

        initAction()
        showChatList()
    }

    private fun initAction() {
        binding.userNext.setOnClickListener {
            if(binding.userEdit.text.isNullOrEmpty() || binding.userChat.text.isNullOrEmpty()){
                return@setOnClickListener
            }

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatName", binding.userChat.text.toString())
            intent.putExtra("userName", binding.userEdit.text.toString())
            startActivity(intent)
        }
    }

    private fun showChatList() {
        // 리스트 어댑터 생성 및 세팅
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1
        )
        binding.chatList.adapter = adapter

        binding.chatList.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatName", adapter.getItem(position))
            intent.putExtra("userName", binding.userEdit.text.toString())
            startActivity(intent)
        }


        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.key)
                adapter.add(dataSnapshot.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


}