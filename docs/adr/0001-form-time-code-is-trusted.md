# 1. Form-time code is trusted

Status: Accepted

## Context

`StateModifier` (`getVisibility`, `getError` and `getPlaceholder`) lets a script run its own code while the user fills
in the configuration form, before any task starts. These hooks read the values the user just typed.

Server-side plugin systems such as Jenkins treat form-time callbacks as an attack surface. A user who can reach the form
can make the callback reach internal hosts (SSRF), so those platforms add permission checks, POST-only endpoints and
network restrictions.

That threat model does not fit Cereal:

- Scripts run in-process in the desktop client on the user's own machine. There is no shared server and no second user
  whose network position the form could borrow.
- The user installed the script, and the same code runs unrestricted when a task starts. Restricting it at form time
  would block nothing that it cannot do a moment later on Start.
- The person typing into the form is the same person whose machine and network the code runs on.

Decision record: Script-AI-Browser-Agent#107, specified in Script-AI-Browser-Agent#110.

## Decision

Form-time code (the `StateModifier` methods) is trusted, in-process code. The SDK and the client apply no network, host
or SSRF restriction to it.

These hooks are called on every configuration change, so they should stay fast and side-effect free. That is a
responsiveness concern, not a security boundary.

## Consequences

- A malicious script is not contained at form time. That is the same as at run time: trust is decided when the user
  installs a script, not per call.
- If scripts ever run on a shared or multi-user server, this decision must be revisited before form-time code runs
  there. Do not add network restrictions to the desktop client on the grounds of SSRF alone.
