package com.example.support

import com.evalship.tinyeval.Tinyeval

// Built on the first input, after the worker has moved System.out off the replies.
private val support by lazy { Support() }

/**
 * tinyeval's worker: the JVM and the client start once per worker, not once per test case.
 * When the cause isn't the app (no key, a rate limit, a server error, a timeout or the network),
 * tinyeval counts the run as couldn't run. Any other error, like a bad model name or a bug in the app, is the reply, so the eval fails.
 */
fun main() = Tinyeval.worker { input -> support.answer(input) }
