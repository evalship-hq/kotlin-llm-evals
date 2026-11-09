package com.example.support

import com.anthropic.errors.AnthropicIoException
import com.anthropic.errors.AnthropicServiceException
import java.io.ByteArrayOutputStream

/**
 * tinyeval's worker: it reads each input up to a NUL byte on stdin, and prints the reply and a NUL byte.
 * The JVM and the client start once per worker, not once per test case. Keep logs off stdout.
 */
fun main() {
    val support = Support()
    val stdin = System.`in`.buffered()
    val input = ByteArrayOutputStream()
    while (true) {
        when (val b = stdin.read()) {
            -1 -> return
            0 -> {
                System.out.write(reply(support, input.toString(Charsets.UTF_8)).toByteArray())
                System.out.write(0)
                System.out.flush()
                input.reset()
            }
            else -> input.write(b)
        }
    }
}

// When the cause isn't the app (no key, a rate limit, a server error, a timeout or the network),
// tinyeval counts the run as couldn't run. Any other API error, like a bad model name, is the reply, so the eval fails.
fun reply(support: Support, input: String): String =
    try {
        support.answer(input)
    } catch (e: AnthropicServiceException) {
        val status = e.statusCode()
        val reason = "anthropic: $status ${e.errorType().map { it.asString() }.orElse("")}".trim()
        if (status == 401 || status == 408 || status == 429 || status >= 500) "#tinyeval couldnt_run $reason\n" else reason
    } catch (e: AnthropicIoException) {
        "#tinyeval couldnt_run ${e.cause?.message ?: e.message}\n" // the cause says "timeout" or "Failed to connect"
    }
