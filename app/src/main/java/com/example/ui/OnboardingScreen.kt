package com.example.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit,
    onLogInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Beautiful gradient mesh background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8DDFF).copy(alpha = 0.5f),
            Color(0xFFFDD0EA).copy(alpha = 0.3f),
            Color(0xFFBEE9FF).copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background
        )
    )

    // Bouncing float value for floating notes animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(24.dp)
    ) {
        // Decorative glowing background blobs
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = (-50).dp)
                .blur(80.dp)
                .background(Color(0xFFA78BFA).copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 50.dp)
                .blur(80.dp)
                .background(Color(0xFFFDD0EA).copy(alpha = 0.15f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Branding Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                LuminaLogo(
                    size = 42.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lumina",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // 2. Central Playful Mock Note Illustration (Match Design image exactly)
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .graphicsLayer {
                        translationY = floatAnim
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background radial glow
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .blur(50.dp)
                        .background(Color(0xFFE8DDFF).copy(alpha = 0.4f), CircleShape)
                )

                // The Floating Glassmorphic Note Card
                Box(
                    modifier = Modifier
                        .size(210.dp, 270.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp, 10.dp)
                                    .background(Color(0xFF674BB5).copy(alpha = 0.2f), RoundedCornerShape(5.dp))
                            )
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pin",
                                tint = Color(0xFF674BB5).copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Simulated content lines
                        Box(
                            modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(14.dp)
                                        .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(14.dp)
                                        .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Pastel categories circles
                        Row {
                            Box(modifier = Modifier.size(14.dp).background(Color(0xFFFDD0EA), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(14.dp).background(Color(0xFFBEE9FF), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.size(14.dp).background(Color(0xFFE8DDFF), CircleShape))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Footer with overlapping avatars & checkmark
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Overlapping profile pics
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuBrZy5hZehG2iYwfCjSE0BpT3NKqtk-umyIemD0LgrO6eIpzdmfHKHpsYvwLkDvtCpzCipZM45LllnX8ctiMO_BS75uNZ4qqMZZ0aU2DUv3O9lFt227AjER21xieg7T85f06vf-yubmbZU0JOlDh95DqkaxxuMxmfyQ_PIaIuRjBlUjUjgfSmS-y-GQPcqPvPzDK5kOQ1lwxrlyjek4_JYIRN2Gj2AtaaBx3JAacmiXgeDiEyHAPlOS"),
                                    contentDescription = "User avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Image(
                                    painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuDDfzgiiIq6ueqa2QH-n20MjbiKa-gxsz4-ykyk2xWPOZ2Gf5aF7km6SHqoFkUi6oyDr1Wzw3VOFXNdaaXFg-UcaGHNiT6asEX-52pG4_Gd67ucE_nENVDOFNKNDvzPXsEY3skHOTpXxH-d3H4tKmE0FRqdk3aIAr2EhbFUACcdNprUV6m776BXrr7kx0PEgDYD-oSx3N6CQPWZAb9cy2De_OzLz8B3FAARg3ioMy8mWp3nUJp0DZFV"),
                                    contentDescription = "User avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .offset(x = (-8).dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Checked",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Floating round Edit icon at top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-20).dp, y = (-10).dp)
                        .size(46.dp)
                        .background(Color.White, CircleShape)
                        .clip(CircleShape)
                        .clickable {}
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Edit note",
                        tint = Color(0xFF765469),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Floating deadline card at bottom-left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-20).dp, y = (-30).dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(26.dp)
                                .background(Color(0xFF396477), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DEADLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Today, 5 PM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // 3. Text Descriptions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Capture Your\n")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontStyle = FontStyle.Italic)) {
                            append("Brilliance")
                        }
                    },
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 42.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Organize your life, projects, and thoughts in a space designed for clarity. Lumina turns your chaotic notes into actionable clarity.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 4. Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGetStartedClick,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .testTag("get_started_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Arrow Forward",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Log In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onLogInClick() }
                            .testTag("login_link")
                    )
                }
            }
        }
    }
}
