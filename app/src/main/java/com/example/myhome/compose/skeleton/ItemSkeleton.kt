package com.example.myhome.compose.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.myhome.compose.templates.DoubleInRow

@Composable
fun ShimmerDeviceListItem(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
){
    val r = listOf<Int>(0,1,2,3,4,5)
    if(isLoading){
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(32.dp)) {
            r.DoubleInRow {
                    first, second ->
                DeviceSkeleton(modifier = Modifier
                    .height(120.dp)
                    .width(150.dp)
                    .background(color = Color.Black.copy(0.1f), shape = RoundedCornerShape(8.dp)))
                second?.let {
                    DeviceSkeleton(modifier = Modifier
                        .height(120.dp)
                        .width(150.dp)
                        .background(color =Color.Black.copy(0.1f), shape = RoundedCornerShape(8.dp)))
                }
            }
        }
    }else{
        contentAfterLoading()
    }
}
@Composable
fun DeviceSkeleton(
    modifier: Modifier
){
    ConstraintLayout(modifier= modifier){
        val (cir,rec) = createRefs()
        Box(modifier = Modifier.constrainAs(cir){
            top.linkTo(parent.top,16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }.size(44.dp).clip(CircleShape).shimmerEffect())
        Box(
            modifier = Modifier.constrainAs(rec){
                bottom.linkTo(parent.bottom,16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }.height(18.dp).fillMaxWidth(0.7f).shimmerEffect().clip(RoundedCornerShape(16.dp))
        )
    }
}