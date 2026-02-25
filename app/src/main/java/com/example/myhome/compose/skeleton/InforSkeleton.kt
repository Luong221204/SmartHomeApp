package com.example.myhome.compose.skeleton

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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

fun mySkeletonConstraints(): ConstraintSet {
    return ConstraintSet {
        // Khai báo các ID
        val title = createRefFor("title")
        val mainBox = createRefFor("main_box")
        val smallBoxLeft = createRefFor("small_box_left")
        val smallBoxRight = createRefFor("small_box_right")

        // Ràng buộc cho thanh tiêu đề phía trên
        constrain(title) {
            top.linkTo(parent.top, margin = 16.dp)
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            width = Dimension.fillToConstraints
        }

        // Ràng buộc cho khối lớn ở giữa
        constrain(mainBox) {
            top.linkTo(title.bottom, margin = 12.dp)
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            width = Dimension.fillToConstraints
        }

        // Ràng buộc cho 2 khối nhỏ phía dưới (Dùng Horizontal Chain)
        constrain(smallBoxLeft) {
            top.linkTo(mainBox.bottom, margin = 12.dp)
            start.linkTo(parent.start, margin = 16.dp)
        }
        constrain(smallBoxRight) {
            top.linkTo(mainBox.bottom, margin = 12.dp)
            end.linkTo(parent.end, margin = 16.dp)
        }

        // Tạo chuỗi ngang để 2 khối nhỏ nằm song song và cách đều
        //createHorizontalChain(smallBoxLeft, smallBoxRight, chainStyle = ChainStyle.Spread)
    }
}
@Composable
fun FullCardSkeleton(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
) {
    if(isLoading){
        ConstraintLayout(
            constraintSet = mySkeletonConstraints(),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding()
                .background(androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(12.dp))
        ) {
            // 1. Thanh tiêu đề dài
            Box(
                modifier = Modifier
                    .layoutId("title")
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            // 2. Khối nội dung lớn ở giữa
            Box(
                modifier = Modifier
                    .layoutId("main_box")
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            // 3. Khối nhỏ bên trái
            Box(
                modifier = Modifier
                    .layoutId("small_box_left")
                    .size(width = 160.dp, height = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )

            // 4. Khối nhỏ bên phải
            Box(
                modifier = Modifier
                    .layoutId("small_box_right")
                    .size(width = 160.dp, height = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
        }
    }
    else{
        contentAfterLoading()

    }

}