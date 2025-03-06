package ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.style.ColorConstant


@Composable
fun UrlField(
    url: String,
    isUrlSaved: Boolean,
    onUrlChanged: (String) -> Unit,
    onSendButtonClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit,
    onCopyButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
            .padding(end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp)
                .background(
                    color = ColorConstant._FEF7E1,
                    shape = RoundedCornerShape(4.dp)
                ).padding(horizontal = 6.dp, vertical = 3.dp),
            text = "Url",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorConstant._E6A358,
        )

        BasicTextField(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f, fill = true),
            value = url,
            onValueChange = {
                onUrlChanged(it)
            },
            textStyle = TextStyle.Default.copy(
                color = ColorConstant._848484
            )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = {
                    onSendButtonClicked()
                },
                interactionSource = remember { MutableInteractionSource() },
                content = {
                    Text(
                        text = "Send",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                },
                enabled = url.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ColorConstant._E6A358,
                    disabledContentColor = ColorConstant._B4B4B4
                )
            )
            Icon(
                imageVector = if (isUrlSaved) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarOutline
                },
                contentDescription = "save url",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSaveButtonClicked()
                    },
                tint = if (isUrlSaved) {
                    ColorConstant._E6A358
                } else {
                    ColorConstant._B4B4B4
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onCopyButtonClicked()
                    },
                tint = ColorConstant._B4B4B4
            )
        }
    }
}
