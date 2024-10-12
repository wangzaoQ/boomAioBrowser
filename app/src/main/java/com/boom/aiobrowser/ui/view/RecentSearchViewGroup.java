package com.boom.aiobrowser.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 当一行显示不够时,自动换行
 */
public class RecentSearchViewGroup extends ViewGroup{
	public int VIEW_MARGI = SizeUtils.dp2px(9f); // 控件间的间距
	public int VIEW_HEIGHT = SizeUtils.dp2px(9f); // 控件间的间距

	public RecentSearchViewGroup(Context context) {
		super(context);
	}

	public RecentSearchViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecentSearchViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public boolean heightLimit = false;
	public boolean maxLimit = true;


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int stages = 1; // 行号
		heightLimit = false;
		int stageHeight = 0;
		int stageWidth = 0; // 记录当前行所占宽度
		List<Integer> heightList = new ArrayList(); // 记录每一行高度

		int wholeWidth = MeasureSpec.getSize(widthMeasureSpec) - (getPaddingRight()); // 父布局宽度

		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			if (i == 0) {
				stageWidth += child.getMeasuredWidth() + getPaddingLeft();
			} else {
				stageWidth += child.getMeasuredWidth() + VIEW_MARGI;
			}
			stageHeight = child.getMeasuredHeight();
			if (stageWidth > wholeWidth) { // 换行
				stages++;
				stageWidth = child.getMeasuredWidth() + getPaddingLeft();

				heightList.add(stageHeight);
			} else {
				if (heightList.size() < stages) { // 添加当前行数据
					heightList.add(stageHeight);
				} else if (heightList.get(stages -1) < stageHeight) { // 设置当前行高度为最高的子item高度
					heightList.set(stages -1, stageHeight);
				}
			}
			if (stages >=4 && maxLimit){
				heightLimit = true;
				heightList.remove(heightList.size()-1);
				break;
			}
		}

		int wholeHeight = 0;

		if (getChildCount() == 0) {
			wholeHeight = 0;
		} else {
			for (int i = 0; i < heightList.size(); i++) {
				wholeHeight += heightList.get(i) + VIEW_HEIGHT;
			}
			wholeHeight -= VIEW_HEIGHT;
		}
//		int wholeHeight = (stageHeight + VIEW_MARGI / 2) * stages - VIEW_MARGI / 2; // 真个布局的上下边缘不加间隔, 每一行之间的间隔为 VIEW_MARGI/2,第一行顶部和最后一行底部没有间距

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), resolveSize(wholeHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		int row = 0; // which row lay you view relative to parent
		int lengthX = l; // right position of child relative to parent
		int lengthY = t; // bottom position of child relative to parent

		List<Integer> heightList = new ArrayList(); // 记录每一行高度

		for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			if (i == 0) { // 第一项没有VIEW_MARGI的间隔
				lengthX += width + getPaddingLeft();
			} else {
				lengthX += width + VIEW_MARGI;
			}
			if (lengthX > (r - getPaddingRight()) ) { // 换行
//				lengthX = width + VIEW_MARGI + l;
				lengthX = width + l + getPaddingLeft();
				row++;

				heightList.add(height);
			} else {
				if (heightList.size() < row+1) { // 添加当前行数据
					heightList.add(height);
				} else if (heightList.get(row) < height) { // 设置当前行高度为最高的子item高度
					heightList.set(row, height);
				}
			}
//			lengthY = row * (height + VIEW_MARGI) + VIEW_MARGI + height + t;
//			int top = row * (height + VIEW_MARGI / 2) + getPaddingTop();

			int top = getPaddingTop();
			for (int j = 0; j < row; j++) {
				top += heightList.get(j) + VIEW_HEIGHT;
			}

			int bottom = top + height;
			child.layout(lengthX - width - l, top, lengthX - l, bottom);
		}

		if (getParent() instanceof ScrollView) {
			((ScrollView) getParent()).smoothScrollTo(0, b);
		}
	}

	public void setViewMargin(int margin){
		VIEW_MARGI = SizeUtils.dp2px(margin);
	}

	public void updateHeight() {

	}
}
