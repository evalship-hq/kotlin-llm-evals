# LLM evals for Kotlin

A Kotlin support bot whose AI call is tested on every pull request with [tinyeval](https://tinyeval.com).

## Run it

Clone this repo or use it as a template. You need JDK 21; `./gradlew` downloads Gradle for you.

```sh
export ANTHROPIC_API_KEY=sk-ant-...
./gradlew -q installDist tinyeval
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
- `src/main/kotlin/com/example/support/EvalWorker.kt`: starts the bot once and answers each test case tinyeval sends it, with tinyeval's worker helper.
- `evals/support.sh`: three test cases, each checked by a regex, a judge or both.
- `.github/workflows/evals.yml`: builds the app and runs the evals on every pull request and push to main.
- `build.gradle.kts` and `gradlew`: the Gradle build, with the official Anthropic Java SDK, the [tinyeval package](https://central.sonatype.com/artifact/com.evalship/tinyeval) and the `tinyeval` task that runs it.
- `.github/dependabot.yml`: weekly pull requests for new versions of tinyeval, the SDK and the actions.

## Use it for your app

1. Replace `answer` with your AI call. Keep the worker, so your app starts once per worker and not once per test case.
2. Write your test cases in `evals/`. The [check lines](https://github.com/evalship-hq/tinyeval#writing-evals) cover regexes, JSON, your own graders and a model judge.

When the API call fails for a reason that isn't your code, such as a missing key, a rate limit or a timeout, tinyeval counts the test case as couldn't run, apart from the failures. A bug in your code becomes the reply, so the eval fails. For other cases your code can't run, throw `CouldntRun("reason")`.

In a Ktor or Spring Boot app, build your services in the worker's lazy value and call your service in the lambda. Set up logging there too: a logger set up before the worker starts still writes to stdout, which carries the replies. In an Android app, keep the AI call in a plain Kotlin/JVM module, so the worker can call it without a device.

## In CI

Add `ANTHROPIC_API_KEY` as a repository secret. The bot and tinyeval's judge both use it. The workflow builds the worker, then runs each test case 3 times. A failing eval fails the job, so it can gate merges. The results upload as the `eval-results` artifact. Without the secret, the eval step is skipped with a notice.

## Updating

Dependabot opens a pull request each week when tinyeval, the Anthropic SDK or an action has a new version. Dependabot pull requests get no secrets, so their eval step is skipped with the notice. To update by hand, change the version in `build.gradle.kts`.

## Compare pull requests with main

A fresh CI run has no earlier run to compare with. [evalship](https://evalship.com) reads `eval-results.json`, compares each pull request with main, tells flaky evals from real drops and comments on the pull request.

## License

MIT
