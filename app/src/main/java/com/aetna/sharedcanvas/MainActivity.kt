package com.aetna.sharedcanvas

import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atMost
import androidx.core.graphics.withTranslation
import com.aetna.sharedcanvas.ui.theme.SharedCanvasPocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharedCanvasPocTheme {
                // A surface container using the 'background' color from the theme
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    val (canvas, text) = createRefs()
                    val path = remember { Path() }
                    val density = LocalDensity.current
                    val textSp = 32.sp
                    val textSizePixels = with(density) { textSp.toPx() }
                    val textPaint = remember {
                        TextPaint().apply {
                            isAntiAlias = true
                            xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
                            style = android.graphics.Paint.Style.FILL
                            textSize = textSizePixels
                            color = android.graphics.Color.BLACK
                        }
                    }
                    Canvas(
                        modifier = Modifier
                            .constrainAs(canvas) {
                                linkTo(top = parent.top, bottom = parent.bottom)
                                linkTo(start = parent.start, end = parent.end)
                                width = Dimension.matchParent
                                height = Dimension.matchParent
                            }
                            .graphicsLayer(alpha = 0.99F, clip = true)
                    ) {
                        drawIntoCanvas { canvas ->
                            // In the real use-case this path is super complicated and
                            //  animated based on many constantly changing variables.
                            path.reset()
                            path.moveTo(0F, 0F)
                            path.relativeLineTo(0F, canvas.nativeCanvas.height.toFloat())
                            path.relativeLineTo(canvas.nativeCanvas.width.toFloat(), 0F)
                            path.close()
                            drawPath(path, brush = canvasBrush)

                            canvas.nativeCanvas.withTranslation(
                                y = canvas.nativeCanvas.height * 0.51F
                            ) {
                                textLayout(
                                    "to color mix like this canvas text.\nThen very complex layouts of text will be simple!",
                                    width,
                                    textPaint
                                )
                                    .draw(this)
                            }
                        }
                    }

                    Text(
                        text = "I want this\ncomposable text",
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 42.sp,
                        maxLines = 2,
                        modifier = Modifier
                            .constrainAs(text) {
                                centerHorizontallyTo(canvas)
                                centerVerticallyTo(canvas, bias = 0.45F)
                                height = Dimension.preferredWrapContent.atMost(128.dp)
                                width = Dimension.preferredWrapContent.atMost(256.dp)
                            }
                    )
                }
            }
        }
    }
}

val canvasBrush =
    Brush.linearGradient(listOf(Color(0XFFD209B4), Color(0XFFD20962)))

fun textLayout(text: String, width: Int, textPaint: TextPaint) =
    StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setIncludePad(false)
        .build()