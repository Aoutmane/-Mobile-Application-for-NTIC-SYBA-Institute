package com.el_aouthmanie.nticapp

import ForgotPasswordScreen
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.el_aouthmanie.nticapp.globals.CONSTANTS
import com.el_aouthmanie.nticapp.modules.OnlineDataBase
import com.el_aouthmanie.nticapp.ui.screens.anouncmentsScreen.AnnouncementsScreen
import com.el_aouthmanie.nticapp.ui.screens.detailScreen.ISTANTICPage
import com.el_aouthmanie.nticapp.ui.screens.homeScreen.HomeScreen
import com.el_aouthmanie.nticapp.ui.screens.launchingScreen.LaunchingScreen
import com.el_aouthmanie.nticapp.ui.screens.loginScreen.LoginScreen
import com.el_aouthmanie.nticapp.ui.screens.scheduleScreen.ScheduleScreen
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppEntry(
    context : Context,
    navController : NavHostController,
    dataBase : OnlineDataBase
) {
    var defaultDestination = CONSTANTS.Screens.LAUNCHING
    val scp = rememberCoroutineScope()


    if (dataBase.isUserLoggedIn(context)){
        defaultDestination = CONSTANTS.Screens.HOME
    }

    NavHost(
            navController = navController,
            startDestination = defaultDestination
        )
        {

            composable(CONSTANTS.Screens.LAUNCHING){
                LaunchingScreen(
                    CONSTANTS.launch.values,
                    onSkip = {
                        navController.navigate(CONSTANTS.Screens.LOGING)
                    }
                ){
                    navController.navigate(CONSTANTS.Screens.LOGING)
                }
            }
            composable(CONSTANTS.Screens.LOGING){
                LoginScreen(onGuestRequest = {
                    navController.navigate(CONSTANTS.Screens.ABOUT)
                }){ login , password ->

                    scp.launch {
                        val usr = OnlineDataBase.loginUser(login,password)

                        if (usr != null){

                            OnlineDataBase.saveLoginState(context,usr)
                            OnlineDataBase.setLogin(context,login)

                            FirebaseMessaging.getInstance().subscribeToTopic(usr.group)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast
                                            .makeText(
                                                context,
                                                "subscribed to ${usr.group}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                "failed to subscribe to ${usr.group}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    }
                                }
                            navController.navigate(CONSTANTS.Screens.HOME){
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            //todo , show a alert dialogue
                            Toast.makeText(
                                context,
                                "failed to log in",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }
            }
            composable(CONSTANTS.Screens.HOME){
                //todo : pass the nav to the home screen
                HomeScreen(
                    navController
                )
            }

            composable(CONSTANTS.Screens.SCHEDULE){
                ScheduleScreen()
            }
            composable(CONSTANTS.Screens.ABOUT){
                ISTANTICPage()
            }
            composable(CONSTANTS.Screens.ANNOUNCMENTS){
                AnnouncementsScreen(navController)
            }
            composable("settings"){
                ForgotPasswordScreen(navController,
                    {navController.popBackStack()}
                ){ old , new ->
                    scp.launch {
                        val rs = OnlineDataBase.updatePassword(OnlineDataBase.getLogin(ctx = context),old,new)
                        Toast.makeText(context,rs,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


}