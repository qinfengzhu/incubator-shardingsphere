/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
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
 * </p>
 */

package io.shardingsphere.core.parsing.antlr.extractor.statement.handler;

import com.google.common.base.Optional;

import io.shardingsphere.core.parsing.antlr.extractor.statement.handler.result.ColumnDefinitionExtractResult;
import io.shardingsphere.core.parsing.antlr.extractor.statement.handler.result.ExtractResult;
import io.shardingsphere.core.parsing.antlr.extractor.statement.util.ASTUtils;
import io.shardingsphere.core.parsing.antlr.sql.ddl.AlterTableStatement;
import io.shardingsphere.core.parsing.antlr.sql.ddl.ColumnDefinition;
import io.shardingsphere.core.parsing.parser.sql.SQLStatement;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collection;
import java.util.Iterator;

/**
 * Rename column extract handler.
 * 
 * @author duhongjun
 */
public final class RenameColumnExtractHandler implements ASTExtractHandler,ASTExtractHandler1 {
    
    @Override
    public void extract(final ParserRuleContext ancestorNode, final SQLStatement statement) {
        AlterTableStatement alterStatement = (AlterTableStatement) statement;
        Optional<ParserRuleContext> modifyColumnNode = ASTUtils.findFirstChildNode(ancestorNode, RuleName.RENAME_COLUMN);
        if (!modifyColumnNode.isPresent()) {
            return;
        }
        Collection<ParserRuleContext> columnNodes = ASTUtils.getAllDescendantNodes(modifyColumnNode.get(), RuleName.COLUMN_NAME);
        if (2 != columnNodes.size()) {
            return;
        }
        Iterator<ParserRuleContext> columnNodesIterator = columnNodes.iterator();
        String oldName = columnNodesIterator.next().getText();
        String newName = columnNodesIterator.next().getText();
        Optional<ColumnDefinition> oldDefinition = alterStatement.getColumnDefinitionByName(oldName);
        if (oldDefinition.isPresent()) {
            oldDefinition.get().setName(newName);
            alterStatement.getUpdateColumns().put(oldName, oldDefinition.get());
        }
    }

    @Override
    public ExtractResult extract(ParserRuleContext ancestorNode) {
        ColumnDefinitionExtractResult extractResult = new ColumnDefinitionExtractResult();
        Optional<ParserRuleContext> modifyColumnNode = ASTUtils.findFirstChildNode(ancestorNode, RuleName.RENAME_COLUMN);
        if (!modifyColumnNode.isPresent()) {
            return extractResult;
        }
        Collection<ParserRuleContext> columnNodes = ASTUtils.getAllDescendantNodes(modifyColumnNode.get(), RuleName.COLUMN_NAME);
        if (2 != columnNodes.size()) {
            return extractResult;
        }
        Iterator<ParserRuleContext> columnNodesIterator = columnNodes.iterator();
        extractResult.getColumnDefintions().add(new ColumnDefinition(columnNodesIterator.next().getText(), columnNodesIterator.next().getText()));
        return extractResult;
    }
}
