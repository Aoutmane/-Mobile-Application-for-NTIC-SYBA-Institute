package com.el_aouthmanie.nticapp.ui.compenents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.el_aouthmanie.nticapp.R

@Composable
fun NotificationItem(
    profileImage: Int,
    title: String,
    sender: String,
    message: String,
    collapse: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(imageVector = Icons.Filled.AccountBox,
                contentDescription = "notification",

                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold

                            )
                        ) {

                            append(sender)

                        }



                        append(" - ")
                        append(message)
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (collapse) 3 else Int.MAX_VALUE

                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

data class NotificationData(
    val imageRes: Int,
    val title: String,
    val sender: String,
    val message: String
)

val notificationList = listOf(
    NotificationData(R.drawable.profile, "EVENT", "lmodira bnt LKALB", "rda makayn ri l3sa"),
    NotificationData(
        R.drawable.profile,
        "ANNOUNCEMENT",
        "mozafucker bn 3axir",
        "ina lilah awdi ahmad"
    ),
    NotificationData(R.drawable.profile, "CHALLENGE", "club IT", "aha tnakt"),
    NotificationData(R.drawable.profile, "CHALLENGE", "club IT", "aha tnakt"),
    NotificationData(
        R.drawable.profile,
        "CHALLENGE",
        "club IT",
        "aha tnaktLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata"
    )
)

@Preview(showBackground = true)
@Composable
fun previewr() {
    NotificationItem(
        profileImage = R.drawable.profile,
        title = "helo",
        sender = "admin",
        message = "foeievoievn svnsmivnddddddddddddddddddddddddddddsdmoiidsmofoLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata",
        true
    ) {}
}