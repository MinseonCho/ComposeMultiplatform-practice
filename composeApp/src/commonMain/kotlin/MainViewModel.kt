import androidx.compose.runtime.mutableStateMapOf
import base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import model.AdbDevice
import platform.AdbPathFinder
import ui.NavigationItem

class MainViewModel : BaseViewModel() {

    private val _eventChannel = Channel<MainEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<MainEvent> = _eventChannel.receiveAsFlow()

    private var _adbAbsolutePath: String = ""
    val adbAbsolutePath: String
        get() = _adbAbsolutePath

    private var _selectedDevice: AdbDevice? = null
    val selectedDevice: AdbDevice?
        get() = _selectedDevice
    private val _devices = mutableStateMapOf<String, AdbDevice>()
    val devices: List<AdbDevice>
        get() = _devices.values.toList()

    init {
        initAdbPath()
        initAdbDevices()
    }

    fun onNavItemClicked(navItem: NavigationItem) {
        when (navItem) {
            NavigationItem.SavedUrl -> {
                _eventChannel.trySend(MainEvent.NavToSavedUrl)
            }

            NavigationItem.Page -> {
                _eventChannel.trySend(MainEvent.ShowPage)
            }

            NavigationItem.Settings -> {
                _eventChannel.trySend(MainEvent.ShowAdbPathDialog)
            }
        }
    }

    fun onAdbPathDialogConfirmButtonClicked(adbAbsolutePath: String) {
        _adbAbsolutePath = adbAbsolutePath
    }

    private fun initAdbPath() {
        val adbPath = AdbPathFinder.findAdbPath()
        if (adbPath != null) {
            _adbAbsolutePath = adbPath
            println("Found ADB path: $_adbAbsolutePath")
        } else {
            println("Failed to find ADB path")
            viewModelScope.launch {
                _eventChannel.send(MainEvent.ShowAdbPathDialog)
            }
        }
    }

    fun onDeviceSelected(device: AdbDevice) {
        _selectedDevice?.let {
            _devices[it.id] = it.copy(isSelected = false)
        }
        _devices[device.id] = device.copy(isSelected = true)
        _selectedDevice = device
    }

    private fun initAdbDevices() {
        val devices = AdbPathFinder.getDevices(adbAbsolutePath).associateBy { it.id }
        _devices.clear()

        if (devices.isNotEmpty()) {
            _devices.putAll(
                AdbPathFinder.getDevices(adbAbsolutePath).associateBy { it.id }
            )
            val firstDevice = _devices.toList().first().second
                .copy(isSelected = true) // 첫 번째 아이템 기본적으로 선택되게 설정
            _devices[firstDevice.id] = firstDevice
            _selectedDevice = firstDevice
        }

        if (devices.size > 1) {
            viewModelScope.launch {
                _eventChannel.send(MainEvent.ShowAdbPathDialog)
            }
        }
    }
}
