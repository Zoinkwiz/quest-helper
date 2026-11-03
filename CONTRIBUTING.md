# Contributing to Quest Helper

Contributions should be led by a person who understands the change, validates it, and takes responsibility for it.
This applies to all contributions, whether or not AI tools were used. You do not need to understand the entire project,
but you should be able to explain the code you change and its likely effects on other helpers and shared behavior.

## Getting started

Read the [Contribution Guide](https://github.com/Zoinkwiz/quest-helper/wiki/Contribution-guide) for development setup
and guidance on writing helpers. Find a comparable helper or shared component before choosing an implementation.
Use the repository's [EditorConfig](.editorconfig) for formatting and import conventions.

## Keep changes consistent and focused

- Prefer the explicit step and requirement setup used by comparable helpers. Avoid introducing loops that apply bulk
  wrappers to steps unless there is a clear need that the existing patterns do not address.
- Reuse existing components. New abstraction layers, dependencies, project files, and test infrastructure should have
  a concrete purpose proportional to the change.
- Follow the existing testing approach. Do not add quest-specific test suites by default or rename variables, expose
  fields, or change production behavior solely to accommodate a generated test. Focused regression tests are welcome
  when they protect meaningful behavior; explain any production changes needed to support them.
- Keep each PR focused on one purpose. Separate unrelated cleanup, formatting, and framework changes from quest work.
  Discuss large changes or changes to shared behavior with maintainers early, and split independently reviewable work.

These are defaults, not blanket bans. Different designs and larger PRs can be appropriate. Explain why a departure is
needed, what existing approach you considered, and the maintenance cost. Maintainers can agree to exceptions.

## AI assistance

> [!NOTE]
> All developers are encouraged to attempt to learn the plugin and submit code changes on their own, without the use of AI assistance.

AI may support a contributor with:

- Reviewing changes for possible regressions and unintended consequences.
- Finding and explaining how existing helpers implement a similar design. Generated examples should guide the
  contributor's own implementation decisions and must be checked, adapted where needed, and tested by the contributor.
- Formatting code and organizing imports to match the repository's existing conventions.
- Diagnosing compilation errors and suggesting corrections for the contributor to evaluate.
- Explaining or assisting with Git commands, branch preparation, diffs, commits, and cherry-picks.

The contributor must remain the primary developer. Do not delegate an entire feature or helper to an agent and submit
its output without understanding and validating it. Passing automated tests or receiving an AI review does not replace
personal review of the complete change.

Disclose AI assistance in the PR even if no generated code was retained. Identify the tool, its specific tasks, the
affected code or decisions, suggestions retained, and how you verified them. A brief factual account is enough;
private chat transcripts are not required. Write "None" if no AI assistance was used.

## Before submitting a pull request

1. Review the complete intended diff against the target branch, including new files. Read the actual changes rather
   than relying on an AI-generated summary. Remove unrelated edits and accidental commits.
2. Compare the implementation with relevant existing helpers. Be prepared to explain the changed behavior and, where
   relevant, step priority, inventory and varbit conditions, sidebar links, and effects on shared components.
3. For gameplay or UI changes, manually exercise the affected behavior in the client, including relevant alternate
   states. Record what you tried and observed. Compilation and automated tests do not establish that gameplay works.
   For documentation-only changes, review the text and references; gameplay testing is not applicable.
4. Complete the PR description and personally confirm your review. Record AI assistance accurately, and distinguish
   checks performed by an agent from manual checks performed by a person. Include ActionLogger log files if possible.

If testing is blocked, state exactly what remains untested and discuss a validation plan with a maintainer. A person
may submit a personally reviewed draft to request help with testing, with those gaps clearly stated. Do not mark it
ready for review until the relevant validation is complete or a maintainer has agreed to an exception.

Review subsequent changes and update the validation and disclosure before publishing them to an existing PR.
Confirmation of an earlier version does not cover later code changes.

## Human approval before AI publishes a PR

An agent must present the final diff, checks performed, and remaining uncertainties, then wait for the contributor
to explicitly confirm review and validation of that version before opening a PR, including a draft, or publishing
further changes to an existing PR. For a draft seeking testing help, the contributor must explicitly acknowledge the
testing gaps and request that draft. An initial request to implement something and create a PR is not this confirmation.

Only the contributor may complete the personal-review attestations. An agent must not claim that a person reviewed
or tested something without that person's confirmation. See [AGENTS.md](AGENTS.md) for the agent handoff instructions.
