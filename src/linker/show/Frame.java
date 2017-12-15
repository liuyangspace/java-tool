package linker.show;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class Frame extends JFrame
{
    // 建立主框架
    JFrame linker = new JFrame("linker");
    // 建立主菜单栏
    JMenuBar menubar = new JMenuBar();
    // 文件菜单
    JMenu fileMenu = new JMenu("文件(F)");
    JMenuItem newWindow = new JMenuItem("新窗口(N)");
    JMenuItem openFile = new JMenuItem("打开(O)");
    JMenuItem saveAs = new JMenuItem("另存为(S)");
    JMenuItem quit = new JMenuItem("退出(Q)");
    // Seting
    JMenu setMenu = new JMenu("设置(V)");
    JMenuItem charset = new JMenuItem("字符集");
    String charsetRun = "UTF-8";
    // 帮助菜单
    JMenu helpMenu = new JMenu("帮助(H)");
    JMenuItem help = new JMenuItem("linker 帮助");
    JMenuItem example = new JMenuItem("linker 用例");
    JMenuItem about = new JMenuItem("关于 linker");
    //
    JButton send = new JButton("Send");
    JTextField host = new JTextField(15);
    JTextField port = new JTextField(5);
    JTextField timeout = new JTextField("3",5);
    JTextArea requestText = new JTextArea();
    JTextArea responseText = new JTextArea();

    public Frame()
    {
        linker = initFrame();
        linker.setJMenuBar(initMenu());
        linker.setLayout(new BorderLayout());
        linker.add(requestPanel(),BorderLayout.NORTH);
        linker.add(responsePanel(),BorderLayout.CENTER);
        linker.setVisible(true);
    }

    protected JFrame initFrame()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        linker.setPreferredSize(new Dimension(1000,700));
        linker.setSize(1000,700);
        linker.setLocation((screenSize.width-linker.getWidth())/2,(screenSize.height-linker.getHeight())/2);
        linker.setTitle("linker");
        linker.setResizable(true);
        linker.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // 设置窗口属性
        //think.setLocationRelativeTo(null);
        return linker;
    }

    protected JMenuBar initMenu()
    {
        // 添加菜单栏
        menubar.add(fileMenu);
        fileMenu.setMnemonic('F');
        menubar.add(setMenu);
        setMenu.setMnemonic('S');
        menubar.add(helpMenu);
        helpMenu.setMnemonic('H');
        //
        fileMenu.add(openFile);
        openFile.setMnemonic('O');
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openFile.addActionListener(new Action());
        fileMenu.add(saveAs);
        saveAs.setMnemonic('S');
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveAs.addActionListener(new Action());
        fileMenu.add(quit);
        quit.setMnemonic('Q');
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        quit.addActionListener(new Action());
        //
        setMenu.add(charset);
        charset.setMnemonic('C');
        charset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        charset.addActionListener(new Action());
        // about
        helpMenu.add(help);
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.BUTTON1_MASK));
        help.addActionListener(new Action());
        helpMenu.add(example);
        example.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.BUTTON1_MASK));
        example.addActionListener(new Action());
        helpMenu.add(about);
        about.addActionListener(new Action());
        return menubar;
    }

    protected JPanel requestPanel()
    {
        JPanel requestPanel = new JPanel();
        JPanel requestPanelTop = new JPanel();

        requestPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        requestPanelTop.add(new JLabel("Host:"));
        requestPanelTop.add(host);
        requestPanelTop.add(new JLabel("Port:"));
        requestPanelTop.add(port);
        requestPanelTop.add(new JLabel("Timeout:"));
        requestPanelTop.add(timeout);
        requestPanelTop.add(new JLabel("(秒)"));
        requestPanelTop.add(send);
        send.addActionListener(new Action());

        requestPanel.setLayout(new BorderLayout());
        requestPanel.add(requestPanelTop,BorderLayout.NORTH);
        //requestPanel.add(new JLabel("RequestData:"));
        requestText.setRows(15);
        requestPanel.add(new JScrollPane(requestText),BorderLayout.CENTER);

        return requestPanel;
    }

    protected JPanel responsePanel()
    {
        JPanel responsePanel = new JPanel();
        responsePanel.setLayout(new BorderLayout());
        responsePanel.add(new JLabel("ResponseData:"),BorderLayout.NORTH);
        responsePanel.add(new JScrollPane(responseText),BorderLayout.CENTER);
        return responsePanel;
    }

    // 处理事件的类
    private class Action implements ActionListener {
        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == quit) {
                System.exit(0);
            }else if(e.getSource() == openFile) {
                JFileChooser openFileChooser = new JFileChooser("D://");
                openFileChooser.setDialogTitle("打开");
                openFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                int result = openFileChooser.showOpenDialog(linker);
                File file = openFileChooser.getSelectedFile();
                String filePath = file.getPath();
                try{
                    BufferedReader reader=new BufferedReader(new FileReader(filePath));
                    String readerLine;
                    String content = "";
                    readerLine = reader.readLine();
                    while (readerLine!=null){
                        content = content + (new String(readerLine.getBytes(), charsetRun)) + "\n";
                        readerLine = reader.readLine();
                    }
                    requestText.setText(content);
                    reader.close();
                }catch(IOException event){
                    notice(event.toString());
                }
            }else if(e.getSource() == saveAs) {
                JFileChooser fileChooser = new JFileChooser("D://");
                //urladdress = urlfield.getText().toString().trim();
                //设置弹出对话框类型
                fileChooser.setDialogTitle("另存为...");
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                int result = fileChooser.showSaveDialog(linker);
                //获取文件名
                File file=fileChooser.getSelectedFile();
                String filePath=file.getPath();
                filePath=filePath+".lin";
                if (result == JFileChooser.APPROVE_OPTION) {
                    try{
                        BufferedWriter writer=new BufferedWriter(new FileWriter(filePath));
                        writer.write(requestText.getText());
                        writer.close();
                    }catch(IOException event){
                        notice(event.toString());
                    }
                }
            }else if(e.getSource() == help) {
                helpWindow().setVisible(true);
            }else if(e.getSource() == about){
                notice("Linker version 0.1 " + "\n谢谢您的使用!", "关于 Linker");
            }else if(e.getSource() == example){
                host.setText("127.0.0.1");
                port.setText("80");
                timeout.setText("3");
                requestText.setText("GET /  HTTP/1.1\n" +
                        "Host: 127.0.0.1\n" +
                        "Connection: close\n" +
                        "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) \n" +
                        "Cache-Control: max-age=0\n" +
                        "Accept: application/xml,application/xhtml+xml,text/html;q=0.9,text/plain,image/png,*/*;  \n" +
                        "Accept-Language: zh-CN,zh; \n" +
                        "Accept-Charset: GBK,utf-8;  \n\n");
            }else if(e.getSource() == send){
                try {
                    Socket socket = new Socket(host.getText(),Integer.parseInt(port.getText()));
                    socket.setSoTimeout(Integer.parseInt(timeout.getText())*1000);
                    BufferedWriter netOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    netOut.write(requestText.getText());
                    netOut.flush();
                    String readLine= netIn.readLine(); // 从系统标准输入读入一字符串
                    String content="";
                    while(readLine!=null){
                        content = content + (new String(readLine.getBytes(), charsetRun)) + "\n";
                        readLine = netIn.readLine();
                    }
                    responseText.setText(content);
                    netOut.close();
                    netIn.close();
                    socket.close();
                }catch (Exception exc){
                    notice(exc.toString());
                }
            }else if(e.getSource() == charset){
                setWindow().setVisible(true);
            }
        }
    }

    protected JFrame helpWindow()
    {
        JFrame helpFrame = new JFrame("帮助");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        helpFrame.setSize(600,750);
        helpFrame.setLocation((screenSize.width-helpFrame.getWidth())/2,(screenSize.height-helpFrame.getHeight())/2);
        //
        JTextArea helpText = new JTextArea();
        JScrollPane helpTextPane = new JScrollPane(helpText);
        //helpTextPane.setSize(helpFrame.getWidth(),helpFrame.getHeight());
        //helpTextPane.setLocation(5,5);
        helpFrame.add(helpTextPane);
        helpText.setText("暂无内容!");
        helpText.setEditable(false);
        return  helpFrame;
    }

    protected JFrame setWindow()
    {
        JFrame setFrame = new JFrame("设置");
        setFrame.setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setFrame.setSize(300,200);
        setFrame.setLocation((screenSize.width-setFrame.getWidth())/2,(screenSize.height-setFrame.getHeight())/2);
        setFrame.setLayout(null);

        JLabel charLab = new JLabel("字符集:");
        charLab.setLocation(20,20);
        charLab.setSize(50,25);

        JComboBox charList = new JComboBox();
        charList.setLocation(70,20);
        charList.setSize(200,25);
        charList.addItem("UTF-8");
        charList.addItem("UTF-16");
        charList.addItem("GBK");
        charList.setSelectedItem(charsetRun);

        JButton save = new JButton("应用");
        save.setBackground(new Color(0,200,200));
        save.setLocation(220,120);
        save.setSize(60,25);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                charsetRun = charList.getSelectedItem().toString();
                setFrame.dispose();
            }
        });

        JButton cancel = new JButton("取消");
        cancel.setBackground(new Color(200,200,200));
        cancel.setLocation(150,120);
        cancel.setSize(60,25);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFrame.dispose();
            }
        });

        setFrame.add(charLab);
        setFrame.add(charList);
        setFrame.add(save);
        setFrame.add(cancel);
        return  setFrame;
    }

    protected void notice(String info)
    {
        notice(info,"Linker");
    }
    protected void notice(String info,String title)
    {
        JOptionPane.showMessageDialog(linker, info, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
