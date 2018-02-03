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

import org.metanalysis.core.model.AddNode
import org.metanalysis.core.model.EditFunction
import org.metanalysis.core.model.EditType
import org.metanalysis.core.model.EditVariable
import org.metanalysis.core.model.Project
import org.metanalysis.core.model.ProjectEdit
import org.metanalysis.core.model.RemoveNode
import org.metanalysis.core.model.SourceNode
import org.metanalysis.core.model.SourceNode.Companion.ENTITY_SEPARATOR
import org.metanalysis.core.model.children
import org.metanalysis.core.model.walkSourceTree
import org.metanalysis.core.repository.Repository
import org.metanalysis.core.repository.Transaction
import kotlin.math.log2

class HistoryVisitor private constructor() {
    private val project = Project.empty()
    private val weight = hashMapOf<String, Double>()
    private val changes = hashMapOf<String, Int>()

    private fun getCost(edit: EditType): Int =
        (if (edit.modifierEdits.isNotEmpty()) MODIFIER_COST else 0) +
            (if (edit.supertypeEdits.isNotEmpty()) SUPERTYPE_COST else 0)

    private fun getCost(edit: EditFunction): Int =
        (if (edit.modifierEdits.isNotEmpty()) MODIFIER_COST else 0) +
            (if (edit.parameterEdits.isNotEmpty()) PARAMETER_COST else 0) +
            edit.bodyEdits.size * LINE_COST

    private fun getCost(edit: EditVariable): Int =
        (if (edit.modifierEdits.isNotEmpty()) MODIFIER_COST else 0) +
            edit.initializerEdits.size * LINE_COST

    private fun getCost(edit: ProjectEdit): Int = when (edit) {
        is EditType -> getCost(edit)
        is EditFunction -> getCost(edit)
        is EditVariable -> getCost(edit)
        else -> 0
    }

    private fun visit(edit: ProjectEdit) {
        when (edit) {
            is AddNode -> {
                for (node in edit.node.walkSourceTree()) {
                    weight[node.id] = 0.0
                    changes[node.id] = 1
                }
            }
            is RemoveNode -> {
                val nodes = project.get<SourceNode>(edit.id).walkSourceTree()
                for (node in nodes) {
                    weight -= node.id
                    changes -= node.id
                }
            }
            else -> {
                val scale = log2(1.0 * changes.getValue(edit.id))
                val addedWeight = scale * getCost(edit)
                weight[edit.id] = weight.getValue(edit.id) + addedWeight
                changes[edit.id] = changes.getValue(edit.id) + 1
            }
        }
        project.apply(edit)
    }

    private fun visit(transaction: Transaction) {
        for (edit in transaction.edits) {
            visit(edit)
        }
    }

    private fun aggregate() {
        for (node in project.sourceTree.sortedByDescending { it.id.length }) {
            for (child in node.children) {
                weight[node.id] =
                    weight.getValue(node.id) + weight.getValue(child.id)
            }
        }
    }

    companion object {
        private const val MODIFIER_COST: Int = 10
        private const val SUPERTYPE_COST: Int = 25
        private const val PARAMETER_COST: Int = 5
        private const val LINE_COST: Int = 1

        fun visit(repository: Repository): Map<String, Double> {
            val visitor = HistoryVisitor()
            for (transaction in repository.getHistory()) {
                visitor.visit(transaction)
            }
            visitor.aggregate()
            return visitor.weight.filterKeys { ENTITY_SEPARATOR !in it }
        }

        fun visit(repository: Repository, path: String): Map<String, Double> {
            val visitor = HistoryVisitor()
            for (transaction in repository.getHistory()) {
                visitor.visit(transaction)
            }
            visitor.aggregate()
            return visitor.weight.filterKeys { it.startsWith(path) }
        }
    }
}
