package com.medprimetech.annotationapp.core.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medprimetech.annotationapp.core.app.theme.AnnotationAppTheme


private const val paddingInnerCircle = 0.45f   // smallest circle
private const val paddingMiddleCircle = 0.3f   // medium circle
private const val paddingOuterCircle = 0.15f   // largest circle

private const val positionOffsetInner = 135f
private const val positionOffsetMiddle = 180f
private const val positionOffsetOuter = 90f

@Composable
fun AnimatedLoading(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    var width by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .size(40.dp)
            .onSizeChanged { width = it.width },
        contentAlignment = Alignment.Center
    ) {
        // Inner (smallest)
        CircularProgressIndicator(
            strokeWidth = 1.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(with(LocalDensity.current) { (width * paddingInnerCircle).toDp() })
                .graphicsLayer { rotationZ = rotation.value + positionOffsetInner },
        )

        // Middle
        CircularProgressIndicator(
            strokeWidth = 1.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(with(LocalDensity.current) { (width * paddingMiddleCircle).toDp() })
                .graphicsLayer { rotationZ = rotation.value + positionOffsetMiddle },
        )

        // Outer (largest)
        CircularProgressIndicator(
            strokeWidth = 1.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(with(LocalDensity.current) { (width * paddingOuterCircle).toDp() })
                .graphicsLayer { rotationZ = rotation.value + positionOffsetOuter },
        )
    }
}


@Preview(
    showBackground = true
)
@Composable
private fun AnimatedLoadingPreview() {
    AnnotationAppTheme {
        AnimatedLoading()
    }
}