package cc.arduino.packages.formatter;

import static processing.app.I18n._;

import java.io.File;
import java.io.IOException;

import javax.swing.text.BadLocationException;

import processing.app.Base;
import processing.app.Editor;
import processing.app.helpers.FileUtils;
import processing.app.syntax.SketchTextArea;
import processing.app.tools.Tool;

public class AStyle implements Tool {

  private static String FORMATTER_CONF = "formatter.conf";

  private final AStyleInterface aStyleInterface;
  private final String formatterConfiguration;
  private Editor editor;

  public AStyle() {
    this.aStyleInterface = new AStyleInterface();
    File customFormatterConf = Base.getSettingsFile(FORMATTER_CONF);
    File defaultFormatterConf = new File(Base.getContentFile("lib"), FORMATTER_CONF);

    File formatterConf;
    if (customFormatterConf.exists()) {
      formatterConf = customFormatterConf;
    } else {
      formatterConf = defaultFormatterConf;
    }
    String formatterConfiguration = "";

    try {
      formatterConfiguration = FileUtils.readFileToString(formatterConf);
    } catch (IOException e) {
      // noop
    }
    this.formatterConfiguration = formatterConfiguration;
  }

  @Override
  public void init(Editor editor) {
    this.editor = editor;
  }

  @Override
  public void run() {
    String originalText = editor.getText();
    String formattedText = aStyleInterface.AStyleMain(originalText, formatterConfiguration);

    if (formattedText.equals(originalText)) {
      editor.statusNotice(_("No changes necessary for Auto Format."));
      return;
    }

    SketchTextArea textArea = editor.getTextArea();
    editor.setText(formattedText);
    editor.getSketch().setModified(true);
    
    try {
      int line = textArea.getLineOfOffset(textArea.getCaretPosition());
      int lineOffset = textArea.getCaretPosition() - textArea.getLineStartOffset(line);
      textArea.setCaretPosition(Math.min(textArea.getLineStartOffset(line) + lineOffset, textArea.getLineEndOffset(line) - 1));
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    // mark as finished
    editor.statusNotice(_("Auto Format finished."));
  }

  @Override
  public String getMenuTitle() {
    return _("Auto Format");
  }

}
