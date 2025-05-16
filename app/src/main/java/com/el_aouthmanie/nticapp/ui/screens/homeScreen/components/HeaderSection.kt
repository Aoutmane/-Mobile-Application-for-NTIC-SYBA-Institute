package com.el_aouthmanie.nticapp.ui.screens.homeScreen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.el_aouthmanie.nticapp.R
import com.el_aouthmanie.nticapp.ui.theme.normalPadding
import com.el_aouthmanie.nticapp.ui.theme.textSecondary
import com.el_aouthmanie.nticapp.ui.theme.titlePrimary

@Composable
fun HeaderSection(
    welcomingMessage : String = "HELLO!",
    name : String = "Aoutmane",
    quote : String = "Mr. Aoutmane is a legend",
    uri : String? = null,
    onProfileClicked : () -> Unit
) {
    val img = if (uri != null) {
        rememberAsyncImagePainter(uri)
    } else {
        painterResource(id = R.drawable.img)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(normalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$welcomingMessage, $name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = titlePrimary
            )

            // Subtitle text
            Text(
                text = quote,
                fontSize = 13.sp,
                color = textSecondary,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(top = 8.dp, start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.width(normalPadding))

        Image(
            painter = img ,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterVertically)
                .clip(CircleShape)
                .clickable {
                    onProfileClicked()
                }
                .shadow(20.dp, CircleShape), // Add a shadow to the image
            contentScale = ContentScale.Crop
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true, device = Devices.DEFAULT)
@Composable
fun Hehe(modifier: Modifier = Modifier) {
    HeaderSection(){}

}