package ui.savedurllist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import ui.style.ColorConstant

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SavedUrlScreen(
    viewModel: SavedUrlViewModel = koinViewModel<SavedUrlViewModel>(),
    navToUrlPage: (UrlItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SavedUrlEvent.NavToUrlPage -> {
                    navToUrlPage(event.urlItem)
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 15.dp, end = 15.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item {
            Text(
                text = "저장된 URL 목록(최신순)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ColorConstant._E6A358,
                modifier = Modifier
                    .background(
                        color = ColorConstant._FEF7E1,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(uiState.urls) { item ->
            UrlListItem(
                urlItem = item,
                onClicked = viewModel::onUrlClicked,
                onDeleteButtonClicked = viewModel::onDeleteButtonClicked
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun UrlListItem(
    urlItem: UrlItem,
    onClicked: (UrlItem) -> Unit,
    onDeleteButtonClicked: (UrlItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMouseHovered by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerMoveFilter(
                onEnter = { isMouseHovered = true; false },
                onExit = { isMouseHovered = false; false }
            )
            .background(
                color = if (isMouseHovered) ColorConstant._FEF7E1 else Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClicked(urlItem)
            }
            .padding(
                vertical = 13.dp,
                horizontal = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = urlItem.url,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            if (urlItem.memo.isNotBlank()) {
                Text(
                    text = urlItem.memo,
                    fontSize = 11.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = urlItem.savedTime,
                color = ColorConstant._B4B4B4,
                fontSize = 10.sp,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onDeleteButtonClicked(urlItem)
                    }
                    .size(18.dp)
                    .alignByBaseline(),
                tint = ColorConstant._B4B4B4
            )
        }
    }
}
