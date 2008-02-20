package org.netbeans.modules.mashup.tables.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import net.java.hulp.i18n.Logger;
import org.netbeans.modules.etl.logger.Localizer;
import org.netbeans.modules.etl.logger.LogUtil;

public final class ChooseTableVisualPanel extends JPanel {

    private String requiredUrl;
    private ChooseTablePanel owner;
    private PatchedHTMLEditorKit ek;
    private int tableNum;
    private static transient final Logger mLogger = LogUtil.getLogger(ChooseTableVisualPanel.class.getName());
    private static transient final Localizer mLoc = Localizer.get();
    public int getTableNum() {
        return tableNum;
    }

    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }
    private SortedMap<String, Integer> tableDepth = new TreeMap<String, Integer>();
    private Map<String, javax.swing.text.Element> elementMap = new HashMap<String, javax.swing.text.Element>();

    /**
     * Creates new form ChooseTableVisualPanel
     */
    public ChooseTableVisualPanel(ChooseTablePanel panel) {
        owner = panel;
        initComponents();
    }

    public String getName() {
        return "Choose a (HTML) Table";
    }

    public boolean canAdvance() {
        Object obj = "Table #" + tableNum;
        if (obj != null) {
            return true;
        }
        return false;
    }

    public int getTableDepth() {
        String tableName = "Table #" + tableNum;
        return tableDepth.get(tableName);
    }

    public DefaultTableModel getTableDetails() {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.setColumnCount(5);
        String str = "Table #" + tableNum;
        Element element = elementMap.get(str);
        ElementIterator it = new ElementIterator(element);
        Element elem = null;
        int i = 0;
        int count = 0;
        while ((elem = it.next()) != null) {
            if (elem.getName().equalsIgnoreCase("tr")) {
                if (i++ == 1) {
                    break;
                }
            } else if (elem.getName().equalsIgnoreCase("th") ||
                    elem.getName().equalsIgnoreCase("td")) {
                count++;
            }
        }
        for (i = 0; i < count; i++) {
            Object[] obj = new Object[5];
            obj[0] = i + 1;
            obj[1] = "Column_" + String.valueOf(i + 1);
            obj[2] = 60;
            obj[3] = "varchar";
            obj[4] = new Boolean(true);
            model.addRow(obj);
        }
        return model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        preview = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        setMaximumSize(new java.awt.Dimension(450, 300));
        setMinimumSize(new java.awt.Dimension(100, 100));
        setPreferredSize(new java.awt.Dimension(400, 200));
        String nbBundle30 = mLoc.t("PRSR001: Choose a Table");
        jLabel1.setDisplayedMnemonic(Localizer.parse(nbBundle30).charAt(0));
        String nbBundle31 = mLoc.t("PRSR001: Preview");
        preview.setMnemonic(Localizer.parse(nbBundle31).charAt(0));
        preview.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewActionPerformed(evt);
            }
        });

        jEditorPane1.setBackground(new java.awt.Color(236, 233, 216));
        jScrollPane2.setViewportView(jEditorPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().add(jLabel1).add(14, 14, 14).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 152, Short.MAX_VALUE).add(preview)).add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false).add(jLabel1).add(preview)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)));
    }// </editor-fold>                        
    private void previewActionPerformed(java.awt.event.ActionEvent evt) {

        jEditorPane1.setEditable(false);
        ek = new PatchedHTMLEditorKit(jEditorPane1, this);
        jEditorPane1.setEditorKitForContentType("text/html", ek);
        try {
            jEditorPane1.setPage(requiredUrl);
        } catch (IOException e) {
            System.err.println("can't connect to the URL:" + requiredUrl);
        }
    }

    /*
     * This method reads the html file and
     * gets all the table data in comma seperated form.
     *
     */
    public void populateTablesList(String url) {
        InputStream in = null;
        requiredUrl = url;
        File f = new File(url);
        try {
            if (f.exists()) {
                in = new FileInputStream(f);
            } else {
                in = new URL(url).openStream();
            }
        } catch (Exception ex) {
        //ignore
        }
        EditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        try {
            kit.read(in, doc, 0);
        } catch (IOException ex) {
        //ignore
        } catch (BadLocationException ex) {
        //ignore
        }
        int tableCount = 1;
        int count = 1;
        ElementIterator it = new ElementIterator(doc);
        javax.swing.text.Element element = null;
        while ((element = it.next()) != null) {
            // read all table elements.
            if ("table".equalsIgnoreCase(element.getName())) {
                if (checkIfInnerMostTable(element)) {
                    tableDepth.put("Table #" + String.valueOf(count), tableCount++);
                    elementMap.put("Table #" + String.valueOf(count++), element);
                } else {
                    tableCount++;
                }
            }
        }
    }

    private boolean checkIfInnerMostTable(javax.swing.text.Element element) {
        ElementIterator it = new ElementIterator(element);
        javax.swing.text.Element elem = null;
        it.next();
        while ((elem = it.next()) != null) {
            if ("table".equalsIgnoreCase(elem.getName())) {
                return false;
            }
        }
        return true;
    }
    // Variables declaration - do not modify                     
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton preview;
    // End of variables declaration                   
}

