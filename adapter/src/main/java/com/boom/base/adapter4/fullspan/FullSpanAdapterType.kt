package com.boom.base.adapter4.fullspan

import androidx.recyclerview.widget.GridLayoutManager

/**
 * If Adapter needs full span, implement this interface.
 * Need to be used with [com.boom.base.adapter4.base.layoutmanager.QuickGridLayoutManager]
 *
 * 如果此类型的 Adapter 需要满跨度，实现此接口。
 * 需要配合 [com.boom.base.adapter4.base.layoutmanager.QuickGridLayoutManager] 使用，或者自行实现[GridLayoutManager.SpanSizeLookup]
 *
 */
interface FullSpanAdapterType