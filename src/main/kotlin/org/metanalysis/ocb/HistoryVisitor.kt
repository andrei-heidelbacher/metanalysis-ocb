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
import org.metanalysis.core.model.EditVariable
import org.metanalysis.core.model.Function
import org.metanalysis.core.model.Project
import org.metanalysis.core.model.ProjectEdit
import org.metanalysis.core.model.RemoveNode
import org.metanalysis.core.model.SourceNode
import org.metanalysis.core.model.SourceUnit
import org.metanalysis.core.model.Type
import org.metanalysis.core.model.Variable
import org.metanalysis.core.model.walkSourceTree
import org.metanalysis.core.repository.Transaction
import kotlin.math.ln

class HistoryVisitor private constructor() {
    private val project = Project.empty()
    private val weight = hashMapOf<String, Double>()
    private val changes = hashMapOf<String, Int>()

    private fun getCost(edit: EditFunction): Int = edit.bodyEdits.size

    private fun getCost(edit: EditVariable): Int = edit.initializerEdits.size

    private fun getCost(edit: ProjectEdit): Int = when (edit) {
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
                val scale = ln(1.0 * changes.getValue(edit.id))
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

    private fun aggregate(variable: Variable): MemberReport =
        MemberReport(variable.name, weight.getValue(variable.id))

    private fun aggregate(function: Function): MemberReport =
        MemberReport(function.signature, weight.getValue(function.id))

    private fun aggregate(type: Type): TypeReport {
        val members = arrayListOf<MemberReport>()
        val types = arrayListOf<TypeReport>()
        for (member in type.members) {
            when (member) {
                is Type -> types += aggregate(member)
                is Function -> members += aggregate(member)
                is Variable -> members += aggregate(member)
            }
        }
        members.sortByDescending(MemberReport::value)
        types.sortByDescending(TypeReport::value)
        return TypeReport(type.name, members, types)
    }

    private fun aggregate(unit: SourceUnit): FileReport {
        val members = arrayListOf<MemberReport>()
        val types = arrayListOf<TypeReport>()
        for (entity in unit.entities) {
            when (entity) {
                is Type -> types += aggregate(entity)
                is Function -> members += aggregate(entity)
                is Variable -> members += aggregate(entity)
            }
        }
        members.sortByDescending(MemberReport::value)
        types.sortByDescending(TypeReport::value)
        return FileReport(unit.path, members, types)
    }

    private fun aggregate(): Report {
        val fileReports = project.sources
            .map(::aggregate)
            .sortedByDescending(FileReport::value)
        return Report(fileReports)
    }

    companion object {
        fun analyze(history: Iterable<Transaction>): Report {
            val visitor = HistoryVisitor()
            history.forEach(visitor::visit)
            return visitor.aggregate()
        }
    }
}
