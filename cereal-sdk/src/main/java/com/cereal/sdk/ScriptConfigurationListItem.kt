package com.cereal.sdk

/**
 * Marker interface for the element type of a **complex list** configuration item — a configuration
 * method returning `List<T>` where `T` is an interface you define.
 *
 * Declare each field of the record as a parameterless function annotated with [ScriptConfigurationItem].
 * The client reflects over the interface, renders one editable card per row with the widget matching
 * each field's type, validates each field individually, and hands your script a `List<T>` of typed
 * objects — no delimiters, no hand-parsing.
 *
 * ```kotlin
 * interface Target : ScriptConfigurationListItem {
 *     @ScriptConfigurationItem(keyName = "sku", name = "SKU", description = "Product identifier")
 *     fun sku(): String
 *
 *     @ScriptConfigurationItem(keyName = "qty", name = "Quantity", description = "How many to buy")
 *     fun qty(): Int
 *
 *     @ScriptConfigurationItem(keyName = "notify", name = "Notify", description = "Notify on success")
 *     fun notify(): Boolean?
 * }
 *
 * interface MyConfiguration : ScriptConfiguration {
 *     @ScriptConfigurationItem(keyName = "targets", name = "Targets", description = "Products to purchase")
 *     fun targets(): List<Target>
 * }
 * ```
 *
 * **Permitted field types:** `String`, `Int`, `Float`, `Double`, `Boolean`, enums, and their nullable
 * forms. `Proxy`, `RandomProxy`, nested lists and nested record types are rejected when the client
 * loads the script.
 *
 * **Cardinality:** a non-nullable `List<T>` is guaranteed to hold at least one row when your script
 * runs. A nullable `List<T>?` arrives as `null` when the user supplied no rows, so "not configured"
 * is distinguishable from "configured as empty". A nullable field within a row arrives as `null` when
 * the user left it blank.
 *
 * **Cardinality limits** (a minimum or maximum number of rows) are expressed through the list item's
 * own [ScriptConfigurationItem.stateModifier], which can count the rows it reads back via
 * [com.cereal.sdk.statemodifier.ScriptConfigValue.ListScriptConfigValue].
 *
 * **Concurrency:** the whole list is a single configuration value for a single script run, and every
 * task receives all of it. A complex list never fans out into one task per row — that remains the job
 * of `valuePerTask` / Task data. Partitioning the rows across tasks is your script's business.
 *
 * **Rejected when the client loads the script**, each with a message naming the fix:
 *  - [ScriptConfigurationItem.valuePerTask] on a complex-list item — use Task data instead.
 *  - [ScriptConfigurationItem.isScriptIdentifier] on a complex-list item.
 *  - A [ScriptConfigurationItem.stateModifier] other than the default on a field inside a record —
 *    visibility and validation belong to the list item itself.
 *  - A default implementation on a complex-list item; pre-seeded rows are not supported. Supply values
 *    in your own unit tests by overriding the method in your test configuration object.
 *  - An unsupported field type inside a record, or two fields sharing a [ScriptConfigurationItem.keyName].
 *
 * Field keys are namespaced under the list item's own key, so a record field may reuse the key name of
 * a top-level configuration item.
 *
 * Requires client SDK version 1.11.0 or newer; older clients prompt the user to update.
 */
interface ScriptConfigurationListItem
