package au.com.shiftyjelly.pocketcasts.endofyear.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import au.com.shiftyjelly.pocketcasts.compose.components.TextH30
import au.com.shiftyjelly.pocketcasts.compose.components.TextH40
import au.com.shiftyjelly.pocketcasts.compose.components.TextH70
import au.com.shiftyjelly.pocketcasts.endofyear.R
import au.com.shiftyjelly.pocketcasts.utils.extensions.pxToDp

private const val PillarRotationAngle = -30f
private const val PillarTextSkew = 0.5f
private const val PillarTopAspectRatio = .58f
private val PillarBaseColor = Color(0xFFFE7E61)
private val Black24 = Color(0x3D000000)
private val PillarGradientStartColor = Color(0xFFFE7E61)
private val PillarGradientEndColor = Color(0x003C1F5D)

@Composable
fun CategoryPillar(
    title: String,
    duration: String,
    text: String,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentLocalView = LocalView.current
    val width = currentLocalView.width.pxToDp(context).dp * .2f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        Title(
            text = title,
            modifier = Modifier
                .width(width * 1.2f)
        )
        Duration(
            text = duration,
            modifier = Modifier
                .width(width * 1.2f)
                .alpha(0.8f)
                .padding(bottom = 30.dp)
        )
        Pillar(
            text = text,
            width = width,
            height = height,
        )
    }
}

@Composable
private fun Title(
    text: String,
    modifier: Modifier = Modifier,
) {
    TextH40(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        disableScale = true,
        modifier = modifier
    )
}

@Composable
private fun Duration(
    text: String,
    modifier: Modifier = Modifier,
) {
    TextH70(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.White,
        disableScale = true,
        modifier = modifier
    )
}

@Composable
private fun Pillar(
    text: String,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    val pillarTopAspectRatio = PillarTopAspectRatio
    val pillarTopHeight = width * pillarTopAspectRatio
    Box {
        Row {
            Box(
                modifier = modifier
                    .width(width / 2f)
                    .height(height)
                    .padding(top = pillarTopHeight / 2)
                    .leftPillarBackground()
            )
            Box(
                modifier = modifier
                    .width(width / 2f)
                    .height(height)
                    .padding(top = pillarTopHeight / 2)
                    .rightPillarBackground()
            )
        }
        Box {
            Image(
                painter = painterResource(R.drawable.rectangle),
                modifier = modifier.width(width),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .width(width)
                    .height(pillarTopHeight)
                    .graphicsLayer {
                        rotationZ = PillarRotationAngle
                    }
                    .drawWithContent {
                        withTransform(
                            {
                                val transformMatrix = Matrix()
                                transformMatrix.values[Matrix.SkewX] = PillarTextSkew
                                transform(transformMatrix)
                            }
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    .offset(x = -width * 0.15f)
            ) {
                TextH30(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    disableScale = true,
                    color = Color.White,
                )
            }
        }
    }
}

fun Modifier.leftPillarBackground() =
    graphicsLayer {
        alpha = 0.99f
    }.drawWithContent {
        drawRect(color = PillarBaseColor)
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Black24, Color.Black),
                startY = Float.POSITIVE_INFINITY,
                endY = 0f,
            ),
            blendMode = BlendMode.DstIn
        )
        drawRect(
            brush = Brush.verticalGradient(
                listOf(PillarGradientStartColor, PillarGradientEndColor),
                startY = 0f,
                endY = Float.POSITIVE_INFINITY,
            ),
            blendMode = BlendMode.DstIn
        )
    }

fun Modifier.rightPillarBackground() =
    graphicsLayer {
        alpha = 0.99f
    }.drawWithContent {
        drawRect(color = PillarBaseColor)
        drawRect(
            brush = Brush.verticalGradient(
                listOf(PillarGradientStartColor, PillarGradientEndColor),
                startY = 0f,
                endY = Float.POSITIVE_INFINITY,
            ),
            blendMode = BlendMode.DstIn
        )
    }
