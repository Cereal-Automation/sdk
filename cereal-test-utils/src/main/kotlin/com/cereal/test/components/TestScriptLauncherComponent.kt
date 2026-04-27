package com.cereal.test.components

import com.cereal.sdk.ChildScript
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.component.script.ScriptHandle
import com.cereal.sdk.component.script.ScriptLauncherComponent
import com.cereal.sdk.component.script.ScriptParameters
import com.cereal.test.ComponentProviderFactory
import com.cereal.test.ScriptStatus
import com.cereal.test.TestScriptRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.isAccessible

class TestScriptLauncherComponent(
    private val componentProviderFactory: ComponentProviderFactory,
    private val childScriptConfigurations: Map<Class<out Script<*>>, ScriptConfiguration>?,
) : ScriptLauncherComponent {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Suppress("UNCHECKED_CAST")
    override suspend fun start(
        scriptCls: Class<out Script<*>>,
        parameters: ScriptParameters?,
    ): ScriptHandle {
        scriptCls.getDeclaredAnnotation(ChildScript::class.java)
            ?: throw Exception(
                "Script '${scriptCls.name}' not annotated with ChildScript. Please add the ChildScript annotation to the child script class:" +
                    "@ChildScript(\n" +
                    "    id = \"CHILD_SCRIPT_ID\",\n" +
                    "    name = \"My child script\"\n" +
                    ")",
            )

        val constructor = scriptCls.kotlin.constructors.first()
        constructor.isAccessible = true

        val scriptInstance =
            if (constructor.parameters.size == 1 &&
                constructor.parameters
                    .first()
                    .type.classifier == ScriptParameters::class
            ) {
                constructor.call(parameters) as Script<ScriptConfiguration>
            } else {
                constructor.call() as Script<ScriptConfiguration>
            }

        val scriptRunner = TestScriptRunner(scriptInstance)

        val configuration =
            childScriptConfigurations?.get(key = scriptCls)
                ?: throw Exception(
                    "No test configuration found for script '${scriptCls.simpleName}'. Please add a configuration to " +
                        "${TestComponentProviderFactory::class.java.simpleName}#childScriptConfigurations.",
                )

        scope.launch {
            scriptRunner.run(configuration, componentProviderFactory)
        }

        return TestScriptHandle(scriptRunner)
    }
}

class TestScriptHandle<T : ScriptConfiguration>(
    private val scriptRunner: TestScriptRunner<T>,
) : ScriptHandle {
    override suspend fun getStatus(): ScriptHandle.Status =
        when (scriptRunner.status) {
            ScriptStatus.Idle -> ScriptHandle.Status(1, 0, 0, 0)
            ScriptStatus.Starting, ScriptStatus.Running, ScriptStatus.Finishing -> ScriptHandle.Status(1, 1, 0, 0)
            ScriptStatus.Success -> ScriptHandle.Status(1, 0, 1, 0)
            ScriptStatus.Error -> ScriptHandle.Status(1, 0, 0, 1)
        }
}
