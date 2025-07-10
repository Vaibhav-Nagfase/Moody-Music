package com.example.moodymusic.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moodymusic.Screen
import com.example.moodymusic.screenInDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusic.R
import com.example.moodymusic.SubscriptionDialog
import com.example.moodymusic.ViewModel.AuthViewModel
import com.example.moodymusic.ViewModel.MoodViewModel
import com.example.moodymusic.ViewModel.MusicBotViewModel
import com.example.moodymusic.ViewModel.PlaylistViewModel
import com.example.moodymusic.component.ConnectSpotifyDialog
import com.example.moodymusic.component.DiaryExportDialog
import com.example.moodymusic.model.MoodType
import com.example.moodymusic.screen.Diary.DiaryScreen
import com.example.moodymusic.screenInBottom
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainView(onSpotifyConnect: () -> Unit,
             authViewModel: AuthViewModel,
             onNavigateToLogin: () -> Unit){

    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val scope:CoroutineScope = rememberCoroutineScope()

    val firstName by authViewModel.firstName.observeAsState("")

    val moodViewModel: MoodViewModel = viewModel()
    val moods by moodViewModel.moods.collectAsState()


    LaunchedEffect(Unit) {
        authViewModel.loadFirstName()
        moodViewModel.fetchMoodLogs()
    }


    val controller:NavHostController = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val dialogOpen = remember { mutableStateOf(false) }
    val showExportDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    val token = prefs.getString("spotify_token", null)

    // 🌟 Show the Spotify connect dialog immediately
    val showSpotifyDialog = remember { mutableStateOf(token == null) }


    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {it != ModalBottomSheetValue.HalfExpanded}
    )

    val title = remember { mutableStateOf("") }

    val bottomBar : @Composable () -> Unit = {

        BottomNavigation(
            Modifier.wrapContentSize(),
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        ) {
            screenInBottom.forEach{
                    item ->
                BottomNavigationItem(selected = currentRoute == item.bRoute,
                    onClick = {controller.navigate(item.bRoute)
                        title.value = ""},
                    icon = {
                        Icon(painter = painterResource(id = item.icon), contentDescription = item.bRoute)
                    },
                    label = { Text(item.bRoute) },
                    unselectedContentColor = Color.White,
                    selectedContentColor = Color.Cyan)
            }
        }

    }


    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        sheetContent = {
            ModalBottomSheet()
        })
    {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF280050), Color(0xFF19004F))
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.mood_back2),
                contentDescription = "Background Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Box( Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title.value,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } },
                        elevation = 0.dp,
                        actions = {
                            IconButton(onClick = {
                                scope.launch{
                                    if (modalSheetState.isVisible) {
                                        modalSheetState.hide()
                                    } else {
                                        modalSheetState.show()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Menu",
                                    modifier = Modifier.size(36.dp),
                                    tint = Color.White
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                //Opens the drawer
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(36.dp),
                                    tint = Color.White
                                )
                            }
                        },

                        backgroundColor = Color.Transparent
                    )
                },
                scaffoldState = scaffoldState,
                drawerContent = {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(screenInDrawer) { item ->
                            DrawerItem(
                                item = item,
                                onDrawerItemClicked = {

                                    scope.launch {
                                        scaffoldState.drawerState.close()
                                        title.value = ""
                                    }

                                    if (item.dRoute == "Subscription") {
                                        dialogOpen.value = true
                                    }else if(item.dRoute == "Export Diary To PDF"){
                                        showExportDialog.value = true
                                    }else if(item.dRoute == "Logout"){
                                        authViewModel.logOut()
                                        onNavigateToLogin()
                                    }
                                    else { }

                                },
                                firstName = firstName
                            )
                        }
                    }
                },
                bottomBar = bottomBar,
                backgroundColor = Color.Transparent
            ) {
                Navigation(navController = controller, pd = it, title = title)

                ConnectSpotifyDialog(
                    showDialog = showSpotifyDialog.value,
                    onConnectClick = { onSpotifyConnect() },
                    onDismiss = { showSpotifyDialog.value = false })

                SubscriptionDialog(dialogOpen = dialogOpen)

                DiaryExportDialog(
                    showDialog = showExportDialog.value,
                    context = LocalContext.current,
                    logs = moods.sortedByDescending { LocalDate.parse(it.date) },
                    onDismiss = { showExportDialog.value = false },
                    scaffoldState = scaffoldState
                )

            }
        }
    }

}



@Composable
fun DrawerItem(
    item:Screen.DrawerScreen,
    onDrawerItemClicked:()->Unit,
    firstName: String = ""
){

    if(item.dRoute == "Account") {

        Row(
            modifier = Modifier.fillMaxSize().background(Color(0xFF030350))
                .size(120.dp)
                .padding(8.dp).clickable { onDrawerItemClicked() },

            verticalAlignment = Alignment.CenterVertically

        ) {

            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = "",
                modifier = Modifier.padding(end = 8.dp, top = 4.dp).size(40.dp),
                tint = Color(0xFFF4F8F8)
            )

            Text(
                text = "Hi $firstName",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 24.sp,
                color = Color.White
            )
        }

    }
    else if(item.dRoute == "Subscription"){

        Row(modifier = Modifier.fillMaxSize().background(Color(0xFF0E9A17))
            .border(2.dp, Color(0xFFDCB73E)).size(60.dp)
            .padding(8.dp).clickable { onDrawerItemClicked() },

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Row(

            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = "",
                    Modifier.padding(end = 8.dp, top = 4.dp),
                    tint = Color(0xFFE8BE39)
                )

                Text(
                    text = item.dRoute,
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Icon(
                painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                contentDescription = "",
                Modifier.padding(end = 8.dp, top = 4.dp),
                tint = Color(0xFFE8BE39)
            )
        }

        Spacer(Modifier.height(20.dp))

    }
    else{

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .clickable {
                    onDrawerItemClicked()
                }
        ){

            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = "",
                Modifier.padding(end = 8.dp, top = 4.dp).size(24.dp),
                tint = Color(0xFF0800FF)
            )

            Text(text = item.dRoute, style = MaterialTheme.typography.headlineSmall)

        }
    }
}


@Composable
fun ModalBottomSheet(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .background(
            colorResource(id = R.color.purple)
        )
    ){
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(R.drawable.baseline_settings_24),
                    contentDescription = "settings",
                    tint = Color.White
                )
                Text("Settings", fontSize = 20.sp, color = Color.White)
            }

            Row(modifier = Modifier
                .padding(16.dp)
                .clickable { /*TODO*/ }) {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(R.drawable.baseline_share_24),
                    contentDescription = "Share",
                    tint = Color.White
                )
                Text("Share", fontSize = 20.sp, color = Color.White)
            }

            Row(modifier = Modifier
                .padding(16.dp)
                .clickable { /*TODO*/ }) {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(R.drawable.baseline_help_center_24),
                    contentDescription = "Help",
                    tint = Color.White
                )
                Text("Help", fontSize = 20.sp, color = Color.White)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController, pd:PaddingValues, title: MutableState<String>){

    val context = LocalContext.current
    val accessToken = remember {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .getString("spotify_token", null) ?: ""
    }

    val playlistViewModel: PlaylistViewModel = viewModel()
    val moodViewModel: MoodViewModel = viewModel()

    val musicBotViewModel: MusicBotViewModel = viewModel()


    NavHost(navController = navController, startDestination = Screen.BottomScreen.MoodScreen.route,
        modifier = Modifier.padding(pd)) {


        composable(Screen.BottomScreen.MoodScreen.route){
            Mood_Screen(pd, onMoodSelected = {mood ->
                moodViewModel.saveMoodToFirebase(MoodType.valueOf(mood.name))
                navController.navigate("${Screen.SongScreen.route}/${mood.name}")
                title.value = "Songs"
            })
        }

        composable("${Screen.SongScreen.route}/{mood}"){
            backStackEntry ->
            val mood = backStackEntry.arguments?.getString("mood") ?: "HAPPY"
            Songs_Screen(mood = mood, accessToken = accessToken)
        }

        composable(Screen.BottomScreen.PlaylistScreen.route) {
            Playlist_Screen(navController = navController, playlistViewModel = playlistViewModel) // pass navController to trigger navigation
        }

        composable(Screen.PlaylistSongScreen.route) {
            PlaylistSongsScreen(playlistViewModel = playlistViewModel)
        }

        composable(Screen.BottomScreen.AI.route){

            val mood by musicBotViewModel.promptMood.collectAsState()
            val recommendation by musicBotViewModel.recommendation.collectAsState()

            LaunchedEffect(mood, recommendation) {
                if (mood.isNotBlank() && recommendation.isNotBlank()) {
                    moodViewModel.saveMoodToFirebase(MoodType.valueOf(mood))
                    navController.navigate("${Screen.SongScreen.route}/$recommendation")

                    // ✅ Clear ViewModel values to prevent re-triggering
                    musicBotViewModel.clearResponse()
                }
            }

            AI_Screen(
                padding = pd,
                onSearchClicked = { prompt ->
                    musicBotViewModel.sendPrompt(prompt)
                }
            )
        }

        composable(Screen.BottomScreen.DiaryScreen.route){
            DiaryScreen()
        }

    }

}