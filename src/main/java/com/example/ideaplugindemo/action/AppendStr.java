package com.example.ideaplugindemo.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteActionAware;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.util.Computable;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppendStr extends AnAction {

    private static final Logger log = LoggerFactory.getLogger(AppendStr.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            // 获取项目信息
            Project project = event.getData(PlatformDataKeys.PROJECT);
            DataContext dataContext = event.getDataContext();
            Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

            if (!editor.getSelectionModel().hasSelection(true)) {
                // 如果没有选中内容，则默认选中光标停留的那一行
                editor.getCaretModel().runForEachCaret(caret -> editor.getSelectionModel().selectLineAtCaret());
            }

            // 获取选中的内容
            SelectionModel selectionModel = editor.getSelectionModel();
            String content = selectionModel.getSelectedText();

            if (StringUtils.isBlank(content)) {
                return;
            }

            // 弹出输入框
                String input = Messages.showInputDialog("输入字符串：buf.append(\"&\");", "追加字符串",
                        Messages.getInformationIcon(), "buf.append(\"&\");", new NonEmptyInputValidator());
            if (StringUtils.isBlank(input)) {
                return;
            }
            if (!StringUtils.contains(input, '&')) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            String[] appendArr = input.split("&");
            String[] contentArr = content.split("\n");
            for (String line : contentArr) {
                String str;
                if (line.trim().equals(StringUtils.EMPTY)) {
                    str = "\n";
                } else {
                    String before = line.substring(0, line.indexOf(line.trim()));
                    str = before + appendArr[0] + line.trim() + appendArr[1] + "\n";
                }
                sb.append(str);
            }

            // 更新
                WriteCommandAction.runWriteCommandAction(editor.getProject(),
                        () -> {EditorModificationUtil.insertStringAtCaret(editor, sb.toString(), true, false);}
                );
        } catch (Exception e) {
            log.error("doGenerate.err", e);
            Messages.showErrorDialog(event.getProject(), "请选中需要追加的对象", "错误提示");
        }
    }

}
