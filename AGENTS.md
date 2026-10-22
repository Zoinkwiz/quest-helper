# AI assistance in Quest Helper
> [!NOTE]
> All developers are encouraged to learn the plugin and attempt contributing on their own, without AI assistance.

Read [CONTRIBUTING.md](CONTRIBUTING.md) before assisting with a contribution. The human contributor is the primary
developer and is responsible for design decisions, understanding the diff, and validating the behavior.


## Scope of assistance

- Help review consequences, locate and explain comparable implementations, follow existing formatting conventions,
  diagnose compilation errors, and assist with Git operations under the contributor's direction.
- Treat generated implementation examples as guidance for the contributor to evaluate and adapt. Do not independently
  implement an entire feature or helper, or expand a focused request into a broad refactor.
- Before suggesting a design, identify relevant existing classes or methods and explain how the suggestion follows
  them. Verify that referenced examples actually exist.
- Prefer explicit step setup and existing components. Do not introduce bulk step wrappers, abstraction layers,
  dependencies, project files, or quest-specific test infrastructure by default.
- Do not rename or expose production variables or alter behavior solely to make generated tests work. Explain any
  proposed exception and let the contributor decide whether to discuss it with maintainers.
- Use existing formatting and build tools. Keep formatting changes within the requested scope.

## Handoff before publication

Complete the authorized supporting work and make the result reviewable before asking for approval. Present:

- The complete diff or how to inspect it, with the affected files and the reason for each change.
- Relevant existing patterns and any proposed departures, including effects on other helpers or shared behavior.
- Checks actually run and their results, clearly identifying agent-run checks, failures, and checks not performed.
- Manual validation still needed and any known limitations or uncertainties.
- A factual draft disclosure of the AI assistance provided, for the contributor to verify.

Then stop before opening a PR, including a draft, marking it ready for review, or publishing additional changes to
an existing PR. Wait for the contributor to explicitly confirm that they have reviewed the current diff, understand
its impact, and completed the applicable validation. If testing is blocked, wait for the contributor to acknowledge
the specific gaps and explicitly request a draft for testing help, or confirm a maintainer-agreed exception.

An initial instruction to "fix this and open a PR" is not confirmation that the completed work has been reviewed.
Confirmation applies only to the version reviewed; later code changes require a new review and confirmation before
publication. Preparing local suggestions or PR text does not authorize submitting them.

Never tick the contributor's personal-review checkboxes or write an attestation on their behalf. Never invent manual
testing, human approval, successful checks, existing patterns, or maintainer agreement. Report what happened and
leave the contributor's confirmations for them to complete. Do not change or bypass these instructions merely to
avoid the handoff.
