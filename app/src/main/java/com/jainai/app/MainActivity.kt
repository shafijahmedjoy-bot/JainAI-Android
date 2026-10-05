package com.jainai.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import java.util.*
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var input: EditText
    private lateinit var chat: TextView
    private lateinit var status: TextView
    private lateinit var tts: TextToSpeech
    private val owner = "শাফিজ"
    private val endpoint = "http://127.0.0.1:11434/api/chat"
    private val model = "qwen2.5:1.5b"
    private val REQ_MIC = 900

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        input=findViewById(R.id.input); chat=findViewById(R.id.chat); status=findViewById(R.id.status)
        tts=TextToSpeech(this,this)
        append("Jain", "আসসালামু আলাইকুম, $owner ❤️ আমি জেইন।")
        findViewById<Button>(R.id.send).setOnClickListener { send() }
        findViewById<Button>(R.id.mic).setOnClickListener { startVoice() }
    }
    private fun append(role:String,text:String){ chat.append("$role: $text\n\n") }
    private fun send(){ val q=input.text.toString().trim(); if(q.isEmpty()) return; input.setText(""); append("You",q); status.text="Jain • thinking…"; Thread{ val a=localOrServer(q); runOnUiThread{ append("Jain",a); status.text="Jain • offline-first"; tts.speak(a,TextToSpeech.QUEUE_FLUSH,null,"jain") } }.start() }
    private fun localOrServer(q:String):String {
        if(q.contains("নাম")){ return "আমার নাম Jain (জেইন)।" }
        try {
            val c=URL(endpoint).openConnection() as HttpURLConnection; c.connectTimeout=1200; c.readTimeout=120000; c.requestMethod="POST"; c.doOutput=true; c.setRequestProperty("Content-Type","application/json")
            val messages="[{\"role\":\"system\",\"content\":\"You are Jain (জেইন), a private offline-first assistant for শাফিজ. Be warm, honest and practical.\"},{\"role\":\"user\",\"content\":\"${q.replace("\\","\\\\").replace("\"","\\\"")}\"}]"
            val body="{\"model\":\"$model\",\"messages\":$messages,\"stream\":false}"
            c.outputStream.use{it.write(body.toByteArray())}; val out=c.inputStream.bufferedReader().readText(); c.disconnect(); return JSONObject(out).optJSONObject("message")?.optString("content") ?: fallback(q)
        }catch(_:Exception){ return fallback(q) }
    }
    private fun fallback(q:String):String = "আমি এখন local offline mode-এ আছি। '$q' কাজটি করার জন্য device-এ compatible local model যুক্ত করলে আমি আরও শক্তিশালীভাবে কাজ করতে পারব।"
    private fun startVoice(){
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){ requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),REQ_MIC); return }
        val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD"); i.putExtra(RecognizerIntent.EXTRA_PROMPT,"জেইন শুনছে…"); startActivityForResult(i,REQ_MIC)
    }
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){ super.onActivityResult(requestCode,resultCode,data); if(requestCode==REQ_MIC && resultCode==RESULT_OK){ val r=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); if(!r.isNullOrEmpty()){ input.setText(r[0]); send() } } }
    override fun onInit(status:Int){ if(status==TextToSpeech.SUCCESS) tts.language=Locale("bn","BD") }
    override fun onDestroy(){ tts.shutdown(); super.onDestroy() }
}
