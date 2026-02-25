package com.example.myhome.compose.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun LineChartSkeleton(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
) {
    if(isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(24.dp))

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 8.dp)
            ) {
                val (ref1, ref2, ref3, ref4, ref5, ref6, ref7) = createRefs()
                val refs = listOf(ref1, ref2, ref3, ref4, ref5, ref6, ref7)

                val columnHeights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 0.4f)

                val chain = createHorizontalChain(
                    elements = refs.toTypedArray(),
                    chainStyle = ChainStyle.Spread
                )

                // 3. Vẽ từng cột
                refs.forEachIndexed { index, ref ->
                    Box(
                        modifier = Modifier
                            .constrainAs(ref) {
                                bottom.linkTo(parent.bottom)
                            }
                            .height(300.dp * columnHeights[index])
                            .width(30.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .shimmerEffect() // Extension function shimmer của bạn
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
        }
    }
    else{
        contentAfterLoading()
    }
}