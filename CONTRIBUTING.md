# Contributing to Group16-DevNexus

## Reporting a bug

Search [existing issues](https://github.com/PiKayz/Group16-DevNexus/issues) before opening a new one. Use the **Bug report** form and include the affected report number (1–32, or N/A), release tag or commit, environment, reproduction steps, expected output and actual output. Attach a short sanitized log excerpt when relevant. Never publish credentials, tokens or personal information.

## Tracking and resolving bugs

New bug reports receive the `bug` label. A team member reproduces the issue, links it to the relevant report or CI job, and assigns an owner. Track progress on the project's Kanban board and record each status change. Use the existing `duplicate`, `invalid` or `wontfix` label if closing an issue for one of those reasons, and explain the decision in the issue.

Fixes should use the team's branch and pull-request review process. Add a regression test where practical, run `mvn test`, and check the Docker Compose or integration test path affected by the bug. Link the pull request to the issue, and mark the issue Done once the fix is verified on the intended branch. See [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for team conduct.
