package com.vv.minerlamp.db.setting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.vv.minerlamp.ButtonAction;
import com.vv.minerlamp.GBC;
import com.vv.minerlamp.util.PropertiesUtil;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

public class DBSettingApp extends JFrame {
	public DBSettingApp() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(DBSettingApp.this);
		} catch (ClassNotFoundException e1) {

			e1.printStackTrace();
		} catch (InstantiationException e1) {

			e1.printStackTrace();
		} catch (IllegalAccessException e1) {

			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {

			e1.printStackTrace();
		}
		;
		try {
			Image icon = ImageIO.read(new File("resources/dbSettingIcon.png"));
			setIconImage(icon);
		} catch (IOException e) {

			e.printStackTrace();
		}

		setTitle("数据库设置");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (screenSize.getWidth() - WIDTH) / 2;
		int y = (int) (screenSize.getHeight() - HEIGHT) / 2;
		setLocation(new Point(x, y));
		add(new Dbpanel(), BorderLayout.CENTER);
	}

	class Dbpanel extends JPanel {
		public Dbpanel() {
			SysConfiguration.init();

			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("数据库设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("数据库IP："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("    端口："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("  用户名："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("    密码："), new GBC(0, 3).setWeight(0, 0));
			ipTxt = new JTextField();
			ipTxt.setText(SysConfiguration.dbIp);
			ipTxt.setPreferredSize(new Dimension(180, 20));
			portTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			portTxt.setValue(SysConfiguration.dbPort);
			portTxt.setPreferredSize(new Dimension(180, 20));
			userTxt = new JTextField();
			userTxt.setText(SysConfiguration.dbUserName);
			userTxt.setPreferredSize(new Dimension(180, 20));
			pwdTxt = new JTextField();
			pwdTxt.setText(SysConfiguration.dbPwd);
			pwdTxt.setPreferredSize(new Dimension(180, 20));
			panel.add(ipTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(portTxt,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(userTxt,
					new GBC(1, 2).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(pwdTxt,
					new GBC(1, 3).setWeight(100, 0).setInsets(5, 3, 0, 0));
			JPanel bottomPanel = new JPanel();
			bottomPanel.add(Util.makeButton(new ButtonAction("保存", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							DBSettingApp.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						String dbIp = ipTxt.getText();
						Integer dbPort = new Integer(portTxt.getText());
						String dbUserName = userTxt.getText();
						String dbPwd = pwdTxt.getText();
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.url",
								SysConfiguration.makeDbUrl(dbIp, dbPort));
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.username", dbUserName);
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.password", dbPwd);

						SysConfiguration.dbIp = dbIp;
						SysConfiguration.dbPort = dbPort;
						SysConfiguration.dbUserName = dbUserName;
						SysConfiguration.dbPwd = dbPwd;

						JOptionPane
								.showMessageDialog(DBSettingApp.this, "保存成功");
					}

				}
			}));
			bottomPanel.add(Util.makeButton(new ButtonAction("连接测试", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Connection con = null;
					String url = SysConfiguration.dbUrl;
					if (url.endsWith(SysConfiguration.dbName)) {
						url = url.substring(0,
								url.indexOf(SysConfiguration.dbName));
					}
					try {
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection(url,
								SysConfiguration.dbUserName,
								SysConfiguration.dbPwd);
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接成功");
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接失败,请检查数据库配置是否正确");
					} catch (SQLException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接失败,请检查数据库配置是否正确");
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(DBSettingApp.this,
								"数据库连接失败,请检查数据库配置是否正确");
					} finally {
						try {
							con.close();
						} catch (SQLException e1) {

							e1.printStackTrace();
						}
					}
				}
			}));
			bottomPanel.add(Util.makeButton(new ButtonAction("创建数据库", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							DBSettingApp.this, "该操作会删除原先的数据库，确定要创建数据库吗", "提示",
							JOptionPane.YES_NO_OPTION);
					Connection con = null;
					Statement sta = null;
					if (selection == JOptionPane.YES_OPTION) {
						Scanner scanner = null;
						StringBuilder sb = new StringBuilder();
						String url = SysConfiguration.dbUrl;
						if (url.endsWith(SysConfiguration.dbName)) {
							url = url.substring(0,
									url.indexOf(SysConfiguration.dbName));
						}
						try {
							Class.forName("com.mysql.jdbc.Driver");
							con = DriverManager.getConnection(url,
									SysConfiguration.dbUserName,
									SysConfiguration.dbPwd);
							sta = con.createStatement();
							scanner = new Scanner(new File("create.sql"));
							while (scanner.hasNextLine()) {
								sb.append(scanner.nextLine());
							}
							String[] statements = sb.toString().split(";");
							for (String statement : statements) {
								sta.addBatch(statement);
							}
							sta.executeBatch();
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"数据库创建成功");
						} catch (FileNotFoundException ex) {

							ex.printStackTrace();
						} catch (SQLException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} catch (ClassNotFoundException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} finally {
							try {
								sta.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								con.close();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}

				}
			}));
			panel.add(bottomPanel, new GBC(0, 4, 2, 1).setWeight(100, 0)
					.setInsets(2));
			JPanel bottomPanel2 = new JPanel();
			bottomPanel2.add(Util.makeButton(new ButtonAction("数据库备份", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser pathChooser = new JFileChooser();
					pathChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int flag = pathChooser.showSaveDialog(DBSettingApp.this);
					if (flag == JFileChooser.APPROVE_OPTION) {
						int execResult = -1;
						try {
							String path = pathChooser.getSelectedFile()
									.getAbsolutePath();
							if (!path.endsWith("\\")) {
								path += "\\";
							}
							path += SysConfiguration.backupFileName;
							execResult = Util.backup(SysConfiguration.dbUrl,
									SysConfiguration.dbUserName,
									SysConfiguration.dbPwd,
									SysConfiguration.dbName, path);
							if (execResult == 0) {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "备份成功");
							} else {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "备份失败，请重启系统后再次尝试");
							}
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"备份失败，请重启系统后再次尝试");
							ex.printStackTrace();
						} catch (InterruptedException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"备份失败，请重启系统后再次尝试");
							ex.printStackTrace();
						}
					}
				}
			}));
			bottomPanel2.add(Util.makeButton(new ButtonAction("数据库还原", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser pathChooser = new JFileChooser();
					pathChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int flag = pathChooser.showSaveDialog(DBSettingApp.this);
					if (flag == JFileChooser.APPROVE_OPTION) {
						try {
							int execResult = -1;
							String path = pathChooser.getSelectedFile()
									.getAbsolutePath();
							execResult = Util.load(SysConfiguration.dbUrl,
									SysConfiguration.dbUserName,
									SysConfiguration.dbPwd,
									SysConfiguration.dbName, path);
							if (execResult == 0) {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "还原成功");
							} else {
								JOptionPane.showMessageDialog(
										DBSettingApp.this, "还原失败，请重启系统后再次尝试");
							}
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"还原失败，请重启系统后再次尝试");
							ex.printStackTrace();
						} catch (InterruptedException ex) {
							JOptionPane.showMessageDialog(DBSettingApp.this,
									"还原失败，请重启系统后再次尝试");
							ex.printStackTrace();
						}
					}
				}
			}));
			panel.add(bottomPanel2, new GBC(0, 5, 2, 1).setWeight(100, 0)
					.setInsets(2));
			add(panel, Util.fillParentPanel());
		}

		private JTextField ipTxt;
		private JFormattedTextField portTxt;
		private JTextField userTxt;
		private JTextField pwdTxt;

	}

	public static void main(String[] args) {
		DBSettingApp dbSettingApp = new DBSettingApp();
		dbSettingApp.setVisible(true);
	}

	private static final int WIDTH = 360;
	private static final int HEIGHT = 250;
}
