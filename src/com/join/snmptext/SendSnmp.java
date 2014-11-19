package com.join.snmptext;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.smi.*;

import java.awt.*;

import javax.swing.*;

import java.net.*;

public class SendSnmp extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;

	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;

	private JTextField ipAddress = null;
	private JTextField Port = null;
	private JTextField Oid = null;
	private JTextArea responseText = null;
	private JScrollPane jsp = null;

	private JComboBox version = null;
	private JButton Get = null;
	private JButton GetNext = null;
	private JButton End = null;

	private CommunityTarget target = new CommunityTarget();
	private static String ip = "";// 目的ip
	private int v;// 版本号

	public SendSnmp() {
		super();
		initialize();

	}

	private void initialize() {
		this.setSize(428, 374);
		this.setContentPane(getJContentPane());
		this.setTitle("Send_Receive");
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(0, 20, 107, 30));
			jLabel1.setText("本地IP:");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(200, 20, 107, 30));
			jLabel.setText("Port:");

			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getIpAddress(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getport(), null);

			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(0, 60, 107, 30));
			jLabel2.setText("snmp版本号:");
			jContentPane.add(jLabel2, null);

			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(0, 150, 107, 30));
			jLabel3.setText("设置OID:");
			jContentPane.add(jLabel3, null);

			jContentPane.add(getVersion(), null);
			jContentPane.add(getOid(), null);

			jContentPane.add(getGetNext(), null);
			jContentPane.add(getEnd(), null);
			jContentPane.add(getGet(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	private JTextField getIpAddress() {
		if (ipAddress == null) {
			ipAddress = new JTextField();
			ipAddress.setBounds(new Rectangle(40, 20, 100, 20));
			try {
				InetAddress address = InetAddress.getLocalHost();
				String IP_name = address.getHostAddress();
				ipAddress.setText(IP_name);
			} catch (UnknownHostException e) {
			}
		}
		return ipAddress;
	}

	private JTextField getport() {
		if (Port == null) {
			Port = new JTextField();
			Port.setBounds(new Rectangle(240, 20, 100, 20));
			Port.setText("161");
		}
		return Port;
	}

	private JTextField getOid() {
		if (Oid == null) {
			Oid = new JTextField();
			Oid.setBounds(new Rectangle(100, 150, 200, 20));
		}
		return Oid;
	}

	private JScrollPane getJScrollPane() {
		if (jsp == null) {
			jsp = new JScrollPane();
			jsp.setBounds(new Rectangle(100, 200, 250, 100));
			jsp.setViewportView(getReply());
		}
		return jsp;
	}

	private JTextArea getReply() {
		if (responseText == null) {
			responseText = new JTextArea();
			responseText.setLineWrap(true);
		}
		return responseText;
	}

	private JButton getGetNext() {
		if (GetNext == null) {
			GetNext = new JButton();
			GetNext.setBounds(new Rectangle(0, 230, 80, 20));
			GetNext.setText("GetNext");
			GetNext.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Set("GetNext");
				}
			});
		}
		return GetNext;
	}

	private JButton getEnd() {
		if (End == null) {
			End = new JButton();
			End.setBounds(new Rectangle(0, 260, 80, 20));
			End.setText("End");
			End.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(1);
				}
			});
		}
		return End;
	}

	private JButton getGet() {
		if (Get == null) {
			Get = new JButton();
			Get.setBounds(new Rectangle(0, 200, 80, 20));
			Get.setText("Get");
			Get.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Set("Get");
				}
			});
		}
		return Get;
	}

	private JComboBox getVersion() {
		if (version == null) {
			String s[] = { "SnmpV1", "SnmpV2", "SnmpV3" };
			version = new JComboBox(s);
			version.setBounds(new Rectangle(100, 60, 107, 20));
			version.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (version.getSelectedIndex() == 0) {
						v = 1;
					}
					if (version.getSelectedIndex() == 1) {
						v = 2;
					}
					if (version.getSelectedIndex() == 2) {
						v = 3;
					}
				}
			});
		}
		return version;
	}

	public void Set(String type) {
		try {
			Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
			target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			if (v == 1) {
				target.setVersion(SnmpConstants.version1);
			}
			if (v == 2) {
				target.setVersion(SnmpConstants.version2c);
			}
			if (v == 3) {
				target.setVersion(SnmpConstants.version3);
			}
			ip = ipAddress.getText();
			String port = Port.getText();
			target.setAddress(new UdpAddress(ip + "/" + port));
			target.setRetries(1);
			target.setTimeout(1000);
			long l = System.currentTimeMillis();
			int iii = 0;
			snmp.listen();
			PDU request = new PDU();
			String OID = Oid.getText();
			if (type.equals("Get")) {
				request.setType(PDU.GET);
				request.add(new VariableBinding(new OID(OID)));
			}
			if (type.equals("GetNext")) {
				request.setType(PDU.GETNEXT);
				request.add(new VariableBinding(new OID(OID)));
			}

			// request.setRequestID(new Integer32(-1111));
			// request.add(new VariableBinding(new OID(OID),new
			// Integer32(Integer.parseInt("01111000011110000",2))));//这里应该添加索要设置的值
			System.out.println("发送的UDP:" + request);
			PDU response = null;
			ResponseEvent responseEvent = snmp.send(request, target);
			iii = 1;
			long ll = System.currentTimeMillis() - l;
			System.out.println("所用时间<=>:" + ll + "    " + iii);
			response = responseEvent.getResponse();
			ll = System.currentTimeMillis() - l;
			if (response != null) {
				if (response.getErrorStatus() == response.noError) {
					String pause = responseEvent.getResponse()
							.getVariableBindings().toString();
					/* getvalue的值为对应OID的值 */
					String getvalue = pause.substring(pause.indexOf("= ") + 2,
							pause.indexOf(']'));
					String oid = pause.substring(pause.indexOf("VBS[") + 2,
							pause.indexOf("=") - 1);
					System.out.println(oid + "::");
					System.out.println(response);
					if (type.equals("Get")) {
						responseText.setText("");
						responseText.append(change(getvalue));
					}
					if (type.equals("GetNext")) {
						Oid.setText(oid);
						responseText.setText("");
						responseText.append(change(getvalue));
					}
				}

			}

			iii = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String change(String ss) {
		byte[] b = ss.getBytes();
		if (b.length == 1) {
			ss = String.valueOf(Integer.toHexString(b[0]));
		}
		return ss;
	}

	public static void main(String[] args) {
		SendSnmp ss = new SendSnmp();
		ss.setVisible(true);
	}

}
