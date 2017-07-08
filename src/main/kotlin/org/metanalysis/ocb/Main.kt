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

@file:JvmName("Main")

package org.metanalysis.ocb

import org.metanalysis.core.delta.NodeSetEdit.Add
import org.metanalysis.core.delta.NodeSetEdit.Change
import org.metanalysis.core.delta.NodeSetEdit.Remove
import org.metanalysis.core.logging.LoggerFactory
import org.metanalysis.core.model.SourceFile
import org.metanalysis.core.delta.Transaction.Companion.apply
import org.metanalysis.core.project.PersistentProject

val logger = LoggerFactory.getLogger("metanalysis-ocb")

fun main(args: Array<String>) {
    LoggerFactory.loadConfiguration("/logging.properties")
    logger.info("Hello, world from logger!")
    val project = PersistentProject.load()
            ?: error("Project not found!")
    val stats = project.listFiles().associate { path ->
        val model = project.getFileModel(path)
        val history = project.getFileHistory(path)
        var sourceFile = SourceFile()
        var addedMembers = 0
        var removedMembers = 0
        var changedMembers = 0
        for ((_, _, _, transaction) in history) {
            addedMembers += transaction?.nodeEdits
                    ?.filterIsInstance<Add>()
                    ?.size
                    ?: 0
            sourceFile = sourceFile.apply(transaction)
        }
        path to model.nodes.associate { node ->
            node.identifier to 2
        }
    }
    println("Hello, world!")
}
