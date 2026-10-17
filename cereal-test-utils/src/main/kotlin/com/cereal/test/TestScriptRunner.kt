package com.cereal.test

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.component.ComponentProvider
import com.cereal.test.util.Logger
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

class TestScriptRunner<T : ScriptConfiguration>(
    private val script: Script<T>,
) {
    private val logger = Logger("TestScriptRunner")
    var status: ScriptStatus = ScriptStatus.Idle
        private set

    suspend fun run(
        configuration: T,
        componentProviderFactory: ComponentProviderFactory,
    ) {
        try {
            val componentProvider = componentProviderFactory.create()
            status = ScriptStatus.Starting
            if (!callOnStart(configuration, componentProvider)) {
                status = ScriptStatus.Error
                return
            }

            status = ScriptStatus.Running
            var taskStatus: ExecutionResult?
            do {
                taskStatus = callLoop(configuration, componentProvider)
                if (taskStatus is ExecutionResult.Loop) {
                    logger.logMessage("Script loop method call delayed by ${taskStatus.delay}ms.")
                    delay(taskStatus.delay)
                }
            } while (taskStatus is ExecutionResult.Loop)

            status = ScriptStatus.Finishing
            callOnFinish(configuration, componentProvider)

            status =
                if (taskStatus is ExecutionResult.Success) {
                    ScriptStatus.Success
                } else {
                    ScriptStatus.Error
                }
        } catch (e: Exception) {
            status = ScriptStatus.Error
            throw e
        }
    }

    private suspend fun callOnStart(
        configuration: T,
        componentProvider: ComponentProvider,
    ): Boolean {
        try {
            var result: Boolean
            logger.logMessage("Calling script onStart method...")
            val elapsedOnStart =
                measureTimeMillis {
                    result = script.onStart(configuration, componentProvider)
                }
            logger.logMessage("Script onStart executed in ${elapsedOnStart}ms")
            if (!result) {
                logger.logMessage("Script returned false in onStart, aborting execution.")
                return false
            }
        } catch (e: Exception) {
            logger.logMessage("Exception while executing onStart method: ${e.message}")
            throw e
        }

        return true
    }

    private suspend fun callLoop(
        configuration: T,
        componentProvider: ComponentProvider,
    ): ExecutionResult? =
        try {
            logger.logMessage("Calling script loop method...")
            var executionResult: ExecutionResult
            val elapsedLoop =
                measureTimeMillis {
                    executionResult =
                        script.execute(configuration, componentProvider) {
                            logger.logMessage("Loop status update: $it")
                        }
                }
            logger.logMessage("Script loop returned '${executionResult.javaClass.simpleName}' status.")
            logger.logMessage("Script loop executed in ${elapsedLoop}ms")

            executionResult
        } catch (e: Exception) {
            logger.logMessage("Exception while executing loop method: ${e.message}")
            throw e
        }

    private suspend fun callOnFinish(
        configuration: T,
        componentProvider: ComponentProvider,
    ) {
        try {
            logger.logMessage("Calling script onFinish method...")
            val elapsedOnFinish =
                measureTimeMillis {
                    script.onFinish(configuration, componentProvider)
                }
            logger.logMessage("Script onFinish executed in ${elapsedOnFinish}ms")
        } catch (e: Exception) {
            logger.logMessage("Exception while executing onFinish method: ${e.message}")
            throw e
        }
    }
}

enum class ScriptStatus {
    Idle,
    Starting,
    Running,
    Finishing,
    Success,
    Error,
}

interface ComponentProviderFactory {
    fun create(): ComponentProvider
}
