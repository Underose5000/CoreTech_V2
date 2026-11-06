package com.example.coretechv2.repository

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent

/**
 * Receives scans from a hardware barcode scanner and passes them to whichever
 * screen is currently listening.
 *
 * The scanner (57/58/62/67/68 DW/DB series) works as a keyboard, in either
 * Bluetooth HID mode or 2.4G mode with its USB receiver. It "types" the
 * barcode and finishes with an end mark (CR, CR+LF or Tab, set by scanning a
 * code in its manual).
 *
 * [MainActivity][com.example.coretechv2.MainActivity] passes every key event
 * to [onKeyEvent]. Key presses from an external keyboard device are collected
 * here instead of being typed into the focused field. When the end mark
 * arrives, or the keys stop for [IDLE_FLUSH_MS] when no end mark is set, the
 * collected text is sent to the most recently registered listener.
 *
 * The device's own keys (including the flip phone keypad) are internal
 * devices and are never captured, and neither is the on-screen keyboard.
 *
 * Screens listen through
 * [BarcodeScanEffect][com.example.coretechv2.ui.component.BarcodeScanEffect],
 * which registers while the screen is shown. Only the newest listener gets a
 * scan, so a popup that listens takes priority over the screen behind it.
 * Scans arriving while nothing is listening are dropped.
 */
object BarcodeScanner {

    /** Time with no key presses after which a scan with no end mark is treated as finished. */
    private const val IDLE_FLUSH_MS = 250L

    /** The GS1 group separator, sent by some scanners as Ctrl + ]. */
    private const val GROUP_SEPARATOR = '\u001D'

    private val buffer = StringBuilder()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val flushRunnable = Runnable { flush() }
    private val listeners = mutableListOf<(String) -> Unit>()

    /**
     * Adds a listener. The newest listener receives scans until it is removed.
     */
    fun register(listener: (String) -> Unit) {
        listeners.add(listener)
    }

    /**
     * Removes a listener added with [register].
     */
    fun unregister(listener: (String) -> Unit) {
        listeners.remove(listener)
    }

    /**
     * Returns true if [event] came from an external hardware keyboard, which
     * is how the barcode scanner appears to Android.
     */
    private fun isFromScanner(event: KeyEvent): Boolean {
        val device = event.device ?: return false
        return !device.isVirtual &&
                device.isExternal &&
                device.supportsSource(InputDevice.SOURCE_KEYBOARD)
    }

    /**
     * Handles a key event from the activity.
     *
     * @return True if the event came from the scanner and has been consumed,
     * false if it should be handled normally.
     */
    fun onKeyEvent(event: KeyEvent): Boolean {
        if (!isFromScanner(event)) return false

        // Older scanners can send a whole string in one event
        @Suppress("DEPRECATION")
        if (event.action == KeyEvent.ACTION_MULTIPLE && event.keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            event.characters?.let { buffer.append(it) }
            scheduleFlush()
            return true
        }
        if (event.action != KeyEvent.ACTION_DOWN) return true

        when (event.keyCode) {
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_TAB -> {
                flush()
                return true
            }
        }

        if (event.isCtrlPressed && event.keyCode == KeyEvent.KEYCODE_RIGHT_BRACKET) {
            buffer.append(GROUP_SEPARATOR)
        } else {
            val char = event.getUnicodeChar(event.metaState)
            if (char > 0 && char and KeyCharacterMap.COMBINING_ACCENT == 0) {
                buffer.append(char.toChar())
            }
        }
        scheduleFlush()
        return true
    }

    private fun scheduleFlush() {
        mainHandler.removeCallbacks(flushRunnable)
        mainHandler.postDelayed(flushRunnable, IDLE_FLUSH_MS)
    }

    /**
     * Sends the collected text to the newest listener and clears the buffer.
     */
    private fun flush() {
        mainHandler.removeCallbacks(flushRunnable)
        val code = buffer.toString().trim { it.isWhitespace() }
        buffer.clear()
        if (code.isEmpty()) return

        Log.d("BarcodeScanner", "Scanned: $code")
        listeners.lastOrNull()?.invoke(code)
    }
}
