package com.example.ideaplugindemo.action;

import com.example.ideaplugindemo.utils.Utils;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ToggleCaseAction extends AnAction {

    private static final Logger log = LoggerFactory.getLogger(ToggleCaseAction.class);

    @Override
    public void actionPerformed(AnActionEvent event) {
        try {
            doGenerate(event);
        } catch (Exception e) {
            log.error("doGenerate.err", e);
            Messages.showErrorDialog(event.getProject(), "请选中需要转换的对象", "错误提示");
        }
    }

    private void doGenerate(AnActionEvent event) {
        // 获取项目信息
        Project project = event.getData(PlatformDataKeys.PROJECT);
        DataContext dataContext = event.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        // 校验
        assert editor != null;
        final SelectionModel selectionModel = editor.getSelectionModel();

        // 获取需要转换的对象内容
        if(!selectionModel.hasSelection(true)) {
            // 若没有选中内容，则默认选择插入符号位置的整个单词
            editor.getCaretModel().runForEachCaret(
                    var1x -> editor.getSelectionModel().selectWordAtCaret(false)
            );
        }

        // 获取选中的内容
        String selectedText = selectionModel.getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }

        String newText = StringUtils.swapCase(selectedText);
        WriteCommandAction.runWriteCommandAction(project,
                () -> editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), newText)
        );
    }

}
