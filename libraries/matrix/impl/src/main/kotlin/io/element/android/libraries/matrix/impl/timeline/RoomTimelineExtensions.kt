/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.matrix.impl.timeline

import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.matrix.impl.util.cancelAndDestroy
import io.element.android.libraries.matrix.impl.util.destroyAll
import io.element.android.libraries.matrix.impl.util.mxCallbackFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import org.matrix.rustcomponents.sdk.BackPaginationStatus
import org.matrix.rustcomponents.sdk.BackPaginationStatusListener
import org.matrix.rustcomponents.sdk.Room
import org.matrix.rustcomponents.sdk.TimelineDiff
import org.matrix.rustcomponents.sdk.TimelineItem
import org.matrix.rustcomponents.sdk.TimelineListener
import timber.log.Timber

internal fun Room.timelineDiffFlow(onInitialList: suspend (List<TimelineItem>) -> Unit): Flow<List<TimelineDiff>> =
    callbackFlow {
        val listener = object : TimelineListener {
            override fun onUpdate(diff: List<TimelineDiff>) {
                trySendBlocking(diff)
            }
        }
        val roomId = id()
        Timber.d("Open timelineDiffFlow for room $roomId")
        val result = addTimelineListenerBlocking(listener)
        try {
            onInitialList(result.items)
        } catch (exception: Exception) {
            Timber.d(exception, "Catch failure in timelineDiffFlow of room $roomId")
        }
        awaitClose {
            Timber.d("Close timelineDiffFlow for room $roomId")
            result.itemsStream.cancelAndDestroy()
            result.items.destroyAll()
        }
    }.catch {
        Timber.d(it, "timelineDiffFlow() failed")
    }.buffer(Channel.UNLIMITED)

internal fun Room.backPaginationStatusFlow(): Flow<BackPaginationStatus> =
    mxCallbackFlow {
        val listener = object : BackPaginationStatusListener {
            override fun onUpdate(status: BackPaginationStatus) {
                trySendBlocking(status)
            }
        }
        tryOrNull {
            subscribeToBackPaginationStatus(listener)
        }
    }.buffer(Channel.UNLIMITED)
