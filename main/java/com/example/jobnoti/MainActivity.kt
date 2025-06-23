package com.example.jobnoti

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobnoti.ui.theme.JobNotiTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = Firebase.database
        val ref = db.getReference("FLINK")
        val myFirebaseService = FirebaseListenerService()

        val intent = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)

        setContent {
            JobNotiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        name = "FLINK",
                        ref = ref,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    @Composable
    fun HomeScreen(name: String, modifier: Modifier = Modifier, ref: DatabaseReference) {
        val context = LocalContext.current
        val myIntent = remember {
            Intent(context, FirebaseListenerService::class.java)
        }
        Box(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Transparent)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        context.startForegroundService(myIntent)
                    },
                    shape = RoundedCornerShape(16.dp), // Set corner radius here
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFff7400), // Background color
                        contentColor = Color.White         // Text color
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Start Service",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        context.stopService(myIntent)
                    },
                    shape = RoundedCornerShape(16.dp), // Set corner radius here
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCD0090FF), // Background color
                        contentColor = Color.White         // Text color
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Stop Service",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
