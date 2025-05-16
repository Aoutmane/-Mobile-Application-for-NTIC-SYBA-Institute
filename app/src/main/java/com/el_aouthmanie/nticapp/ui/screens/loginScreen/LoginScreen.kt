package com.el_aouthmanie.nticapp.ui.screens.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.el_aouthmanie.nticapp.R
import com.el_aouthmanie.nticapp.ui.compenents.NextButton
import com.el_aouthmanie.nticapp.ui.theme.normalPadding

@Composable
fun LoginScreen(
    logoImagePainter: Painter? = null,
    backgroundImagePainter: Painter? = null,
    onLogoClicked : () -> Unit = {},
    onGuestRequest: () -> Unit = {},
    onLoginRequest: (String, String) -> Unit = { _, _-> }

) {

    var emaill by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {

        // Background Image with Slanted Bottom Edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)

                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
        ) {
            Image(
                painter = backgroundImagePainter ?: painterResource(id = R.drawable.background_ofppt),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)

                .padding(top = 200.dp, bottom = normalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo (Overlapping the Image)
            Image(
                painter = logoImagePainter ?: painterResource(id = R.drawable.logo_ofppt),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            TextField(
                value = emaill,
                onValueChange = {emaill = it},

                label = { Text("Username") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Person, contentDescription = "Email Icon")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Blue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            var passwordVisible by remember { mutableStateOf(false) }
            TextField(
                value = password,
                onValueChange = {password = it},
                label = { Text("Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Lock Icon")
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Lock else Icons.Default.RemoveRedEye,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color.Blue
                )
            )

            Spacer(modifier = Modifier.height(normalPadding))

            // Instructional Text
            Text(
                text = "You get your login and password from the administration.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(2f))

            // Login Button
            NextButton(
                modifier = Modifier,
                text = "Login"){
                onLoginRequest(emaill,password)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enter as Guest Button
            NextButton(text = "Enter As Guest"){
                onGuestRequest()
            }

        }
    }
}
