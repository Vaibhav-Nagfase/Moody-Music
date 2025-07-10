package com.example.moodymusic

import androidx.annotation.DrawableRes

sealed class Screen(val route:String) {

    object SongScreen:Screen("songscreen")
    object PlaylistSongScreen:Screen("playlistsongscreen")
    object LoginScreen:Screen("loginscreen")
    object SignUpScreen:Screen("signupscreen")
    object MainView:Screen("mainView")


    sealed class BottomScreen(val bRoute:String, @DrawableRes val icon:Int)
        : Screen(bRoute) {

        object MoodScreen: BottomScreen(
            "Moods",
            R.drawable.ic_tab_moods
        )

        object PlaylistScreen: BottomScreen(
            "Playlists",
            R.drawable.ic_tab_playlists
        )

        object AI: BottomScreen(
            "AI Mood",
            R.drawable.ic_tab_ai
        )

        object DiaryScreen: BottomScreen(
            "Diary",
            R.drawable.ic_tab_diary
        )

    }

    sealed class DrawerScreen(val dRoute:String, @DrawableRes val icon:Int)
        : Screen(dRoute){
        object Account : DrawerScreen(
            "Account",
            R.drawable.baseline_account_circle_24
        )

        object Subscription : DrawerScreen(
            "Subscription",
            R.drawable.ic_subscribe
        )

        object Export : DrawerScreen(
            "Export Diary To PDF",
            R.drawable.export_icon_removebg_preview
        )

        object Logout : DrawerScreen(
            "Logout",
            R.drawable.baseline_logout_24
        )

    }

}

val screenInBottom = listOf(
    Screen.BottomScreen.PlaylistScreen,
    Screen.BottomScreen.MoodScreen,
    Screen.BottomScreen.AI,
    Screen.BottomScreen.DiaryScreen

)

val screenInDrawer = listOf(
    Screen.DrawerScreen.Account,
    Screen.DrawerScreen.Subscription,
    Screen.DrawerScreen.Export,
    Screen.DrawerScreen.Logout

)