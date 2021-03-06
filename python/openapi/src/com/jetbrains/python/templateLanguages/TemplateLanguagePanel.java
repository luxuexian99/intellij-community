package com.jetbrains.python.templateLanguages;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TemplateLanguagePanel extends JPanel {
  private JTextField myTemplatesFolder;
  private JPanel myMainPanel;
  private JLabel myTemplatesFolderLabel;
  private JComboBox myTemplateLanguage;

  private static final String DEFAULT_TEMPLATES_FOLDER = "templates";

  public TemplateLanguagePanel() {
    super(new BorderLayout());
    add(myMainPanel, BorderLayout.CENTER);
    myTemplatesFolderLabel.setLabelFor(myTemplatesFolder);
    FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
    descriptor.withTreeRootVisible(true);
    descriptor.setShowFileSystemRoots(true);
    List<String> templateConfigurations = TemplatesService.getAllTemplateLanguages();
    for (String configuration : templateConfigurations) {
      if (!configuration.equals(TemplatesService.WEB2PY))
        myTemplateLanguage.addItem(configuration);
    }
    myTemplatesFolder.setText(DEFAULT_TEMPLATES_FOLDER);
  }

  public String getTemplatesFolder() {
    return myTemplatesFolder.getText();
  }

  public String getTemplateLanguage() {
    final Object selectedItem = myTemplateLanguage.getSelectedItem();
    return selectedItem != null ? (String)selectedItem : null;
  }

  public void setTemplateLanguage(String language) {
    myTemplateLanguage.setSelectedItem(language);
  }

  public void saveSettings(TemplateSettingsHolder holder) {
    holder.setTemplatesFolder(getTemplatesFolder());
    final Object templateLanguage = getTemplateLanguage();
    holder.setTemplateLanguage((String)templateLanguage);
  }

  public Dimension getLabelSize() {
    return new JBLabel("Template language:").getPreferredSize();
  }
}
