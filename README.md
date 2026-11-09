# LLM evals for Kotlin

A Kotlin support bot whose AI call is tested on every pull request with [tinyeval](https://tinyeval.com).

## Run it

Clone this repo or use it as a template. You need JDK 21; `./gradlew` downloads Gradle for you.

```sh
export ANTHROPIC_API_KEY=sk-ant-...
./gradlew -q installDist   # again after each change to the Kotlin code
./tinyeval
```

```
support
  ✓ refund after the window 9.0s
  ✓ missing package 6.9s
  ✓ refund inside the window 9.8s

All 3 evals passed (eval-results.json)
```

## What's here

- `src/main/kotlin/com/example/support/Support.kt`: the bot. `answer(message)` sends the message to Claude with the system prompt.
- `prompts/support.md`: the system prompt.
- `src/main/kotlin/com/example/support/EvalWorker.kt`: starts the bot once and answers each test case tinyeval sends it.
- `evals/support.sh`: three test cases, each checked by a regex, a judge or both.
- `tinyeval`: the eval runner, one bash file.
- `.github/workflows/evals.yml`: builds the app and runs the evals on every pull request and push to main.
- `build.gradle.kts` and `gradlew`: the Gradle build, with the official Anthropic Java SDK.

## Use it for your app

1. Replace `answer` with your AI call. Keep the worker, so your app starts once per worker and not once per test case.
2. Write your test cases in `evals/`. The [check lines](https://github.com/evalship-hq/tinyeval#writing-evals) cover regexes, JSON, your own graders and a model judge.

When the API call fails for a reason that isn't your code, such as a missing key, a rate limit or a timeout, the worker replies `#tinyeval couldnt_run`. tinyeval then counts the test case as couldn't run, not as failed.

In a Ktor or Spring Boot app, build your services once in the worker's `main` and call your service in the loop. Keep logs off stdout, since stdout carries the replies. In an Android app, keep the AI call in a plain Kotlin/JVM module, so the worker can call it without a device.

## In CI

Add `ANTHROPIC_API_KEY` as a repository secret. The bot and tinyeval's judge both use it. The workflow builds the worker, then runs each test case 3 times. A failing eval fails the job, so it can gate merges. The results upload as the `eval-results` artifact. Without the secret, the eval step is skipped with a notice.

## Compare pull requests with main

A fresh CI run has no earlier run to compare with. [evalship](https://evalship.com) reads `eval-results.json`, compares each pull request with main, tells flaky evals from real drops and comments on the pull request.

## License

MIT
