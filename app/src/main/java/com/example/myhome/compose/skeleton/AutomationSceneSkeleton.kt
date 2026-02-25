package com.example.myhome.compose.skeleton
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId


fun autoSceneConstraints(): ConstraintSet {
    return ConstraintSet {
        val image = createRefFor("image")
        val lineTop = createRefFor("line_top")
        val lineBottom = createRefFor("line_bottom")

        // Khối vuông bo góc bên trái
        constrain(image) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 12.dp)
            bottom.linkTo(parent.bottom, margin = 12.dp)
        }

        // Dòng chữ phía trên (Tên thiết bị)
        constrain(lineTop) {
            top.linkTo(image.top, margin = 4.dp)
            start.linkTo(image.end, margin = 16.dp)
            width = Dimension.value(120.dp) // Độ dài trung bình
        }

        // Dòng chữ phía dưới (Trạng thái/Phòng)
        constrain(lineBottom) {
            top.linkTo(lineTop.bottom, margin = 8.dp)
            start.linkTo(lineTop.start)
            width = Dimension.value(180.dp) // Độ dài dài hơn một chút
        }
    }
}
@Composable
fun AutomationSceneSkeleton() {
    ConstraintLayout(
        constraintSet = autoSceneConstraints(),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(androidx.compose.ui.graphics.Color.White) // Hoặc màu nền thẻ của bạn
    ) {
        // Ảnh thiết bị giả lập (Hình vuông bo góc)
        Box(
            modifier = Modifier
                .layoutId("image")
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )

        // Dòng text 1
        Box(
            modifier = Modifier
                .layoutId("line_top")
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )

        // Dòng text 2
        Box(
            modifier = Modifier
                .layoutId("line_bottom")
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}
@Composable
fun AutomationSceneListSkeleton(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
) {
    if(isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(3) {
                AutomationSceneSkeleton()
                Divider(
                    color = androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }else{
        contentAfterLoading()
    }
}