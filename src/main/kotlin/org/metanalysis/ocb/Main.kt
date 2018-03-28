/*
 * Copyright 2017 Andrei Heidelbacher <andrei.heidelbacher@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.metanalysis.ocb

import org.metanalysis.core.repository.PersistentRepository
import org.metanalysis.core.serialization.JsonModule
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.ExecutionException
import picocli.CommandLine.RunAll
import picocli.CommandLine.defaultExceptionHandler
import kotlin.system.exitProcess

@Command(
    name = "metanalysis-ocb",
    mixinStandardHelpOptions = true,
    version = ["0.2"],
    description = [],
    showDefaultValues = true
)
class Main : Runnable {
    override fun run() {
        val analyzer = HistoryAnalyzer()
        val repository = PersistentRepository.load()
            ?: error("Repository not found!")
        val report = analyzer.analyze(repository.getHistory())
        JsonModule.serialize(System.out, report.files)
    }
}

fun main(vararg args: String) {
    val cmd = CommandLine(Main())
    val exceptionHandler = defaultExceptionHandler().andExit(1)
    try {
        cmd.parseWithHandlers(RunAll(), exceptionHandler, *args)
    } catch (e: ExecutionException) {
        System.err.println(e.message)
        e.printStackTrace(System.err)
        exitProcess(1)
    }
}
