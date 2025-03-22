package ui

import NavDestination
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val unselectedIconRes: ImageVector,
    val selectedIconRes: ImageVector,
    val description: String,
    val destination: NavDestination? = null,
) {
    SavedUrl(
        unselectedIconRes = Icons.Outlined.StarBorder,
        selectedIconRes = Icons.Default.Star,
        description = "SavedUrl",
        destination = NavDestination.SavedUrl,
    ),
    Page(
        unselectedIconRes = Icons.Default.AddCircleOutline,
        selectedIconRes = Icons.Default.AddCircle,
        description = "Page",
        destination = NavDestination.Page,
    ),
    Settings(
        unselectedIconRes = Icons.Outlined.Settings,
        selectedIconRes = Icons.Default.Settings,
        description = "Settings",
        destination = null,
    )
}
