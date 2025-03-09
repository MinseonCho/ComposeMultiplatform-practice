package ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import model.QueryItem
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import ui.style.ColorConstant

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PageScreen(
    onSendDeeplinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    sendLogTexts: ImmutableList<String>,
    pageViewModel: PageViewModel = koinViewModel<PageViewModel>(),
    urlId: Int? = null,
) {
    val uiState by pageViewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        pageViewModel.init(urlId = urlId)
    }

    LaunchedEffect(Unit) {
        pageViewModel.eventFlow.collect { event ->
            when (event) {
                is PageEvent.TriggerUrl -> {
                    onSendDeeplinkClicked(event.url)
                }

                is PageEvent.ShowSnackBar -> {
                    coroutineScope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }

                is PageEvent.CopyUrlToClipboard -> {
                    clipboardManager.setText(
                        AnnotatedString(text = event.url)
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorConstant._F5F5F7)
                .padding(top = 15.dp, end = 15.dp)
                .verticalScroll(rememberScrollState())
        ) {
            UrlField(
                url = uiState.url,
                isUrlSaved = uiState.isUrlSaved,
                onUrlChanged = pageViewModel::onUrlChanged,
                onSendButtonClicked = pageViewModel::onSendButtonClicked,
                onSaveButtonClicked = pageViewModel::onSaveButtonClicked,
                onCopyButtonClicked = pageViewModel::onCopyButtonClicked,
            )

            Spacer(modifier = Modifier.height(5.dp))

            QueryContent(
                queries = uiState.queries,
                onKeyChanged = pageViewModel::onQueryKeyChanged,
                onValueChanged = pageViewModel::onQueryValueChanged,
                onCheckedChanged = pageViewModel::onCheckedChanged,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(5.dp))

            LogBody(
                logTexts = sendLogTexts,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QueryContent(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (QueryItem, String) -> Unit,
    onValueChanged: (QueryItem, String) -> Unit,
    onCheckedChanged: (QueryItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = ColorConstant._E2EFFB,
                    shape = RoundedCornerShape(4.dp)
                ).padding(horizontal = 6.dp, vertical = 3.dp),
            text = "Query Params", // TODO: string resource 로 분리
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._5A8CDD,
        )

        Spacer(modifier = Modifier.height(10.dp))

        QueryTable(
            queries = queries,
            onKeyChanged = onKeyChanged,
            onValueChanged = onValueChanged,
            onCheckedChanged = onCheckedChanged
        )
    }
}

@Composable
fun LogBody(
    logTexts: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = ColorConstant._E8F3E7,
                    shape = RoundedCornerShape(4.dp)
                ).padding(horizontal = 6.dp, vertical = 3.dp),
            text = "Log",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._79AD73,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ColorConstant._F5F5F7)
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = logTexts.joinToString("\n"),
                color = ColorConstant._848484,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun QueryTable(
    queries: ImmutableList<QueryItem>,
    onKeyChanged: (QueryItem, String) -> Unit,
    onValueChanged: (QueryItem, String) -> Unit,
    onCheckedChanged: (QueryItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(width = 1.dp, color = ColorConstant._E8E8E8),
        state = listState
    ) {
        item {
            QueryTableHeaderRow()
            Divider(color = ColorConstant._E8E8E8, modifier = Modifier.height(1.dp))
        }

        items(queries) { query ->
            SingleQuery(
                queryItem = query,
                onCheckedChanged = onCheckedChanged,
                onKeyChanged = onKeyChanged,
                onValueChanged = onValueChanged
            )
            Divider(color = ColorConstant._E8E8E8, modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
fun QueryTableHeaderRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Checkbox(
            modifier = Modifier.width(50.dp), // checkBox width 는 고정
            checked = false,
            enabled = false,
            onCheckedChange = {}
        )

        TableVerticalDivider()

        Text(
            text = "Key",
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .weight(weight = 0.3f, fill = true)
                .align(Alignment.CenterVertically),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._848484
        )

        TableVerticalDivider()

        Text(
            text = "Value",
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .weight(weight = 0.7f, fill = true)
                .align(Alignment.CenterVertically),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._848484
        )
    }
}

@Composable
fun SingleQuery(
    queryItem: QueryItem,
    onKeyChanged: (QueryItem, String) -> Unit,
    onValueChanged: (QueryItem, String) -> Unit,
    onCheckedChanged: (QueryItem, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Checkbox(
            modifier = Modifier.width(50.dp), // checkBox width 는 고정
            checked = queryItem.isChecked,
            colors = CheckboxDefaults.colors(
                checkedColor = ColorConstant._5A8CDD,
                checkmarkColor = Color.White,
                uncheckedColor = ColorConstant._E8E8E8
            ),
            onCheckedChange = { isChecked ->
                onCheckedChanged(queryItem, isChecked)
            }
        )

        TableVerticalDivider()

        InputField(
            text = queryItem.key,
            onValueChanged = {
                onKeyChanged(queryItem, it)
            },
            modifier = Modifier
                .padding(5.dp)
                .weight(weight = 0.3f, fill = true)
                .align(Alignment.CenterVertically)
        )

        TableVerticalDivider()

        InputField(
            text = queryItem.value,
            onValueChanged = {
                onValueChanged(queryItem, it)
            },
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(weight = 0.7f, fill = true)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TableVerticalDivider() {
    Divider(
        color = ColorConstant._E8E8E8,
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isSingleLine: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = text,
        onValueChange = {
            onValueChanged(it)
        },
        modifier = modifier
            .background(
                color = if (isFocused) ColorConstant._FAFAFA else Color.Transparent
            ).onFocusChanged {
                isFocused = it.isFocused
            },
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontSize = 12.sp,
            color = ColorConstant._848484,
            lineHeight = 16.sp,
            letterSpacing = 1.sp
        ),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = text,
            innerTextField = innerTextField,
            enabled = isEnabled,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            singleLine = isSingleLine,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color(0xfffaf7f7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
    }
}
