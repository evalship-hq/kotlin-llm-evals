# Evals for the support bot. Run them with ./gradlew -q installDist tinyeval.
# The worker starts the JVM and the client once, and answers every test case sent to it.
worker build/install/support/bin/support

test_case "refund after the window" "Can I still return my shoes? They arrived 40 days ago."
  target src/main/kotlin/com/example/support/Support.kt#answer
  matches "30[- ]day"   # "30 days" and "30-day window" are both right
  judge "Says a refund isn't possible anymore, politely"

test_case "missing package" "My package never showed up and I'm getting really annoyed."
  target src/main/kotlin/com/example/support/Support.kt#answer
  matches "order (number|id|#)"
  judge "Asks for the order number before promising a refund or resend"

test_case "refund inside the window" "My shoes arrived last week and they don't fit. Can I send them back?"
  target src/main/kotlin/com/example/support/Support.kt#answer
  judge "Says they can return them for a refund"
