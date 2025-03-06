package ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.SettingsApplications
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val iconRes: ImageVector,
    val description: String,
) {
    SavedUrl(Icons.Rounded.ListAlt, "SavedUrl"),
    Page(Icons.Outlined.AddBox, "Page"),
    Settings(Icons.Outlined.SettingsApplications, "Settings")
}
