import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.AdbDevice
import org.koin.core.context.stopKoin
import ui.NavigationItem
import ui.page.InputField
import ui.page.PageScreen
import ui.style.ColorConstant
import ui.style.ColorConstant._E6A358
import ui.utils.CustomDialog

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = {
            stopKoin() // 앱 종료 시 Koin 정리
            exitApplication()
        },
        title = "KotlinProject")
    {
        val viewModel = remember {
            MainViewModel()
        }
        var showAdbAbsolutePathDialog by remember { mutableStateOf(false) }
        val sendLogMaxSize = 30
        val sendLogTexts = remember { mutableStateListOf<String>() }
        val coroutineScope = rememberCoroutineScope()
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    MainEvent.ShowPage -> {
                        if (navController.currentDestination?.route != NavDestination.Page.route) {
                            navController.navigate(
                                route = NavDestination.Page.route
                            )
                        }
                    }

                    is MainEvent.ShowAdbPathDialog -> {
                        showAdbAbsolutePathDialog = true
                    }
                }
            }
        }

        MaterialTheme {
            var selectedRailItem by remember { mutableIntStateOf(0) }

            Row(
                modifier = Modifier
                    .background(Color(0xFFF5F5F7))
                    .fillMaxSize()
            ) {
                NavigationRail(
                    containerColor = Color(0xFFF5F5F7),
                    contentColor = Color(0xFFF5F5F7),
                    modifier = Modifier.width(50.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    NavigationItem.entries.forEachIndexed { index, navItem ->
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    imageVector = navItem.iconRes,
                                    contentDescription = navItem.description,
                                    tint = Color(0xFF374957),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = null,
                            selected = false,
                            onClick = {
                                selectedRailItem = index
                                viewModel.onNavItemClicked(navItem = navItem)
                            },
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavDestination.Page.route
                    ) {
                        composable(route = NavDestination.Page.route) {
                            PageScreen(
                                onSendDeeplinkClicked = { url ->
                                    coroutineScope.launch {
                                        triggerUrl(
                                            absoluteAdbPath = viewModel.adbAbsolutePath,
                                            adbDevice = viewModel.selectedDevice,
                                            url = url,
                                            onError = { errorMsg ->
                                                sendLogTexts.add(errorMsg)
                                                if (sendLogTexts.size > sendLogMaxSize) {
                                                    sendLogTexts.removeFirstOrNull()
                                                }
                                            }
                                        )
                                    }
                                },
                                sendLogTexts = sendLogTexts.toImmutableList()
                            )
                        }

                        composable(route = NavDestination.History.route) {
                        }
                    }
                }
            }

            if (showAdbAbsolutePathDialog) {
                showADBAbsolutePathDialog(
                    path = viewModel.adbAbsolutePath,
                    devices = viewModel.devices.toImmutableList(),
                    onDeviceSelected = viewModel::onDeviceSelected,
                    onConfirmButtonClicked = viewModel::onAdbPathDialogConfirmButtonClicked,
                    onDismissed = {
                        showAdbAbsolutePathDialog = false
                    },
                )
            }
        }
    }
}

suspend fun triggerUrl(
    absoluteAdbPath: String,
    adbDevice: AdbDevice?,
    url: String,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        runCatching {
            val command = if (adbDevice == null) {
                "$absoluteAdbPath shell am start -a android.intent.action.VIEW -d \"$url\""
            } else {
                "$absoluteAdbPath -s ${adbDevice.id} shell am start -a android.intent.action.VIEW -d \"$url\""
            }
            val process = Runtime.getRuntime().exec(command)

            val errorStream = process.errorStream.bufferedReader().use { it.readText() }
            if (errorStream.isNotEmpty()) {
                throw Exception(errorStream)
            }
            
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw Exception("Command failed with exit code: $exitCode")
            }
        }.onFailure { throwable ->
            withContext(Dispatchers.Main) {
                onError(throwable.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}

@Composable
private fun showADBAbsolutePathDialog(
    path: String,
    devices: ImmutableList<AdbDevice>,
    onDeviceSelected: (AdbDevice) -> Unit,
    onConfirmButtonClicked: (String) -> Unit,
    onDismissed: () -> Unit,
) {
    var pathString by remember { mutableStateOf(path) }
    val focusRequester = remember { FocusRequester() }

    CustomDialog(
        title = {
            Text(
                text = "ADB 설정",
                fontWeight = FontWeight.Bold,
                color = ColorConstant._E6A358,
                fontSize = 18.sp
            )
        },
        content = {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ADB 절대 경로를 입력해주세요. 👀",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorConstant._848484
                )
                Spacer(modifier = Modifier.height(10.dp))
                InputField(
                    text = pathString,
                    onValueChanged = {
                        pathString = it
                    },
                    isSingleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                if (devices.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    Text(
                        text = "연결된 기기 목록이에요. 사용할 기기를 선택해주세요.🍤",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorConstant._848484
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        items(devices) { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = device.isSelected,
                                    onCheckedChange = { 
                                        onDeviceSelected(device)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = _E6A358,
                                        uncheckedColor = _E6A358
                                    )
                                )
                                Column(
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = device.id,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = device.description,
                                        fontSize = 11.sp,
                                        color = ColorConstant._848484
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        onDismissButtonClicked = onDismissed,
        onConfirmButtonClicked = {
            onConfirmButtonClicked(pathString)
        },
        onDismissed = onDismissed
    )

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

