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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
fun activityLogConstraints(): ConstraintSet {
    return ConstraintSet {
        val icon = createRefFor("icon")
        val lineTop = createRefFor("line_top")
        val lineBottom = createRefFor("line_bottom")

        // Icon hình tròn bên trái
        constrain(icon) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 12.dp)
            bottom.linkTo(parent.bottom, margin = 12.dp)
        }

        // Dòng chữ ngắn phía trên
        constrain(lineTop) {
            top.linkTo(icon.top)
            start.linkTo(icon.end, margin = 12.dp)
            width = Dimension.value(100.dp) // Độ dài cố định ngắn
        }

        // Dòng chữ dài phía dưới
        constrain(lineBottom) {
            top.linkTo(lineTop.bottom, margin = 8.dp)
            start.linkTo(lineTop.start)
            end.linkTo(parent.end, margin = 12.dp)
            width = Dimension.fillToConstraints // Kéo dài hết mức có thể
        }
    }
}

@Composable
fun ActivityLogItemSkeleton() {
    ConstraintLayout(
        constraintSet = activityLogConstraints(),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Icon tròn
        Box(
            modifier = Modifier
                .layoutId("icon")
                .size(28.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        // Thanh tiêu đề ngắn
        Box(
            modifier = Modifier
                .layoutId("line_top")
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )

        // Thanh nội dung dài
        Box(
            modifier = Modifier
                .layoutId("line_bottom")
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}
@Composable
fun LogListSkeleton(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
) {
    if(isLoading) {
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(3) {
                ActivityLogItemSkeleton()
                // Thêm đường kẻ mờ giữa các item
                Divider(
                    color = androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
    else{
        contentAfterLoading()
    }
}