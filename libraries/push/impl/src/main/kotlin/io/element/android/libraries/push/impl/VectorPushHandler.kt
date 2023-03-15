/*
 * Copyright (c) 2022 New Vector Ltd
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

package io.element.android.libraries.push.impl

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.element.android.libraries.androidutils.network.WifiDetector
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.di.ApplicationContext
import io.element.android.libraries.push.api.store.PushDataStore
import io.element.android.libraries.push.impl.model.PushData
import io.element.android.libraries.push.impl.notifications.NotifiableEventResolver
import io.element.android.libraries.push.impl.notifications.NotificationActionIds
import io.element.android.libraries.push.impl.notifications.NotificationDrawerManager
import io.element.android.libraries.push.impl.store.DefaultPushDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

private val loggerTag = LoggerTag("Push", LoggerTag.SYNC)

class VectorPushHandler @Inject constructor(
    private val notificationDrawerManager: NotificationDrawerManager,
    private val notifiableEventResolver: NotifiableEventResolver,
    // private val activeSessionHolder: ActiveSessionHolder,
    private val pushDataStore: PushDataStore,
    private val defaultPushDataStore: DefaultPushDataStore,
    private val actionIds: NotificationActionIds,
    @ApplicationContext private val context: Context,
    private val buildMeta: BuildMeta
) {

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val wifiDetector: WifiDetector = WifiDetector(context)

    // UI handler
    private val mUIHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    /**
     * Called when message is received.
     *
     * @param pushData the data received in the push.
     */
    fun handle(pushData: PushData) {
        Timber.tag(loggerTag.value).d("## handling pushData")

        if (buildMeta.lowPrivacyLoggingEnabled) {
            Timber.tag(loggerTag.value).d("## pushData: $pushData")
        }

        runBlocking {
            defaultPushDataStore.incrementPushCounter()
        }

        // Diagnostic Push
        if (pushData.eventId == PushersManager.TEST_EVENT_ID) {
            val intent = Intent(actionIds.push)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            return
        }

        if (!pushDataStore.areNotificationEnabledForDevice()) {
            Timber.tag(loggerTag.value).i("Notification are disabled for this device")
            return
        }

        mUIHandler.post {
            if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                // we are in foreground, let the sync do the things?
                Timber.tag(loggerTag.value).d("PUSH received in a foreground state, ignore")
            } else {
                coroutineScope.launch(Dispatchers.IO) { handleInternal(pushData) }
            }
        }
    }

    /**
     * Internal receive method.
     *
     * @param pushData Object containing message data.
     */
    private suspend fun handleInternal(pushData: PushData) {
        try {
            if (buildMeta.lowPrivacyLoggingEnabled) {
                Timber.tag(loggerTag.value).d("## handleInternal() : $pushData")
            } else {
                Timber.tag(loggerTag.value).d("## handleInternal()")
            }

            /* TODO EAx
            - Open session
            - get the event
            - display the notif

            val session = activeSessionHolder.getOrInitializeSession()

            if (session == null) {
                Timber.tag(loggerTag.value).w("## Can't sync from push, no current session")
            } else {
                if (isEventAlreadyKnown(pushData)) {
                    Timber.tag(loggerTag.value).d("Ignoring push, event already known")
                } else {
                    // Try to get the Event content faster
                    Timber.tag(loggerTag.value).d("Requesting event in fast lane")
                    getEventFastLane(session, pushData)

                    Timber.tag(loggerTag.value).d("Requesting background sync")
                    session.syncService().requireBackgroundSync()
                }
            }

             */
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## handleInternal() failed")
        }
    }

    /* TODO EAx
    private suspend fun getEventFastLane(session: Session, pushData: PushData) {
        pushData.roomId ?: return
        pushData.eventId ?: return

        if (wifiDetector.isConnectedToWifi().not()) {
            Timber.tag(loggerTag.value).d("No WiFi network, do not get Event")
            return
        }

        Timber.tag(loggerTag.value).d("Fast lane: start request")
        val event = tryOrNull { session.eventService().getEvent(pushData.roomId, pushData.eventId) } ?: return

        val resolvedEvent = notifiableEventResolver.resolveInMemoryEvent(session, event, canBeReplaced = true)

        if (resolvedEvent is NotifiableMessageEvent) {
            // If the room is currently displayed, we will not show a notification, so no need to get the Event faster
            if (notificationDrawerManager.shouldIgnoreMessageEventInRoom(resolvedEvent)) {
                return
            }
        }

        resolvedEvent
                ?.also { Timber.tag(loggerTag.value).d("Fast lane: notify drawer") }
                ?.let {
                    notificationDrawerManager.updateEvents { it.onNotifiableEventReceived(resolvedEvent) }
                }
    }

     */

    // check if the event was not yet received
    // a previous catchup might have already retrieved the notified event
    private fun isEventAlreadyKnown(pushData: PushData): Boolean {
        /* TODO EAx
        if (pushData.eventId != null && pushData.roomId != null) {
            try {
                val session = activeSessionHolder.getSafeActiveSession() ?: return false
                val room = session.getRoom(pushData.roomId) ?: return false
                return room.getTimelineEvent(pushData.eventId) != null
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## isEventAlreadyKnown() : failed to check if the event was already defined")
            }
        }

         */
        return false
    }
}
