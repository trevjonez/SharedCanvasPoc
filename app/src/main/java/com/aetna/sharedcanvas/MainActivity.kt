package com.aetna.sharedcanvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atMost
import com.aetna.sharedcanvas.ui.theme.SharedCanvasPocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharedCanvasPocTheme {
                val path = remember { Path() }
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.99F, clip = true)
                        .drawWithCache {
                            val canvasBrush = Brush.linearGradient(
                                listOf(Color(0XFFD209B4), Color(0XFFD20962))
                            )
                            onDrawWithContent {
                                // Drawing the black text where the pink is
                                //  path is not drawn requires transparent pixels
                                drawRect(Color.Transparent)

                                // In the real use-case this path is super complicated and
                                //  animated based on many constantly changing variables.
                                path.reset()
                                path.moveTo(0F, 0F)
                                path.relativeLineTo(0F, size.height)
                                path.relativeLineTo(size.width, 0F)
                                path.close()
                                drawPath(path, brush = canvasBrush)
                                drawContent()
                            }
                        }
                ) {
                    val (text) = createRefs()
                    Text(
                        text = "This composable text is composited with the path colors of the constraint layout modifier!",
                        modifier = Modifier
                            .constrainAs(text) {
                                centerHorizontallyTo(parent)
                                centerVerticallyTo(parent, bias = 0.45F)
                                height = Dimension.preferredWrapContent
                                width = Dimension.preferredWrapContent
                            }
                            .drawWithCache {
                                val paint = Paint().apply {
                                    blendMode = BlendMode.Xor
                                    colorFilter = ColorFilter.tint(color = Color.Black)
                                }
                                onDrawWithContent {
                                    drawContext.canvas.saveLayer(size.toRect(), paint)
                                    drawContent()
                                    drawContext.canvas.restore()
                                }
                            },
                        color = Color.White,
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Visible,
                    )
                }
            }
        }
    }
}
