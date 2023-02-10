package com.example.ideaplugindemo.action;

import com.example.ideaplugindemo.model.CurWord;
import com.example.ideaplugindemo.utils.Utils;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
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

        // 获取选中的内容
        String selectedText = selectionModel.getSelectedText();
        int selectionStart = selectionModel.getSelectionStart();
        int selectionEnd = selectionModel.getSelectionEnd();
        if (TextUtils.isEmpty(selectedText)) {
            CurWord curWord = getCurrentWords(editor);
            if (curWord == null || TextUtils.isEmpty(curWord.getText())) {
                return;
            } else {
                selectedText = curWord.getText();
                selectionStart = curWord.getStart();
                selectionEnd = curWord.getEnd();
            }
        }

//        if(!selectionModel.hasSelection(true)) {
//            // 若没有选中内容，则默认选择插入符号位置的整个单词
//            editor.getCaretModel().runForEachCaret(
//                    var1x -> editor.getSelectionModel().selectWordAtCaret(false)
//            );
//        }

        String newText = StringUtils.swapCase(selectedText);
        int finalSelectionStart = selectionStart;
        int finalSelectionEnd = selectionEnd;
        WriteCommandAction.runWriteCommandAction(project,
                () -> editor.getDocument().replaceString(finalSelectionStart, finalSelectionEnd, newText)
        );
    }

    public CurWord getCurrentWords(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int lineNum = document.getLineNumber(caretOffset);
        int lineStartOffset = document.getLineStartOffset(lineNum);
        int lineEndOffset = document.getLineEndOffset(lineNum);
        String lineContent = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        char[] chars = lineContent.toCharArray();
        int start = 0, end = 0, cursor = caretOffset - lineStartOffset;

        if (!Character.isLetter(chars[cursor])) {
            log.warn("Caret not in a word");
            return null;
        }

        for (int ptr = cursor; ptr >= 0; ptr--) {
            if (!Character.isLetter(chars[ptr])) {
                start = ptr + 1;
                break;
            }
        }

        int lastLetter = 0;
        for (int ptr = cursor; ptr < lineEndOffset - lineStartOffset; ptr++) {
            lastLetter = ptr;
            if (!Character.isLetter(chars[ptr])) {
                end = ptr;
                break;
            }
        }
        if (end == 0) {
            end = lastLetter + 1;
        }

        String ret = new String(chars, start, end-start);
        log.info("Selected words: " + ret);
        CurWord curWord = new CurWord();
        curWord.setText(ret);
        curWord.setStart(lineStartOffset + start);
        curWord.setEnd(lineStartOffset + end);
        return curWord;
    }

}
