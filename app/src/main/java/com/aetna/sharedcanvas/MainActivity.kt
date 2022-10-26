package com.aetna.sharedcanvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aetna.sharedcanvas.ui.theme.SharedCanvasPocTheme

class MainActivity : ComponentActivity() {
    @ExperimentalTextApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharedCanvasPocTheme {
                // Our example of XOR to clip out the pink requires we use a surface behind the
                // layout that is providing the root graphics layer with an alpha < 1
                Surface(color = MaterialTheme.colorScheme.background) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(alpha = 0.99F, clip = true)
                            .drawWithCache {
                                val path = Path()
                                val canvasBrush = Brush.linearGradient(
                                    listOf(Color(0XFFD209B4), Color(0XFFD20962))
                                )
                                onDrawWithContent {
                                    // The color from a tint filter with a Xor blend will only show
                                    // on top of transparent areas. otherwise it is clipped and made
                                    // transparent.
                                    drawRect(Color.Transparent)

                                    //  For attain we should need only call drawWave
                                    //  that is currently defined in WaveAnimation.kt
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
                        val (heading, text) = createRefs()
                        val density = LocalDensity.current
                        Text(
                            text = "BOOM!",
                            modifier = Modifier
                                .constrainAs(heading) {
                                    centerHorizontallyTo(parent)
                                    centerVerticallyTo(parent, bias = 0.45F)
                                    linkTo(top = parent.top, bottom = text.top, bias = 1.0F)
                                    height = Dimension.preferredWrapContent
                                    width = Dimension.preferredWrapContent
                                }
                                .drawWithLayerPaint {
                                    blendMode = BlendMode.Xor
                                    colorFilter = ColorFilter.tint(Color(142, 142, 147, 255))
                                },
                            fontSize = 96.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.2.sp,
                            style = TextStyle(
                                drawStyle = Stroke(
                                    width = with(density) { 3.dp.toPx() },
                                    join = StrokeJoin.Round,
                                    miter = 1F
                                )
                            )
                        )
                        Text(
                            text = "This composable text is composited with the path colors of the constraint layout modifier!",
                            modifier = Modifier
                                .constrainAs(text) {
                                    centerHorizontallyTo(parent)
                                    centerVerticallyTo(parent, bias = 0.55F)
                                    height = Dimension.preferredWrapContent
                                    width = Dimension.preferredWrapContent
                                }
                                .drawWithLayerPaint {
                                    blendMode = BlendMode.Xor
                                    colorFilter = ColorFilter.tint(color = Color.Black)
                                },
                            fontSize = 32.sp,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.drawWithLayerPaint(configure: Paint.(scope: CacheDrawScope) -> Unit): Modifier {
    return drawWithCache {
        val paint = Paint()
        paint.configure(this)
        onDrawWithContent {
            drawContext.canvas.saveLayer(size.toRect(), paint)
            drawContent()
            drawContext.canvas.restore()
        }
    }
}
