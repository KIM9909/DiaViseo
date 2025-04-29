package com.example.diaviseo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.diaviseo.ui.splash.SplashScreen
import com.example.diaviseo.ui.signup.signupNavGraph
import com.example.diaviseo.ui.main.MainScreen

import com.example.diaviseo.ui.components.TransparentStatusBar
import androidx.navigation.compose.rememberNavController

import android.util.Log
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaviseo.datastore.TokenDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val testViewModel = TestViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 앱 켤 때마다 토큰 초기화 (테스트용)
//        val context = this.applicationContext
//        CoroutineScope(Dispatchers.IO).launch {
//            com.example.diaviseo.datastore.TokenDataStore.clearAccessToken(context)
//        }

        setContent {
            TransparentStatusBar(window) // setContent {} 안에서 호출
            // 회원가입 & 로그인 로직 구현 이후
            // 로그인, 회원가입된 사용자 -> MainScreen으로
            // 회원가입해야하는 신규 유저 -> SignupNavGraph로 이동하도록 수정 필요

            testViewModel.printAccessToken(this)

            val navController = rememberNavController()
            NavHost(navController, startDestination = "splash") {
                composable("splash") { SplashScreen(navController) }
                signupNavGraph(navController)
                composable("main") { MainScreen() }
            }
        }
    }
}

class TestViewModel : ViewModel() {

    fun printAccessToken(context: Context) {
        viewModelScope.launch {
            val token = TokenDataStore.getAccessToken(context).first() // 🔥 바로 첫 번째 데이터만 읽기
            Log.d("TestViewModel", "저장된 accessToken: $token")
        }
    }
}
