# 1. Form-time code is trusted

Status: Accepted

## Context

`FieldOptions.load` lets a script run its own code while the user fills in the configuration form, before any task
starts. A typical `load` takes a URL and a key the user just typed and calls that server to list choices (for example
an LLM provider's model list).

Server-side plugin systems such as Jenkins treat this kind of form-time callback as an attack surface. A user who can
reach the form can point the callback at internal hosts (SSRF), so those platforms add permission checks, POST-only
endpoints and network restrictions.

That threat model does not fit Cereal:

- Scripts run in-process in the desktop client on the user's own machine. There is no shared server and no second user
  whose network position the form could borrow.
- The user installed the script, and the same code runs unrestricted when a task starts. It can already open any
  connection it wants. Restricting it at form time would block nothing that it cannot do a moment later on Start.
- The person typing into the form is the same person whose machine and network the code runs on.

Decision record: Script-AI-Browser-Agent#107, specified in Script-AI-Browser-Agent#110.

## Decision

Form-time code (`FieldOptions.load` and the `StateModifier` methods) is trusted, in-process code. The SDK and the client
apply no network, host or SSRF restriction to it.

The client owns one shared form-time execution model, which exists for responsiveness and stale-result handling, not
for security:

- `load` runs only on an explicit gesture: opening the dropdown or pressing its refresh. Never on a keystroke, blur, Save
  or Start.
- It runs off the UI thread as a cancellable coroutine, with a fixed timeout of about 15 seconds (a client constant).
- At most one call per field is in flight. A new trigger, an edit to a `dependsOn` field, or closing the form cancels it.
- The client does not skip the call when `dependsOn` fields are empty; `load` decides what an empty value means.
- `load` receives only its `dependsOn` fields. That limits coupling between fields; it is not a sandbox.

## Consequences

- Script authors can call any endpoint from `load`, including localhost servers such as LM Studio or Ollama.
- A malicious script is not contained at form time. That is the same as at run time: trust is decided when the user
  installs a script, not per call.
- If scripts ever run on a shared or multi-user server, this decision must be revisited before form-time code runs
  there. Do not add network restrictions to the desktop client on the grounds of SSRF alone.
